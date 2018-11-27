import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/*
 * GUI for recording audio. Recorder class is used to record and playback audio in WAV format.
 * 
 * Useful information: 
 * -default text file on start screen is "test.txt"
 * -directory system is created when the user enters text into the text boxes on the start screen
 * -user may use the enter key to navigate through the experiment's recording functions and next prompt functions
 * 
 */
public class TextDisplayTrial3 extends Application {

	public static void main (String [] args) {
		command = args;
		launch(args);
	}

	int taskNum = 0;
	int totalTasks = 4;
	int promptNum = 2;
	Scene scene;
	TextField partID;
	Recorder recorder1;
	Recorder recorder2;
	Recorder recorder3;
	Recorder recorder4;
	PrintWriter uiLog;
	
	//For task generation
	ArrayList<ArrayList<String>> allTasks;
	ArrayList<Label> taskLabels = new ArrayList<Label>();
	static String [] command = {};
	
	//For main stage of GUI
	double WIDTH = 1000;
	double HEIGHT = 800;
	double MIC_H = 65;
	double MIC_W = 800;
	ArrayList<String []> sessionInfo;

	@Override
	public void start(Stage primaryStage) throws Exception {
		screen = new BorderPane();
		scene = new Scene(startScreen(primaryStage));
		parseCommandLine(command);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Text Display");
		primaryStage.show();
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);
		stageOne = primaryStage;
		
		//PrintWriter will write once window is closed
		Window temp = scene.getWindow();
		temp.setOnCloseRequest( e -> {
			if (uiLog != null) {
				uiLog.print("<Close Window>");
				uiLog.close();
			}
		});
	}
	
	//For recorded file name
	String sesNum;
	String condVal;
	String participantName;
	public void parseCommandLine(String [] args) {
		if (args.length == 1) {
			promptFile = new File(args[0]);
			fileName.setText(promptFile.getName());
		} else if (args.length == 2) {
			participantName = args[0];
			partID.setText(participantName);
			promptFile = new File(args[1]);
			fileName.setText(promptFile.getName());
		} else if (args.length == 3) {
			participantName = args[0];
			partID.setText(participantName);
			sesNum = args[1];
			sNum.setText(sesNum);
			promptFile = new File(args[2]);
			fileName.setText(promptFile.getName());
		} else if (args.length == 4) {
			participantName = args[0];
			partID.setText(participantName);
			sesNum = args[1];
			sNum.setText(sesNum);
			promptFile = new File(args[2]);
			fileName.setText(promptFile.getName());
			condVal = args[3];
			cond.setValue(condVal);
		}
	}

	public Image drawData(File temp) {
		//Stage stage = new Stage();
		Group g = new Group();
		double [] pts;
		if ( temp.exists() ) {
			pts = read(temp);
		} else {
			pts = new double[0];
		}
		int width = pts.length / (int)MIC_W;
		double pix = MIC_W/pts.length;
		double sum = 0;
		Rectangle back = new Rectangle(0, 0, MIC_W, MIC_H);
		back.setFill(Color.ALICEBLUE);
		g.getChildren().add(back);
		Rectangle r = new Rectangle(0, 2, MIC_W, MIC_H);
		r.setFill(Color.LIGHTGOLDENRODYELLOW);
		g.getChildren().add(r);
		long start = System.currentTimeMillis();
		for (int i = 0; i < pts.length; i += width) {
			//System.out.println("pts: " + pts[i]);
			// formula for height:  y = (1-pts[i]) * half-of-desired-height; h = pts[i] * desired-height
			// 50 gives a buffer for those cases that produce an overflowing decimal greater than 1.0 for pts[i]
			Rectangle rect = new Rectangle(i * pix, (1 - pts[i]) * (r.getHeight()/2) + 2, 1, pts[i] * r.getHeight());
			g.getChildren().add(rect);
		}
		long end = System.currentTimeMillis();
		//System.out.println(end - start);

		Scene s = new Scene(g);
		WritableImage i = new WritableImage((int)MIC_W, (int)MIC_H);
		s.snapshot(i);
		return i;
	}

	/**
	 * Reads audio samples from a file (in .wav or .au format) and returns
	 * them as a double array with values between -1.0 and +1.0.
	 * Modified from Princeton's StdAudio https://introcs.cs.princeton.edu/java/stdlib/StdAudio.java.html
	 *
	 * @param  filename the name of the audio file
	 * @return the array of samples
	 */

	public static double[] read(File temp) {
		byte[] data = readByte(temp);
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
	private static byte[] readByte(File temp) {
		byte[] data = null;
		AudioInputStream ais = null;
		try {
			
			if (temp.exists()) {
				ais = AudioSystem.getAudioInputStream(temp);
				int bytesToRead = ais.available();
				data = new byte[bytesToRead];
				int bytesRead = ais.read(data);
				if (bytesToRead != bytesRead)
					throw new IllegalStateException("read only " + bytesRead + " of " + bytesToRead + " bytes"); 
			}

		}
		catch (IOException e) {
			throw new IllegalArgumentException();
		}

		catch (UnsupportedAudioFileException e) {
			throw new IllegalArgumentException();
		}

		return data;
	}

	int state = 0;
	File promptFile;
	TextField sNum;
	BorderPane screen;
	Recorder[] listOfRecorders = new Recorder[4];
	ArrayList<Mixer.Info> allMixerInfos = new ArrayList<Mixer.Info>(4);
	ArrayList<Mixer.Info> selectedMics = new ArrayList<Mixer.Info>(4);
	Stage stageOne;
	Label fileName;
	ComboBox<String> cond;
	/**
	 * Creates a display for the opening setup of a session 
	 * and allows for transition to the next part.
	 * @return
	 */
	public Group startScreen(Stage stage) {
		Group g = new Group();
		GridPane grid = new GridPane();

		Label participantID = new Label("Participant ID");
		partID = new TextField();
		partID.setPromptText("participant ID");

		Label session = new Label("Session #");
		sNum = new TextField();
		sNum.setPromptText("Session #");

		Label fileLabel = new Label("file");
		Button choose = new Button("File");
		promptFile = new File("test.txt");	//default to test.txt if no file is chosen
		fileName = new Label("");
		fileName.setBackground(background(Color.ALICEBLUE));
		
		choose.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			File f = new File("TextDisplayTrial3.java");
			String absPath = f.getAbsolutePath();
			absPath = absPath.substring(0, absPath.lastIndexOf(File.separator));
			
			chooser.setInitialDirectory(new File(absPath));
			promptFile = chooser.showOpenDialog(stage);
			fileName.setText(promptFile.getName());
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
		
		CheckBox micCB;
		for(int i = 0; i < allMixerInfos.size(); i++) {
			micCB = new CheckBox(allMixerInfos.get(i).getName());
			micCB.setOnAction(micCheckHandle);
			allMics.add(micCB);
		}

		grid.addRow(0, participantID, partID);
		grid.addRow(1, session, sNum);
		grid.addRow(2, fileLabel, choose, fileName);
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

			try {
				uiLog = new PrintWriter(new File(recorder1.getFilePath() + File.separator + "UILog.txt"));
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
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
			totalTasks = allTasks.size();
			setupFileSystem(partID, sNum, cond);
			
			screen.setBottom(viewer());
			scene.setRoot(screen);
		});
		
		grid.addRow(++row, lab,  next);
		g.getChildren().add(grid);

		
		return g;
	}
	
	private void setupFileSystem(TextField partID, TextField sNum, ComboBox<String> cond) {
		String t = System.getProperty("user.dir");
		boolean dirs = false;
		
		for (int i = 0; i < listOfRecorders.length; i++) {
			Recorder tempRec = listOfRecorders[i];
			if (tempRec != null) {
				String mixName = getMixForFile(listOfRecorders[i]);
				participantName = partID.getText();
				sesNum = sNum.getText();
				condVal = cond.getValue();
				ArrayList<String> current = allTasks.get(taskNum);
				
				if(participantName.length() != 0) {
					t += File.separator + participantName;
					dirs = true;
				}
				
				if (sesNum.length() != 0) {
					t += File.separator + sesNum;
					dirs = true;
				}
				
				if (condVal != null) {
					t += File.separator + condVal;
					dirs = true;
				}
				
				if (dirs) {
					File temp = new File(t);	//make appropriate directories
					temp.mkdirs();
					tempRec.setFilePath(t);
					tempRec.setFileName(mixName + "_" + current.get(0) + ".wav");
				} else {
					listOfRecorders[i].setFileName(mixName + "_" + current.get(0) + ".wav");
				}
			}
		}
	}
	
	private String getMixForFile(Recorder rec) {
		if(rec != null) {
			return rec.getMixer().getName().replaceAll(" ", "");
		} else {
			System.err.println("Recorder is null!");
			return null;
		}
	}

	GridPane btnPanel;
	Label status;
	Button prev;
	Button start;
	Button stop;
	Button next;
	Label count;
	GridPane main;
	ArrayList<Label> micLabels = new ArrayList<>();

	public Group viewer() {
		main = new GridPane();
		GridPane taskBar = new GridPane();
		Label spacer = new Label();
		spacer.setText("Directions: Read the yellow highlighted text aloud");
		spacer.setFont(Font.font("times", FontWeight.BOLD, 20));
		spacer.setPrefSize(600, 30);
		count = new Label("Task " + (taskNum + 1) + " of " + totalTasks);
		count.setTextFill(Color.CRIMSON);
		count.setBackground(background(Color.FLORALWHITE));
		count.setFont(Font.font(27));
		taskBar.add(spacer, 0, 0);
		taskBar.add(count, 1, 0);
		main.add(taskBar, 0, 0);
		
		GridPane temp = prompt();
		temp.setAlignment(Pos.TOP_CENTER);
		main.add(temp, 0, 1);
		
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
	
	/*
	 * Scans the prompt file.
	 * 
	 * I changed the format of the definition files. I thought the <br> were kind of redundant. Each sentence in a prompt is just tab delimited for each speaker. Each speaker
	 * is its own element in an ArrayList (the tasks can be several sizes) the whole prompt is then added to allTasks.
	 */
	private void scanMe(File promptFileName) {
		ArrayList<ArrayList<String>> tempAllTasks = new ArrayList<ArrayList<String>>();
		
		try (Scanner scan = new Scanner(promptFileName)) {
			while (scan.hasNext()) {
				Scanner lineScanner = new Scanner(scan.nextLine());
				lineScanner.useDelimiter("\t");
				
				ArrayList<String> parts = new ArrayList<String>();
				while (lineScanner.hasNext()) {
					parts.add(lineScanner.next());
				}
				
				tempAllTasks.add(parts);
				lineScanner.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		allTasks = tempAllTasks;
	}
	
	public GridPane prompt() {
		GridPane grid = new GridPane();
		grid.setPrefSize(800, 300);
		
		ArrayList<String> task = allTasks.get(taskNum);
		
		for (int i = 1; i < task.size(); i++) {
			String tempString = task.get(i);
			Label tempLabel = new Label();
			
			if (tempString.contains("<h>") && tempString.contains("</h>")) {
				tempLabel.setTextFill(Color.BLACK);
				tempLabel.setBackground(promptFill());
				tempString = tempString.replaceAll("<h>", "");
				tempString = tempString.replaceAll("</h>", "");
			} else if (tempString.contains("<h>") && !tempString.contains("</h>")) {
				System.err.println("FILE IS INCORRECT");
			} else {
				tempLabel.setTextFill(Color.GRAY);
				tempLabel.setBackground(contextFill());
			}
			
			if (i % 2 == 1) {
				tempString = "A" + (i/2 + 1) + ": " + tempString;
			} else {
				tempString = "B" + (i/2 + 1) + ": " + tempString; 
			}
			
			tempLabel.setText(tempString);
			tempLabel.setWrapText(true);
			tempLabel.setPrefWidth(800);
			tempLabel.setFont(Font.font(24));
			
			taskLabels.add(tempLabel);
			grid.addRow(i -1, tempLabel);
		}
		
		grid.setBackground(background(Color.TRANSPARENT));
		return grid;
	}
	
	public ArrayList<Label> updateLabels() {
		ArrayList<String> task = allTasks.get(taskNum);
		ArrayList<Label> tempTaskLabels = new ArrayList<Label>();
		
		for (int i = 1; i < task.size(); i++) {
			String tempString = task.get(i);
			Label tempLabel = new Label();
			
			if (tempString.contains("<h>") && tempString.contains("</h>")) {
				tempLabel.setTextFill(Color.BLACK);
				tempLabel.setBackground(promptFill());
				tempString = tempString.replaceAll("<h>", "");
				tempString = tempString.replaceAll("</h>", "");
			} else if (tempString.contains("<h>") && !tempString.contains("</h>")) {
				System.err.println("FILE IS INCORRECT");
			} else {
				tempLabel.setTextFill(Color.GRAY);
				tempLabel.setBackground(contextFill());
			}
			
			if (i % 2 == 1) {
				tempString = "A" + (i/2 + 1) + ": " + tempString;
			} else {
				tempString = "B" + (i/2 + 1) + ": " + tempString; 
			}
			
			tempLabel.setText(tempString);
			tempLabel.setWrapText(true);
			tempLabel.setPrefWidth(800);
			tempLabel.setFont(Font.font(24));
			
			tempTaskLabels.add(tempLabel);
		}
		
		return tempTaskLabels;
	}

	private Background promptFill() {
		return new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(2), new Insets(0)));
	}

	private Background contextFill() {
		return new Background(new BackgroundFill(Color.ALICEBLUE, new CornerRadii(2), new Insets(0)));
	}

	private Background neutralFill() {
		return new Background(new BackgroundFill(Color.PINK, new CornerRadii(2), new Insets(0)));
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
			ImageView status = new ImageView();
			//Label status = new Label("Status: ");
			//status.setBorder(new Border));
			if ( listOfRecorders[i] != null && listOfRecorders[i].getFile() != null ) {
				status.setImage(drawData(listOfRecorders[i].getFile()));
			}
			
			Button playback = new Button("Playback Mic " + (i+1));
			playback.setFont(Font.font(15));
			playback.setMaxSize(100, MIC_H);
			playback.setMaxWidth(800);
			grid.add(label, 0, i, 1, 1);
			grid.add(status, 1, i, 1, 1);
			grid.add(playback, 2, i, 1, 1);
			
			playback.setOnAction(event -> {
				if(((Button) event.getSource()).getText().contains("1") && listOfRecorders[0] != null && listOfRecorders[0].getFile().exists()){
					listOfRecorders[0].startPlaybackWAV();
					status.setImage(drawData(listOfRecorders[0].getFile()));
					uiLog.println("<Playback1>");
				} else if (((Button) event.getSource()).getText().contains("2") && listOfRecorders[1] != null && listOfRecorders[0].getFile().exists()) {
					listOfRecorders[1].startPlaybackWAV();
					status.setImage(drawData(listOfRecorders[1].getFile()));
					uiLog.println("<Playback2>");
				} else if (((Button) event.getSource()).getText().contains("3") && listOfRecorders[2] != null && listOfRecorders[0].getFile().exists()) {
					listOfRecorders[2].startPlaybackWAV();
					status.setImage(drawData(listOfRecorders[2].getFile()));
					uiLog.println("<PlaybackBtn3>");
				} else if (((Button) event.getSource()).getText().contains("4") && listOfRecorders[3] != null && listOfRecorders[0].getFile().exists()) {
					listOfRecorders[3].startPlaybackWAV();
					status.setImage(drawData(listOfRecorders[3].getFile()));
					uiLog.println("<PlaybackBtn4>");
				}
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
		prev = new Button("Previous Prompt");
		prev.setPrefSize(100, 60);
		start = new Button("Start Recording");
		start.setPrefSize(100, 60);
		stop = new Button("Stop Recording");
		stop.setPrefSize(100, 60);
		next = new Button("Next Prompt");
		next.setPrefSize(100, 60);

		prev.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(2), new Insets(0))));
		start.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(2), new Insets(0))));
		stop.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(2), new Insets(0))));
		next.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(2), new Insets(0))));

		/*Dummy buttons for spacing purposes */
		for (int i = 0; i < 8; i += 2) {
			Button d1 = new Button();
			d1.setPrefSize(100,  60);
			btnPanel.add(d1, i, 0);
			d1.setVisible(false);
		}


		btnPanel.add(prev, 1, 0);
		btnPanel.add(start, 3, 0);
		btnPanel.add(stop, 5, 0);
		btnPanel.add(next, 7, 0);

		next.setDisable(true);
		stop.setDisable(true);
		prev.setDisable(true);
		start.setDefaultButton(true);
		start.requestFocus();

		// Default buttons not currently working for typing ENTER to move to the next one
		next.setOnAction(event -> {
			uiLog.println("<Next>");
			
			start.setDisable(false);
			next.setDisable(true);
			prev.setDisable(false);
			stop.setDisable(true);
			start.setDefaultButton(true);
			stop.setDefaultButton(false);
			next.setDefaultButton(false);
			
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.PEACHPUFF, 0, 0));
				lab.setText("Status:  ");
			}
			
			taskNum++;
			if (taskNum < totalTasks) {
				taskLabels = updateLabels();
				count.setText("Task " + (taskNum + 1) + " of " + totalTasks);
				screen.setBottom(viewer());
				scene.setRoot(screen);
			} else {	
				scene.setRoot(endScreen());
			}
			
			//Set new file name
			if (taskNum < totalTasks) {
				ArrayList<String> current = allTasks.get(taskNum);
				for(int i = 0; i < listOfRecorders.length; i++) {
					if (listOfRecorders[i] != null) {
							listOfRecorders[i].setFileName(getMixForFile(listOfRecorders[i]) + "_" + current.get(0) + ".wav");
					}
				}
			}
		});
		
		prev.setOnAction(event -> {
			uiLog.println("<Previous>");
			start.setDisable(false);
			next.setDisable(false);
			stop.setDisable(true);
			start.setDefaultButton(true);
			stop.setDefaultButton(false);
			next.setDefaultButton(false);
			
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.PEACHPUFF, 0, 0));
				lab.setText("Status:  ");
			}
			
			taskNum--;
			if (taskNum < totalTasks && taskNum >= 0) {
				taskLabels = updateLabels();
				count.setText("Task " + (taskNum + 1) + " of " + totalTasks);
				screen.setBottom(viewer());
				scene.setRoot(screen);
			}			
			
			//Set new file name
			if (taskNum >= 0) {
				ArrayList<String> current = allTasks.get(taskNum);
				for(int i = 0; i < listOfRecorders.length; i++) {
					if (listOfRecorders[i] != null) {
						listOfRecorders[i].setFileName(getMixForFile(listOfRecorders[i]) + "_" + current.get(0) + ".wav");
					}
				}
			}
		});

		/* Listeners attached to buttons here.  Nothing currently attached to "next" */
		start.setOnAction(event -> {
			uiLog.println("<Start>");
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.GREEN, 0, 0));
				lab.setText("Status:  Recording...");
			}
			
			start.setDisable(true);
			next.setDisable(true);
			stop.setDisable(false);
			stop.setDefaultButton(true);
			start.setDefaultButton(false);
			next.setDefaultButton(false);
			
			int lastMicIndex = 0;
			for(int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null && !listOfRecorders[i].getMixer().getName().equals("Primary Sound Capture Driver")) {
					listOfRecorders[i].startRecordingSingleInputWAV();
					lastMicIndex = i;
				}
			}
				
			listOfRecorders[lastMicIndex].setTargetStatus(true);	//Syncs mics start
			playRecordingBeep();
		});
		
		stop.setOnAction(event -> {
			uiLog.println("<Stop>");
			for (int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null) {
					listOfRecorders[i].stopRecording();
				}
			}
			
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.RED, 0, 0));
				lab.setText("Status:  Stopped ...");
			}
			
			start.setDisable(false);
			next.setDisable(false);
			stop.setDisable(true);
			next.setDefaultButton(true);
			stop.setDefaultButton(false);
			start.setDefaultButton(false);

		});
	}
	
	public Group endScreen() {
		Group group = new Group();
		GridPane grid = new GridPane();
		Label lab = new Label("Thank you for participating\nin our study");
		lab.setPrefWidth(1000);
		lab.setPrefHeight(400);
		lab.setBackground(background(Color.PEACHPUFF));
		grid.addRow(0, lab);
		Button exit = new Button("Exit");
		exit.setOnAction(e -> {
			uiLog.print("<Final Exit>");
			uiLog.close();
			stageOne.close();
		});
		exit.setPrefWidth(1000);
		exit.setBackground(background(Color.RED));
		exit.setFont(Font.font(60));
		grid.addRow(1, exit);
		group.getChildren().add(grid);
		return group;
	}

	private Background backgrounds(Color c, int rad, int inset) {
		return new Background(new BackgroundFill(c, new CornerRadii(rad), new Insets(inset)));
	}
	
	public void playRecordingBeep() {
		Clip clip = null;
		AudioInputStream audioStream;
		
		try {
			audioStream = AudioSystem.getAudioInputStream(new File("buttonBeep.wav"));
			clip = AudioSystem.getClip();
			clip.open(audioStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clip.start();
		//To play the entire clip and not close
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} while (clip.isActive());
		
		clip.close();
	}

	public void getMicrophoneInfo() {
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		for (Mixer.Info mixInfo : mixers) {
			Mixer m = AudioSystem.getMixer(mixInfo);
			Line.Info[] lines = m.getTargetLineInfo();
			
			for (Line.Info li : lines) {
				if(li.toString().contains("interface TargetDataLine")) {
					allMixerInfos.add(mixInfo);
				}
			}			
		}
	}
}