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

import java.io.File;

import org.concord.otrunk.xml.dod.DoDescription;
import org.concord.otrunk.xml.dod.Pfobjects;
import org.concord.otrunk.xml.dod.PfobjectsDocument;
import org.concord.otrunk.xml.dod.ResourceType;


/**
 * XMLTypeDefinitions
 * Class name and description
 *
 * Date created: Nov 17, 2004
 *
 * @author scott<p>
 *
 */
public class XMLTypeDefinitions
{
	public static void registerTypes(File objectsFile, TypeService typeService)
		throws Exception 
	{
		PfobjectsDocument pfObjectDoc = PfobjectsDocument.Factory.parse(objectsFile);
		
		Pfobjects pfObjects = pfObjectDoc.getPfobjects();

		
		
		registerTypes(pfObjects, typeService);


	}
	
	/**
	 * This handles the pfObject way of defining objects in the system
	 * 
	 * @param pfObjects
	 * @param typeService
	 */
	public static void registerTypes(Pfobjects pfObjects, TypeService typeService)
	{
		DoDescription [] dods = (DoDescription [])pfObjects.getDoDescriptionArray();
		for(int i=0; i<dods.length; i++) {
			ResourceType [] resources = dods[i].getResourceArray();
			ResourceDefinition [] resourceDefs = new ResourceDefinition[resources.length];
			for(int j=0; j<resources.length; j++) {
				String resName = resources[j].getName();
				String resType = resources[j].getType();
				ResourceType.Param [] resParams = resources[j].getParamArray();
				ResourceDefinition.Parameter [] defParams = new ResourceDefinition.Parameter [resParams.length];
				for(int k=0; k<resParams.length; k++) {
					defParams[k] = 
						new ResourceDefinition.Parameter(resParams[k].getName(),
								resParams[k].getValue());
				}
				resourceDefs[j] = new ResourceDefinition(resName, resType, defParams);
			}
			/*
			 * This class is deprecated so I'm commenting out sections that are
			 * need to be fixed instead of fixing them
			ObjectTypeHandler objectType = 
				new ObjectTypeHandler(
						dods[i].getName(),
						"org.concord.portfolio.objects." + dods[i].getName(),
						dods[i].getExtends(),
						resourceDefs,
						typeService);
			typeService.registerUserType(dods[i].getName(), objectType);
			*/
		}

	}
	
}
