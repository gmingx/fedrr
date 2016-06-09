package fedrr.process;


import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import fedrr.model.Concept;
import fedrr.model.ConceptOrderedPair;
import fedrr.model.ConceptRef;
import fedrr.model.Ontology;
import fedrr.parser.Parser;
import fedrr.parser.impl.GoParser;


/**
 * @author Gaungming Xing and Licong Cui
 * @date Jan 15, 2015
 * 
 * RRDetector implements the algorithms in 
 */

public class RRDetector {
	
	private Map<String, Concept> conceptMap;
	Set<String>  relations;
	public List<ConceptOrderedPair> redundantPairs = new ArrayList<ConceptOrderedPair>();

	
	
	public RRDetector(Ontology ontology) {
		this.conceptMap = ontology.getConceptMap();
		this.relations = ontology.getAllRelations();
	}
	
	public List<String> detect(PrintStream writer) throws Exception {
		
		List<String> result = new ArrayList<String>();
		
		long topoSortedCnt = 0;
		int cnt = 0;
		Concept[] concepts = new Concept[this.conceptMap.size()];

		Concept[] sorted = new Concept[this.conceptMap.size()];

		Iterator<Entry<String, Concept>> it = this.conceptMap.entrySet().iterator();

		while (it.hasNext()) {
			Concept con = it.next().getValue();
			con.directAncestor = new LinkedHashSet<Concept>();
			concepts[cnt++] = con;
		}

		Queue<Concept> conQueue = new LinkedList<Concept>();

		for (int i = 0; i < concepts.length; i++) {
			concepts[i].ancestor = new LinkedHashSet<Concept>();
			if (concepts[i].getFromRefs() == null
					|| concepts[i].getFromRefs().size() == 0) {
				conQueue.add(concepts[i]);
				concepts[i].visited = true;
			}
		}

		long startTime = System.currentTimeMillis();

		while (!conQueue.isEmpty()) {
			Concept con = conQueue.remove();

			sorted[(int) topoSortedCnt] = con;
			topoSortedCnt++;

			if (con.getToRefs() == null)
				continue;

			for (ConceptRef conRef : con.getToRefs()) {
				conRef.getConcept().toProcessed++;
				conRef.getConcept().ancestor.addAll(con.ancestor);
				conRef.getConcept().ancestor.addAll(con.directAncestor);
				conRef.getConcept().directAncestor.add(con);

				if (conRef.getConcept().toProcessed == conRef.getConcept()
						.getFromRefs().size()) {
					if (!conRef.getConcept().visited)
						conQueue.add(conRef.getConcept());
				}
			}

		}

		System.out.println("topo count " + topoSortedCnt);
		System.out.println("total " + conceptMap.size());
		
		
		if(topoSortedCnt < conceptMap.size()) {
			
			for (int i = 0; i < concepts.length; i++) {
				if(!concepts[i].visited ) {
				concepts[i].ancestor = new LinkedHashSet<Concept>();
				
					conQueue.add(concepts[i]);
					concepts[i].visited = true;
				
				break;
				}
			}
			
			
			while (!conQueue.isEmpty()) {
				Concept con = conQueue.remove();
				System.out.println(con);
				sorted[(int) topoSortedCnt] = con;
				topoSortedCnt++;

				if (con.getToRefs() == null)
					continue;

				for (ConceptRef conRef : con.getToRefs()) {
					conRef.getConcept().toProcessed++;
					conRef.getConcept().ancestor.addAll(con.ancestor);
					conRef.getConcept().ancestor.addAll(con.directAncestor);
					conRef.getConcept().directAncestor.add(con);

					if (conRef.getConcept().toProcessed == conRef.getConcept()
							.getFromRefs().size()) {
						if (!conRef.getConcept().visited)
							conQueue.add(conRef.getConcept());
					}
				}

			}
			
			
		}
		
		
		cnt = 0;

		// go through all edges
		// in the topological order of the concepts that are visited
		
		for (int i = 0; i < sorted.length; i++) {
			Concept con = sorted[i];

			Iterator<Concept> directIterator = con.directAncestor.iterator();
			while (directIterator.hasNext()) {
				Concept direct = directIterator.next();

				if (con.ancestor.contains(direct)) {
					cnt++;
					String res = direct.getId() + "\t" + con.getId();
					redundantPairs.add(new ConceptOrderedPair(direct, con));
					//System.out.println("[" + direct.getId() + ", " + con.getId() + "]");
					//writer.println(res);
				
					
					result.add(res);
				}
			}

		}
	
		long endTime = System.currentTimeMillis();

		System.out
				.println("time for topo sorting and finding short cut in milliseconds "
						+ (endTime - startTime));
		return result;
	}
	

	
	public void printConceptLabel(PrintStream writer) throws Exception {
		
		for (Map.Entry<String, Concept> entry : this.conceptMap.entrySet()){
			writer.println(entry.getKey() + "\t" + entry.getValue().getLabel());
		}
	}
	
	public void countTCPair(){
		int cnt = 0;
		for (Map.Entry<String, Concept> entry : this.conceptMap.entrySet()){
			Concept c = entry.getValue();
			cnt = cnt + c.ancestor.size();
			cnt = cnt + c.directAncestor.size();
		}
		System.out.println("Number of TC pairs: " + cnt);
	}
	
	public List<String> redundancy() {
		long startTime = System.currentTimeMillis();
		

		List<String> res = null;
		try {
		
			res = detect(System.out);
			countTCPair();
			// printConceptLabel(System.out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Processing time in milliseconds "
				+ (System.currentTimeMillis() - startTime));
		System.out.println("Total redundant is-a relaitons cnt " + res.size());
		
		return res;
	}
	

	
	public static void main(String[] args) throws Exception {
		Parser parser = new GoParser();
		
		new RRDetector(parser.parse("e:/validation-go/gofiles/gene_ontology_edit.obo.2015-04-01")).redundancy(); 
	
	
//		Parser parser = new SnomedParser();
//		
//		new RRDetector(parser.parse("e:\\snomed\\sct2_Description_Full-en_US1000124_20160301.txt",
//				"E:\\snomed\\sct2_Relationship_Full_US1000124_20160301.txt")).redundancy(); 
//
//	
	}



}
