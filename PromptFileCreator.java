import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class PromptFileCreator {
	static Scanner scan;
	static ArrayList<String> trainingLines = new ArrayList<String>();
	static ArrayList<String> developmentLines = new ArrayList<String>();
	static ArrayList<String> testLines = new ArrayList<String>();
	
	public static void main(String[] args) {
		scan = null;
		try {
			scan = new Scanner(new File("turk-dialogues.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		String firstLine = scan.nextLine();
		System.out.printf("Definition Line: %s\n", firstLine);
		
		while(scan.hasNext()) {
			String s = scan.nextLine();
			if(s.contains("train"))
				trainingLines.add(s);
			else if(s.contains("dev"))
				developmentLines.add(s);
			else if(s.contains("test"))
				testLines.add(s);
		}
		
		System.out.printf("Training Lines index %d: %s\n Size: %d\n", 0, trainingLines.get(0), trainingLines.size());
		System.out.printf("Development Lines index %d: %s\n Size: %d\n", 0, developmentLines.get(0), developmentLines.size());
		System.out.printf("Test Lines index %d: %s\n Size: %d\n", 0, testLines.get(0), testLines.size());
		
		createPromptSets(developmentLines);
	}
	
	private static void createPromptSets(ArrayList<String> lines) {
		if (lines.equals(developmentLines)) {
			System.out.println("Develpment");
		} else if (lines.equals(testLines)) {
			System.out.println("Test");
		} else if (lines.equals(trainingLines)) {
			System.out.println("Training");
		}
		PrintWriter ASpeakerOutput = null;
		PrintWriter BSpeakerOutput = null;
		try {
			ASpeakerOutput = new PrintWriter(new File("promptSet1.txt"));
			BSpeakerOutput = new PrintWriter(new File("Name"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ASpeakerOutput.close();
		BSpeakerOutput.close();
	}
}
