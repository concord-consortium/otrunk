/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-11-22 23:05:40 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.xml;


/**
 * ResourceDefinition
 * Class name and description
 *
 * Date created: Nov 17, 2004
 *
 * @author scott<p>
 *
 */
public class ResourceDefinition
{
	public static class Parameter {
		public Parameter(String name, String value)
		{
			this.name = name;
			this.value = value;
		}
		
		public String name;
		public String value;
	}
	
	protected String name;
	protected String type;
	protected Parameter [] parameters;
	
	public ResourceDefinition(String name, String type,
			Parameter [] parameters)
	{
		this.name = name;
		this.type = type;
		this.parameters = parameters;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return Returns the type.
	 */
	public String getType()
	{
		return type;
	}	
	
	/**
	 * @return Returns the parameters.
	 */
	public Parameter[] getParameters()
	{
		return parameters;
	}	
}
