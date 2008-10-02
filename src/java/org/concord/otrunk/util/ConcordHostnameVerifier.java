package org.concord.otrunk.util;

import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class ConcordHostnameVerifier
    implements HostnameVerifier
{
	X509Certificate concordCert;
	
	public ConcordHostnameVerifier() throws CertificateException, IOException {
		// Init the concordCert
		URL certURL = this.getClass().getClassLoader().getResource("org/concord/otrunk/concord.cert");
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		concordCert = (X509Certificate) certFactory.generateCertificate(certURL.openStream());
	}

	public boolean verify(String hostname, SSLSession session)
	{
		/*
		 * Verify if the Concord Cert matches the session cert
		 */
		try {
        	Certificate[] certs = session.getPeerCertificates();
        	X509Certificate cert = (X509Certificate) certs[0];
        	if (cert.equals(concordCert)) {
        		return true;
        	}
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
	}

}
