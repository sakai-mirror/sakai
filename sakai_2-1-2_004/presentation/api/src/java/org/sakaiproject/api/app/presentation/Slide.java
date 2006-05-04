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
 * Created on March 26, 2004, 3:17 PM
 */

package org.sakaiproject.api.app.presentation;
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

/**********************************************************************************
*
* $Footer:  $
*
***********************************************************************************/

