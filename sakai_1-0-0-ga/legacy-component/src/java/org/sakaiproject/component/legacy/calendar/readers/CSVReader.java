/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/calendar/readers/CSVReader.java,v 1.2 2004/06/22 03:14:02 ggolden Exp $
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.component.legacy.calendar.GenericCalendarImporter;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.service.legacy.time.TimeBreakdown;

/**
 * This class parses a comma (or other separator other than a double-quote) delimited
 * file.
 */
public class CSVReader extends Reader
{
	private static final String COMMENT_LINE_PREFIX = "//";
	/** 
	 * This regular expression will split separate separated values, with optionally quoted columns.
	 * Quote characters not used to group columns must be escaped with a backslash.  The __DELIMITER__
	 * token is replaced with the actual character in the form of \x2c where the "2c" part is the
	 * character value, in this case a comma.
	 */
	private static final String CSV_REGEX =
		"__DELIMITER__(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))";
		
	private static final String DELIMITER_TOKEN_IN_REGEX = "__DELIMITER__";

	/** Defaults to comma */
	private String columnDelimiter = ",";

	/**
	 * Default constructor
	 */
	public CSVReader()
	{
		super();
	}

	/**
	 * Import a CSV file from a stream and callback on each row.
	 * @param stream Stream of CSV (or other delimited data)
	 * @param handler Callback for each row.
	 */
	public void importStreamFromDelimitedFile(
		InputStream stream,
		ReaderImportRowHandler handler) throws ImportException
	{
		BufferedReader bufferedReader = getReader(stream);

		ColumnHeader columnDescriptionArray[] = null;

		int lineNumber = 1;

		boolean readDone = false;

		while (!readDone)
		{
			try
			{
				// Prepare the column map on the first line.
				String lineBuffer = bufferedReader.readLine();
				
				// See if we have exhausted the input
				if (lineBuffer == null)
				{
					break;
				}
				
				// Skip comment or empty lines, but keep track of the line number.
				if (lineBuffer.startsWith(COMMENT_LINE_PREFIX)
					|| lineBuffer.trim().length() == 0)
				{
					lineNumber++;
					continue;
				}
				
				if (columnDescriptionArray == null)
				{
					columnDescriptionArray =
						buildColumnDescriptionArray(
							parseLineFromDelimitedFile(lineBuffer));

					lineNumber++;
					
					// Immediately start the next loop, don't do any more
					// processing or increment the line counter.
					continue;
				}
				else
				{
					handler.handleRow(
						processLine(
							columnDescriptionArray,
							lineNumber,
							parseLineFromDelimitedFile(lineBuffer)));
				}
			}
			catch (IOException e)
			{
				// We'll get an exception when we've exhauster
				readDone = true;
			}

			// If we get this far, increment the line counter.
			lineNumber++;
		}
	}

	/**
	 * Form the hex string for the delimiter character(s)
	 * @return
	 */
	private String getHexStringForDelimiter()
	{
		StringBuffer delimiter = new StringBuffer();
		
		for ( int i=0; i < columnDelimiter.length(); i++)
		{
			delimiter.append( "\\" + "x"); 
			delimiter.append( Integer.toHexString(this.columnDelimiter.charAt(i)) );
		}
		
		return delimiter.toString().replaceAll("\\\\","\\\\\\\\");		
	}
	
	/**
	 * Break a line's columns up into a String array.  (One element for each column.)
	 * @param line
	 * @return
	 */
	protected String[] parseLineFromDelimitedFile(String line)
	{
		String[] columnsReadFromFile;
		String pattern = CSV_REGEX;
		
		pattern =
			pattern.replaceAll(
				DELIMITER_TOKEN_IN_REGEX,
				getHexStringForDelimiter());

		columnsReadFromFile = line.trim().split(pattern);
		
		trimLeadingTrailingQuotes(columnsReadFromFile);

		return columnsReadFromFile;
	}

	/**
	 * Set the delimiter
	 * @param string
	 */
	public void setColumnDelimiter(String columnDelimiter)
	{
		this.columnDelimiter = columnDelimiter;
	}
	
	/**
	 * Get the default column map for CSV files.
	 */
	public Map getDefaultColumnMap()
	{
		Map columnMap = new HashMap();

		columnMap.put(GenericCalendarImporter.LOCATION_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.LOCATION_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.ITEM_TYPE_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.ITEM_TYPE_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.FREQUENCY_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.FREQUENCY_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.DURATION_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.DURATION_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.START_TIME_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.START_TIME_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.DATE_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.DATE_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.DESCRIPTION_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.DESCRIPTION_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.TITLE_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.TITLE_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.INTERVAL_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.INTERVAL_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.ENDS_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.ENDS_PROPERTY_NAME);
		columnMap.put(GenericCalendarImporter.REPEAT_DEFAULT_COLUMN_HEADER, GenericCalendarImporter.REPEAT_PROPERTY_NAME);
		
		return columnMap;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.tool.calendar.schedimportreaders.Reader#filterEvents(java.util.List, java.lang.String[])
	 */
	public List filterEvents(List events, String[] customFieldNames) throws ImportException
	{
		setColumnDelimiter(",");
		
		Map augmentedMapping = getDefaultColumnMap();
		
		// Add custom fields.
		if ( customFieldNames != null )
		{
			for ( int i=0; i < customFieldNames.length; i++ )
			{
				augmentedMapping.put(customFieldNames[i], customFieldNames[i]);
			}
		}
		
		// Use the default mappings
		setColumnHeaderToAtributeMapping(augmentedMapping);

		Iterator it = events.iterator();
		
		int lineNumber = 1;
		
		//
		// Convert the date/time fields as they appear in the Outlook import to
		// be a synthesized start/end timerange.
		//
		while ( it.hasNext() )
		{
			Map eventProperties = (Map)it.next();

			Date startTime = (Date) eventProperties.get(GenericCalendarImporter.START_TIME_PROPERTY_NAME);
			TimeBreakdown startTimeBreakdown = null;
			
			if ( startTime != null )
			{
				startTimeBreakdown = getTimeService().newTime(startTime.getTime()).breakdownLocal();
			}
			
			Integer durationInMinutes = (Integer)eventProperties.get(GenericCalendarImporter.DURATION_PROPERTY_NAME);

			if ( durationInMinutes == null )
			{
				throw new ImportException("No duration time specified on line #" + lineNumber + ". Please make the appropriate changes to your template and save it again.");
			}
			
			Date endTime =
				new Date(
					startTime.getTime()
						+ (durationInMinutes.longValue() * 60 * 1000));

			TimeBreakdown endTimeBreakdown = null;

			if ( endTime != null )
			{
				endTimeBreakdown = getTimeService().newTime(endTime.getTime()).breakdownLocal();
			}

			Date startDate = (Date) eventProperties.get(GenericCalendarImporter.DATE_PROPERTY_NAME);
			TimeBreakdown startDateBreakdown = null;
			
			if ( startDate != null )
			{
				startDateBreakdown = getTimeService().newTime(startDate.getTime()).breakdownGmt();
			}
			
			GregorianCalendar startCal =
				getTimeService().getCalendar(
					getTimeService().getLocalTimeZone(),
					startDateBreakdown.getYear(),
					startDateBreakdown.getMonth() - 1,
					startDateBreakdown.getDay(),
					startTimeBreakdown.getHour(),
					startTimeBreakdown.getMin(),
					startTimeBreakdown.getSec(),
					0);

			GregorianCalendar endCal =
				getTimeService().getCalendar(
					getTimeService().getLocalTimeZone(),
					startDateBreakdown.getYear(),
					startDateBreakdown.getMonth() - 1,
					startDateBreakdown.getDay(),
					endTimeBreakdown.getHour(),
					endTimeBreakdown.getMin(),
					endTimeBreakdown.getSec(),
					0);

					
			// Include the start time, but not the end time.
			eventProperties.put(
				GenericCalendarImporter.ACTUAL_TIMERANGE,
				getTimeService().newTimeRange(
					getTimeService().newTime(startCal.getTimeInMillis()),
					getTimeService().newTime(endCal.getTimeInMillis()),
					true,
					false));
					
			lineNumber++;
		}
		
		return events;
	}

}
