/**********************************************************************************
*
* $Header: /cvs/sakai/ctools/src/java/org/sakaiproject/component/legacy/user/SSLSocketClientWithClientAuth.java,v 1.4 2004/07/30 04:57:34 ggolden.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/

package org.sakaiproject.component.legacy.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import javax.net.ssl.*;

/**
 * <p>SSLSocketClientWithClientAuth knows how to talk to COSIGN via an HTTPS connection to authenticate
 * U of M uniqnames, both internal and friend.
 *
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.4 $
 */
public class SSLSocketClientWithClientAuth
{
	protected String host = null;
	protected int port = 0;
	protected String pathExists = null;
	protected String pathAuthenticate = null;
	protected String keyStorePassPhrase = null;
	protected String keyStoreFilePath = null;

	public SSLSocketClientWithClientAuth()
	{
		//defaults
		host = System.getProperty("cosign-server");
		if (host == null)
			host = "weblogin.umich.edu";

		try
		{
			port = new Integer(System.getProperty("cosign-port")).intValue();
			if (port == 0)
			{
				port = 443;
			}
		}
		catch (NumberFormatException ex)
		{
			port = 443;
		}
		pathExists = System.getProperty("cosign-pathExists");
		if (pathExists == null)
			pathExists = "/check/";

		pathAuthenticate = System.getProperty("cosign-pathAuthenticate");
		if (pathAuthenticate == null)
			pathAuthenticate = "/check/";

		keyStorePassPhrase = System.getProperty("cosign-keystorePassPhrase");
		if (keyStorePassPhrase == null)
			keyStorePassPhrase = "sakaidev";

		keyStoreFilePath = System.getProperty("cosign-keystoreFilePath");
		if (keyStoreFilePath == null)
			keyStoreFilePath = "/usr/local/sakai/keystore/sakaiKeystore";
	}

	protected SSLSocket initSocket() throws Exception
	{
		SSLSocketFactory factory = null;
		try
		{
			SSLContext ctx;
			KeyManagerFactory kmf;
			KeyStore ks;
			char[] passphrase = keyStorePassPhrase.toCharArray();

			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream(keyStoreFilePath), passphrase);

			kmf.init(ks, passphrase);
			ctx.init(kmf.getKeyManagers(), null, null);

			factory = ctx.getSocketFactory();
		}
		catch (Exception e)
		{
			throw new IOException(e.getMessage());
		}

		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

		return socket;
	}

	/**
	 * responseContainsTrue
	 *
	 * @param response String
	 * @return boolean
	 */
	protected boolean responseContainsTrue(String response)
	{
		return (response.indexOf("success") > 0);
	}

	/**
	 * Send a request and return the response
	 * @param req The request string
	 * @return The response
	 */
	protected String reqRes(String req) throws Exception
	{
		SSLSocket socket = initSocket();

		socket.setUseClientMode(true);
		socket.startHandshake();
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("GET " + req + " HTTP/1.1");
		out.println("Host: " + host);
		out.println();
		out.flush();

		// make sure there were no surprises
		if (out.checkError())
		{
			System.out.println("SSLSocketClient: java.io.PrintWriter error");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String inputLine;
		StringBuffer buf = new StringBuffer();
		while ((inputLine = in.readLine()) != null)
		{
			buf.append(inputLine);
                        if(inputLine.indexOf("success")>=0 || inputLine.indexOf("failure")>=0)
                          break;
		}
		String response = buf.toString();

		socket.close();
		out.close();
		in.close();

		return response;
	}

	public boolean userKnownToCosign(String userid) throws Exception
	{
		String req = pathExists + "?command=friend-lookup&login=" + userid;
		String response = reqRes(req);

		return responseContainsTrue(response);
	}

	public boolean authenticateCosign(String userid, String password) throws Exception
	{
		String req = pathAuthenticate + "?command=check&login=" + userid + "&passwd=" + password;
		String response = reqRes(req);

		return responseContainsTrue(response);
	}

	//	public static void main(String[] args) throws Exception
	//	{
	//		// System.setProperty("cosign-server","cosign-test.www.umich.edu");
	//		//System.setProperty("cosign-port","443");
	//		//System.setProperty("cosign-pathExists","/test/");
	//		//System.setProperty("cosign-pathAuthenticate","/test/");
	//		//System.setProperty("cosign-keystorePassPhrase","sakaidev");
	//		//System.setProperty("cosign-keystoreFilePath","C:\\Documents and Settings\\patkm\\.keystore");
	//		SSLSocketClientWithClientAuth cosign = new SSLSocketClientWithClientAuth();
	//		boolean known = cosign.userKnownTocosign("patkm@aol.com");
	//		boolean canAuthenticate = cosign.authenticateCosign("patkm@aol.com", "test");
	//	}

}

/**********************************************************************************
*
* $Header: /cvs/sakai/ctools/src/java/org/sakaiproject/component/legacy/user/SSLSocketClientWithClientAuth.java,v 1.4 2004/07/30 04:57:34 ggolden.umich.edu Exp $
*
**********************************************************************************/
