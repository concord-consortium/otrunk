package org.concord.otrunk.applet;

import java.applet.Applet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JApplet;
import javax.swing.JButton;

public class AppletTester extends JApplet
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	public void start()
	{
	    super.start();

	    JButton listApplets = new JButton("List Applets");
	    getContentPane().add(listApplets);
	    listApplets.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		Enumeration<Applet> applets = getAppletContext().getApplets();
	    		while(applets.hasMoreElements()){
	    			Applet a = applets.nextElement();
	    			System.out.println("  " +  a.getParameter("name"));
	    		}
	    	}
	    });
	}
}
