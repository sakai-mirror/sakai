/*
 * Presentation.java
 *
 * Created on March 26, 2004, 3:34 PM
 */

package org.sakaiproject.service.presentation;
import java.util.List;

import org.osid.shared.Id;

import org.sakaiproject.service.legacy.time.Time;

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
