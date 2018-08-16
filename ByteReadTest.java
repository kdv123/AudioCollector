import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.application.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Displays a view of a sound wave in its own window.  
 * Currently selecting 1 out of every pts.length/1000 samples
 * @author Sarah Larkin
 *
 */
public class ByteReadTest extends Application {
	
	public static void main(String [] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		Stage stage = new Stage();
		Group g = new Group();
		double [] pts = read("test.wav");
		int width = pts.length / 1000;
		double pix = 1000.0/pts.length;
		double sum = 0;
//		for (int i = 0, j = 0; i < pts.length; i++) {
//			sum += pts[i];
//			if (i % width == 0) {
//				double avg = sum / width;
//				double y = 1 - avg;
//				Rectangle rect = new Rectangle(j, y * 500 - 400, 1, avg * 1000);
//				g.getChildren().add(rect);
//				sum = 0;
//				j++;
//			}
//		}
		long start = System.currentTimeMillis();
		for (int i = 0; i < pts.length; i += width) {
			Rectangle rect = new Rectangle(i * pix, (1 - pts[i]) * 500 - 400, 1, pts[i] * 1000);
			g.getChildren().add(rect);
		}
//		for (int i = 0; i < pts.length; i++) {
//			Rectangle rect = new Rectangle(i * pix, (1 - pts[i]) * 500 - 400, 1, pts[i] * 1000);
//			g.getChildren().add(rect);
//		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		Scene s = new Scene(g);
		stage.setScene(s);
		stage.show();		
	}
	
	/**
     * Reads audio samples from a file (in .wav or .au format) and returns
     * them as a double array with values between -1.0 and +1.0.
     * Modified from Princeton's StdAudio https://introcs.cs.princeton.edu/java/stdlib/StdAudio.java.html
     *
     * @param  filename the name of the audio file
     * @return the array of samples
     */
	 
    public static double[] read(String filename) {
        byte[] data = readByte(filename);
        int n = data.length;
        double[] d = new double[n/2];
        for (int i = 0; i < n/2; i++) {
            d[i] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) MAX_16_BIT);
        }
        return d;
    }
    
    private static final double MAX_16_BIT = Short.MAX_VALUE;
    /**
     * From princeton, see above
     * @param filename
     * @return
     */
    private static byte[] readByte(String filename) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {

            // try to read from file
            File file = new File(filename);
            if (file.exists()) {
                ais = AudioSystem.getAudioInputStream(file);
                int bytesToRead = ais.available();
                data = new byte[bytesToRead];
                int bytesRead = ais.read(data);
                if (bytesToRead != bytesRead)
                    throw new IllegalStateException("read only " + bytesRead + " of " + bytesToRead + " bytes"); 
            }

        }
        catch (IOException e) {
            throw new IllegalArgumentException("could not read '" + filename + "'", e);
        }

        catch (UnsupportedAudioFileException e) {
            throw new IllegalArgumentException("unsupported audio format: '" + filename + "'", e);
        }

        return data;
    }

	
}
