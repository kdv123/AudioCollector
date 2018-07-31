

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class rawDataObtainer {
	
	public rawDataObtainer() {
	}
	
	public static void main(String[] args) {
		//rawDataObtainer myConverter = new rawDataObtainer();
		DataInputStream instream = null;
		DataOutputStream fout = null;
		
		try {
			instream = new DataInputStream(new FileInputStream("test.wav"));
			fout = new DataOutputStream(new FileOutputStream("r_test.RAW"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			instream.skipBytes(36);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < 4; i++) {
			try {
				System.out.print((char)instream.readByte());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int bytesRemaining = 0;
		try {
			bytesRemaining =instream.available();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (int i = bytesRemaining; i >= 0; i--) {
			try {
				fout.write(instream.read());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			instream.close();
			fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
