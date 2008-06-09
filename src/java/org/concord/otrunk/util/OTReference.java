package org.concord.otrunk.util;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.OTObjectInterface;

/**
 * An OTReference object can be used to store meta-information about a link
 * between two objects. In our current pattern, an OTReference is contained by a parent 
 * object, the "FROM" object, and so this is not explicitly set. The "TO" object is 
 * explicitly set in the OTReference.
 * 
 * @author sfentress
 *
 */
public interface OTReference extends OTObjectInterface
{
	public OTObject getReferencedObject();
	public void setReferencedObject(OTObject referencedObject);
	
	/**
	 * A description of the type of link. Possible annotations include:
	 * 
	 * * CONTEXT				// e.g. the model that a question is asking about
	 * * ACTIVITY_REFERENCE		// the activity an object relates to
	 * * ...
	 * 
	 * @return
	 */
	public String getAnnotation();
	public void setAnnotation(String annotation);
	
	public static String CONTEXT = "CONTEXT";
	public static String ACTIVITY_REFERENCE = "ACTIVITY_REFERENCE";
}
