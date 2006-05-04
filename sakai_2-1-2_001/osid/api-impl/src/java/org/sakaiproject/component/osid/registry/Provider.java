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
 * Provider implements a "straw-man" registry interface.  Each RepositoryManager
 * is a separate Provider.  RepositoryManagers are the top-level object in the
 * O.K.I. Repository OSID.  Providers have several attributes.
 * </p>
 * 
 * @author Massachusetts Institute of Technology
 */
public class Provider implements edu.mit.osid.registry.Provider
{
	private edu.mit.osid.registry.RegistryManager registryManager = null;
	private String osidService = null;
	private String osidVersion = null;
	private String osidLoadKey = null;				
	private String displayName = null;
	private String description = null;
	private org.osid.shared.Id id = null;
	private String creator = null;
	private String publisher = null;
	private String registrationDate = null;
	private String rights = null;
				
	/**
	 * Store away the input arguments for use by accessors.
	 */
	protected Provider(edu.mit.osid.registry.RegistryManager registryManager,
					   String osidService,
					   String osidVersion,
					   String osidLoadKey,
					   String displayName,
					   String description,
					   org.osid.shared.Id id,
					   String creator,
					   String publisher,
					   String registrationDate,
					   String rights)
	{
		this.registryManager = registryManager;
		this.osidService = osidService;
		this.osidVersion = osidVersion;
		this.osidLoadKey = osidLoadKey;
		this.displayName = displayName;
		this.description = description;
		this.id = id;
		this.creator = creator;
		this.publisher = publisher;
		this.registrationDate = registrationDate;
		this.rights = rights;
	}
	
	/**
	 * The OSID Service is the package name defined by O.K.I.
	 * An example is org.osid.repository
	 * Get the value stored at construction or during an update.
	 */
	public String getOsidService()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.osidService;
	}

	/**
	 * The OSID Service is the package name defined by O.K.I.
	 * An example is org.osid.repositor
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateOsidService(String osidService)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (osidService == null) || (osidService.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.osidService;
		this.osidService = osidService;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.osidService = undo;
		}
	}

	/**
	 * The version of the OSID -- not the version of the implementation.  An example is 2.0.
	 * Get the value stored at construction or during an update.	 
	 */
	public String getOsidVersion()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.osidVersion;
	}

	/**
	 * The version of the OSID -- not the version of the implementation.  An example is 2.0.
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateOsidVersion(String osidVersion)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (osidVersion == null) || (osidVersion.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.osidVersion;
		this.osidVersion = osidVersion;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.osidVersion = undo;
		}
	}

	/**
	 * The information a loader / factory would need to return an instance of an implementation.
	 * An example is edu.mit.osidimpl.repository.xyz
	 * Get the value stored at construction or during an update.
	 */
	public String getOsidLoadKey()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.osidLoadKey;
	}

	/**
	 * The information a loader / factory would need to return an instance of an implementation.
	 * An example is edu.mit.osidimpl.repository.xyz
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateOsidLoadKey(String osidLoadKey)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (osidLoadKey == null) || (osidLoadKey.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.osidLoadKey;
		this.osidLoadKey = osidLoadKey;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.osidLoadKey = undo;
		}
	}

	/**
	 * A short name for this implementation suitable for display in a picklist.
	 * Get the value stored at construction or during an update.
	 */
	public String getDisplayName()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.displayName;
	}

	/**
	 * A short name for this implementation suitable for display in a picklist.
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateDisplayName(String displayName)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (displayName == null) || (displayName.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.displayName;
		this.displayName = displayName;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.displayName = undo;
		}
	}

	/**
	 * Longer and more informative text than the display name.
	 * Get the value stored at construction or during an update.
	 */
	public String getDescription()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.description;
	}

	/**
	 * Longer and more informative text than the display name.
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateDescription(String description)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (description == null) || (description.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.description;
		this.description = description;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.description = undo;
		}
	}

	/**
	 * A unique identifier for the Repository.
	 * Get the value stored at construction or during an update.
	 */
	public org.osid.shared.Id getId()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.id;
	}

	/**
	 * A unique identifier for the Repository.
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateId(org.osid.shared.Id id)
	throws edu.mit.osid.registry.RegistryException
	{
		if (id == null) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		org.osid.shared.Id undo = this.id;
		this.id = id;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.id = undo;
		}
	}

	/**
	 * The author of the implementation.
	 * Get the value stored at construction or during an update.
	 */
	public String getCreator()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.creator;
	}

	/**
	 * The author of the implementation.
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateCreator(String creator)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (creator == null) || (creator.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.creator;
		this.creator = creator;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.creator = undo;
		}
	}
	
	/**
	 * The institution that developed or is providing the implementation.
	 * Get the value stored at construction or during an update.
	 */
	public String getPublisher()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.publisher;
	}
	
	/**
	 * The institution that developed or is providing the implementation.
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updatePublisher(String publisher)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (publisher == null) || (publisher.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.publisher;
		this.publisher = publisher;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.publisher = undo;
		}
	}
	
	/**
	 * The timestamp for this registration.  This is set by the RegistryManager.createProvider()
	 * method.  Consumers of the Registry may use this date to determine which of two versions
	 * of a Provider is more recent.
	 * Get the value stored at construction or during an update.
	 */
	public String getRegistrationDate()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.registrationDate;
	}
	
	/**
	 * The timestamp for this registration.  This is set by the RegistryManager.createProvider()
	 * method.  Consumers of the Registry may use this date to determine which of two versions
	 * of a Provider is more recent.  Call this method with care.  Letting createProvider()
	 * populate this value may be safer.
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateRegistrationDate(String registrationDate)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (registrationDate == null) || (registrationDate.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.registrationDate;
		this.registrationDate = registrationDate;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.registrationDate = undo;
		}		
	}

	/**
	 * A description of how the Repository may be used.  Note there is no digital rights enforcement
	 * in the Registry, so this is descriptive only.
	 * Get the value stored at construction or during an update.
	 */
	public String getRights()
	throws edu.mit.osid.registry.RegistryException
	{
		return this.rights;
	}

	/**
	 * A description of how the Repository may be used.  Note there is no digital rights enforcement
	 * in the Registry, so this is descriptive only.
	 * Update the value stored at construction or during an earlier update.
	 * This change is immediately written to the registry.
	 */
	public void updateRights(String rights)
	throws edu.mit.osid.registry.RegistryException
	{
		if ( (rights == null) || (rights.length() == 0) ) {
			throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NULL_ARGUMENT);
		}
		
		String undo = this.rights;
		this.rights = rights;
		try {
			syncWithXML();
		} catch (Throwable t) {
			log(t);
			this.rights = undo;
		}
	}

	private void log(Throwable t)
	{
		t.printStackTrace();
	}
	
	/*
	 * Update the XML by deleting and then creating (with updated data) this provider.  If the update
	 * fails, the data will be out-of-sync.
	*/
	private void syncWithXML()
		throws edu.mit.osid.registry.RegistryException
	{
		this.registryManager.deleteProvider(this.id);
		this.registryManager.createProvider(osidService,
											osidVersion,
											osidLoadKey,
											this.displayName,
											this.description,
											this.id,
											this.creator,
											this.publisher,
											this.registrationDate,
											this.rights);
	}
}