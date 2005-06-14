/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/calendar/ExclusionRecurrenceRule.java,v 1.6 2004/06/22 03:13:57 ggolden Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.TimeZone;
import java.util.Vector;

import org.sakaiproject.service.legacy.calendar.RecurrenceRule;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeRange;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>ExclusionRecurrenceRule is a rule which excludes specific time ranges from a list of instances.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.6 $
*/
public class ExclusionRecurrenceRule
	implements RecurrenceRule
{
	/** The list of TimeRange values to exclude. */
	protected List m_ranges = null;

	/**
	* Construct.
	*/
	public ExclusionRecurrenceRule()
	{
		m_ranges = new Vector();

	}	// ExclusionRecurrenceRule

	/**
	* Construct with these limits.
	* @param ranges The list of ranges to exclude
	*/
	public ExclusionRecurrenceRule(List ranges)
	{
		m_ranges = new Vector(ranges);

	}	// ExclusionRecurrenceRule

	/**
	* Access the List of TimeRange values excluded.
	* @return the List of TimeRange values excluded.
	*/
	public List getRanges() { return m_ranges; }

	/**
	* Take values from this xml element
	* @param el The xml element.
	*/
	public void set(Element el)
	{
		// the children (time ranges)
		NodeList children = el.getChildNodes();
		final int length = children.getLength();
		for(int i = 0; i < length; i++)
		{
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			Element element = (Element)child;

			// look for a time range
			if (element.getTagName().equals("range"))
			{
				try
				{
					m_ranges.add(TimeService.newTimeRange(element.getAttribute("range")));
				}
				catch (Exception e) { Logger.warn(this + ".set: while reading time range: " + e); }
			}
		}

	}	// set

	/**
	* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
	* @param doc The DOM doc to contain the XML (or null for a string return).
	* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
	* @return The newly added element.
	*/
	public Element toXml(Document doc, Stack stack)
	{
		// add the "rule" element to the stack'ed element
		Element rule = doc.createElement("ex-rule");
		((Element)stack.peek()).appendChild(rule);

		// set the class name - old style for CHEF 1.2.10 compatibility
		rule.setAttribute("class", "org.chefproject.osid.calendar.ExclusionRecurrenceRule");

		// set the rule class name w/o package, for modern usage
		rule.setAttribute("name", "ExclusionRecurrenceRule");

		// set the ranges
		for (Iterator iRanges = m_ranges.iterator(); iRanges.hasNext();)
		{
			TimeRange range = (TimeRange) iRanges.next();

			Element rangeElement = doc.createElement("range");
			rule.appendChild(rangeElement);
			rangeElement.setAttribute("range", range.toString());
		}

		return rule;

	}	// toXml

	/**
	* Return a List of all RecurrenceInstance objects generated by this rule within the given time range, based on the
	* prototype first range, in time order.
	* @param prototype The prototype first TimeRange.
	* @param range A time range to limit the generated ranges.
	* @return a List of RecurrenceInstance generated by this rule in this range.
	*/
	public List generateInstances(TimeRange prototype, TimeRange range, TimeZone timeZone)
	{
		return new Vector();

	}	// generateInstances

	/**
	* Remove from the ranges list any RecurrenceInstance excluded by this rule.
	* @param ranges The list (RecurrenceInstance) of ranges.
	*/
	public void excludeInstances(List ranges)
	{
		Vector rv = new Vector();
		
		for (Iterator iInstances = ranges.iterator(); iInstances.hasNext();)
		{
			RecurrenceInstance ri = (RecurrenceInstance) iInstances.next();

			if (!m_ranges.contains(ri.getRange()))
			{
				rv.add(ri);
			}
		}

		ranges.clear();
		ranges.addAll(rv);

	}

	/**
	 * {@inheritDoc}
	 */
	public String getFrequencyDescription()
	{
		return "x";
	}

	/**
	 * {@inheritDoc}
	 */
	public Time getUntil()
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCount()
	{
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getInterval()
	{
		return 0;
	}

}	// ExclusionRecurrenceRule

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/calendar/ExclusionRecurrenceRule.java,v 1.6 2004/06/22 03:13:57 ggolden Exp $
*
**********************************************************************************/
