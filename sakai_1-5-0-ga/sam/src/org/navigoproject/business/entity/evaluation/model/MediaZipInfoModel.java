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
package org.navigoproject.business.entity.evaluation.model;

import java.io.OutputStream;
import java.util.Map;

/**
 * Model the information we may want to put in an evaluation archive
 * in the file names or paths.
 * @author Ed Smiley
 * @version $Id: MediaZipInfoModel.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class MediaZipInfoModel
{
  private OutputStream outputStream;
  private String assessmentName;
  private String publishedAssessmentId;
  private String part;
  private String question;
  private String courseName;
  private Map agents;

  /**
   * get the outputstream for the archive
   * @return outputstream for the archive
   */
  public OutputStream getOutputStream()
  {
    return outputStream;
  }

  /**
   * get the assessment name for the archive
   * @return assessment name for the archive
   */
  public String getAssessmentName()
  {
    return assessmentName;
  }

  /**
   * get the published assessment id for the archive
   * @return published assessment id for the archive
   */
  public String getPublishedAssessmentId()
  {
    return publishedAssessmentId;
  }

  /**
   * get the part for the archive
   * @return part # for the archive
   */
  public String getPart()
  {
    return part;
  }

  /**
   * get the question for the archive
   * @return question # for the archive
   */
  public String getQuestion()
  {
    return question;
  }

  /**
   * get the course name for the archive
   * @return course name for the archive
   */
  public String getCourseName()
  {
    return courseName;
  }

  /**
   * get a collection of agents corresponding to each item
   * @return the collection
   */
  public Map getAgents()
  {
    return agents;
  }

  /**
   * set the outputstream for the archive
   *
   * @param pOutputStream outputstream for the archive
   */
  public void setOutputStream(OutputStream pOutputStream)
  {
    outputStream = pOutputStream;
  }

  /**
   * set the outputstream for the archive
   * @param pAssessmentName the outputstream for the archive
   */
  public void setAssessmentName(String pAssessmentName)
  {
    assessmentName = pAssessmentName;
  }

  /**
   * set the published assessment id for the archive
   * @param pPublishedAssessmentId published assessment id for the archive
   */
  public void setPublishedAssessmentId(String pPublishedAssessmentId)
  {
    publishedAssessmentId = pPublishedAssessmentId;
  }

  /**
   * set the part # for the archive
   * @param pPart the part #
   */
  public void setPart(String pPart)
  {
    part = pPart;
  }

  /**
   * set the question # for the archive
   * @param pQuestion the question #
   */
  public void setQuestion(String pQuestion)
  {
    question = pQuestion;
  }

  /**
   * set the course name for the archive
   * @param pCourseName teh name of the course
   */
  public void setCourseName(String pCourseName)
  {
    courseName = pCourseName;
  }

  /**
   * set up the collection of agents
   * @param pAgents the list of the agents
   */
  public void setAgents(Map pAgents)
  {
    agents = pAgents;
  }

}