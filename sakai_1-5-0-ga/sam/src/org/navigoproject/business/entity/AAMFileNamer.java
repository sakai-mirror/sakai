/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

package org.navigoproject.business.entity;

import java.util.Date;
import org.navigoproject.util.*;
import org.apache.log4j.Logger;

/**
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 *
 * <p>
 * Organization: Stanford University
 * </p>
 *
 * <p>
 * This class implements common methods for describing data needed to
 * record/save and retrieve audio recordings.
 * </p>
 *
 * <p>
 * Usage (hypothetical example): <code><br>
 * <br>
 * RecordingData rd = new RecordingData( agent_name,  agent_id, course_name,
 * course_id); LOG.debug("file" + rd.getFileName() + "." +
 * rd.getFileExtension()); </code>
 * </p>
 *
 * @author Ed Smiley
 * @version 1.0
 */
public class AAMFileNamer
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AAMFileNamer.class);

  // internals
  private static final int maxAgentName = 20;
  private static final int maxAgentId = 10;
  private static final int maxcourseAssignmentContext = 15;
  private static final int maxCourseId = 10;
  private static final int maxFileName = 64;

  /**
   * Makes a unique file name from the data supplied.
   *
   * @param agent_name The name of the person uploading the file
   * @param agent_id The id code of the person uploading the file
   * @param course_assignment_context The name of the course, assignment, part,
   *        quetion etc.
   *
   * @return a meaningful file name
   */
  public static String make(
    String agent_name, String agent_id, String course_assignment_context)
  {
    // we can still create a unique file if one or more values are null
    agent_name = "" + agent_name;
    agent_id = "" + agent_id;
    course_assignment_context = "" + course_assignment_context;

    //timestamp this access
    Date date = new Date();

    // hold in a StringBuffer
    StringBuffer sb = new StringBuffer();

    // make a unique random signature
    String rand = "" + Math.random() + "" + Math.random();

    sb.append(
      StringParseUtils.simplifyString("" + agent_name, maxAgentName, agent_name.length()));
    sb.append(
      StringParseUtils.simplifyString("" + agent_id, maxAgentId, agent_id.length()));
    sb.append(
      StringParseUtils.simplifyString(
        "" + course_assignment_context, maxcourseAssignmentContext,
        course_assignment_context.length()));
    sb.append(new SortableDate(date));
    sb.append("__");
    sb.append(StringParseUtils.simplifyString(rand, 99, 99));

    // return as a string
    if(sb.length() > maxFileName)
    {
      return sb.substring(0, maxFileName - 1);
    }

    return sb.toString();
  }

  /**
   * unit test for use with jUnit etc.
   */
  public static void unitTest()
  {
    String s;
    s = make("Ed Smiley", "esmiley", "Intro to Wombats 101");
    LOG.debug("esmiley file: " + s);

    s = make(
        "Rachel Gollub", "rgollub", "Intro to Wolverines and Aardvarks 221B");
    LOG.debug("rgollub file: " + s);

    s = make("Ed Smiley", "esmiley", "Intro to Wombats 101");
    LOG.debug("esmiley file: " + s);

    s = make(
        "Rachel Gollub", "rgollub", "Intro to Wolverines and Aardvarks 221B");
    LOG.debug("rgollub file: " + s);

    s = make(null, null, null);
    LOG.debug("NULL file: " + s);
    s = make(null, null, null);
    LOG.debug("NULL file: " + s);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param args DOCUMENTATION PENDING
   */
  public static void main(String[] args)
  {
    unitTest();
  }
}
