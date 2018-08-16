import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/* August 12, 2018 */
public class TextDisplayTrial3 extends Application {


	public static void main (String [] args) {
		launch(args);
	}
	
	int taskNum = 0;
	int totalTasks = 4;
	File session = null; // to be used as part of the filenaming convention
	Scene scene;
	TextField partID; 
	TextField sessionNum;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		tasks = scanMe("test.txt");
		totalTasks = tasks.size();
		Group g = viewer();
		scene = new Scene(startScreen());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Text Display");
		primaryStage.show();
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);

	}
	
	
	
	
	public void drawData() {
		Stage stage = new Stage();
		Group g = new Group();
		double [] pts = read("test.wav");
		int width = pts.length / 1000;
		double pix = 1000.0/pts.length;
		double sum = 0;
		/*for (int i = 0, j = 0; i < pts.length; i++) {
			sum += pts[i];
			if (i % width == 0) {
				double avg = sum / width;
				double y = 1 - avg;
				Rectangle rect = new Rectangle(j, y, 1, avg * 2);
				g.getChildren().add(rect);
				sum = 0;
				j++;
			}
		}*/
		long start = System.currentTimeMillis();
		for (int i = 0; i < pts.length; i += width) {
			Rectangle rect = new Rectangle(i * pix, (1 - pts[i]) * 500 - 400, 1, pts[i] * 1000);
			g.getChildren().add(rect);
		}
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
	
    /**
	 * Creates a display for the opening setup of a session 
	 * and allows for transition to the next part.
	 * @return
	 */
	public Group startScreen() {
		Group g = new Group();
		GridPane grid = new GridPane();
		Label participantID = new Label("Participant ID");
		partID = new TextField();
		partID.setPromptText("participant ID");
		Label session = new Label("Session #");
		sessionNum = new TextField();
		sessionNum.setPromptText("Session #");
		Label file = new Label("file");
		TextField files = new TextField();
		Button choose = new Button("File");
		//choose.setOnAction);
		Label condition = new Label("Condition");
		ComboBox cond = new ComboBox();
		cond.getItems().addAll("one", "two", "outside", "inside");
		
		ArrayList<CheckBox> allMics = new ArrayList<CheckBox>();
		Label selectMics = new Label("Select microphones");
		getMicrophoneInfo();
		for(Entry<Mixer.Info, Line.Info> ourEntry : mixerToTarget.entrySet()) {
			allMics.add(new CheckBox(ourEntry.getKey().getName()));
		}
		
		
		
		Label sampRate = new Label("Sampling Rate");
		ComboBox sampl = new ComboBox();
		sampl.getItems().addAll("16000 Hz", "22050 Hz", "37800 Hz","44100 Hz");
		
		grid.addRow(0, participantID, partID);
		grid.addRow(1, session, sessionNum);
		grid.addRow(2, file, choose);
		grid.addRow(3, condition, cond);
		grid.addRow(4, selectMics);
		
		int row = 5;
		for(CheckBox cb : allMics) {
			grid.addRow(row, cb);
			row++;
		}
		
		grid.addRow(++row, sampRate, sampl);
		
		Label lab = new Label();
		lab.setMinSize(100, 100);
		Button ok = new Button("NEXT");
		ok.setOnAction(event -> {
			state = 1;
			scene.setRoot(viewer());
		});
		grid.addRow(++row, lab,  ok);
		g.getChildren().add(grid);
		
		return g;
	}
	
	
	Label count;
	GridPane main;
	
	public Group viewer() {
		main = new GridPane();
		GridPane taskBar = new GridPane();
		Label spacer = new Label();
		spacer.setText("Directions: Read the yellow highlighted text aloud");
		spacer.setFont(Font.font("times", FontWeight.BOLD, 20));
		spacer.setPrefSize(600, 30);
		count = new Label("Task " + taskNum + " of " + totalTasks);
		count.setTextFill(Color.CRIMSON);
		count.setBackground(background(Color.FLORALWHITE));
		count.setFont(Font.font(27));
//		count.setPrefSize(300, 30);
		taskBar.add(spacer, 0, 0);
		taskBar.add(count, 1, 0);
		main.add(taskBar, 0, 0);
		main.add(prompt(), 0, 1);
		main.add(mics(), 0, 2);
		Label label = new Label();
		label.setPrefSize(800, 50);
		main.add(label, 0, 3);
		makeBtnPanel();
		main.add(btnPanel, 0, 3);
		Group g = new Group();
		
		//Group g = prompt();
		g.getChildren().add(main);
		return g;
	}
	
	

	double WIDTH = 1000;
	double HEIGHT = 800;
	double MIC_H = 65;
	double MIC_W = 800;
	ArrayList<String []> sessionInfo;
	int state = 0;
	GridPane btnPanel;
	Label status;
	Button start;
	Button stop;
	Button next;

	/**
	 * Parses the given session control file into its constituent parts and 
	 * returns them as an array list of String arrays
	 * @param filename
	 * @return
	 */
	private ArrayList<String []> scanMe(String filename) {
		ArrayList<String []> list = new ArrayList<>();
		String id = "";
		String context = "";

		try (Scanner scan = new Scanner(new File(filename))) {
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

	
	Label label1 = new Label();
	Label label2 = new Label();
	Label label3 = new Label();
	ArrayList<String []> tasks;

	public Group prompt() {
		Group group = new Group();
		GridPane grid = new GridPane();
		grid.setPrefSize(800,  300);
		
		String [] str = tasks.get(taskNum);
		
		for (int i = 1; i < 4; i++) {
			String s = str[i];
			if (s.charAt(0) == ' ') {
				s = s.substring(1);
			}
			Label label = new Label();
			switch(i) {
			case 1:  label = label1; break;
			case 2:  label = label2; break;
			case 3:  label = label3; break;
			}
			label.setText(s);
			label.setMaxWidth(800);
			label.setFont(Font.font(30));
			label.setWrapText(true);
			label.setPrefWidth(800);
			if (i == 2) {
				label.setTextFill(Color.DARKBLUE);
				label.setBackground(promptFill());
			} else {
				label.setTextFill(Color.CHOCOLATE);
				label.setBackground(contextFill());
			}	
			grid.add(label, 0, i - 1);
		}
		grid.setBackground(background(Color.TRANSPARENT));
		group.getChildren().add(grid);
		return group;
	}

	private Background promptFill() {
		return new Background(new BackgroundFill(Color.ANTIQUEWHITE, new CornerRadii(2), new Insets(0)));
	}

	private Background contextFill() {
		return new Background(new BackgroundFill(Color.ALICEBLUE, new CornerRadii(2), new Insets(0)));
	}
	
	private Background neutralFill() {
		return new Background(new BackgroundFill(Color.PINK, new CornerRadii(2), new Insets(0)));
	}
	
	private Background recordingFill() {
		return new Background(new BackgroundFill(Color.GREEN, new CornerRadii(2), new Insets(0)));
	}
	
	private Background stoppedFill() {
		return new Background(new BackgroundFill(Color.RED, new CornerRadii(2), new Insets(0)));
	}
	
	private Background background(Color c) {
		return new Background(new BackgroundFill(c, new CornerRadii(2), new Insets(0)));
	}

	public Group mics() {
		Group group = new Group();
		GridPane grid = new GridPane();
		for (int i = 0; i < 4; i++) {
			Label label = new Label("Mic " + (i + 1) + ": ");
			label.setPrefSize(100,  MIC_H);
			label.setFont(Font.font(20));
			Label status = new Label("Status: ");
			status.setFont(Font.font(20));
			status.setMaxWidth(600);
			status.setPrefWidth(600);
			status.setPrefHeight(MIC_H);
			//status.setBorder(new Border));
			status.setBackground(neutralFill());
			Button playback = new Button("Playback");
			playback.setFont(Font.font(15));
			playback.setMaxSize(100, MIC_H);
			playback.setOnAction(event -> {
				//playback(i);
			});
			grid.add(label, 0, i, 1, 1);
			grid.add(status, 1, i, 1, 1);
			grid.add(playback, 2, i, 1, 1);
			playback.setOnAction(event -> {
				drawData();
			});
		}
		group.getChildren().add(grid);
		return group;
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
//		playback.setDefaultButton(false);
		start.setDefaultButton(true);
		start.requestFocus();
		
		// Default buttons not currently working for typing ENTER to move to the next one
		next.setOnAction(event -> {
			start.setDisable(false);
			next.setDisable(true);
			stop.setDisable(true);
			start.setDefaultButton(true);
			stop.setDefaultButton(false);
			next.setDefaultButton(false);
			System.err.println("next");
			System.out.println("next!");
			taskNum++;
//			egress
			if (taskNum < totalTasks) {
			String [] str = tasks.get(taskNum);
			
			for (int i = 1; i < 4; i++) {
				String s = str[i];
				if (s.charAt(0) == ' ') {
					s = s.substring(1);
				}
				Label label = new Label();
				switch(i) {
				case 1:  label = label1; break;
				case 2:  label = label2; break;
				case 3:  label = label3; break;
				}
				label.setText(s);
			}
			count.setText("Task " + taskNum + " of " + totalTasks);
			} else {
				Group en = new Group();
				en.getChildren().add(new Rectangle(0, 0, 1000, 400));
				scene.setRoot(en);
			}
			//main.add(prompt(), 0, 1);
		});

		/* Listeners attached to buttons here.  Nothing currently attached to "next" */
		start.setOnAction(event -> {
//			status.setBackground(backgrounds(Color.GREEN, 0, 0));
//			status.setText(status.getText() + "Recording ...");
//			recorder.startRecording();
			System.err.println("started");
			start.setDisable(true);
			next.setDisable(true);
			stop.setDisable(false);
			stop.setDefaultButton(true);
			start.setDefaultButton(false);
			next.setDefaultButton(false);
			System.out.println(event.getSource());
		});
		stop.setOnAction(event -> {
//			status.setBackground(backgrounds(Color.RED, 0, 0));
//			status.setText("Status:\t\t" + "Stopped Recording!");
//			recorder.stopRecording();
			System.err.println("Stopped!");
			start.setDisable(true);
			next.setDisable(false);
			stop.setDisable(true);
			next.setDefaultButton(true);
			stop.setDefaultButton(false);
			start.setDefaultButton(false);	
			
			//updateText();
			
		});


	}
	
	private Background backgrounds(Color c, int rad, int inset) {
		return new Background(new BackgroundFill(c, new CornerRadii(rad), new Insets(inset)));
	}
	
	HashMap<Mixer.Info, Line.Info> mixerToTarget = new HashMap<Mixer.Info, Line.Info>();
	public void getMicrophoneInfo() {
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		for (Mixer.Info mixInfo : mixers) {
			Mixer m = AudioSystem.getMixer(mixInfo);
			Line.Info[] lines = m.getTargetLineInfo();
			
			for (Line.Info li : lines) {
				if(li.toString().equals("interface TargetDataLine supporting 8 audio formats, and buffers of at least 32 bytes")) {
					mixerToTarget.put(mixInfo, li);
				}
			}			
		}
	}


}
