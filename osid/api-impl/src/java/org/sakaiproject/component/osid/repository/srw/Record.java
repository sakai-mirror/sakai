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
public class Record
implements org.osid.repository.Record
{
    private java.util.Vector partVector = new java.util.Vector();
    private org.osid.id.IdManager idManager = null;
    private org.osid.logging.WritableLog log = null;
    private org.osid.shared.Id recordStructureId = null;
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

    protected Record(org.osid.shared.Id recordStructureId
                   , org.osid.id.IdManager idManager
                   , org.osid.logging.WritableLog log)
    throws org.osid.repository.RepositoryException
    {
        try
        {
            this.idManager = idManager;
            this.log = log;
            this.recordStructureId = recordStructureId;
            this.id = idManager.createId();
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
        try
        {
            org.osid.repository.Part part = new Part(partStructureId,value,this.idManager,this.log);                    
            this.partVector.addElement(part);
            return part;
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
        }
    }

    public void deletePart(org.osid.shared.Id partId)
    throws org.osid.repository.RepositoryException
    {
        try
        {
            for (int i=0, size = this.partVector.size(); i < size; i++)
            {
                org.osid.repository.Part part = (org.osid.repository.Part)this.partVector.elementAt(i);
                if (part.getId().isEqual(partId))
                {
                    this.partVector.removeElementAt(i);
                    return;
                }
            }
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.UNKNOWN_ID);
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
        }
    }

    public void updateDisplayName(String displayName)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.PartIterator getParts()
    throws org.osid.repository.RepositoryException
    {
        return new PartIterator(this.partVector);
    }

    public org.osid.repository.RecordStructure getRecordStructure()
    throws org.osid.repository.RepositoryException
    {
        try
        {
            if (this.recordStructureId.isEqual(this.idManager.getId("af106d4f201080006d751920168000100")))
            {
                return new RecordStructure(this.idManager, this.log);
            }
            else if (this.recordStructureId.isEqual(this.idManager. getId("f6c16d4f201080006d751920168000100")))
            {
                return new DCRecordStructure(this.idManager, this.log);
            }
            else if (this.recordStructureId.isEqual(this.idManager. getId("d5e9eea5301080006d751920168000100")))
            {
                return new VueRecordStructure(this.idManager, this.log);
            }
            else
            {
                return null;
            }
        }
        catch (Throwable t)
        {
            log(t.getMessage());
            throw new org.osid.repository.RepositoryException(org.osid.repository.RepositoryException.OPERATION_FAILED);
        }
    }

    public boolean isMultivalued()
    throws org.osid.repository.RepositoryException
    {
        return false;
    }
}
