package org.sakaiproject.service.legacy.content;

import java.util.Collection;

public interface LockManager {

    public abstract void lockObject(String assetId, String qualifierId,
            String reason, boolean system);

    public abstract void removeLock(String assetId, String qualifierId);

    /**
     *
     * @param node - the asset to check
     * @return - a non-empty Collection of active Locks, or null
     */
    public abstract Collection getLocks(String assetId);

    // TODO create a faster query (don't need all rows)
    public abstract boolean isLocked(String assetId);

    public abstract void removeAllLocks(String qualifier);
}
