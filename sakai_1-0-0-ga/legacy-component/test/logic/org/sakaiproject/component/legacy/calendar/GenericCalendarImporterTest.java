/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/test/logic/org/sakaiproject/component/legacy/calendar/GenericCalendarImporterTest.java,v 1.2 2004/06/22 03:14:03 ggolden Exp $
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
package org.sakaiproject.component.legacy.calendar;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.sakaiproject.exception.ImportException;

/**
 * Test Case for the CalendarImporter class.
 */
public class GenericCalendarImporterTest extends TestCase
{
	private GenericCalendarImporter scheduleImporter;

	/**
	 * Constructor for ScheduleImporterTest.
	 * @param arg0
	 */
	public GenericCalendarImporterTest(String arg0)
	{
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		scheduleImporter = new GenericCalendarImporter(); 
		scheduleImporter.init();
	}
	
	/**
	 * Make sure that bad date formats cause the import to fail.
	 */
	final public void testBadDateFormat()
	{
		Map columnMap = new HashMap();
		StringBuffer buffer = new StringBuffer();
		
		// Make the column headers
		buffer.append("TITLE,DESCRIPTION,DATE,START,END,LENGTH,TYPE,FREQUENCY,INTERVAL,LOCATION,ENDS,REPEAT,\"QUOTED HEADER\"");
		
		buffer.append("\n");
		
		columnMap.put("TITLE", GenericCalendarImporter.TITLE_PROPERTY_NAME);
		columnMap.put("DESCRIPTION", GenericCalendarImporter.DESCRIPTION_PROPERTY_NAME);
		columnMap.put("DATE", GenericCalendarImporter.DATE_PROPERTY_NAME);
		columnMap.put("START", GenericCalendarImporter.START_TIME_PROPERTY_NAME);
		columnMap.put("END", GenericCalendarImporter.END_TIME_PROPERTY_NAME);
		columnMap.put("LENGTH", GenericCalendarImporter.DURATION_PROPERTY_NAME);
		columnMap.put("TYPE", GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME);
		columnMap.put("FREQUENCY", GenericCalendarImporter.FREQUENCY_PROPERTY_NAME);
		columnMap.put("INTERVAL", GenericCalendarImporter.INTERVAL_PROPERTY_NAME);
		columnMap.put("LOCATION", GenericCalendarImporter.LOCATION_PROPERTY_NAME);
		columnMap.put("ENDS", GenericCalendarImporter.ENDS_PROPERTY_NAME);
		columnMap.put("REPEAT", GenericCalendarImporter.REPEAT_PROPERTY_NAME);
		columnMap.put("QUOTED HEADER", "QUOTED_PROPERTY");
		
		buffer.append("Test Event 1,This is test event 1,13/50/2004,2:00 PM,3:30 PM,1:30,Activity,,,Taubman Library,,,\"Quoted string\"");
		buffer.append("\n");

		try
		{
			scheduleImporter.doImport(
				GenericCalendarImporter.CSV_IMPORT,
				new ByteArrayInputStream(buffer.toString().getBytes()),
				columnMap, null);
					
			fail("Didn't catch bad date format");
		}
		catch (ImportException e)
		{
			// We should get here when we detect a bad format.
		}
	}
	
	/**
	 * Make sure that bad time formats cause the import to fail.
	 */
	final public void testBadTimeFormat()
	{
		Map columnMap = new HashMap();
		StringBuffer buffer = new StringBuffer();
		
		// Make the column headers
		String header = "TITLE,DESCRIPTION,DATE,START,END,LENGTH,TYPE,FREQUENCY,INTERVAL,LOCATION,ENDS,REPEAT,\"QUOTED HEADER\"";
		buffer.append(header);
		
		buffer.append("\n");
		
		columnMap.put("TITLE", GenericCalendarImporter.TITLE_PROPERTY_NAME);
		columnMap.put("DESCRIPTION", GenericCalendarImporter.DESCRIPTION_PROPERTY_NAME);
		columnMap.put("DATE", GenericCalendarImporter.DATE_PROPERTY_NAME);
		columnMap.put("START", GenericCalendarImporter.START_TIME_PROPERTY_NAME);
		columnMap.put("END", GenericCalendarImporter.END_TIME_PROPERTY_NAME);
		columnMap.put("LENGTH", GenericCalendarImporter.DURATION_PROPERTY_NAME);
		columnMap.put("TYPE", GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME);
		columnMap.put("FREQUENCY", GenericCalendarImporter.FREQUENCY_PROPERTY_NAME);
		columnMap.put("INTERVAL", GenericCalendarImporter.INTERVAL_PROPERTY_NAME);
		columnMap.put("LOCATION", GenericCalendarImporter.LOCATION_PROPERTY_NAME);
		columnMap.put("ENDS", GenericCalendarImporter.ENDS_PROPERTY_NAME);
		columnMap.put("REPEAT", GenericCalendarImporter.REPEAT_PROPERTY_NAME);
		columnMap.put("QUOTED HEADER", "QUOTED_PROPERTY");
		
		buffer.append("Test Event 1,This is test event 1,5/27/2004,55:00 PM,3:30 PM,1:30,Activity,,,Taubman Library,,,\"Quoted string\"");
		buffer.append("\n");

		try
		{
			scheduleImporter.doImport(
				GenericCalendarImporter.CSV_IMPORT,
				new ByteArrayInputStream(buffer.toString().getBytes()),
				columnMap,
				null);

			fail("Didn't catch bad time format");
		}
		catch (ImportException e)
		{
			// We should get here when we detect a bad format.
		}
		
		buffer.setLength(0);
		buffer.append(header);
		buffer.append("\n");
		buffer.append("Test Event 1,This is test event 1,5/27/2004,2:00 PM,:30 PM,1:30,Activity,,,Taubman Library,,,\"Quoted string\"");
		buffer.append("\n");

		try
		{
			scheduleImporter.doImport(
				GenericCalendarImporter.CSV_IMPORT,
				new ByteArrayInputStream(buffer.toString().getBytes()),
				columnMap,
				null);

			fail("Didn't catch bad time format");
		}
		catch (ImportException e)
		{
			// We should get here when we detect a bad format.
	}
	}
	
//	/**
//	 * 
//	 */
//	final public void testNonRecurringImport()
//	{
//		Map columnMap = new HashMap();
//		List rowList = null;
//		StringBuffer buffer = new StringBuffer();
//		
//		// Make the column headers
//		buffer.append("TITLE,DESCRIPTION,DATE,START,END,LENGTH,TYPE,FREQUENCY,INTERVAL,LOCATION,ENDS,REPEAT,\"QUOTED HEADER\"");
//		
//		buffer.append("\n");
//		
//		columnMap.put("TITLE", GenericScheduleImporter.TITLE_PROPERTY_NAME);
//		columnMap.put("DESCRIPTION", GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME);
//		columnMap.put("DATE", GenericScheduleImporter.DATE_PROPERTY_NAME);
//		columnMap.put("START", GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//		columnMap.put("END", GenericScheduleImporter.END_TIME_PROPERTY_NAME);
//		columnMap.put("LENGTH", GenericScheduleImporter.DURATION_PROPERTY_NAME);
//		columnMap.put("TYPE", GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME);
//		columnMap.put("FREQUENCY", GenericScheduleImporter.FREQUENCY_PROPERTY_NAME);
//		columnMap.put("INTERVAL", GenericScheduleImporter.INTERVAL_PROPERTY_NAME);
//		columnMap.put("LOCATION", GenericScheduleImporter.LOCATION_PROPERTY_NAME);
//		columnMap.put("ENDS", GenericScheduleImporter.ENDS_PROPERTY_NAME);
//		columnMap.put("REPEAT", GenericScheduleImporter.REPEAT_PROPERTY_NAME);
//		columnMap.put("QUOTED HEADER", "QUOTED_PROPERTY");
//		
//		buffer.append("Test Event 1,This is test event 1,5/27/2004,2:00 PM,3:30 PM,1:30,Activity,,,Taubman Library,,,\"Quoted string\"");
//		buffer.append("\n");
//
//		buffer.append("Test Event 2,This is test event 2,5/28/2004,5:00 PM,8:00 PM,3,Cancellation,,,Taubman Library Basement,,");
//		buffer.append("\n");
//
//		// Add a third blank row
//		buffer.append("\n");
//		
//		// Try a GMT time format
//		buffer.append("Test Event 2,This is test event 2,5/28/2004,17:00,20:00,3,Cancellation,,,Taubman Library Basement,,");
//		buffer.append("\n");
//
//		try
//		{
//			rowList =
//				scheduleImporter.doImport(
//					Importer.CSV_IMPORT,
//					new ByteArrayInputStream(buffer.toString().getBytes()),
//					columnMap,
//					null);
//		}
//		catch (ImportException e)
//		{
//			fail("Import caused exception");
//		}
//		
//		int rowNumber = 0;
//		
//		Iterator rowIterator = rowList.iterator();
//		
//		while ( rowIterator.hasNext() )
//		{
//			Map activityProperties = (Map) rowIterator.next();
//			
//			if ( rowNumber == 0 )
//			{
//				assertEquals("Unexpected import value", "Test Event 1", (String)activityProperties.get(GenericScheduleImporter.TITLE_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "This is test event 1", (String)activityProperties.get(GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//
//				Date activityDate = (Date)activityProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "05/27/04", GenericScheduleImporter.DATE_FORMATTER.format(activityDate));
//				
//				Date activityStartTime = (Date)activityProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "02:00 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityStartTime));
//
//				Date activityEndTime = (Date)activityProperties.get(GenericScheduleImporter.END_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "03:30 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityEndTime));
//
//				assertEquals("Unexpected import value", 90, ((Integer)activityProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME)).intValue());
//				
//				assertEquals("Unexpected import value", "Activity", (String)activityProperties.get(GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.FREQUENCY_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.INTERVAL_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "Taubman Library", (String)activityProperties.get(GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.ENDS_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.REPEAT_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "Quoted string", activityProperties.get("QUOTED_PROPERTY"));
//			}
//			else
//			if ( rowNumber == 1 )
//			{
//				assertEquals("Unexpected import value", "Test Event 2", (String)activityProperties.get(GenericScheduleImporter.TITLE_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "This is test event 2", (String)activityProperties.get(GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//
//				Date activityDate = (Date)activityProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "05/28/04", GenericScheduleImporter.DATE_FORMATTER.format(activityDate));
//				
//				Date activityStartTime = (Date)activityProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "05:00 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityStartTime));
//
//				Date activityEndTime = (Date)activityProperties.get(GenericScheduleImporter.END_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "08:00 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityEndTime));
//
//				assertEquals("Unexpected import value", 180, ((Integer)activityProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME)).intValue());
//				
//				assertEquals("Unexpected import value", "Cancellation", (String)activityProperties.get(GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.FREQUENCY_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.INTERVAL_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "Taubman Library Basement", (String)activityProperties.get(GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.ENDS_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.REPEAT_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get("QUOTED_PROPERTY"));
//			}
//			else
//			if ( rowNumber == 2 )
//			{
//				// The first field will be an empty string, but everything
//				// else should be null.
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.TITLE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.END_TIME_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME));
//				
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.FREQUENCY_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.INTERVAL_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.ENDS_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.REPEAT_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get("QUOTED_PROPERTY"));
//			}
//			
//			else
//			if ( rowNumber == 3 )
//			{
//				assertEquals("Unexpected import value", "Test Event 2", (String)activityProperties.get(GenericScheduleImporter.TITLE_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "This is test event 2", (String)activityProperties.get(GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//
//				Date activityDate = (Date)activityProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "05/28/04", GenericScheduleImporter.DATE_FORMATTER.format(activityDate));
//				
//				Date activityStartTime = (Date)activityProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "05:00 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityStartTime));
//
//				Date activityEndTime = (Date)activityProperties.get(GenericScheduleImporter.END_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "08:00 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityEndTime));
//
//				assertEquals("Unexpected import value", 180, ((Integer)activityProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME)).intValue());
//				
//				assertEquals("Unexpected import value", "Cancellation", (String)activityProperties.get(GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.FREQUENCY_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.INTERVAL_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "Taubman Library Basement", (String)activityProperties.get(GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.ENDS_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.REPEAT_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get("QUOTED_PROPERTY"));
//			}
//
//			rowNumber++; 
//		}
//		
//		assertEquals("Wrong ", 4, rowList.size());
//			
//	}
	
//	/**
//	 * Test importing recurring events
//	 */
//	final public void testRecurringImport()
//	{
//		Map columnMap = new HashMap();
//		List rowList = null;
//		StringBuffer buffer = new StringBuffer();
//		
//		// Make the column headers
//		buffer.append("TITLE,DESCRIPTION,DATE,START,END,LENGTH,TYPE,FREQUENCY,INTERVAL,LOCATION,ENDS,REPEAT,\"QUOTED HEADER\"");
//		
//		buffer.append("\n");
//		
//		columnMap.put("TITLE", GenericScheduleImporter.TITLE_PROPERTY_NAME);
//		columnMap.put("DESCRIPTION", GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME);
//		columnMap.put("DATE", GenericScheduleImporter.DATE_PROPERTY_NAME);
//		columnMap.put("START", GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//		columnMap.put("END", GenericScheduleImporter.END_TIME_PROPERTY_NAME);
//		columnMap.put("LENGTH", GenericScheduleImporter.DURATION_PROPERTY_NAME);
//		columnMap.put("TYPE", GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME);
//		columnMap.put("FREQUENCY", GenericScheduleImporter.FREQUENCY_PROPERTY_NAME);
//		columnMap.put("INTERVAL", GenericScheduleImporter.INTERVAL_PROPERTY_NAME);
//		columnMap.put("LOCATION", GenericScheduleImporter.LOCATION_PROPERTY_NAME);
//		columnMap.put("ENDS", GenericScheduleImporter.ENDS_PROPERTY_NAME);
//		columnMap.put("REPEAT", GenericScheduleImporter.REPEAT_PROPERTY_NAME);
//		columnMap.put("QUOTED HEADER", "QUOTED_PROPERTY");
//		
//		buffer.append("Test Event 1,This is test event 1,5/27/2004,2:00 PM,3:30 PM,1:30,Activity,daily,2,Taubman Library,2/3/2005,9,\"Quoted string\"");
//		buffer.append("\n");
//
//		buffer.append("Test Event 2,This is test event 2,5/28/2004,5:00 PM,8:00 PM,3,Cancellation,weekly,3,Taubman Library Basement,2/6/2005,10,");
//		buffer.append("\n");
//		
//		try
//		{
//			rowList =
//				scheduleImporter.doImport(
//					Importer.CSV_IMPORT,
//					new ByteArrayInputStream(buffer.toString().getBytes()),
//					columnMap,
//					null);
//		}
//		catch (ImportException e)
//		{
//			fail("Import caused exception");
//		}
//		
//		int rowNumber = 0;
//		
//		Iterator rowIterator = rowList.iterator();
//		
//		while ( rowIterator.hasNext() )
//		{
//			Map activityProperties = (Map) rowIterator.next();
//			
//			if ( rowNumber == 0 )
//			{
//				assertEquals("Unexpected import value", "Test Event 1", (String)activityProperties.get(GenericScheduleImporter.TITLE_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "This is test event 1", (String)activityProperties.get(GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//
//				Date activityDate = (Date)activityProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "05/27/04", GenericScheduleImporter.DATE_FORMATTER.format(activityDate));
//				
//				Date activityStartTime = (Date)activityProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "02:00 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityStartTime));
//
//				Date activityEndTime = (Date)activityProperties.get(GenericScheduleImporter.END_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "03:30 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityEndTime));
//
//				assertEquals("Unexpected import value", 90, ((Integer)activityProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME)).intValue());
//				
//				assertEquals("Unexpected import value", "Activity", (String)activityProperties.get(GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "daily", activityProperties.get(GenericScheduleImporter.FREQUENCY_PROPERTY_NAME));
//				assertEquals("Unexpected import value", 2, ((Integer)activityProperties.get(GenericScheduleImporter.INTERVAL_PROPERTY_NAME)).intValue());
//				assertEquals("Unexpected import value", "Taubman Library", (String)activityProperties.get(GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//
//				Date activityEndDate = (Date)activityProperties.get(GenericScheduleImporter.ENDS_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "02/03/05", GenericScheduleImporter.DATE_FORMATTER.format(activityEndDate));
//
//				assertEquals("Unexpected import value", 9, ((Integer)activityProperties.get(GenericScheduleImporter.REPEAT_PROPERTY_NAME)).intValue());
//				assertEquals("Unexpected import value", "Quoted string", activityProperties.get("QUOTED_PROPERTY"));
//			}
//			else
//			if ( rowNumber == 1 )
//			{
//				assertEquals("Unexpected import value", "Test Event 2", (String)activityProperties.get(GenericScheduleImporter.TITLE_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "This is test event 2", (String)activityProperties.get(GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//
//				Date activityDate = (Date)activityProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "05/28/04", GenericScheduleImporter.DATE_FORMATTER.format(activityDate));
//				
//				Date activityStartTime = (Date)activityProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "05:00 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityStartTime));
//
//				Date activityEndTime = (Date)activityProperties.get(GenericScheduleImporter.END_TIME_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "08:00 PM", GenericScheduleImporter.TIME_FORMATTER.format(activityEndTime));
//
//				assertEquals("Unexpected import value", 180, ((Integer)activityProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME)).intValue());
//				
//				assertEquals("Unexpected import value", "Cancellation", (String)activityProperties.get(GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME));
//				assertEquals("Unexpected import value", "weekly", activityProperties.get(GenericScheduleImporter.FREQUENCY_PROPERTY_NAME));
//				assertEquals("Unexpected import value", 3, ((Integer)activityProperties.get(GenericScheduleImporter.INTERVAL_PROPERTY_NAME)).intValue());
//				assertEquals("Unexpected import value", "Taubman Library Basement", (String)activityProperties.get(GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//
//				Date activityEndDate = (Date)activityProperties.get(GenericScheduleImporter.ENDS_PROPERTY_NAME);
//				assertEquals("Unexpected import value", "02/06/05", GenericScheduleImporter.DATE_FORMATTER.format(activityEndDate));
//
//				assertEquals("Unexpected import value", 10, ((Integer)activityProperties.get(GenericScheduleImporter.REPEAT_PROPERTY_NAME)).intValue());
//				assertNull("Unexpected import value", activityProperties.get("QUOTED_PROPERTY"));
//			}
//			else
//			if ( rowNumber == 2 )
//			{
//				// The first field will be an empty string, but everything
//				// else should be null.
//				assertEquals("Unexpected import value", "", activityProperties.get(GenericScheduleImporter.TITLE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.END_TIME_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME));
//				
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.ITEM_TYPE_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.FREQUENCY_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.INTERVAL_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.ENDS_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get(GenericScheduleImporter.REPEAT_PROPERTY_NAME));
//				assertNull("Unexpected import value", activityProperties.get("QUOTED_PROPERTY"));
//			}
//			
//			rowNumber++; 
//		}
//			
//		assertEquals("Wrong ", 2, rowList.size());
//	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();

		scheduleImporter.destroy();
	}

}
