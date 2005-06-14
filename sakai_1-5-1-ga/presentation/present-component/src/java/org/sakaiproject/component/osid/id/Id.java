package org.sakaiproject.component.osid.id;

// Simply Call the Legacy Service to get the Id

import org.sakaiproject.service.legacy.id.cover.IdService;

public class Id
implements org.osid.shared.Id
{
    private String idString = null;

    private void log(String entry)
    throws org.osid.shared.SharedException
    {
	System.out.println("Id:"+entry);
    }

    protected Id()
    throws org.osid.shared.SharedException
    {
	idString = IdService.getUniqueId();
    }

    protected Id(String idString)
    throws org.osid.shared.SharedException
    {
        if (idString == null)
        {
            throw new org.osid.shared.SharedException(org.osid.id.IdException.NULL_ARGUMENT);    
        }
        this.idString = idString;
    }

    public String getIdString()
    throws org.osid.shared.SharedException
    {
        return this.idString;
    }

    public boolean isEqual(org.osid.shared.Id id)
    throws org.osid.shared.SharedException
    {
        return id.getIdString().equals(this.idString);
    }
}
