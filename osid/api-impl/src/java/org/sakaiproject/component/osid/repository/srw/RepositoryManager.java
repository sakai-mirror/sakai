package org.sakaiproject.component.osid.repository.srw;

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
/**
 * @author Massachusetts Institute of Techbology, Sakai Software Development Team
 * @version
 */
public class RepositoryManager
implements org.osid.repository.RepositoryManager
{
    private org.osid.repository.Repository repository = null;
    private org.osid.id.IdManager idManager = null;
    private org.osid.logging.LoggingManager loggingManager = null;
    private org.osid.logging.WritableLog log = null;
    private org.osid.OsidContext context = null;
    private java.util.Properties configuration = null;
    private java.util.Vector repositoryVector = new java.util.Vector();
    private java.util.Vector searchTypeVector = new java.util.Vector();

    public org.osid.OsidContext getOsidContext()
    throws org.osid.repository.RepositoryException
    {
        return context;
    }

    private void log(String entry)
    throws org.osid.repository.RepositoryException
    {
        if (log != null)
        {
            try
            {
                log.appendLog(entry);
            }
            catch (org.osid.logging.LoggingException lex) 
            {
                // swallow exception since logging is a best attempt to log an exception anyway
            }   
        }
    }

    public void assignOsidContext(org.osid.OsidContext context)
    throws org.osid.repository.RepositoryException
    {
        this.context = context;
    }

    public void assignConfiguration(java.util.Properties configuration)
    throws org.osid.repository.RepositoryException
    {
        this.configuration = configuration;
		//System.out.println(this+" Configuration = "+configuration);
        try
        {
            String idImplementation = (String)configuration.get("osid_20_Id_Implementation");
            String loggingImplementation = (String)configuration.get("osid_20_Logging_Implementation");  
            String logFilename = (String)configuration.get("log_filename");
            String loggingFormatTypeDomain = (String)configuration.get("loggingFormatType_domain");
            String loggingFormatTypeAuthority = (String)configuration.get("loggingFormatType_authority");
            String loggingFormatTypeKeyword = (String)configuration.get("loggingFormatType_keyword");
            String loggingPriorityTypeDomain = (String)configuration.get("loggingPriorityType_domain");
            String loggingPriorityTypeAuthority = (String)configuration.get("loggingPriorityType_authority");
            String loggingPriorityTypeKeyword = (String)configuration.get("loggingPriorityType_keyword");
            
            if ( (logFilename == null) || (loggingFormatTypeDomain == null) ||
                 (loggingFormatTypeAuthority == null) || (loggingFormatTypeKeyword == null) ||
                 (loggingPriorityTypeDomain == null) || (loggingPriorityTypeAuthority == null) || 
                 (loggingPriorityTypeKeyword == null) )                  
            {
                throw new org.osid.repository.RepositoryException(
                    org.osid.OsidException.CONFIGURATION_ERROR);
            }
            else
            {
                loggingManager = (org.osid.logging.LoggingManager)org.sakaiproject.component.osid.loader.OsidLoader.getManager(
                    "org.osid.logging.LoggingManager",
                    loggingImplementation,
                    this.context,
                    new java.util.Properties());
                try
                {
                    log = loggingManager.getLogForWriting(logFilename);
                }
                catch (org.osid.logging.LoggingException lex)
                {
                    log = loggingManager.createLog(logFilename);
                }
                log.assignFormatType(new Type(
                    loggingFormatTypeAuthority,loggingFormatTypeDomain,loggingFormatTypeKeyword,""));
                log.assignPriorityType(new Type(
                    loggingPriorityTypeAuthority,loggingPriorityTypeDomain,loggingPriorityTypeKeyword,""));
            }
            
            if (idImplementation == null)
            {
                log("no Id Implementation configuration");
                throw new org.osid.repository.RepositoryException(org.osid.OsidException.CONFIGURATION_ERROR);
            }
            this.idManager = (org.osid.id.IdManager)org.sakaiproject.component.osid.loader.OsidLoader.getManager(
                "org.osid.id.IdManager",
                idImplementation,
                this.context,
                new java.util.Properties());
			
			Managers.setIdManager(this.idManager);

            // load repositories from configuration
            int repositoryNum = 0;
            String displayName = null;
            String description = null;
            String id = null;
            
            while ( (displayName = (String)configuration.get("repository_" + repositoryNum + "_displayName")) != null )
            {
                description = (String)configuration.get("repository_" + repositoryNum + "_description");
                id = (String)configuration.get("repository_" + repositoryNum + "_id");

                int searchTypeNum = 0;
                java.util.Vector searchTypeVector = new java.util.Vector();
                java.util.Vector searchQueryVector = new java.util.Vector();
                
                String authority = null;
                String domain = null;
                String keyword = null;
                String typeDescription = null;

                while ( (authority = (String)configuration.get("repository_" + repositoryNum + "_searchtype_" + searchTypeNum + "_authority")) != null )
                {
                    domain = (String)configuration.get("repository_" + repositoryNum + "_searchtype_" + searchTypeNum + "_domain");
                    keyword = (String)configuration.get("repository_" + repositoryNum + "_searchtype_" + searchTypeNum + "_keyword");
                    typeDescription = (String)configuration.get("repository_" + repositoryNum + "_searchtype_" + searchTypeNum + "_typeDescription");
                    searchQueryVector.addElement((String)configuration.get("repository_" + repositoryNum + "_searchtype_" + searchTypeNum + "_query"));
                    searchTypeVector.addElement(new Type(authority,domain,keyword,typeDescription));
                    searchTypeNum++;
                }
                    
                this.repositoryVector.addElement(new Repository(displayName, 
                                                                description, 
                                                                id, 
                                                                searchTypeVector, 
                                                                searchQueryVector, 
                                                                this.idManager, 
                                                                this.log));
                repositoryNum++;
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            log(t.getMessage());
            if (t instanceof org.osid.repository.RepositoryException)
            {
                throw new org.osid.repository.RepositoryException(t.getMessage());
            }
            else
            {                
                throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
            }
        }                
    }

    public org.osid.repository.Repository createRepository(String displayName
                                                         , String description
                                                         , org.osid.shared.Type repositoryType)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public void deleteRepository(org.osid.shared.Id repositoryId)
    throws org.osid.repository.RepositoryException
    {
        if (repositoryId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.RepositoryIterator getRepositories()
    throws org.osid.repository.RepositoryException
    {
        return new RepositoryIterator(this.repositoryVector);
    }

    public org.osid.repository.RepositoryIterator getRepositoriesByType(org.osid.shared.Type repositoryType)
    throws org.osid.repository.RepositoryException
    {
        if (repositoryType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        java.util.Vector result = new java.util.Vector();
        org.osid.repository.RepositoryIterator repositoryIterator = getRepositories();
        while (repositoryIterator.hasNextRepository())
        {
            org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
            if (nextRepository.getType().isEqual(repositoryType))
            {
                result.addElement(nextRepository);
            }
        }
        return new RepositoryIterator(result);
    }

    public org.osid.repository.Repository getRepository(org.osid.shared.Id repositoryId)
    throws org.osid.repository.RepositoryException
    {
        if (repositoryId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        try
        {
            org.osid.repository.RepositoryIterator repositoryIterator = getRepositories();
            while (repositoryIterator.hasNextRepository())
            {
                org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
                if (nextRepository.getId().isEqual(repositoryId))
                {
                    return nextRepository;
                }
            }
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
    }

    public org.osid.repository.Asset getAsset(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        try
        {
            org.osid.repository.RepositoryIterator repositoryIterator = getRepositories();
            while (repositoryIterator.hasNextRepository())
            {
                org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
                try
                {
                    org.osid.repository.Asset asset = nextRepository.getAsset(assetId);
                    return asset;
                }
                catch (Throwable t) {}
            }
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
        throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
    }

    public org.osid.repository.Asset getAssetByDate(org.osid.shared.Id assetId
                                                  , long date)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        try
        {
            org.osid.repository.RepositoryIterator repositoryIterator = getRepositories();
            while (repositoryIterator.hasNextRepository())
            {
                org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
                try
                {
                    org.osid.repository.Asset asset = nextRepository.getAssetByDate(assetId,date);
                    return asset;
                }
                catch (Throwable t) {}
            }
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
        throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_ID);
    }

    public org.osid.shared.LongValueIterator getAssetDates(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        java.util.Vector result = new java.util.Vector();
        try
        {
            org.osid.repository.RepositoryIterator repositoryIterator = getRepositories();
            while (repositoryIterator.hasNextRepository())
            {
                org.osid.repository.Repository nextRepository = repositoryIterator.nextRepository();
                org.osid.shared.LongValueIterator longValueIterator = repository.getAssetDates(assetId);
                while (longValueIterator.hasNextLongValue())
                {
                    result.addElement(new Long(longValueIterator.nextLongValue()));
                }
            }
            return new LongValueIterator(result);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
    }

    public org.osid.repository.AssetIterator getAssetsBySearch(org.osid.repository.Repository[] repositories
                                                             , java.io.Serializable searchCriteria
                                                             , org.osid.shared.Type searchType
                                                             , org.osid.shared.Properties searchProperties)
    throws org.osid.repository.RepositoryException
    {
        if (repositories == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        try
        {
            java.util.Vector results = new java.util.Vector();
            for (int j=0; j < repositories.length; j++)
            {
                org.osid.repository.Repository nextRepository = repositories[j];
                //optionally add a separate thread here
                try
                {
                    org.osid.repository.AssetIterator assetIterator =
                        nextRepository.getAssetsBySearch(searchCriteria,searchType,searchProperties);
                    while (assetIterator.hasNextAsset())
                    {
                        results.addElement(assetIterator.nextAsset());
                    }
                }
                catch (Throwable t) 
                {
                    // log exceptions but don't stop searching
                    log(t.getMessage());
                }
            }
            return new AssetIterator(results);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
    }

    public org.osid.shared.Id copyAsset(org.osid.repository.Repository repository
                                      , org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if ((repository == null) || (assetId == null))
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.shared.TypeIterator getRepositoryTypes()
    throws org.osid.repository.RepositoryException
    {
        java.util.Vector results = new java.util.Vector();
        try
        {
            results.addElement(new Type("mit.edu","repository","library_content"));
            return new TypeIterator(results);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
    }

    public void osidVersion_2_0()
    throws org.osid.repository.RepositoryException
    {
    }
}
