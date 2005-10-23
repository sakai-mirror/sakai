/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/osid/api-impl/src/java/org/sakaiproject/component/osid/id/IdManager.java $
 * $Id: IdManager.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
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
package org.sakaiproject.component.osid.repository.registry;

/**
 * <p> This is an implementation of the RepositoryManager interface in the Repository OSID.
 * This implementation federates across other RepositoryManagers.  These managers are found
 * in a registry.  The details of the registry are hidden within the implementation of a
 * "straw-man" Registry OSID.  That OSID's RegistryManager provides access to the individual
 * RepositoryManagers' Repositories.  In this implementation of RepositoryManager, in many cases
 * we pass the call to the registered implementations and the result or results.  </p>
 * 
 * @author Massachusetts Institute of Techbology, Sakai Software Development Team
 * @version
*/

public class RepositoryManager
implements org.osid.repository.RepositoryManager
{
    private org.osid.OsidContext context = null;
    private java.util.Properties configurationProperties = null;
	private java.util.Vector repositoryManagerVector = new java.util.Vector();
	private edu.mit.osid.registry.RegistryManager registryManager = null;
	private org.osid.id.IdManager idManager = null;
	private static final String ADUSTER_XML_FILENAME = "RepositoryOSIDAdjuster.xml";
	private static final String REGISTRY_OSID = "edu.mit.osid.registry.RegistryManager";
	private static final String REGISTRY_OSID_IMPLEMENTATION = "org.sakaiproject.component.osid.registry";
	private static final String ID_OSID ="org.osid.id.IdManager";
	private static final String ID_OSID_IMPLEMENTATION = "org.sakaiproject.component.osid.id";
	private org.w3c.dom.Document adjusterDocument = null;

	/**
		Simply return OsidContext set by assignOsidContext
	*/
    public org.osid.OsidContext getOsidContext()
    throws org.osid.repository.RepositoryException
    {
        return context;
    }

	/**
		Simply stores the OsidContext
	 */
    public void assignOsidContext(org.osid.OsidContext context)
    throws org.osid.repository.RepositoryException
    {
        this.context = context;
    }

	/**
		Simply stores the Configuration and then sets up to access implementations of the RegistryManager and an IdManager
	 */
    public void assignConfiguration(java.util.Properties configurationProperties)
    throws org.osid.repository.RepositoryException
    {
        this.configurationProperties = configurationProperties;
		
		try {
			if (this.registryManager == null) {
				this.registryManager = (edu.mit.osid.registry.RegistryManager)
                                           org.sakaiproject.component.osid.repository.registry.OsidLoader.getManager(REGISTRY_OSID,
																										 REGISTRY_OSID_IMPLEMENTATION,
																										 this.context,
																										 new java.util.Properties());
				this.idManager = (org.osid.id.IdManager)OsidLoader.getManager(ID_OSID,
																					   ID_OSID_IMPLEMENTATION,
																					   this.context,
																					   new java.util.Properties());
			}
		} catch (Throwable t) {
			t.printStackTrace();
			log(t);
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.CONFIGURATION_ERROR);
		}
		
		java.io.InputStream istream = null;
		try {
			istream = getClass().getClassLoader().getResourceAsStream(ADUSTER_XML_FILENAME);
            if (istream != null) {
				javax.xml.parsers.DocumentBuilderFactory dbf = null;
				javax.xml.parsers.DocumentBuilder db = null;
				
				dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
				db = dbf.newDocumentBuilder();
				adjusterDocument = db.parse(istream);
			}
		} catch (Throwable t) {
			// try the ../shared directory
			try {
				istream = getClass().getClassLoader().getResourceAsStream("../shared/" + ADUSTER_XML_FILENAME);
				if (istream != null) {
					javax.xml.parsers.DocumentBuilderFactory dbf = null;
					javax.xml.parsers.DocumentBuilder db = null;
					
					dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
					db = dbf.newDocumentBuilder();
					adjusterDocument = db.parse(istream);
				}				
			}
			catch (Throwable t1) {
			}
		}
    }

	/**
		Unimplemented method.  We don't know to which RepositoryManager to delegate this, so we do nothing.
	 */
    public org.osid.repository.Repository createRepository(String displayName
                                                         , String description
                                                         , org.osid.shared.Type repositoryType)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

	/**
		Unimplemented method.  We could do this by checking all managers, but this seems outside the purpose of this implementation.
	 */
    public void deleteRepository(org.osid.shared.Id repositoryId)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

	/**
		Working from the current Repository Manager Registry, this method returns the union of Repositories across registered RepositoryManagers.  Note that the contents of the Registry can change at run-time and the Repositories and their order returned by a registered RepositoryManager can change at any time.
		<p>
		If an exception is thrown by a call to a manager, the exception is logged and processing continues.
		<p>
		This method can throw org.osid.OsidException.OPERATION_FAILED
	*/
    public org.osid.repository.RepositoryIterator getRepositories()
    throws org.osid.repository.RepositoryException
    {
		try {
			// set the current list of repository managers
			refresh();
			java.util.Vector result = new java.util.Vector();
			java.util.Vector idStringVector = new java.util.Vector(); // to help check for duplicates

			// call getRepositories() for each registered manager and accumulate the results
			for (int i=0, size = this.repositoryManagerVector.size(); i < size; i++) {
				org.osid.repository.RepositoryManager repositoryManager = 
				(org.osid.repository.RepositoryManager)this.repositoryManagerVector.elementAt(i);
				try {
					org.osid.repository.RepositoryIterator repositoryIterator = repositoryManager.getRepositories();
					while (repositoryIterator.hasNextRepository()) {
						// we should check for the unlikely case of a duplicate
						org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
						String idString = nextRepository.getId().getIdString();
						if (!result.contains(idString)) {
							result.addElement(nextRepository);
							idStringVector.addElement(idString);
						}
					}
				} catch (Throwable t) {
					log(t);
				}
			}
			return new RepositoryIterator(result);
		} catch (Throwable t) {
			t.printStackTrace();
			log(t);
		}
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
    }

	/**
		Working from the current Repository Manager Registry, this method returns the union of Repositories across registered RepositoryManagers.  Note that the contents of the Registry can change at run-time and the Repositories and their order returned by a registered RepositoryManager can change at any time. 
		<p>
		If an exception is thrown by a call to a manager, the exception is logged and processing continues.  Any repository that does not support the repositoryType, should throw the exception org.osid.shared.SharedException.UNKNOWN_TYPE.
		<p>
		This method throws org.osid.shared.SharedException.NULL_ARGUMENT if the repositoryType argument is null.
		This method throws org.osid.shared.SharedException.UNKNOWN_TYPE if all managers throw this exception.
		This method can throw org.osid.OsidException.OPERATION_FAILED.
	 */
    public org.osid.repository.RepositoryIterator getRepositoriesByType(org.osid.shared.Type repositoryType)
    throws org.osid.repository.RepositoryException
    {
        if (repositoryType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.NULL_ARGUMENT);
        }
		boolean unknownTypeAlwaysThrown = true;
		try {
			// set the current list of repository managers
			refresh();
			java.util.Vector result = new java.util.Vector();
			
			// call getRepositoriesByType() for each registered manager and accumulate the results
			for (int i=0, size = this.repositoryManagerVector.size(); i < size; i++) {
				org.osid.repository.RepositoryManager repositoryManager = 
				(org.osid.repository.RepositoryManager)this.repositoryManagerVector.elementAt(i);
				try {
					org.osid.repository.RepositoryIterator repositoryIterator = repositoryManager.getRepositoriesByType(repositoryType);
					while (repositoryIterator.hasNextRepository()) {
						result.addElement(repositoryIterator.nextRepository());
					}
					unknownTypeAlwaysThrown = false;
				} catch (Throwable t) {
					unknownTypeAlwaysThrown = unknownTypeAlwaysThrown && (t.getMessage() == org.osid.shared.SharedException.UNKNOWN_TYPE);
					log(t);
				}
			}
			return new RepositoryIterator(result);
		} catch (Throwable t) {
			log(t);
			unknownTypeAlwaysThrown = false;
		}
		if (unknownTypeAlwaysThrown) {
			throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_TYPE);
		}
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
    }

	/**
		Working from the current Repository Manager Registry, this method checks the repository id provided against the known repositories ids for a match.
		<p>
		This implementation assumes repository ids are unique and returns the first repository whose id matches the criterion.
		<p>
		If an exception is thrown by a call to a manager, the exception is logged and processing continues.  Any repository that does not recognize the repository id, should throw the exception org.osid.shared.SharedException.UNKNOWN_ID.
		This method throws org.osid.shared.SharedException.NULL_ARGUMENT if the repositoryId argument is null.
		This method throws org.osid.shared.SharedException.UNKNOWN_ID if all managers throw this exception.
		This method can throw org.osid.OsidException.OPERATION_FAILED.
	 */
    public org.osid.repository.Repository getRepository(org.osid.shared.Id repositoryId)
    throws org.osid.repository.RepositoryException
    {
        if (repositoryId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.NULL_ARGUMENT);
        }
		boolean unknownIdAlwaysThrown = true;
		try {
			// set the current list of repository managers
			refresh();
			
			// call getRepository() for each registered manager and return the first match
			for (int i=0, size = this.repositoryManagerVector.size(); i < size; i++) {
				org.osid.repository.RepositoryManager repositoryManager = 
				(org.osid.repository.RepositoryManager)this.repositoryManagerVector.elementAt(i);
				try {
					return repositoryManager.getRepository(repositoryId);
				} catch (Throwable t) {
					unknownIdAlwaysThrown = unknownIdAlwaysThrown && (t.getMessage() == org.osid.shared.SharedException.UNKNOWN_ID);
				}
			}
		} catch (Throwable t) {
			log(t);
			unknownIdAlwaysThrown = false;
		}
		if (unknownIdAlwaysThrown) {
			throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
		}
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
    }

	/**
		Working from the current Repository Manager Registry, this method asks each manager to get the asset.
		<p>
		If an exception is thrown by a call to a manager, the exception is logged and processing continues.  Any manager that does not recognize the asset id, should throw the exception org.osid.shared.SharedException.UNKNOWN_ID.
		This method throws org.osid.shared.SharedException.NULL_ARGUMENT if the assetId argument is null.
		This method throws org.osid.shared.SharedException.UNKNOWN_ID if all managers throw this exception.
		This method can throw org.osid.OsidException.OPERATION_FAILED.
	*/
    public org.osid.repository.Asset getAsset(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.NULL_ARGUMENT);
        }
		boolean unknownIdAlwaysThrown = true;
		try {
			// set the current list of repository managers
			refresh();
			
			// call getAsset() for each registered manager and return the first match
			for (int i=0, size = this.repositoryManagerVector.size(); i < size; i++) {
				org.osid.repository.RepositoryManager repositoryManager = 
				(org.osid.repository.RepositoryManager)this.repositoryManagerVector.elementAt(i);
				try {
					return repositoryManager.getAsset(assetId);
				} catch (Throwable t) {
					unknownIdAlwaysThrown = unknownIdAlwaysThrown && (t.getMessage() == org.osid.shared.SharedException.UNKNOWN_ID);
				}
			}
		} catch (Throwable t) {
			log(t);
			unknownIdAlwaysThrown = false;
		}
		if (unknownIdAlwaysThrown) {
			throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
		}
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
    }

	/**
		Working from the current Repository Manager Registry, this method asks each manager to get the asset.
		<p>
		If an exception is thrown by a call to a manager, the exception is logged and processing continues.  Any manager that does not recognize the asset id, should throw the exception org.osid.shared.SharedException.UNKNOWN_ID.
		This method throws org.osid.shared.SharedException.NULL_ARGUMENT if the assetId argument is null.
		This method throws org.osid.shared.SharedException.UNKNOWN_ID if all managers throw this exception.
		This method can throw org.osid.OsidException.OPERATION_FAILED.
	 */
    public org.osid.repository.Asset getAssetByDate(org.osid.shared.Id assetId
                                                  , long date)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.NULL_ARGUMENT);
        }
		boolean unknownIdAlwaysThrown = true;
		try {
			// set the current list of repository managers
			refresh();
			
			// call getAssetByDate() for each registered manager and return the first match
			for (int i=0, size = this.repositoryManagerVector.size(); i < size; i++) {
				org.osid.repository.RepositoryManager repositoryManager = 
				(org.osid.repository.RepositoryManager)this.repositoryManagerVector.elementAt(i);
				try {
					return repositoryManager.getAssetByDate(assetId, date);
				} catch (Throwable t) {
					unknownIdAlwaysThrown = unknownIdAlwaysThrown && (t.getMessage() == org.osid.shared.SharedException.UNKNOWN_ID);
				}
			}
		} catch (Throwable t) {
			log(t);
			unknownIdAlwaysThrown = false;
		}
		if (unknownIdAlwaysThrown) {
			throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
		}
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
    }

	/**
		Working from the current Repository Manager Registry, this method asks each manager to get the asset's dates.
		<p>
		If an exception is thrown by a call to a manager, the exception is logged and processing continues.  Any manager that does not recognize the asset id, should throw the exception org.osid.shared.SharedException.UNKNOWN_ID.
		This method throws org.osid.shared.SharedException.NULL_ARGUMENT if the repositories argument is null.
		This method throws org.osid.shared.SharedException.UNKNOWN_ID if all managers throw this exception.
		This method can throw org.osid.OsidException.OPERATION_FAILED.
	 */
    public org.osid.shared.LongValueIterator getAssetDates(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.NULL_ARGUMENT);
        }
		boolean unknownIdAlwaysThrown = true;
		try {
			// set the current list of repository managers
			refresh();
			java.util.Vector result = new java.util.Vector();
			
			// call getAssetDates() for each registered manager and return the first match
			for (int i=0, size = this.repositoryManagerVector.size(); i < size; i++) {
				org.osid.repository.RepositoryManager repositoryManager = 
				(org.osid.repository.RepositoryManager)this.repositoryManagerVector.elementAt(i);
				try {
					org.osid.shared.LongValueIterator longValueIterator = repositoryManager.getAssetDates(assetId);
					while (longValueIterator.hasNextLongValue()) {
						result.addElement(new Long(longValueIterator.nextLongValue()));
					}
					unknownIdAlwaysThrown = false;
				} catch (Throwable t) {
					log(t);
					unknownIdAlwaysThrown = unknownIdAlwaysThrown && (t.getMessage() == org.osid.shared.SharedException.UNKNOWN_ID);
				}
			}
			return new LongValueIterator(result);
		} catch (Throwable t) {
			log(t);
			unknownIdAlwaysThrown = false;
		}
		if (unknownIdAlwaysThrown) {
			throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
		}
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
    }

	/**
		Working from the current Repository Manager Registry, this method asks each manager to search for the assets.
		<p>
		If an exception is thrown by a call to a manager, the exception is logged and processing continues.
		This method throws org.osid.shared.SharedException.NULL_ARGUMENT if the repositories argument is null.
		This method can throw org.osid.OsidException.OPERATION_FAILED.
	 */
    public org.osid.repository.AssetIterator getAssetsBySearch(org.osid.repository.Repository[] repositories
                                                             , java.io.Serializable searchCriteria
                                                             , org.osid.shared.Type searchType
                                                             , org.osid.shared.Properties searchProperties)
    throws org.osid.repository.RepositoryException
    {
        if (repositories == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.NULL_ARGUMENT);
        }

		try {
			// set the current list of repository managers
			refresh();
			java.util.Vector result = new java.util.Vector();
			
			/*
			 Call getAssetsBySearch() for each repository.  Since the list of Repositories may span Managers, check all
			 Managers for the Repository.
			 */
			
			int numManagers = this.repositoryManagerVector.size();
			// for each Repository
			for (int i = 0; i < repositories.length; i++) {
				// get its id
				org.osid.shared.Id repositoryId = repositories[i].getId();
				boolean found = false;
				
				// look in each Manager for the Repository
				for (int j=0; j < numManagers; j++) {
					org.osid.repository.RepositoryManager repositoryManager = 
					(org.osid.repository.RepositoryManager)this.repositoryManagerVector.elementAt(j);
					
					try {
						org.osid.repository.Repository repository = repositoryManager.getRepository(repositoryId);
						
						// we have found the Repository, now call the search
						found = true;
						try {
							
							java.io.Serializable adjustedSearchCriteria = null;
							org.osid.shared.Type adjustedSearchType = null;
							org.osid.shared.Properties adjustedSearchProperties = null;
							
							// check there is no class that implements the type or criteria adjuster interface
							if (this.adjusterDocument != null) {
								try {
									// find the adjuster class for this reposiory id, if there is one
									org.w3c.dom.NodeList nodeList = this.adjusterDocument.getElementsByTagName("repository");
									int numNodes = nodeList.getLength();
									for (int n=0; n < numNodes; n++) {
										org.w3c.dom.Element node = (org.w3c.dom.Element)nodeList.item(n);
										String idString = node.getAttribute("id");
										org.osid.shared.Id id = idManager.getId(idString);
										if (id.isEqual(repositoryId)) {
											String adjusterClassName = node.getAttribute("adjusterclass");
											if (adjusterClassName.length() > 0) {
												
												try {
													// instantiate the class and get the adjuster arguments
													Class adjusterClass = Class.forName(adjusterClassName);
													edu.mit.osid.repository.SearchArgumentAdjuster adjuster = (edu.mit.osid.repository.SearchArgumentAdjuster)(adjusterClass.newInstance());
													adjuster.adjust(searchCriteria,
																	searchType,
																	searchProperties);
													adjustedSearchCriteria = adjuster.getSearchCriteria();
													adjustedSearchType = adjuster.getSearchType();
													adjustedSearchProperties = adjuster.getSearchProperties();
												} catch (Throwable t) {
													// just log
													log(t);
												}
											}
										}
									}
								} catch (Throwable t) {
									// just log
									log(t);
								}
							}
							
							// perform the search
							System.out.println("Now searching " + 
											   repository.getDisplayName() +
											   " with criteria " +
											   ((adjustedSearchCriteria != null) ? adjustedSearchCriteria : searchCriteria));
							org.osid.repository.AssetIterator assetIterator = repository.getAssetsBySearch((adjustedSearchCriteria != null) ? adjustedSearchCriteria : searchCriteria,
																										   (adjustedSearchType != null) ? adjustedSearchType : searchType,
																										   (adjustedSearchProperties != null) ? adjustedSearchProperties : searchProperties);
							// accumulate in the result set
							while (assetIterator.hasNextAsset()) {
								result.addElement(assetIterator.nextAsset());
							}
						} catch (Throwable t) {
							// this could be a Type mismatch or other non-fatal issue
						}
						break;
					} catch (Throwable t) {
						// this could be a repository not found
					}					
				}
				if (!found) {
					throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
				}				
			}
			return new AssetIterator(result);
		} catch (Throwable t) {
			log(t);
			if (t.getMessage().equals(org.osid.shared.SharedException.UNKNOWN_ID)) {
				throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
			}
		}
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
	}
	
	/**
		Unimplemented method
	 */
    public org.osid.shared.Id copyAsset(org.osid.repository.Repository repository
                                      , org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

	/**
		Working from the current Repository Manager Registry, this method asks each manager to get all its repository types.  The method returns distinct values -- no duplicates.
		<p>
		If an exception is thrown by a call to a manager, the exception is logged and processing continues.
		This method can throw org.osid.OsidException.OPERATION_FAILED.
	 */
    public org.osid.shared.TypeIterator getRepositoryTypes()
    throws org.osid.repository.RepositoryException
    {
		try {
			// set the current list of repository managers
			refresh();
			java.util.Vector result = new java.util.Vector();
			
			// call getRepositoryTypes() for each registered manager and accumulate the results
			for (int i=0, size = this.repositoryManagerVector.size(); i < size; i++) {
				org.osid.repository.RepositoryManager repositoryManager = 
				(org.osid.repository.RepositoryManager)this.repositoryManagerVector.elementAt(i);
				try {
					org.osid.shared.TypeIterator typeIterator = repositoryManager.getRepositoryTypes();
					while (typeIterator.hasNextType()) {
						org.osid.shared.Type nextType = typeIterator.nextType();
						// do not add in duplicates; note we do not simply check if the type is in the vector since
						// equality is defined by the OSIDType class distributed by O.K.I.
						boolean found = false;
						for (int j=0, numTypes = result.size(); j < numTypes; j++) {
							if (nextType.isEqual( (org.osid.shared.Type)result.elementAt(j) )) {
								found = true;
								break;
							}
						}
						if (!found) {
							result.addElement(nextType);
						}
					}
				} catch (Throwable t) {
					log(t);
				}
			}
			return new TypeIterator(result);
		} catch (Throwable t) {
			log(t);
		}
		throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
    }

	/*
	 * A required empty implementation called by the default OsidLoader.
	 */
    public void osidVersion_2_0()
    throws org.osid.repository.RepositoryException
    {
    }
	
	// private utility methods
	
	private void refresh()
		throws org.osid.repository.RepositoryException
	{
		try {
			this.repositoryManagerVector.removeAllElements();
			edu.mit.osid.registry.ProviderIterator providerIterator = this.registryManager.getProviders();
			while (providerIterator.hasNextProvider()) {
				edu.mit.osid.registry.Provider provider = providerIterator.nextProvider();
				
				String service = provider.getOsidService();
				String version = provider.getOsidVersion();
				String loadkey = provider.getOsidLoadKey();
				
				if ( (service.equals("org.osid.repository")) && (version.equals("2.0")) ) {
					try {
						this.repositoryManagerVector.addElement(org.osid.OsidLoader.getManager("org.osid.repository.RepositoryManager",
																								loadkey,
																								this.context,
																								new java.util.Properties()));
					} catch (Throwable t) {
						log(t);
						log("OSID Service: " + service);
						log("OSID Version: " + version);
						log("OSID LoadKey: " + loadkey);
						t.printStackTrace();
						// continue trying other managers
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
			log(t);
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
		}
	}
	
	private void log(Throwable t)
	{
		System.out.println(t.getMessage());
	}

	private void log(String entry)
	{
		System.out.println(entry);
	}	
}
