import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Recorder {
	private AudioFormat af = null;	
	private FileOutputStream fileOutput = null;
	private ByteArrayOutputStream byteOutput = null;
	private TargetDataLine target = null;
	private boolean isActive;
	
	public Recorder() {
		openFileOutputStream("test.raw");
		byteOutput = new ByteArrayOutputStream();
		af = new AudioFormat(16000, 16, 1, true, false);
	}
	
	public Recorder(String fileName, float sampleRate, int sampleSizeBits, int channels) {
		openFileOutputStream(fileName);
		byteOutput = new ByteArrayOutputStream();
		//Booleans in af constructor: Signed, bigEndian
		af = new AudioFormat(sampleRate, sampleSizeBits, channels, true, false);
	}
	
	/*
	 * This method records raw data from a single line and writes it to the specified file
	 */
	public void record() {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
		openTargetLine(info);
		
		int numBytesRead = 0;
		byte[] data = new byte[target.getBufferSize()/5];
		
		target.start();
		isActive = true;
		
		try {
			while(getStatus()) {
				numBytesRead = target.read(data, 0, data.length);
				byteOutput.write(data, 0, numBytesRead);
				byteOutput.writeTo(fileOutput);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		closeEverything();
	}
	
	/*
	 * Private utility
	 */
	private void openFileOutputStream(String fileName) {
		try {
			fileOutput = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Private utility
	 */
	private void openTargetLine(DataLine.Info info) {
		try {
			target = (TargetDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	private void closeEverything() {
		try {
			target.close();
			byteOutput.close();
			fileOutput.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public boolean getStatus() {
		return isActive;
	}
	
	/*
	 * This method was created so that when the subject begins recording this is set to true
	 */
	public void setStatus(boolean active) {
		isActive = active;
	}
}