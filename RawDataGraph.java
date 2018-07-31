import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Draft of class for making raw data graph.  This shows the process of getting to a good graph.
 * @author sel49
 *
 */
public class RawDataGraph extends Application{

	ArrayList<Integer> list = new ArrayList<>();

	public void readFile() {
		//		try (Scanner scan = new Scanner(new File("r_test.RAW"))) {
		//			// byte at a time doesn't work
		//			//			while (scan.hasNextByte()) {
		//			//				System.out.println((int)scan.nextByte());
		//			//			}
		//			if (scan.hasNextByte()) {
		//				System.err.println("b: " + scan.nextByte());
		//			}
		//			while(scan.hasNext()) {
		//				String s = scan.next();
		//				for (int i = 0; i < s.length(); i++) {
		//					//					System.out.println((int)(s.charAt(i)));
		//					list.add((int)(s.charAt(i)));
		//				}
		//			}
		//		} catch (FileNotFoundException e) {
		//			e.printStackTrace();
		//		}

		/* From online */

		//	     InputStream inStream = null;
		//	      BufferedInputStream bis = null;
		//	      
		//	      try {
		//	      
		//	         // open input stream test.txt for reading purpose.
		//	         inStream = new FileInputStream("r_test.RAW");
		//
		//	         // input stream is converted to buffered input stream
		//	         bis = new BufferedInputStream(inStream);			
		//
		//	         // read until a single byte is available
		//	         while(bis.available()>0) {
		//	         
		//	            // read the byte and convert the integer to character
		//	            char c = (char)bis.read();
		//
		//	            // print the characters
		//	            System.out.println("Char: "+c);;
		//	         }
		//	      } catch(Exception e) {
		//	         // if any I/O error occurs
		//	         e.printStackTrace();
		//	      } finally {		
		//	         // releases any system resources associated with the stream
		//	         if(inStream!=null)
		//	            inStream.close();
		//	         if(bis!=null)
		//	            bis.close();
		//	      }
		/* end online */

		/* Robbie's idea from Data Structures */
		//		try {
		//			DataInputStream inputStream = new DataInputStream(new FileInputStream("text.txt"));
		//
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
		/* end Robbie's idea */

		/* My idea */
		FileInputStream input = null;
		try {
			input = new FileInputStream("r_test.RAW");
			int i = 0;
			do {
				i = input.read();
				list.add(i);
				System.out.println(i);

			} while (i != -1);

		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}


	}

	public static void main(String [] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		readFile();
		Group g = new Group();

		//		int size = list.size();
		//		System.err.println(size);
		//		double width = 800;
		//
		//		double pix = size / 44100;
		//		double pixel = width / 44100;
		//		System.err.println("pix: " + pix);
		//		int pixie = (int) (size / width);
		//		System.err.println("pixel: " + pixel);
		//		System.err.println("pixie: " + pixie );

		//		for (int i = 0; i < list.size(); i += pixie * 2) {
		//			
		//			Line line = new Line(i, 100 + list.get(i), i + 1, 100 + list.get(i + pixie));
		//			line.setStroke(Color.BLACK);
		//			line.setFill(Color.BLACK);
		//			g.getChildren().add(line);
		//		}

		//		for (int i = 0; i < list.size() - 1; i++) {
		//			Line line = new Line(i * pixel, list.get(i), (i+1) * pixel, list.get(i + 1)* 1 );
		//			line.setFill(Color.BLACK);
		//			line.setStroke(Color.BLACK);
		//			g.getChildren().add(line);
		//		}

		//		for (int i = 0; i < list.size() - pix; i+= pix) {
		//			Line line = new Line(i * pixel, list.get(i) * 0.3, (i+1) * pixel, list.get((int)(i + pix)* 1 ) * 0.3);
		//			line.setFill(Color.BLACK);
		//			line.setStroke(Color.BLACK);
		//			g.getChildren().add(line);
		//		}

//		int size = list.size();
//		int sample = 44100/800;
//		System.out.println("sample: " + sample);
//		int pixel = size / 800;
//		System.out.println(pixel);
//		double pixie = 800 / (double)size;
//		System.out.println("pixie: " + pixie);
//		double y = sample / (double) pixel;

//		for (int i = 0; i < list.size() - 1; i++) {
//			Line line = new Line(i * pixie, 100 + list.get(i) * pixie, (i+1) * pixie, 100 + list.get((int)(i + 1))*pixie * pixel/2);
//			line.setFill(Color.BLACK);
//			line.setStroke(Color.BLACK);
//			g.getChildren().add(line);
//		}
		
//		for (int i = 0; i < list.size() - sample; i+= sample) {
//			Line line = new Line(i/pixel, 100 + list.get(i) * y, (i+1)/pixel, 100 + y * list.get((int)(i + sample)));
//			line.setFill(Color.BLACK);
//			line.setStroke(Color.BLACK);
//			g.getChildren().add(line);
//		}
		Rectangle rect = new Rectangle(0, 0, 800, 295);
		rect.setFill(Color.LIGHTBLUE);
		g.getChildren().add(rect);
		
		Group show = new Group();
		displayProportional(show);
		g.getChildren().add(show);


		Scene s = new Scene(g);
		s.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.RIGHT) {
				show.setTranslateX(show.getTranslateX()  - 10);
				System.out.println("right");
			}
			if (event.getCode() == KeyCode.LEFT) {
				show.setTranslateX(show.getTranslateX()  + 10);
			}
		});

		arg0.setScene(s);
		arg0.setWidth(800);
		arg0.setHeight(600);
		arg0.show();
	}
	
	private void displayProportional(Group g) {
		int size = list.size();
		int sample = 44100/800;
		System.out.println("sample: " + sample);
		int pixel = size / 800;
		System.out.println(pixel);
		double pixie = 800 / (double)size;
		System.out.println("pixie: " + pixie);
		double y = sample / (double) pixel;
		
		
		for (int i = 0; i < list.size() - sample; i+= sample) {
			Line line = new Line(i/pixel, 100 + list.get(i) * y, (i+1)/pixel, 100 + y * list.get((int)(i + sample)));
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);
		}
	}

	private void displayFullHeight(Group g) {
		int size = list.size();
		System.err.println(size);
		double width = 800;

		double pix = size / 44100;
		double pixel = width / 44100;
		System.err.println("pix: " + pix);
		int pixie = (int) (size / width);
		System.err.println("pixel: " + pixel);
		System.err.println("pixie: " + pixie );
		Rectangle rect = new Rectangle(0, 0, pix, 295);
		rect.setFill(Color.LIGHTBLUE);
		g.getChildren().add(rect);
		for (int i = 0; i < list.size() - 1; i++) {
			Line line = new Line(i * pixel, list.get(i), (i+1) * pixel, list.get(i + 1)* 1 );
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);
		}
	}

	private void displayFullSize(Group g) {
		Rectangle rect = new Rectangle(0, 0, list.size(), 295);
		rect.setFill(Color.LIGHTBLUE);
		g.getChildren().add(rect);
		for (int i = 0; i < list.size() - 1; i++) {
			Line line = new Line(i, 20 + list.get(i), (i+1), 20 + list.get(i + 1));
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);

		}
	}

	private void displayWave3(Group g) {
		int size = list.size();
		System.err.println(size);
		double width = 800;

		double pix = size / 44100;
		double pixel = width / 44100;
		System.err.println("pix: " + pix);
		int pixie = (int) (size / width);
		System.err.println("pixel: " + pixel);
		System.err.println("pixie: " + pixie );

		for (int i = 0; i < list.size() - pix; i+= pix) {
			Line line = new Line(i * pixel, list.get(i) * 0.3, (i+1) * pixel, list.get((int)(i + pix)* 1 ) * 0.3);
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);
		}
	}
	
	private void displaySparseWave(Group g) {
		int size = list.size();
		int sample = 44100/800;
		System.out.println("sample: " + sample);
		int pixel = size / 800;
		System.out.println(pixel);

		for (int i = 0; i < list.size() - pixel; i+= pixel) {
			Line line = new Line(i/pixel, 100 + list.get(i), (i+1)/pixel, 100 + list.get((int)(i + pixel)));
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);
		}
	}
	
	private void shrinkToFitWave(Group g) {
		int size = list.size();
		int sample = 44100/800;
		System.out.println("sample: " + sample);
		int pixel = size / 800;
		System.out.println(pixel);
		double pixie = 800 / (double)size;
		System.out.println("pixie: " + pixie);

		for (int i = 0; i < list.size() - 1; i++) {
			Line line = new Line(i * pixie, 100 + list.get(i) * pixie, (i+1) * pixie, 100 + list.get((int)(i + 1))*pixie);
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);
		}
	}
	
	private void proportionedWaveFit(Group g) {
		int size = list.size();
		int sample = 44100/800;
		System.out.println("sample: " + sample);
		int pixel = size / 800;
		System.out.println(pixel);
		double pixie = 800 / (double)size;
		System.out.println("pixie: " + pixie);

		for (int i = 0; i < list.size() - 1; i++) {
			Line line = new Line(i * pixie, 100 + list.get(i) * pixie * pixel/2, (i+1) * pixie, 100 + list.get((int)(i + 1))*pixie * pixel/2);
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);
		}
	}

}
