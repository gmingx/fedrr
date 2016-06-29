/**
 * 
 */
package fedrr.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import fedrr.model.Concept;
import fedrr.model.ConceptRef;
import fedrr.model.Ontology;
import fedrr.model.UmlsRelationRowData;
import fedrr.parser.Parser;

/**
 * @author Gaungming Xing
 * @date Apr 22, 2015
 */
public class UmlsUsingAUIParser implements Parser{

	Map<String, Concept> conceptMap;

	public UmlsUsingAUIParser() {
		this.conceptMap = new LinkedHashMap<String, Concept>();

	}
	
	@Override
	public Ontology parse(String... specs) {
		String conceptFileName = specs[0], relationFileName = specs[1],
				dataSrc = specs[2];
		this.loadConcepts(conceptFileName, dataSrc);

		this.processRelations(relationFileName, dataSrc);
		return new Ontology(this.conceptMap);
	}

	

	public void loadConcepts(String conceptFileName, String dataSrc) {
		Scanner input = null;
		try {
			input = new Scanner(new FileInputStream(conceptFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (input.hasNextLine()) {
			String line = input.nextLine();
			// System.out.println(line);
			String[] lineData = line.split("\\|");

			if(!lineData[11].equals(dataSrc))
				continue;
			Concept concept = conceptMap.get(lineData[7]);
			if (!lineData[16].equals("N"))
				continue;

			if (concept == null) {
				concept = new Concept();
				conceptMap.put(lineData[7], concept);
				concept.setId(lineData[7]);
				concept.setLabel(lineData[13]);
			}

		}

		System.out.println("loading done with " + conceptMap.size()
				+ " entries for " + conceptFileName);
	}

	public void processRelations(String relationFileName, String dataSrc) {
		Scanner input = null;

		try {
			input = new Scanner(new FileInputStream(relationFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// skip the first line
		System.out.println(input.nextLine());
		int cnt = 0;
		while (input.hasNextLine()) {
			String line = input.nextLine();
			UmlsRelationRowData data = new UmlsRelationRowData(line);
			
			if(!data.dataSrc().equals(dataSrc))
				continue;

			
			if (!data.active())
				continue;

			String type = data.type();
			String subType = data.subType();
			
			if (!(type.equals("CHD") && subType.equals("isa")))
				continue;
			
			Concept src = conceptMap.get(data.srcAui());
			Concept dest = conceptMap.get(data.destAui());
			

			if (src == null || dest == null)
				continue;

			src.addToRef(new ConceptRef(dest, type));
			dest.addFromRef(new ConceptRef(src, type));
			cnt++;
		}
		System.out.println("done with " + cnt + " entries for "
				+ relationFileName);
	}
}
