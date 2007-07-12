/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
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
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2007-07-12 18:07:51 $
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
	protected Class typeClass;
	protected Parameter [] parameters;
	
	public ResourceDefinition(String name, String type,
			Class typeClass, Parameter [] parameters)
	{
		this.name = name;
		this.type = type;
		this.typeClass = typeClass;
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

	public Class getTypeClass()
    {
    	return typeClass;
    }	
}
