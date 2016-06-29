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
	
	
	public Ontology(Map<String, Concept> conceptMap) {
		super();
		this.conceptMap = conceptMap;
	}
	public Map<String, Concept> getConceptMap() {
		return conceptMap;
	}
	public void setConceptMap(Map<String, Concept> conceptMap) {
		this.conceptMap = conceptMap;
	}
	
	
}
