package org.sakaiproject.service.legacy.content;

import java.util.Date;

public class Lock {

    private String id;

    private String asset;

    private String qualifier;

    private boolean active;
    private boolean system;

    private String reason;

    private Date dateAdded;

    private Date dateRemoved;

    public boolean equals(Object in) {
        if (in == null && this == null)
            return true;
        if (in == null && this != null)
            return false;
        if (this == null && in != null)
            return false;
        if (!this.getClass().isAssignableFrom(in.getClass()))
            return false;
        if (this.getId() == null && ((Lock) in).getId() == null)
            return true;
        if (this.getId() == null || ((Lock) in).getId() == null)
            return false;
        return this.getId().equals(((Lock) in).getId());
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date getDateRemoved() {
        return dateRemoved;
    }

    public void setDateRemoved(Date dateRemoved) {
        this.dateRemoved = dateRemoved;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
    
    

}
