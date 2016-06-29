/**
 * 
 */
package fedrr.model;

/**
 * @author Gaungming Xing
 * @date Jan 15, 2015
 */
public class UmlsRelationRowData {
	String[] fields;

	public UmlsRelationRowData(String data) {
		fields = data.split("\\|");
	}

	public boolean active() {
		return fields[14].equals("N");
	}

	public String destAui() {
		return fields[5];
	}

	public String srcAui() {
		return fields[1];
	}

	public String destCui() {
		return fields[4];
	}

	public String srcCui() {
		return fields[0];
	}

	public String subType() {
		return fields[7];
	}

	public String type() {
		return fields[3];
	}

	public String dir() {
		return fields[13];
	}

	public String dataSrc() {
		return fields[10];
	}
}
