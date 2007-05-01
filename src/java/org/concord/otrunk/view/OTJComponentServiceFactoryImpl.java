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

	private OTViewFactory viewFactory;


	public OTJComponentServiceFactoryImpl(OTViewFactory viewFactory)
	{
		this.viewFactory = viewFactory;
	}
	

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.view.OTJComponentServiceFactory#createOTJComponentService()
	 */
	public OTJComponentService createOTJComponentService()
	{
		return new OTJComponentServiceImpl(viewFactory);
	}

}
