import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class PromptFileCreator {
	static Scanner scan;
	static ArrayList<String> practiceLines = new ArrayList<String>();
	static ArrayList<String> experimentLines = new ArrayList<String>();
	
	public static void main(String[] args) {
		scan = null;
		try {
			scan = new Scanner(new File("turk-dialogues-merged-cleaned-case.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/* Lines 1 to 3 will be to create 9 practice prompts
		 * We will create 1204 experiment lines as it is divisble by 28
		 * 28 lines creates 84 practice prompts for participants.
		 */
		for (int i = 0; i < 1207 && scan.hasNext(); i++ ) {
			if (i < 3) {
				practiceLines.add(scan.nextLine());
			} else
				experimentLines.add(scan.nextLine());
		}
		
		//See method header
		//Only creating 20 particpant files as of 2/9/2019
		createParticipantFiles(experimentLines);
		createPracticePrompts(practiceLines);
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
		
		int count = 1;
		for (int i = 0; i < 3; i++) {
			
			Scanner scan = new Scanner(lines.get(i));
			scan.useDelimiter("\t");
			String a1 = scan.next();
			String b1 = scan.next();
			String a2 = scan.next();
			String b2 = scan.next();
			String a3 = scan.next();
			String b3 = scan.next();
			
			a1 = a1.replaceAll("\"", "");
			a2 = a2.replaceAll("\"", "");
			a3 = a3.replaceAll("\"", "");
			b1 = b1.replaceAll("\"", "");
			b2 = b2.replaceAll("\"", "");
			b3 = b3.replaceAll("\"", "");
			
			if (i % 2 == 0) {
				practiceWriter.println(count++ + "_practice_a1\t<h>" + a1 + "</h>");
				practiceWriter.println(count++ + "_practice_a2\t" + a1 + "\t" + b1 + "\t<h>" + a2 + "</h>");
				practiceWriter.println(count++ + "_practice_a3\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t<h>" + a3 + "</h>");
			} else {
				practiceWriter.println(count++ + "_practice_b1\t"+ a1 + "\t<h>" + b1 + "</h>");
				practiceWriter.println(count++ + "_practice_b2\t" + a1 + "\t" + b1 + "\t" + a2 + "\t<h>" + b2 + "</h>");
				practiceWriter.println(count++ + "_practice_b3\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t" + a3 + "\t<h>" + b3 + "</h>");
			}
			
			scan.close();
		}
		
		practiceWriter.close();
		
	}

	private static void createParticipantFiles(ArrayList<String> lines) {
		int subSize = 28;
		int countOdd = 0;
		int countEven = 0;
		
		//For each set of 28 lines
		for (int i = 0; i < 14; i++) {
			String fileNameOdd = "p" + (i*2 + 1) + ".txt";
			String fileNameEven = "p" + (i*2 + 2) + ".txt";

			// It does not require to hard code the following lines
			/*switch (i) {
			case 0:
				fileNameOdd = "p1.txt";
				fileNameEven = "p2.txt";
				break;
			case 1:
				fileNameOdd = "p3.txt";
				fileNameEven = "p4.txt";
				break;
			case 2:
				fileNameOdd = "p5.txt";
				fileNameEven = "p6.txt";
				break;
			case 3:
				fileNameOdd = "p7.txt";
				fileNameEven = "p8.txt";
				break;
			case 4:
				fileNameOdd = "p9.txt";
				fileNameEven = "p10.txt";
				break;
			case 5:
				fileNameOdd = "p11.txt";
				fileNameEven = "p12.txt"; 
				break;
			case 6:
				fileNameOdd = "p13.txt";
				fileNameEven = "p14.txt";
				break;
			case 7:
				fileNameOdd = "p15.txt";
				fileNameEven = "p16.txt";
				break;
			case 8:
				fileNameOdd = "p17.txt";
				fileNameEven = "p18.txt";
				break;
			case 9:
				fileNameOdd = "p19.txt";
				fileNameEven = "p20.txt";
				break;
			case 10:
				fileNameOdd = "p21.txt";
				fileNameEven = "p22.txt";
				break;
			case 11:
				fileNameOdd = "p23.txt";
				fileNameEven = "p24.txt";
				break;
			case 12:
				fileNameOdd = "p25.txt";
				fileNameEven = "p26.txt";
				break;
			case 13:
				fileNameOdd = "p27.txt";
				fileNameEven = "p28.txt";
				break;
			}*/
			
			PrintWriter outputOdd = null;
			PrintWriter outputEven = null;
			try {
				outputOdd = new PrintWriter(fileNameOdd);
				outputEven = new PrintWriter(fileNameEven);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			for (int j = i * subSize; j < (i+1) * subSize; j++) {
				Scanner scan = new Scanner(lines.get(j));
				scan.useDelimiter("\t");
				String a1 = scan.next();
				String b1 = scan.next();
				String a2 = scan.next();
				String b2 = scan.next();
				String a3 = scan.next();
				String b3 = scan.next();
				
				a1 = a1.replaceAll("\"", "");
				a2 = a2.replaceAll("\"", "");
				a3 = a3.replaceAll("\"", "");
				b1 = b1.replaceAll("\"", "");
				b2 = b2.replaceAll("\"", "");
				b3 = b3.replaceAll("\"", "");
				
				if (j % 2 == 0) {
					outputOdd.println(countOdd++ + "_a1\t<h>" + a1 + "</h>");
					outputOdd.println(countOdd++ + "_a2\t" + a1 + "\t" + b1 + "\t<h>" + a2 + "</h>" );
					outputOdd.println(countOdd++ + "_a3\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t<h>" + a3 + "</h>");
					outputEven.println(countEven++ + "_b1\t" + a1 + "\t<h>" + b1 + "</h>");
					outputEven.println(countEven++ + "_b2\t" + a1 + "\t" + b1 + "\t" + a2 + "\t<h>" + b2 + "</h>");
					outputEven.println(countEven++ +  "_b3\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t" + a3 + "\t<h>" + b3 + "</h>");
				} else {
					outputEven.println(countEven++ + "_a1\t<h>" + a1 + "</h>");
					outputEven.println(countEven++ + "_a2\t" + a1 + "\t" + b1 + " \t<h>" + a2 + "</h>" );
					outputEven.println(countEven++ + "_a3\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t<h>" + a3 + "</h>");
					outputOdd.println(countOdd++ + "_b1\t" + a1 + "\t<h>" + b1 + "</h>");
					outputOdd.println(countOdd++ + "_b2\t" + a1 + "\t" + b1 + "\t" + a2 + "\t<h>" + b2 + "</h>");
					outputOdd.println(countOdd++ + "_b3\t" + a1 + "\t" + b1 + "\t" + a2 + "\t" + b2 + "\t" + a3 + "\t<h>" + b3 + "</h>");
				}
				
				scan.close();
			}
			
			outputOdd.close();
			outputEven.close();
		}
	}
}
