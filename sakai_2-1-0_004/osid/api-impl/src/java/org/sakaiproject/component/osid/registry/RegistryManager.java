/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/portal/presence/src/java/org/sakaiproject/tool/portal/PresenceTool.java $
* $Id: PresenceTool.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
**********************************************************************************
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
package org.sakaiproject.component.osid.registry;

/**
* <p>
 * RegistryManager implements a "straw-man" registry interface.
 * </p>
 * 
 * @author Massachusetts Institute of Technology
 * @version $Id: PresenceTool.java 632 2005-07-14 21:22:50Z jeffkahn@mit.edu $
 */
public class RegistryManager
implements edu.mit.osid.registry.RegistryManager
{
	private static final String REGISTRY_XML_FILENAME = "OSIDProviderRegistry.xml";
	private static final String ID_MANAGER_IMPLEMENTATION = "org.sakaiproject.component.osid.id";
	private static final String FILE_NOT_FOUND_MESSAGE = "Cannot find or open ";
	
	private static final String REGISTRY_TAG = "registry";
	private static final String PROVIDER_RECORD_TAG = "record";
	private static final String OSID_SERVICE_TAG = "oki:osidservice";
	private static final String OSID_VERSION_TAG = "oki:osidversion";
	private static final String OSID_LOAD_KEY_TAG = "oki:osidloadkey";
	private static final String DISPLAY_NAME_TAG = "dc:title";
	private static final String DESCRIPTION_TAG = "dc:description";
	private static final String ID_TAG = "dc:identifier";
	private static final String CREATOR_TAG = "dc:creator";
	private static final String PUBLISHER_TAG = "dc:publisher";
	private static final String REGISTRATION_DATE_TAG = "dc:registrationDate";
	private static final String RIGHTS_TAG = "dc:rights";
	private static final String DC_NAMESPACE = "xmlns:dc";
	private static final String DC_NAMESPACE_URL = "http://purl.org/dc/elements/1.1/";
	private static final String OKI_NAMESPACE = "xmlns:oki";
	private static final String OKI_NAMESPACE_URL = "http://www.okiproject.org/registry/elements/1.0/" ;
	private static final String DATE_FORMAT = "yyyy-MM-dd kk:mm:ss:SSS zz";

    	private org.osid.OsidContext passedInContext = null;
	private org.osid.OsidContext emptyContext = new org.osid.OsidContext();
    	private java.util.Properties configuration = null;
	private java.util.Properties managerProperties = new java.util.Properties();
	private org.osid.id.IdManager idManager = null;
	
	/**
	 * Return the OsidContext assigned earlier.
	 */
    	public org.osid.OsidContext getOsidContext()
		throws edu.mit.osid.registry.RegistryException
    	{
        	return this.passedInContext;
    	}

	/**
	 * Store away an OsidContext from the consumer.
	 */
    	public void assignOsidContext(org.osid.OsidContext context)
		throws org.osid.repository.RepositoryException
    	{
    	    this.passedInContext = context;
    	}

	/**
	 * Simple getter for IdManager with error checking
	 */
	private org.osid.id.IdManager getIdManager()
	{
		if ( this.idManager != null ) return this.idManager;
		try {
			this.idManager = (org.osid.id.IdManager) 
				org.sakaiproject.service.framework.component.cover.ComponentManager.get(org.osid.id.IdManager.class);

		} catch (Throwable t) {
			log(t);
		}
		return this.idManager;
	}

	/**
	 * Store the configuration from the consumer and perform other intialization.
	 * This method should be called after assignOsidContext() and before any others.
	 * The default OsidLoader does this automatically.
	 */
    	public void assignConfiguration(java.util.Properties configuration)
		throws org.osid.repository.RepositoryException
    	{
        	this.configuration = configuration;
	}

	/**
	 * Examine the registry XML for information and maker Providers
	 */
	public edu.mit.osid.registry.ProviderIterator getProviders()
		throws edu.mit.osid.registry.RegistryException
	{
		java.util.Vector result = new java.util.Vector();

		java.io.InputStream istream = org.sakaiproject.component.osid.loader.OsidLoader.getConfigStream(REGISTRY_XML_FILENAME,getClass());
		if (istream == null) {
			log(FILE_NOT_FOUND_MESSAGE +  REGISTRY_XML_FILENAME);					
			throw new edu.mit.osid.registry.RegistryException(org.osid.OsidException.CONFIGURATION_ERROR);
		}

		try {
			javax.xml.parsers.DocumentBuilderFactory dbf = null;
			javax.xml.parsers.DocumentBuilder db = null;
			org.w3c.dom.Document document = null;
			
			dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			document = db.parse(istream);
			
			org.w3c.dom.NodeList records = document.getElementsByTagName(PROVIDER_RECORD_TAG);
			int numRecords = records.getLength();
			for (int i=0; i < numRecords; i++) {
				String osidService = null;
				String osidVersion = null;
				String osidLoadKey = null;				
				String title = null;
				String description = null;
				String creator = null;
				String publisher = null;
				String registrationDate = null;
				String identifier = null;
				String rights = null;
				
				org.w3c.dom.Element record = (org.w3c.dom.Element)records.item(i);
				org.w3c.dom.NodeList nodeList = record.getElementsByTagName(OSID_SERVICE_TAG);
				int numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						osidService = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(OSID_VERSION_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						osidVersion = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(OSID_LOAD_KEY_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						osidLoadKey = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(DISPLAY_NAME_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						title = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(DESCRIPTION_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						description = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(ID_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						identifier = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(CREATOR_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						creator = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(PUBLISHER_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						publisher = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(REGISTRATION_DATE_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						registrationDate = e.getFirstChild().getNodeValue();
					}
				}
				
				record = (org.w3c.dom.Element)records.item(i);
				nodeList = record.getElementsByTagName(RIGHTS_TAG);
				numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						rights = e.getFirstChild().getNodeValue();
					}
				}
				
/*
				System.out.println(osidVersion);
				System.out.println(osidLoadKey);
				System.out.println(title);
				System.out.println(description);
				System.out.println(creator);
				System.out.println(publisher);
				System.out.println(registrationDate);
				System.out.println(identifier);
				System.out.println(rights);
*/
				result.addElement(new Provider(this,
							   osidService,
							   osidVersion,
							   osidLoadKey,
							   title,
							   description,
							   getIdManager().getId(identifier),
							   creator,
							   publisher,
							   registrationDate,
							   rights));
				//System.out.println("Element Added...");
			}
		} catch (Throwable t) {
			log(t);
		}
		return new ProviderIterator(result);
	}
	
	/**
	 * Unimplemented method.  We have no Provider Types for the community at this time.
	 */
	public edu.mit.osid.registry.ProviderIterator getProvidersByType(org.osid.shared.Type providerType)
		throws edu.mit.osid.registry.RegistryException
	{
		throw new edu.mit.osid.registry.RegistryException(org.osid.OsidException.UNIMPLEMENTED);
	}
	
	/**
	 *  Return a Provider from the content in the registry XML file 
	 */
	public edu.mit.osid.registry.Provider getProvider(org.osid.shared.Id providerId)
		throws edu.mit.osid.registry.RegistryException
	{
		edu.mit.osid.registry.ProviderIterator providerIterator = getProviders();
		try {
			while (providerIterator.hasNextProvider())
			{
				edu.mit.osid.registry.Provider provider = providerIterator.nextProvider();
				if (provider.getId().isEqual(providerId)) {
					return provider;
				}
			}
		} catch (Throwable t) {
			log(t);
			throw new edu.mit.osid.registry.RegistryException(org.osid.OsidException.OPERATION_FAILED);
		}
		throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.UNKNOWN_ID);
	}
	
	/**
	 *  Append a new record element to registry XML file.  Omit any nodes where the input argument is
	 *  null, except for the Registration Date.  If that is null, insert the current date and time.
	 */
	public edu.mit.osid.registry.Provider createProvider(String osidService,
													 String osidVersion,
													 String osidLoadKey,
													 String displayName,
													 String description,
													 org.osid.shared.Id id,
													 String creator,
													 String publisher,
													 String registrationDate,
													 String rights)
		throws edu.mit.osid.registry.RegistryException
	{
        try {
			String now = null;
			java.io.InputStream istream = getClass().getClassLoader().getResourceAsStream(REGISTRY_XML_FILENAME);
			if (istream == null) {
				log(FILE_NOT_FOUND_MESSAGE + REGISTRY_XML_FILENAME);
				throw new edu.mit.osid.registry.RegistryException(org.osid.OsidException.CONFIGURATION_ERROR);
			}
			
			javax.xml.parsers.DocumentBuilderFactory dbf = null;
			javax.xml.parsers.DocumentBuilder db = null;
			org.w3c.dom.Document document = null;
			
			dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			document = db.parse(istream);
			
			org.w3c.dom.NodeList nodeList = document.getElementsByTagName(REGISTRY_TAG);
			int numNodes = nodeList.getLength();
			org.w3c.dom.Element records = (org.w3c.dom.Element)nodeList.item(numNodes-1);
			org.w3c.dom.Element record = document.createElement(PROVIDER_RECORD_TAG);
			record.setAttribute(DC_NAMESPACE,DC_NAMESPACE_URL);
			record.setAttribute(OKI_NAMESPACE,OKI_NAMESPACE_URL);
			
			org.w3c.dom.Element e;
			if (osidService != null) {
				e = document.createElement(OSID_SERVICE_TAG);
				e.appendChild(document.createTextNode(osidService));
				record.appendChild(e);
			}			

			if (osidVersion != null) {
				e = document.createElement(OSID_VERSION_TAG);
				e.appendChild(document.createTextNode(osidVersion));
				record.appendChild(e);
			}			
			
			if (osidLoadKey != null) {
				e = document.createElement(OSID_LOAD_KEY_TAG);
				e.appendChild(document.createTextNode(osidLoadKey));
				record.appendChild(e);
			}			
			
			if (displayName != null) {
				e = document.createElement(DISPLAY_NAME_TAG);
				e.appendChild(document.createTextNode(displayName));
				record.appendChild(e);
			}			
			
			if (description != null) {
				e = document.createElement(DESCRIPTION_TAG);
				e.appendChild(document.createTextNode(description));
				record.appendChild(e);
			}			
			
			if (id != null) {
				e = document.createElement(ID_TAG);
				e.appendChild(document.createTextNode(id.getIdString()));
				record.appendChild(e);
			}			
			
			if (creator != null) {
				e = document.createElement(CREATOR_TAG);
				e.appendChild(document.createTextNode(creator));
				record.appendChild(e);
			}			
			
			if (publisher != null) {
				e = document.createElement(PUBLISHER_TAG);
				e.appendChild(document.createTextNode(publisher));
				record.appendChild(e);
			}			

			if (registrationDate != null) {
				now = registrationDate;
			} else {
				java.util.Calendar calendar = java.util.Calendar.getInstance();
				java.util.Date date = calendar.getTime();
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
				StringBuffer sb = sdf.format(date, new StringBuffer(), new java.text.FieldPosition(0));
				now = sb.toString();
			}
			e = document.createElement(REGISTRATION_DATE_TAG);
			e.appendChild(document.createTextNode(now));
			record.appendChild(e);
			
			if (rights != null) {
				e = document.createElement(RIGHTS_TAG);
				e.appendChild(document.createTextNode(rights));
				record.appendChild(e);
			}			
			
			records.appendChild(record);

			javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = tf.newTransformer();
			java.util.Properties properties = new java.util.Properties();
			properties.put("indent","yes");
			transformer.setOutputProperties(properties);
			javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(document);
			javax.xml.transform.stream.StreamResult result = 
				new javax.xml.transform.stream.StreamResult (REGISTRY_XML_FILENAME);
			transformer.transform(domSource,result);

			return new Provider(this,
								osidService,
								osidVersion,
								osidLoadKey,
								displayName,
								description,
								id,
								creator,
								publisher,
								now,
								rights);
		} catch (Throwable t) {
			log(t);
		}
		throw new edu.mit.osid.registry.RegistryException(org.osid.OsidException.OPERATION_FAILED);
	}
	
	/**
	 * Find the record element in the registry XML file whose identifier node matches the
	 * input.  Remove that record and re-save the XML.
	 */
	public void deleteProvider(org.osid.shared.Id providerId)
		throws edu.mit.osid.registry.RegistryException
	{
		if (providerId == null) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}

		java.io.InputStream istream = getClass().getClassLoader().getResourceAsStream(REGISTRY_XML_FILENAME);
		if (istream == null) {
			log(FILE_NOT_FOUND_MESSAGE + REGISTRY_XML_FILENAME);
			throw new edu.mit.osid.registry.RegistryException(org.osid.OsidException.CONFIGURATION_ERROR);
		}
		
		try {
			javax.xml.parsers.DocumentBuilderFactory dbf = null;
			javax.xml.parsers.DocumentBuilder db = null;
			org.w3c.dom.Document document = null;
			
			dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			document = db.parse(istream);

			org.w3c.dom.NodeList registryEntries = document.getElementsByTagName(REGISTRY_TAG);
			org.w3c.dom.Element registry = (org.w3c.dom.Element)registryEntries.item(0);
			
			org.w3c.dom.NodeList records = document.getElementsByTagName(PROVIDER_RECORD_TAG);
			int numRecords = records.getLength();
			for (int i=0; i < numRecords; i++) {
				org.w3c.dom.Element record = (org.w3c.dom.Element)records.item(i);
				org.w3c.dom.NodeList nodeList = record.getElementsByTagName(ID_TAG);
				int numNodes = nodeList.getLength();
				for (int k=0; k < numNodes; k++) {
					org.w3c.dom.Element e = (org.w3c.dom.Element)nodeList.item(k);
					if (e.hasChildNodes()) {
						String idString = e.getFirstChild().getNodeValue();
						org.osid.shared.Id id = getIdManager().getId(idString);
						if (id.isEqual(providerId)) {
							registry.removeChild(record);
							
							javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
							javax.xml.transform.Transformer transformer = tf.newTransformer();
							java.util.Properties properties = new java.util.Properties();
							properties.put("indent","yes");
							transformer.setOutputProperties(properties);
							javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(document);
							javax.xml.transform.stream.StreamResult result = 
								new javax.xml.transform.stream.StreamResult (REGISTRY_XML_FILENAME);
							transformer.transform(domSource,result);
							
							return;
						}
					}
				}
			}
		} catch (Throwable t) {
			log(t);
			throw new edu.mit.osid.registry.RegistryException(org.osid.OsidException.OPERATION_FAILED);
		}
		throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.UNKNOWN_ID);
	}
	
	/**
	 * Checked by the org.osid.OsidLoader.getManager() method
	 */
	public void osidVersion_2_0()
		throws edu.mit.osid.registry.RegistryException
	{
	}
 
	private void log(Throwable t)
	{
		t.printStackTrace();
	}	
	
	private void log(String message)
	{
		System.out.println(message);
	}	
}

	
