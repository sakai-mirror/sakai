/*
 * Slide.java
 *
 * Created on March 26, 2004, 3:17 PM
 */

package org.sakaiproject.service.presentation;
import java.io.Serializable;

/**
 *
 * @author  Mark Norton
 *
 *  A slide is a single page of a presentation.  Each slide has a type, which usually
 *  will be consistent throughout a presentation, but is not required to be so.  Each
 *  slide may have an optional displayName.
 */
public interface Slide extends java.io.Serializable {

	/**
	 * 	Return the Url for this slide.
	 * 	@return url of this slide.
	 */
	public String getUrl();
	
	/**
	 * 	Set the url for this slide.
	 * 	@param url
	 */
	public void setUrl(String url);
	
    /**
     *  Return the content associated with this slide.
     *
     *  @author Mark Norton
     */
    public Serializable getContent();

    /**
     *  Set the content associated with this slide.
     *
     *  @author Mark Norton
     */
    public void setContent(Serializable content);

    /**
     *  Get the display name (title) of this slide.
     *
     *  @author Mark Norton
     */
    public String getDisplayName();

    /**
     *  Set the display name (title) of this slide.
     *
     *  @author Mark Norton
     */
    public void setDisplayName(String name);

    /**
     *  Get the slide type.  Slide type determines which kind of content
     *  is included in this slide.
     *
     *  @author Mark Norton
     */
    public String getType();

    /**
     *  Set the slide type.  Slide type identifies the kind of content
     *  included in this slide.
     *
     *  @author Mark Norton
     */
    public void setType(String type);

}
