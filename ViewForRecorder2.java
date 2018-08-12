
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Stage;

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
	Recorder recorder = new Recorder();
	Scanner userPrompt;
	ArrayList<String []> sessionInfo;
	int state = 0;
	
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
		scene = new Scene(startScreen());
		scene.setFill(Color.ANTIQUEWHITE);
		//scene.setRoot(screen);
//		scene.getStylesheets().add("style21.css");
		stage.setScene(scene);
		stage.show();
		
		ArrayList<String []> tasks = scanMe("test.txt");
		for (int i = 0; i < tasks.size(); i++) {
			
//			System.out.println(java.util.Arrays.toString(tasks.get(i)) + "\n\n");
			for (int j = 0; j < tasks.get(i).length; j++) {
				System.out.print(tasks.get(i)[j] + "\t");
			}
			System.out.println();
		}

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
		TextField partID = new TextField();
		partID.setPromptText("participant ID");
		Label session = new Label("Session #");
		TextField sNum = new TextField();
		sNum.setPromptText("Session #");
		Label file = new Label("file");
		TextField files = new TextField();
		Button choose = new Button("File");
		Label condition = new Label("Condition");
		ComboBox cond = new ComboBox();
		cond.getItems().addAll("one", "two", "outside", "inside");
		Label numMics = new Label("# of microphones");
		ComboBox mics = new ComboBox();
		mics.getItems().addAll("1", "2", "3", "4");
		Label sampRate = new Label("Sampling Rate");
		ComboBox sampl = new ComboBox();
		sampl.getItems().addAll("1kh", "2kh", "3kh", "4kh");
		
		grid.addRow(0, participantID, partID);
		grid.addRow(1, session, sNum);
		grid.addRow(2, file, choose);
		grid.addRow(3, condition, cond);
		grid.addRow(4, numMics, mics);
		grid.addRow(5, sampRate, sampl);
		
		Label lab = new Label();
		lab.setMinSize(100, 100);
		Button next = new Button("NEXT");
		next.setOnAction(event -> {
			state = 1;
			scene.setRoot(screen);
		});
		grid.addRow(6, lab,  next);
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
			recorder.startRecording();
			start.setDisable(true);
			next.setDisable(true);
			stop.setDisable(false);
			stop.setDefaultButton(true);
			start.setDefaultButton(false);
			next.setDefaultButton(false);
			
		});
		stop.setOnAction(event -> {
			status.setBackground(backgrounds(Color.RED, 0, 0));
			status.setText("Status:\t\t" + "Stopped Recording!");
			recorder.stopRecording();
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
			recorder.startPlayback();
		});
		directions.add(playback, 5, 2, 1, 1);
	}
	
}
