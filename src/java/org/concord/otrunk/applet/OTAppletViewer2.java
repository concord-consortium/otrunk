package org.concord.otrunk.applet;

import java.applet.Applet;
import java.util.ArrayList;
import java.util.Enumeration;

public class OTAppletViewer2 extends OTAppletViewer
{
    private static final long serialVersionUID = 1L;
	private static ArrayList<OTAppletViewer> viewers = new ArrayList<OTAppletViewer>();
	
	@Override
	public void init() {
		super.init();
		viewers.add(this);
	}
	
	@Override
	public void destroy() {
		viewers.remove(this);
		super.destroy();
	}
	
	@Override
	public OTAppletViewer getMaster()
	{
		if(isMaster()){
			return this;
		}

		if(master != null) {
			return master;
		}

		String masterName = getMasterName();
		if(masterName == null){
			// we don't have a pointer to our master, and we are not a master ourselves
			// this should never happen.
			throw new RuntimeException("Non-master applet doesn't have a set master property");
		}
		
		// first try the current JVM's applets
		for (OTAppletViewer viewer : viewers) {
			System.out.println("" + getAppletName() + " found in jvm: " + viewer.getParameter("name"));
			if (isSiblingMaster(viewer)) {
				System.out.println("Found a master in the current JVM");
				master = viewer;
				return master;
			}
		}
		
		Applet masterApplet = getAppletContext().getApplet(masterName);
		if(masterApplet instanceof OTAppletViewer &&
				((OTAppletViewer)masterApplet).isMaster()){
			System.out.println("Found a master directly by name");
			master = (OTAppletViewer)masterApplet;
			return master;
		}

		// We did not find our master using that simple look up approach
		// try manually going through all the applets
		Enumeration<Applet> applets = getAppletContext().getApplets();
		while(applets.hasMoreElements()){
			Applet a = applets.nextElement();
			System.out.println("" + getAppletName() + " found: " + a.getParameter("name"));
			if(a instanceof OTAppletViewer){
				if (isSiblingMaster((OTAppletViewer)a)) {
					master = (OTAppletViewer)a;
					return master;
				}
			}
		}

		return null;
	}
	
	private boolean isSiblingMaster(OTAppletViewer sibling) {
		if(sibling.isMaster() &&
			sibling.getAppletName() != null &&
			sibling.getAppletName().equals(getMasterName())){
			return true;					
		}
		return false;
	}

}
