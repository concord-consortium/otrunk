package org.concord.otrunk.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class StandardPasswordAuthenticator extends Authenticator
{
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication("otrunksoftware", "0trunk50ftw@r3".toCharArray());
	}

}
