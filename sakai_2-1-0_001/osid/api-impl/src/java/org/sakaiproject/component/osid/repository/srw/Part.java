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
public class Part
implements org.osid.repository.Part
{
    private org.osid.repository.PartStructure partStructure = null;
    private org.osid.shared.Id partStructureId = null;
    private java.io.Serializable value = null;
    private org.osid.id.IdManager idManager = null;
    private org.osid.logging.WritableLog log = null;
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
    private String displayName = null;
    private org.osid.shared.Id id = null;

    public String getDisplayName()
    throws org.osid.repository.RepositoryException
    {
        return this.displayName;
    }

    public org.osid.shared.Id getId()
    throws org.osid.repository.RepositoryException
    {
        return this.id;
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

    protected Part(org.osid.shared.Id partStructureId
                 , java.io.Serializable value
                 , org.osid.id.IdManager idManager
                 , org.osid.logging.WritableLog log)
    throws org.osid.repository.RepositoryException
    {
        this.idManager = idManager;
        this.log = log;
        this.partStructureId = partStructureId;
        this.value = value;
        try
        {
            this.id = this.idManager.createId();
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
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
        }
    }

    public org.osid.repository.Part createPart(org.osid.shared.Id partStructureId
                                             , java.io.Serializable value)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public void deletePart(org.osid.shared.Id partStructureId)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public void updateDisplayName(String displayName)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.PartIterator getParts()
    throws org.osid.repository.RepositoryException
    {
        return new PartIterator(new java.util.Vector());
    }

    public org.osid.repository.PartStructure getPartStructure()
    throws org.osid.repository.RepositoryException
    {
        try
        {
            if (this.partStructureId.isEqual(this.CREATOR_PART_STRUCTURE_ID)) return new CreatorPartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.SUBJECT_PART_STRUCTURE_ID)) return new SubjectPartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.PUBLISHER_PART_STRUCTURE_ID)) return new PublisherPartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.CONTRIBUTOR_PART_STRUCTURE_ID)) return new ContributorPartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.DATE_PART_STRUCTURE_ID)) return new DatePartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.TYPE_PART_STRUCTURE_ID)) return new TypePartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.FORMAT_PART_STRUCTURE_ID)) return new FormatPartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.SOURCE_PART_STRUCTURE_ID)) return new SourcePartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.LANGUAGE_PART_STRUCTURE_ID)) return new LanguagePartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.RELATION_PART_STRUCTURE_ID)) return new RelationPartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.COVERAGE_PART_STRUCTURE_ID)) return new CoveragePartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.RIGHTS_PART_STRUCTURE_ID)) return new RightsPartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.XML_PART_STRUCTURE_ID)) return new XmlPartStructure(this.idManager,this.log);
            else if (this.partStructureId.isEqual(this.VUE_SPEC_PART_STRUCTURE_ID)) return new VueSpecPartStructure(this.idManager,this.log);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
        }
        return null;
    }

    public java.io.Serializable getValue()
    throws org.osid.repository.RepositoryException
    {
        return this.value;
    }

    public void updateValue(java.io.Serializable value)
    throws org.osid.repository.RepositoryException
    {
        this.value = value;
    }
}
