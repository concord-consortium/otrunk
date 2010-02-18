package org.concord.otrunk.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;

import org.concord.framework.otrunk.OTObject;
import org.concord.swing.util.ComponentScreenshot;

	public class ComponentImageSaver
	    implements Runnable
	{
		Component comp;

		File folder;

		OTObject otObject;

		String text = null;
		int width = -1;
		int height = -1;
				
		float scaleX = 1;

		float scaleY = 1;

		private String folderPath;
		
		private boolean useRobot = false;

		private Robot robot;

		public ComponentImageSaver(Component comp, File folder, String folderPath,
		        OTObject otObject, float scaleX, float scaleY, boolean useRobot)
		{
			this.comp = comp;
			this.folder = folder;
			this.otObject = otObject;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.folderPath = folderPath;
			this.useRobot = useRobot;
		}

		public void run()
		{
			// TODO Auto-generated method stub
			try {
				String id = otObject.otExternalId();
				id = id.replaceAll("/", "_");
				id = id.replaceAll("'", "");
				id = id.replaceAll("!", "");
				
				if (!folder.isDirectory()) {
					text = null;
					return;
				}

				File newFile = new File(folder, id + ".png");
				String originalId = id;
				
				// This is a hack so when multiple students objects are shown at the same time
				// the file names don't overlap because all of them will return the same thing from
				// otExternalId().  This approach isn't perfect though because the same object 
				// might be referenced twice so in that case the same image should be used.
				int i=1;
				while(newFile.exists()){
					id = originalId + "_" + i;
					i++;
					newFile = new File(folder, id + ".png");
				}
				BufferedImage bim;
				if (useRobot) {
					if (robot == null) {
						robot = new Robot();
					}
					Point topLeft = comp.getLocationOnScreen();
					Dimension size = comp.getSize();
					bim = robot.createScreenCapture(new Rectangle(topLeft, size));
				} else {
					bim = ComponentScreenshot.makeComponentImageAlpha(comp, scaleX, scaleY);
				}
				
				width = bim.getWidth();
				height = bim.getHeight();
				
				ComponentScreenshot.saveImageAsFile(bim, newFile, "png");
				bim.flush();
				
				text = folderPath + "/" + id + ".png";
				return;

			} catch (Throwable t) {
				t.printStackTrace();
			}
			text = null;
		}

		public String getText()
		{
			return text;
		}
		
		public int getWidth()
		{
			return width;
		}
		
		public int getHeight()
		{
			return height;
		}
	}