/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.content;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 *
 */
public class ResourcesMetadata
{
	/** Resource bundle using current language locale */
	private static ResourceBundle rb = ResourceBundle.getBundle("content");
    
	public static final String WIDGET_STRING = "string";
	public static final String WIDGET_TEXTAREA = "textarea";
	public static final String WIDGET_BOOLEAN = "boolean";
	public static final String WIDGET_INTEGER = "integer";
	public static final String WIDGET_DOUBLE = "double";
	public static final String WIDGET_DATE = "date";
	public static final String WIDGET_TIME = "time";
	public static final String WIDGET_DATETIME = "datetime";
	public static final String WIDGET_ANYURI = "anyURI";
	
	public static final String XSD_STRING = "string";
	public static final String XSD_BOOLEAN = "boolean";	
	public static final String XSD_INTEGER = "integer";	
	public static final String XSD_FLOAT = "float";	
	public static final String XSD_DOUBLE = "double";	
	public static final String XSD_DATE = "date";	
	public static final String XSD_TIME = "time";	
	public static final String XSD_DATETIME = "dateTime";
	public static final String XSD_DURATION = "duration";	
	public static final String XSD_ANYURI = "anyURI";	
	
	public static final String CLASS_SAKAI_RESOURCE_NAMESPACE = "http://sakaiproject.org/metadata#";
	public static final String CLASS_SAKAI_RESOURCE_LOCALNAME = "Resource";
	public static final String CLASS_SAKAI_RESOURCE_LABEL = "Resource";
	
	public static final String NAMESPACE_DC = "http://purl.org/dc/elements/1.1/";
	public static final String NAMESPACE_DC_ABBREV = "dc:";
	public static final String NAMESPACE_DCTERMS = "http://purl.org/dc/terms/";
	public static final String NAMESPACE_DCTERMS_ABBREV = "dcterms:";
	public static final String NAMESPACE_XSD = "http://www.w3.org/2001/XMLSchema#";
	public static final String NAMESPACE_XSD_ABBREV = "xsd:";
	
	protected static Integer NamespaceNumber = new Integer(0);
	
	public static final String PROPERTY_NAME_DC_TITLE = "title";
	public static final String PROPERTY_LABEL_DC_TITLE = rb.getString("label.dc_title");
	public static final String PROPERTY_DESCRIPTION_DC_TITLE = rb.getString("descr.dc_title");
	public static final String PROPERTY_TYPE_DC_TITLE = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_TITLE = WIDGET_STRING;
	
	public static final ResourcesMetadata PROPERTY_DC_TITLE
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_TITLE, 
								PROPERTY_LABEL_DC_TITLE,
								PROPERTY_DESCRIPTION_DC_TITLE,
								PROPERTY_TYPE_DC_TITLE,
								PROPERTY_WIDGET_DC_TITLE
							);

	public static final String PROPERTY_NAME_DC_ALTERNATIVE = "alternative";
	public static final String PROPERTY_LABEL_DC_ALTERNATIVE = rb.getString("label.dc_alt");
	public static final String PROPERTY_DESCRIPTION_DC_ALTERNATIVE = rb.getString("descr.dc_alt");
	public static final String PROPERTY_TYPE_DC_ALTERNATIVE = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_ALTERNATIVE = WIDGET_STRING;
		
	public static final ResourcesMetadata PROPERTY_DC_ALTERNATIVE
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_ALTERNATIVE, 
								PROPERTY_LABEL_DC_ALTERNATIVE,
								PROPERTY_DESCRIPTION_DC_ALTERNATIVE,
								PROPERTY_TYPE_DC_ALTERNATIVE,
								PROPERTY_WIDGET_DC_ALTERNATIVE
							);
	
	public static final String PROPERTY_NAME_DC_CREATOR = "creator";
	public static final String PROPERTY_LABEL_DC_CREATOR = rb.getString("label.dc_creator");
	public static final String PROPERTY_DESCRIPTION_DC_CREATOR = rb.getString("descr.dc_creator");
	public static final String PROPERTY_TYPE_DC_CREATOR = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_CREATOR = WIDGET_STRING;
	
	public static final ResourcesMetadata PROPERTY_DC_CREATOR
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_CREATOR, 
								PROPERTY_LABEL_DC_CREATOR,
								PROPERTY_DESCRIPTION_DC_CREATOR,
								PROPERTY_TYPE_DC_CREATOR,
								PROPERTY_WIDGET_DC_CREATOR
							);
	
	public static final String PROPERTY_NAME_DC_SUBJECT = "subject";
	public static final String PROPERTY_LABEL_DC_SUBJECT = rb.getString("label.dc_subject");
	public static final String PROPERTY_DESCRIPTION_DC_SUBJECT = rb.getString("descr.dc_subject");
	public static final String PROPERTY_TYPE_DC_SUBJECT = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_SUBJECT = WIDGET_TEXTAREA;
	
	public static final ResourcesMetadata PROPERTY_DC_SUBJECT
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_SUBJECT, 
								PROPERTY_LABEL_DC_SUBJECT,
								PROPERTY_DESCRIPTION_DC_SUBJECT,
								PROPERTY_TYPE_DC_SUBJECT,
								PROPERTY_WIDGET_DC_SUBJECT
							);

	public static final String PROPERTY_NAME_DC_DESCRIPTION = "description";
	public static final String PROPERTY_LABEL_DC_DESCRIPTION = rb.getString("label.dc_descr");
	public static final String PROPERTY_DESCRIPTION_DC_DESCRIPTION = rb.getString("descr.dc_descr");
	public static final String PROPERTY_TYPE_DC_DESCRIPTION = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_DESCRIPTION = WIDGET_TEXTAREA;
	
	public static final ResourcesMetadata PROPERTY_DC_DESCRIPTION
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_DESCRIPTION, 
								PROPERTY_LABEL_DC_DESCRIPTION,
								PROPERTY_DESCRIPTION_DC_DESCRIPTION,
								PROPERTY_TYPE_DC_DESCRIPTION,
								PROPERTY_WIDGET_DC_DESCRIPTION
							);

	public static final String PROPERTY_NAME_DC_PUBLISHER = "publisher";
	public static final String PROPERTY_LABEL_DC_PUBLISHER = rb.getString("label.dc_publisher");
	public static final String PROPERTY_DESCRIPTION_DC_PUBLISHER = rb.getString("descr.dc_publisher");
	public static final String PROPERTY_TYPE_DC_PUBLISHER = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_PUBLISHER = WIDGET_STRING;
	
	public static final ResourcesMetadata PROPERTY_DC_PUBLISHER
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_PUBLISHER, 
								PROPERTY_LABEL_DC_PUBLISHER,
								PROPERTY_DESCRIPTION_DC_PUBLISHER,
								PROPERTY_TYPE_DC_PUBLISHER,
								PROPERTY_WIDGET_DC_PUBLISHER
							);

	public static final String PROPERTY_NAME_DC_CONTRIBUTOR = "contributor";
	public static final String PROPERTY_LABEL_DC_CONTRIBUTOR = rb.getString("label.dc_contributor");
	public static final String PROPERTY_DESCRIPTION_DC_CONTRIBUTOR = rb.getString("descr.dc_contributor");
	public static final String PROPERTY_TYPE_DC_CONTRIBUTOR = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_CONTRIBUTOR = WIDGET_TEXTAREA;
	
	public static final ResourcesMetadata PROPERTY_DC_CONTRIBUTOR
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_CONTRIBUTOR, 
								PROPERTY_LABEL_DC_CONTRIBUTOR,
								PROPERTY_DESCRIPTION_DC_CONTRIBUTOR,
								PROPERTY_TYPE_DC_CONTRIBUTOR,
								PROPERTY_WIDGET_DC_CONTRIBUTOR
							);

	public static final String PROPERTY_NAME_DC_TYPE = "type";
	public static final String PROPERTY_LABEL_DC_TYPE = rb.getString("label.dc_type");
	public static final String PROPERTY_DESCRIPTION_DC_TYPE = rb.getString("descr.dc_type");
	public static final String PROPERTY_TYPE_DC_TYPE = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_TYPE = WIDGET_STRING;

	public static final ResourcesMetadata PROPERTY_DC_TYPE
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_TYPE, 
								PROPERTY_LABEL_DC_TYPE,
								PROPERTY_DESCRIPTION_DC_TYPE,
								PROPERTY_TYPE_DC_TYPE,
								PROPERTY_WIDGET_DC_TYPE
							);

	public static final String PROPERTY_NAME_DC_FORMAT = "format";
	public static final String PROPERTY_LABEL_DC_FORMAT = rb.getString("label.dc_format");
	public static final String PROPERTY_DESCRIPTION_DC_FORMAT = rb.getString("descr.dc_format");
	public static final String PROPERTY_TYPE_DC_FORMAT = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_FORMAT = WIDGET_STRING;
	
	public static final ResourcesMetadata PROPERTY_DC_FORMAT
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_FORMAT, 
								PROPERTY_LABEL_DC_FORMAT,
								PROPERTY_DESCRIPTION_DC_FORMAT,
								PROPERTY_TYPE_DC_FORMAT,
								PROPERTY_WIDGET_DC_FORMAT
							);

	public static final String PROPERTY_NAME_DC_IDENTIFIER = "identifier";
	public static final String PROPERTY_LABEL_DC_IDENTIFIER = rb.getString("label.dc_id");
	public static final String PROPERTY_DESCRIPTION_DC_IDENTIFIER = rb.getString("descr.dc_id");
	public static final String PROPERTY_TYPE_DC_IDENTIFIER = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_IDENTIFIER = WIDGET_STRING; // WIDGET_ANYURI;
	
	public static final ResourcesMetadata PROPERTY_DC_IDENTIFIER
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_IDENTIFIER, 
								PROPERTY_LABEL_DC_IDENTIFIER,
								PROPERTY_DESCRIPTION_DC_IDENTIFIER,
								PROPERTY_TYPE_DC_IDENTIFIER,
								PROPERTY_WIDGET_DC_IDENTIFIER
							);

	public static final String PROPERTY_NAME_DC_SOURCE = "source";
	public static final String PROPERTY_LABEL_DC_SOURCE = rb.getString("label.dc_source");
	public static final String PROPERTY_DESCRIPTION_DC_SOURCE = rb.getString("descr.dc_source");
	public static final String PROPERTY_TYPE_DC_SOURCE = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_SOURCE = WIDGET_STRING;
	
	public static final ResourcesMetadata PROPERTY_DC_SOURCE
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_SOURCE, 
								PROPERTY_LABEL_DC_SOURCE,
								PROPERTY_DESCRIPTION_DC_SOURCE,
								PROPERTY_TYPE_DC_SOURCE,
								PROPERTY_WIDGET_DC_SOURCE
							);

	public static final String PROPERTY_NAME_DC_LANGUAGE = "language";
	public static final String PROPERTY_LABEL_DC_LANGUAGE = rb.getString("label.dc_lang");
	public static final String PROPERTY_DESCRIPTION_DC_LANGUAGE = rb.getString("descr.dc_lang");
	public static final String PROPERTY_TYPE_DC_LANGUAGE = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_LANGUAGE = WIDGET_STRING;

	public static final ResourcesMetadata PROPERTY_DC_LANGUAGE
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_LANGUAGE, 
								PROPERTY_LABEL_DC_LANGUAGE,
								PROPERTY_DESCRIPTION_DC_LANGUAGE,
								PROPERTY_TYPE_DC_LANGUAGE,
								PROPERTY_WIDGET_DC_LANGUAGE
							);

	public static final String PROPERTY_NAME_DC_COVERAGE = "coverage";
	public static final String PROPERTY_LABEL_DC_COVERAGE = rb.getString("label.dc_coverage");
	public static final String PROPERTY_DESCRIPTION_DC_COVERAGE = rb.getString("descr.dc_coverage");
	public static final String PROPERTY_TYPE_DC_COVERAGE = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_COVERAGE = WIDGET_STRING;

	public static final ResourcesMetadata PROPERTY_DC_COVERAGE
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_COVERAGE, 
								PROPERTY_LABEL_DC_COVERAGE,
								PROPERTY_DESCRIPTION_DC_COVERAGE,
								PROPERTY_TYPE_DC_COVERAGE,
								PROPERTY_WIDGET_DC_COVERAGE
							);

	public static final String PROPERTY_NAME_DC_RIGHTS = "rights";
	public static final String PROPERTY_LABEL_DC_RIGHTS = rb.getString("label.dc_rights");
	public static final String PROPERTY_DESCRIPTION_DC_RIGHTS = rb.getString("descr.dc_rights");
	public static final String PROPERTY_TYPE_DC_RIGHTS = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_RIGHTS = WIDGET_STRING;

	public static final ResourcesMetadata PROPERTY_DC_RIGHTS
		= new ResourcesMetadata(
								NAMESPACE_DC,
								PROPERTY_NAME_DC_RIGHTS, 
								PROPERTY_LABEL_DC_RIGHTS,
								PROPERTY_DESCRIPTION_DC_RIGHTS,
								PROPERTY_TYPE_DC_RIGHTS,
								PROPERTY_WIDGET_DC_RIGHTS
							);

	public static final String PROPERTY_NAME_DC_AUDIENCE = "audience";
	public static final String PROPERTY_LABEL_DC_AUDIENCE = rb.getString("label.dc_audience");
	public static final String PROPERTY_DESCRIPTION_DC_AUDIENCE = rb.getString("descr.dc_audience");
	public static final String PROPERTY_TYPE_DC_AUDIENCE = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_AUDIENCE = WIDGET_STRING;

	public static final ResourcesMetadata PROPERTY_DC_AUDIENCE
		= new ResourcesMetadata(
								NAMESPACE_DCTERMS,
								PROPERTY_NAME_DC_AUDIENCE, 
								PROPERTY_LABEL_DC_AUDIENCE,
								PROPERTY_DESCRIPTION_DC_AUDIENCE,
								PROPERTY_TYPE_DC_AUDIENCE,
								PROPERTY_WIDGET_DC_AUDIENCE
							);

	public static final String PROPERTY_NAME_DC_TABLEOFCONTENTS = "tableOfContents";
	public static final String PROPERTY_LABEL_DC_TABLEOFCONTENTS = rb.getString("label.dc_toc");
	public static final String PROPERTY_DESCRIPTION_DC_TABLEOFCONTENTS = rb.getString("descr.dc_toc");
	public static final String PROPERTY_TYPE_DC_TABLEOFCONTENTS = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_TABLEOFCONTENTS = WIDGET_TEXTAREA;
	
	public static final ResourcesMetadata PROPERTY_DC_TABLEOFCONTENTS
		= new ResourcesMetadata(
								NAMESPACE_DCTERMS,
								PROPERTY_NAME_DC_TABLEOFCONTENTS, 
								PROPERTY_LABEL_DC_TABLEOFCONTENTS,
								PROPERTY_DESCRIPTION_DC_TABLEOFCONTENTS,
								PROPERTY_TYPE_DC_TABLEOFCONTENTS,
								PROPERTY_WIDGET_DC_TABLEOFCONTENTS
							);

	public static final String PROPERTY_NAME_DC_ABSTRACT = "abstract";
	public static final String PROPERTY_LABEL_DC_ABSTRACT = rb.getString("label.dc_abstract");
	public static final String PROPERTY_DESCRIPTION_DC_ABSTRACT = rb.getString("descr.dc_abstract");
	public static final String PROPERTY_TYPE_DC_ABSTRACT = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_ABSTRACT = WIDGET_TEXTAREA;
	
	public static final ResourcesMetadata PROPERTY_DC_ABSTRACT
		= new ResourcesMetadata(
								NAMESPACE_DCTERMS,
								PROPERTY_NAME_DC_ABSTRACT, 
								PROPERTY_LABEL_DC_ABSTRACT,
								PROPERTY_DESCRIPTION_DC_ABSTRACT,
								PROPERTY_TYPE_DC_ABSTRACT,
								PROPERTY_WIDGET_DC_ABSTRACT
							);

	public static final String PROPERTY_NAME_DC_CREATED = "created";
	public static final String PROPERTY_LABEL_DC_CREATED = rb.getString("label.dc_created");
	public static final String PROPERTY_DESCRIPTION_DC_CREATED = rb.getString("descr.dc_created");
	public static final String PROPERTY_TYPE_DC_CREATED = NAMESPACE_XSD + XSD_DATE;
	public static final String PROPERTY_WIDGET_DC_CREATED = WIDGET_DATE;
	
	public static final ResourcesMetadata PROPERTY_DC_CREATED
		= new ResourcesMetadata(
								NAMESPACE_DCTERMS,
								PROPERTY_NAME_DC_CREATED, 
								PROPERTY_LABEL_DC_CREATED,
								PROPERTY_DESCRIPTION_DC_CREATED,
								PROPERTY_TYPE_DC_CREATED,
								PROPERTY_WIDGET_DC_CREATED
							);

	public static final String PROPERTY_NAME_DC_ISSUED = "issued";
	public static final String PROPERTY_LABEL_DC_ISSUED = rb.getString("label.dc_issued");
	public static final String PROPERTY_DESCRIPTION_DC_ISSUED = rb.getString("descr.dc_issued");
	public static final String PROPERTY_TYPE_DC_ISSUED = NAMESPACE_XSD + XSD_DATE;
	public static final String PROPERTY_WIDGET_DC_ISSUED = WIDGET_DATE;
	
	public static final ResourcesMetadata PROPERTY_DC_ISSUED
		= new ResourcesMetadata(
								NAMESPACE_DCTERMS,
								PROPERTY_NAME_DC_ISSUED, 
								PROPERTY_LABEL_DC_ISSUED,
								PROPERTY_DESCRIPTION_DC_ISSUED,
								PROPERTY_TYPE_DC_ISSUED,
								PROPERTY_WIDGET_DC_ISSUED
							);

	public static final String PROPERTY_NAME_DC_MODIFIED = "modified";
	public static final String PROPERTY_LABEL_DC_MODIFIED = rb.getString("label.dc_modified");
	public static final String PROPERTY_DESCRIPTION_DC_MODIFIED = rb.getString("descr.dc_modified");
	public static final String PROPERTY_TYPE_DC_MODIFIED = NAMESPACE_XSD + XSD_DATE;
	public static final String PROPERTY_WIDGET_DC_MODIFIED = WIDGET_DATE;
	
	public static final ResourcesMetadata PROPERTY_DC_MODIFIED
		= new ResourcesMetadata(
								NAMESPACE_DCTERMS,
								PROPERTY_NAME_DC_MODIFIED, 
								PROPERTY_LABEL_DC_MODIFIED,
								PROPERTY_DESCRIPTION_DC_MODIFIED,
								PROPERTY_TYPE_DC_MODIFIED,
								PROPERTY_WIDGET_DC_MODIFIED
							);

	public static final String PROPERTY_NAME_DC_EDULEVEL = "educationLevel";
	public static final String PROPERTY_LABEL_DC_EDULEVEL = rb.getString("label.dc_edlevel");
	public static final String PROPERTY_DESCRIPTION_DC_EDULEVEL = rb.getString("descr.dc_edlevel");
	public static final String PROPERTY_TYPE_DC_EDULEVEL = NAMESPACE_XSD + XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_EDULEVEL = WIDGET_TEXTAREA;
	
	public static final ResourcesMetadata PROPERTY_DC_EDULEVEL
		= new ResourcesMetadata(
								NAMESPACE_DCTERMS,
								PROPERTY_NAME_DC_EDULEVEL, 
								PROPERTY_LABEL_DC_EDULEVEL,
								PROPERTY_DESCRIPTION_DC_EDULEVEL,
								PROPERTY_TYPE_DC_EDULEVEL,
								PROPERTY_WIDGET_DC_EDULEVEL
							);

	public static final ResourcesMetadata PROPERTY_DC_BOOLEAN
		= new ResourcesMetadata(
								NAMESPACE_XSD,
								XSD_BOOLEAN, 
								WIDGET_BOOLEAN,
								"Test " + WIDGET_BOOLEAN,
								NAMESPACE_XSD + XSD_BOOLEAN,
								WIDGET_BOOLEAN
							);

	public static final ResourcesMetadata PROPERTY_DC_DATE
		= new ResourcesMetadata(
								NAMESPACE_XSD,
								XSD_DATE, 
								WIDGET_DATE,
								"Test " + WIDGET_DATE,
								NAMESPACE_XSD + XSD_DATE,
								WIDGET_DATE
							);

	public static final ResourcesMetadata PROPERTY_DC_TIME
		= new ResourcesMetadata(
								NAMESPACE_XSD,
								XSD_TIME, 
								WIDGET_TIME,
								"Test " + WIDGET_TIME,
								NAMESPACE_XSD + XSD_TIME,
								WIDGET_TIME
							);

	public static final ResourcesMetadata PROPERTY_DC_DATETIME
		= new ResourcesMetadata(
								NAMESPACE_XSD,
								XSD_DATETIME, 
								WIDGET_DATETIME,
								"Test " + WIDGET_DATETIME,
								NAMESPACE_XSD + XSD_DATETIME,
								WIDGET_DATETIME
							);

	public static final ResourcesMetadata PROPERTY_DC_INTEGER
		= new ResourcesMetadata(
								NAMESPACE_XSD,
								XSD_INTEGER, 
								WIDGET_INTEGER,
								"Test " + WIDGET_INTEGER,
								NAMESPACE_XSD + XSD_INTEGER,
								WIDGET_INTEGER
							);

	public static final ResourcesMetadata PROPERTY_DC_DOUBLE
		= new ResourcesMetadata(
								NAMESPACE_XSD,
								XSD_DOUBLE, 
								WIDGET_DOUBLE,
								"Test " + WIDGET_DOUBLE,
								NAMESPACE_XSD + XSD_DOUBLE,
								WIDGET_DOUBLE
							);

	public static final ResourcesMetadata PROPERTY_DC_ANYURI
		= new ResourcesMetadata(
								NAMESPACE_XSD,
								XSD_ANYURI, 
								WIDGET_ANYURI,
								"Test " + WIDGET_ANYURI,
								NAMESPACE_XSD + XSD_ANYURI,
								WIDGET_ANYURI
							);


	/*
	public static final String PROPERTY_NAME_DC_ = "";
	public static final String PROPERTY_LABEL_DC_ = "";
	public static final String PROPERTY_DESCRIPTION_DC_ = 
			"";
	public static final String PROPERTY_TYPE_DC_ = XSD_STRING;
	public static final String PROPERTY_WIDGET_DC_ = WIDGET_STRING;
	
	public static final ResourcesMetadata PROPERTY_DC_
		= new ResourcesMetadata(
								PROPERTY_NAME_DC_, 
								PROPERTY_LABEL_DC_,
								PROPERTY_DESCRIPTION_DC_,
								PROPERTY_TYPE_DC_,
								PROPERTY_WIDGET_DC_
							);

*/

	public static final String[] DublinCore =	
		{
			PROPERTY_NAME_DC_TITLE,
			PROPERTY_NAME_DC_ALTERNATIVE,
			PROPERTY_NAME_DC_CREATOR,
			PROPERTY_NAME_DC_SUBJECT,
			PROPERTY_NAME_DC_DESCRIPTION,
			PROPERTY_NAME_DC_TABLEOFCONTENTS,
			PROPERTY_NAME_DC_ABSTRACT,
			PROPERTY_NAME_DC_PUBLISHER,
			PROPERTY_NAME_DC_CONTRIBUTOR,
			PROPERTY_NAME_DC_TYPE,
			PROPERTY_NAME_DC_FORMAT,
			PROPERTY_NAME_DC_CREATED,
			PROPERTY_NAME_DC_ISSUED,
			PROPERTY_NAME_DC_MODIFIED,
			PROPERTY_NAME_DC_IDENTIFIER,
			PROPERTY_NAME_DC_SOURCE,
			PROPERTY_NAME_DC_LANGUAGE,
			PROPERTY_NAME_DC_COVERAGE,
			PROPERTY_NAME_DC_RIGHTS,
			PROPERTY_NAME_DC_AUDIENCE,
			PROPERTY_NAME_DC_EDULEVEL
		};
		

	public static final ResourcesMetadata[] DEFINED_PROPERTIES =	
		{
			PROPERTY_DC_TITLE,
			PROPERTY_DC_ALTERNATIVE,
			PROPERTY_DC_CREATOR,
			PROPERTY_DC_SUBJECT,
			PROPERTY_DC_DESCRIPTION,
			PROPERTY_DC_TABLEOFCONTENTS,
			PROPERTY_DC_ABSTRACT,
			PROPERTY_DC_PUBLISHER,
			PROPERTY_DC_CONTRIBUTOR,
			PROPERTY_DC_TYPE,
			PROPERTY_DC_FORMAT,
			PROPERTY_DC_CREATED,
			PROPERTY_DC_ISSUED,
			PROPERTY_DC_MODIFIED,
			PROPERTY_DC_IDENTIFIER,
			PROPERTY_DC_SOURCE,
			PROPERTY_DC_LANGUAGE,
			PROPERTY_DC_COVERAGE,
			PROPERTY_DC_RIGHTS,
			PROPERTY_DC_AUDIENCE,
			PROPERTY_DC_EDULEVEL,
			PROPERTY_DC_ANYURI,
			PROPERTY_DC_DOUBLE,
			PROPERTY_DC_DATETIME,
			PROPERTY_DC_TIME,
			PROPERTY_DC_DATE,
			PROPERTY_DC_BOOLEAN,
			PROPERTY_DC_INTEGER
			
		};
		
	public static ResourcesMetadata getProperty(String name)
	{
		ResourcesMetadata rv = null;
		if(name != null)
		{
			boolean found = false;
			for(int k = 0; !found && k < DEFINED_PROPERTIES.length; k++)
			{
				if(DEFINED_PROPERTIES[k].getFullname().equalsIgnoreCase(name) || DEFINED_PROPERTIES[k].getShortname().equalsIgnoreCase(name))
				{
					rv = DEFINED_PROPERTIES[k];
					found = true;
				}
			}
		}
		return rv;
	}
	

	public static ResourcesMetadata[] getProperties(String[] names)
	{
		List results = new Vector();
		for(int i = 0; i < names.length; i++)
		{
			if(names[i] == null)
			{
				continue;
			}
			boolean found = false;
			for(int k = 0; !found && k < DEFINED_PROPERTIES.length; k++)
			{
				if(DEFINED_PROPERTIES[k].getFullname().equalsIgnoreCase(names[i]))
				{
					results.add(DEFINED_PROPERTIES[k]);
					found = true;
				}
			}
		}
		
		ResourcesMetadata[] rv = new ResourcesMetadata[results.size()];
		
		for(int j = 0; j < results.size(); j++)
		{
			rv[j] = (ResourcesMetadata) results.get(j);
		}
		
		return rv;
	}
	
	/**
	 * The string representation of the localname for the metadata property
	 */
	protected String m_localname;
	
	/**
	 * The string representation of the namespace for the metadata property
	 */
	protected String m_namespace;
	
	/**
	 * A string that can be used to refer to the metadata property
	 */
	protected String m_label;
	
	/**
	 * An explanation of the metadata property, including the nature of the legal values
	 */
	protected String m_description;
	
	/**
	 * The datatype of legal values for the metadata property 
	 * (usually a URI ref for an XML Schema Datatype)
	 */
	protected String m_datatype;
	
	/**
	 * The default editor widget for the metadata property
	 */
	protected String m_widget;
	
	/**
	 * Constructor.
	 * @param 	name		The string representation of the URI for the metadata property
	 * @param 	label		A string that can be used to refer to the metadata property
	 * @param 	description	An explanation of the metadata property, describing the valid values
	 * @param 	datatype	The datatype of legal values for the metadata property 
	 * 						(usually a URI ref for an XML Schema Datatype)
	 * @param 	widget		The default editor widget for the metadata property 
	 */
	public ResourcesMetadata(String namespace, String localname, String label, String description, String datatype, String widget)
	{
		m_datatype = datatype;
		m_description = description;
		m_label = label;
		m_namespace = namespace;
		m_localname = localname;
		m_widget = widget;
	}
	
	/**
	 * @return The datatype of legal values for the metadata property (usually a URI ref for an XML Schema Datatype)
	 */
	public String getDatatype()
	{
		return m_datatype;
	}

	/**
	 * @return An explanation of the metadata property describing the valid values
	 */
	public String getDescription()
	{
		return m_description;
	}

	/**
	 * @return
	 */
	public String getLabel()
	{
		return m_label;
	}

	/**
	 * @return The string representation of the URI for the metadata property
	 */
	public String getLocalname()
	{
		return m_localname;
	}

	/**
	 * @return The string representation of the full namespace for the metadata property
	 */
	public String getNamespace()
	{
		return m_namespace;
	}
	
	/**
	 * @return The abbreviated version of the namespace (including delimiter)
	 */
	public String getNamespaceAbbrev()
	{
		return getNamespaceAbbrev(m_namespace);
	}

	/**
	 * @return The string representation of the URI for the metadata property
	 */
	public String getFullname()
	{
		return m_namespace + m_localname;
	}

	/**
	 * @return The string representation of the URI for the metadata property
	 */
	public String getShortname()
	{
		String abbrev = getNamespaceAbbrev(m_namespace);
		if(abbrev == null)
		{
			abbrev = m_namespace;
		}
		return abbrev + m_localname;
	}

	/**
	 * @return The default editor widget for the metadata property 
	 */
	public String getWidget()
	{
		return m_widget;
	}

	/**
	 * @param 	datatype	The datatype of legal values for the metadata property (usually a URI ref for an XML Schema Datatype)
	 */
	public void setDatatype(String datatype)
	{
		m_datatype = datatype;
	}

	/**
	 * @param 	description	An explanation of the metadata property describing the valid values
	 */
	public void setDescription(String description)
	{
		m_description = description;
	}

	/**
	 * @param 	label	A string that can be used to refer to the metadata property
	 */
	public void setLabel(String label)
	{
		m_label = label;
	}

	/**
	 * @param 	name	The string representation of the namespace for the metadata property
	 */
	public void setNamespace(String namespace)
	{
		m_namespace = namespace;
	}

	/**
	 * @param 	name	The string representation of the URI for the metadata property
	 */
	public void setLocalname(String localname)
	{
		m_localname = localname;
	}

	/**
	 * @param 	widget	The default editor widget for the metadata property 
	 */
	public void setWidget(String widget)
	{
		m_widget = widget;
	}
	
	protected static Map m_ns2abbrev;
	protected static Map m_abbrev2ns;
	 
	/**
	 * @param namespace The string representation of the namespace for the metadata property
	 * @param abbrev The abbreviated version of the namespace (including delimiter)
	 */
	public static void setNamespaceAbbrev(String namespace, String abbrev)
	{
		if(m_ns2abbrev == null || m_abbrev2ns == null)
		{
			initNamespaceMaps();
		}

		// what if namespace already defined mapping to a different abbrev?
		// new abbrev will be used instead but old abbrev will still map to namespace
		
		m_abbrev2ns.put(abbrev, namespace);
		m_ns2abbrev.put(namespace,abbrev);
	}
	
	/**
	 * @param namespace	The string representation of the namespace for a metadata property
	 * @return The abbreviated version of the namespace identifier (including delimiter)
	 */
	public static String getNamespaceAbbrev(String namespace)
	{
		String abbrev = null;
		if(m_ns2abbrev == null)
		{
			initNamespaceMaps();
		}
		abbrev = (String) m_ns2abbrev.get(namespace);
		if(abbrev == null)
		{
			abbrev = assignAbbrev(namespace);
		}
		return abbrev;
	}
	
	/**
	 * @param abbrev The abbreviated version of the namespace identifier (including delimiter)
	 * @return The string representation of the full name of the namespace 
	 */
	public static String getNamespace(String abbrev)
	{
		String namespace = null;
		if(m_abbrev2ns == null)
		{
			initNamespaceMaps();
		}
		namespace = (String) m_abbrev2ns.get(abbrev);
		return namespace;
	}
	
	/**
	 * Make sure that maps are defined and default values for Dublin Core 
	 * and XMLSchema Datatypes are included
	 */
	protected static void initNamespaceMaps()
	{
		if(m_ns2abbrev == null)
		{
			m_ns2abbrev = new Hashtable();
		}
		if(m_abbrev2ns == null)
		{
			m_abbrev2ns = new Hashtable();
		}
		setNamespaceAbbrev(NAMESPACE_DC, NAMESPACE_DC_ABBREV);
		setNamespaceAbbrev(NAMESPACE_DCTERMS, NAMESPACE_DCTERMS_ABBREV);
		setNamespaceAbbrev(NAMESPACE_XSD, NAMESPACE_XSD_ABBREV);
	}
	
	protected static String assignAbbrev(String namespace)
	{
		String abbrev = "error";
		synchronized(NamespaceNumber)
		{
			abbrev = "s" + NamespaceNumber;
			NamespaceNumber = new Integer(NamespaceNumber.byteValue() + 1);
		}
		setNamespaceAbbrev(namespace, abbrev);
		return abbrev;
	}

}	// ResourcesMetadata



