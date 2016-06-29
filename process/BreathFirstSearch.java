package fedrr.process;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import fedrr.model.Concept;
import fedrr.model.ConceptRef;


public class BreathFirstSearch {

		
	/**
	 * Finds the shortest path between two nodes (src and dst) in a graph.
	 */
	public static Map<Concept, Concept> bfs(Concept src, Concept dst) {		
		// A list that stores the path.
		Map<Concept, Concept> path = new HashMap<Concept, Concept>();
		
		// If the source is the same as destination, I'm done.
		if (src.equals(dst)) {
			path.put(src, src);
			return path;
		}
		
		// A queue to store the visited nodes.
		Queue<Concept> q = new LinkedList<Concept>();
		
		// A queue to store the visited nodes.
		Queue<Concept> visited = new LinkedList<Concept>();
		
		q.add(src);
		while (!q.isEmpty()) {
			Concept from = q.remove();
			visited.add(from);
			
			// Search directly reachable list of from.
			List<ConceptRef> toList = from.getToRefs();
			if (toList == null)
				continue;
			for (ConceptRef toRef : toList) {
				Concept to = toRef.getConcept();
				if (to.equals(dst)){
					path.put(to, from);
					return path;
				}
				if (!visited.contains(to)) {
					visited.add(to);
					path.put(to, from);
					q.add(to);
				}
			}
			
		}
		return null;
	} 
	
	public static ArrayList<Concept> getShortedPath(Concept src, Concept dst) {
		Map<Concept, Concept> path = bfs(src, dst);
		if (path == null)
			return null;
		
		ArrayList<Concept> conList = new ArrayList<Concept>();
		conList.add(dst);
		if (src.equals(dst))
			return conList;
		
		Concept tmp = dst;
		while (path.containsKey(tmp)){
			conList.add(path.get(tmp));
			tmp = path.get(tmp);
			if (tmp.equals(src)){
				break;
			}
		}
		Collections.reverse(conList);
		return conList;
	}
}
