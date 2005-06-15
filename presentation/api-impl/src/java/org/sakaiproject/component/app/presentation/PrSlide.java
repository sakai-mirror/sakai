/*
 * Slide.java
 *
 * Created on March 26, 2004, 3:17 PM
 */

package org.sakaiproject.component.app.presentation;
import java.io.Serializable;

/**
 *
 * @author  Mark Norton
 *
 *  A slide is a single page of a presentation.  Each slide has a type, which usually
 *  will be consistent throughout a presentation, but is not required to be so.  Each
 *  slide may have an optional displayName.
 */
public class PrSlide implements org.sakaiproject.api.app.presentation.Slide{

	private String url = null;

    private Serializable content = null;

    private String displayName = null;

    private String contentType = null;

    /** Creates a new Slide given content and type */
    public PrSlide(Serializable content, String type) {
        this.content = content;
        this.contentType = type;
    }

    /**	Creates a new Slide given url, name, and type.  */
    public PrSlide (String url, String name, String type) {
    	this.url = url;
    	this.displayName = name;
    	this.contentType = type;
    }

    /**
     * 	Get the URL for this slide.
     * 	@return slide URL.
     */
    public String getUrl () {
    	return this.url;
    }

    /**
     * 	Set the URL for this slide.
     * 	@param url
     */
    public void setUrl (String url) {
    		this.url = url;
    }

    /**
     *  Return the content of this slide.
     *
     *  @author Mark Norton
     */
    public Serializable getContent() {
        return this.content;
    }

    /**
     *  Set the content of this slide.
     *
     *  @author Mark Norton
     */
    public void setContent(Serializable content) {
        this.content = content;
    }

    /**
     *  Get the display name (title) of this slide.
     *
     *  @author Mark Norton
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     *  Set the display name of this slide.
     *
     *  @author Mark Norton
     */
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    /**
     *  Get the content type of this slide.
     *
     *  @author Mark Norton
     */
    public String getType() {
        return this.contentType;
    }

    /**
     *  Set the content type of this slide.
     *
     *  @author Mark Norton
     */
    public void setType(String type) {
        this.contentType = type;
    }

}
