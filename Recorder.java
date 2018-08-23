import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class Recorder {
	private AudioFormat audioFormat = null;	
	private ByteArrayOutputStream byteOutput = null;
	private TargetDataLine target = null;
	private Clip testClip;
	static Mixer mix;
	private Mixer.Info mixInfo;
	private FileOutputStream fileOutput = null;
	
	private volatile static boolean targetActive = false;
	private boolean signed = true, bigEndian = false;
	private String fileName = "test.wav";
	private float sampleRate = 44100;
	private int bitsPerSample = 16;
	
	/*
	 * Constructors
	 */
	public Recorder() {
		//byteOutput = new ByteArrayOutputStream();
		audioFormat = new AudioFormat(sampleRate, bitsPerSample, 1, signed, bigEndian);
	}
	
	public Recorder(Mixer.Info mixer) {
		//byteOutput = new ByteArrayOutputStream();
		audioFormat = new AudioFormat(sampleRate, bitsPerSample, 1, signed, bigEndian);
		mixInfo = mixer;
	}
	
	/*
	 * This method records raw data from a single line and writes it to the specified file
	 * 
	 * May or may not use this method. Thinking of adding yet another overloaded constructor.
	 */
	public void startRecordingSingleInputWAV() {
		
		try {
			target = (TargetDataLine) AudioSystem.getTargetDataLine(audioFormat, mixInfo);
			target.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}		
		
		target.start();
		
		Thread targetThread = new Thread() {
			@Override
			public void run() {
				AudioInputStream audioStream = new AudioInputStream(target);
				File fout = new File(fileName);
				System.out.println("fill: " + fileName);
				//if(targetActive) {
					try {
						AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, fout);		//writes continuously
						System.out.println("foo: " + fileName);
					} catch (IOException e) {
						e.printStackTrace();
					}
				//}
			}
		};
		
		targetThread.start();
	}
	
	/*
	 * This method records raw data from a single line from the first available target line and writes it to the specified file
	 */
//	public void startRecordingRaw() {
//		try {
//			fileOutput = new FileOutputStream(fileName);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
//		setTargetStatus(true);
//		
//		try {
//			target = (TargetDataLine) AudioSystem.getLine(info);
//			target.open();
//		} catch (LineUnavailableException e) {
//			e.printStackTrace();
//		}		
//		
//		byteOutput.reset();	//Eliminates the information in the buffer. Reset is necessary in case the user wished to re-record an utterance
//		target.start();
//		
//		Thread targetThread = new Thread() {
//			@Override
//			public void run() {
//				int numBytesRead = 0;
//				byte[] data = new byte[target.getBufferSize()/5];
//				
//				while(getTargetStatus()) {
//					numBytesRead = target.read(data, 0, data.length);
//					byteOutput.write(data, 0, numBytesRead);
//				}
//				
//				try {
//					byteOutput.writeTo(fileOutput);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		
//		targetThread.start();
//	}

	/*
	 * This method records raw data from a single line and writes it to the specified file
	 * 
	 * May or may not use this method. Thinking of adding yet another overloaded constructor.
	 */
//	public void startRecordingSingleInputRaw() {
//		
//		try {
//			fileOutput = new FileOutputStream(fileName);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		setTargetStatus(true);
//		
//		try {
//			target = (TargetDataLine) AudioSystem.getTargetDataLine(audioFormat, mixInfo);
//			target.open();
//		} catch (LineUnavailableException e) {
//			e.printStackTrace();
//		}		
//		
//		byteOutput.reset();	//Eliminates the information in the buffer. Reset is necessary in case the user wished to re-record an utterance
//		target.start();
//		
//		Thread targetThread = new Thread() {
//			@Override
//			public void run() {
//				int numBytesRead = 0;
//				byte[] data = new byte[target.getBufferSize()/5];
//				
//				while(getTargetStatus()) {
//					numBytesRead = target.read(data, 0, data.length);
//					byteOutput.write(data, 0, numBytesRead);
//				}
//				
//				try {
//					byteOutput.writeTo(fileOutput);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		
//		targetThread.start();
//	}
//	
	/*
	 * Waits 200 ms then stops recording, closes fileOutput
	 */
	public void stopRecording() {
		long timer = System.currentTimeMillis();
		while (System.currentTimeMillis() - timer < 200) {
			
		}
		
		target.stop();
		target.close();
	}
	
	/*
	 * Plays through the information previously recorded
	 */
//	public void startPlaybackRaw() {
//		if (byteOutput.size() == 0) {
//			System.out.println("No data has been recorded");
//			return;
//		}
//		
//		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
//		mix = AudioSystem.getMixer(mixInfos[0]);	//Default system mixer
//		DataLine.Info playbackInfo = new DataLine.Info(Clip.class, audioFormat);
//
//		try {
//			testClip = (Clip) mix.getLine(playbackInfo);
//			testClip.open(audioFormat, byteOutput.toByteArray(), 0, byteOutput.size());
//		} catch (LineUnavailableException lue) {
//			lue.printStackTrace();
//		}
//		
//		testClip.start();
//		
//		/*
//		 * Once testClip.start() is called, the audio will play but then the program will terminate before much of the audio is played.
//		 */
//		do {
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException ie) {
//				ie.printStackTrace();
//			}
//		} while (testClip.isActive());
//		
//	}
	
	public void startPlaybackWAV() {
		FileInputStream clipFileStream = null;
		File temp = new File(getFileName());
		try {
			clipFileStream = new FileInputStream(temp);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		mix = AudioSystem.getMixer(mixInfos[0]);	//default audio output
		DataLine.Info playbackInfo = new DataLine.Info(Clip.class, audioFormat);
		
		try {
			testClip = (Clip) mix.getLine(playbackInfo);
			AudioInputStream clipStream = new AudioInputStream(clipFileStream, audioFormat,(long) clipFileStream.available());
			testClip.open(clipStream);
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		testClip.start();
		
		/*
		 * Once testClip.start() is called, the audio will play but then the program will terminate before much of the audio is played.
		 */
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} while (testClip.isActive());
	}
	
	public void resetPlayback() {
		testClip.stop();
		testClip.setFramePosition(0);
	}
	
	/* 
	 * This method closes the ByteOutputArrayOutputStream which is used for both capture and playback.
	 * 
	 * This should be closed once the user is finished with an utterance.
	 */
	public void finish() {
		try {
			byteOutput.close();
			fileOutput.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public boolean getTargetStatus() {
		return targetActive;
	}
	
	public void setTargetStatus(boolean active) {
		targetActive = active;
	}
	
	/**
	 * Accessor for the byte array representation of data before it is stored
	 * @return the array
	 */
	public byte [] getBytes() {
		if (byteOutput != null) {
			return byteOutput.toByteArray();
		}
		return null;
	}
	
	public void setFileName(String newFile) {
		fileName = newFile;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public Mixer.Info getMixer() {
		return mixInfo;
	}
}