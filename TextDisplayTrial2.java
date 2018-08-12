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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TextDisplayTrial2 extends Application {


	public static void main (String [] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane main = new GridPane();
		main.add(prompt(), 0, 0);
		main.add(mics(), 0, 1);
		Label label = new Label();
		label.setPrefSize(800, 50);
		main.add(label, 0, 2);
		makeBtnPanel();
		main.add(btnPanel, 0, 3);
		Group g = new Group();
		
		//Group g = prompt();
		g.getChildren().add(main);
		Scene s = new Scene(g);
		primaryStage.setScene(s);
		primaryStage.setTitle("Text Display");
		primaryStage.show();
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);

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

	public Group promptText() {
		Group g = new Group();

		GridPane grid = new GridPane();
		grid.setPrefSize(400,  400);
		ArrayList<String []> tasks = scanMe("test.txt");
		String [] str = tasks.get(1);
		for (int i = 1; i < 4; i++) {
			Text tex = new Text();
			tex.setText(str[i]);
			tex.setWrappingWidth(100);
			if (i == 2) {
				tex.setFill(Color.YELLOW);
			} else {
				tex.setFill(Color.RED);
			}
			tex.setFont(Font.font(25));
			grid.setBackground(new Background(new BackgroundFill(Color.PINK, new CornerRadii(2), new Insets(0))));
			Label lab = new Label("Once upon a time there lived a puppy that was ginger colored");
			lab.setWrapText(true);
			lab.setMaxWidth(200);
			lab.setTextFill(Color.ALICEBLUE);
			lab.setBackground(new Background(new BackgroundFill(Color.NAVY, new CornerRadii(2), new Insets(0))));
			lab.setFont(Font.font(30));
			grid.add(lab, 0, 0);
			grid.add(tex, 0, i - 1);
		}

		g.getChildren().add(grid);

		return g;
	}

	public Group prompt() {
		Group group = new Group();
		GridPane grid = new GridPane();
		grid.setPrefSize(800,  300);
		ArrayList<String []> tasks = scanMe("test.txt");
		String [] str = tasks.get(1);
		for (int i = 1; i < 4; i++) {
			String s = str[i];
			if (s.charAt(0) == ' ') {
				s = s.substring(1);
			}
			Label label = new Label(s);
			label.setMaxWidth(600);
			label.setFont(Font.font(30));
			label.setWrapText(true);
			label.setPrefWidth(600);
			if (i == 2) {
				label.setTextFill(Color.DARKBLUE);
				label.setBackground(promptFill());
			} else {
				label.setTextFill(Color.CHOCOLATE);
				label.setBackground(contextFill());
			}	
			grid.add(label, 0, i - 1);
		}
		grid.setBackground(stoppedFill());
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
		next.setOnMouseClicked(event -> {
			start.setDisable(false);
			next.setDisable(true);
			stop.setDisable(true);
			start.setDefaultButton(true);
			stop.setDefaultButton(false);
			next.setDefaultButton(false);
			System.err.println("next");
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
		});


	}
	
	private Background backgrounds(Color c, int rad, int inset) {
		return new Background(new BackgroundFill(c, new CornerRadii(rad), new Insets(inset)));
	}


}
