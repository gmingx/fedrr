package fedrr.process;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import fedrr.model.Concept;
import fedrr.model.ConceptOrderedPair;
import fedrr.model.ConceptRef;
import fedrr.model.Ontology;
import fedrr.parser.Parser;
import fedrr.parser.impl.UmlsUsingAUIParser;

/**
 * @author Gaungming Xing and Licong Cui
 * @date Jan 15, 2015
 * 
 *       RRDetector implements the algorithms in
 */

public class RRDetector {

	private Map<String, Concept> conceptMap;
	public List<ConceptOrderedPair> redundantPairs = new ArrayList<ConceptOrderedPair>();

	
	public RRDetector(Ontology ontology) {
		this.conceptMap = ontology.getConceptMap();
	}

	public int numOfUniqueConcepts() {
		LinkedHashSet<String> uniqueConcepts = new LinkedHashSet<String>();
		Iterator<Entry<String, Concept>> it = this.conceptMap.entrySet()
				.iterator();
		
		while(it.hasNext()) {
			Concept con = it.next().getValue();
			
			if(con.getToRefs() == null || con.getToRefs().size() == 0)
				continue;
			
			uniqueConcepts.add(con.getId());
			for(ConceptRef ref : con.getToRefs()) {
				uniqueConcepts.add(ref.getConcept().getId());
			}
		}
		return uniqueConcepts.size();
	}
	
	public List<String> detect(PrintStream writer) throws Exception {

		List<String> result = new ArrayList<String>();

		long topoSortedCnt = 0;
		int cnt = 0;
		Concept[] concepts = new Concept[this.conceptMap.size()];

		Concept[] sorted = new Concept[this.conceptMap.size()];

		Iterator<Entry<String, Concept>> it = this.conceptMap.entrySet()
				.iterator();

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
					if (!conRef.getConcept().visited) {
						conQueue.add(conRef.getConcept());
						conRef.getConcept().visited = true;
					}
				}
			}

		}

		while (topoSortedCnt < conceptMap.size()) {

			System.out.println(topoSortedCnt);
			for (int i = 0; i < concepts.length; i++) {
				if (!concepts[i].visited) {
					concepts[i].ancestor = new LinkedHashSet<Concept>();

					conQueue.add(concepts[i]);
					concepts[i].visited = true;

					break;
				}
			}

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
						if (!conRef.getConcept().visited) {
							conQueue.add(conRef.getConcept());
							conRef.getConcept().visited = true;
						}
					}
				}

			}

		}

		System.out.println("topo count " + topoSortedCnt);
		System.out.println("total " + conceptMap.size());

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

		for (Map.Entry<String, Concept> entry : this.conceptMap.entrySet()) {
			writer.println(entry.getKey() + "\t" + entry.getValue().getLabel());
		}
	}

	public void countTCPair() {
		int cnt = 0;
		for (Map.Entry<String, Concept> entry : this.conceptMap.entrySet()) {
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

	
	/**
	 * Get second-most shorted path for redundant concept pairs
	 */
	public void getSecondShortedPath(PrintStream writer) throws Exception{
		Map<Integer, Integer> statsMap = new HashMap<Integer, Integer>();
		
		//Collections.sort(this.redundantPairs);
		Collections.sort(this.redundantPairs, new Comparator<ConceptOrderedPair>(){

			@Override
			public int compare(ConceptOrderedPair a, ConceptOrderedPair b) {
				if(a.b.getLabel().equals(b.b.getLabel()))
					return a.a.getLabel().compareTo(b.a.getLabel());
				else
					return a.b.getLabel().compareTo(b.b.getLabel());
			}});
		for (ConceptOrderedPair cp : this.redundantPairs){
			Concept src = cp.a;
			Concept dst = cp.b;
			ConceptRef conRef = new ConceptRef(dst, "is_a");
			src.removeToRef(conRef);
			ArrayList<Concept> bfs = BreathFirstSearch.getShortedPath(src, dst);
			if (bfs == null)
				continue;
			int bfsCnt = bfs.size() - 1;
			if (statsMap.containsKey(bfsCnt)){
				statsMap.put(bfsCnt, statsMap.get(bfsCnt) + 1);
			}else{
				statsMap.put(bfsCnt, 1);
			}
			writer.print(dst.getLabel() + "," + src.getLabel() + "\t");
			writer.print(this.arrayListToString(bfs));
			writer.print("\n");
		}
		
		System.out.println("Statistics by length: " + statsMap);
	}
	private String arrayListToString(ArrayList<Concept> cList) {
		StringBuilder builder = new StringBuilder();
	    for (Concept c : cList){
	    	builder.append(c.getLabel());
	    	builder.append(",");
	    }
	    builder.setLength(builder.length() - 1);
		return builder.toString();
	}
	
	
	
	public static void main(String[] args) throws Exception {
//		 Parser parser = new GoParser();
//		
//		 RRDetector rrd = new
//		 RRDetector(parser.parse("D:/GO/gene_ontology_edit.obo.2015-06-01"));
//		 rrd.redundancy();
//		 rrd.getSecondShortedPath(System.out);
		
//		 Parser parser = new SnomedParser();	
//		 new
//		 RRDetector(parser.parse("E:\\snomed\\rf2\\Snapshot\\Terminology\\sct2_Description_Snapshot-en_US1000124_20150301.txt",
//		 "E:\\snomed\\rf2\\Snapshot\\Terminology\\sct2_Relationship_Snapshot_US1000124_20150301.txt")).redundancy();
//		
		
//		 RRDetector rrd = new
//		 RRDetector(parser.parse("E:/snomed/SnomedCT_RF2Release_US1000124_20160301/SnomedCT_RF2Release_US1000124_20160301/Snapshot/Terminology/sct2_Description_Snapshot-en_US1000124_20160301.txt",
//					"E:/snomed/SnomedCT_RF2Release_US1000124_20160301/SnomedCT_RF2Release_US1000124_20160301/Snapshot/Terminology/sct2_Relationship_Snapshot_US1000124_20160301.txt"));
//		 
//		 rrd.redundancy();
//		 
//		
//		 rrd.getSecondShortedPath(System.out);

		Parser parser = new UmlsUsingAUIParser();

		RRDetector rrd = new RRDetector(parser.parse("D:\\UMLS\\data\\MRCONSO.RRF",
				"D:\\UMLS\\data\\MRREL.RRF", "GO"));
		System.out.println("unique concepts involved: " + rrd.numOfUniqueConcepts());
		rrd.redundancy();
		rrd.getSecondShortedPath(System.out);
		rrd.countTCPair();
	}

}
