/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/log/Jdk14LoggerFormatter.java,v 1.1 2004/06/25 03:58:46 ggolden.umich.edu Exp $
*
***********************************************************************************
@license@
**********************************************************************************/

package org.sakaiproject.component.framework.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * <p>Jdk14LoggerFormatter is a formatter that may be used by log implementations making use of the Java 1.4 built in logging features.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.1 $
 */
public class Jdk14LoggerFormatter extends Formatter
{
	/** The time zone for our GMT times. */
	protected static TimeZone M_tz = null;

	/** formatter for times. */
	protected static SimpleDateFormat M_fmt = null;

	static {
		M_tz = TimeZone.getTimeZone("GMT");
		M_fmt = new SimpleDateFormat("[ddMMMyyyy:HH:mm:ss]");
		M_fmt.setTimeZone(M_tz);
	}

	public String format(LogRecord record)
	{
		return record.getSequenceNumber()
			+ " "
			+ M_fmt.format(new Date(record.getMillis()))
			+ " "
			+ record.getLevel().getName()
			+ " "
			+ record.getMessage()
			+ "\n";
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/log/Jdk14LoggerFormatter.java,v 1.1 2004/06/25 03:58:46 ggolden.umich.edu Exp $
*
**********************************************************************************/