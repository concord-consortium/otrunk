package org.concord.otrunk.test;

import java.io.File;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.concord.otrunk.xml.URLUtil;

public class RelativeURLTest extends TestCase
{
	public void test1() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com/main.txt"),
				new java.net.URL("http://hello.com/bye.txt")
			);
		
		assertEquals("bye.txt", result);
	}
	
	public void test2() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://goodbye.com/folder"),
				new java.net.URL("http://hello.com/bye.txt")
			);
		
		assertEquals("http://hello.com/bye.txt", result);
	}
	
	public void test3() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com/main.txt"),
				new java.net.URL("http://hello.com/folder/bye.txt")
			);
		
		assertEquals("folder/bye.txt", result);
	}
	
	public void test4() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com/folder1/main.txt"),
				new java.net.URL("http://hello.com/folder1/bye.txt")
			);
		
		assertEquals("bye.txt", result);
	}

	public void test5() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com/"),
				new java.net.URL("http://hello.com/")
			);
		
		// I don't know if this is generally correct for html, but the "" works for
		// new URL(new URL("http://hello.com/", "")
		assertEquals("", result);
	}

	public void test6() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com"),
				new java.net.URL("http://hello.com")
			);
		
		assertEquals("", result);
	}

	/** 
	 * This one is questionable.  Should this return 
	 * "/bye.txt" or "../bye.txt" both are valid.
	 * The /bye.txt is better in some cases.  the "../bye.txt" is better
	 * in others.
	 * 
	 * @throws MalformedURLException
	 */
	public void test7() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com/folder1/main.txt"),
				new java.net.URL("http://hello.com/bye.txt")
			);
		
		assertEquals("/bye.txt", result);
	}
	
	public void test8() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com/folder1/folder2/main.txt"),
				new java.net.URL("http://hello.com/folder1/bye.txt")
			);
		
		assertEquals("../bye.txt", result);
	}
	
	public void test9() throws MalformedURLException
	{
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com/folder1/folder2/main.txt"),
				new java.net.URL("http://hello.com/folder1/folder2/bye.txt")
			);
		
		assertEquals("bye.txt", result);
	}
	
	public void test10() throws MalformedURLException
	{
		File contextFile = new File("/home/test/context.txt");
		File relativeFile = new File("/home/test/relative.txt");
		
		String result = URLUtil.getRelativeURL(
				contextFile.toURL(), relativeFile.toURL());
		
		assertEquals("relative.txt", result);
	}
	
	public void test11() throws MalformedURLException
	{
		File relativeFile = new File("/home/test/relative.txt");
		
		String result = URLUtil.getRelativeURL(
				new java.net.URL("http://hello.com/home/test/context.txt"), 
				relativeFile.toURL());
		
		assertEquals(relativeFile.toURL().toExternalForm(), result);
	}

	public void test12() throws MalformedURLException
	{
		File contextFile = new File("/context.txt");
		File relativeFile = new File("/home/test/relative.txt");
		
		String result = URLUtil.getRelativeURL(
				contextFile.toURL(), relativeFile.toURL());
		
		assertEquals("home/test/relative.txt", result);
	}
	
}
