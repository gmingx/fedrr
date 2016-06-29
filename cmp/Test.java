/**
 * 
 */
package fedrr.cmp;

import java.io.FileInputStream;
import java.util.Scanner;

import fedrr.cmp.Snomed.UMLSCon;

/**
 * @author Gaungming Xing
 * @date Jun 14, 2016
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String conFile = "D:\\UMLS\\data\\MRCONSO.RRF";
		Scanner input = null;
		try {
			input = new Scanner(new FileInputStream(conFile));
			int progressCnt = 0;
			while (input.hasNextLine()) {
				
				
				if (progressCnt == 100)
					return;

				String line = input.nextLine();

				String[] fields = line.split("\\|");

				if (!fields[11].equals("SNOMEDCT_US"))
					continue;
				
				progressCnt++;
				System.out.println(fields[13]);

			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}
