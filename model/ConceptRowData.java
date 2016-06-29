/**
 * 
 */
package fedrr.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gaungming Xing
 * @date Jan 15, 2015
 */
public class ConceptRowData {
	static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
	String[] fields;

	public ConceptRowData(String data) {
		fields = data.split("\t");
	}

	public boolean active() {
		return fields[2].equals("1");
	}

	public String getLabel() {
		return fields[7];
	}

	public String conceptId() {
		return fields[4];
	}

	public Date getEffectiveDate() {
		try {
			return df.parse(fields[1]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
