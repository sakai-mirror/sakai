/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/custom/SakaiFacesContextFactory.java,v 1.8 2004/08/06 01:28:47 ggolden.umich.edu Exp $
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

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.util.Setup;

import com.sun.faces.context.FacesContextFactoryImpl;

/**
 * <p>SakaiFacesContextFactory extends the sun implementation adding integration with the ContextService as needed
 * for Sakai component use.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.8 $
 */
public class SakaiFacesContextFactory extends FacesContextFactoryImpl
{
	protected class ContextWrapper extends FacesContext
	{
		protected FacesContext m_context = null;

		public ContextWrapper(FacesContext ctx)
		{
			m_context = ctx;

			// setup the current service
			try
			{
				Setup.setup((HttpServletRequest) getExternalContext().getRequest(),
						(HttpServletResponse) getExternalContext().getResponse());
			}
			catch (Exception e)
			{
				Logger.warn(this + "Setup exception: " + e);
			}
			
			// pick up any messages for this mode from a prior (redirected) response
			MessageSaver.restoreMessages(ctx);
		}

		/**
		 * {@inheritDoc}
		 */
		public Application getApplication()
		{
			return m_context.getApplication();
		}

		/**
		 * {@inheritDoc}
		 */
		public Iterator getClientIdsWithMessages()
		{
			return m_context.getClientIdsWithMessages();
		}

		/**
		 * {@inheritDoc}
		 */
		public ExternalContext getExternalContext()
		{
			return m_context.getExternalContext();
		}

		/**
		 * {@inheritDoc}
		 */
		public Severity getMaximumSeverity()
		{
			return m_context.getMaximumSeverity();
		}

		/**
		 * {@inheritDoc}
		 */
		public Iterator getMessages()
		{
			return m_context.getMessages();
		}

		/**
		 * {@inheritDoc}
		 */
		public Iterator getMessages(String clientId)
		{
			return m_context.getMessages(clientId);
		}

		/**
		 * {@inheritDoc}
		 */
		public RenderKit getRenderKit()
		{
			return m_context.getRenderKit();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean getRenderResponse()
		{
			return m_context.getRenderResponse();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean getResponseComplete()
		{
			return m_context.getResponseComplete();
		}

		/**
		 * {@inheritDoc}
		 */
		public ResponseStream getResponseStream()
		{
			return m_context.getResponseStream();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setResponseStream(ResponseStream responseStream)
		{
			m_context.setResponseStream(responseStream);
		}

		/**
		 * {@inheritDoc}
		 */
		public ResponseWriter getResponseWriter()
		{
			return m_context.getResponseWriter();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setResponseWriter(ResponseWriter responseWriter)
		{
			m_context.setResponseWriter(responseWriter);
		}

		/**
		 * {@inheritDoc}
		 */
		public UIViewRoot getViewRoot()
		{
			return m_context.getViewRoot();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setViewRoot(UIViewRoot root)
		{
			m_context.setViewRoot(root);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addMessage(String clientId, FacesMessage message)
		{
			m_context.addMessage(clientId, message);
		}

		/**
		 * {@inheritDoc}
		 */
		public void release()
		{
			m_context.release();

			// cleanup the current service
			CurrentService.clearInThread();
		}

		/**
		 * {@inheritDoc}
		 */
		public void renderResponse()
		{
			m_context.renderResponse();
		}

		/**
		 * {@inheritDoc}
		 */
		public void responseComplete()
		{
			m_context.responseComplete();
		}
	}

	/**
	 * 
	 */
	public SakaiFacesContextFactory()
	{
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
		throws FacesException
	{
		FacesContext ctx = super.getFacesContext(context, request, response, lifecycle);
		return new ContextWrapper(ctx);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/custom/SakaiFacesContextFactory.java,v 1.8 2004/08/06 01:28:47 ggolden.umich.edu Exp $
*
**********************************************************************************/
