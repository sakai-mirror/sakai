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

package org.navigoproject.business.entity.evaluation.model.util;

import java.util.Date;
import java.util.HashMap;
import org.apache.log4j.Logger;

import org.navigoproject.business.entity.SortableDate;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.util.StringParseUtils;
/**
 *
 * @author Ed Smiley
 * @version $Id: MediaArchiveNamer.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */

public class MediaArchiveNamer
{
  // log4j
  public static Logger LOG = Logger.getLogger(MediaArchiveNamer.class.getName());

  // internals
  private static final int maxAuthorName = 20;
  private static final int maxDescription = 15;
  private static final int maxFileName = 64;

  /**
   * Makes a unique file name from the data supplied.
   *
   * @param md Media Data object
   *
   * @return a meaningful file name
   */
  public static String make(MediaData md)
  {
    String authorName = md.getAuthor();
    String description = md.getDescription();
    String location = md.getLocation();
    String fileName = md.getFileName();
    Date date = md.getDateAdded();

    return make(authorName, description, location, fileName, date);
  }

  /**
   * Makes a unique file name from the data supplied.
   *
   * @param authorName The name of the person uploading the file
   * @param agent_id The id code of the person uploading the file
   * @param description The name of the course, assignment, part,
   *  commentary, notes etc.
   *
   * @return a meaningful file name
   */
  public static String make(String authorName, String description, String location,
                            String fileName, Date date)

  {
//    if((location != null) && location.startsWith("file://"))
//    {
//      return getNameFromLocation(location);
//    }

    // we look at the name file uploaded as to determine the extension
    String extension = "";
    if (fileName!=null && !"".equals(fileName))
    {
          fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    // we can still create a unique file if one or more values are null
    authorName = "" + authorName;
    description = "" + description;

    // hold in a StringBuffer
    StringBuffer sb = new StringBuffer();

    // make a unique random signature
    String rand = "" + Math.random() + "" + Math.random();

    sb.append(
      StringParseUtils.simplifyString("" + authorName, maxAuthorName, authorName.length()));
    sb.append(
      StringParseUtils.simplifyString(
        "" + description, maxDescription,
        description.length()));
    sb.append(new SortableDate(date));
    sb.append("__");
    sb.append(StringParseUtils.simplifyString(rand, 99, 99));

    // return as a string
    if(sb.length() > maxFileName)
    {
      return sb.substring(0, maxFileName - 1);
    }

    String name = sb.toString();
    if (!"".equals(extension)) name += "." + extension;
    return name;
  }


  public static void main(String[] args)
  {
    unitTest();
  }
  /**
   * unit test for use with jUnit etc.
   */
  public static void unitTest()
  {
    String s;
    s = make("Ed Smiley", "Image of a Wombat for Intro to Wombats 101", null,
             "wombat.gif", new Date());
    LOG.debug("Wombat file: " + s);
    s = make("Rachel Gollub", "Image of Wolverines and Aardvarks", null,
             "8989382938.jpg", new Date());
    LOG.debug("Wolverines and Aardvarks file: " + s);
    s = make(null, null, "file://rachel_gollub_another_image_of_wolver_200404141222__0_8983278984783.gif",
             null,null);
    LOG.debug("Another Wolverines and Aardvarks file: " + s);

    s = make(null, null, null, null,null);
    System.out.println("NULL file: " + s);
  }

  /**
   * Helper method.
   * Strip the "file://" or other 7 char heading
   * @param location
   * @return
   */
  public static String getNameFromLocation(String location)
  {
//    System.out.println("got here");
    String s = location + "            ";
    s = s.substring(7);
    s = s.trim();

    return s;
  }



}