/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/util/src/java/org/sakaiproject/util/PrintFileGenerator.java,v 1.1 2005/04/14 02:17:52 ggolden.umich.edu Exp $
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

package org.sakaiproject.util;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;
import org.apache.xerces.dom.DocumentImpl;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.legacy.calendar.Calendar;
import org.sakaiproject.service.legacy.calendar.CalendarEvent;
import org.sakaiproject.service.legacy.calendar.CalendarEventVector;
import org.sakaiproject.service.legacy.calendar.cover.CalendarService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeBreakdown;
import org.sakaiproject.service.legacy.time.TimeRange;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.util.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Used to generate PDF output for the Schedule tool.  Several view types
 * are supported: daily, weekly, and monthly.  A "year" view is not supported.
 */
public class PrintFileGenerator
{
	// XSL File Names
	private final static String DAY_VIEW_XLST_FILENAME = "schedule.xsl";
	private final static String LIST_VIEW_XLST_FILENAME = "schlist.xsl";
	private final static String MONTH_VIEW_XLST_FILENAME = "schedulemm.xsl";
	private final static String WEEK_VIEW_XLST_FILENAME = "schedule.xsl";
	private final static String XSLT_BASE_DIRECTORY = "xslt";
	
	// Mime Types
	private final static String PDF_MIME_TYPE = "application/pdf";
	
	// Constants for time calculations
	private static long MILLISECONDS_IN_DAY = (60 * 60 * 24 * 1000);
	private final static long MILLISECONDS_IN_HOUR = (60 * 60 * 1000);
	private final static long MILLISECONDS_IN_MINUTE = (1000 * 60);
	private static final long MINIMUM_EVENT_LENGTH_IN_MSECONDS = (29*MILLISECONDS_IN_MINUTE);
	private static final int SCHEDULE_INTERVAL_IN_MINUTES = 15;
	private static final int MAX_OVERLAPPING_COLUMNS = 7;
	private static final int TIMESLOT_FOR_OVERLAP_DETECTION_IN_MINUTES = 10;
	
	// View types
	public final static int UNKNOWN_VIEW = -1;
	public final static int DAY_VIEW = 0;
	public final static int WEEK_VIEW = 2;
	public final static int MONTH_VIEW = 3;
	public final static int LIST_VIEW = 5;
	
	// URL Parameter Constants
	public final static String PRINT_REQUEST_NAME = "/PrintFileGeneration";
	private final static String PRINTING_REQUEST_NAME = "PrintFileGeneration";
	public static final String TIME_RANGE_PARAMETER_NAME = "timeRange";
	private static final String DAILY_START_TIME_PARAMETER_NAME = "dailyStartTime";
	private final static String USER_NAME_PARAMETER_NAME = "user";
	private final static String CALENDAR_PARAMETER_BASE_NAME = "calendar";
	private final static String SCHEDULE_TYPE_PARAMETER_NAME = "scheduleType";

	
	// XML Node/Attribute Names
	private static final String COLUMN_NODE_NAME = "col";
	private static final String EVENT_NODE_NAME = "event";
	private static final String FACULTY_EVENT_ATTRIBUTE_NAME = "Faculty";
	private static final String FACULTY_NODE = "faculty";
	private static final String FROM_ATTRIBUTE_STRING = "from";
	private static final String GROUP_NODE = "grp";
	private static final String LIST_DATE_ATTRIBUTE_NAME = "dt";
	private static final String LIST_DAY_OF_WEEK_ATTRIBUTE_NAME = "dayofweek";
	private static final String LIST_NODE_NAME = "list";
	private static final String MONTH_NODE_NAME = "month";
	private static final String MAX_CONCURRENT_EVENTS_NAME = "maxConcurrentEvents";
	private static final String PLACE_NODE = "place";
	private static final String ROW_NODE_NAME = "row";
	private static final String SCHEDULE_NODE = "schedule";
	private static final String START_DAY_WEEK_ATTRIBUTE_NAME = "startdayweek";
	private static final String START_TIME_ATTRIBUTE_NAME = "start-time";
	private static final String SUB_EVENT_NODE_NAME = "subEvent";
	private static final String TITLE_NODE = "title";
	private static final String TO_ATTRIBUTE_STRING = "to";
	private static final String TYPE_NODE = "type";
	private static final String UID_NODE = "uid";
	
	// Misc.
	private static final String HOUR_MINUTE_SEPARATOR = ":";
		
	/**
	 * This is a container for a list of columns, plus the timerange for
	 * all the events contained in the row.  This time range is a union
	 * of all the separate time ranges.
	 */
	private static class LayoutRow extends ArrayList
	{
		// Union of all event time ranges in this row.
		private TimeRange rowTimeRange;

		/**
		 * Gets the union of all event time ranges in this row.
		 */
		public TimeRange getRowTimeRange()
		{
			return rowTimeRange;
		}

		/**
		 * Sets the union of all event time ranges in this row.
		 */
		public void setRowTimeRange(TimeRange range)
		{
			rowTimeRange = range;
		}
	}

	/**
	 * Table used to layout a single day, with potentially overlapping
	 * events.
	 */
	private static class SingleDayLayoutTable
	{
		private long millisecondsPerTimeslot;
		private int numCols;
		private int numRows;
		private ArrayList rows;
		
		// Overall time range for this table.
		private TimeRange timeRange;

		/**
		 * Constructor for SingleDayLayoutTable
		 */
		public SingleDayLayoutTable(
			TimeRange timeRange,
			int maxNumberOverlappingEvents,
			int timeslotInMinutes)
		{
			this.timeRange = timeRange;
			numCols = maxNumberOverlappingEvents;

			millisecondsPerTimeslot =
				timeslotInMinutes * MILLISECONDS_IN_MINUTE;

			numRows = getNumberOfRowsNeeded(timeRange);

			rows = new ArrayList(numRows);

			for (int i = 0; i < numRows; i++)
			{
				ArrayList newRow = new ArrayList(numCols);

				rows.add(i, newRow);

				for (int j = 0; j < numCols; j++)
				{
					newRow.add(j, new LayoutTableCell());
				}
			}
		}

		/**
		 * Adds an event to the SingleDayLayoutTable
		 */
		void addEvent(CalendarEvent calendarEvent)
		{
			if (calendarEvent == null)
			{
				return;
			}

			int startingRow = getStartingRow(roundRangeToMinimumTimeInterval(calendarEvent.getRange()));

			int numRowsNeeded = getNumberOfRowsNeeded(roundRangeToMinimumTimeInterval(calendarEvent.getRange()));

			// Trim to the end of the table.
			if (startingRow + numRowsNeeded >= getNumRows())
			{
				numRowsNeeded = getNumRows() - startingRow;
			}

			// Get the first column that has enough sequential free intervals to 
			// contain this event.
			int columnNumber = getFreeColumn(startingRow, numRowsNeeded);

			if (columnNumber != -1)
			{
				for (int i = startingRow; i < startingRow + numRowsNeeded; i++)
				{
					LayoutTableCell cell = getCell(i, columnNumber);

					// All cells have the calendar event information.
					cell.setCalendarEvent(calendarEvent);

					// Only the first cell is marked as such.
					if (i == startingRow)
					{
						cell.setFirstCell(true);
					}

					cell.setFirstCellRow(startingRow);
					cell.setFirstCellColumn(columnNumber);

					cell.setThisCellRow(i);
					cell.setThisCellColumn(columnNumber);

					cell.setNumCellsInEvent(numRowsNeeded);
				}
			}
		}

		/**
		 * Convert the time range to fall entirely within the time range of the
		 * layout table.
		 */
		private TimeRange adjustTimeRangeToLayoutTable(TimeRange eventTimeRange)
		{
			Time lowerBound = null, upperBound = null;

			//
			// Make sure that the upper/lower bounds fall within the layout table.
			//
			if (this
				.timeRange
				.firstTime()
				.compareTo(eventTimeRange.firstTime())
				> 0)
			{
				lowerBound = this.timeRange.firstTime();
			}
			else
			{
				lowerBound = eventTimeRange.firstTime();
			}

			if (this.timeRange.lastTime().compareTo(eventTimeRange.lastTime())
				< 0)
			{
				upperBound = this.timeRange.lastTime();
			}
			else
			{
				upperBound = eventTimeRange.lastTime();
			}

			return TimeService.newTimeRange(lowerBound, upperBound, true, false);
		}

		/**
		 * Returns true if there are any events in this or other rows that
		 * overlap the event associated with this cell.
		 */
		private boolean cellHasOverlappingEvents(int rowNum, int colNum)
		{
			LayoutTableCell cell = this.getFirstCell(rowNum, colNum);

			// Start at the first cell of this event and check every row
			// to see if we find any cells in that row that are not empty
			// and are not one of ours.
			if (cell != null && !cell.isEmptyCell())
			{
				for (int i = cell.getFirstCellRow();
					i < (cell.getFirstCellRow() + cell.getNumCellsInEvent());
					i++)
				{
					for (int j = 0; j < this.numCols; j++)
					{
						LayoutTableCell curCell = this.getCell(i, j);

						if (curCell != null
							&& !curCell.isEmptyCell()
							&& curCell.getCalendarEvent()
								!= cell.getCalendarEvent())
						{
							return true;
						}
					}
				}
			}

			return false;
		}

		/**
		 * Get a particular cell.  Returns a reference to the actual cell and not a copy.
		 */
		private LayoutTableCell getCell(int rowNum, int colNum)
		{
			if (rowNum < 0
				|| rowNum >= this.numRows
				|| colNum < 0
				|| colNum >= this.numCols)
			{
				// Illegal cell indices
				return null;
			}
			else
			{
				ArrayList row = (ArrayList) rows.get(rowNum);
				return (LayoutTableCell) row.get(colNum);
			}
		}

		/**
		 * Gets the first cell associated with the event that's stored at this row/column
		 */
		private LayoutTableCell getFirstCell(int rowNum, int colNum)
		{
			LayoutTableCell cell = this.getCell(rowNum, colNum);

			if (cell == null || cell.isEmptyCell())
			{
				return null;
			}
			else
			{
				return getCell(
					cell.getFirstCellRow(),
					cell.getFirstCellColumn());
			}
		}

		/**
		 * Looks for a column where the whole event can be placed.
		 */
		private int getFreeColumn(int rowNum, int numberColumnsNeeded)
		{
			// Keep going through the columns until we hit one that has
			// enough empty cells to accomodate our event.
			for (int i = 0; i < this.numCols; i++)
			{
				boolean foundOccupiedCell = false;

				for (int j = rowNum; j < rowNum + numberColumnsNeeded; j++)
				{
					LayoutTableCell cell = getCell(j, i);

					if (cell == null)
					{
						// Out of range.
						return -1;
					}

					if (!cell.isEmptyCell())
					{
						foundOccupiedCell = true;
						break;
					}
				}

				if (!foundOccupiedCell)
				{
					return i;
				}
			}

			return -1;
		}

		/**
		 * Creates a list of lists of lists.  The outer list is a list of rows.  Each row is
		 * a list of columns.  Each column is a list of column values.
		 */
		public List getLayoutRows()
		{
			List allRows = new ArrayList();

			// Scan all rows in the table.
			for (int mainRowIndex = 0;
				mainRowIndex < this.getNumRows();
				mainRowIndex++)
			{
				// If we hit a starting row, then iterate through all rows of the
				// event group.
				if (isStartingRowOfGroup(mainRowIndex))
				{
					LayoutRow newRow = new LayoutRow();
					allRows.add(newRow);

					int numRowsInGroup =
						getNumberRowsInEventGroup(mainRowIndex);

					newRow.setRowTimeRange(
						getTimeRangeForEventGroup(mainRowIndex, numRowsInGroup));

					for (int columnIndex = 0;
						columnIndex < this.getNumCols();
						columnIndex++)
					{
						List columnList = new ArrayList();
						boolean addedCell = false;

						for (int eventGroupRowIndex = mainRowIndex;
							eventGroupRowIndex < mainRowIndex + numRowsInGroup;
							eventGroupRowIndex++)
						{
							LayoutTableCell cell =
								getCell(eventGroupRowIndex, columnIndex);

							if (cell.isFirstCell())
							{
								columnList.add(cell.getCalendarEvent());
								addedCell = true;
							}
						}

						// Don't add to our list unless we actually added a cell.
						if (addedCell)
						{
							newRow.add(columnList);
						}
					}

					// Get ready for the next iteration.  Skip those
					// rows that we have already processed.
					mainRowIndex += (numRowsInGroup - 1);
				}
			}

			return allRows;
		}

		
		private int getNumberOfRowsNeeded(TimeRange eventTimeRange)
		{
			TimeRange adjustedTimeRange =
				adjustTimeRangeToLayoutTable(eventTimeRange);

			// Use the ceiling function to obtain the next highest integral number of time slots.
			return (int)
				(Math
					.ceil(
						(double) (adjustedTimeRange.duration())
							/ (double) millisecondsPerTimeslot));
		}

		/**
		 * Gets the number of rows in an event group.  This function assumes that the
		 * row that it starts on is the starting row of the group.
		 */
		private int getNumberRowsInEventGroup(int rowNum)
		{
			int numEventRows = 0;

			if (isStartingRowOfGroup(rowNum))
			{
				numEventRows++;

				// Keep going unless we see an all empty row
				// or another starting row.
				for (int i = rowNum + 1;
					i < this.getNumRows()
						&& !isEmptyRow(i)
						&& !isStartingRowOfGroup(i);
					i++)
				{
					numEventRows++;
				}
			}

			return numEventRows;
		}

		/**
		 * Gets the total number of columns in the layout table.
		 */
		int getNumCols()
		{
			return this.numCols;
		}

		/**
		 * Gets the total number of rows in the layout table.
		 */
		int getNumRows()
		{
			return rows.size();
		}

		/**
		 * Given a time range, returns the starting row number in the
		 * layout table.
		 */
		private int getStartingRow(TimeRange eventTimeRange)
		{
			TimeRange adjustedTimeRange =
				adjustTimeRangeToLayoutTable(eventTimeRange);

			TimeRange timeRangeToStart =
				TimeService.newTimeRange(
					this.timeRange.firstTime(),
					adjustedTimeRange.firstTime(),
					true,
					true);

			//
			// We form a new time range where the ending time is the (adjusted) event
			// time range and the starting time is the starting time of the layout table.
			// The number of rows required for this range will be the starting row of the table.
			//
			return getNumberOfRowsNeeded(timeRangeToStart);
		}

		/**
		 * Returns the earliest/latest times for events in this group.  This function 
		 * assumes that the row that it starts on is the starting row of the group.
		 */
		TimeRange getTimeRangeForEventGroup(int rowNum, int numRowsInThisEventGroup)
		{
			Time firstTime = null;
			Time lastTime = null;

			for (int i = rowNum; i < rowNum + numRowsInThisEventGroup; i++)
			{
				for (int j = 0; j < this.getNumCols(); j++)
				{
					LayoutTableCell cell = getCell(i, j);
					CalendarEvent event = cell.getCalendarEvent();

					if (event != null)
					{
						TimeRange adjustedTimeRange =
							adjustTimeRangeToLayoutTable(
								roundRangeToMinimumTimeInterval(
									cell.getCalendarEvent().getRange()));

						//
						// Replace our earliest time to date with the
						// time from the event, if the time from the
						// event is earlier.
						//
						if (firstTime == null)
						{
							firstTime = adjustedTimeRange.firstTime();
						}
						else
						{
							Time eventFirstTime = adjustedTimeRange.firstTime();

							if (eventFirstTime.compareTo(firstTime) < 0)
							{
								firstTime = eventFirstTime;
							}
						}

						//
						// Replace our latest time to date with the
						// time from the event, if the time from the
						// event is later.
						//
						if (lastTime == null)
						{
							lastTime = adjustedTimeRange.lastTime();
						}
						else
						{
							Time eventLastTime = adjustedTimeRange.lastTime();

							if (eventLastTime.compareTo(lastTime) > 0)
							{
								lastTime = eventLastTime;
							}
						}
					}
				}
			}

			return TimeService.newTimeRange(firstTime, lastTime, true, false);
		}

		/**
		 * Returns true if this row has only empty cells.
		 */
		private boolean isEmptyRow(int rowNum)
		{
			boolean sawNonEmptyCell = false;

			for (int i = 0; i < this.getNumCols(); i++)
			{
				LayoutTableCell cell = getCell(rowNum, i);

				if (!cell.isEmptyCell())
				{
					sawNonEmptyCell = true;
					break;
				}
			}
			return !sawNonEmptyCell;
		}

		/**
		 * Returns true if this row has only starting cells and no
		 * continuation cells.
		 */
		private boolean isStartingRowOfGroup(int rowNum)
		{
			boolean sawContinuationCells = false;
			boolean sawFirstCell = false;

			for (int i = 0; i < this.getNumCols(); i++)
			{
				LayoutTableCell cell = getCell(rowNum, i);

				if (cell.isContinuationCell())
				{
					sawContinuationCells = true;
				}

				if (cell.isFirstCell)
				{
					sawFirstCell = true;
				}
			}

			//
			// In order to be a starting row must have a "first"
			// cell no continuation cells.
			//
			return (!sawContinuationCells && sawFirstCell);
		}

		/**
		 * Returns true if there are any cells in this row associated
		 * with events which overlap each other in this row or any
		 * other row.
		 */
		public boolean rowHasOverlappingEvents(int rowNum)
		{
			for (int i = 0; i < this.getNumCols(); i++)
			{
				if (cellHasOverlappingEvents(rowNum, i))
				{
					return true;
				}
			}

			return false;
		}
	}
	/**
	 * This is a single cell in a layout table (an instance of SingleDayLayoutTable).
	 */
	static class LayoutTableCell
	{
		private CalendarEvent calendarEvent = null;
		private int firstCellColumn = -1;
		private int firstCellRow = -1;
		private boolean isFirstCell = false;
		private int numCellsInEvent = 0;
		private int thisCellColumn = -1;
		private int thisCellRow = -1;

		/**
		 * Gets the calendar event associated with this cell.
		 */
		public CalendarEvent getCalendarEvent()
		{
			return calendarEvent;
		}

		/**
		 * Gets the first column associated with this cell.
		 */
		public int getFirstCellColumn()
		{
			return firstCellColumn;
		}

		/**
		 * Gets the first row associated with this cell.
		 */
		public int getFirstCellRow()
		{
			return firstCellRow;
		}

		/**
		 * Get the number of cells in this event.
		 */
		public int getNumCellsInEvent()
		{
			return numCellsInEvent;
		}

		/**
		 * Gets the column associated with this particular cell.
		 */
		public int getThisCellColumn()
		{
			return thisCellColumn;
		}

		/**
		 * Gets the row associated with this cell.
		 */
		public int getThisCellRow()
		{
			return thisCellRow;
		}

		/**
		 * Returns true if this cell is a continuation of an event and not
		 * the first cell in the event.
		 */
		public boolean isContinuationCell()
		{
			return !isFirstCell() && !isEmptyCell();
		}

		/**
		 * Returns true if this cell is not associated with any events.
		 */
		public boolean isEmptyCell()
		{
			return calendarEvent == null;
		}

		/**
		 * Returns true if this is the first cell in a column of cells associated
		 * with an event.
		 */
		public boolean isFirstCell()
		{
			return isFirstCell;
		}

		/**
		 * Set the calendar event associated with this cell.
		 */
		public void setCalendarEvent(CalendarEvent event)
		{
			calendarEvent = event;
		}

		/**
		 * Set flag indicating that this is the first cell in column
		 * of cells associated with an event.
		 */
		public void setFirstCell(boolean b)
		{
			isFirstCell = b;
		}

		/**
		 * Sets a value in this cell to point to the very first
		 * cell in the column of cells associated with this event.
		 */
		public void setFirstCellColumn(int i)
		{
			firstCellColumn = i;
		}

		/**
		 * Sets a value in this cell to point to the very first
		 * cell in the column of cells associated with this event.
		 */
		public void setFirstCellRow(int i)
		{
			firstCellRow = i;
		}

		/**
		 * Gets the number of cells (if any) in the group of cells associated with this cell
		 * by event.
		 */
		public void setNumCellsInEvent(int i)
		{
			numCellsInEvent = i;
		}

		/**
		 * Sets the actual column index for this cell.
		 */
		public void setThisCellColumn(int i)
		{
			thisCellColumn = i;
		}

		/**
		 * Sets the actual row index for this cell.
		 */
		public void setThisCellRow(int i)
		{
			thisCellRow = i;
		}

	}

	/**
	 * Debugging routine to get a string for a TimeRange.  This should probably
	 * be in the TimeRange class.
	 */
	private static String dumpTimeRange(TimeRange timeRange)
	{
		String returnString = "";
		
		if ( timeRange != null )
		{
			returnString = timeRange.firstTime().toStringLocalFull() + " - " + timeRange.lastTime().toStringLocalFull(); 
		}
		
		return returnString;
	}
	
//	/**
//	 * Debugging routine to write out a file of the generated XML.  This should be disabled in production.
//	 * @param servletContext
//	 * @param document
//	 */
//	private static void dumpGeneratedXML(
//		ServletContext servletContext,
//		Document document)
//	{
//		OutputFormat format = new OutputFormat();
//
//		format.setIndenting(true);
//
//		FileOutputStream out = null;
//		XMLSerializer serial = null;
//
//		try
//		{
//			out =
//				new FileOutputStream(servletContext.getRealPath("output.xml"));
//			serial = new XMLSerializer(out, format);
//		}
//
//		catch (FileNotFoundException e)
//		{
//			Log.debug("chef", "PrintFileGeneration.dumpGeneratedXML(): " + e);
//		}
//
//		try
//		{
//			serial.serialize(document);
//			out.flush();
//			out.close();
//		}
//
//		catch (IOException e)
//		{
//			Log.debug("chef", "PrintFileGeneration.dumpGeneratedXML(): " + e);
//		}
//	}

	/**
	 * Takes a DOM structure and renders a PDF
	 * @param doc DOM structure
	 * @param xslFileName XSL file to use to translate the DOM document to FOP
	 */
	private static void generatePDF(
		Document doc,
		String xslFileName,
		OutputStream streamOut)
	{
		Driver driver = new Driver();

		Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_ERROR);
		MessageHandler.setScreenLogger(logger);
		driver.setLogger(logger);

		driver.setOutputStream(streamOut);
		driver.setRenderer(Driver.RENDER_PDF);

		Transformer transformer = null;
		try
		{
			transformer =
				TransformerFactory.newInstance().newTransformer(
					new StreamSource(xslFileName));
		}

		catch (TransformerException e)
		{
			Log.debug("chef", "PrintFileGeneration.generatePDF(): " + e);
			return;
		}

		Source x = new DOMSource(doc);

		try
		{
			transformer.transform(x, new SAXResult(driver.getContentHandler()));
		}

		catch (TransformerException e1)
		{
			Log.debug("chef", "PrintFileGeneration.generatePDF(): " + e1);
			return;
		}
	}

	/**
	 * Make a full-day time range given a year, month, and day 
	 */
	private static TimeRange getFullDayTimeRangeFromYMD(
		int year,
		int month,
		int day)
	{
		return TimeService.newTimeRange(
			TimeService.newTimeLocal(year, month, day, 0, 0, 0, 0),
			TimeService.newTimeLocal(year, month, day, 23, 59, 59, 999));
	}
	
	/**
	 * Make a list of days for use in generating an XML document for the list view.
	 */
	private static List makeListViewTimeRangeList(
		TimeRange timeRange,
		List calendarReferenceList)
	{
		// This is used to dimension a hash table.  The default load factor is .75.
		// A rehash isn't done until the number of items in the table is .75 * the number
		// of items in the capacity.
		final int DEFAULT_INITIAL_HASH_CAPACITY = 150;
		
		List listOfDays = new ArrayList();

		// Get a list of merged events.
		CalendarEventVector calendarEventVector =
			CalendarService.getEvents(calendarReferenceList, timeRange);

		Iterator itEvents = calendarEventVector.iterator();
		HashMap datesSeenSoFar = new HashMap(DEFAULT_INITIAL_HASH_CAPACITY);

		while (itEvents.hasNext())
		{
			
			CalendarEvent event = (CalendarEvent) itEvents.next();
			
			//
			// Each event may span multiple days, so we need to split each
			// events's time range into single day slots.
			//
			List timeRangeList =
				splitTimeRangeIntoListOfSingleDayTimeRanges(
					event.getRange(),
					null);
					
			Iterator itDatesInRange = timeRangeList.iterator();
			
			while ( itDatesInRange.hasNext() )
			{
				TimeRange curDay = (TimeRange)itDatesInRange.next();
				String curDate = curDay.firstTime().toStringLocalDate();
				
				if ( !datesSeenSoFar.containsKey(curDate))
				{
					// Add this day to list
					TimeBreakdown startBreakDown = curDay.firstTime().breakdownLocal();
					
					listOfDays.add(
						getFullDayTimeRangeFromYMD(
							startBreakDown.getYear(),
							startBreakDown.getMonth(),
							startBreakDown.getDay()));
						
					datesSeenSoFar.put(curDate, "");
				}
			}
		}
		
		return listOfDays;
	}
	
	/**
	 * @param scheduleType daily, weekly, monthly, or list (no yearly).
	 * @param doc XML output document
	 * @param timeRange this is the overall time range.  For example, for a weekly schedule, it would be the start/end times for the currently selected  week period.
	 * @param dailyTimeRange On a weekly time schedule, even if the overall time range is for a week, you're only looking at a portion of the day (e.g., 8 AM to 6 PM, etc.)
	 * @param userID This is the name of the user whose schedule is being printed.
	 */
	private static void generateXMLDocument(
		int scheduleType,
		Document doc,
		TimeRange timeRange,
		TimeRange dailyTimeRange,
		List calendarReferenceList,
		String userID)
	{

		// This list will have an entry for every week day that we care about.
		List timeRangeList = null;
		TimeRange actualTimeRange = null;
		Element topLevelMaxConcurrentEvents = null;

		switch (scheduleType)
		{
			case WEEK_VIEW :
				actualTimeRange = timeRange;
				timeRangeList =
					getTimeRangeListForWeek(
						actualTimeRange,
						calendarReferenceList,
						dailyTimeRange,
						true);
				break;

			case MONTH_VIEW :
				// Make sure that we trim off the days of the previous and next
				// month.  The time range that we're being passed is "padded"
				// with extra days to make up a full block of an integral number
				// of seven day weeks.
				actualTimeRange = shrinkTimeRangeToCurrentMonth(timeRange);
				timeRangeList =
					splitTimeRangeIntoListOfSingleDayTimeRanges(
						actualTimeRange,
						null);
				break;

			case LIST_VIEW :
				//
				// With the list view, we want to come up with a list of days
				// that have events, not every day in the range.
				//
				actualTimeRange = timeRange;

				timeRangeList =
					makeListViewTimeRangeList(
						actualTimeRange,
						calendarReferenceList);
				break;

			case DAY_VIEW :
				//
				// We have a single entry in the list for a day.  Having a singleton
				// list may seem wasteful, but it allows us to use one loop below
				// for all processing.
				//
				actualTimeRange = timeRange;
				timeRangeList =
					splitTimeRangeIntoListOfSingleDayTimeRanges(
						actualTimeRange,
						dailyTimeRange);
				break;

			default :
				Log.debug(
					"chef",
					"PrintFileGeneration.generateXMLDocument(): bad scheduleType parameter = "
						+ scheduleType);
				break;
		}

		if (timeRangeList != null)
		{
			// Create Root Element
			Element root = doc.createElement(SCHEDULE_NODE);

			if (userID != null)
			{
				writeStringNodeToDom(doc, root, UID_NODE, userID);
			}

			// Write out the number of events that we have per timeslot.
			// This is used to figure out how to display overlapping events.
			// At this level, assume that we start with 1 event.
			topLevelMaxConcurrentEvents = writeStringNodeToDom(
				doc,
				root,
				MAX_CONCURRENT_EVENTS_NAME,
				"1");

			// Add a start time node.
			writeStringNodeToDom(
				doc,
				root,
				START_TIME_ATTRIBUTE_NAME,
				getTimeString(dailyTimeRange.firstTime()));

			// Add the Root Element to Document
			doc.appendChild(root);

			//
			// Only add a "month" node with the first numeric day
			// of the month if we're in the month view.
			//
			if (scheduleType == MONTH_VIEW)
			{
				CalendarUtil monthCalendar = new CalendarUtil();

				// Use the middle of the month since the start/end ranges
				// may be in an adjacent month.
				TimeBreakdown breakDown = actualTimeRange.firstTime().breakdownLocal();
				
				monthCalendar.setDay(breakDown.getYear(), breakDown.getMonth(), breakDown.getDay());
				
				int firstDayOfMonth =
					monthCalendar.getFirstDayOfMonth(breakDown.getMonth()-1);

				// Create a list of events for the given day.
				Element monthElement = doc.createElement(MONTH_NODE_NAME);
				monthElement.setAttribute(
					START_DAY_WEEK_ATTRIBUTE_NAME,
					Integer.toString(firstDayOfMonth));

				root.appendChild(monthElement);
			}

			Iterator itList = timeRangeList.iterator();

			int maxNumberOfColumnsPerRow = 1;

			// Go through all the time ranges (days)
			while (itList.hasNext())
			{
				TimeRange currentTimeRange = (TimeRange) itList.next();
				int maxConcurrentEventsOverListNode = 1;
					

				// Get a list of merged events.
				CalendarEventVector calendarEventVector =
					CalendarService.getEvents(
						calendarReferenceList,
						currentTimeRange);

				//
				// We don't need to generate "empty" event lists for the list view.
				//
				if ( scheduleType == LIST_VIEW && calendarEventVector.size() == 0 )
				{
					continue;
				}
				
				// Create a list of events for the given day.
				Element eventList = doc.createElement(LIST_NODE_NAME);

				// Set the current date
				eventList.setAttribute(
					LIST_DATE_ATTRIBUTE_NAME,
					getDateFromTime(currentTimeRange.firstTime()));

				// Set the maximum number of events per timeslot
				// Assume 1 as a starting point.  This may be changed
				// later on.
				eventList.setAttribute(MAX_CONCURRENT_EVENTS_NAME, Integer.toString(maxConcurrentEventsOverListNode));

				// Calculate the day of the week.
				CalendarUtil cal = new CalendarUtil();

				Time date = currentTimeRange.firstTime();
				TimeBreakdown breakdown = date.breakdownLocal();

				cal.setDay(breakdown.getYear(), breakdown.getMonth(), breakdown.getDay());

				// Set the day of the week as a node attribute.
				eventList.setAttribute(
					LIST_DAY_OF_WEEK_ATTRIBUTE_NAME,
					Integer.toString(cal.getDay_Of_Week() - 1));

				// Attach this list to the top-level node
				root.appendChild(eventList);

				Iterator itEvent = calendarEventVector.iterator();

				//
				// Day and week views use a layout table to assist in constructing the
				// rowspan information for layout.
				//
				if ( scheduleType == DAY_VIEW || scheduleType == WEEK_VIEW )
				{
					SingleDayLayoutTable layoutTable =
						new SingleDayLayoutTable(
							currentTimeRange,
							MAX_OVERLAPPING_COLUMNS,
							SCHEDULE_INTERVAL_IN_MINUTES);

					// Load all the events into our layout table.
					while (itEvent.hasNext())
					{
						CalendarEvent event = (CalendarEvent) itEvent.next();
						layoutTable.addEvent(event);
					}

					List layoutRows = layoutTable.getLayoutRows();

					Iterator rowIterator = layoutRows.iterator();

					// Iterate through the list of rows.
					while (rowIterator.hasNext())
					{
						LayoutRow layoutRow = (LayoutRow) rowIterator.next();
						TimeRange rowTimeRange = layoutRow.getRowTimeRange();
						
						if ( maxNumberOfColumnsPerRow < layoutRow.size() )
						{
							maxNumberOfColumnsPerRow = layoutRow.size();
						}
						
						if ( maxConcurrentEventsOverListNode < layoutRow.size() )
						{
							maxConcurrentEventsOverListNode = layoutRow.size();
						}
						
						Element eventNode = doc.createElement(EVENT_NODE_NAME);
						eventList.appendChild(eventNode);

						// Add the "from" time as an attribute.
						eventNode.setAttribute(
							FROM_ATTRIBUTE_STRING,
							getTimeString(rowTimeRange.firstTime()));

						// Add the "to" time as an attribute.
						eventNode.setAttribute(
							TO_ATTRIBUTE_STRING,
							getTimeString(
								performEndMinuteKludge(
									rowTimeRange.lastTime().breakdownLocal())));

						Element rowNode = doc.createElement(ROW_NODE_NAME);
						
						// Set an attribute indicating the number of columns in this row.
						rowNode.setAttribute(MAX_CONCURRENT_EVENTS_NAME, Integer.toString(layoutRow.size()));
						
						eventNode.appendChild(rowNode);

						Iterator layoutRowIterator = layoutRow.iterator();

						// Iterate through our list of column lists.
						while (layoutRowIterator.hasNext())
						{
							Element columnNode =
								doc.createElement(COLUMN_NODE_NAME);
							rowNode.appendChild(columnNode);

							List columnList = (List) layoutRowIterator.next();

							Iterator columnListIterator = columnList.iterator();

							// Iterate through the list of columns.
							while (columnListIterator.hasNext())
							{
								CalendarEvent event =
									(CalendarEvent) columnListIterator.next();
								generateXMLEvent(
									doc,
									columnNode,
									event,
									SUB_EVENT_NODE_NAME,
									rowTimeRange,
									true,
									false, 
									false);
							}
						}
					}
				}
				else
				{
					// Generate XML for all the events.
					while (itEvent.hasNext())
					{
						CalendarEvent event = (CalendarEvent) itEvent.next();
						generateXMLEvent(
							doc,
							eventList,
							event,
							EVENT_NODE_NAME,
							currentTimeRange,
							false,
							false,
							( scheduleType == LIST_VIEW ? true : false ));
					}
				}
				
				// Update this event after having gone through all the rows.
				eventList.setAttribute(MAX_CONCURRENT_EVENTS_NAME, Integer.toString(maxConcurrentEventsOverListNode));

			}
			
			// Set the node value way up at the head of the document to indicate
			// what the maximum number of columns was for the entire document.
			topLevelMaxConcurrentEvents.getFirstChild().setNodeValue(Integer.toString(maxNumberOfColumnsPerRow));
		}

	}

	/**
	 * Trim the range that is passed in to the containing time range.
	 */
	private static TimeRange trimTimeRange(
		TimeRange containingRange,
		TimeRange rangeToTrim)
	{
		long containingRangeStartTime = containingRange.firstTime().getTime();
		long containingRangeEndTime = containingRange.lastTime().getTime();

		long rangeToTrimStartTime = rangeToTrim.firstTime().getTime();
		long rangeToTrimEndTime = rangeToTrim.lastTime().getTime();

		long trimmedStartTime = 0, trimmedEndTime = 0;

		trimmedStartTime =
			Math.min(
				Math.max(containingRangeStartTime, rangeToTrimStartTime),
				containingRangeEndTime);
		trimmedEndTime =
			Math.max(
				Math.min(containingRangeEndTime, rangeToTrimEndTime),
				rangeToTrimStartTime);

		return TimeService.newTimeRange(
			TimeService.newTime(trimmedStartTime),
			TimeService.newTime(trimmedEndTime),
			true,
			false);
	}
	
	/**
	 * Rounds a time range up to a minimum interval.
	 */
	private static TimeRange roundRangeToMinimumTimeInterval( TimeRange timeRange )
	{
		TimeRange roundedTimeRange = timeRange;
		
		if ( timeRange.duration() < MINIMUM_EVENT_LENGTH_IN_MSECONDS )
		{
			roundedTimeRange = TimeService.newTimeRange( timeRange.firstTime().getTime(), MINIMUM_EVENT_LENGTH_IN_MSECONDS );
		}
		
		return roundedTimeRange;
	}
	
	/**
	 * Generates the XML for an event.
	 */
	private static void generateXMLEvent(
		Document doc,
		Element parent,
		CalendarEvent event,
		String eventNodeName,
		TimeRange containingTimeRange,
		boolean forceMinimumTime,
		boolean hideGroupIfNoSpace,
		boolean performEndTimeKludge)
	{
		Element eventElement = doc.createElement(eventNodeName);

		TimeRange trimmedTimeRange = trimTimeRange(containingTimeRange, event.getRange());
		
		// Optionally force the event to have a minimum time slot.
		if ( forceMinimumTime )
		{
			trimmedTimeRange = roundRangeToMinimumTimeInterval(trimmedTimeRange);
		}
		
		// Add the "from" time as an attribute.
		eventElement.setAttribute(
			FROM_ATTRIBUTE_STRING,
			getTimeString(trimmedTimeRange.firstTime()));

		// Add the "to" time as an attribute.
		Time endTime = null;
		
		// Optionally adjust the end time
		if ( performEndTimeKludge )
		{
			endTime =
			performEndMinuteKludge(
				trimmedTimeRange.lastTime().breakdownLocal());
		}
		else
		{
			endTime = trimmedTimeRange.lastTime();
		}

		eventElement.setAttribute(TO_ATTRIBUTE_STRING, getTimeString(endTime));

		//
		// Add the group (really "site") node
		// Provide that we have space or if we've been told we need to display it.
		//
		if ( !hideGroupIfNoSpace || trimmedTimeRange.duration() > MINIMUM_EVENT_LENGTH_IN_MSECONDS )
		{
			writeStringNodeToDom(doc, eventElement, GROUP_NODE, getSiteName(event));
		}

		// Add the display name node.
		writeStringNodeToDom(
			doc,
			eventElement,
			TITLE_NODE,
			event.getDisplayName());

		// Add the event type node.
		writeStringNodeToDom(doc, eventElement, TYPE_NODE, event.getType());

		// Add the place/location node.
		writeStringNodeToDom(
			doc,
			eventElement,
			PLACE_NODE,
			event.getLocation());

		// If a "Faculty" extra field is present, then add the node.
		writeStringNodeToDom(
			doc,
			eventElement,
			FACULTY_NODE,
			event.getField(FACULTY_EVENT_ATTRIBUTE_NAME));

		parent.appendChild(eventElement);
	}

	/**
	 * Given a Properties structure, generates a list of calendar references.
	 */
	private static List getCalendarListFromParameters(Properties parameters)
	{
		int curParameterNumber = 0;

		List calendarList = new ArrayList();

		String calendarReference = null;

		do
		{
			calendarReference =
				(String) parameters.get(
					CALENDAR_PARAMETER_BASE_NAME + curParameterNumber++);

			if (calendarReference != null)
			{
				calendarList.add(calendarReference);
			}

		}
		while (calendarReference != null);

		return calendarList;
	}

	/**
	 * Given a list of calendar references, formats a URL fragment of parameters.
	 */
	private static String getCalendarListParameters(List calendars)
	{
		Iterator it = calendars.iterator();
		int calendarNumber = 0;
		StringBuffer parameters = new StringBuffer();
		boolean firstTime = true;

		while (it.hasNext())
		{
			String calendarReference = (String) it.next();

			if (!firstTime)
			{
				parameters.append("&");
			}
			else
			{
				firstTime = false;
			}

			parameters.append(
				CALENDAR_PARAMETER_BASE_NAME
					+ calendarNumber++
					+ "="
					+ Validator.escapeHtml(calendarReference));
		}

		return parameters.toString();
	}

	/*
	 * Gets the daily start time parameter from a Properties object filled from URL parameters.
	 */
	static private TimeRange getDailyStartTimeFromParameters(Properties parameters)
	{
		return getTimeRangeParameterByName(
			parameters,
			DAILY_START_TIME_PARAMETER_NAME);
	}

	/**
	 * Gets the standard date string from the time parameter
	 */
	private static String getDateFromTime(Time time)
	{
		TimeBreakdown timeBreakdown = time.breakdownLocal();

		return timeBreakdown.getMonth()
			+ "/"
			+ timeBreakdown.getDay()
			+ "/"
			+ timeBreakdown.getYear();
	}

	/**
	 * Gets the schedule type from a Properties object (filled from a URL parameter list).
	 */
	private static int getScheduleTypeFromParameterList(Properties parameters)
	{
		int scheduleType = UNKNOWN_VIEW;

		// Get the type of schedule (daily, weekly, etc.)
		String scheduleTypeString =
			(String) parameters.get(SCHEDULE_TYPE_PARAMETER_NAME);
		scheduleType = Integer.parseInt(scheduleTypeString);

		return scheduleType;
	}

	/**
	 * Gets a site name given a CalendarEvent
	 */
	private static String getSiteName(CalendarEvent event)
	{
		Calendar calendar = null;
		String calendarName = "";

		try
		{
			calendar =
				CalendarService.getCalendar(event.getCalendarReference());
		}
		catch (IdUnusedException e)
		{
			Log.debug("chef", "PrintFileGeneration.getGroupName(): " + e);
		}
		catch (PermissionException e)
		{
			Log.debug("chef", "PrintFileGeneration.getGroupName(): " + e);
		}

		// Use the context name as the site name.
		if (calendar != null)
		{
			Site site = null;
			
			try
			{
				site = SiteService.getSite(calendar.getContext());
				
				if ( site != null )
				{
					calendarName = site.getTitle();
				}
			}
			catch (IdUnusedException e1)
			{
				Log.debug("chef", "PrintFileGeneration.getGroupName(): " + e1);
			}
		}

		return calendarName;
	}

	/**
	* Access some named configuration value as a string.
	* @param name The configuration value name.
	* @param dflt The value to return if not found.
	* @return The configuration value with this name, or the default value if not found.
	*/
	private static String getString(String name, String dflt)
	{
		return ServerConfigurationService.getString(name, dflt);
	}

	/*
	 * Gets the time range parameter from a Properties object filled from URL parameters.
	 */
	static TimeRange getTimeRangeFromParameters(Properties parameters)
	{
		return getTimeRangeParameterByName(
			parameters,
			TIME_RANGE_PARAMETER_NAME);
	}

	/**
	 * Generates a list of time ranges for a week.  Each range in the list is a day.
	 * @param dailyTimeRange representative daily time range (start hour/minute, end hour/minute)
	 * @param skipSaturdayAndSundayIfNoEvents if true, then Saturday and Sundary are skipped if there are no events.
	 */
	private static ArrayList getTimeRangeListForWeek(
		TimeRange timeRange,
		List calendarReferenceList,
		TimeRange dailyTimeRange,
		boolean skipSaturdayAndSundayIfNoEvents)
	{
		TimeBreakdown startBreakdown = timeRange.firstTime().breakdownLocal();
		
		GregorianCalendar startCalendarDate = TimeService.getCalendar(
			TimeService.getLocalTimeZone(), startBreakdown.getYear(), startBreakdown.getMonth()-1, startBreakdown.getDay(), 0, 0, 0, 0);

		ArrayList weekDayTimeRanges = new ArrayList();

		int sundayDayIndex = 0;
		int saturdayDayIndex = 6;
		boolean skipSaturdayAndSunday = false;
		
		TimeBreakdown startBreakDown =
			dailyTimeRange.firstTime().breakdownLocal();
			
		TimeBreakdown endBreakDown =
			dailyTimeRange.lastTime().breakdownLocal();

		for (int i = sundayDayIndex; i <= saturdayDayIndex; i++)
		{
			//
			// Use the same start/end times for all days.
			//
			Time curStartTime =
				TimeService.newTimeLocal(
					startCalendarDate.get(GregorianCalendar.YEAR),
					startCalendarDate.get(GregorianCalendar.MONTH) + 1,
					startCalendarDate.get(GregorianCalendar.DAY_OF_MONTH),
					startBreakDown.getHour(),
					startBreakDown.getMin(),
					startBreakDown.getSec(),
					startBreakDown.getMs());

			Time curEndTime =
				TimeService.newTimeLocal(
					startCalendarDate.get(GregorianCalendar.YEAR),
					startCalendarDate.get(GregorianCalendar.MONTH) + 1,
					startCalendarDate.get(GregorianCalendar.DAY_OF_MONTH),
					endBreakDown.getHour(),
					endBreakDown.getMin(),
					endBreakDown.getSec(),
					endBreakDown.getMs());

			TimeRange newTimeRange =
				TimeService.newTimeRange(curStartTime, curEndTime, true, false);

			//
			// If the Saturday/Sunday skipping feature is specified, then on 
			// Saturday, if there are no events scheduled, then check Sunday.
			// If Sunday also has no events, then we will trim off Saturday/Sunday
			//
			if (skipSaturdayAndSundayIfNoEvents && i == sundayDayIndex)
			{
				// Get a list of events for Saturday
				CalendarEventVector calendarEventVectorSunday =
					CalendarService.getEvents(
						calendarReferenceList,
						newTimeRange);

				// If there are no Sunday events, check if there are no Saturday events.
				if (calendarEventVectorSunday.isEmpty())
				{
					GregorianCalendar saturdayCalendarDate = (GregorianCalendar)startCalendarDate.clone();
					saturdayCalendarDate.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SATURDAY);

					//
					// Use the same start/end times for all days.
					//
					Time saturdayStartTime =
						TimeService.newTimeLocal(
							saturdayCalendarDate.get(GregorianCalendar.YEAR),
							saturdayCalendarDate.get(GregorianCalendar.MONTH)
								+ 1,
							saturdayCalendarDate.get(
								GregorianCalendar.DAY_OF_MONTH),
							startBreakDown.getHour(),
							startBreakDown.getMin(),
							startBreakDown.getSec(),
							startBreakDown.getMs());

					Time saturdayEndTime =
						TimeService.newTimeLocal(
							saturdayCalendarDate.get(GregorianCalendar.YEAR),
							saturdayCalendarDate.get(GregorianCalendar.MONTH)
								+ 1,
							saturdayCalendarDate.get(
								GregorianCalendar.DAY_OF_MONTH),
							endBreakDown.getHour(),
							endBreakDown.getMin(),
							endBreakDown.getSec(),
							endBreakDown.getMs());
					TimeRange saturdayTimeRange =
						TimeService.newTimeRange(saturdayStartTime, saturdayEndTime, true, false);

					// Get a list of events for Sunday
					CalendarEventVector calendarEventVectorSaturday =
						CalendarService.getEvents(
							calendarReferenceList,
							saturdayTimeRange);

					// If Saturday and Sunday have no events, set a flag to skip them.
					if (calendarEventVectorSaturday.isEmpty())
					{
						skipSaturdayAndSunday = true;
					}
				}
			}

			// If it is not the case that "this is Saturday or Sunday
			// and we're skipping Saturday and Sunday", then
			// add the range to the list.
			if (!((i == sundayDayIndex || i == saturdayDayIndex)
				&& skipSaturdayAndSunday))
			{
				weekDayTimeRanges.add(newTimeRange);
			}
			
			// Move to the next day.
			startCalendarDate.add(GregorianCalendar.DATE, 1);
		}

		return weekDayTimeRanges;
	}

	/**
	 * Utility routine to get a time range parameter from the URL parameters store in a Properties object.
	 */
	static private TimeRange getTimeRangeParameterByName(
		Properties parameters,
		String name)
	{
		// Now get the time range.
		String timeRangeString = (String) parameters.get(name);

		TimeRange timeRange = null;
		timeRange = TimeService.newTimeRange(timeRangeString);

		return timeRange;
	}

	/**
	 * Gets a standard time string give the time parameter.
	 */
	private static String getTimeString(Time time)
	{
		TimeBreakdown timeBreakdown = time.breakdownLocal();

		DecimalFormat twoDecimalDigits = new DecimalFormat("00");

		return timeBreakdown.getHour()
			+ HOUR_MINUTE_SEPARATOR
			+ twoDecimalDigits.format(timeBreakdown.getMin());
	}

	/**
	 * Given a schedule type, the appropriate XSLT file is returned
	 */
	private static String getXSLFileNameForScheduleType(
		int scheduleType,
		ServletContext servletContext)
	{
		// get a full file system path to the file
		String baseFileName = "";
		String fullPath = "";
		boolean goodScheduleType = false;

		switch (scheduleType)
		{
			case WEEK_VIEW :
				baseFileName = WEEK_VIEW_XLST_FILENAME;
				goodScheduleType = true;
				break;

			case DAY_VIEW :
				baseFileName = DAY_VIEW_XLST_FILENAME;
				goodScheduleType = true;
				break;

			case MONTH_VIEW :
				baseFileName = MONTH_VIEW_XLST_FILENAME;
				goodScheduleType = true;
				break;

			case LIST_VIEW :
				baseFileName = LIST_VIEW_XLST_FILENAME;
				goodScheduleType = true;
				break;

			default :
				Log.debug(
					"chef",
					"PrintFileGeneration.getXSLFileNameForScheduleType(): unexpected scehdule type = "
						+ scheduleType);
				break;
		}

		if (goodScheduleType)
		{
			fullPath =
				servletContext.getRealPath(
					XSLT_BASE_DIRECTORY + "/" + baseFileName);
		}

		return fullPath;
	}

	/**
	 * This routine is used to round the end time.  The time is stored at one minute less than the
	 * actual end time, but the user will expect to see the end time on the hour.  For example,
	 * an event that ends at 10:00 is actually stored at 9:59.  This code should really be
	 * in a central place so that the velocity template can see it as well.
	 */
	private static Time performEndMinuteKludge(TimeBreakdown breakDown)
	{
		int endMin = breakDown.getMin();
		int endHour = breakDown.getHour();

		int tmpMinVal = endMin % TIMESLOT_FOR_OVERLAP_DETECTION_IN_MINUTES;

		if (tmpMinVal == 4 || tmpMinVal == 9)
		{
			endMin = endMin + 1;

			if (endMin == 60)
			{
				endMin = 00;
				endHour = endHour + 1;
			}
		}

		return TimeService.newTimeLocal(
			breakDown.getYear(),
			breakDown.getMonth(),
			breakDown.getDay(),
			endHour,
			endMin,
			breakDown.getSec(),
			breakDown.getMs());
	}

	/**
	 * Called by the servlet to service a get/post requesting a calendar in PDF format.
	 */
	public static void printSchedule(
		Properties parameters,
		StringBuffer contentType,
		ServletContext servletContext,
		OutputStream os)
		throws PermissionException
	{
		// Get the user name.
		String userName = (String) parameters.get(USER_NAME_PARAMETER_NAME);

		// Get the list of calendars.
		List calendarReferenceList = getCalendarListFromParameters(parameters);
		
		// check if there is any calendar to which the user has acces
		Iterator it = calendarReferenceList.iterator();
		int permissionCount = calendarReferenceList.size();
		while (it.hasNext())
		{
			String calendarReference = (String) it.next();
			try
			{
				CalendarService.getCalendar(calendarReference);
			}

			catch (IdUnusedException e)
			{
				continue;
			}

			catch (PermissionException e)
			{
				permissionCount--;
				continue;
			}
		}
		// if no permission to any of the calendars, throw exception and do nothing
		// the expection will be caught by AccessServlet.doPrintingRequest()
		if (permissionCount == 0)
		{
			throw new PermissionException("", "", "");
		}
		else
		{
			// Get the type of schedule (daily, weekly, etc.)
			int scheduleType = getScheduleTypeFromParameterList(parameters);
	
			// Now get the time range.
			TimeRange timeRange = getTimeRangeFromParameters(parameters);
	
			Document document = new DocumentImpl();
	
			generateXMLDocument(
				scheduleType,
				document,
				timeRange,
				getDailyStartTimeFromParameters(parameters),
				calendarReferenceList,
				userName);
	
			// Start Debug Code
	
			//dumpGeneratedXML(servletContext, document);
			// Leaving this call in, but commented out until PDF generation is
			// more mature. -- brettm 6/18/2003
	
			// End Debug Code		
	
			generatePDF(
				document,
				getXSLFileNameForScheduleType(scheduleType, servletContext),
				os);
	
			// Return the content type so that it can be set in the response.
			if (contentType != null)
			{
				contentType.setLength(0);
				contentType.append(PDF_MIME_TYPE);
			}
		}
	}

	/**
	 * The time ranges that we get from the CalendarAction class have days in the week
	 * of the first and last weeks padded out to make a full week.  This function will
	 * shrink this range to only one month. 
	 */
	static private TimeRange shrinkTimeRangeToCurrentMonth(TimeRange expandedTimeRange)
	{
		long millisecondsInWeek = (7 * MILLISECONDS_IN_DAY);

		Time startTime = expandedTimeRange.firstTime();

		// Grab something in the middle of the time range so that we know that we're
		// in the right month.
		Time somewhereInTheMonthTime =
			TimeService.newTime(startTime.getTime() + 2 * millisecondsInWeek);

		TimeBreakdown somewhereInTheMonthBreakdown =
			somewhereInTheMonthTime.breakdownLocal();

		CalendarUtil calendar = new CalendarUtil();

		calendar.setDay(
			somewhereInTheMonthBreakdown.getYear(),
			somewhereInTheMonthBreakdown.getMonth(),
			somewhereInTheMonthBreakdown.getDay());

		int numDaysInMonth = calendar.getNumberOfDays();

		//
		// Construct a new time range starting on the first day of the month and ending on
		// the last day at one millisecond before midnight.
		//
		return TimeService.newTimeRange(
			TimeService.newTimeLocal(
				somewhereInTheMonthBreakdown.getYear(),
				somewhereInTheMonthBreakdown.getMonth(),
				1,
				0,
				0,
				0,
				0),
			TimeService.newTimeLocal(
				somewhereInTheMonthBreakdown.getYear(),
				somewhereInTheMonthBreakdown.getMonth(),
				numDaysInMonth,
				23,
				59,
				59,
				999));
	}

	/**
	 * Calculate the number of days in a range of time given two dates.
	 * @param startMonth (zero based, 0-11)
	 * @param startDay (one based, 1-31)
	 * @param endYear (one based, 1-31)
	 * @param endMonth (zero based, 0-11
	 */
	private static long getNumberDaysGivenTwoDates(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay)
	{
		GregorianCalendar startDate = new GregorianCalendar();
		GregorianCalendar endDate = new GregorianCalendar();

		startDate.set(startYear, startMonth, startDay, 0, 0, 0);
		endDate.set(endYear, endMonth, endDay, 0, 0, 0);

		long duration =
			endDate.getTime().getTime() - startDate.getTime().getTime();

		// Allow for daylight savings time.
		return ((duration + MILLISECONDS_IN_HOUR) / (24 * MILLISECONDS_IN_HOUR)) + 1;
	}
	
	/**
	 * Returns a list of daily time ranges for every day in a range.
	 * @param timeRange overall time range
	 * @param dailyTimeRange representative daily time range (start hour/minute, end hour/minute).  If null, this parameter is ignored.
	 */
	private static ArrayList splitTimeRangeIntoListOfSingleDayTimeRanges(
		TimeRange timeRange,
		TimeRange dailyTimeRange)
	{

		TimeBreakdown startBreakdown = timeRange.firstTime().breakdownLocal();
		TimeBreakdown endBreakdown = timeRange.lastTime().breakdownLocal();
		
		GregorianCalendar startCalendarDate = new GregorianCalendar();
		startCalendarDate.set(startBreakdown.getYear(), startBreakdown.getMonth()-1, startBreakdown.getDay(), 0, 0, 0);
		

		long numDaysInTimeRange =
			getNumberDaysGivenTwoDates(
				startBreakdown.getYear(),
				startBreakdown.getMonth() - 1,
				startBreakdown.getDay(),
				endBreakdown.getYear(),
				endBreakdown.getMonth() - 1,
				endBreakdown.getDay());

		ArrayList splitTimeRanges = new ArrayList();

		TimeBreakdown dailyStartBreakDown = null;
		TimeBreakdown dailyEndBreakDown = null;
		
		if (dailyTimeRange != null)
		{
			dailyStartBreakDown = dailyTimeRange.firstTime().breakdownLocal();
			dailyEndBreakDown = dailyTimeRange.lastTime().breakdownLocal();
		}

		for (long i = 0; i < numDaysInTimeRange; i++)
		{
			Time curStartTime = null;
			Time curEndTime = null;

			if (dailyTimeRange != null)
			{
				//
				// Use the same start/end times for all days.
				//
				curStartTime =
					TimeService.newTimeLocal(
						startCalendarDate.get(GregorianCalendar.YEAR),
						startCalendarDate.get(GregorianCalendar.MONTH) + 1,
						startCalendarDate.get(GregorianCalendar.DAY_OF_MONTH),
						dailyStartBreakDown.getHour(),
						dailyStartBreakDown.getMin(),
						dailyStartBreakDown.getSec(),
						dailyStartBreakDown.getMs());

				curEndTime =
					TimeService.newTimeLocal(
						startCalendarDate.get(GregorianCalendar.YEAR),
						startCalendarDate.get(GregorianCalendar.MONTH) + 1,
						startCalendarDate.get(GregorianCalendar.DAY_OF_MONTH),
						dailyEndBreakDown.getHour(),
						dailyEndBreakDown.getMin(),
						dailyEndBreakDown.getSec(),
						dailyEndBreakDown.getMs());

				splitTimeRanges.add(
					TimeService.newTimeRange(curStartTime, curEndTime, true, false));
			}
			else
			{
				//
				// Add a full day range since no start/stop time was specified.
				//
				splitTimeRanges.add(
					getFullDayTimeRangeFromYMD(
						startCalendarDate.get(GregorianCalendar.YEAR),
						startCalendarDate.get(GregorianCalendar.MONTH)+1,
						startCalendarDate.get(GregorianCalendar.DAY_OF_MONTH)));
			}

			// Move to the next day.
			startCalendarDate.add(GregorianCalendar.DATE, 1);
		}

		return splitTimeRanges;
	}

	/**
	 * Called when generating the HTML for a page that will have a link to the PDF generation process.
	 */
	public static String submissionsPrintingReference(
		int scheduleType,
		List calendars,
		String timeRangeString,
		String userName,
		TimeRange dailyTimeRange)
	{
		String REFERENCE_ROOT = "/" + PRINTING_REQUEST_NAME;

		return REFERENCE_ROOT
			+ "?"
			+ SCHEDULE_TYPE_PARAMETER_NAME
			+ "="
			+ Validator.escapeHtml(new Integer(scheduleType).toString())
			+ "&"
			+ getCalendarListParameters(calendars)
			+ "&"
			+ TIME_RANGE_PARAMETER_NAME
			+ "="
			+ timeRangeString
			+ "&"
			+ Validator.escapeHtml(USER_NAME_PARAMETER_NAME)
			+ "="
			+ Validator.escapeHtml(userName)
			+ "&"
			+ DAILY_START_TIME_PARAMETER_NAME
			+ "="
			+ Validator.escapeHtml(dailyTimeRange.toString());

	} // submissionsPrintingReference

	/**
	 * Utility routine to write a string node to the DOM.
	 */
	private static Element writeStringNodeToDom(
		Document doc,
		Element parent,
		String nodeName,
		String nodeValue)
	{
		if (nodeValue != null && nodeValue.length() != 0)
		{
			Element name = doc.createElement(nodeName);
			name.appendChild(doc.createTextNode(nodeValue));
			parent.appendChild(name);
			return name;
		}
		
		return null;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/util/src/java/org/sakaiproject/util/PrintFileGenerator.java,v 1.1 2005/04/14 02:17:52 ggolden.umich.edu Exp $
*
**********************************************************************************/
