/**
 * 
 */
package fedrr.model;

import java.util.Map;
import java.util.Set;

/**
 * @author Gaungming Xing
 * @date May 22, 2016
 */
public class Ontology {
	private Map<String, Concept> conceptMap;
	private Set<String> allRelations;
	
	
	public Ontology(Map<String, Concept> conceptMap, Set<String> allRelations) {
		super();
		this.conceptMap = conceptMap;
		this.allRelations = allRelations;
	}
	public Map<String, Concept> getConceptMap() {
		return conceptMap;
	}
	public void setConceptMap(Map<String, Concept> conceptMap) {
		this.conceptMap = conceptMap;
	}
	public Set<String> getAllRelations() {
		return allRelations;
	}
	public void setAllRelations(Set<String> allRelations) {
		this.allRelations = allRelations;
	}
	
	
	
}
