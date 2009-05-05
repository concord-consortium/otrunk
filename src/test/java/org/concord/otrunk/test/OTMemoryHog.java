package org.concord.otrunk.test;

import org.concord.framework.otrunk.OTObjectInterface;

public interface OTMemoryHog
    extends OTObjectInterface
{
	// dfault to around 10 mega bytes
	public static int DEFAULT_kiloBytes = 10000;
	public int getKiloBytes();
}
