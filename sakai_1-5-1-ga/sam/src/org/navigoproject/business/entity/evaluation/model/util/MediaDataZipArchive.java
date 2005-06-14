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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.evaluation.model.AgentResults;
import org.navigoproject.business.entity.evaluation.model.MediaZipInfoModel;
import org.navigoproject.util.MimeType;
import org.navigoproject.util.StringParseUtils;
import org.navigoproject.util.ZipArchiver;


/**
 * Utility to zip up media records.
 * @author Ed Smiley
 * @version $Id: MediaDataZipArchive.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class MediaDataZipArchive extends ZipArchiver
{
  private static HashMap mediaMap = new HashMap();
  static Logger LOG =
      Logger.getLogger(MediaDataZipArchive.class.getName());
  private MediaZipInfoModel zipInfo;
  private Map agents;

  public MediaDataZipArchive(OutputStream zipOut)
  {
    super(zipOut);
    zipInfo = new MediaZipInfoModel();
    agents = new HashMap();
  }

  public MediaDataZipArchive(MediaZipInfoModel mzim)
  {
    super(mzim.getOutputStream());
    zipInfo = mzim;
    agents = mzim.getAgents();
  }

  /**
   * Create a lookup map by submission.
   *
   * This utility takes a Collection of agents and places them in
   * a HashMap by assessmentResultID.
   *
   * @param agents the agents to be processed
   * @return
   */
  public static HashMap createAgentMap(Collection agents){
    HashMap map = new HashMap();
    Iterator iter = agents.iterator();
    while (iter.hasNext()){
      AgentResults result = (AgentResults) iter.next();
      map.put(result.getAssessmentResultID(), result);
    }

    return map;
  }

  /**
   * If you have a set of media in a map of AgentResults,
   * use this method instead of create().
   *
   * This takes a map such as that created by createAgentMap().
   * @param mAgentResults
   */
  public void create(Map mAgentResults){
    String[] is = makeInputSourcesFromMap(mAgentResults);
    create(is);
  }

  private String[] makeInputSourcesFromMap(Map m)
  {
    Set keys = m.keySet();
    Iterator iter = keys.iterator();
    int length = keys.size();
    String[] handles = new String[length];

    for(int count = 0; iter.hasNext(); count++)
    {
      handles[count] = (String) iter.next();
    }

    return handles;
  }

  /**
   * Get name under which MediaData item is to be zipped.
   * @param mediaId the unique identifier of the source to be zipped
   * @return the name
   */
  public String getArchivableName(String resultId)
  {
    AgentResults results = (AgentResults) agents.get(resultId);
    MediaData data = results.getMediaData();
    String mediaFileName = "FILE" + resultId + ".txt";

    if(data != null)
    {
        mediaFileName = MediaArchiveNamer.make(data);
        if (data.getType()!= null)
        {
          String type = data.getType();
          String extension = MimeType.getExtension(type);
          // note that the extension included the "."!
          mediaFileName = mediaFileName + extension;
        }
    }

    return makePath(results) + mediaFileName;
  }

  /**
   * Get input from MediaData item.
   * @param id the MediaData item id to be zipped
   * @return an InputStream into it
   */
  public InputStream getZipInputSource(String assessmentResultID)
  {
    InputStream in = null;
    AgentResults result = (AgentResults) agents.get(assessmentResultID);

    MediaData data = result.getMediaData();
    mediaMap.put(""+data.getId(), data);
    try {

      String location = data.getLocation();
      if((location != null) && location.startsWith("file://"))
      {
        // in file system, call the file input helper method
         in = getMediaFileInputStream(data);
      }
      else
      {
        // otherwise display the raw data in content property
        in = getMediaRawContentInputStream(data);
      }
    }
    catch (Exception ex) {
      // we will store a text error message
      System.out.println("exception ignored: " + ex);
      String s = "failed to locate media for: " +
                          result.getFirstName() + result.getLastName();
      in = new ByteArrayInputStream(s.getBytes());
      data.setType("text/plain");
      result.setMediaData(data);
      agents.put(assessmentResultID, result);
//      throw new RuntimeException("failed to locate media for: " +
//                          result.getFirstName() + result.getLastName());
    }

    return in;
  }

  /**
   * Helper method.
   * Displays media that is in the MediaData content property.
   *
   * @param md MediaData object to be shown
   */
  private InputStream getMediaRawContentInputStream(MediaData md) throws IOException
  {
    return new ByteArrayInputStream(md.getContent());
  }

  /**
   * @todo determine how draconian we want to be with exceptions
   * do we want to abort the whole thing if one student's work cannot be found?
   * right now I am forgiving, lest the rest of the classes's assignments get lost
   *
   * Helper method.
   * Retrieves media that is persisted to the file system with a file:// url
   * @param md MediaData object to be shown
   */
  private FileInputStream getMediaFileInputStream(MediaData md) throws IOException
  {
    FileInputStream fileStream = null;
    String location = md.getLocation();
    LOG.debug("debug:" + location);
    if((location == null) || ! location.startsWith("file://"))
    {
      LOG.warn("Unable to locate media.  Not a file url.");

      return fileStream;
    }

    String fileName = MediaArchiveNamer.getNameFromLocation(location);

    File fileToZip = null;

    if(fileName != null)
    {
      fileToZip = new File(fileName);
    }

    if((fileToZip == null) || fileToZip.exists() || fileToZip.canRead())
    {
      LOG.warn("Unable to locate media.  File cannot be read.");
    }

    try
    {
      fileStream = new FileInputStream(fileName);
    }
    catch(FileNotFoundException e)
    {
      LOG.warn("File " + fileName + " not found:" + e.getMessage());
    }

    return fileStream;

  }

  /**
   * Taking an AgentResults object get a path for its entry
   * @param agent the AgentResults
   * @return the path
   */
  private String makePath(AgentResults agent)
  {
    return makeZipInfoPath(zipInfo) + makeAgentPath(agent);
  }

  /**
   * make the portion of the path that does not change between files
   * @param info the media information
   * @return the path
   */
  private String makeZipInfoPath(MediaZipInfoModel info)
  {
    String course = info.getCourseName();
    String assessment = info.getAssessmentName();
    // I removed this as it is not meaningful to the instructor
//    String pubid = info.getPublishedAssessmentId();
    String p = "p" + info.getPart();
    String q = "q" + info.getQuestion();
    String path =
        StringParseUtils.simplifyString(course, 50,1) + "/" +
        StringParseUtils.simplifyString(assessment, 50,1) + "/" +
//        StringParseUtils.simplifyString(pubid, 32,1) + "/" +
        StringParseUtils.simplifyString(p, 32,1) + "/" +
        StringParseUtils.simplifyString(q, 32,1) + "/";

    return path;
  }

  /**
   * Taking an AgentResults object get a partial path for it
   * @param agent the AgentResults
   * @return the path
   */
  private String makeAgentPath(AgentResults agent)
  {
    String initial = agent.getLastInitial();
    String last = agent.getLastName();
    String first = agent.getFirstName();
    String result = agent.getAssessmentResultID();

    String path =
        StringParseUtils.simplifyString(initial, 1,1) + "/" +
        StringParseUtils.simplifyString(last, 50,1) + "/" +
        StringParseUtils.simplifyString(first, 20,1) + "/" +
        StringParseUtils.simplifyString(result, 32,1) + "/";

    return path;

  }

}
