/*
 * Created on Jul 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.service.presentation;
import java.util.List;

import org.osid.shared.Id;
import org.osid.id.IdManager;

import org.sakaiproject.exception.*;

/**
 * @author Mark Norton
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PresentationManager {
    IdManager getIdManager();
    void setIdManager (IdManager im);

    public boolean allowUpdate(Presentation pres);

    /**
     * Start a slide show on a presentation
     * 
     * @author	Charles Severance
     */
    
    public boolean startShow(Presentation pres);

    /**
     * Stop a slide show on a presentation
     * 
     * @author	Charles Severance
     */
    
    public boolean stopShow(Presentation pres);

    /**
     * Rewind a slide show on a presentation
     * 
     * @author	Charles Severance
     */
    
    public boolean rewindShow(Presentation pres);

    /**
     * Advance a slide show on a presentation
     * 
     * @author	Charles Severance
     */
    
    public boolean advanceShow(Presentation pres);

    /**
     * Reverse a slide show on a presentation
     * 
     * @author	Charles Severance
     */
    
    public boolean backShow(Presentation pres);

    /**
     * Determine if the show is currently showing
     *
     * @author  Charles Severance
     */

    public boolean isShowing(Presentation pres);

    /**
     * Get current slide for a show
     *
     * @author  Charles Severance
     */

    public Slide getCurrentSlide(Presentation pres);

    /**
     * Get current slide number for a show
     *
     * @author  Charles Severance
     */

    public int getCurrentSlideNumber(Presentation pres);

    /**
     * Get the Modification Date as a String
     *
     * @author Charles Severance
     */

    public String getModificationDate(Presentation pres);

    /**
     *  Return an interator which lists all known presentations.
     *
     *  @author Mark Norton
     */
    public List getPresentations()
                throws IdUnusedException, TypeException, PermissionException;

    /**
     * Get a Sakai reference to the presentation area - returns null on 
     * any type of failure.
     *
     *  @author Charles Severance
     */
    public String getHomeReference();

    /**
     * Get a Sakai reference to a particular presentation - returns null on 
     * any type of failure.
     *
     *  @author Charles Severance;
     */
    public String getReference(Presentation pres);

    public void clearPresentationCache();
}
