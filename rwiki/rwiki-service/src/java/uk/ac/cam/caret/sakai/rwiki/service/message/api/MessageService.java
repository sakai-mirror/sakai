/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.service.message.api;

import java.util.List;


/**
 * @author ieb
 *
 */
public interface MessageService {
    /**
     * Updates the session presence record in the wiki
     * @param session
     * @param user
     * @param page
     * @param space
     */
    void updatePresence(String session, String user, String page, String space);
    /**
     * Update the users preference
     * @param user
     * @param perference
     */
    void updatePreference(String user, String perference);
    /**
     * Add a chat message
     * @param session
     * @param user
     * @param page
     * @param space
     * @param message
     */
    void addMessage(String session, String user, String page, String space, String message);
    
    /**
     * returns a List of the Messages associated with the session
     * @param session
     * @return
     */
    List getSessionMessages(String session);
    /**
     * Returns List of the Messages in the space
     * @param space
     * @return
     */
    List getMessagesInSpace(String space);
    /**
     * Returns List of Messages in the page
     * @param space
     * @param page
     * @return
     */
    List getMessagesInPage(String space, String page);
    /**
     * Returns List representation of the users in the space 
     * @param space
     * @return
     */
    List getUsersInSpace(String space);
    /** 
     * Returns List representation of the users on the page
     * @param space
     * @param page
     * @return
     */
    List getUsersOnPage(String space, String page);
    /**
     * @param pageSpace
     * @param pageName
     * @return
     */
    List getUsersInSpaceOnly(String pageSpace, String pageName);
    
    

}
