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
 * $Revision: 1.3 $
 * $Date: 2007-02-12 05:37:48 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.otrunk.datamodel;


public class DataObjectUtil
{
    /**
     * This does a shallow copy.  It copys the collection objects but 
     * all other resources are just added as references not copied. 
     * @param original
     * @param copy
     */
    public static void copyInto(OTDataObject original, OTDataObject copy)
    {
    	try {
			copyInto(original, copy, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
     
    public static void copyInto(OTDataObject source, OTDataObject dest,
    		int maxDepth) throws Exception
    {
    	Copier.copyInto(source, dest, maxDepth);
    }

    
    
    public static OTDataObject copy(OTDataObject original, OTDatabase otDb, int maxDepth) 
    	throws Exception
    {
    	OTDataObject copy = otDb.createDataObject();

    	copyInto(original, copy, maxDepth);
    	
    	return copy;
    }    
}
