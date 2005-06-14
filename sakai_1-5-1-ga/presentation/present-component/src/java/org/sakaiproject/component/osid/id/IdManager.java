package org.sakaiproject.component.osid.id;

public class IdManager
implements org.osid.id.IdManager
{
    org.osid.OsidContext context = null;
    java.util.Properties configuration = null;

    public org.osid.OsidContext getOsidContext()
    throws org.osid.id.IdException
    {
	return null;
    }

    public void assignOsidContext(org.osid.OsidContext context)
    throws org.osid.id.IdException
    {
        // Nothing to see here folks
    }

    public void assignConfiguration(java.util.Properties configuration)
    throws org.osid.id.IdException
    {
        // Nothing to see here folks
    }

    private void log(String entry)
    throws org.osid.id.IdException
    {
	System.out.println(entry);
    }

    public org.osid.shared.Id createId()
    throws org.osid.id.IdException
    {
        try
        {
            return new Id();
        }
        catch (org.osid.shared.SharedException sex)
        {
            throw new org.osid.id.IdException(sex.getMessage());
        }
    }

    public org.osid.shared.Id getId(String idString)
    throws org.osid.id.IdException
    {
        if (idString == null)
        {
            throw new org.osid.id.IdException(org.osid.id.IdException.NULL_ARGUMENT);    
        }
        try
        {
            return new Id(idString);
        }
        catch (org.osid.shared.SharedException sex)
        {
            throw new org.osid.id.IdException(sex.getMessage());
        }
    }

    public void osidVersion_2_0()
    throws org.osid.id.IdException
    {
    }
}
