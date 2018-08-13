import java.util.Scanner;

public class DecimalBinaryTest {
	
	public static void main(String [] args) {
		DecimalBinaryTest dbt = new DecimalBinaryTest();
		Scanner in = new Scanner(System.in);
		int n = 223;
		int q = 20;
		int p = 80;
		String nn = dbt.decToBin(n);
		String qq = dbt.decToBin(q);
		String pq = dbt.decToBin(p);
		System.out.println(n + " -> " + nn);
		System.out.println(q + " -> " + qq);
		System.out.println(p + " -> " + pq);
		System.out.println("\n--------------------\n");
		int an = dbt.binToDec(nn);
		int aq = dbt.binToDec(qq);
		int ap = dbt.binToDec(pq);
		System.out.println(nn + " -> " + an);
		System.out.println(qq + " -> " + aq);
		System.out.println(pq + " -> " + ap);
		System.out.println(qq + pq + " -> " + dbt.binToDec(qq+pq));
	}
	
	public String decToBin(int n) {
		String s = "";
		for (int i = 7; i >= 0; i--) {
			if (n - Math.pow(2, i) >= 0) {
				//System.out.println(Math.pow(2, i));
				n -= Math.pow(2,  i);
				s += "1";
			} else {
				s += "0";
			}
		}
		return s;
	}
	
	public int binToDec(String s) {
		int mcount = s.length() - 1;
		double max = Math.pow(2, mcount);
		int sum = 0;
		String num = "";
		for (int i = s.length() - 1; i >= 0; i--) {
			num += s.charAt(i);
		}
		for (int i = 0; i < num.length(); i++) {
			int pow = (int) Math.pow(2, i);
			switch (num.charAt(i)) {
			case '0': break;
			case '1': sum += pow;
			}
		}
		return sum;
	}

}
