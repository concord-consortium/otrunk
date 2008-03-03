/**
 * 
 */
package org.concord.otrunk;

public class OTrunkServiceEntry
{
	public Object service;
	public Class serviceInterface;
	
	public OTrunkServiceEntry(Object service, Class serviceInterface)
	{
		this.service = service;
		this.serviceInterface = serviceInterface;
	}
}