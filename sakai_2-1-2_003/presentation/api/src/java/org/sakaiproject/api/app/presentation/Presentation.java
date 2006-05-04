/**********************************************************************************
* $URL$
* $Id$
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

/*
 * Presentation.java
 *
 * Created on March 26, 2004, 3:34 PM
 */

package org.sakaiproject.api.app.presentation;
import java.util.List;

import org.osid.shared.Id;

/**
 *  The Presentation class allows content to be grouped together in a ordered, linear sequence.
 *  In addition to the list of content slides, a wait slide may be included to be displayed
 *  before the presentation starts.  If no wait slide is present, nothing is displayed.
 *
 * @author  Mark Norton
 */
public interface Presentation extends java.io.Serializable {
    //  This string constants are used to identify properties associated with presentations.
    public static final String PRESENTATION_TITLE = "org.sakaiproject.tools.presentation.title";
    public static final String PRESENTATION_AUTHOR = "org.sakaiproject.tools.presentation.author";
    public static final String PRESENTATION_CREATED = "org.sakaiproject.tools.presentation.created";

    /**
     *  Get the Id of this presentation.
     *
     *  @author Mark Norton
     */
    public Id getId();

    /**
     *  Set the Id of this presentation.
     *
     *  @author Mark Norton
     */
        public void setId (Id id);

        public String getTitle ();

	public String getModificationDate();

    /**
     *  Return an iterator which lists the slides in this presentation.
     *
     *  @author Mark Norton
     */
    public List getSlides();

    /**
     *  Return the slide at the given offset.
     *
     *  @author Mark Norton
     *  @exception Throws INVALID_OFFSET if the offset is less than zero or greater than max.
     */
    public Slide getSlide (int offset);

    /**
     *  Append a slide to the end of the slide list.
     *
     *  @author Mark Norton
     */
    public void addSlide(Slide slide);

    /**
     *  Get the number of slides in the slide set associated with this presentation.
     *
     *  @author Mark Norton
     */
    public int getSlideCount();

    /**
     *  Delete the slide at the position given.
     *
     *  @author Mark Norton
     *  @exception Throws INVALID_OFFSET if the offset is less than zero or greater than max.
     */
    public void deleteSlide (int position);

    /**
     *  Insert a slide at the position given.
     *
     *  @author Mark Norton
     *  @exception Throws INVALID_OFFSET if the offset is less than zero or greater than max.
     */
    public void insertSlide(int position, Slide slide);

}

/**********************************************************************************
*
* $Footer:  $
*
***********************************************************************************/
