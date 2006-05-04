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
			if (partStructureId.isEqual(ContributorPartStructure.getInstance().getId())) {
				this.partStructure = ContributorPartStructure.getInstance();
			} else if (partStructureId.isEqual(CoveragePartStructure.getInstance().getId())) {
				this.partStructure = CoveragePartStructure.getInstance();
			} else if (partStructureId.isEqual(CreatorPartStructure.getInstance().getId())) {
				this.partStructure = CreatorPartStructure.getInstance();
			} else if (partStructureId.isEqual(DatePartStructure.getInstance().getId())) {
				this.partStructure = DatePartStructure.getInstance();
			} else if (partStructureId.isEqual(FormatPartStructure.getInstance().getId())) {
				this.partStructure = FormatPartStructure.getInstance();
			} else if (partStructureId.isEqual(LanguagePartStructure.getInstance().getId())) {
				this.partStructure = LanguagePartStructure.getInstance();
			} else if (partStructureId.isEqual(PublisherPartStructure.getInstance().getId())) {
				this.partStructure = PublisherPartStructure.getInstance();
			} else if (partStructureId.isEqual(RelationPartStructure.getInstance().getId())) {
				this.partStructure = RelationPartStructure.getInstance();
			} else if (partStructureId.isEqual(RightsPartStructure.getInstance().getId())) {
				this.partStructure = RightsPartStructure.getInstance();
			} else if (partStructureId.isEqual(SourcePartStructure.getInstance().getId())) {
				this.partStructure = SourcePartStructure.getInstance();
			} else if (partStructureId.isEqual(SubjectPartStructure.getInstance().getId())) {
				this.partStructure = SubjectPartStructure.getInstance();
			} else if (partStructureId.isEqual(TypePartStructure.getInstance().getId())) {
				this.partStructure = TypePartStructure.getInstance();
			} else if (partStructureId.isEqual(VueSpecPartStructure.getInstance().getId())) {
				this.partStructure = VueSpecPartStructure.getInstance();
			} else if (partStructureId.isEqual(XmlPartStructure.getInstance().getId())) {
				this.partStructure = XmlPartStructure.getInstance();
			} else if (partStructureId.isEqual(URLPartStructure.getInstance().getId())) {
				this.partStructure = URLPartStructure.getInstance();
			}
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
		return this.partStructure;
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
