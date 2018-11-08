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
	
	private volatile static boolean targetActive = false;
	private boolean signed = true, bigEndian = false;
	private String fileName = "test.wav";
	private float sampleRate = 44100;
	private int bitsPerSample = 16;
	private File recFile;
	private String pathName = System.getProperty("user.dir");	//Default directory
	
	public Recorder(Mixer.Info mixer) {
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
		
		recFile = fileExists(recFile);
		
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
	
	private File fileExists(File f) {
		File tempFile = f;

		int recNum = 0;
		while (tempFile.exists()) {
			if (tempFile.getName().contains("bad")) {
				System.out.println("First if");
				int i = tempFile.getName().indexOf("bad");
				String s = tempFile.getName().substring(i, i+5);
				String replacement = tempFile.getName().replaceAll(s, "");
				tempFile = new File(tempFile.getParentFile() + File.separator + "bad" + recNum +"_" + replacement);
			} else if (!tempFile.getName().contains("bad")) {
				System.out.println("Second if");
				tempFile = new File(tempFile.getParentFile() + File.separator + "bad" + recNum++ + "_" + tempFile.getName());
			}
			recNum++;
		}
		
		System.out.println(tempFile.getName());
		return tempFile;
	}
	
	/*
	 * Waits 300 ms then stops recording, closes fileOutput
	 */
	public void stopRecording() {		
		if (targetActive) {
			long timer = System.currentTimeMillis();
			while (System.currentTimeMillis() - timer < 300) {
				
			}
			targetActive = false;	//Added an if statement into record
		}
		
		target.stop();
		target.close();
		setTargetStatus(false);
	}
	
	public void startPlaybackWAV() {
		Clip testClip = null;
		AudioInputStream audioStream;
		
		if (!getFile().exists())
			System.err.println("No file recorded!");
		
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
		recFile = new File(pathName + File.separator + fileName);
		System.gc();
	}
	
	public String getFilePath() {
		return pathName;
	}
	
	public void setFilePath(String pathname) {
		pathName  = pathname;
	}
	
	public Mixer.Info getMixer() {
		return mixInfo;
	}
	
	public File getFile() {
		return recFile;
	}
}