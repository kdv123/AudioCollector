import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	CheckBox cb1;
	CheckBox cb2;
	CheckBox cb3;
	CheckBox cb4;
	CheckBox cb5;
	File promptFile;
	TextField sNum;
	BorderPane screen;
	Recorder[] listOfRecorders = new Recorder[4];
	ArrayList<Mixer.Info> allMixerInfos = new ArrayList<Mixer.Info>(4);
	ArrayList<Mixer.Info> selectedMics = new ArrayList<Mixer.Info>(4);
	Stage stageOne;
	Label fileName;
	ComboBox<String> cond;
	
	//For recorded file name
	String sesNum;
	String condVal;
	String participantName;
	
	//For task generation
	Label label1 = new Label();
	Label label2 = new Label();
	Label label3 = new Label();
	ArrayList<String []> tasks;
	static String [] command = {};
	
	//For main stage of GUI
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
	Label count;
	GridPane main;
	ArrayList<Label> micLabels = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) throws Exception {
		tasks = scanMe(new File("test.txt"));
		totalTasks = tasks.size();
//		Group g = viewer();
		screen = new BorderPane();
		//screen.setCenter(directions);
//		screen.setBottom(g);
		scene = new Scene(startScreen(primaryStage));
		parseCommandLine(command);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Text Display");
		primaryStage.show();
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);
		stageOne = primaryStage;
		
	}
	
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

	public void drawData(File temp) {
		Stage stage = new Stage();
		Group g = new Group();
		double [] pts = read(temp);
		int width = pts.length / 1000;
		double pix = 1000.0/pts.length;
		double sum = 0;
		Rectangle back = new Rectangle(0, 0, 1000, 500);
		back.setFill(Color.ALICEBLUE);
		g.getChildren().add(back);
		Rectangle r = new Rectangle(0, 50, 1000, 400);
		r.setFill(Color.LIGHTGOLDENRODYELLOW);
		g.getChildren().add(r);
		long start = System.currentTimeMillis();
		for (int i = 0; i < pts.length; i += width) {
			//System.out.println("pts: " + pts[i]);
			// formula for height:  y = (1-pts[i]) * half-of-desired-height; h = pts[i] * desired-height
			// 50 gives a buffer for those cases that produce an overflowing decimal greater than 1.0 for pts[i]
			Rectangle rect = new Rectangle(i * pix, (1 - pts[i]) * 200 + 50, 1, pts[i] * 400);
			g.getChildren().add(rect);
		}
		long end = System.currentTimeMillis();
		//System.out.println(end - start);

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

			setupFileSystem(partID, sNum, cond);

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
			tasks = scanMe(promptFile);
			totalTasks = tasks.size();
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
				String[] current = tasks.get(taskNum);
				
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
					tempRec.setFilePath(t);	//mkdirs() is called in class
					tempRec.setFileName(mixName + "_" + current[0] + ".wav");
				} else {
					listOfRecorders[i].setFileName(mixName + "_" + current[0] + ".wav");
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
						//System.out.println(context);
					}
				}
				parseTask(context, tasks);
				list.add(tasks);
				cols.close();
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
				label.setTextFill(Color.BLACK);
				label.setBackground(promptFill());
			} else {
				label.setTextFill(Color.GRAY);
				label.setBackground(contextFill());
			}	
			grid.add(label, 0, i - 1);
		}
		grid.setBackground(background(Color.TRANSPARENT));
		group.getChildren().add(grid);
		return group;
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
			
			Button playback = new Button("Playback Mic " + (i+1));
			playback.setFont(Font.font(15));
			playback.setMaxSize(100, MIC_H);
			playback.setMaxWidth(800);
			grid.add(label, 0, i, 1, 1);
			grid.add(status, 1, i, 1, 1);
			grid.add(playback, 2, i, 1, 1);
			micLabels.add(status);
			
			playback.setOnAction(event -> {
				if(((Button) event.getSource()).getText().contains("1") && listOfRecorders[0] != null){
					listOfRecorders[0].startPlaybackWAV();
					drawData(listOfRecorders[0].getFile());
				} else if (((Button) event.getSource()).getText().contains("2") && listOfRecorders[1] != null) {
					listOfRecorders[1].startPlaybackWAV();
					drawData(listOfRecorders[1].getFile());
				} else if (((Button) event.getSource()).getText().contains("3") && listOfRecorders[2] != null) {
					listOfRecorders[2].startPlaybackWAV();
					drawData(listOfRecorders[1].getFile());
				} else if (((Button) event.getSource()).getText().contains("4") && listOfRecorders[3] != null) {
					listOfRecorders[3].startPlaybackWAV();
					drawData(listOfRecorders[1].getFile());
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
		start = new Button("Start Recording");
		start.setPrefSize(100, 60);
		stop = new Button("Stop Recording");
		stop.setPrefSize(100, 60);
		next = new Button("Next Prompt");
		next.setPrefSize(100, 60);

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
			
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.PEACHPUFF, 0, 0));
				lab.setText("Status:  ");
			}
			
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
				count.setText("Task " + (taskNum + 1) + " of " + totalTasks);
			} else {
//				Group en = new Group();
//				GridPane grid = new GridPane();
//				Button restart = new Button("New Session");
//				restart.setOnAction(e -> {
//					scene.setRoot(startScreen(stageOne));
//					taskNum = 0;					
//				});
//				Button exit = new Button("Exit");
//				exit.setOnAction(e -> {
//					stageOne.close();
//				});
//				exit.setBackground(background(Color.RED));
//				restart.setBackground(background(Color.GREEN));
//				grid.setBackground(background(Color.AZURE));
//				grid.add(restart, 2, 1);
//				grid.add(exit, 2, 4);
//				en.getChildren().add(grid);
//				//en.getChildren().add(new Rectangle(0, 0, 1000, 400));
				
				scene.setRoot(endScreen());
			}
			
			//Set new file name
			String[] current = tasks.get(taskNum);
			for(int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null) {
					listOfRecorders[i].setFileName(getMixForFile(listOfRecorders[i]) + "_" + current[0] + ".wav");
				}
			}
		});

		/* Listeners attached to buttons here.  Nothing currently attached to "next" */
		start.setOnAction(event -> {
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
			for (int i = 0; i < listOfRecorders.length; i++) {
				if (listOfRecorders[i] != null) {
					listOfRecorders[i].stopRecording();
				}
			}
			
			for (Label lab: micLabels) {
				lab.setBackground(backgrounds(Color.RED, 0, 0));
				lab.setText("Status:  Stopped ...");
			}
			
			start.setDisable(true);
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
				if(li.toString().equals("interface TargetDataLine supporting 8 audio formats, and buffers of at least 32 bytes")) {
					allMixerInfos.add(mixInfo);
				}
			}			
		}
	}
}