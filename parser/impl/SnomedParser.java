/**
 * 
 */
package fedrr.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import fedrr.model.Concept;
import fedrr.model.ConceptRef;
import fedrr.model.Ontology;
import fedrr.model.RelationRowData;
import fedrr.parser.Parser;

/**
 * @author Gaungming Xing
 * @date May 22, 2016
 */
public class SnomedParser implements Parser {

	Map<String, Concept> conceptMap;

	Set<String> allRelations;
	Set<String> allSources;

	private int rel = 116680003;

	/**
	 * 
	 */
	@Override
	public Ontology parse(String... specs) {
		String conceptFileName = specs[0], relationFileName = specs[1];
		this.loadConcepts(conceptFileName);

		this.processRelations(relationFileName);
		return new Ontology(this.conceptMap, this.allRelations);
	}

	public void loadConcepts(String conceptFileName) {
		this.conceptMap = new LinkedHashMap<String, Concept>();
		Scanner input = null;
		try {
			input = new Scanner(new FileInputStream(conceptFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// skip the first line
		input.nextLine();

		while (input.hasNextLine()) {
			String line = input.nextLine();
			String[] lineData = line.split("\t");
			if (!lineData[2].equals("1"))
				continue;

			Concept concept = conceptMap.get(String.valueOf(lineData[4]));

			if (concept == null) {
				concept = new Concept();
				conceptMap.put(String.valueOf(lineData[4]), concept);
				concept.setId(String.valueOf(lineData[4]));
				concept.setLabel(lineData[7]);
			}

		}

		System.out.println("loading done with " + conceptMap.size()
				+ " entries for " + conceptFileName);
	}

	public void processRelations(String relationFileName) {
		Scanner input = null;
		String line = null;
		int total = 0;
		int nullCnt = 0;
		RelationRowData data = null;
		try {

			input = new Scanner(new FileInputStream(relationFileName));

			// skip the first line
			System.out.println(input.nextLine());
			int cnt = 0;
			while (input.hasNextLine()) {
				total++;
				line = input.nextLine();
				data = new RelationRowData(line);
				if (Long.parseLong(data.type()) != rel)
					continue;

				boolean status = data.active();
				Date effectiveDate = data.getEffectiveDate();
				
				
				Concept src = conceptMap.get(String.valueOf(data.src()));
				Concept dest = conceptMap.get(String.valueOf(data.dest()));
				if(src == null || dest == null) {
					nullCnt++;
					continue;
				}
				
				long type = Long.parseLong(data.type());

				src.addToRef(new ConceptRef(dest, String.valueOf(type), status, effectiveDate));
				dest.addFromRef(new ConceptRef(src, String.valueOf(type), status, effectiveDate));
			}

			
			
			Iterator<Entry<String, Concept>> it = this.conceptMap.entrySet().iterator();

			while (it.hasNext()) {
				Concept con = it.next().getValue();
				if(con.getToRefs() != null)
					cnt += con.getToRefs().size();
			}
			
			System.out.println("done with " + cnt + " " + rel + " entries for "
					+ relationFileName + "\t" + total + "\t" + nullCnt);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			data.print();
			e.printStackTrace();
		}
	}

}
