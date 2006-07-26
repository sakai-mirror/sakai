/**
 * 
 */
package uk.ac.cam.caret.sakai.rwiki.component.message.model.impl;

import java.util.Date;

import uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference;

/**
 * @author ieb
 *
 */
public class PreferenceImpl implements Preference {
    private String id;
    private String user;
    private Date lastseen;
    private String preference;
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference#getId()
     */
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference#setId(java.lang.String)
     */
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference#getLastseen()
     */
    public Date getLastseen() {
        return lastseen;
    }
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference#setLastseen(java.util.Date)
     */
    public void setLastseen(Date lastseen) {
        this.lastseen = lastseen;
    }
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference#getPreference()
     */
    public String getPreference() {
        return preference;
    }
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference#setPreference(java.lang.String)
     */
    public void setPreference(String preference) {
        this.preference = preference;
    }
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference#getUser()
     */
    public String getUser() {
        return user;
    }
    /* (non-Javadoc)
     * @see uk.ac.cam.caret.sakai.rwiki.service.message.api.model.Preference#setUser(java.lang.String)
     */
    public void setUser(String user) {
        this.user = user;
    }
    
 
}
