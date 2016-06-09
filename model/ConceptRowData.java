/**
 * 
 */
package fedrr.model;

/**
 * @author Gaungming Xing
 * @date Jan 15, 2015
 */
public class ConceptRowData {
	String[] fields;

	public ConceptRowData(String data) {
		fields = data.split("|");
	}

	public boolean active() {
		return true;
	}

	public String desc() {
		return "";
	}

	public String conceptId() {
		return fields[0];
	}

	public boolean isSynonym() {
		return "900000000000013009".equals(fields[6]);
	}

	public boolean isDefinition() {
		return "900000000000013004".equals(fields[6]);
	}

	public boolean isName() {
		return "900000000000003001".equals(fields[6]);
	}
}
