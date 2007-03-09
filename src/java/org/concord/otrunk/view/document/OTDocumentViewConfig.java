/**
 * 
 */
package org.concord.otrunk.view.document;

import org.concord.framework.otrunk.OTObjectInterface;

/**
 * @author scott
 *
 */
public interface OTDocumentViewConfig extends OTObjectInterface 
{
	/**
	 * The mode the document view uses to render embedded components
	 * 
	 * @return
	 */
	public String getMode();
	public void setMode(String mode);
}
