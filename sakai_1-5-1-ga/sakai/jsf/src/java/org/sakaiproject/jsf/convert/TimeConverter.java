/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/convert/TimeConverter.java,v 1.2 2004/06/22 03:11:05 ggolden Exp $
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

package org.sakaiproject.jsf.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;

/**
 * <p>TimeConverter is ...</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.2 $
 */
public class TimeConverter implements Converter
{
	/** The standard converter id for this converter. */
	public static final String CONVERTER_ID = "SakaiTimeConverter";

	/**
	 * {@inheritDoc}
	 */
	public Object getAsObject(FacesContext context, UIComponent component, String value)
	{
		if (context == null || component == null)
		{
			throw new NullPointerException();
		}

		return TimeService.newTimeGmt(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAsString(FacesContext context, UIComponent component, Object value)
	{
		if (context == null || component == null)
		{
			throw new NullPointerException();
		}

		// If the specified value is null, return a zero-length String
		if (value == null)
		{
			return "";
		}

		// If the incoming value is still a string, play nice
		// and return the value unmodified
		if (value instanceof String)
		{
			return (String) value;
		}

		try
		{
			return (((Time) value).toStringLocalFull());
		}
		catch (Exception e)
		{
			throw new ConverterException(e);
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/convert/TimeConverter.java,v 1.2 2004/06/22 03:11:05 ggolden Exp $
*
**********************************************************************************/
