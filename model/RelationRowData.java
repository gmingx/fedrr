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
public class RelationRowData {
	String[] fields;

	public RelationRowData(String data) {
		fields = data.split("\t");
	}

	public boolean active() {
		return fields[2].equals("1");
	}

	public String dest() {
		return fields[5];
	}

	public String src() {
		return fields[4];
	}

	
	public String type() {
		return fields[7];
	}

	
	public Date getEffectiveDate() {
		try {
			return new SimpleDateFormat("yyyyMMdd").parse(fields[1]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void print() {
		for(int i = 0; i < this.fields.length; i++)
			System.out.print(fields[i] + "*\t");
		
		System.out.println();
		
	}
	
	public static void main(String[] args) {
		try {
			Date d= new SimpleDateFormat("yyyyMMdd").parse("19741029");
			System.out.println(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
