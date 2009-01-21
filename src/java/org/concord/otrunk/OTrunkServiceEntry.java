/**
 * 
 */
package org.concord.otrunk;

public class OTrunkServiceEntry<T>
{
	public T service;
	public Class<T> serviceInterface;
	
	public OTrunkServiceEntry(T service, Class<T> serviceInterface)
	{
		this.service = service;
		this.serviceInterface = serviceInterface;
	}
}