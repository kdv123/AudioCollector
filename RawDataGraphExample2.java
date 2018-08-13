import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Displays different wave-form interpretations of data.  
 * Click on a wave band to scroll across it using the arrow keys.
 * One of these needs to be selected as "best" for the main GUI.
 * The top wave "show" can be set to each wave form below for comparison.
 * @author Sarah Larkin
 * 
 * Date Last Modified:  7/31/18
 *
 */
public class RawDataGraphExample2 extends Application{

	ArrayList<Integer> list = new ArrayList<>();
	Group active;
	int graph = 0;
	Rectangle rect;

	public void readFile() {
		/* Using a file input stream to read byte by byte */
		FileInputStream input = null;
		DecimalBinaryTest dbt = new DecimalBinaryTest();
		try {
			input = new FileInputStream("test.RAW");
			int i = 0;
			// read each byte
			//			do {
			//				
			//				i = input.read();
			//				list.add(i);
			//				System.out.println(i);
			//
			//			} while (i != -1);
			do {
				int j =  input.read();
				int k =  input.read();
				if (j == -1 || k == -1) {
					break;
				}
				String jj = dbt.decToBin(j);
				String kk = dbt.decToBin(k);
				String sum = jj+kk;
				int num = dbt.binToDec(sum);
				System.out.println(num);
				list.add((int) Math.pow(2, 16) - num);
				//				int n = ((k << 8) + j);
				//				list.add(n);
				//				System.out.println(n);
				//				ByteBuffer buff = ByteBuffer.allocate(4);
				//				buff.put((byte)0x00);
				//				buff.put((byte)0x00);
				//				buff.put((byte) j);
				//				buff.put((byte) k);
				//				buff.flip();
				//				int num = buff.getInt();
				//				list.add(num);
				//				System.out.println(num);
				i = k;

			} while (i != -1);

		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(input!=null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}


	}



	ArrayList<Double> pick = new ArrayList<>();

	public int biggest() {
		int n = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) > n) {
				n = list.get(i);
			}
		}
		return n;
	}

	public void parseList(double width, double height) {
		int group = (int) (Math.ceil(list.size() / width));
		double hp = height / 2300;
		double sum = 0;
		for (int i = 0, j = 0; i < list.size() && j < group; i++, j++) {
			sum += list.get(j);
			if (j == group - 1) {
				System.out.println(sum/group);
				pick.add(sum/group);
				j = 0;
			}
		}
	}

	public Group drawPick(double width, double height) {
		Group g = new Group();
		double hp = height / (biggest());
		parseList(width, height);
		System.out.println("pick: " + pick.size());
		for (int i = 0; i < pick.size() - 1; i++) {
			double h = pick.get(i) * hp;
			//h = h/2;
			double num = h / 2;
			Line line = new Line(i, h, i + 1, pick.get(i + 1) * hp);
			//			Rectangle rec = new Rectangle(i, height - h, 1, h);
			//			rec.setFill(Color.BLACK);
			g.getChildren().add(line);
			System.out.println("aha");
		}
		return g;
	}

	public static void main(String [] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		readFile();
		Group g =  new Group();
		int size = list.size();
		double width = 800;
		double num = 44100 / 800;
		System.out.println("num: " + num);
		//		double sum = 0;
		//double avg = 0;
		//		int count = 0;
		int big = Integer.MIN_VALUE;
		/*for (int i = 0; i < list.size(); i++) {
			if (list.get(i) > 20000) {
				Rectangle r = new Rectangle(i, 0, 1, 10);
				r.setFill(Color.RED);
				r.setStroke(Color.RED);
				g.getChildren().add(r);
			} else {
				Rectangle r = new Rectangle(i, 0, 1, list.get(i)/100.0);
				r.setFill(Color.BLACK);
				r.setStroke(Color.BLACK);
				g.getChildren().add(r);
			}
			if (list.get(i) > big) {
				big = list.get(i);
			}

		}*/
		//.out.println("b: " + big);
		//		ArrayList<Integer> li = new ArrayList<>();
		//		while (count < size) {
		//			sum += list.get(count);
		//			
		//			if (count != 0 && count % 220 == 0) {
		//				int avg = (int)(sum/count);
		//				li.add(avg);
		//				sum = 0;
		//			}
		//			count++;
		//		}
		//		System.out.println("li size: " + li.size());
		//		int big = Integer.MIN_VALUE;
		//		for (int i: li) {
		//			if (i > big) {
		//				big = i;
		//			}
		//		}
		//		System.out.println("big: " +  big);
		//		
		//		double hp = 1000.0/big;
		//		for (int i = 0; i < li.size(); i++) {
		//			Rectangle r = new Rectangle(i, 0, 1, li.get(i));
		//			g.getChildren().add(r);
		//		}

		//		for (int i = 0; i < list.size(); i++) {
		//			
		//		}
		//		//parseList(800, 100);
		//		double big = 0;
		//		for (int i = 0; i < pick.size(); i++) {
		//			System.out.println(pick.get(i));
		//			if (pick.get(i) > big) {
		//				big = pick.get(i);
		//			}
		//		}
		//		
		//		System.err.println("big: " + big);
		//		shrinkToFitWave(g);
		System.err.println("biggest: " + biggest());

		//		rect = new Rectangle(0, 0, 800, 295);
		//		rect.setFill(Color.LIGHTBLUE);
		//		g.getChildren().add(rect);

		//		Rectangle r1 = new Rectangle(0, 300, 800, 100);
		//		r1.setFill(Color.ANTIQUEWHITE);
		//		g.getChildren().add(r1);
		//
		//		Rectangle r2 = new Rectangle(0, 400, 800, 100);
		//		r2.setFill(Color.LAVENDER);
		//		g.getChildren().add(r2);
		//
		//		Rectangle r3 = new Rectangle(0, 500, 800, 100);
		//		r3.setFill(Color.PEACHPUFF);
		//		g.getChildren().add(r3);

		//		Group show = new Group();
		//displayWave3(show);
		//g.getChildren().add(show);
		//readFile("")
		//parseList(800, 200);
		//		show = drawPick(800, 200);



		/* This appears to give the best display, with displayFullHeight a runner-up */
		//		Group g1 = new Group();
		//		displayProportional(g1);
		//		g1.setTranslateY(300);
		//		g.getChildren().add(g1);
		//
		//		Group g2 = new Group();
		//		shrinkToFitWave3(g2);
		//		g2.setTranslateY(300);
		//		g.getChildren().add(g2);
		//
		//		Group g3 = new Group();
		//		displaySparseWave3(g3);
		//		g3.setTranslateY(500);
		//		g.getChildren().add(g3);


		Group grown = new Group();
		ArrayList<Integer> lip = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) <= 20000) {
				lip.add(list.get(i));
			}
		}
		System.out.println("lip: " + lip.size());
		//		for (int i = 0; i < lip.size(); i++) {
		//			Rectangle r = new Rectangle(i, 0, 1, lip.get(i));
		//			g.getChildren().add(r);
		//		} // This takes too much space showing all of them, but may be okay.
//		int count = 0;
//		int sum = 0;
//		int nu = 0;
		ArrayList<Integer> li = new ArrayList<>();
//		size = lip.size();
//		System.out.println("size: " + size);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) > 80 && list.get(i) < 300) {
				li.add(list.get(i));
			}
		}
		System.out.println("lisize: " + li.size());
		for (int i = 0; i < li.size(); i++) {
			Rectangle r = new Rectangle(i, 0, 1, li.get(i));
			g.getChildren().add(r);
		}
//		while (count < size) {
//			sum += lip.get(count);
//
//			if (count != 0 && count % 110 == 0) {
//				
//				int avg = (int)(sum/count);
//				Rectangle r = new Rectangle(nu, 0, 1, avg);
//				g.getChildren().add(r);
//				System.out.println("avg: " + avg);
//				//li.add(avg);
//				sum = 0;
//			}
//			count++;
//		}
		active = g;


		Scene s = new Scene(g);



		s.setOnMouseClicked(event -> {
			System.out.println("y: " + event.getY());
			// not currently working to display all nine wave forms at once
			//			if (event.getButton() == MouseButton.SECONDARY) {
			//				rect.toFront();
			//				for (int i = 0; i < show.getChildren().size(); i++) {
			//				show.getChildren().remove(i);
			//				}
			//				graph++;
			//				if (graph == 9) {
			//					graph = 0;
			//				}
			//				switch(graph) {
			//				case 0: displayProportional(show); break;
			//				case 1: displayFullHeight(show); break;
			//				case 2: displayFullSize(show); break;
			//				case 3: displayWave3(show); break;
			//				case 4: displaySparseWave(show); break;
			//				case 5: displaySparseWave3(show); break;
			//				case 6: shrinkToFitWave(show); break;
			//				case 7: shrinkToFitWave3(show); break;
			//				case 8: proportionedWaveFit(show); break;
			//				}
			//				show.toFront();
			//			} else {
			//				if (event.getY() > 500) {
			//					active = g3;
			//				} else if (event.getY() > 400) {
			//					active = g2;
			//				} else if (event.getY() > 295) {
			//					active = g1;
			//					System.err.println(event.getSource());
			//				} else if (event.getY() < 295) {
			//					active = show;
			//				} 
			//			}
		});

		s.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.RIGHT) {
				active.setTranslateX(active.getTranslateX()  - 10);
				System.out.println("right");
			}
			if (event.getCode() == KeyCode.LEFT) {
				active.setTranslateX(active.getTranslateX()  + 10);
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
			Line line = new Line(i/pixel, 5 + list.get(i) * y, (i+1)/pixel, 5 + y * list.get((int)(i + sample)));
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

		for (int i = 0; i < list.size() - 1; i++) {
			Line line = new Line(i * pixel, list.get(i), (i+1) * pixel, list.get(i + 1)* 1 );
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);
		}
	}

	private void displayFullSize(Group g) {

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
			Line line = new Line(i/pixel, 1 + list.get(i), (i+1)/pixel, 1 + list.get((int)(i + pixel)));
			line.setFill(Color.BLACK);
			line.setStroke(Color.BLACK);
			g.getChildren().add(line);
		}
	}

	private void displaySparseWave3(Group g) {
		int size = list.size();
		int sample = 44100/800;
		System.out.println("sample: " + sample);
		int pixel = size / 800;
		System.out.println(pixel);

		for (int i = 0; i < list.size() - pixel; i+= pixel) {
			Line line = new Line(i/pixel, 1 + list.get(i) * 0.3, (i+1)/pixel, 1 + list.get((int)(i + pixel)) * 0.3);
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

	private void shrinkToFitWave3(Group g) {
		int size = list.size();
		int sample = 44100/800;
		System.out.println("sample: " + sample);
		int pixel = size / 800;
		System.out.println(pixel);
		double pixie = 800 / (double)size;
		System.out.println("pixie: " + pixie);

		for (int i = 0; i < list.size() - 1; i++) {
			Line line = new Line(i * pixie, 100 + list.get(i) * 0.3, (i+1) * pixie, 100 + list.get((int)(i + 1))*0.3);
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
