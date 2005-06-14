/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/test/logic/org/sakaiproject/component/legacy/calendar/readers/CSVReaderTest.java,v 1.4 2004/11/25 03:32:58 janderse.umich.edu Exp $
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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.sakaiproject.component.legacy.calendar.readers.Reader.ReaderImportRowHandler;
import org.sakaiproject.exception.ImportException;

/**
 * TestCast for CVSReader
 */
public class CSVReaderTest extends TestCase
{
	// Object under test.
	private CSVReader scheduleImport;
	
	/**
	 * Remove leading/trailing quotes
	 * @param columnsReadFromFile
	 */
	String trimLeadingTrailingQuotes(String value)
	{
		String regex2 = "(?:\")*([^\"]+)(?:\")*";
		return value.trim().replaceAll(regex2, "$1");
	}

	
	/**
	 * @param columnHeaders
	 * @param propertyNames
	 * @param testData
	 * @param delimiter
	 * @param quoteValues
	 */
	private InputStream getVariableWidthTestData(String [] columnHeaders, String [][] testData, String delimiter, boolean quoteValues)
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outStream);
		
		// Print the column headers
		for ( int column=0; column < columnHeaders.length; column++ )
		{
			if ( quoteValues )
			{
				writer.print("\"" + columnHeaders[column] + "\"");
			}				
			else
			{
				writer.print(columnHeaders[column]); 
			}
			
			// Don't print a trailing delimiter.
			if ( column < columnHeaders.length-1 )
			{
				writer.print(delimiter);
			}
		}
		writer.println();
		
		for ( int row = 0; row < testData.length; row++ )
		{
			for ( int column = 0; column< testData[row].length; column++ )
			{
				// Print out the optionally quoted data value.
				if ( quoteValues )
				{
					writer.print("\"" + testData[row][column] + "\"");
				}
				else
				{
					writer.print(testData[row][column]);
				}
			
				// Don't print a trailing delimiter.
				if ( column < columnHeaders.length-1 )
				{
					writer.print(delimiter);
				}
			}
			writer.println();
		}
		
		writer.close();
		
		return new ByteArrayInputStream(outStream.toByteArray());
	}
	
	/**
	 * Constructor for ScheduleImport.
	 * @param arg0
	 */
	public CSVReaderTest(String arg0)
	{
		super(arg0);
	}

	/**
	 * 
	 */
	final public void testImportStreamVariableWidth() throws ImportException
	{
		// Column information for the test with delimiters.
		String [] columnPropertyNames2 = {"Title", "Description", "Date", "StartTime", "Duration", "Frequency", "ItemType","Location"};
		String [] columnHeaderNames2 = {"Title", "Description", "Date", "Start Time", "Length", "Frequency", "Type","Location"};
	
		final String testData1[][] = {
			{"Test Event 1", "This is a test description", "5/2/2004", "11:00 AM", "1", "daily", "Activity", "Taubman Library 2919" },
//			{"", "", "", "", "1", "", "", "", "" },
			{"Test Event 1", "\"This is a test description, with a comma\"", "5/2/2004", "11:00 AM", "1", "daily", "Activity", "Taubman Library 2919" },
			};

		// Make a 2-d array of rows/columns that we will use to record whether or
		// not the parsing recognized a particular value.
		final boolean valueHits[][] = new boolean [testData1.length][testData1[0].length];

		InputStream stream = getVariableWidthTestData(columnHeaderNames2, testData1, ",", false);
		importStream(columnHeaderNames2, columnPropertyNames2, testData1, valueHits, stream);
		
		// Make sure that all the data values were hit by the parsing.
		for ( int row = 0; row < testData1.length; row++)
		{
			for ( int col = 0; col < testData1[0].length; col++ )
			{
				if (!valueHits[row][col])
				{
					fail("Missing value at row: " + row + ", col: " + col);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	final public void testImportStreamVariableWidthBadColumnHeaders() throws ImportException
	{
		// Column information for the test with delimiters.
		String [] columnPropertyNames = {"Title", "Description", "Date", "StartTime", "Duration", "Frequency", "ItemType","Location"};
		String [] columnHeaderNames = {"Title", "Description", "Date", "Start Time", "Length", "Frequency", "Type","Location"};

		String [] columnHeaderNamesBad = {"Title", "DescriptionX", "Date", "Start Time", "Length", "Frequency", "T ype","Location"};
	
		final String testData[][] = {
			{"Test Event 1", "This is a test description", "5/2/2004", "11:00 AM", "1", "daily", "Activity", "Taubman Library 2919" },
			{"Test Event 1", "\"This is a test description, with a comma\"", "5/2/2004", "11:00 AM", "1", "daily", "Activity", "Taubman Library 2919" },
			};

		// Make a 2-d array of rows/columns that we will use to record whether or
		// not the parsing recognized a particular value.
		final boolean valueHits[][] = new boolean [testData.length][testData[0].length];

		InputStream stream = getVariableWidthTestData(columnHeaderNamesBad, testData, ",", false);
		
		importStream(columnHeaderNames, columnPropertyNames, testData, valueHits, stream);
		
		// Make sure that all the data values were hit by the parsing.
		for ( int row = 0; row < testData.length; row++)
		{
			for ( int col = 0; col < testData[0].length; col++ )
			{
				if (!valueHits[row][col])
				{
					fail("Missing value at row: " + row + ", col: " + col);
				}
			}
		}
	}
	
	/**
	 * @param columnHeaderNames2
	 * @param columnPropertyNames2
	 * @param testData1
	 * @param valueHits
	 */
	final public void importStream(String [] columnHeaderNames, final String [] columnPropertyNames, final String testData1[][], final boolean valueHits[][], InputStream stream) throws ImportException
	{
		for ( int row = 0; row < testData1.length; row++)
		{
			for ( int col = 0; col < testData1[0].length; col++ )
			{
				valueHits[row][col] = false;
			}
		}

		Map columnHeaderMap = new HashMap();
		
		for ( int i=0; i < columnPropertyNames.length; i++ )
		{
			columnHeaderMap.put(columnHeaderNames[i], columnPropertyNames[i]);
		}
		

		scheduleImport.setColumnHeaderToAtributeMapping(columnHeaderMap);

		scheduleImport.setColumnDelimiter(",");
		
		// Do this nonsense since we're using an annoymous inner class
		// and we can't modify a simple integer/Integer variable.
		final Integer [] rowNumber = { new Integer(0) };
		
		
		// Read in the file.
		scheduleImport
			.importStreamFromDelimitedFile(stream, new ReaderImportRowHandler()
		{
			// This is the callback that is called for each row.
			public void handleRow(Iterator columnIterator)
			{
				while (columnIterator.hasNext())
				{
					Reader.ReaderImportCell column =
						(Reader.ReaderImportCell) columnIterator.next();
					
					assertEquals(
						"Mismatched data value",
						trimLeadingTrailingQuotes(
							testData1[rowNumber[0].intValue()][column.getColumnNumber()]),
						column.getCellValue());
						
					// Flag that this data value was hit by parsing.
					valueHits[rowNumber[0].intValue()][column.getColumnNumber()] = true;
				}

				rowNumber[0] = new Integer(rowNumber[0].intValue() + 1);
			}
		});
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		scheduleImport = new CSVReader();
	}

}
