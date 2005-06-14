/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/test/logic/org/sakaiproject/component/legacy/calendar/readers/MeetingMakerReaderTest.java,v 1.3 2004/06/22 03:14:03 ggolden Exp $
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

import org.sakaiproject.component.legacy.calendar.readers.Reader.ReaderImportCell;
import org.sakaiproject.component.legacy.calendar.readers.Reader.ReaderImportRowHandler;
import org.sakaiproject.exception.ImportException;

/**
 * TestCast for MeetingMakerReader
 */
public class MeetingMakerReaderTest extends TestCase
{
	//
	// This is a simulated MeetingMaker Text export file.
	// It includes reminder and to-do sections to make sure
	// that we can reliably skip these.
	//
	final static private int ROW_COUNT_1 =  12;
	final static private int COLUMN_COUNT =  10;

	private final static String meetingMakerFile1Contents = 
		
		"Events\t" + "\n" + 
		"Title\tLocation\tDate\tStart Time\tDuration\tPrivate\tFlexible\tPublishable\tLabel\tAgenda/Notes" + "\n" + 
		"Staff Meeting\tMSIS zConference Room\t5/3/2004\t10:00\t02:00\t0\t0\t0\t\t" + "\n" + 
		"On-line grade recording\tMSIS zLarge Conference Room\t5/5/2004\t10:00\t01:00\t0\t0\t0\t\t\"To review java class\"" + "\n" + 
		"on-line grade recording Evaluation database detail\tMSIS zLarge Conference Room\t5/5/2004\t14:00\t01:00\t0\t0\t0\t\t" + "\n" + 
		"Portal Issues Meeting\tMSIS zLarge Conference Room\t5/6/2004\t10:00\t01:00\t0\t0\t0\t\t" + "\n" + 
		"c-tools SIG\tLanguage Resource Center Viewing Room - 2nd Floor MLB\t5/6/2004\t13:00\t02:00\t0\t0\t0\t\t" + "\n" + 
		"MSIS lunch\t\t5/12/2004\t11:45\t01:30\t0\t0\t0\t\t\"\t" + "\n" + 
		"Staff\t                  Lunch Day" + "\n" + 
		"SIS                          March 2004" + "\n" + 
		"FIS                          April 2004" + "\n" + 
		"RIS                         May 2004" + "\n" + 
		"\"" + "\n" + 
		"MCAT scores\tMSIS zLarge Conference Room\t5/17/2004\t09:00\t00:30\t0\t0\t0\t\t" + "\n" + 
		"Staff Meeting\tMSIS zConference Room\t5/17/2004\t10:00\t02:00\t0\t0\t0\t\t" + "\n" + 
		"Global Reach Survey\tMSIS zLarge Conference Room\t5/17/2004\t13:30\t01:00\t0\t0\t0\t\t" + "\n" + 
		"Test Activity\tSomewhere\t6/3/2004\t09:00\t01:00\t0\t0\t0\tLabel 1\t\"These are notes for a test activity." + "\n" + 
		"This is the second line." + "\n" + 
		"This is the third line." + "\n" + 
		"Quoted String -> 'This is a string in quotes' <- Quoted String\"" + "\n" + 
		"New Dashboard - Add to Prototype\t\t6/3/2004\t10:30\t01:00\t0\t0\t0\t\t" + "\n" + 
		"Forms on-line grading\tMSIS zLarge Conference Room\t5/28/2004\t14:00\t01:00\t0\t0\t0\t\t" + "\n" + 
		"" + "\n" + 
		"Todos\t" + "\n" + 
		"Priority\tName\tDate\tDone\tPrivate" + "\n" + 
		"" + "\n" + 
		"Contacts\t" + "\n" + 
		"First Name\tLast Name\tTitle\tDepartment\tRoom\tCompany\tAddress\tCity\tState\tZIP\tCountry\tPhone\tExtension\tFax\tEmail\tNotes\t" + "\n" + 
		"" + "\n";
		
	//
	// This is a simulated MeetingMaker Text export file.
	// It includes reminder and to-do sections to make sure
	// that we can reliably skip these.
	//
	final static private int ROW_COUNT_2 =  2;

	private final static String meetingMakerFile2Contents = 
		
		"Events\t" + "\n" + 
		"Title\tLocation\tDate\tStart Time\tDuration\tPrivate\tFlexible\tPublishable\tLabel\tAgenda/Notes" + "\n" + 
		"Staff Meeting\tMSIS zConference Room\t5/3/2004\t10:00\t02:00\t0\t0\t0\t\t" + "\n" + 
		"On-line grade recording\tMSIS zLarge Conference Room\t5/5/2004\t10:00\t01:00\t0\t0\t0\t\t\"To review java class\"" + "\n";
		

	
	/**
	 * Constructor for MeetingMakerImportReaderTest.
	 * @param arg0
	 */
	public MeetingMakerReaderTest(String arg0)
	{
		super(arg0);
	}

	// Object under test.
	private MeetingMakerReader scheduleImport;
	
	/**
	 * 
	 */
	final public void testImportStreamVariableWidth() throws ImportException
	{
		ByteArrayInputStream inStream = new ByteArrayInputStream(meetingMakerFile1Contents.getBytes());
		
		final String [][] cellValues = new String [ROW_COUNT_1][];
		
		for ( int i=0 ; i < ROW_COUNT_1; i++ )
		{
			cellValues[i] = new String [COLUMN_COUNT];
		}
		
		scheduleImport
			.importStreamFromDelimitedFile(
				inStream,
				new ReaderImportRowHandler()
		{

			// Do this nonsense since we're using an annoymous inner class
			// and we can't modify a simple integer/Integer variable.
			final Integer [] rowNumber = { new Integer(0) };

			public void handleRow(Iterator columnIterator)
				throws ImportException
			{

				while (columnIterator.hasNext())
				{
					Reader.ReaderImportCell column =
						(ReaderImportCell) columnIterator.next();

					cellValues[rowNumber[0].intValue()][column.getColumnNumber()] =
						column.getCellValue();
				}
				
				rowNumber[0] = new Integer(rowNumber[0].intValue() + 1);

			}
		});
		
		//
		// Now verify that all the data made it into the correct row/column.
		//
		assertEquals("Unexpected import value", "Staff Meeting", cellValues[0][0]);
		assertEquals("Unexpected import value", "MSIS zConference Room", cellValues[0][1]);
		assertEquals("Unexpected import value", "5/3/2004", cellValues[0][2]);
		assertEquals("Unexpected import value", "10:00", cellValues[0][3]);
		assertEquals("Unexpected import value", "02:00", cellValues[0][4]);
		assertEquals("Unexpected import value", "0", cellValues[0][5]);
		assertEquals("Unexpected import value", "0", cellValues[0][6]);
		assertEquals("Unexpected import value", "0", cellValues[0][7]);
		assertNull("Unexpected import value", cellValues[0][8]);
		assertNull("Unexpected import value", cellValues[0][9]);
		assertEquals("Unexpected import value", "On-line grade recording", cellValues[1][0]);
		assertEquals("Unexpected import value", "MSIS zLarge Conference Room", cellValues[1][1]);
		assertEquals("Unexpected import value", "5/5/2004", cellValues[1][2]);
		assertEquals("Unexpected import value", "10:00", cellValues[1][3]);
		assertEquals("Unexpected import value", "01:00", cellValues[1][4]);
		assertEquals("Unexpected import value", "0", cellValues[1][5]);
		assertEquals("Unexpected import value", "0", cellValues[1][6]);
		assertEquals("Unexpected import value", "0", cellValues[1][7]);
		assertEquals("Unexpected import value", "", cellValues[1][8]);
		assertEquals("Unexpected import value", "To review java class", cellValues[1][9]);
		assertEquals("Unexpected import value", "on-line grade recording Evaluation database detail", cellValues[2][0]);
		assertEquals("Unexpected import value", "MSIS zLarge Conference Room", cellValues[2][1]);
		assertEquals("Unexpected import value", "5/5/2004", cellValues[2][2]);
		assertEquals("Unexpected import value", "14:00", cellValues[2][3]);
		assertEquals("Unexpected import value", "01:00", cellValues[2][4]);
		assertEquals("Unexpected import value", "0", cellValues[2][5]);
		assertEquals("Unexpected import value", "0", cellValues[2][6]);
		assertEquals("Unexpected import value", "0", cellValues[2][7]);
		assertNull("Unexpected import value", cellValues[2][8]);
		assertNull("Unexpected import value", cellValues[2][9]);
		assertEquals("Unexpected import value", "Portal Issues Meeting", cellValues[3][0]);
		assertEquals("Unexpected import value", "MSIS zLarge Conference Room", cellValues[3][1]);
		assertEquals("Unexpected import value", "5/6/2004", cellValues[3][2]);
		assertEquals("Unexpected import value", "10:00", cellValues[3][3]);
		assertEquals("Unexpected import value", "01:00", cellValues[3][4]);
		assertEquals("Unexpected import value", "0", cellValues[3][5]);
		assertEquals("Unexpected import value", "0", cellValues[3][6]);
		assertEquals("Unexpected import value", "0", cellValues[3][7]);
		assertNull("Unexpected import value", cellValues[3][8]);
		assertNull("Unexpected import value", cellValues[3][9]);
		assertEquals("Unexpected import value", "c-tools SIG", cellValues[4][0]);
		assertEquals("Unexpected import value", "Language Resource Center Viewing Room - 2nd Floor MLB", cellValues[4][1]);
		assertEquals("Unexpected import value", "5/6/2004", cellValues[4][2]);
		assertEquals("Unexpected import value", "13:00", cellValues[4][3]);
		assertEquals("Unexpected import value", "02:00", cellValues[4][4]);
		assertEquals("Unexpected import value", "0", cellValues[4][5]);
		assertEquals("Unexpected import value", "0", cellValues[4][6]);
		assertEquals("Unexpected import value", "0", cellValues[4][7]);
		assertNull("Unexpected import value", cellValues[4][8]);
		assertNull("Unexpected import value", cellValues[4][9]);
		assertEquals("Unexpected import value", "MSIS lunch", cellValues[5][0]);
		assertEquals("Unexpected import value", "", cellValues[5][1]);
		assertEquals("Unexpected import value", "5/12/2004", cellValues[5][2]);
		assertEquals("Unexpected import value", "11:45", cellValues[5][3]);
		assertEquals("Unexpected import value", "01:30", cellValues[5][4]);
		assertEquals("Unexpected import value", "0", cellValues[5][5]);
		assertEquals("Unexpected import value", "0", cellValues[5][6]);
		assertEquals("Unexpected import value", "0", cellValues[5][7]);
		assertEquals("Unexpected import value", "", cellValues[5][8]);
		assertEquals("Unexpected import value", "\nStaff	                  Lunch Day\nSIS                          March 2004\nFIS                          April 2004\nRIS                         May 2004\n", cellValues[5][9]);
		assertEquals("Unexpected import value", "MCAT scores", cellValues[6][0]);
		assertEquals("Unexpected import value", "MSIS zLarge Conference Room", cellValues[6][1]);
		assertEquals("Unexpected import value", "5/17/2004", cellValues[6][2]);
		assertEquals("Unexpected import value", "09:00", cellValues[6][3]);
		assertEquals("Unexpected import value", "00:30", cellValues[6][4]);
		assertEquals("Unexpected import value", "0", cellValues[6][5]);
		assertEquals("Unexpected import value", "0", cellValues[6][6]);
		assertEquals("Unexpected import value", "0", cellValues[6][7]);
		assertNull("Unexpected import value", cellValues[6][8]);
		assertNull("Unexpected import value", cellValues[6][9]);
		assertEquals("Unexpected import value", "Staff Meeting", cellValues[7][0]);
		assertEquals("Unexpected import value", "MSIS zConference Room", cellValues[7][1]);
		assertEquals("Unexpected import value", "5/17/2004", cellValues[7][2]);
		assertEquals("Unexpected import value", "10:00", cellValues[7][3]);
		assertEquals("Unexpected import value", "02:00", cellValues[7][4]);
		assertEquals("Unexpected import value", "0", cellValues[7][5]);
		assertEquals("Unexpected import value", "0", cellValues[7][6]);
		assertEquals("Unexpected import value", "0", cellValues[7][7]);
		assertNull("Unexpected import value", cellValues[7][8]);
		assertNull("Unexpected import value", cellValues[7][9]);
		assertEquals("Unexpected import value", "Global Reach Survey", cellValues[8][0]);
		assertEquals("Unexpected import value", "MSIS zLarge Conference Room", cellValues[8][1]);
		assertEquals("Unexpected import value", "5/17/2004", cellValues[8][2]);
		assertEquals("Unexpected import value", "13:30", cellValues[8][3]);
		assertEquals("Unexpected import value", "01:00", cellValues[8][4]);
		assertEquals("Unexpected import value", "0", cellValues[8][5]);
		assertEquals("Unexpected import value", "0", cellValues[8][6]);
		assertEquals("Unexpected import value", "0", cellValues[8][7]);
		assertNull("Unexpected import value", cellValues[8][8]);
		assertNull("Unexpected import value", cellValues[8][9]);
		assertEquals("Unexpected import value", "Test Activity", cellValues[9][0]);
		assertEquals("Unexpected import value", "Somewhere", cellValues[9][1]);
		assertEquals("Unexpected import value", "6/3/2004", cellValues[9][2]);
		assertEquals("Unexpected import value", "09:00", cellValues[9][3]);
		assertEquals("Unexpected import value", "01:00", cellValues[9][4]);
		assertEquals("Unexpected import value", "0", cellValues[9][5]);
		assertEquals("Unexpected import value", "0", cellValues[9][6]);
		assertEquals("Unexpected import value", "0", cellValues[9][7]);
		assertEquals("Unexpected import value", "Label 1", cellValues[9][8]);
		assertEquals("Unexpected import value", "These are notes for a test activity.\nThis is the second line.\nThis is the third line.\nQuoted String -> 'This is a string in quotes' <- Quoted String", cellValues[9][9]);
		assertEquals("Unexpected import value", "New Dashboard - Add to Prototype", cellValues[10][0]);
		assertEquals("Unexpected import value", "", cellValues[10][1]);
		assertEquals("Unexpected import value", "6/3/2004", cellValues[10][2]);
		assertEquals("Unexpected import value", "10:30", cellValues[10][3]);
		assertEquals("Unexpected import value", "01:00", cellValues[10][4]);
		assertEquals("Unexpected import value", "0", cellValues[10][5]);
		assertEquals("Unexpected import value", "0", cellValues[10][6]);
		assertEquals("Unexpected import value", "0", cellValues[10][7]);
		assertNull("Unexpected import value", cellValues[10][8]);
		assertNull("Unexpected import value", cellValues[10][9]);
		assertEquals("Unexpected import value", "Forms on-line grading", cellValues[11][0]);
		assertEquals("Unexpected import value", "MSIS zLarge Conference Room", cellValues[11][1]);
		assertEquals("Unexpected import value", "5/28/2004", cellValues[11][2]);
		assertEquals("Unexpected import value", "14:00", cellValues[11][3]);
		assertEquals("Unexpected import value", "01:00", cellValues[11][4]);
		assertEquals("Unexpected import value", "0", cellValues[11][5]);
		assertEquals("Unexpected import value", "0", cellValues[11][6]);
		assertEquals("Unexpected import value", "0", cellValues[11][7]);
		assertNull("Unexpected import value", cellValues[11][8]);
		assertNull("Unexpected import value", cellValues[11][9]);

//
// The commented out code below was used to generate the above asserts.  The asserts were
// manually verified before being incorporated into the test.
//
//
//		for ( int i=0 ; i < ROW_COUNT; i++ )
//		{
//			for ( int j=0; j < COLUMN_COUNT; j++ )
//			{
//
//				String line = null;
//				
//				if ( cellValues[i][j] == null )
//				{
//					line = "assertNull(\"Unexpected import value\", cellValues[_ROW_][_COLUMN_]);"; 
//					line = line.replaceAll("_ROW_", Integer.toString(i));
//					line = line.replaceAll("_COLUMN_", Integer.toString(j));
//				}
//				else
//				{
//					line = "assertEquals(\"Unexpected import value\", \"_EXPECTED_\", cellValues[_ROW_][_COLUMN_]);";
//				
//					line = line.replaceAll("_EXPECTED_", cellValues[i][j].replaceAll("\"", "\\\"").replaceAll("\n", "\\\\\\\\n"));
//					line = line.replaceAll("_ROW_", Integer.toString(i));
//					line = line.replaceAll("_COLUMN_", Integer.toString(j));
//				}
//				System.out.println(line);
//			}
//		}
	}
	
//	/**
//	 * 
//	 */
//	final public void testDoImport()
//	{
//		ByteArrayInputStream inputStream = new ByteArrayInputStream(meetingMakerFile2Contents.getBytes());
//		
//		try
//		{
//			List rows = scheduleImport.doImport(inputStream);
//
//			assertEquals("Incorrect number of rows imported", ROW_COUNT_2, rows.size());
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
//					assertEquals("Unexpected import value", "Staff Meeting", rowProperties.get( GenericScheduleImporter.TITLE_PROPERTY_NAME));
//					assertNull("Unexpected import value", rowProperties.get( GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//					
//					Date activityDate = (Date)rowProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME);
//					assertEquals("Unexpected import value", "05/03/04", GenericScheduleImporter.DATE_FORMATTER.format(activityDate));
//
//					Date activityStartTime = (Date)rowProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//					assertEquals("Unexpected import value", "10:00 AM", GenericScheduleImporter.TIME_FORMATTER.format(activityStartTime));
//
//					assertEquals("Unexpected import value", 120, ((Integer)rowProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME)).intValue());
//
//					assertEquals("Unexpected import value", "MSIS zConference Room", rowProperties.get( GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//				}
//				else
//				if ( rowNumber == 1 )
//				{
//					assertEquals("Unexpected import value", "On-line grade recording", rowProperties.get( GenericScheduleImporter.TITLE_PROPERTY_NAME));
//					assertEquals("Unexpected import value", "To review java class", rowProperties.get( GenericScheduleImporter.DESCRIPTION_PROPERTY_NAME));
//
//					Date activityDate = (Date)rowProperties.get(GenericScheduleImporter.DATE_PROPERTY_NAME);
//					assertEquals("Unexpected import value", "05/05/04", GenericScheduleImporter.DATE_FORMATTER.format(activityDate));
//
//					Date activityStartTime = (Date)rowProperties.get(GenericScheduleImporter.START_TIME_PROPERTY_NAME);
//					assertEquals("Unexpected import value", "10:00 AM", GenericScheduleImporter.TIME_FORMATTER.format(activityStartTime));
//
//					assertEquals("Unexpected import value", 60, ((Integer)rowProperties.get(GenericScheduleImporter.DURATION_PROPERTY_NAME)).intValue());
//
//					assertEquals("Unexpected import value", "MSIS zLarge Conference Room", rowProperties.get( GenericScheduleImporter.LOCATION_PROPERTY_NAME));
//				}
//				
//				rowNumber++;
//			}
//		}
//		catch (ImportException e)
//		{
//			fail("Exception thrown during import or MeetingMakerFile");
//		}
//	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		scheduleImport = new MeetingMakerReader();
	}


}
