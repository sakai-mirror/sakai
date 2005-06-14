/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/test/logic/org/sakaiproject/component/legacy/calendar/readers/OutlookReaderTest.java,v 1.3 2004/06/22 03:14:03 ggolden Exp $
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
package org.sakaiproject.component.legacy.calendar.readers;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import org.sakaiproject.component.legacy.calendar.readers.Reader.ReaderImportRowHandler;
import org.sakaiproject.exception.ImportException;


/**
 * TestCast for OutlookReader
 */
public class OutlookReaderTest extends TestCase
{

	final static private int ROW_COUNT =  4;
	final static private int COLUMN_COUNT =  22;
		
	String outlookImportFile1 =
		"\"Subject\",\"Start Date\",\"Start Time\",\"End Date\",\"End Time\",\"All day event\",\"Reminder on/off\",\"Reminder Date\",\"Reminder Time\",\"Meeting Organizer\",\"Required Attendees\",\"Optional Attendees\",\"Meeting Resources\",\"Billing Information\",\"Categories\",\"Description\",\"Location\",\"Mileage\",\"Priority\",\"Private\",\"Sensitivity\",\"Show time as\"" + "\n" +
		"\"Test Event 1\",\"6/3/2004\",\"8:00:00 AM\",\"6/3/2004\",\"9:00:00 AM\",\"False\",\"False\",\"6/3/2004\",\"7:45:00 AM\",\"Unknown\",,,,,,\"This is the text associated with the event" + "\n" +
		"This is line 2" + "\n" +
		"This is line 3" + "\n" +
		"     This line has spaces in front of it" + "\n" +
		"\",\"Nowhere\",,\"Normal\",\"False\",\"Normal\",\"2\"" + "\n" +
		"\"Test Event 1\",\"8/3/2005\",\"8:00:00 AM\",\"8/3/2005\",\"9:00:00 AM\",\"False\",\"False\",\"8/3/2005\",\"7:45:00 AM\",\"Unknown\",,,,,,\"This is the text associated with the event" + "\n" +
		"\",\"Nowhere\",,\"Normal\",\"False\",\"Normal\",\"2\"" + "\n" +
		"\"Test Event 3\",\"6/7/2004\",\"8:00:00 AM\",\"6/10/2004\",\"1:00:00 PM\",\"False\",\"False\",\"6/7/2004\",\"7:45:00 AM\",,,,,,,\"This is a description" + "\n" +
		"\",\"Somewhere\",,\"Normal\",\"False\",\"Normal\",\"2\"" + "\n" +
		"\"Test Event 4\",\"6/3/2004\",\"12:00:00 AM\",\"6/4/2004\",\"12:00:00 AM\",\"True\",\"False\",\"6/2/2004\",\"11:45:00 PM\",,,,,,,\"This is test event 4" + "\n" +
		"\",\"\",,\"Normal\",\"False\",\"Normal\",\"3\"" + "\n";
	
	private OutlookReader outlookReader; 
	
	/**
	 * Constructor for OutlookReaderTest.
	 * @param arg0
	 */
	public OutlookReaderTest(String arg0)
	{
		super(arg0);
		outlookReader = new OutlookReader();
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	final public void testImportStreamFromDelimitedFile()
	{
		ByteArrayInputStream inStream = new ByteArrayInputStream(outlookImportFile1.getBytes());
		
		final String [][] cellValues = new String [ROW_COUNT][];
		
		for ( int i=0 ; i < ROW_COUNT; i++ )
		{
			cellValues[i] = new String [COLUMN_COUNT];
		}

		try
		{
			// Do this nonsense since we're using an annoymous inner class
			// and we can't modify a simple integer/Integer variable.
			final Integer [] rowNumber = { new Integer(0) };

			outlookReader.importStreamFromDelimitedFile(inStream, 
				new ReaderImportRowHandler()
				{
			
					public void handleRow(Iterator columnIterator) throws ImportException
					{
						while ( columnIterator.hasNext() )
						{
							Reader.ReaderImportCell column =
								(Reader.ReaderImportCell) columnIterator.next();

							cellValues[rowNumber[0].intValue()][column.getColumnNumber()] =
								column.getCellValue();
						}
						
						rowNumber[0] = new Integer(rowNumber[0].intValue() + 1);
					}
				}
			);
		}
		catch (ImportException e)
		{
			fail("Encountered exception during import of Outlook file");
		}
		
		assertEquals("Unexpected import value", "Test Event 1", cellValues[0][0]);
		assertEquals("Unexpected import value", "6/3/2004", cellValues[0][1]);
		assertEquals("Unexpected import value", "8:00:00 AM", cellValues[0][2]);
		assertEquals("Unexpected import value", "6/3/2004", cellValues[0][3]);
		assertEquals("Unexpected import value", "9:00:00 AM", cellValues[0][4]);
		assertEquals("Unexpected import value", "False", cellValues[0][5]);
		assertEquals("Unexpected import value", "False", cellValues[0][6]);
		assertEquals("Unexpected import value", "6/3/2004", cellValues[0][7]);
		assertEquals("Unexpected import value", "7:45:00 AM", cellValues[0][8]);
		assertEquals("Unexpected import value", "Unknown", cellValues[0][9]);
		assertEquals("Unexpected import value", "", cellValues[0][10]);
		assertEquals("Unexpected import value", "", cellValues[0][11]);
		assertEquals("Unexpected import value", "", cellValues[0][12]);
		assertEquals("Unexpected import value", "", cellValues[0][13]);
		assertEquals("Unexpected import value", "", cellValues[0][14]);
		assertEquals("Unexpected import value", "This is the text associated with the event\nThis is line 2\nThis is line 3\n     This line has spaces in front of it\n", cellValues[0][15]);
		assertEquals("Unexpected import value", "Nowhere", cellValues[0][16]);
		assertEquals("Unexpected import value", "", cellValues[0][17]);
		assertEquals("Unexpected import value", "Normal", cellValues[0][18]);
		assertEquals("Unexpected import value", "False", cellValues[0][19]);
		assertEquals("Unexpected import value", "Normal", cellValues[0][20]);
		assertEquals("Unexpected import value", "2", cellValues[0][21]);
		assertEquals("Unexpected import value", "Test Event 1", cellValues[1][0]);
		assertEquals("Unexpected import value", "8/3/2005", cellValues[1][1]);
		assertEquals("Unexpected import value", "8:00:00 AM", cellValues[1][2]);
		assertEquals("Unexpected import value", "8/3/2005", cellValues[1][3]);
		assertEquals("Unexpected import value", "9:00:00 AM", cellValues[1][4]);
		assertEquals("Unexpected import value", "False", cellValues[1][5]);
		assertEquals("Unexpected import value", "False", cellValues[1][6]);
		assertEquals("Unexpected import value", "8/3/2005", cellValues[1][7]);
		assertEquals("Unexpected import value", "7:45:00 AM", cellValues[1][8]);
		assertEquals("Unexpected import value", "Unknown", cellValues[1][9]);
		assertEquals("Unexpected import value", "", cellValues[1][10]);
		assertEquals("Unexpected import value", "", cellValues[1][11]);
		assertEquals("Unexpected import value", "", cellValues[1][12]);
		assertEquals("Unexpected import value", "", cellValues[1][13]);
		assertEquals("Unexpected import value", "", cellValues[1][14]);
		assertEquals("Unexpected import value", "This is the text associated with the event\n", cellValues[1][15]);
		assertEquals("Unexpected import value", "Nowhere", cellValues[1][16]);
		assertEquals("Unexpected import value", "", cellValues[1][17]);
		assertEquals("Unexpected import value", "Normal", cellValues[1][18]);
		assertEquals("Unexpected import value", "False", cellValues[1][19]);
		assertEquals("Unexpected import value", "Normal", cellValues[1][20]);
		assertEquals("Unexpected import value", "2", cellValues[1][21]);
		assertEquals("Unexpected import value", "Test Event 3", cellValues[2][0]);
		assertEquals("Unexpected import value", "6/7/2004", cellValues[2][1]);
		assertEquals("Unexpected import value", "8:00:00 AM", cellValues[2][2]);
		assertEquals("Unexpected import value", "6/10/2004", cellValues[2][3]);
		assertEquals("Unexpected import value", "1:00:00 PM", cellValues[2][4]);
		assertEquals("Unexpected import value", "False", cellValues[2][5]);
		assertEquals("Unexpected import value", "False", cellValues[2][6]);
		assertEquals("Unexpected import value", "6/7/2004", cellValues[2][7]);
		assertEquals("Unexpected import value", "7:45:00 AM", cellValues[2][8]);
		assertEquals("Unexpected import value", "", cellValues[2][9]);
		assertEquals("Unexpected import value", "", cellValues[2][10]);
		assertEquals("Unexpected import value", "", cellValues[2][11]);
		assertEquals("Unexpected import value", "", cellValues[2][12]);
		assertEquals("Unexpected import value", "", cellValues[2][13]);
		assertEquals("Unexpected import value", "", cellValues[2][14]);
		assertEquals("Unexpected import value", "This is a description\n", cellValues[2][15]);
		assertEquals("Unexpected import value", "Somewhere", cellValues[2][16]);
		assertEquals("Unexpected import value", "", cellValues[2][17]);
		assertEquals("Unexpected import value", "Normal", cellValues[2][18]);
		assertEquals("Unexpected import value", "False", cellValues[2][19]);
		assertEquals("Unexpected import value", "Normal", cellValues[2][20]);
		assertEquals("Unexpected import value", "2", cellValues[2][21]);
		assertEquals("Unexpected import value", "Test Event 4", cellValues[3][0]);
		assertEquals("Unexpected import value", "6/3/2004", cellValues[3][1]);
		assertEquals("Unexpected import value", "12:00:00 AM", cellValues[3][2]);
		assertEquals("Unexpected import value", "6/4/2004", cellValues[3][3]);
		assertEquals("Unexpected import value", "12:00:00 AM", cellValues[3][4]);
		assertEquals("Unexpected import value", "True", cellValues[3][5]);
		assertEquals("Unexpected import value", "False", cellValues[3][6]);
		assertEquals("Unexpected import value", "6/2/2004", cellValues[3][7]);
		assertEquals("Unexpected import value", "11:45:00 PM", cellValues[3][8]);
		assertEquals("Unexpected import value", "", cellValues[3][9]);
		assertEquals("Unexpected import value", "", cellValues[3][10]);
		assertEquals("Unexpected import value", "", cellValues[3][11]);
		assertEquals("Unexpected import value", "", cellValues[3][12]);
		assertEquals("Unexpected import value", "", cellValues[3][13]);
		assertEquals("Unexpected import value", "", cellValues[3][14]);
		assertEquals("Unexpected import value", "This is test event 4\n", cellValues[3][15]);
		assertEquals("Unexpected import value", "\"\"", cellValues[3][16]);
		assertEquals("Unexpected import value", "", cellValues[3][17]);
		assertEquals("Unexpected import value", "Normal", cellValues[3][18]);
		assertEquals("Unexpected import value", "False", cellValues[3][19]);
		assertEquals("Unexpected import value", "Normal", cellValues[3][20]);
		assertEquals("Unexpected import value", "3", cellValues[3][21]);
		
	//		The commented out code below was used to generate the above asserts.  The asserts were
	//		manually verified before being incorporated into the test.
//
//
//			 for ( int i=0 ; i < ROW_COUNT; i++ )
//			 {
//				 for ( int j=0; j < COLUMN_COUNT; j++ )
//				 {
//
//					 String line = null;
//				
//					 if ( cellValues[i][j] == null )
//					 {
//						 line = "assertNull(\"Unexpected import value\", cellValues[_ROW_][_COLUMN_]);"; 
//						 line = line.replaceAll("_ROW_", Integer.toString(i));
//						 line = line.replaceAll("_COLUMN_", Integer.toString(j));
//					 }
//					 else
//					 {
//						 line = "assertEquals(\"Unexpected import value\", \"_EXPECTED_\", cellValues[_ROW_][_COLUMN_]);";
//				
//						 line = line.replaceAll("_EXPECTED_", cellValues[i][j].replaceAll("\"", "\\\"").replaceAll("\n", "\\\\\\\\n"));
//						 line = line.replaceAll("_ROW_", Integer.toString(i));
//						 line = line.replaceAll("_COLUMN_", Integer.toString(j));
//					 }
//					 System.out.println(line);
//				 }
//			 }		
	}

//	final public void testDoImport()
//	{
//		ByteArrayInputStream inStream = new ByteArrayInputStream(outlookImportFile1.getBytes());
//
//		
//		try
//		{
//			List rows = outlookReader.doImport(inStream);
//
//			assertEquals("Incorrect number of rows imported", ROW_COUNT, rows.size());
//			
//			Iterator it = rows.iterator();
//			
//			int rowNumber = 0;
//		
//			while ( it.hasNext() )
//			{
//				Map rowProperties = (Map) it.next();
//				
//				if ( rowNumber == 0 )
//				{
//					assertEquals("Unexpected import value", "Test Event 1", rowProperties.get( GenericScheduleImporter.TITLE_PROPERTY_NAME));
//					assertEquals(
//						"Unexpected import value",
//						"This is the text associated with the event"
//							+ "\n"
//							+ "This is line 2"
//							+ "\n"
//							+ "This is line 3"
//							+ "\n"+
//							"     This line has spaces in front of it" + "\n" ,
//						rowProperties.get(
//							GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//					assertEquals("Unexpected import value", "Nowhere", rowProperties.get( GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//
//					Date activityStartDate = (Date)rowProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME);
//					assertEquals("Unexpected import value", "06/03/04", GenericScheduleImporter.DATE_FORMATTER.format(activityStartDate));
//
//					Date activityStartTime = (Date)rowProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//					assertEquals("Unexpected import value", "08:00 AM", GenericScheduleImporter.TIME_FORMATTER.format(activityStartTime));
//
//					Date activityEndTime = (Date)rowProperties.get(GenericScheduleImporter.END_TIME_PROPERTY_NAME);
//					assertEquals("Unexpected import value", "09:00 AM", GenericScheduleImporter.TIME_FORMATTER.format(activityEndTime));
//
//					// TODO - What about the end date?
//					//public final String END_DATE_HEADER = "End Date";	
//				}
//				else
//				if ( rowNumber == 1 )
//				{
//					// TODO 
//				}
//				if ( rowNumber == 2 )
//				{
//					// TODO 
//				}
//				else
//				if ( rowNumber == 3 )
//				{
//					// TODO 
//				}
//								
//				rowNumber++;
//			}
//		}
//		
//		catch (ImportException e)
//		{
//			fail("An exception occurred during import of an Outlook file.");
//		}
//	}

}
