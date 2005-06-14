/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/tag/ButtonBarItem.java,v 1.4 2004/06/22 03:11:04 ggolden Exp $
*
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.jsf.tag;

import com.sun.faces.taglib.html_basic.CommandButtonTag;

/**
 * <p>ButtonBarItem ...</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.4 $
 */
public class ButtonBarItem extends CommandButtonTag
{
	public String getComponentType()
	{
		return "SakaiButtonBarItem";
	}

	public String getRendererType()
	{
		return "SakaiButtonBarItem";
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/tag/ButtonBarItem.java,v 1.4 2004/06/22 03:11:04 ggolden Exp $
*
**********************************************************************************/
