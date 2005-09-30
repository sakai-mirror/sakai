/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/renderer/DateInput.java,v 1.1 2005/03/31 04:16:55 ggolden.umich.edu Exp $
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

package org.sakaiproject.jsf.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

/**
 * <p>DateInput is an HTML renderer for the Sakai DateInput UIComponent in JSF.</p>
 * <p>Dates are rendered as three fields (year, month, day) and a picker widget.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public class DateInput extends Renderer
{
	protected static String[] MONTHS =
		{ "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	public boolean supportsComponentType(UIComponent component)
	{
		return (component instanceof org.sakaiproject.jsf.component.DateInput);
	}

//	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
//	{
//		TimeBreakdown tb = null;
//
//		Object currentObj = ((ValueHolder) component).getValue();
//		if (currentObj != null)
//		{
//			// the value should be a Time
//			if (currentObj instanceof Time)
//			{
//				Time t = (Time) currentObj;
//				tb = t.breakdownLocal();
//			}
//
//			// if it's a string, it must be a default time format GMT string
//			else if (currentObj instanceof String)
//			{
//				Time t = TimeService.newTimeGmt((String) currentObj);
//				tb = t.breakdownLocal();
//			}
//		}
//
//		// get the clientId (base) - the name (base) for our form field(s)
//		String clientId = component.getClientId(context);
//
//		ResponseWriter writer = context.getResponseWriter();
//		writer.write("<select name=\"" + clientId + "_month\" id=\"" + clientId + "_month\">\n");
//		for (int i = 0; i < 12; i++)
//		{
//			writer.write(
//				"<option value=\""
//					+ (i + 1)
//					+ "\""
//					+ (((tb != null) && (tb.getMonth() == (i + 1))) ? " selected=\"selected\"" : "")
//					+ ">"
//					+ MONTHS[i]
//					+ "</option>\n");
//		}
//		writer.write("</select>\n");
//
//		writer.write("<select name=\"" + clientId + "_day\" id=\"" + clientId + "_day\">");
//		for (int i = 1; i <= 31; i++)
//		{
//			writer.write(
//				"<option value=\""
//					+ i
//					+ "\""
//					+ (((tb != null) && (tb.getDay() == i)) ? " selected=\"selected\"" : "")
//					+ ">"
//					+ i
//					+ "</option>\n");
//		}
//		writer.write("</select>\n");
//
//		writer.write("<select name=\"" + clientId + "_year\" id=\"" + clientId + "_year\">");
//		for (int i = 2001; i <= 2008; i++)
//		{
//			writer.write(
//				"<option value=\""
//					+ i
//					+ "\""
//					+ (((tb != null) && (tb.getYear() == i)) ? " selected=\"selected\"" : "")
//					+ ">"
//					+ i
//					+ "</option>\n");
//		}
//		writer.write("</select>\n");
//	}
//
//	//	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
//	//	{
//	//		ResponseWriter writer = context.getResponseWriter();
//	//		writer.write("</div>");
//	//	}
//
//	/**
//	 * <p>Decode any new state of the specified {@link UIComponent}
//	 * from the request contained in the specified {@link FacesContext},
//	 * and store that state on the {@link UIComponent}.</p>
//	 *
//	 * <p>During decoding, events may be enqueued for later processing
//	 * (by event listeners that have registered an interest), by calling
//	 * <code>queueEvent()</code> on the associated {@link UIComponent}.
//	 * </p>
//	 *
//	 * @param context {@link FacesContext} for the request we are processing
//	 * @param component {@link UIComponent} to be decoded.
//	 *
//	 * @exception NullPointerException if <code>context</code>
//	 *  or <code>component</code> is <code>null</code>
//	 */
//	public void decode(FacesContext context, UIComponent component)
//	{
//		if (null == context || null == component || !(component instanceof org.sakaiproject.jsf.component.DateInput))
//		{
//			throw new NullPointerException();
//		}
//
//		// get the clientId (base) - the name (base) for our form field(s)
//		String clientId = component.getClientId(context);
//
//		Map requestParameterMap = context.getExternalContext().getRequestParameterMap();
//
//		// read the value(s)
//		try
//		{
//			String newYear = (String) requestParameterMap.get(clientId + "_year");
//			int year = Integer.parseInt(newYear);
//			String newMonth = (String) requestParameterMap.get(clientId + "_month");
//			int month = Integer.parseInt(newMonth);
//			String newDay = (String) requestParameterMap.get(clientId + "_day");
//			int day = Integer.parseInt(newDay);
//
//			// compute the new value
//			Time time = TimeService.newTimeLocal(year, month, day, 0, 0, 0, 0);
//			String newValue = time.toString();
//
//			// set it
//			org.sakaiproject.jsf.component.DateInput comp = (org.sakaiproject.jsf.component.DateInput) component;
//			comp.setSubmittedValue(newValue);
//		}
//		catch (Exception any)
//		{
//		}
//	}
//
//	/**
//	 * <p>Attempt to convert previously stored state information into an
//	 * object of the type required for this component (optionally using the
//	 * registered {@link javax.faces.convert.Converter} for this component,
//	 * if there is one).  If conversion is successful, the new value
//	 * should be returned from this method;  if not, a
//	 * {@link ConverterException} should be thrown.</p>
//	 * 
//	 * @param context {@link FacesContext} for the request we are processing
//	 * @param component {@link UIComponent} to be decoded.
//	 * @param submittedValue a value stored on the component during
//	 *    <code>decode</code>.
//	 * 
//	 * @exception ConverterException if the submitted value
//	 *   cannot be converted successfully.
//	 * @exception NullPointerException if <code>context</code>
//	 *  or <code>component</code> is <code>null</code>
//	 */
//	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
//		throws ConverterException
//	{
//		if ((context == null) || (component == null))
//		{
//			throw new NullPointerException();
//		}
//
//		// convert from the string, as set in decode (GMT time string) to the Time object of our model / local value
//		Time time = TimeService.newTimeGmt((String) submittedValue);
//
//		return time;
//	}

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();
		writer.write("<!--");
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException
	{
	}

	/**
	 * @param context FacesContext for the request we are processing
	 * @param component UIComponent to be rendered
	 * @exception IOException if an input/output error occurs while rendering
	 * @exception NullPointerException if <code>context</code> or <code>component</code> is null
	 */
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException
	{
		ResponseWriter writer = context.getResponseWriter();

		String txt = (String) component.getAttributes().get("text");
		if (txt != null)
		{
			writer.write(txt);
		}

		writer.write(" -->");
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/jsf/widgets-1/src/java/org/sakaiproject/jsf/renderer/DateInput.java,v 1.1 2005/03/31 04:16:55 ggolden.umich.edu Exp $
*
**********************************************************************************/
