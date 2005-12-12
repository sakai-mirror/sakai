/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.service.message.api.model;

import java.util.Date;

/**
 * @author ieb
 *
 */
public interface Preference {

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
     * @return Returns the preference.
     */
    String getPreference();

    /**
     * @param preference The preference to set.
     */
    void setPreference(String preference);

    /**
     * @return Returns the user.
     */
    String getUser();

    /**
     * @param user The user to set.
     */
    void setUser(String user);

}