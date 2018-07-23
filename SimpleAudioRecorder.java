/*
 * Simple audio recorder with a few prompts from the console.
 * The program utilizes multi-threading.
 * Currently saving files in WAV format as buffering in audio input is a bit unclear
 */

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class SimpleAudioRecorder {
	public static void main (String[] args) {
		float samplingRate = 44100;
		
		try {
			/**
			 * AudioFormat(AudioFormat.Encoding encoding, float sample rate, int sampleSizeInBits,
			 * int channels, int frameSizeInBytes, frameRate, boolean BigEndian (generally false))
			 * 
			 * Sample rate: samples / second
			 * Frame: 1 sample from each channel
			 * Frame size: sample size * channels
			 * Frame rate = frames / second
			 */
			AudioFormat myAF = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplingRate, 16, 1, 2, samplingRate, false);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, myAF);
			final TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
			
			Scanner scan = new Scanner(System.in);
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
					File audioFile = new File("test.wav");
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
			
			Thread.sleep(50);
			targetLine.stop();
			targetLine.close();
			scan.close();
			
			System.out.println("terminated");
			
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		}	
	}
}