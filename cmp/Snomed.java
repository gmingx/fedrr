/**
 * 
 */
package fedrr.cmp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import fedrr.model.SnomedRelationRowData;

/**
 * @author Gaungming Xing
 * @date Jun 13, 2016
 */
public class Snomed {
	static class UMLSCon {
		String code;
		String suppress;
	}

	Map<Relation, Relation> all;
	Map<Relation, Relation> inactive;

	Map<String, UMLSCon> auiMap;
	Map<String, UMLSCon> cuiMap;

	int auiCollision = 0;
	int cuiCollision = 0;
	int auiFound = 0;
	int cuiFound = 0;
	int activeCnt = 0;
	int notSupActiveCnt = 0;
	int supActiveCnt = 0;
	int notSupInCnt = 0;
	int supInCnt = 0;

	int inCCnt = 0;
	int outsideCCnt = 0;

	int intACnt = 0;
	int outsideACnt = 0;

	int aMissed = 0;
	int cMissed = 0;
	private int activeAUIRemoved = 0;
	private int activatedAUI = 0;
	private int activeCUIRemoved = 0;
	private int activatedCUI = 0;
	
	private int supressible = 0;
	private int insuppressible = 0;

	public Snomed() {
		super();
		this.all = new LinkedHashMap<Relation, Relation>();
		this.inactive = new LinkedHashMap<Relation, Relation>();

		auiMap = new LinkedHashMap<String, UMLSCon>();
		cuiMap = new LinkedHashMap<String, UMLSCon>();
	}

	public void add(Relation r) {

	}

	public void add(String line) {
		SnomedRelationRowData rrl = new SnomedRelationRowData(line);
		
		if (Long.parseLong(rrl.type()) != 116680003)
			return;
		
		Relation r = new Relation();
		r.src = rrl.dest();
		r.dest = rrl.src();
		r.effective = rrl.getEffectiveDate();
		r.active = rrl.active();

		if (r.active)
			all.put(r, r);
		else
			inactive.put(r, r);
	}

	public void load(String fileName) {
		Scanner input = null;
		String line = null;
		try {
			input = new Scanner(new FileInputStream(fileName));

			int lineCnt = 0;
			input.nextLine();
			while (input.hasNextLine()) {
				line = input.nextLine();
				add(line);
				lineCnt++;
			}

			input.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cmp(String conFile, String relFile) {
		Scanner input = null;
		String[] fields = null;
		String line = null;
		int progressCnt = 0;
		int ex = 0;
		try {
			input = new Scanner(new FileInputStream(conFile));

			while (input.hasNextLine()) {
				progressCnt++;
				if (progressCnt % 10000 == 0)
					System.out.println("##" + progressCnt);
				try {
					line = input.nextLine();
					
					fields = line.split("\\|");

					if (!fields[11].equals("SNOMEDCT_US"))
						continue;
					UMLSCon con = new UMLSCon();
					con.code = fields[13];
					con.suppress = fields[16];

					if (this.auiMap.containsKey(fields[7]))
						this.auiCollision++;
					else
						this.auiMap.put(fields[7], con);

					if (this.cuiMap.containsKey(fields[0]))
						this.cuiCollision++;
					else
						this.cuiMap.put(fields[0], con);
				} catch (Exception e) {
					ex++;
					System.out.println(line);
					e.printStackTrace();
					continue;
				}
			}
			System.out.println("Done loading " + ex + "\t" + progressCnt);
			System.out.println(auiMap.size());
			System.out.println(cuiMap.size());
			input.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("2error");
			e.printStackTrace();
			System.out.println(line);

		}

		progressCnt = 0;
		// process the relation now
		try {
			input = new Scanner(new FileInputStream(relFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Relation r = new Relation();
		while (input.hasNextLine()) {
			progressCnt++;
			if (progressCnt % 1000000 == 0)
				System.out.println("#######" + progressCnt);
			line = input.nextLine();
			fields = line.split("\\|");
			if (!fields[10].equals("SNOMEDCT_US") || !fields[3].equals("CHD"))
				continue;

			
			if(fields[14].equals("N")) {
				this.insuppressible++;
			} else{
				this.supressible++;
			}
			
			UMLSCon src = auiMap.get(fields[1]);
			UMLSCon dest = auiMap.get(fields[5]);
			if (src != null && dest != null) {
				this.auiFound++;
				r.src = src.code;
				r.dest = dest.code;

				Relation ar = all.get(r);
				
				
				
				Relation ir = inactive.get(r);
				if (ar != null && ir != null) {
					if (ar.effective.before(ir.effective)) {
						activeAUIRemoved++;
					} else {
						activatedAUI++;
					}
				} else if (ar != null) {
					this.intACnt++;
				} else if (ir != null) {
					this.outsideACnt++;
				}

			} else
				this.aMissed++;

			src = cuiMap.get(fields[0]);
			dest = cuiMap.get(fields[4]);
			if (src != null && dest != null) {
				this.cuiFound++;
				r.src = src.code;
				r.dest = dest.code;

				Relation ar = all.get(r);
				Relation ir = inactive.get(r);
				if (ar != null && ir != null) {
					if (ar.effective.before(ir.effective)) {
						activeCUIRemoved++;
					} else {
						activatedCUI++;
					}
				} else if (ar != null) {
					this.inCCnt++;
				} else if (ir != null) {
					this.outsideCCnt++;
				}

			} else
				this.cMissed++;

		}

		input.close();

		System.out.println(this);

	}





	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Snomed [auiCollision=");
		builder.append(auiCollision);
		builder.append(" ,  cuiCollision=");
		builder.append(cuiCollision);
		builder.append(" ,  auiFound=");
		builder.append(auiFound);
		builder.append(" ,  cuiFound=");
		builder.append(cuiFound);
		builder.append(" ,  activeCnt=");
		builder.append(activeCnt);
		builder.append(" ,  notSupActiveCnt=");
		builder.append(notSupActiveCnt);
		builder.append(" ,  supActiveCnt=");
		builder.append(supActiveCnt);
		builder.append(" ,  notSupInCnt=");
		builder.append(notSupInCnt);
		builder.append(" ,  supInCnt=");
		builder.append(supInCnt);
		builder.append(" ,  inCCnt=");
		builder.append(inCCnt);
		builder.append(" ,  outsideCCnt=");
		builder.append(outsideCCnt);
		builder.append(" ,  intACnt=");
		builder.append(intACnt);
		builder.append(" ,  outsideACnt=");
		builder.append(outsideACnt);
		builder.append(" ,  aMissed=");
		builder.append(aMissed);
		builder.append(" ,  cMissed=");
		builder.append(cMissed);
		builder.append(" ,  activeAUIRemoved=");
		builder.append(activeAUIRemoved);
		builder.append(" ,  activatedAUI=");
		builder.append(activatedAUI);
		builder.append(" ,  activeCUIRemoved=");
		builder.append(activeCUIRemoved);
		builder.append(" ,  activatedCUI=");
		builder.append(activatedCUI);
		builder.append(" ,  supressible=");
		builder.append(supressible);
		builder.append(" ,  insuppressible=");
		builder.append(insuppressible);
		builder.append("]");
		return builder.toString();
	}

	public static void main(String[] args) {
		Snomed sno = new Snomed();
		sno.load("e:\\snomed\\rf2\\Full\\Terminology\\sct2_Relationship_Full_US1000124_20150301.txt");

		sno.cmp("D:\\UMLS\\data\\MRCONSO.RRF", "D:\\UMLS\\data\\MRREL.RRF");
	}

}
