/**********************************************************************************
*
* $Header: /cvs/sakai/module/src/java/org/sakaiproject/component/sample/myjsf/MyCustomJSFRenderer.java,v 1.1 2004/12/03 01:19:21 janderse.umich.edu Exp $
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

package org.sakaiproject.component.sample.myjsf;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

/**
 * Example of custom JSF component just for this Sakai tool. 
 */
public class MyCustomJSFRenderer extends Renderer
{
	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof org.sakaiproject.component.sample.myjsf.MyCustomJSFComponent);
	}

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		writer.write("my_custom_tag - <b>This is the output of my custom JSF tag!</b>");

		return;
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
	}

	public boolean getRendersChildren()
	{
		return false;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/module/src/java/org/sakaiproject/component/sample/myjsf/MyCustomJSFRenderer.java,v 1.1 2004/12/03 01:19:21 janderse.umich.edu Exp $
*
**********************************************************************************/
