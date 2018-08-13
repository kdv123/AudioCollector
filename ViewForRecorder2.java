import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map.Entry;

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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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
	Recorder[] listOfRecorders = new Recorder[4];
	File promptFile;
	String outputFileName = "test.raw";
	Scanner userPrompt;
	ArrayList<String []> sessionInfo;
	int state = 0;
	HashMap<Mixer.Info, Line.Info> mixerToTarget = new HashMap<Mixer.Info, Line.Info>();
	ArrayList<Mixer.Info> selectedMics = new ArrayList<Mixer.Info>(4);
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
			int micCheckCount = 0;
			for(Entry<Mixer.Info, Line.Info> info: mixerToTarget.entrySet()) {
				if(allMics.get(i).isSelected()) {
					micCheckCount++;
					selectedMics.add(info.getKey());
					if (micCheckCount == 1) {
						recorder1 = new Recorder(mixerToTarget.get(info.getKey()));
					} else if (micCheckCount == 2) {
						recorder2 = new Recorder(mixerToTarget.get(info.getKey()));
					} else if (micCheckCount == 3) {
						recorder3 = new Recorder(mixerToTarget.get(info.getKey()));
					} else if (micCheckCount == 4) {
						recorder4 = new Recorder(mixerToTarget.get(info.getKey()));
					} else {
						try {
							throw new Exception("More than four mic have been selected");
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				
				i++;
			}
		};
					
		for(Entry<Mixer.Info, Line.Info> ourEntry : mixerToTarget.entrySet()) {
			allMics.add(new CheckBox(ourEntry.getKey().getName()));
			allMics.get(allMics.size()-1).setOnAction(micCheckHandle);	//Most recent addition
		}
		
		
		Label sampRate = new Label("Sampling Rate");
		ComboBox<String> sampl = new ComboBox<String>();
		sampl.getItems().addAll("16000 Hz", "22050 Hz", "37800 Hz","44100 Hz");
		
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
		
		grid.addRow(++row, sampRate, sampl);
		
		Label lab = new Label();
		lab.setMinSize(100, 100);
		Button next = new Button("NEXT");
		next.setOnAction(event -> {
			for (int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null) {
					listOfRecorders[i].setCurrentFile("part" + partID.getText()+ "_"+ sNum.getText()+"_"+selectedMics.get(i).getName()+cond.getValue());
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
			System.out.println("start");
			status.setBackground(backgrounds(Color.GREEN, 0, 0));
			status.setText(status.getText() + "Recording ...");
				for(int i = 0; i < listOfRecorders.length; i++) {
					System.out.println(Arrays.toString(listOfRecorders));	//All recorders all null!
					if (listOfRecorders[i] != null) {
						listOfRecorders[i].startRecordingSingleInput();
					}
				}
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
		playback.setOnAction(event -> {
			//recorder1.startPlayback();
		});
		directions.add(playback, 5, 2, 1, 1);
	}
	
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
