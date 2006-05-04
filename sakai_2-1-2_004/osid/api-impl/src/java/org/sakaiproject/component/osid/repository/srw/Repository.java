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
public class Repository
implements org.osid.repository.Repository
{
    private java.util.Vector assetVector = new java.util.Vector();
    private org.osid.id.IdManager idManager = null;
    private org.osid.logging.WritableLog log = null;
    private org.osid.shared.Id id = null;
    private String idString = null;
    private String displayName = null;
    private String description = null;
    private String url = null;
    private org.osid.shared.Type repositoryType = new Type("mit.edu","repository","library_content");
    private org.osid.shared.Type assetType = new Type("mit.edu","asset","library_content");
    private java.util.Vector searchTypeVector = new java.util.Vector();
    private java.util.Vector searchQueryVector = new java.util.Vector();

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

    protected Repository(String displayName
                       , String description
                       , String idString
                       , java.util.Vector searchTypeVector
                       , java.util.Vector searchQueryVector
                       , org.osid.id.IdManager idManager
                       , org.osid.logging.WritableLog log)
    throws org.osid.repository.RepositoryException
    {
        this.displayName = displayName;
        this.description = description;
        this.repositoryType = new Type("mit.edu","repository","library_content");
        this.idString = idString;
        this.searchTypeVector = searchTypeVector;
        this.searchQueryVector = searchQueryVector;
        this.idManager = idManager;
        this.log = log;
        try
        {
            this.id = idManager.getId(this.idString);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
        }
    }

    public String getDisplayName()
    throws org.osid.repository.RepositoryException
    {
        return this.displayName;
    }

    public void updateDisplayName(String displayName)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public String getDescription()
    throws org.osid.repository.RepositoryException
    {
        return this.description;
    }

    public void updateDescription(String description)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.shared.Id getId()
    throws org.osid.repository.RepositoryException
    {
        return this.id;
    }

    public org.osid.shared.Type getType()
    throws org.osid.repository.RepositoryException
    {
        return this.repositoryType;
    }

    public org.osid.repository.Asset createAsset(String displayName
                                               , String description
                                               , org.osid.shared.Type assetType)
    throws org.osid.repository.RepositoryException
    {
        if ( (displayName == null ) || (description == null) || (assetType == null) )
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        if (!assetType.isEqual(this.assetType))
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_TYPE);
        }
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public void deleteAsset(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.AssetIterator getAssets()
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.AssetIterator getAssetsByType(org.osid.shared.Type assetType)
    throws org.osid.repository.RepositoryException
    {
        if (assetType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.shared.TypeIterator getAssetTypes()
    throws org.osid.repository.RepositoryException
    {
        java.util.Vector results = new java.util.Vector();
        try
        {
            results.addElement(this.assetType);
            return new TypeIterator(results);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
    }

    public org.osid.repository.RecordStructureIterator getRecordStructures()
    throws org.osid.repository.RepositoryException
    {
        java.util.Vector results = new java.util.Vector();
        results.addElement(RecordStructure.getInstance());
        return new RecordStructureIterator(results);
    }

    public org.osid.repository.RecordStructureIterator getMandatoryRecordStructures(org.osid.shared.Type assetType)
    throws org.osid.repository.RepositoryException
    {
        if (assetType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        if (assetType.isEqual(this.assetType))
        {
            java.util.Vector results = new java.util.Vector();
            results.addElement(RecordStructure.getInstance());
            return new RecordStructureIterator(results);
        }
        throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_TYPE);
    }

    public org.osid.shared.TypeIterator getSearchTypes()
    throws org.osid.repository.RepositoryException
    {
        java.util.Vector results = new java.util.Vector();
        try
        {
            return new TypeIterator(this.searchTypeVector);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
    }

    public org.osid.shared.TypeIterator getStatusTypes()
    throws org.osid.repository.RepositoryException
    {
        java.util.Vector results = new java.util.Vector();
        try
        {
            results.addElement(new Type("mit.edu","asset","valid"));
            return new TypeIterator(results);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
    }

    public org.osid.shared.Type getStatus(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        return new Type("mit.edu","asset","valid");
    }

    public boolean validateAsset(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        return true;
    }

    public void invalidateAsset(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.Asset getAsset(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.Asset getAssetByDate(org.osid.shared.Id assetId
                                                  , long date)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.shared.LongValueIterator getAssetDates(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.AssetIterator getAssetsBySearch(java.io.Serializable searchCriteria
                                                             , org.osid.shared.Type searchType
                                                             , org.osid.shared.Properties searchProperties)
    throws org.osid.repository.RepositoryException
    {
        if (searchCriteria == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        if (searchType == null) 
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        if (!(searchCriteria instanceof String))
        {
            // maybe change this to a new exception message
            log("invalid criteria");
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }

        java.util.Vector results = new java.util.Vector();
        boolean knownType = false;
        try
        {
            String criteria = (String)searchCriteria;
            for (int searchTypeNum = 0, size = this.searchTypeVector.size(); searchTypeNum < size; searchTypeNum++)
            {
                org.osid.shared.Type type = (org.osid.shared.Type)(this.searchTypeVector.elementAt(searchTypeNum));

//System.out.println("searchTypeNum " + searchTypeNum);
//System.out.println("search type " + searchType.getAuthority() + " / " + searchType.getDomain() + " / " + searchType.getKeyword());
//System.out.println("type " + type.getAuthority() + " / " + type.getDomain() + " / " + type.getKeyword());

                if (type.isEqual(searchType))
                {
                    knownType = true;
                    String query = (String)this.searchQueryVector.elementAt(searchTypeNum);
					query = query.replaceAll("CRITERIA",criteria);
                    System.out.println("SRW Query: " + query);
					
                    java.net.URL url = new java.net.URL(query);
                    java.net.URLConnection connection = url.openConnection();
                    java.net.HttpURLConnection http = (java.net.HttpURLConnection)connection;
                    java.io.InputStreamReader in = new java.io.InputStreamReader(http.getInputStream());

                    StringBuffer xml = new StringBuffer();
                    try
                    {
                        int i = 0;
                        while ( (i = in.read()) != -1 )
                        {
                            xml.append(Character.toString((char)i));
                        }
                    }
                    catch (Throwable t) {}
                    //System.out.println("xml " + xml);

					try
					{
						return new AssetIterator(xml,this.log,this.id);
					}
					catch (Throwable t)
					{
						// just ignore
					}
                }
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
        if (!knownType)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_TYPE);
        }
        return new AssetIterator(results);
    }

    public org.osid.shared.Id copyAsset(org.osid.repository.Asset asset)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.RecordStructureIterator getRecordStructuresByType(org.osid.shared.Type recordStructureType)
    throws org.osid.repository.RepositoryException
    {
        if (recordStructureType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        if (recordStructureType.isEqual(new Type("mit.edu","recordStructure","wellFormed")))
        {
            java.util.Vector results = new java.util.Vector();
            // don't return the content's sturcutre even if it matches, since this that is a separate and special case
            results.addElement(RecordStructure.getInstance());
            return new RecordStructureIterator(results);
        }
        throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.UNKNOWN_TYPE);
    }

    public org.osid.shared.PropertiesIterator getProperties()
    throws org.osid.repository.RepositoryException
    {
        try
        {
            return new PropertiesIterator(new java.util.Vector());
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }        
    }

    public org.osid.shared.Properties getPropertiesByType(org.osid.shared.Type propertiesType)
    throws org.osid.repository.RepositoryException
    {
        if (propertiesType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NULL_ARGUMENT);
        }
        return new Properties();
    }

    public org.osid.shared.TypeIterator getPropertyTypes()
    throws org.osid.repository.RepositoryException
    {
        try
        {
            return new TypeIterator(new java.util.Vector());
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }        
    }

    protected void addAsset(org.osid.repository.Asset asset)
    throws org.osid.repository.RepositoryException
    {
        this.assetVector.addElement(asset);
    }

    public boolean supportsUpdate()
    throws org.osid.repository.RepositoryException
    {
        return false;
    }

    public boolean supportsVersioning()
    throws org.osid.repository.RepositoryException
    {
        return false;
    }
}
