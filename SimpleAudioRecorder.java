/** 
 * Simple audio recorder with playback.
 * The program utilizes multi-threading.
 * Currently saving files in WAV format as buffering in audio input as raw binary is a "bit" unclear, but probably possible.
 */

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

/**
 * --------Miscellaneous Information--------
 * 
 * AudioFormat(AudioFormat.Encoding encoding, float sample rate, int sampleSizeInBits,
 * int channels, int frameSizeInBytes, frameRate, boolean BigEndian (generally false))
 * 
 * Sample rate: samples / second
 * Frame: 1 sample from each channel
 * Frame size: sample size * channels
 * Frame rate = frames / second
 * 
 * -----------------------------------------
 */
public class SimpleAudioRecorder {
	float samplingRate;
	File audioFile;
	static AudioFormat myAF;
	static Mixer mix;
	static Clip testClip ;
	static Scanner scan;
	
	public SimpleAudioRecorder() {
		samplingRate = 44100;
		audioFile = new File("test.wav");
		myAF = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplingRate, 16, 1, 2, samplingRate, false);
	}
	
	public SimpleAudioRecorder(float samplingRate, String fileName) {
		if (samplingRate < 16000 || samplingRate > 44100)
			samplingRate = 44100;
		audioFile = new File(fileName + ".wav");
		myAF = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplingRate, 16, 1, 2, samplingRate, false);
	}
	
	public static void main (String[] args) {
		SimpleAudioRecorder sar = new SimpleAudioRecorder();
		
		sar.record();
		sar.playback();
		
		scan.close();
		System.out.println("terminated");
	} 

	private void record() {
		scan = new Scanner(System.in);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, myAF);
		
		try {
			final TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);

			System.out.println("Press <Enter> to begin recording");
			scan.nextLine();

			targetLine.open();
			System.out.println("Recording...");
			targetLine.start();
			
			/*
			 * Creates a separate thread for recording. So after this anonymous inner class there exist two threads of execution.
			 * One handling the sleeps and the other recording data. Mostly to make sure there isn't any interference.
			 * Not entirely certain that this is necessary.
			 */
			Thread thread = new Thread() {
				@Override
				public void run() {
					AudioInputStream ais = new AudioInputStream(targetLine);
					try {
						AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			thread.start();
			
			System.out.println("Press <Enter> to stop");
			scan.nextLine();
			/* Added timer section to keep it from chopping off the end of the recording
			 * Previously, it stopped immediately when it should have kept recording to the 
			 * end of the speech.  May not need Thread.sleep anymore.
			 */
			long timer = System.currentTimeMillis();
			while (System.currentTimeMillis() - timer < 400) {
				
			}
			
			Thread.sleep(50);
			targetLine.stop();
			targetLine.close();			
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		}
		
		System.out.println("Recording has finished");
	}
	
	private void playback() {
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		mix = AudioSystem.getMixer(mixInfos[0]);	//Default system mixer
		DataLine.Info playbackInfo = new DataLine.Info(Clip.class, myAF);
		
		System.out.println("Beginning playback...");
		
		try {
			AudioInputStream playbackAudioStream = AudioSystem.getAudioInputStream(audioFile);
			testClip = (Clip) mix.getLine(playbackInfo);
			testClip.open(playbackAudioStream);
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (UnsupportedAudioFileException uafe) {
			uafe.printStackTrace();
		}
		testClip.start();
		
		/*
		 * Once testClip.start() is called, the audio will play but then the program will terminate before much of the audio is played.
		 * Here is one way to "hack" the program into playing the whole clip.
		 */
		do {
			try {
				Thread.sleep(200);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} while (testClip.isActive());
	}
}