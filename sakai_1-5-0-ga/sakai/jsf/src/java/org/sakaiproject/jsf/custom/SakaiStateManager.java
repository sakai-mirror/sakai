/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/custom/SakaiStateManager.java,v 1.8 2005/02/08 20:42:40 janderse.umich.edu Exp $
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

package org.sakaiproject.jsf.custom;

import java.util.HashSet;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;

import com.sun.faces.application.StateManagerImpl;

/**
 * <p>SakaiStateManager extends the functionality of the default
 * StateManager to sometimes store the JSF view (aka JSF component tree)
 * in the current tool state.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.8 $
 */
public class SakaiStateManager extends StateManagerImpl
{

	/** Namespace prefix in tool state for views. */
	protected static final String NMS = "org.sakaiproject.view:";

	/**
	 * <p>Find the view stored in the current request's ToolState if we have one.</p>
	 * {@inheritDoc}
	 */
	public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId)
	{
		if (isSavingStateInClient(context))
		{
			return super.restoreView(context, viewId, renderKitId);
		}

		// if we have tool state
		SessionState state = PortalService.getCurrentToolState();
		if (state == null)
		{
			return super.restoreView(context, viewId, renderKitId);
		}

		UIViewRoot viewRoot = (UIViewRoot) state.getAttribute(NMS + viewId);
		return viewRoot;
	}

	/**
	 * <p>Store the view stored in the current request's ToolState if we have one.</p>
	 * {@inheritDoc}
	 */
	public SerializedView saveSerializedView(FacesContext context)
	{
		if (isSavingStateInClient(context))
		{
			return super.saveSerializedView(context);
		}

		// if we have tool state
		SessionState state = PortalService.getCurrentToolState();
		if (state == null)
		{
			return super.saveSerializedView(context);
		}

		// if transient let super handle it
		UIViewRoot viewRoot = context.getViewRoot();
		if (viewRoot.isTransient())
		{
			return super.saveSerializedView(context);
		}

		// honor the transient property and remove children from the tree
		// that are marked transient.
		removeTransientChildrenAndFacets(context, viewRoot, new HashSet());

		state.setAttribute(NMS + viewRoot.getViewId(), viewRoot);
		return null;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/custom/SakaiStateManager.java,v 1.8 2005/02/08 20:42:40 janderse.umich.edu Exp $
*
**********************************************************************************/
