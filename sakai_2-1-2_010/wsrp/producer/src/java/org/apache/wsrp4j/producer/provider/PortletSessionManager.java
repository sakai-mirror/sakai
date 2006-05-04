/**
 * 
 */
package org.apache.wsrp4j.producer.provider;

import org.apache.wsrp4j.exception.WSRPException;

/**
 * @author <a href="mailto:vgoenka@sungardsct.com">Vishal Goenka</a>
 *
 */
public interface PortletSessionManager {
    
    /*
     * If the session does not exist, and create=true, a new Session is created. 
     * If create=false, a session MUST exist; if it does, it is set as current, 
     * but if it doesn't, an exception is thrown. 
     */
    boolean setCurrentSession(boolean create) throws WSRPException;

    /*
     * Reset the current session back to what it was prior to the call to setCurrentSession
     * in the current thread of execution.
     */
    void resetCurrentSession();

    /*
     * Set the given userid as the user for the current session
     */
    void setCurrentUser(String userId);

    /*
     * Release the session given by the id
     */
    void releaseSession(String sessionId);    
}
