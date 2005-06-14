/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/calendar/WeeklyRecurrenceRule.java,v 1.1 2005/04/13 23:33:36 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.component.legacy.calendar;

// import
import java.util.GregorianCalendar;
import java.util.Stack;

import org.sakaiproject.service.legacy.time.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>WeeklyRecurrenceRule is a time range generating rule that is based on a weekly recurrence.</p>
* <p>The recurrences happen on the same day-of-week, at the same time as the prototype.</p>
* <p>TODO: support changing the day-of-week of recurrences -ggolden</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public class WeeklyRecurrenceRule extends RecurrenceRuleBase
{
	/** The unique type / short frequency description. */
	protected final static String FREQ = "week";

	/**
	* Construct.
	*/
	public WeeklyRecurrenceRule()
	{
		super();
	}	// WeeklyRecurrenceRule

	/**
	* Construct with no  limits.
	* @param interval Every this many number of weeks: 1 would be weekly.
	*/
	public WeeklyRecurrenceRule(int interval)
	{
		super(interval);
	}	// WeeklyRecurrenceRule

	/**
	* Construct with count limit.
	* @param interval Every this many number of weeks: 1 would be weekly.
	* @param count For this many occurrences - if 0, does not limit.
	*/
	public WeeklyRecurrenceRule(int interval, int count)
	{
		super(interval, count);
	}	// WeeklyRecurrenceRule

	/**
	* Construct with time limit.
	* @param interval Every this many number of weeks: 1 would be weekly.
	* @param until No time ranges past this time are generated - if null, does not limit.
	*/
	public WeeklyRecurrenceRule(int interval, Time until)
	{
		super(interval, until);
	}	// WeeklyRecurrenceRule


	/**
	* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
	* @param doc The DOM doc to contain the XML (or null for a string return).
	* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
	* @return The newly added element.
	*/
	public Element toXml(Document doc, Stack stack)
	{
		// add the "rule" element to the stack'ed element
		Element rule = doc.createElement("rule");
		((Element)stack.peek()).appendChild(rule);

		// set the class name - old style for CHEF 1.2.10 compatibility
		rule.setAttribute("class", "org.chefproject.osid.calendar.WeeklyRecurrenceRule");

		// set the rule class name w/o package, for modern usage
		rule.setAttribute("name", "WeeklyRecurrenceRule");

		// Do the base class part.
		setBaseClassXML(rule);

		return rule;

	}	// toXml

	/* (non-Javadoc)
	 * @see org.chefproject.service.calendar.RecurrenceRuleBase#getRecurrenceType()
	 */
	protected int getRecurrenceType()
	{
		return GregorianCalendar.WEEK_OF_MONTH;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFrequencyDescription()
	{
		return FREQ;
	}

}	// WeeklyRecurrenceRule

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/calendar/WeeklyRecurrenceRule.java,v 1.1 2005/04/13 23:33:36 ggolden.umich.edu Exp $
*
**********************************************************************************/
