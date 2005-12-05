/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.service.message.api;

import java.util.List;

import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Trigger;

/**
 * @author ieb
 *
 */
public interface TriggerService {
    /**
     * Fire triggers based on space
     * @param space
     */
    void fireSpaceTriggers(String space);
    /** 
     * Fire Triggers on page
     * @param space
     * @param page
     */
    void firePageTriggers(String space, String page);
    
    /**
     * add a new Trigger
     * @param user
     * @param space
     * @param page
     * @param spec
     */
    void addTrigger(String user, String space, String page, String spec);
    /**
     *remove a Trigger
     * @param trigger
     */
    void removeTrigger(Trigger trigger);
    /**
     * Update a Trigger
     * @param trigger
     */
    void updateTrigger(Trigger trigger);
    /**
     * Get a list of triggers for this user on this page
     * @param user
     * @param space
     * @param page
     * @return
     */
    List getUserTriggers(String user, String space, String page);
    /**
     * Get a list of triggers for this user in the space
     * @param user
     * @param space
     * @return
     */
    List getUserTriggers(String user, String space);
    /**
     * get all triggers for this user
     * @param user
     * @return
     */
    List getUserTriggers(String user);
    
    /**
     * get all triggers for the page
     * @param space
     * @param page
     * @return
     */
    List getPageTriggers(String space, String page );
    /**
     * Get a list of all triggers for the space
     * @param space
     * @return
     */
    List getSpaceTriggers(String space);
    

}