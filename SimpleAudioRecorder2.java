/*
 * Simple audio recorder with playback.
 * The program utilizes multi-threading.
 * Currently saving files in WAV format as buffering in audio input as raw binary is a "bit" unclear, but probably possible.
 */

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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
public class SimpleAudioRecorder2 {
	float samplingRate;
	File audioFile;
	static AudioFormat myAF;
	static Mixer mix;
	static Clip testClip ;
	static Scanner scan;
	
	/**
	 * Setter needed for audio file to be able to run through multiple files using some 
	 * standard file format for each person recorded.
	 * @param filename
	 */
	public void setAudioFile(String filename) {
		audioFile = new File(filename + ".wav");
	}

	public SimpleAudioRecorder2() {
		samplingRate = 44100;
		audioFile = new File("test.wav");
		myAF = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplingRate, 16, 1, 2, samplingRate, false);
	}

	public SimpleAudioRecorder2(float samplingRate, String fileName) {
		if (samplingRate < 16000 || samplingRate > 44100)
			samplingRate = 44100;
		audioFile = new File(fileName + ".wav");
		myAF = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplingRate, 16, 1, 2, samplingRate, false);
	}

	DataLine.Info info = new DataLine.Info(TargetDataLine.class, myAF);
	TargetDataLine targetLine = null;

	public void startRecording () {
		scan = new Scanner(System.in);


		try {
			targetLine = (TargetDataLine) AudioSystem.getLine(info);

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
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		}

		
	}

	/**
	 * Stops the current recording.  We should probably keep a check 
	 * to see if the targetLine is null in case there was a problem with the audio file.
	 */
	public void stopRecording() {
		if (targetLine == null) {
			throw new NullPointerException("no process is recording");
		}
		try {
			/* Added timer section to keep it from chopping off the end of the recording
			 * Previously, it stopped immediately when it should have kept recording to the 
			 * end of the speech.  May not need Thread.sleep anymore.
			 */
			long timer = System.currentTimeMillis();
			while (System.currentTimeMillis() - timer < 400) {

			}

			targetLine.stop();
			targetLine.close();			
		} catch (Exception ie) {
			ie.printStackTrace();
		}
		System.out.println("Recording has finished");
	}

	public void playback() {
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
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} while (testClip.isActive());
	}
}