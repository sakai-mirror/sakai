/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.component.legacy.content;

// imports
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.content.ContentTypeImageService;

/**
* <p>BasicContentTypeImage implements the CHEF ContentTypeImageService.</p>
* <p>Work Interfaces:<ul><li>ContentTypeImageService</li></ul></p>
* <p>Implementation Design:<ul><li>
* This implementation reads the properties file, populate a Properties object and do the look up afterwards.
* </li></ul></p>
* <p>External Dependencies:<ul><li>none</li></ul></p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.service.ContentTypeImageService
*/
public class BasicContentTypeImageService implements ContentTypeImageService
{
	/** Map content type to image file name. */
	protected Properties m_contentTypeImages = null;

	/** Map content type to display name. */
	protected Properties m_contentTypeDisplayNames = null;

	/** Map content type to file extension. */
	protected Properties m_contentTypeExtensions = null;

	/** Map file extension to content type. */
	protected Properties m_contentTypes = null;

	/** Default file name for unknown types. */
	protected static final String DEFAULT_FILE = "/sakai/generic.gif";

	/** Default content type for unknown extensions. */
	protected static final String UNKNOWN_TYPE = "application/octet-stream";

	/** Another type reported when the file is unknown, Mac IE 5.2. */
	// Note: although this is reported by IE, it's not just binary...
	//protected static final String UNKNOWN_TYPE_II = "application/x-macbinary";

	/** The file name containing the image definitions. */
	protected String m_imageFileName = null;

	/** The file name containing the name definitions. */
	protected String m_nameFileName = null;

	/** The file name containing the extension definitions. */
	protected String m_extensionFileName = null;

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/**
	 * Set the file name containing the image definitions.
	 * @param name the file name.
	 */
	public void setImageFile(String name)
	{
		m_imageFileName = name;
	}

	/**
	 * Set the file name containing the name definitions.
	 * @param name the file name.
	 */
	public void setNameFile(String name)
	{
		m_nameFileName = name;
	}

	/**
	 * Set the file name containing the extension definitions.
	 * @param name the file name.
	 */
	public void setExtensionFile(String name)
	{
		m_extensionFileName = name;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			m_logger.info(this +".init()");

			ClassLoader cl = this.getClass().getClassLoader();

			// read the images file
			m_contentTypeImages = new Properties();
			try
			{
				m_contentTypeImages.load(cl.getResourceAsStream(m_imageFileName));
			}
			catch (FileNotFoundException e)
			{
				m_logger.warn(this +".init(): File not found for images file: " + m_imageFileName);
			}
			catch (IOException e)
			{
				m_logger.warn(this +".init(): IOException with images file: " + m_imageFileName);
			}

			// read the display names file
			m_contentTypeDisplayNames = new Properties();
			try
			{
				m_contentTypeDisplayNames.load(cl.getResourceAsStream(m_nameFileName));
			}
			catch (FileNotFoundException e)
			{
				m_logger.warn(this +".init(): File not found for names file: " + m_nameFileName);
			}
			catch (IOException e)
			{
				m_logger.warn(this +".init(): IOException with names file: " + m_nameFileName);
			}

			// read the content type extensions file
			// use content type as the key
			m_contentTypeExtensions = new Properties();
			try
			{
				m_contentTypeExtensions.load(cl.getResourceAsStream(m_extensionFileName));
			}
			catch (FileNotFoundException e)
			{
				m_logger.warn(this +".init(): File not found for names file: " + m_extensionFileName);
			}
			catch (IOException e)
			{
				m_logger.warn(this +".init(): IOException with names file: " + m_extensionFileName);
			}

			// read the content type extensions file
			// use extension as the key
			m_contentTypes = new Properties();
			try
			{
				// open the file for line reading
				BufferedReader reader = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(m_extensionFileName)));

				// read each line
				String line = null;
				do
				{
					// read the line - null is eof
					line = reader.readLine();
					if ((line != null) && (line.length() != 0))
					{
						// skip the comment lines
						if (line.startsWith("#"))
							continue;

						// MIME type is the string before the equal sign
						String type = line.substring(0, line.indexOf("="));

						// extension string is after the equal sign
						// parse the extension string by space 
						String tokens = line.substring(line.indexOf("=") + 1);
						StringTokenizer st = new StringTokenizer(tokens, " ", false);

						if (!st.hasMoreTokens())
							continue;

							while (st.hasMoreTokens())
							{
								String ext = st.nextToken();
								m_contentTypes.put(ext, type);
							}
					}
				}
				while (line != null);

				reader.close();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".init(): Exception with ext file: " + m_extensionFileName + " : " + e.toString());
			}
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{

		m_contentTypeImages.clear();
		m_contentTypeImages = null;

		m_contentTypeDisplayNames.clear();
		m_contentTypeDisplayNames = null;

		m_contentTypeExtensions.clear();
		m_contentTypeExtensions = null;

		m_contentTypes.clear();
		m_contentTypes = null;

		m_logger.info(this +".destroy()");

	} // shutdown

	/*******************************************************************************
	* ContentTypeImageService implementation
	*******************************************************************************/

	/**
	* Get the image file name based on the content type.
	* @param contentType The content type string.
	* @return The image file name based on the content type.
	*/
	public String getContentTypeImage(String contentType)
	{
		String image = m_contentTypeImages.getProperty(contentType.toLowerCase());

		// if not there, use the DEFAULT_FILE
		if (image == null)
			image = DEFAULT_FILE;

		return image;

	} // getContentTypeImage

	/**
	* Get the display name of the content type.
	* @param contentType The content type string.
	* @return The display name of the content type.
	*/
	public String getContentTypeDisplayName(String contentType)
	{
		String name = m_contentTypeDisplayNames.getProperty(contentType.toLowerCase());

		// if not there, use the content type as the name
		if (name == null)
			name = contentType;

		return name;

	} // getContentTypeDisplayName

	/**
	* Get the file extension value of the content type.
	* @param contentType The content type string.
	* @return The file extension value of the content type.
	*/
	public String getContentTypeExtension(String contentType)
	{
		String extension = m_contentTypeExtensions.getProperty(contentType.toLowerCase());

		// if not there, use empty String
		if (extension == null)
		{
			extension = "";
		}
		else
		{
			if (extension.indexOf(" ") != -1)
			{
				// there might be more than one extension for this MIME type, get one listed first
				extension = extension.substring(0, extension.indexOf(" "));
			}
		}

		return extension;

	} // getContentTypeExtension

	/**
	* Get the content type string that is used for this file extension.
	* @param extension The file extension (to the right of the dot, not including the dot).
	* @return The content type string that is used for this file extension.
	*/
	public String getContentType(String extension)
	{
		String type = m_contentTypes.getProperty(extension);

		// if not there, use the UNKNOWN_TYPE
		if (type == null)
			type = UNKNOWN_TYPE;

		return type;

	} // getContentTypeDisplayName

	/**
	* Is the type one of the known types used when the file type is unknown?
	* @param contentType The content type string to test.
	* @return true if the type is a type used for unknown file types, false if not.
	*/
	public boolean isUnknownType(String contentType)
	{
		if (contentType.equals(UNKNOWN_TYPE))
			return true;
		//if (contentType.equals(UNKNOWN_TYPE_II)) return true;

		return false;

	} // isUnknownType

} // BasicContentTypeImage



