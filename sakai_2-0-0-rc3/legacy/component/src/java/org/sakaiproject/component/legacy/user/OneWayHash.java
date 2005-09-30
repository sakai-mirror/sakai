/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/user/OneWayHash.java,v 1.1 2005/04/06 02:42:38 ggolden.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

// package
package org.sakaiproject.component.legacy.user;

// imports
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import javax.mail.internet.MimeUtility;

import org.sakaiproject.service.framework.log.cover.Logger;

/**
* <p>OneWayHash converts a plain text string into an encoded string.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public class OneWayHash
{
	/**
	* Encode the clear text into an encoded form.
	* @param clear The text to encode.
	* @return The encoded text.
	*/
	public static String encode(String clear)
	{
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(clear.getBytes("UTF-8"));
            ByteArrayOutputStream bas = new ByteArrayOutputStream(digest.length + digest.length / 3 + 1);
            OutputStream encodedStream = MimeUtility.encode(bas, "base64");
            encodedStream.write(digest);
            return bas.toString();
        }
        catch (Exception e)
        {
            Logger.warn("OneWayHash.encode: exception: " + e);
            return null;
        }

	}	// encode

	/*
	public static void main(String args[])
	{
		String encoded = OneWayHash.encode("admin");
		System.out.println("admin = " + encoded);
		
		encoded = OneWayHash.encode("");
		System.out.println(" = " + encoded);

	}	// main
	*/

}	// OneWayHash

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/user/OneWayHash.java,v 1.1 2005/04/06 02:42:38 ggolden.umich.edu Exp $
*
**********************************************************************************/
