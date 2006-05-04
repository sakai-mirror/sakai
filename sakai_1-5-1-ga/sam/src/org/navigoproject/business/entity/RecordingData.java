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

import java.io.File;
import java.io.Serializable;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
// these are used for unit test only
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
 * Usage : <code><br>
 * <br>
 * RecordingData rd = new RecordingData("Rachel Gollub", "rgollub", "Intro to
 * Wolverines and Aardvarks 221B", "10", "25"); LOG.debug("rgollub
 * file:" + rd.getFileName() + "." + rd.getFileExtension());
 * LOG.debug("limit =" + rd.getLimit());
 * LOG.debug("seconds=" + rd.getSeconds()); </code>
 * </p>
 *
 * @author Ed Smiley
 * @version $Id: RecordingData.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class RecordingData
  implements Serializable
{
  // log4j
  public static Logger LOG = Logger.getLogger(RecordingData.class.getName());

  //properties
  private String agentName;
  private String agentId;
  private String courseAssignmentContext;
  private String fileExtension;
  private String fileName;
  private String limit;
  private String dir;
  private String seconds;
  private String appName;
  private String imageURL;

  /**
   * Initialize with required data.  All other values are assigned a default.
   * All values can be overridden with the mutators.
   *
   * @param agent_name The name of the person uploading the file
   * @param agent_id The id code of the person uploading the file
   * @param course_assignment_context The name of the course, assignment, part,
   *        quetion etc.
   * @param lim limit on number of tries (0=unlimited, default if null)
   * @param sec limit on time in seconds (30 default if null)
   *
   * <p>
   * Usage : <code><br>
   * <br>
   * RecordingData rd = new RecordingData("Rachel Gollub", "rgollub", "Intro to
   * Wolverines and Aardvarks 221B", "10", "25"); LOG.debug("rgollub
   * file:" + rd.getFileName() + "." + rd.getFileExtension());
   * LOG.debug("limit =" + rd.getLimit());
   * LOG.debug("seconds=" + rd.getSeconds()); </code>
   * </p>
   */
  public RecordingData(
    String agent_name, String agent_id, String course_assignment_context,
    String lim, String sec)
  {
    agentName = "" + agent_name;
    agentId = "" + agent_id;
    courseAssignmentContext = "" + course_assignment_context;
    // quotation marks can interfere with dynamic JavaScript for WYSIWYG
    courseAssignmentContext = cleanOutQuotes(courseAssignmentContext);
    limit = lim;
    seconds = sec;
    initDefaults();
  }

  /**
   * Usage: for parameters that might show up quoted in dynamic Javascript
   * @param s raw string
   * @return string with no quotes
   */
  private String cleanOutQuotes(String s)
  {
    StringTokenizer st = new StringTokenizer(s, "\"'");
    String cleanS = "";
    while (st.hasMoreTokens())
    {
      cleanS += st.nextToken();
    }

    return cleanS;
  }

  /**
   * Private helper for constructor, sets defaults.
   */
  private void initDefaults()
  {
    fileExtension = "au";
    fileName = AAMFileNamer.make(agentName, agentId, courseAssignmentContext);
    if(limit == null)
    {
      limit = "0";
    }

    if(seconds == null)
    {
      seconds = "30";
    }

    // just to be good, we make this OS independent so you can run on Windoze
    // also you can change via setDir()

    // @todo: determine what is the best default target directory to use
    // for file system uploads
    if(File.separator.equals("/"))
    {
      dir = "/tmp";
    }
    else
    {
      dir = "c:\\tmp";
    }

    appName = "Audio Recording";

    //
    // we will probably want to migrate these images to the
    // main image directory, the current directory is an artifact of
    // Stanford/Indiana code merge
    //
    //    imageURL = "/Navigo/images/";
    //
    // we use slashes because this is a URL
    imageURL = "/Navigo/jsp/aam/images/";  }

  /**
   * Accessor for agent (creator) name.
   *
   * @return agent (creator) name.
   */
  public String getAgentName()
  {
    return agentName;
  }

  /**
   * Accessor for agent (creator) id.
   *
   * @return agent (creator) id.
   */
  public String getAgentId()
  {
    return agentId;
  }

  /**
   * Accessor for free form text describing creation context.
   * See Usage notes.
   *
   * @return free form text describing creation context.
   */
  public String getCourseAssignmentContext()
  {
    return courseAssignmentContext;
  }

  /**
   * Accessor for standard audio filename extension.
   *
   * @return standard audio file extension.
   */
  public String getFileExtension()
  {
    return fileExtension;
  }

  /**
   * Accessor for standard audio filename.
   *
   * @return standard audio file name.
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Accessor for retry limit.
   * If the number of retries are unlimited, this is 0.
   *
   * @return retry limit
   */
  public String getLimit()
  {
    return limit;
  }

  /**
   * Get target file system directory for audio uploads.
   *
   * @return file system directory for audio uploads.
   */
  public String getDir()
  {
    return dir;
  }

  /**
   * Accessor for maximum number of seconds for recording.
   *
   * @return maximum number of seconds for recording.
   */
  public String getSeconds()
  {
    return seconds;
  }

  /**
   * Accessor for the user-facing application name for recording applet.
   *
   * @return user-facing application name for recording applet.
   */
  public String getAppName()
  {
    return appName;
  }

  /**
   * Accessor for the image URL for recording widget images.
   *
   * @return image URL for recording widget images.
   */
  public String getImageURL()
  {
    return imageURL;
  }

  /**
   * Mutator for recording agent.
   *
   * @param s recording agent
   */
  public void setAgentName(String s)
  {
    agentName = s;
  }

  /**
   * Mutator for recording agent id.
   *
   * @param s recording agent id.
   */
  public void setAgentId(String s)
  {
    agentId = s;
  }

  /**
   * Mutator for recording context string.
   * See: Usage
   *
   * @param s recording context string.
   */
  public void setCourseAssignmentContext(String s)
  {
    courseAssignmentContext = s;
  }

  /**
   * Mutator for file extension.
   *
   * @param s audio recording file extension.
   */
  public void setFileExtension(String s)
  {
    fileExtension = s;
  }

  /**
   * Mutator for file name.
   *
   * @param s audio recording file name.
   */
  public void setFileName(String s)
  {
    fileName = s;
  }

  /**
   * Mutator for retry limit.
   *
   * @param s audio recording retry limit.
   */
  public void setLimit(String s)
  {
    limit = s;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param s DOCUMENTATION PENDING
   */
  public void setDir(String s)
  {
    dir = s;
  }

  /**
   * Mutator for seconds limit.
   *
   * @param s audio recording seconds limit.
   */
  public void setSeconds(String s)
  {
    seconds = s;
  }

  /**
   * Mutator  for the user-facing application name for recording applet.
   *
   * @param s user-facing application name for recording applet.
   */
  public void setAppName(String s)
  {
    appName = s;
  }

  /**
   * Mutator for widget images directory.
   *
   * @param s widget images directory.
   */
  public void setImageURL(String s)
  {
    imageURL = s;
  }

  /**
 * This takes a RecordingData object and puts it in XML.
 * @return the XML as an org.w3c.dom.Document
 */
public Document getXMLDataModel()
{
  Document document = null;
  DocumentBuilderFactory builderFactory =
    DocumentBuilderFactory.newInstance();
  builderFactory.setNamespaceAware(true);
  try
  {
    DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
    document = documentBuilder.newDocument();
  }
  catch(ParserConfigurationException e)
  {
    LOG.error(e.getMessage(), e);
  }

  //add audio setup data to  XML document
  //root
  Element recordingData = document.createElement("RecordingData");

  //sub elements
  Element agentName = document.createElement("AgentName");
  Element agentId = document.createElement("AgentId");
  Element courseAssignmentContext = document.createElement("CourseAssignmentContext");
  Element fileExtension = document.createElement("FileExtension");
  Element fileName = document.createElement("FileName");
  Element limit = document.createElement("Limit");
  Element dir = document.createElement("Dir");
  Element seconds = document.createElement("Seconds");
  Element appName = document.createElement("AppName");
  Element imageURL = document.createElement("ImageURL");

  agentName.appendChild(document.createTextNode(this.getAgentName()));
  agentId.appendChild(document.createTextNode(this.getAgentId()));
  courseAssignmentContext.appendChild(
    document.createTextNode(this.getCourseAssignmentContext()));
  fileExtension.appendChild(document.createTextNode(this.getFileExtension()));
  fileName.appendChild(document.createTextNode(this.getFileName()));
  limit.appendChild(document.createTextNode(this.getLimit()));
  dir.appendChild(document.createTextNode(this.getDir()));
  seconds.appendChild(document.createTextNode(this.getSeconds()));
  appName.appendChild(document.createTextNode(this.getAppName()));
  imageURL.appendChild(document.createTextNode(this.getImageURL()));

  recordingData.appendChild(agentName);
  recordingData.appendChild(agentId);
  recordingData.appendChild(courseAssignmentContext);
  recordingData.appendChild(fileExtension);
  recordingData.appendChild(fileName);
  recordingData.appendChild(limit);
  recordingData.appendChild(dir);
  recordingData.appendChild(seconds);
  recordingData.appendChild(appName);
  recordingData.appendChild(imageURL);

  document.appendChild(recordingData);

  // return the recording data available in  XML
  return document;
}


  /**
   * unit test for use with jUnit etc. this only tests the file name
   * computation, the other methods are pretty trivial
   *
   * @param none
   * @return none
   */
  public static void unitTest()
  {
    RecordingData rd =
      new RecordingData(
        "Ed Smiley", "esmiley", "Intro to Wombats 101", "10", "30");
    LOG.debug(
      "esmiley file:" + rd.getFileName() + "." + rd.getFileExtension());
    LOG.debug("limit =" + rd.getLimit());
    LOG.debug("seconds=" + rd.getSeconds());

    rd =
      new RecordingData(
        "Rachel Gollub", "rgollub", "Rachel's Intro to Wolverines and Aardvarks 221B",
        "10", "25");
    LOG.debug(
      "rgollub file:" + rd.getFileName() + "." + rd.getFileExtension());
    LOG.debug("limit =" + rd.getLimit());
    LOG.debug("seconds=" + rd.getSeconds());

    rd =
      new RecordingData(
        "Rachel Gollub", "rgollub", "Intro to Wolverines and Aardvarks 221B",
        "10", "25");
    LOG.debug(
      "rgollub file:" + rd.getFileName() + "." + rd.getFileExtension());
    LOG.debug("limit =" + rd.getLimit());
    LOG.debug("seconds=" + rd.getSeconds());

    OutputFormat formatter = new OutputFormat();
    formatter.setPreserveSpace(true);
    formatter.setIndent(2);
    try
    {
      XMLSerializer serializer = new XMLSerializer(System.out, formatter);
      serializer.serialize(rd.getXMLDataModel());
    }
    catch(Exception e)
    {
      LOG.debug("cannot serialize"+e);
    }

    rd = new RecordingData(null, null, null, null, null);
    LOG.debug(
      "NULL file: " + rd.getFileName() + "." + rd.getFileExtension());
    LOG.debug("limit =" + rd.getLimit());
    LOG.debug("seconds=" + rd.getSeconds());

    rd = new RecordingData(null, null, null, null, null);
    LOG.debug(
      "NULL file: " + rd.getFileName() + "." + rd.getFileExtension());
    LOG.debug("limit =" + rd.getLimit());
    LOG.debug("seconds=" + rd.getSeconds());
  }

  /**
   * hook for unit test
   *
   * @param args not used
   */
  public static void main(String[] args)
  {
    unitTest();
  }
}