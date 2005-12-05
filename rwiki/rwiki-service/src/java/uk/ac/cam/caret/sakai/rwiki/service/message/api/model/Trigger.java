/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.service.message.api.model;

import java.util.Date;

/**
 * @author ieb
 *
 */
public interface Trigger {

    /**
     * @return Returns the id.
     */
    String getId();

    /**
     * @param id The id to set.
     */
    void setId(String id);

    /**
     * @return Returns the lastseen.
     */
    Date getLastseen();

    /**
     * @param lastseen The lastseen to set.
     */
    void setLastseen(Date lastseen);

    /**
     * @return Returns the pagename.
     */
    String getPagename();

    /**
     * @param pagename The pagename to set.
     */
    void setPagename(String pagename);

    /**
     * @return Returns the pagespace.
     */
    String getPagespace();

    /**
     * @param pagespace The pagespace to set.
     */
    void setPagespace(String pagespace);

    /**
     * @return Returns the triggerspec.
     */
    String getTriggerspec();

    /**
     * @param triggerspec The triggerspec to set.
     */
    void setTriggerspec(String triggerspec);

    /**
     * @return Returns the user.
     */
    String getUser();

    /**
     * @param user The user to set.
     */
    void setUser(String user);

}