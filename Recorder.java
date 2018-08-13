import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class Recorder {
	private AudioFormat audioFormat = null;	
	private ByteArrayOutputStream byteOutput = null;
	private TargetDataLine target = null;
	private Line.Info lineInfo = null;
	private Clip testClip;
	static Mixer mix;
	private FileOutputStream fileOutput = null;
	
	private boolean targetActive = false, signed = true, bigEndian = false, specifiedLine = false;
	private String fileName = "test.raw";
	private float sampleRate = 44100;
	private int bitsPerSample = 16;
	
	/*
	 * Constructors
	 */
	public Recorder() {
		byteOutput = new ByteArrayOutputStream();
		audioFormat = new AudioFormat(sampleRate, bitsPerSample, 1, signed, bigEndian);
	}
	
	public Recorder(Line.Info line) {
		byteOutput = new ByteArrayOutputStream();
		audioFormat = new AudioFormat(sampleRate, bitsPerSample, 1, signed, bigEndian);
		lineInfo = line;
		specifiedLine = true;
	}
	
	/*
	 * This method records raw data from a single line and writes it to the specified file
	 */
	public void startRecording() throws Exception {
		if (specifiedLine) {
			throw new Exception("A line has been specified!");
		}
		
		try {
			fileOutput = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//DataLine.Info info = new DataLine.Info(lineInfo.getLineClass(), audioFormat);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
		setTargetStatus(true);
		
		try {
			target = (TargetDataLine) AudioSystem.getLine(info);
			target.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}		
		
		byteOutput.reset();	//Eliminates the information in the buffer. Reset is necessary in case the user wished to re-record an utterance
		target.start();
		
		Thread targetThread = new Thread() {
			@Override
			public void run() {
				int numBytesRead = 0;
				byte[] data = new byte[target.getBufferSize()/5];
				
				while(getTargetStatus()) {
					numBytesRead = target.read(data, 0, data.length);
					byteOutput.write(data, 0, numBytesRead);
				}
				
				try {
					byteOutput.writeTo(fileOutput);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		targetThread.start();
	}
	
	/*
	 * This method records raw data from a single line and writes it to the specified file
	 * 
	 * May or may not use this method. Thinking of adding yet another overloaded constructor.
	 */
	public void startRecordingSingleInput() {
		System.out.println(lineInfo.getLineClass());
		
		try {
			fileOutput = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//DataLine.Info info = new DataLine.Info(lineInfo.getLineClass(), audioFormat);
		System.out.println(lineInfo.getLineClass());
		setTargetStatus(true);
		
		try {
			target = (TargetDataLine) AudioSystem.getLine(lineInfo);
			target.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}		
		
		byteOutput.reset();	//Eliminates the information in the buffer. Reset is necessary in case the user wished to re-record an utterance
		target.start();
		
		Thread targetThread = new Thread() {
			@Override
			public void run() {
				int numBytesRead = 0;
				byte[] data = new byte[target.getBufferSize()/5];
				
				while(getTargetStatus()) {
					numBytesRead = target.read(data, 0, data.length);
					byteOutput.write(data, 0, numBytesRead);
				}
				
				try {
					byteOutput.writeTo(fileOutput);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		targetThread.start();
	}
	
	/*
	 * Waits 200 ms then stops recording, closes fileOutput
	 */
	public void stopRecording() {
		long timer = System.currentTimeMillis();
		while (System.currentTimeMillis() - timer < 200) {
			
		}
		
		setTargetStatus(false);	//Exits loop in thread
		target.close();
	}
	
	/*
	 * Plays through the information previously recorded
	 */
	public void startPlayback() {
		/*
		 * Edit buttons so that playback is not allowed if nothing has been recorded?
		 */
		if (byteOutput.size() == 0) {
			System.out.println("No data has been recorded");
			return;
		}
		
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		mix = AudioSystem.getMixer(mixInfos[0]);	//Default system mixer
		DataLine.Info playbackInfo = new DataLine.Info(Clip.class, audioFormat);

		try {
			testClip = (Clip) mix.getLine(playbackInfo);
			testClip.open(audioFormat, byteOutput.toByteArray(), 0, byteOutput.size());
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
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
	
	public void setCurrentFile(String newFile) {
		fileName = newFile;
	}
	
	public void setSampleRate(float rate) {
		sampleRate = rate;
	}
	
	public void setBitsPerSample(int bits) {
		bitsPerSample = bits;
	}
	
	/*
	 * Main method for testing purposes
	 */
//	public static void main (String[] args) {
//		Recorder myRecorder1 = new Recorder("test1");
//		Recorder myRecorder2 = new Recorder("test2");
//		Scanner scan = new Scanner(System.in);
//		
//		System.out.println("Press enter to record test1");
//		scan.nextLine();
//		myRecorder1.startRecording();
//		
//		System.out.println("Press enter to stop recording test1");
//		scan.nextLine();
//		myRecorder1.stopRecording();
//		
//		System.out.println("Press enter to start test1 playback");
//		scan.nextLine();
//		myRecorder1.startPlayback();
//		
//		System.out.println("Press enter to reset test1 playback");
//		scan.nextLine();
//		myRecorder1.stopPlayback();
//		
//		System.out.println("Press enter to begin test1 playback");
//		scan.nextLine();
//		myRecorder1.startPlayback();
//		
//		myRecorder1.finish();
//		System.out.println("test1 complete");
//		
//		System.out.println("Press enter to record test2");
//		scan.nextLine();
//		myRecorder2.startRecording();
//		
//		System.out.println("Press enter to stop recording test2");
//		scan.nextLine();
//		myRecorder2.stopRecording();
//		
//		System.out.println("Press enter to start test2 playback");
//		scan.nextLine();
//		myRecorder2.startPlayback();
//		
//		System.out.println("Press enter to reset test2 playback");
//		scan.nextLine();
//		myRecorder2.stopPlayback();
//		
//		System.out.println("Press enter to begin test2 playback");
//		scan.nextLine();
//		myRecorder2.startPlayback();
//		
//		myRecorder2.finish();
//		System.out.println("test2 complete");
//		
//		scan.close();
//		System.out.println("Terminated");
//	}
}