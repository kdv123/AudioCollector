import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ViewForRecorder2 extends Application {

	Dimension screenSize;
	Scene scene;
	BorderPane screen;
	GridPane directions;
	GridPane btnPanel;
	Label status;
	Button start;
	Button stop;
	Button next;
	Button playback;
	Recorder recorder1;
	Recorder recorder2;
	Recorder recorder3;
	Recorder recorder4;
	Recorder[] listOfRecorders = new Recorder[5];
	File promptFile;
	String outputFileName = "test.WAV";
	Scanner userPrompt;
	ArrayList<String []> sessionInfo;
	int state = 0;
	//HashMap<Mixer.Info, Line.Info> mixerToTarget = new HashMap<Mixer.Info, Line.Info>();
	ArrayList<Mixer.Info> allMixerInfos = new ArrayList<Mixer.Info>(5);
	ArrayList<Mixer.Info> selectedMics = new ArrayList<Mixer.Info>(5);
	float[] sampleRates = {16000, 22050, 37800, 44100};
	
	/*
	 * Planning:  need a file to parse.  Then have a series of questions starting with
	 * entering info, then test phrase to get comfortable, then all the problems.
	 * Should probably organize the screen to have a count of problem number and add
	 * in the options for a previous button.  Also need to add the spacing and file support
	 * for multiple mics.
	 */
	
	/**
	 * Parses the given session control file into its constituent parts and 
	 * returns them as an array list of String arrays
	 * @param filename
	 * @return
	 */
	private ArrayList<String []> scanMe() {
		ArrayList<String []> list = new ArrayList<>();
		String id = "";
		String context = "";
		
		try (Scanner scan = new Scanner(promptFile)) {
			while (scan.hasNext()) {
				String [] tasks = new String[4];
				String task = scan.nextLine();
				Scanner cols = new Scanner(task);
				cols.useDelimiter("\t");
				id = null;
				while(cols.hasNext()) {
					if (id == null) {
						id = cols.next();
						tasks[0] = id;
					} else {
						context = cols.nextLine();
						System.err.println("context: " + context);
					}
				}
				parseTask(context, tasks);
				list.add(tasks);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * Helper method parses the context string and desired speech string into a
	 * given String array
	 * @param context
	 * @param parts
	 */
	private void parseTask(String context, String [] parts) {
		Scanner info = new Scanner(context);
		for (int i = 1; i < parts.length; i++) {
			parts[i] = "";
		}
		int part = 1;
		while(info.hasNext()) {
			String s = info.next();
			if (s.equals("<br>")) {
				parts[part] += "\n";	
			} else if (s.equals("<h>")) {
				part = 2;
			} else if (s.equals("</h>")) {
				part = 3;
			} else {
				parts[part] += " " + s + " ";
			}
		}
		info.close();
	}


	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Simple Audio Recorder");
		stage.setWidth(800);
		stage.setHeight(600);
		makeDirections();
		makeBtnPanel();
		screen = new BorderPane();
		screen.setCenter(directions);
		screen.setBottom(btnPanel);
		scene = new Scene(startScreen(stage));
		scene.setFill(Color.ANTIQUEWHITE);
		//scene.setRoot(screen);
//		scene.getStylesheets().add("style21.css");
		stage.setScene(scene);
		stage.show();
		
//		ArrayList<String []> tasks = scanMe();
//		for (int i = 0; i < tasks.size(); i++) {
//			
//			System.out.println(java.util.Arrays.toString(tasks.get(i)) + "\n\n");
//			for (int j = 0; j < tasks.get(i).length; j++) {
//				System.out.print(tasks.get(i)[j] + "\t");
//			}
//			System.out.println();
//		}

	}
	
	/**
	 * Creates a display for the opening setup of a session 
	 * and allows for transition to the next part.
	 * @return
	 */
	public Group startScreen(Stage stage) {
		Group g = new Group();
		GridPane grid = new GridPane();
		
		Label participantID = new Label("Participant ID");
		TextField partID = new TextField();
		partID.setPromptText("participant ID");
		
		Label session = new Label("Session #");
		TextField sNum = new TextField();
		sNum.setPromptText("Session #");
		
		Label fileLabel = new Label("file");
		TextField files = new TextField();
		Button choose = new Button("File");

		choose.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			chooser.setInitialDirectory(new File("D:\\eclipse-workspace\\AudioCollector"));	//Set initial directory to something else
			promptFile = chooser.showOpenDialog(stage);
		});
		
		
		Label condition = new Label("Condition");
		ComboBox<String> cond = new ComboBox<String>();
		cond.getItems().addAll("one", "two", "outside", "inside");
		
		ArrayList<CheckBox> allMics = new ArrayList<CheckBox>();
		Label selectMics = new Label("Select microphones");
		getMicrophoneInfo();
		
		EventHandler<ActionEvent> micCheckHandle = e-> {
			int i = 0;
			for(Mixer.Info info: allMixerInfos) {
				if(allMics.get(i).isSelected() && i < 5) {
					selectedMics.add(info);
				}
				i++;
			}
		};
					
		for(Mixer.Info info : allMixerInfos) {
			allMics.add(new CheckBox(info.getName()));
			allMics.get(allMics.size()-1).setOnAction(micCheckHandle);	//Most recent addition
		}
		
		
//		Label sampRate = new Label("Sampling Rate");
//		ComboBox<String> sampl = new ComboBox<String>();
//		sampl.getItems().addAll("16000 Hz", "22050 Hz", "37800 Hz","44100 Hz");
		
		grid.addRow(0, participantID, partID);
		grid.addRow(1, session, sNum);
		grid.addRow(2, fileLabel, choose);
		grid.addRow(3, condition, cond);
		grid.addRow(4, selectMics);
		
		int row = 5;
		for(CheckBox cb : allMics) {
			grid.addRow(row, cb);
			row++;
		}
		
//		grid.addRow(++row, sampRate, sampl);
		
		Label lab = new Label();
		lab.setMinSize(100, 100);
		Button next = new Button("NEXT");
		next.setOnAction(event -> {
			for(int i = 0; i < selectedMics.size(); i++) {
				if (i == 0) {
					recorder1 = new Recorder(allMixerInfos.get(0));
					listOfRecorders[0] = recorder1;
					System.out.println("1 mic");
				} else if (i == 1) {
					recorder2 = new Recorder(allMixerInfos.get(1));
					listOfRecorders[1] = recorder2;
					System.out.println("2 mics");
				} else if (i == 2) {
					recorder3 = new Recorder(allMixerInfos.get(2));
					listOfRecorders[2] = recorder3;
				} else if (i == 3) {
					recorder4 = new Recorder(allMixerInfos.get(3));
					listOfRecorders[3] = recorder4;
				}
			}
			
			for (int i = 0; i < listOfRecorders.length; i++) {
				System.out.println(listOfRecorders[i]);
				if (listOfRecorders[i] != null) {
					if (partID.getText().length() < 1) {
						listOfRecorders[i].setFileName("Test"+selectedMics.get(i).getName().replaceAll(" ", "")+".WAV");
					}
					listOfRecorders[i].setFileName("participant" + partID.getText()+ "_Session"+ sNum.getText()+"_"+listOfRecorders[i].getMixer().getName().replaceAll(" ", "")+ "_"+ cond.getValue()+".WAV");
				}
			}
			
			state = 1;
			scene.setRoot(screen);
		});
		grid.addRow(++row, lab,  next);
		g.getChildren().add(grid);
		
		return g;
	}

	public static void main(String [] args) {
		launch(args);
	}
	
	

	/**
	 * Makes the button panel at the bottom of the screen.  
	 * 
	 */
	public void makeBtnPanel() {
		btnPanel = new GridPane();
		start = new Button("Start Recording");
		start.setPrefSize(100, 60);
		stop = new Button("Stop Recording");
		stop.setPrefSize(100, 60);
		next = new Button("Next Prompt");
		next.setPrefSize(100, 60);

		start.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(2), new Insets(0))));
		stop.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(2), new Insets(0))));
		next.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(2), new Insets(0))));

		/*Dummie buttons for spacing purposes */
		for (int i = 0; i < 8; i += 2) {
			Button d1 = new Button();
			d1.setPrefSize(100,  60);
			btnPanel.add(d1, i, 0);
			d1.setVisible(false);
		}


		btnPanel.add(start, 1, 0);
		btnPanel.add(stop, 3, 0);
		btnPanel.add(next, 5, 0);
		
		next.setDisable(true);
		stop.setDisable(true);
		playback.setDefaultButton(false);
		start.setDefaultButton(true);
		start.requestFocus();
		
		// Default buttons not currently working for typing ENTER to move to the next one
		next.setOnMouseClicked(event -> {
			System.out.println("next");
			start.setDisable(false);
			next.setDisable(true);
			stop.setDisable(true);
			start.setDefaultButton(true);
			stop.setDefaultButton(false);
			next.setDefaultButton(false);
		});

		/* Listeners attached to buttons here.  Nothing currently attached to "next" */
		start.setOnAction(event -> {
			status.setBackground(backgrounds(Color.GREEN, 0, 0));
			status.setText(status.getText() + "Recording ...");
				int lastMicIndex = 0;
				for(int i = 0; i < listOfRecorders.length; i++) {
					//System.out.println(Arrays.toString(listOfRecorders));
					if (listOfRecorders[i] != null) {
						listOfRecorders[i].getMixer().getName();
						listOfRecorders[i].startRecordingSingleInputWAV();
						lastMicIndex = i;
					}
				}
				
			listOfRecorders[lastMicIndex].setTargetStatus(true);
			
			start.setDisable(true);
			next.setDisable(true);
			stop.setDisable(false);
			stop.setDefaultButton(true);
			start.setDefaultButton(false);
			next.setDefaultButton(false);
			
		});
		stop.setOnAction(event -> {
			System.out.println("stop");
			status.setBackground(backgrounds(Color.RED, 0, 0));
			status.setText("Status:\t\t" + "Stopped Recording!");
			for (int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null) {
					listOfRecorders[i].stopRecording();
				}
			}
			start.setDisable(true);
			next.setDisable(false);
			stop.setDisable(true);
			next.setDefaultButton(true);
			stop.setDefaultButton(false);
			start.setDefaultButton(false);			
		});


	}
	
	private Background backgrounds(Color c, int rad, int inset) {
		return new Background(new BackgroundFill(c, new CornerRadii(rad), new Insets(inset)));
	}


	/**
	 * Creates the directions panel with info and text to read.
	 */
	public void makeDirections() {
		directions = new GridPane();
		Label dir = new Label("Please click start and read the following text.  \nWhen you have finished reading, click stop.  Then click next.");
		dir.setFont(Font.font(30));
		TextArea prompt = new TextArea();
		prompt.setText("one... two ... \nthree ... four ...");
		prompt.setEditable(false);
		prompt.setFont(Font.font("Consolas", 30));
		prompt.setMaxSize(800, 300);
		prompt.setBackground(backgrounds(Color.YELLOW, 0, 0));
		prompt.setStyle("-fx-font: 22 arial; -fx-base: #ff0000; -fx-background-color: #FFFF00;");//#b6e7c9
		//prompt.setStyle("background-color:yellow");
		//prompt.getStylesheets().add("/AudioCollector/style21.css.textArea1");
		prompt.getStyleClass().add("textArea1");
		directions.add(dir, 0, 0, 5, 1);
		directions.add(prompt, 0, 1, 5, 1);
		status = new Label("Status:\t\t");
		status.setFont(Font.font(40));
		status.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(0), new Insets(4))));
		directions.add(status, 0,  2, 4, 1);
		status.setMaxWidth(600);
		status.setMinWidth(600);
		status.setMaxHeight(100);
		playback = new Button("Playback");
		playback.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				recorder1.startPlaybackWAV();
			} else {
				System.out.println("aha");
				System.out.println("prompt");
				// Get the byte Array and graph it directly in a new window. */
				//byte [] bites = recorder1.getBytes();
				//showGraph(bites);
				/* Attempt to show python chart failed - it didn't display when run */
				long time = System.currentTimeMillis();
				String command = "python /c start python C:/Users/sel49/workspace/AudioCollector/myScript.py";
//				
				try {
//					String [] args = {command, "one", "two.txt", "three"}
					Process p = Runtime.getRuntime().exec(command);
					System.out.println("run!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long end = System.currentTimeMillis();
				System.out.println(end - time);
			}
		});
//		System.out.println("script finished");
//		playback.setOnAction(event -> {
//			
//			recorder.startPlayback();
//		});
		directions.add(playback, 5, 2, 1, 1);
	}
	
	/**
	 * This function creates a graph of the sound produced.
	 * @param bites
	 */
	public void showGraph(byte [] bites) {
		Stage sec = new Stage();
		sec.setTitle("Graph");
		Group g = new Group();
		ArrayList<Double> pts = new ArrayList<>();
		System.out.println("bites: " + bites.length);
		for (int i = 0; i < bites.length - 1; i++) {
			byte b1 = bites[i];
			byte b2 = bites[i+1];
			 pts.add((double) (b2 << 8 | b1 & 0xFF) / 32767.0);
		}
		System.out.println("bites formed");
		ArrayList<Double> bit = new ArrayList<>();
		ArrayList<Double> biti = new ArrayList<>();
		for (int i = 0; i < pts.size(); i++) {
			if (i % 2 != 0) {
				bit.add(pts.get(i));
//			Ellipse e = new Ellipse(i, pts.get(i) * 100, 1, 1);
//			g.getChildren().add(e);
			} else {
				biti.add(pts.get(i));
			}
		}
		double sum = 0;
//		int count = (bites.length / 2) / 44100;
//		System.out.println(count);
		for (int i = 0, j = 500; i < bit.size(); i++) {
			Rectangle r = new Rectangle(i * 800.0/bit.size(), 0, 1, bit.get(i) * 100);
			g.getChildren().add(r);
		}
		for (int i = 0; i < pts.size(); i++) {
			Rectangle r = new Rectangle(i * 800.0/pts.size(), 300, 1, pts.get(i) * 50);
			Rectangle s = new Rectangle(i * 800.0/pts.size(), 200 + (100 - pts.get(i) * 50), 1, pts.get(i) * 100);
			//s.setRotate(180);
			g.getChildren().add(r);
			g.getChildren().add(s);
		}
		
		double [] a = new double[biti.size()];
		double [] b = new double[bit.size()];
		for (int i = 0; i < biti.size(); i++) {
			a[i] = biti.get(i);
		}
		for (int i = 0; i < bit.size(); i++) {
			b[i] = bit.get(i);
		}
//		double [] de = fft(a, b, false);
//		for (int i = 0; i < de.length; i++) {
//			Rectangle r = new Rectangle(i * 800.0/pts.size(), 400, 1, pts.get(i));
//			g.getChildren().add(r);
//		}
		Scene sc = new Scene(g);
		sc.setOnMouseClicked(event -> {
			System.out.println("cli");
			if (event.getButton() == MouseButton.PRIMARY) {
				g.setTranslateX(g.getTranslateX() - 30);
			} else {
				g.setTranslateX(g.getTranslateX() + 30);
			}
		});
		sc.setOnKeyPressed(event -> {
			System.out.println("moo");
			if (event.getCode() == KeyCode.RIGHT) {
				g.setTranslateX(g.getTranslateX() - 30);
			}
			if (event.getCode() == KeyCode.LEFT) {
				g.setTranslateX(g.getTranslateX() + 30);
			}
		});
		System.out.println("bites shown");
		
		sec.setScene(sc);
		sec.setWidth(800);
		sec.setHeight(600);
		sec.show();
		
	}
	
	/** Code from Stack Overflow:  
	 * https://stackoverflow.com/questions/3287518/reliable-and-fast-fft-in-java/3287544
	 */
//	public class FFTbase {
	/**
	 * The Fast Fourier Transform (generic version, with NO optimizations).
	 *
	 * @param inputReal
	 *            an array of length n, the real part
	 * @param inputImag
	 *            an array of length n, the imaginary part
	 * @param DIRECT
	 *            TRUE = direct transform, FALSE = inverse transform
	 * @return a new array of length 2n
	 */
	public double[] fft(final double[] inputReal, double[] inputImag,
	                           boolean DIRECT) {
	    // - n is the dimension of the problem
	    // - nu is its logarithm in base e
	    int n = inputReal.length;

	    // If n is a power of 2, then ld is an integer (_without_ decimals)
	    double ld = Math.log(n) / Math.log(2.0);

	    // Here I check if n is a power of 2. If exist decimals in ld, I quit
	    // from the function returning null.
	    if (((int) ld) - ld != 0) {
	        System.out.println("The number of elements is not a power of 2.");
	        return null;
	    }

	    // Declaration and initialization of the variables
	    // ld should be an integer, actually, so I don't lose any information in
	    // the cast
	    int nu = (int) ld;
	    int n2 = n / 2;
	    int nu1 = nu - 1;
	    double[] xReal = new double[n];
	    double[] xImag = new double[n];
	    double tReal, tImag, p, arg, c, s;

	    // Here I check if I'm going to do the direct transform or the inverse
	    // transform.
	    double constant;
	    if (DIRECT)
	        constant = -2 * Math.PI;
	    else
	        constant = 2 * Math.PI;

	    // I don't want to overwrite the input arrays, so here I copy them. This
	    // choice adds \Theta(2n) to the complexity.
	    for (int i = 0; i < n; i++) {
	        xReal[i] = inputReal[i];
	        xImag[i] = inputImag[i];
	    }

	    // First phase - calculation
	    int k = 0;
	    for (int l = 1; l <= nu; l++) {
	        while (k < n) {
	            for (int i = 1; i <= n2; i++) {
	                p = bitreverseReference(k >> nu1, nu);
	                // direct FFT or inverse FFT
	                arg = constant * p / n;
	                c = Math.cos(arg);
	                s = Math.sin(arg);
	                tReal = xReal[k + n2] * c + xImag[k + n2] * s;
	                tImag = xImag[k + n2] * c - xReal[k + n2] * s;
	                xReal[k + n2] = xReal[k] - tReal;
	                xImag[k + n2] = xImag[k] - tImag;
	                xReal[k] += tReal;
	                xImag[k] += tImag;
	                k++;
	            }
	            k += n2;
	        }
	        k = 0;
	        nu1--;
	        n2 /= 2;
	    }

	    // Second phase - recombination
	    k = 0;
	    int r;
	    while (k < n) {
	        r = bitreverseReference(k, nu);
	        if (r > k) {
	            tReal = xReal[k];
	            tImag = xImag[k];
	            xReal[k] = xReal[r];
	            xImag[k] = xImag[r];
	            xReal[r] = tReal;
	            xImag[r] = tImag;
	        }
	        k++;
	    }

	    // Here I have to mix xReal and xImag to have an array (yes, it should
	    // be possible to do this stuff in the earlier parts of the code, but
	    // it's here to readibility).
	    double[] newArray = new double[xReal.length * 2];
	    double radice = 1 / Math.sqrt(n);
	    for (int i = 0; i < newArray.length; i += 2) {
	        int i2 = i / 2;
	        // I used Stephen Wolfram's Mathematica as a reference so I'm going
	        // to normalize the output while I'm copying the elements.
	        newArray[i] = xReal[i2] * radice;
	        newArray[i + 1] = xImag[i2] * radice;
	    }
	    return newArray;
	}

	/**
	 * The reference bitreverse function.
	 */
	private int bitreverseReference(int j, int nu) {
	    int j2;
	    int j1 = j;
	    int k = 0;
	    for (int i = 1; i <= nu; i++) {
	        j2 = j1 / 2;
	        k = 2 * k + j1 - 2 * j2;
	        j1 = j2;
	    }
	    return k;
	  }
//	}
	// end Stack overflow;
	
	public void getMicrophoneInfo() {
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		for (Mixer.Info mixInfo : mixers) {
			Mixer m = AudioSystem.getMixer(mixInfo);
			Line.Info[] lines = m.getTargetLineInfo();
			
			for (Line.Info li : lines) {
				if(li.toString().equals("interface TargetDataLine supporting 8 audio formats, and buffers of at least 32 bytes")) {
					allMixerInfos.add(mixInfo);
				}
			}			
		}
	}
	
}