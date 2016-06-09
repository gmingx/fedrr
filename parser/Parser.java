/**
 * 
 */
package fedrr.parser;

import fedrr.model.Ontology;

/**
 * @author Gaungming Xing
 * @date May 22, 2016
 * Defines the interface for so RRDetector can adapt to different data sources.
 */
public interface Parser {
	/**
	 * API for parsing an ontology specified by a list of specification files.
	 * @param specs
	 * @return the concept map and set of relations wrapped in an Ontology object
	 */
	public Ontology parse(String ... specs);
}
