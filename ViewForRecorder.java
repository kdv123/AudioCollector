
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewForRecorder extends Application {

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
	SimpleAudioRecorder2 sar = new SimpleAudioRecorder2();
	Scanner userPrompt;
	ArrayList<String []> sessionInfo;
	
	private ArrayList<String []> scanMe(String filename) {
		ArrayList<String []> list = new ArrayList<>();
		String id = "";
//		String input1 = "";
//		String speak = "";
//		String input2 = "";
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
//				Scanner info = new Scanner(context);
//				int part = 0;
//				while(info.hasNext()) {
//					String s = info.next();
//					//String output = "";
//					if (s.equals("<br>")) {
//						switch (part) {
//						case 0: input1 += "\n";
//						case 1: speak += "\n";
//						case 2: input2 += "\n";
//						}
//					} else if (s.equals("<h>")) {
//						part = 1;
//					} else if (s.equals("</h>")) {
//						part = 2;
//					} else {
//						switch (part) {
//						case 0: input1 += s;
//						case 1: speak += s;
//						case 2: input2 += s;
//						}
//					}
//				}
				list.add(tasks);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private void parseTask(String context, String [] parts) {
		Scanner info = new Scanner(context);
		for (int i = 1; i < parts.length; i++) {
			parts[i] = "";
		}
		int part = 1;
		while(info.hasNext()) {
			String s = info.next();
			//String output = "";
			if (s.equals("<br>")) {
				parts[part] += "\n";
				
			} else if (s.equals("<h>")) {
				part = 2;
			} else if (s.equals("</h>")) {
				part = 3;
			} else {
				parts[part] += s;
			}
		}
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
		scene = new Scene(screen);
		scene.setFill(Color.ANTIQUEWHITE);
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

		/* Listeners attached to buttons here.  Nothing currently attached to "next" */
		start.setOnAction(event -> {
			status.setBackground(backgrounds(Color.GREEN, 0, 0));
			status.setText(status.getText() + "Recording ...");
			sar.startRecording();
		});
		stop.setOnAction(event -> {
			status.setBackground(backgrounds(Color.RED, 0, 0));
			status.setText("Status:\t\t" + "Stopped Recording!");
			sar.stopRecording();
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
			sar.playback();
		});
		directions.add(playback, 5, 2, 1, 1);
	}
	
}
