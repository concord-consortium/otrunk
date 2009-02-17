/**
 * 
 */
package org.concord.otrunk.net;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class IndentingPrintWriter extends PrintWriter
{
	/**
	 * @param out
	 */
	public IndentingPrintWriter(OutputStream out) {
		super(out);
	}

	/**
	 * @param writer
	 */
	public IndentingPrintWriter(StringWriter writer) {
		super(writer);
	}

	/**
	 * add indentation to the printer
	 * @see java.io.PrintWriter#println(java.lang.String)
	 */
	@Override
	public void println(String x) {
		print("   ");
		super.println(x);
	}		
	
	public void printFirstln(String x) {
		super.println(x);
	}
}