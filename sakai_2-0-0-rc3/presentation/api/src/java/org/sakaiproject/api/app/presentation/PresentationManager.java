/**********************************************************************************
*
* $Header: /cvs/sakai2/presentation/api/src/java/org/sakaiproject/api/app/presentation/PresentationManager.java,v 1.1 2005/05/17 20:58:20 csev.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
* 
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
* 
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/

package org.sakaiproject.api.app.presentation;

import java.util.List;

import org.osid.id.IdManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;

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

/**********************************************************************************
*
* $Footer:  $
*
***********************************************************************************/

