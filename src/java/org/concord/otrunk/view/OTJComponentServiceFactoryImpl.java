/**
 * 
 */
package org.concord.otrunk.view;

import org.concord.framework.otrunk.view.OTJComponentService;
import org.concord.framework.otrunk.view.OTViewFactory;

/**
 * @author scott
 *
 */
public class OTJComponentServiceFactoryImpl
    implements org.concord.framework.otrunk.view.OTJComponentServiceFactory
{
	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTJComponentServiceFactory#createOTJComponentService()
	 */
	public OTJComponentService createOTJComponentService(OTViewFactory viewFactory, 
		boolean maintainViewMap)
	{
		return new OTJComponentServiceImpl(viewFactory, maintainViewMap);
	}

}
