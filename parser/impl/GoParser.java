/**
 * 
 */
package fedrr.parser.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import fedrr.model.Concept;
import fedrr.model.ConceptRef;
import fedrr.model.Ontology;
import fedrr.parser.Parser;

/**
 * @author Gaungming Xing
 * @date May 23, 2016
 */
public class GoParser implements Parser {

	Map<String, Concept> conceptMap;

	Set<String> allRelations;
	Set<String> allSources;
	/* (non-Javadoc)
	 * @see fedrr.parser.Parser#parse(java.lang.String[])
	 */
	@Override
	public Ontology parse(String... specs) {
		String conceptFileName = specs[0];
		loadConcept(conceptFileName);
		buildConceptRelation(conceptFileName);
		return new Ontology(conceptMap);
	}
	
	

	public void loadConcept(String conceptFileName) {
		boolean readingTerm = false;
		boolean termEnd = false;
		this.conceptMap = new LinkedHashMap<String, Concept>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(conceptFileName));
		
			  String line;
			    String[] lineArr;
		    	Map<String, String> cm = new HashMap<String, String>();
			    while ((line = br.readLine()) != null){
			    	line = line.trim();
			    	if (line.equals("[Term]")){
			    		readingTerm = true;
			    		termEnd = false;
			    		cm.clear();
			    		continue;
			    	}
			    	if (readingTerm && line.trim().length() == 0){
			    		termEnd = true;
			    	}
			    	if (readingTerm){
				    	lineArr = line.split(":");
				    	String key = lineArr[0].trim();
				    	String value = "";
				    	if (key.equals("id")){
				    		value = line.replace("id:", "").trim();
				    		cm.put("id", value);
				    	}
				    	if (key.equals("name")){
					    	value = lineArr[1].trim();
				    		cm.put("label", value);
				    	}
				    	if (key.equals("is_obsolete") && value.equals("true")){
				    		value = lineArr[1].trim();
				    		cm.put("obsolete", "true");
				    	}
				    	if (termEnd && cm.get("obsolete") == null){
				    		Concept concept = new Concept();
				    		concept.setId(cm.get("id"));
				    		concept.setLabel(cm.get("label"));
				    		if (cm.get("id").startsWith("GO:"))
				    			conceptMap.put(cm.get("id"), concept);
				    	}
			    	}

			    }
			    br.close();	

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
		System.out.println("Loading concept done: "  + conceptMap.size() + " concepts");
	}

	public void buildConceptRelation(String conceptFileName) {
		boolean readingTerm = false;
		boolean termEnd = false;
		this.allRelations = new LinkedHashSet<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(conceptFileName));
		
			int cnt = 0;
		    String line;
		    String[] lineArr;
		    String cid = "";
		    boolean obsolete = false;
		    Set<String> relSet = new HashSet<String>();
		    while ((line = br.readLine()) != null){
		    	line = line.trim();
		    	if (line.equals("[Term]")){
		    		readingTerm = true;
		    		termEnd = false;
		    		obsolete = false;
		    		relSet.clear();
		    		continue;
		    	}
		    	if (readingTerm && line.trim().length() == 0){
		    		termEnd = true;
		    	}
		    	if (readingTerm){
			    	lineArr = line.split(":");
			    	String key = lineArr[0].trim();
			    	String value = "";
			    	if (key.equals("id")){
			    		value = line.replace("id:", "").trim();
			    		cid = value;
			    	}
			    	if (key.equals("is_a")){
			    		value = line.replace("is_a:", "").trim().split("!")[0].trim();
			    		relSet.add(value);
			    	}
			    	if (key.equals("is_obsolete") && value.equals("true")){
			    		obsolete = true;
			    	}
			    	if (termEnd && !obsolete && cid.startsWith("GO:")){
						Concept src = conceptMap.get(cid);
						for (String pid : relSet){
							Concept dest = conceptMap.get(pid);
							if (src == null || dest == null)
								continue;
							src.addToRef(new ConceptRef(dest, "is_a"));
							dest.addFromRef(new ConceptRef(src, "is_a"));
							this.allRelations.add(src.getId() + "\t" + dest.getId());
							cnt++;
						}
			    	}
		    	}
		    }
			br.close();
			System.out.println("Loading relations done: " + cnt + " is_a relations");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
