import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Recorder {
	private AudioFormat audioFormat = null;	
	private ByteArrayOutputStream byteOutput = null;
	private TargetDataLine target = null;
	static Mixer mix;
	private Mixer.Info mixInfo;
	//private FileOutputStream fileOutput = null;
	
	private volatile static boolean targetActive = false;
	private boolean signed = true, bigEndian = false;
	private String fileName = "test.wav";
	private float sampleRate = 44100;
	private int bitsPerSample = 16;
	private File recFile;
	private String pathName = System.getProperty("user.dir");	//Default directory
	int recNum = 0; // Recording number if a user decides to rerecord. Save all utterances.
	
	/*
	 * Constructor
	 */
//	public Recorder() {
//		//byteOutput = new ByteArrayOutputStream();
//		audioFormat = new AudioFormat(sampleRate, bitsPerSample, 1, signed, bigEndian);
//	}
//	
	public Recorder(Mixer.Info mixer) {
		//byteOutput = new ByteArrayOutputStream();
		audioFormat = new AudioFormat(sampleRate, bitsPerSample, 1, signed, bigEndian);
		mixInfo = mixer;
	}
	
	/*
	 * This method records data from a single line and writes it to the specified file in WAV Format
	 * 
	 */
	public void startRecordingSingleInputWAV() {
		try {
			target = (TargetDataLine) AudioSystem.getTargetDataLine(audioFormat, mixInfo);
			target.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		while (recFile.exists()) {
			if (recNum < 10) {
				String temp = recFile.toString();
				temp = temp.substring(0, temp.length()-6);
				temp +=  "_" + recNum + ".wav";
				recFile = new File(temp);
				recNum++;
			} else {
				System.err.println("Attempted to rerecord more than 10 times! Please delete previous files.");
				break;
			}
		}
		
		target.start();
		
		Thread targetThread = new Thread() {
			@Override
			public void run() {
				AudioInputStream audioStream = new AudioInputStream(target);
				
				//syncs mics
				while(!targetActive) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if(targetActive) {
				try {
					AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, recFile);		//writes continuously
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			}
		};
		

		targetThread.start();
	}
	
	/*
	 * This method records raw data from a single line from the first available target line and writes it to the specified file
	 */
	/*public void startRecordingRaw() {
		try {
			fileOutput = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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
	}*/

	/*
	 * This method records raw data from a single line and writes it to the specified file
	 * 
	 * May or may not use this method. Thinking of adding yet another overloaded constructor.
	 */
	/*public void startRecordingSingleInputRaw() {
		
		try {
			fileOutput = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		setTargetStatus(true);
		
		try {
			target = (TargetDataLine) AudioSystem.getTargetDataLine(audioFormat, mixInfo);
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
	}*/
	
	/*
	 * Waits 200 ms then stops recording, closes fileOutput
	 */
	public void stopRecording() {		
		if (targetActive) {
			long timer = System.currentTimeMillis();
			while (System.currentTimeMillis() - timer < 200) {
				
			}
			targetActive = false;	//Added an if statement into record
		}
		
		target.stop();
		target.close();
		setTargetStatus(false);
	}
	
	/*
	 * Plays through the information previously recorded
	 */
	/*public void startPlaybackRaw() {
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
		
		 // Once testClip.start() is called, the audio will play but then the program will terminate before much of the audio is played.

		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} while (testClip.isActive());
		
	}*/
	
	public void startPlaybackWAV() {
		Clip testClip = null;
		AudioInputStream audioStream;
		
		try {
			audioStream = AudioSystem.getAudioInputStream(getFile());
			testClip = AudioSystem.getClip();
			testClip.open(audioStream);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		testClip.start();
		
		//To play the entire clip and not close
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} while (testClip.isActive());
		
		testClip.close();
	}
	
	public void startRecordingBeep() {
		Clip testClip = null;
		AudioInputStream audioStream;
		
		try {
			audioStream = AudioSystem.getAudioInputStream(new File("buttonBeep.wav"));
			testClip = AudioSystem.getClip();
			testClip.open(audioStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//To play the entire clip and not close
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} while (testClip.isActive());
		
		testClip.close();
	}
	
	
	
	/* 
	 * This method closes the ByteOutputArrayOutputStream which is used for both capture and playback.
	 * 
	 * This should be closed once the user is finished with an utterance.
	 */
	/*public void finish() {
		try {
			byteOutput.close();
			fileOutput.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}*/
	
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
		recFile = new File(pathName + File.separator + fileName + "_" + recNum + ".wav");
		System.gc();
	}
	
	public String getFileName() {
		return recFile.toString();
	}
	
	public boolean setFilePath(String pathname) {
		pathName  = pathname;
		return new File(pathname).mkdirs();
	}
	
	public Mixer.Info getMixer() {
		return mixInfo;
	}
	
	public File getFile() {
		return recFile;
	}
}