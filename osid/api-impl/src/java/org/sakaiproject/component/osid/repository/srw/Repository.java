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
    private org.osid.shared.Id recordStructureId = null;
    private org.osid.shared.Id vueRecordStructureId = null;
    private org.osid.shared.Id dcRecordStructureId = null;
    private org.osid.shared.Id CREATOR_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id SUBJECT_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id PUBLISHER_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id CONTRIBUTOR_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id DATE_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id TYPE_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id FORMAT_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id SOURCE_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id LANGUAGE_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id RELATION_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id COVERAGE_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id RIGHTS_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id XML_PART_STRUCTURE_ID = null;
    private org.osid.shared.Id VUE_SPEC_PART_STRUCTURE_ID = null;
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
            this.recordStructureId = idManager.getId("af106d4f201080006d751920168000100");
            this.dcRecordStructureId = idManager.getId("f6c16d4f201080006d751920168000100");
            this.vueRecordStructureId = idManager.getId("d5e9eea5301080006d751920168000100");
            this.CREATOR_PART_STRUCTURE_ID = idManager.getId("b5ae441f201080006d751920168000100");
            this.SUBJECT_PART_STRUCTURE_ID = idManager.getId("a8a1541f201080006d751920168000100");
            this.PUBLISHER_PART_STRUCTURE_ID = idManager.getId("0bd5374f201080006d751920168000100");
            this.CONTRIBUTOR_PART_STRUCTURE_ID = idManager.getId("18a4541f201080006d751920168000100");
            this.DATE_PART_STRUCTURE_ID = idManager.getId("b197541f201080006d751920168000100");
            this.TYPE_PART_STRUCTURE_ID = idManager.getId("0a3a541f201080006d751920168000100");
            this.FORMAT_PART_STRUCTURE_ID = idManager.getId("e46d541f201080006d751920168000100");
            this.SOURCE_PART_STRUCTURE_ID = idManager.getId("e350641f201080006d751920168000100");
            this.LANGUAGE_PART_STRUCTURE_ID = idManager.getId("1c74641f201080006d751920168000100");
            this.RELATION_PART_STRUCTURE_ID = idManager.getId("6597641f201080006d751920168000100");
            this.COVERAGE_PART_STRUCTURE_ID = idManager.getId("e0ff641f201080006d751920168000100");
            this.RIGHTS_PART_STRUCTURE_ID = idManager.getId("5492741f201080006d751920168000100");
            this.XML_PART_STRUCTURE_ID = idManager.getId("dfef451f201080006d751920168000100");
            this.VUE_SPEC_PART_STRUCTURE_ID = idManager.getId("c928eea5301080006d751920168000100");
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
        if (displayName == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public String getDescription()
    throws org.osid.repository.RepositoryException
    {
        return this.description;
    }

    public void updateDescription(String description)
    throws org.osid.repository.RepositoryException
    {
        if (description == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
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
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        if (!assetType.isEqual(this.assetType))
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNKNOWN_TYPE);
        }
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public void deleteAsset(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public org.osid.repository.AssetIterator getAssets()
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public org.osid.repository.AssetIterator getAssetsByType(org.osid.shared.Type assetType)
    throws org.osid.repository.RepositoryException
    {
        if (assetType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
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
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
        }
    }

    public org.osid.repository.RecordStructureIterator getRecordStructures()
    throws org.osid.repository.RepositoryException
    {
        java.util.Vector results = new java.util.Vector();
        results.addElement(new RecordStructure(this.idManager));
        return new RecordStructureIterator(results);
    }

    public org.osid.repository.RecordStructureIterator getMandatoryRecordStructures(org.osid.shared.Type assetType)
    throws org.osid.repository.RepositoryException
    {
        if (assetType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        if (assetType.isEqual(this.assetType))
        {
            java.util.Vector results = new java.util.Vector();
            results.addElement(new RecordStructure(this.idManager));
            return new RecordStructureIterator(results);
        }
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNKNOWN_TYPE);
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
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
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
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
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
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public org.osid.repository.Asset getAsset(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        if (assetId == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public org.osid.repository.Asset getAssetByDate(org.osid.shared.Id assetId
                                                  , long date)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public org.osid.shared.LongValueIterator getAssetDates(org.osid.shared.Id assetId)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public org.osid.repository.AssetIterator getAssetsBySearch(java.io.Serializable searchCriteria
                                                             , org.osid.shared.Type searchType
                                                             , org.osid.shared.Properties searchProperties)
    throws org.osid.repository.RepositoryException
    {
        if (searchCriteria == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        if (searchType == null) 
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        if (!(searchCriteria instanceof String))
        {
            // maybe change this to a new exception message
            log("invalid criteria");
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
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
					
					// Create a trust manager that does not validate certificate chains
					javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
						new javax.net.ssl.X509TrustManager() {
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return null;
							}
							public void checkClientTrusted(
								java.security.cert.X509Certificate[] certs, String authType) {
							}
							public void checkServerTrusted(
								java.security.cert.X509Certificate[] certs, String authType) {
							}
						}
					};
    
					// Install the all-trusting trust manager
					try {
						javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
						sc.init(null, trustAllCerts, new java.security.SecureRandom());
						javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
					} catch (Exception e) {
					}
    
					// Now you can access an https URL without having the certificate in the truststore
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
//                    System.out.println("xml " + xml);

                    javax.xml.parsers.DocumentBuilderFactory dbf = null;
                    javax.xml.parsers.DocumentBuilder db = null;
                    org.w3c.dom.Document document = null;

                    dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                    db = dbf.newDocumentBuilder();
                    document = db.parse(new java.io.ByteArrayInputStream(xml.toString().getBytes()));
                    // for each DOC (maps 1-to-1 with Asset)
                    org.w3c.dom.NodeList docs = document.getElementsByTagName("record");
                    int numDocs = docs.getLength();
                    for (int i=0; i < numDocs; i++)
                    {
                        String assetTitle = null;
                        String assetDescription = "";
                        String assetId = null;
                        org.osid.repository.Asset asset = null;
                        org.osid.repository.Record record = null;

                        org.w3c.dom.Element doc = (org.w3c.dom.Element)docs.item(i);
                        org.w3c.dom.NodeList dcs = doc.getElementsByTagName("dc:title");
                        int numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                    assetTitle = dc.getFirstChild().getNodeValue();
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:description");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                assetDescription = dc.getFirstChild().getNodeValue();
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:identifier.uri");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                assetId = dc.getFirstChild().getNodeValue();
                            }
                        }

                        if ((assetTitle != null) && (assetId != null))
                        {
                            asset = new Asset(assetTitle,assetDescription,assetId,this.idManager,this.log,this.id);
                            results.addElement(asset);
                        }
                        
                        dcs = doc.getElementsByTagName("dc:creator");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.CREATOR_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:subject");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.SUBJECT_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:publisher");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.PUBLISHER_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:contributor");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.CONTRIBUTOR_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:date");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.DATE_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:type");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.TYPE_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:format");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.FORMAT_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:source");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.SOURCE_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:language");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.LANGUAGE_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:relation");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.RELATION_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:coverage");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) record = asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.COVERAGE_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        dcs = doc.getElementsByTagName("dc:rights");
                        numDCs = dcs.getLength();
                        for (int k=0; k < numDCs; k++)
                        {
                            org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
                            if (dc.hasChildNodes()) 
                            {
                                if ((asset != null) && (record == null)) asset.createRecord(this.recordStructureId);
                                if ((asset != null) && (record != null)) record.createPart(this.RIGHTS_PART_STRUCTURE_ID,dc.getFirstChild().getNodeValue());
                            }
                        }
                        if ((xml.length() > 0) && (asset != null))
                        {
                            org.osid.repository.Record r = asset.createRecord(this.dcRecordStructureId);
                            r.createPart(this.XML_PART_STRUCTURE_ID,xml);
                        }
                        // VUE integration
                        if ((asset != null) && (record != null))
                        {
                            //System.out.println("creating VUE integration record");
                            org.osid.repository.Record r = asset.createRecord(this.vueRecordStructureId);
                            org.osid.repository.PartIterator partIterator = record.getParts();
                            while (partIterator.hasNextPart())
                            {
                                org.osid.repository.Part part = partIterator.nextPart();
                                r.createPart(part.getPartStructure().getId(),part.getValue());
                            }
                            //System.out.println("setting spec in rep merge " + this. VUE_SPEC_PART_STRUCTURE_ID.getIdString() + " " + assetId);
                            r.createPart(this.VUE_SPEC_PART_STRUCTURE_ID,assetId);
/*                            
                            System.out.println("reading record back");
                            org.osid.repository.PartIterator p = r.getParts();
                            while (p.hasNextPart())
                            {
                                org.osid.repository.Part p1 = p.nextPart();
                                org.osid.repository.PartStructure ps = p1.getPartStructure();
                                System.out.println("next part name " + ps.getDisplayName());
                            }
*/
                        }
                    }
                }
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
        }
        if (!knownType)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNKNOWN_TYPE);
        }
        return new AssetIterator(results);
    }

    public org.osid.shared.Id copyAsset(org.osid.repository.Asset asset)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNIMPLEMENTED);
    }

    public org.osid.repository.RecordStructureIterator getRecordStructuresByType(org.osid.shared.Type recordStructureType)
    throws org.osid.repository.RepositoryException
    {
        if (recordStructureType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
        }
        if (recordStructureType.isEqual(new Type("mit.edu","recordStructure","wellFormed")))
        {
            java.util.Vector results = new java.util.Vector();
            // don't return the content's sturcutre even if it matches, since this that is a separate and special case
            results.addElement(new RecordStructure(this.idManager));
            return new RecordStructureIterator(results);
        }
        throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNKNOWN_TYPE);
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
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
        }        
    }

    public org.osid.shared.Properties getPropertiesByType(org.osid.shared.Type propertiesType)
    throws org.osid.repository.RepositoryException
    {
        if (propertiesType == null)
        {
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.NULL_ARGUMENT);
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
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
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
