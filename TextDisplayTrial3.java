import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/* August 12, 2018 */
public class TextDisplayTrial3 extends Application {


	public static void main (String [] args) {
		launch(args);
	}

	int taskNum = 0;
	int totalTasks = 4;
	int promptNum = 2;
	File session = null; // to be used as part of the filenaming convention
	Scene scene;
	TextField partID; 
	TextField sessionNum;
	Recorder recorder1;
	Recorder recorder2;
	Recorder recorder3;
	Recorder recorder4;
	CheckBox cb1;
	CheckBox cb2;
	CheckBox cb3;
	CheckBox cb4;
	CheckBox cb5;
	File promptFile;
	BorderPane screen;
	Recorder[] listOfRecorders = new Recorder[5];
	ArrayList<Mixer.Info> allMixerInfos = new ArrayList<Mixer.Info>(5);
	ArrayList<Mixer.Info> selectedMics = new ArrayList<Mixer.Info>(5);
	float[] sampleRates = {16000, 22050, 37800, 44100};
	Stage stageOne;
	
	//For recorded file name
	String sesNum;
	String condVal;
	String participantName;

	@Override
	public void start(Stage primaryStage) throws Exception {
		tasks = scanMe(new File("test.txt"));
		totalTasks = tasks.size();
		Group g = viewer();
		screen = new BorderPane();
		//screen.setCenter(directions);
		screen.setBottom(g);
		scene = new Scene(startScreen(primaryStage));
		primaryStage.setScene(scene);
		primaryStage.setTitle("Text Display");
		primaryStage.show();
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);
		stageOne = primaryStage;
		
	}




	public void drawData(int num) {
		Stage stage = new Stage();
		Group g = new Group();
		String name = "test.wav";
		switch (num) {
		case 1: if (recorder1 != null) {
			name = recorder1.getFileName();
		}
 		break;
		case 2:if (recorder2 != null) {
			name = recorder2.getFileName();
		}
 		break;
		case 3: if (recorder3 != null) {
					name = recorder3.getFileName();
				}
		 		break;
		case 4:if (recorder4 != null) {
			name = recorder4.getFileName();
		}
 		break;
		}
		double [] pts = read(name);
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
			//			chooser.setInitialDirectory(new File("D:\\eclipse-workspace\\AudioCollector"));	//Set initial directory to something else
			//			String filePath = getAbsolutePath().substring(0,absolutePath.lastIndexOf(File.separator));
			File f = new File("TextDisplayTrial3.java");
			//f = f.getParentFile().getParentFile();
			String absPath = f.getAbsolutePath();
			absPath = absPath.substring(0, absPath.lastIndexOf(File.separator));
//			absPath = absPath.substring(0, absPath.lastIndexOf(File.separator));
			
			chooser.setInitialDirectory(new File(absPath)/*new File("AudioCollector")*/);
			promptFile = chooser.showOpenDialog(stage);
		});


		Label condition = new Label("Condition");
		ComboBox<String> cond = new ComboBox<String>();
		cond.getItems().addAll("one", "two", "outside", "inside");

		ArrayList<CheckBox> allMics = new ArrayList<CheckBox>();
		Label selectMics = new Label("Select microphones");
		getMicrophoneInfo();

		EventHandler<ActionEvent> micCheckHandle = e-> {
			CheckBox temp = (CheckBox) e.getSource();
			String name = temp.getText();
			for(Mixer.Info info: allMixerInfos) {
				if (info.getName().equals(name)) {
					selectedMics.add(info);
				}
			}
		};

		System.out.println("all mixer: " + allMixerInfos.size());
		for(int i = 0; i < allMixerInfos.size(); i++) {
			if(!allMixerInfos.get(i).getName().contains("Primary Sound")) {
				if (i == 0) {
					cb1 = new CheckBox(allMixerInfos.get(i).getName());
					cb1.setOnAction(micCheckHandle);
					allMics.add(cb1);
				} else if ( i == 1) {
					cb2 = new CheckBox(allMixerInfos.get(i).getName());
					cb2.setOnAction(micCheckHandle);
					allMics.add(cb2);
				} else if (i == 2) {
					cb3 = new CheckBox(allMixerInfos.get(i).getName());
					cb3.setOnAction(micCheckHandle);
					allMics.add(cb3);
				} else if (i == 3) {
					cb3 = new CheckBox(allMixerInfos.get(i).getName());
					cb3.setOnAction(micCheckHandle);
					allMics.add(cb4);
				} else if (i == 4) {
					cb3 = new CheckBox(allMixerInfos.get(i).getName());
					cb3.setOnAction(micCheckHandle);
					allMics.add(cb5);
				}
			}
		}

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

		Label lab = new Label();
		lab.setMinSize(100, 100);
		Button next = new Button("NEXT");
		next.setOnAction(event -> {
			for(int i = 0; i < selectedMics.size(); i++) {
				if (i == 0) {
					recorder1 = new Recorder(selectedMics.get(0));
					listOfRecorders[0] = recorder1;
				} else if (i == 1) {
					recorder2 = new Recorder(selectedMics.get(1));
					listOfRecorders[1] = recorder2;
				} else if (i == 2) {
					recorder3 = new Recorder(selectedMics.get(2));
					listOfRecorders[2] = recorder3;
				} else if (i == 3) {
					recorder4 = new Recorder(selectedMics.get(3));
					listOfRecorders[3] = recorder4;
				}
			}

			for (int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null) {
					if (partID.getText().length() < 1) {
						listOfRecorders[i].setFileName("Test"+selectedMics.get(i).getName().replaceAll(" ", "")+".WAV");
					}
					sesNum = sNum.getText();
					condVal = cond.getValue();
					participantName = partID.getText();
					listOfRecorders[i].setFileName("participant" + participantName + "_Session"+ sesNum+"_"+listOfRecorders[i].getMixer().getName().replaceAll(" ", "")+ "_"+ condVal+"Prompt1.wav");
				}
			}

			if (selectedMics.size() == 0) {
				Alert alert = new Alert(AlertType.ERROR, "No microphone is selected\nPlease select a mic to continue", ButtonType.OK);
				alert.showAndWait().ifPresent(response -> {
				     if (response == ButtonType.OK) {
				        alert.close();
				     }
				 });
				return;
			}
			
			state = 1;
			taskNum = 0;
			scanMe(promptFile);
			totalTasks = tasks.size();
			scene.setRoot(screen);
		});
		grid.addRow(++row, lab,  next);
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
	private ArrayList<String []> scanMe(File filename) {
		ArrayList<String []> list = new ArrayList<>();
		String id = "";
		String context = "";

		try (Scanner scan = new Scanner(filename)) {
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

	private int micNumRec = 0; // used for generation of pic during playback
	ArrayList<Label> micLabels = new ArrayList<>();
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
			grid.add(label, 0, i, 1, 1);
			grid.add(status, 1, i, 1, 1);
			grid.add(playback, 2, i, 1, 1);
			micNumRec = i;
			micLabels.add(status);
			playback.setOnAction(event -> {
//				if (event.getButton() == MouseButton.PRIMARY) {
				for (Label lab: micLabels) {
					lab.setBackground(backgrounds(Color.CORNFLOWERBLUE, 0, 0));
					lab.setText("Status:  Playing back ...");
				}
					recorder1.startPlaybackWAV();
//				}
				drawData(micNumRec);
				

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
			//Set new file name
			for(int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null)
					listOfRecorders[i].setFileName("participant" + participantName + "_Session"+ sesNum +"_"+listOfRecorders[i].getMixer().getName().replaceAll(" ", "")+ "_"+ condVal +"Prompt"+ promptNum + ".wav");
			}
			promptNum++;
			
			start.setDisable(false);
			next.setDisable(true);
			stop.setDisable(true);
			start.setDefaultButton(true);
			stop.setDefaultButton(false);
			next.setDefaultButton(false);
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.PEACHPUFF, 0, 0));
				lab.setText("Status:  ");
			}
			System.err.println("next");
			System.out.println("next!");
			taskNum++;
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
				GridPane grid = new GridPane();
				Button restart = new Button("New Session");
				restart.setOnAction(e -> {
					scene.setRoot(startScreen(stageOne));
					taskNum = 0;					
				});
				Button exit = new Button("Exit");
				exit.setOnAction(e -> {
					stageOne.close();
				});
				exit.setBackground(background(Color.RED));
				restart.setBackground(background(Color.GREEN));
				grid.setBackground(background(Color.AZURE));
				grid.add(restart, 2, 1);
				grid.add(exit, 2, 4);
				en.getChildren().add(grid);
				//en.getChildren().add(new Rectangle(0, 0, 1000, 400));
				
				scene.setRoot(en);
			}
			//main.add(prompt(), 0, 1);
		});

		/* Listeners attached to buttons here.  Nothing currently attached to "next" */
		start.setOnAction(event -> {
			//			status.setBackground(backgrounds(Color.GREEN, 0, 0));
			//			status.setText(status.getText() + "Recording ...");
			//			recorder.startRecording();
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.GREEN, 0, 0));
				lab.setText("Status:  Recording...");
			}
			System.err.println("started");
			start.setDisable(true);
			next.setDisable(true);
			stop.setDisable(false);
			stop.setDefaultButton(true);
			start.setDefaultButton(false);
			next.setDefaultButton(false);
			System.out.println(event.getSource());
			int lastMicIndex = 0;
			for(int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null && !listOfRecorders[i].getMixer().getName().equals("Primary Sound Capture Driver")) {
					listOfRecorders[i].startRecordingSingleInputWAV();
					lastMicIndex = i;
				}
			}
				
			listOfRecorders[lastMicIndex].setTargetStatus(true);	//Syncs mics
		});
		stop.setOnAction(event -> {
			//			status.setBackground(backgrounds(Color.RED, 0, 0));
			//			status.setText("Status:\t\t" + "Stopped Recording!");
			//			recorder.stopRecording();
			
			for (int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null) {
					listOfRecorders[i].stopRecording();
				}
			}
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.RED, 0, 0));
				lab.setText("Status:  Stopped ...");
			}
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
//	public void getMicrophoneInfo() {
//		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
//		for (Mixer.Info mixInfo : mixers) {
//			Mixer m = AudioSystem.getMixer(mixInfo);
//			Line.Info[] lines = m.getTargetLineInfo();
//
//			for (Line.Info li : lines) {
//				if(li.toString().equals("interface TargetDataLine supporting 8 audio formats, and buffers of at least 32 bytes")) {
//					mixerToTarget.put(mixInfo, li);
//				}
//			}			
//		}
//	}
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
