
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-04-11 15:01:08 $
 * $Author: maven $
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
