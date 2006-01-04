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
public class RecordStructure
implements org.osid.repository.RecordStructure
{
    private String idString = "af106d4f201080006d751920168000100";
    private String displayName = "Library Content";
    private String description = "Holds metadata for an asset of no particular kind";
    private String format = "";
    private String schema = "";
    private org.osid.shared.Type type = new Type("mit.edu","repository","library_content");
    private boolean repeatable = false;
	private static RecordStructure recordStructure = new RecordStructure();
    private org.osid.shared.Id id = null;
	
	protected static RecordStructure getInstance()
	{
		return recordStructure;
	}
	
    protected RecordStructure()
    {
        try
		{
			this.id = Managers.getInstance().getIdManager().getId(this.idString);
		}
		catch (Throwable t)
		{
			System.out.println(t.getMessage());
		}
	}

    public String getDisplayName()
    throws org.osid.repository.RepositoryException
    {
        return this.displayName;
    }

    public String getDescription()
    throws org.osid.repository.RepositoryException
    {
        return this.description;
    }

    public String getFormat()
    throws org.osid.repository.RepositoryException
    {
        return this.format;
    }

    public String getSchema()
    throws org.osid.repository.RepositoryException
    {
        return this.schema;
    }

    public org.osid.shared.Type getType()
    throws org.osid.repository.RepositoryException
    {
        return this.type;
    }

    public boolean isRepeatable()
    throws org.osid.repository.RepositoryException
    {
        return this.repeatable;
    }

    public org.osid.shared.Id getId()
    throws org.osid.repository.RepositoryException
    {
        return this.id;
    }

    public void updateDisplayName(String displayName)
    throws org.osid.repository.RepositoryException
    {
        throw new org.osid.repository.RepositoryException(org.osid.OsidException.UNIMPLEMENTED);
    }

    public org.osid.repository.PartStructureIterator getPartStructures()
    throws org.osid.repository.RepositoryException
    {
        java.util.Vector results = new java.util.Vector();
        try
        {
            results.addElement(new CreatorPartStructure());
            results.addElement(new SubjectPartStructure());
            results.addElement(new PublisherPartStructure());
            results.addElement(new ContributorPartStructure());
            results.addElement(new DatePartStructure());
            results.addElement(new TypePartStructure());
            results.addElement(new FormatPartStructure());
            results.addElement(new SourcePartStructure());
            results.addElement(new LanguagePartStructure());
            results.addElement(new RelationPartStructure());
            results.addElement(new CoveragePartStructure());
            results.addElement(new RightsPartStructure());
        }
        catch (Throwable t)
        {
            throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
        }
        return new PartStructureIterator(results);
    }

    public boolean validateRecord(org.osid.repository.Record record)
    throws org.osid.repository.RepositoryException
    {
        return true;
    }
}
