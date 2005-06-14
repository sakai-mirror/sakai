/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/Xml.java,v 1.11 2004/10/14 22:38:30 janderse.umich.edu Exp $
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

// package
package org.sakaiproject.util;

// imports
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.impl.dv.util.Base64;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
* <p>Xml is a DOM XML helper object with static functions to help with XML.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.11 $
*/
public class Xml
{
	/**
	* Create a new DOM Document.
	* @return A new DOM document.
	*/
	public static Document createDocument()
	{
		try
		{
			DocumentBuilder builder = getDocumentBuilder();
			Document doc = builder.newDocument();

			return doc;
		}
		catch (Exception any)
		{
			Logger.warn("Xml.createDocument: " + any.toString());
			return null;
		}

	}	// createDocument

	/**
	* Read a DOM Document from xml in a file.
	* @param name The file name for the xml file.
	* @return A new DOM Document with the xml contents.
	*/
	public static Document readDocument(String name)
	{
		Document doc = null;
		// first try using whatever character encoding the XML itself specifies
		try
		{
			DocumentBuilder docBuilder = getDocumentBuilder();
			InputStream fis = new FileInputStream(name);
			doc = docBuilder.parse(fis);	
		}
		catch (Exception e)
		{
			doc = null;
		}
		
		if (doc != null) return doc;
		
		// OK, that didn't work - the document is probably ISO-8859-1
		try
		{
			DocumentBuilder docBuilder = getDocumentBuilder();
			InputStreamReader in = new InputStreamReader(new FileInputStream(name), "ISO-8859-1");
			InputSource inputSource = new InputSource(in);
			doc = docBuilder.parse(inputSource);
		}
		catch (Exception any)
		{
			doc = null;
		}
		
		if (doc != null) return doc;
		
		// try forcing UTF-8
		try
		{
			DocumentBuilder docBuilder = getDocumentBuilder();
			InputStreamReader in = new InputStreamReader(new FileInputStream(name), "UTF-8");
			InputSource inputSource = new InputSource(in);
			doc = docBuilder.parse(inputSource);
		}
		catch (Exception any)
		{
			Logger.warn("Xml.readDocument failed on file: " + name + " with exception: " + any.toString());
			doc = null;
		}
		
		return doc;

	}	// readDocument

	/**
	* Read a DOM Document from xml in a string.
	* @param in The string containing the XML
	* @return A new DOM Document with the xml contents.
	*/
	public static Document readDocumentFromString(String in)
	{
		try
		{
			DocumentBuilder docBuilder = getDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(in));
			//Class clazz = this.getClass();
			//URL dtdURL = clazz.getResource("/chef_services.dtd");
			//inputSource.setSystemId(dtdURL.toString());
			Document doc = docBuilder.parse(inputSource);
			return doc;
		}
		catch (Exception any)
		{
			Logger.warn("Xml.readDocumentFromString: " + any.toString());
//			any.printStackTrace();
			return null;
		}

	}	// readDocumentFromString

	
	/**
	* Write a DOM Document to an xml file.
	* @param doc The DOM Document to write.
	* @param fileName The complete file name path.
	*/
	public static void writeDocument(Document doc, String fileName)
	{
		try
		{
			// create a file that uses the UTF-8 encoding
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");

			// Note: using xerces %%% is there a org.w3c.dom way to do this?
			XMLSerializer s = new XMLSerializer(out, new OutputFormat(
								"xml", "UTF-8", true));
			s.serialize(doc);
			out.close();
		}
		catch (Exception any)
		{
			Logger.warn("Xml.writeDocument: " + any.toString());
		}

    }	// writeDocument

   	/**
	* Write a DOM Document to an output stream.
	* @param doc The DOM Document to write.
	* @param out The output stream.
	*/
	public static String writeDocumentToString(Document doc)
	{
		try
		{
			StringWriter sw = new StringWriter();
			// Note: using xerces %%% is there a org.w3c.dom way to do this?
			XMLSerializer s = new XMLSerializer(sw, new OutputFormat(
								"xml", "UTF-8", true /*doc*/));
			s.serialize(doc);

			sw.flush();
			return sw.toString();
		}
		catch (Exception any)
		{
			Logger.warn("Xml.writeDocumentToString: " + any.toString());
			return null;
		}

    }	// writeDocument

	/**
	* Place a string into the attribute <tag> of the element <el>, encoded so special characters can be used.
	* @param el The element.
	* @param tag The attribute name.
	* @param value The string.
	*/
	public static void encodeAttribute(Element el, String tag, String value)
	{
		// encode the message body base64, and make it an attribute
		try
		{
			String encoded = Base64.encode(value.getBytes("UTF-8"));
			el.setAttribute(tag, encoded);
		}
		catch (Exception e) { Logger.warn("Xml.encodeAttribute: " + e); }


	}	// encodeAttribute

	/**
	* Decode a string from the attribute <tag> of the element <el>, that was made using encodeAttribute().
	* @param el The element.
	* @param tag The attribute name.
	* @return The string; may be empty, won't be null.
	*/
	public static String decodeAttribute(Element el, String tag)
	{
		String charset = StringUtil.trimToNull(el.getAttribute("charset"));
		if (charset == null) charset = "UTF-8";
		
		String body = StringUtil.trimToNull(el.getAttribute(tag));
		if (body != null)
		{
			try
			{
				byte[] decoded = Base64.decode(body);
				body = new String(decoded, charset);
			}
			catch (Exception e) { Logger.warn("Xml.decodeAttribute: " + e); }
		}

		if (body == null) body = "";

		return body;

	}	// decodeAttribute

	/** Whether to cache the DocumentBuilder object - should be set to true for performance enhancement */
	protected static final boolean CACHE_DOCUMENT_BUILDER = false;

	/** The key which binds our already created document builder to the thread in the CurrentService. */
	protected static final String CACHE_DOCUMENT_BUILDER_KEY = "Xml.DocumentBuilder";

	protected static final boolean DEBUG = false;

	/** Returns a DocumentBuilder object for XML parsing - the object is safe to use within the CURRENT thread only. */
	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException
	{
		DocumentBuilder ret = null;
		long time = 0;
		if (DEBUG) time = System.currentTimeMillis();

		if (CACHE_DOCUMENT_BUILDER)
		{
			ret = (DocumentBuilder) CurrentService.getInThread(CACHE_DOCUMENT_BUILDER_KEY);
			if (ret == null)
			{
				// expensive parser construction operations
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				ret = dbf.newDocumentBuilder();

				// save for reuse later in the thread
				CurrentService.setInThread(CACHE_DOCUMENT_BUILDER_KEY, ret);
			}
		}
		
		else
		{
			// expensive parser construction operations
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			ret = dbf.newDocumentBuilder();
		}

		if (DEBUG)
		{
			StringBuffer buf = (StringBuffer) CurrentService.getInThread("DEBUG");
			if (buf != null)
			{
				buf.append("\n construct document builder: " + (System.currentTimeMillis()-time));
			}
		}
		return ret;
	}	
	
}	// Xml

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/Xml.java,v 1.11 2004/10/14 22:38:30 janderse.umich.edu Exp $
*
**********************************************************************************/
