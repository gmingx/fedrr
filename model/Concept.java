/**
 * 
 */
package fedrr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Gaungming Xing and Licong Cui
 * @date Jan 15, 2015
 * Defines a concept in an ontology.
 * 
 * Each concept has an id from the ontology and definition, and label for human verification.
 * 
 * The hierarchical relations in an ontology are represented by lists indicating the 
 * parent child relation. 
 */
public class Concept {
	public static int relationInActivated = 0;
	private String id;
	public String label;

	private List<ConceptRef> toRefs;
	private List<ConceptRef> fromRefs;

	
	// the following are used in the algorithm
	// make them public for easy access
	public Set<Concept> ancestor;

	public Set<Concept> directAncestor;

	public boolean visited = false;
	public int toProcessed = 0;
	
	
	/**
	 * to deal with relation cancellations
	 */
	Date effectiveDate;
	boolean active;



	public void addToRef(ConceptRef ref) {
		if (toRefs == null)
			toRefs = new ArrayList<ConceptRef>();
		
		 if(ref.isActive())
			toRefs.add(ref);

//		int index;
//		if(ref.getEffectiveDate() != null && (index = toRefs.indexOf(ref)) >= 0) {
//			ConceptRef old = toRefs.get(index);
//			if(old.getEffectiveDate().before(ref.getEffectiveDate())) {
//				if(ref.isActive()) {
//					toRefs.remove(index);
//					toRefs.add(ref);
//				} else {
//					relationInActivated++;
//					toRefs.remove(index);
//				}
//			}
//		} else {
//			// make sure that we would add if the effective date has been set 
//			// and it is active
//			if(ref.getEffectiveDate() == null || ref.isActive())
//				toRefs.add(ref);
//		}
			
	}

	public void addFromRef(ConceptRef ref) {
		if (fromRefs == null)
			fromRefs = new ArrayList<ConceptRef>();

		 if(ref.isActive())
				fromRefs.add(ref);
		 
		 
//		int index;
//		if(ref.getEffectiveDate() != null && (index = fromRefs.indexOf(ref)) >= 0) {
//			ConceptRef old = fromRefs.get(index);
//			if(old.getEffectiveDate().before(ref.getEffectiveDate())) {
//				if(ref.isActive()) {
//					fromRefs.remove(index);
//					fromRefs.add(ref);
//				} else {
//					fromRefs.remove(index);
//				}
//			}
//		} else {
//			// make sure that we would add if the effective date has been set 
//			// and it is active
//			if(ref.getEffectiveDate() == null || ref.isActive())
//				fromRefs.add(ref);
//		}	
	}

	
	public void removeToRef(ConceptRef ref){
		if (toRefs != null)
			toRefs.remove(ref);
	}
	
	public void removeFromRef(ConceptRef ref){
		if (fromRefs != null)
			fromRefs.remove(ref);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<ConceptRef> getToRefs() {
		return toRefs;
	}

	public List<ConceptRef> getFromRefs() {
		return fromRefs;
	}
	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Concept [id:");
		builder.append(id);
		builder.append(" \n toProcessed:");
		builder.append(toProcessed);
		builder.append(" \n toRefs:");
		builder.append(toRefs);
		builder.append(" \n fromRefs:");
		builder.append(fromRefs);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concept other = (Concept) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
