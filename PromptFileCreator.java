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


		
		scan.nextLine();	//First line
		
		while(scan.hasNext()) {
			String s = scan.nextLine();
			if(s.contains("train"))
				trainingLines.add(s);
			else if(s.contains("dev"))
				developmentLines.add(s);
			else if(s.contains("test"))
				testLines.add(s);
		}
		
		//See method header
		//createDevPromptSets(developmentLines);
		createPracticePrompts(trainingLines);
	}
	
	/*
	 * Create 9 practice prompts
	 */
	private static void createPracticePrompts(ArrayList<String> lines) {
		PrintWriter practiceWriter = null;
		
		try {
			practiceWriter = new PrintWriter("practice.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 3; i++) {
			
			Scanner scan = new Scanner(lines.get(i));
			scan.useDelimiter("\t");
			String train = scan.next();
			String chain = scan.next();
			chain = chain.substring(5);		//removes the chain part
			String a1 = scan.next();
			String b1 = scan.next();
			String a2 = scan.next();
			String b2 = scan.next();
			String a3 = scan.next();
			String b3 = scan.next();
			
			if (i % 2 == 0) {
				practiceWriter.println("a1_practice\t" + "<h>" + a1 + "</h>");
				practiceWriter.println("a2_practice\t" + a1 + "\t" + b1 + "\t<h>" + a2 + "</h>");
				practiceWriter.println("a3_practice\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t<h>" + a3 + "</h>");
			} else {
				practiceWriter.println("a1_practice\t" + a1 + "\t<h>" + b1 + "</h>");
				practiceWriter.println("a2_practice\t" + a1 + "\t" + b1 + "\t" + a2 + "\t<h>" + b2 + "</h>");
				practiceWriter.println("a3_practice\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t" + a3 + "\t<h>" + b3 + "</h>");
			}
			
		}
		
		scan.close();
		practiceWriter.close();
		
	}

	/*
	 * Currently only creating development lines. Functionality for test lines should also be available, but have not been tested yet.
	 * Using this method, every 28 lines will creates 84 prompts for an odd number participant and 84 prompts for an even number participant.
	 * For the development this will create 20 prompt files. 280 Development lines, 10 for odd participant, 10 for even participant. Each participant
	 * will speak 84 total prompts. We are assuming 3 prompts will take one minute. Thus 28 minutes for the times the user will be utilizing the interface.
	 */
	private static void createDevPromptSets(ArrayList<String> lines) {
		int subSize = 28;

		//For each set of 28 lines in the development lines set (280 total)
		for (int i = 0; i < 10; i++) {
			//File name syntax: p<participant #>_dev_<lineStartFromSet>_<lineEndFromSet>.txt
			String fileNameOdd = "";
			String fileNameEven = "";
			
			switch (i) {
			case 0:
				fileNameOdd = "p1_dev.txt";
				fileNameEven = "p2_dev.txt";
				break;
			case 1:
				fileNameOdd = "p3_dev.txt";
				fileNameEven = "p4_dev.txt";
				break;
			case 2:
				fileNameOdd = "p5_dev.txt";
				fileNameEven = "p6_dev.txt";
				break;
			case 3:
				fileNameOdd = "p7_dev.txt";
				fileNameEven = "p8_dev.txt";
				break;
			case 4:
				fileNameOdd = "p9_dev.txt";
				fileNameEven = "p10_dev.txt";
				break;
			case 5:
				fileNameOdd = "p11_dev.txt";
				fileNameEven = "p12_dev.txt"; 
				break;
			case 6:
				fileNameOdd = "p13_dev.txt";
				fileNameEven = "p14_dev.txt";
				break;
			case 7:
				fileNameOdd = "p15_dev.txt";
				fileNameEven = "p16_dev.txt";
				break;
			case 8:
				fileNameOdd = "p17_dev.txt";
				fileNameEven = "p18_dev.txt";
				break;
			case 9:
				fileNameOdd = "p19_dev.txt";
				fileNameEven = "p20_dev.txt";
				break;
			}
			
			PrintWriter outputOdd = null;
			PrintWriter outputEven = null;
			try {
				outputOdd = new PrintWriter(fileNameOdd);
				outputEven = new PrintWriter(fileNameEven);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			int numOdd = 1;
			int numEven = 1;
			//String context: <highlighted speaker>_dev_chainNum_<stuff to print to screen>
			for (int j = i * subSize; j < (i+1) * subSize; j++) {
				Scanner scan = new Scanner(lines.get(j));
				scan.useDelimiter("\t");
				String dev = scan.next();
				String chain = scan.next();
				chain = chain.substring(5);		//removes the chain part
				String a1 = scan.next();
				String b1 = scan.next();
				String a2 = scan.next();
				String b2 = scan.next();
				String a3 = scan.next();
				String b3 = scan.next();
				
				if (j % 2 == 0) {
					outputOdd.println("a1_" + dev + numOdd++ + "\t<h>" + a1 + "</h>");
					outputOdd.println("a2_" + dev + numOdd++ + "\t" + a1 + "\t" + b1 + "\t<h>" + a2 + "</h>" );
					outputOdd.println("a3_" + dev + numOdd++ + "\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t<h>" + a3 + "</h>");
					outputEven.println("b1_" + dev + numEven++ + "\t" + a1 + "\t<h>" + b1 + "</h>");
					outputEven.println("b2_" + dev + numEven++ + "\t" + a1 + "\t" + b1 + "\t" + a2 + "\t<h>" + b2 + "</h>");
					outputEven.println("b3_" + dev + numEven++ + "\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t" + a3 + "\t<h>" + b3 + "</h>");
				} else {
					outputEven.println("a1_" + dev + numEven++ + "\t<h>" + a1 + "</h>");
					outputEven.println("a2_" + dev + numEven++ + "\t" + a1 + "\t" + b1 + " \t<h>" + a2 + "</h>" );
					outputEven.println("a3_" + dev + numEven++ + "\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t<h>" + a3 + "</h>");
					outputOdd.println("b1_" + dev + numOdd++ + "\t" + a1 + "\t<h>" + b1 + "</h>");
					outputOdd.println("b2_" + dev + numOdd++ + "\t" + a1 + "\t" + b1 + "\t" + a2 + "\t<h>" + b2 + "</h>");
					outputOdd.println("b3_" + dev + numOdd++ + "\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t" + a3 + "\t<h>" + b3 + "</h>");
				}
				
				scan.close();
			}
			
			outputOdd.close();
			outputEven.close();
		}
		
		
		
		
//		PrintWriter ASpeakerOutput = null;
//		PrintWriter BSpeakerOutput = null;
//		try {
//			ASpeakerOutput = new PrintWriter(new File("promptSet1.txt"));
//			BSpeakerOutput = new PrintWriter(new File("Name"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		ASpeakerOutput.close();
//		BSpeakerOutput.close();
	}
}
