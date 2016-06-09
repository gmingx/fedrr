/**
 * 
 */
package fedrr.model;

import java.util.Date;

/**
 * @author Gaungming Xing
 * @date Jan 19, 2015
 */
public class ConceptRef {
	Concept concept;
	String relation;
	/**
	 * to deal with relation cancellations
	 */
	Date effectiveDate;
	boolean active;

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public ConceptRef(Concept concept, String relation, boolean status, Date effectiveDate) {
		this.concept = concept;
		this.relation = relation;
		this.active = status;
		this.effectiveDate = effectiveDate;
	}
	
	
	public ConceptRef(Concept concept, String relation) {
		this.concept = concept;
		this.relation = relation;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConceptRef [concept:");
		builder.append(" \n id:" + concept.getId());
		// builder.append(" \n name:"+concept.getText());
		builder.append(" \n relation:");
		// builder.append(ConceptProcessor.getInstance().getConcept(relation).getText()
		// + "(" + relation + ")");
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((concept == null) ? 0 : concept.hashCode());
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
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
		ConceptRef other = (ConceptRef) obj;
		if (concept == null) {
			if (other.concept != null)
				return false;
		} else if (!concept.equals(other.concept))
			return false;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		return true;
	}

	
	
}
