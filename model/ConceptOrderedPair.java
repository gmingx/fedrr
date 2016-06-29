package fedrr.model;


public class ConceptOrderedPair implements Comparable{
	
	public Concept a;
	public Concept b;
	
	public ConceptOrderedPair(Concept a, Concept b) {
		this.a = a;
		this.b = b;
	}
	
	
	@Override
	public int compareTo(Object arg0) {
		ConceptOrderedPair another = (ConceptOrderedPair) arg0;
		
		if(a.getId().equals(another.a.getId())) {
			return (b.getId().compareTo(another.b.getId()));
		}
		else
			return (a.getId().compareTo(another.a.getId()));
		
	}
	
	

}
