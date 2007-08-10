package org.concord.otrunk.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.AbstractOTJComponentView;

public class OTMainClassLauncherView extends AbstractOTJComponentView
{
	public static boolean alreadyLaunched = false;
	
	OTMainClassLauncher launcher;
	
	public JComponent getComponent(OTObject otObject, boolean editable)
	{
		/**
		 * This is a hack because getComponent is getting called twice in some cases.
		 */
		if(alreadyLaunched){
			return null;
		}
		
		alreadyLaunched = true;		
		
		launcher = (OTMainClassLauncher) otObject;
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				runMainMethod();
			}
		});
		
		// Return a label that hides the main frame
		return new JLabel("Closing..."){
			/**
             * 
             */
            private static final long serialVersionUID = 1L;

			public void addNotify()
			{
			    // TODO Auto-generated method stub
			    super.addNotify();
			    
				getTopLevelAncestor().setVisible(false);
			}
		};
	}

	public void runMainMethod()
	{
		// hide the current frame and show
		// run the main method of the class passed in.
		String mainClassStr = launcher.getMainClass();
		
		try {
			Class mainClass = Class.forName(mainClassStr);
			java.lang.reflect.Method mainMethod = mainClass.getMethod("main", new Class [] {String[].class});
			mainMethod.invoke(null, new Object []{null});
        } catch (SecurityException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (NoSuchMethodException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (ClassNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IllegalArgumentException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IllegalAccessException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (InvocationTargetException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }		
	}
	
	public void viewClosed()
	{
		// TODO Auto-generated method stub

	}

}
