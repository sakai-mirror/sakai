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
public class AssetIterator
implements org.osid.repository.AssetIterator
{
    private java.util.Vector vector = new java.util.Vector();
    private int i = 0;

    protected AssetIterator(java.util.Vector vector)
    throws org.osid.repository.RepositoryException
    {
        this.vector = vector;
    }

    public boolean hasNextAsset()
    throws org.osid.repository.RepositoryException
    {
        return (i < vector.size());
    }

    public org.osid.repository.Asset nextAsset()
    throws org.osid.repository.RepositoryException
    {
        if (i >= vector.size())
        {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NO_MORE_ITERATOR_ELEMENTS);
        }
        return (org.osid.repository.Asset)vector.elementAt(i++);
    }

	protected AssetIterator(StringBuffer xml, org.osid.logging.WritableLog log, org.osid.shared.Id repositoryId)
	throws org.osid.repository.RepositoryException
	{
		try 
		{
			javax.xml.parsers.DocumentBuilderFactory dbf = null;
			javax.xml.parsers.DocumentBuilder db = null;
			org.w3c.dom.Document document = null;
			
			dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			document = db.parse(new java.io.ByteArrayInputStream(xml.toString().getBytes()));
			org.osid.shared.Id recordStructureId = RecordStructure.getInstance().getId();
			
			// for each DOC (maps 1-to-1 with Asset)
			org.w3c.dom.NodeList docs = document.getElementsByTagName("record");
			int numDocs = docs.getLength();
			for (int i=0; i < numDocs; i++)
			{
				org.w3c.dom.Element doc = (org.w3c.dom.Element)docs.item(i);
				
				String assetTitle = null;
				String assetDescription = "";
				String assetId = null;
				org.osid.repository.Asset asset = null;
				org.osid.repository.Record record = null;
				
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
					asset = new Asset(assetTitle,assetDescription,assetId, log, repositoryId);
					record = asset.createRecord(recordStructureId);
					record.createPart(URLPartStructure.getInstance().getId(),assetId);
					this.vector.addElement(asset);
				}
				
				dcs = doc.getElementsByTagName("dc:creator");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(CreatorPartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:subject");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(SubjectPartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:publisher");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(PublisherPartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:contributor");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(ContributorPartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:date");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(DatePartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:type");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(TypePartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:format");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(FormatPartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:source");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(SourcePartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:language");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(LanguagePartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:relation");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(RelationPartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:coverage");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) record = asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(CoveragePartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				dcs = doc.getElementsByTagName("dc:rights");
				numDCs = dcs.getLength();
				for (int k=0; k < numDCs; k++)
				{
					org.w3c.dom.Element dc = (org.w3c.dom.Element)dcs.item(k);
					if (dc.hasChildNodes()) 
					{
						if ((asset != null) && (record == null)) asset.createRecord(recordStructureId);
						if ((asset != null) && (record != null)) record.createPart(RightsPartStructure.getInstance().getId(),dc.getFirstChild().getNodeValue());
					}
				}
				if ((xml.length() > 0) && (asset != null))
				{
					org.osid.repository.Record r = asset.createRecord(DCRecordStructure.getInstance().getId());
					r.createPart(XmlPartStructure.getInstance().getId(),xml);
				}
				// VUE integration
				if ((asset != null) && (record != null))
				{
					//System.out.println("creating VUE integration record");
					org.osid.repository.Record r = asset.createRecord(VueRecordStructure.getInstance().getId());
					org.osid.repository.PartIterator partIterator = record.getParts();
					while (partIterator.hasNextPart())
					{
						org.osid.repository.Part part = partIterator.nextPart();
						r.createPart(part.getPartStructure().getId(),part.getValue());
					}
					//System.out.println("setting spec in rep merge " + this. VUE_SPEC_PART_STRUCTURE_ID.getIdString() + " " + assetId);
					r.createPart(VueSpecPartStructure.getInstance().getId(),assetId);
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
		catch (Throwable t)
		{
			t.printStackTrace();
			if (log != null)
			{
				try
				{
					log.appendLog(t.getMessage());
				}
				catch (org.osid.logging.LoggingException lex) 
				{
					// swallow exception since logging is a best attempt to log an exception anyway
				}   
			}
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
		}
	}
}
