/*
 * Presentation.java
 *
 * Created on March 26, 2004, 3:34 PM
 */

package org.sakaiproject.component.app.presentation;
import java.util.List;
import java.util.Vector;

import org.osid.shared.Id;
import org.sakaiproject.api.app.presentation.Slide;
import org.sakaiproject.service.legacy.time.Time;

/**
 *  The Presentation class allows content to be grouped together in a linear sequence.
 * Any type of presentation control is handled by the presentation manager.
 * @author  Mark Norton
 */
public class PrPresentation implements org.sakaiproject.api.app.presentation.Presentation {
    private Id id = null;   //  The Id of this presentation.
    private String title = null;
    private List slides = new Vector();       //  The ordered set of slides in this presentation.
    private Time modificationTime = null;
    
    /** Creates a new instance of Presentation */
    public PrPresentation(Id id) {
        this.id = id;
    }

    /** Creates a new instances of Presentation given a list of slides.  */
    public PrPresentation(Id id, List slides) {
        this.id = id;
        if (slides != null)
        	this.slides = slides;
    }

    public PrPresentation (Id id, List slides, String title, Time time) {
		this.id = id;
		if (slides != null)
			this.slides = slides;
		this.title = title;
		this.modificationTime = time;
	}

    /**
     *  Return the id of this presentation.
     *
     *  @author Mark Norton
     */
    public Id getId()  {
        return this.id;
    }

    /**
     *  Set the id of this presentation.
     *
     *  @author Mark Norton
     */
    public void setId (Id id)  {
        this.id = id;
    }
	
    public String getTitle () {
		return this.title;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public String getModificationDate () {
		return this.modificationTime.toStringLocalFull();
	}

    /**
     *  Return an iterator which lists the slides in this presentation.
     *
     *  @author Mark Norton
     */
    public List getSlides() {
		return this.slides;
    }

    /**
     *  Return the slide at the given offset.
     *
     *  @author Mark Norton
     */
    public Slide getSlide (int offset) {
		return (Slide) this.slides.get(offset);
    }

    /**
     *  Append a slide to the end of the slide list.
     *
     *  @author Mark Norton
     */
    public void addSlide(Slide slide) {
        this.slides.add (slide);
    }

    /**
     *  Insert a slide at the position given.
     *
     *  @author Mark Norton
     */
    public void insertSlide(int position, Slide slide) {
        this.slides.add (position, slide);
    }
    
    /**
     *  Return the number of slides in this presentation.
     *
     *  @author Mark Norton
     */
    public int getSlideCount() {
        return this.slides.size();
    }

    /**
     *  Delete the slide at the given position.
     *
     *  @author Mark Norton
     */
    public void deleteSlide(int position)  {
        this.slides.remove(position);
    }

}
