package org.concord.otrunk.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.zip.GZIPInputStream;

public class StreamUtil
{

	public static void printFromStream(String label, InputStream stream)
	{
		printFromStream(label, stream, null);
	}

	public static void printFromStream(String label, InputStream stream, String encoding)
	{
		String message;
		try {
			message = StreamUtil.getStringFromStream(stream, encoding);
		} catch (IOException e) {
			message = "Exception getting stream contents: " + e;
		}
		if (message != null) {
			System.err.println("===== " + label + " =====");
			System.err.println(message);
			System.err.println("=========================");
		}
	}

	public static String getStringFromStream(InputStream stream, String encoding)
		throws IOException
    {		
		if (stream == null){
			return null;
		}
	
		StringWriter sWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(sWriter);
		writeFromStream(printWriter, stream, encoding);
		sWriter.flush();
		return sWriter.toString();
    }

	public static void writeFromStream(PrintWriter writer, InputStream stream, String encoding)
	throws IOException
	{
		if (stream == null){
			return;
		}

		if (encoding != null && encoding.toLowerCase().equals("gzip")) {
			stream = new GZIPInputStream(stream);
		}

		InputStreamReader _reader = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(_reader);
		while(true){
			String line = reader.readLine();
			if(line == null){
				break;
			}
			writer.println(line);			
		}
	}
}
