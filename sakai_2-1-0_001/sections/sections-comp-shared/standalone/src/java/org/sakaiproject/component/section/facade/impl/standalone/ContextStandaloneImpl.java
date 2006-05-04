/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The Regents of the University of Michigan,
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

package org.sakaiproject.component.section.facade.impl.standalone;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.sakaiproject.api.section.facade.manager.Context;

/**
 * Standalone implementation of Context, using the HttpSession to store current
 * context.
 * 
 * @author <a href="jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class ContextStandaloneImpl implements Context {

	public static final String CONTEXT = "context";

	public String getContext(Object request) {
    	HttpSession session = null;
    	if(request == null) {
            session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    	} else {
    		session = ((HttpServletRequest)request).getSession();
    	}
        return (String)session.getAttribute(CONTEXT);
	}

	public String getContextTitle(Object request) {
		return getContext(request);
	}	
	
}



/**********************************************************************************
 * $Id$
 *********************************************************************************/