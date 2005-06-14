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

package org.navigoproject.ui.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.navigoproject.settings.ApplicationSettings;

/**
 * Helper class to get request attributes initilaized for view's consumption.
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id: FunctionalityDisabler.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class FunctionalityDisabler
{
  private static final Log LOG = LogFactory.getLog(FunctionalityDisabler.class);
  private static final String POOLINGUSERADMINDISABLED = "poolingUserAdminDisabled";
  private static final String DISABLEFILEUPLOAD = "disableFileUpload";
  private static final String DISABLEAUDIORECORDING = "disableAudioRecording";
  private static final String DISABLETRUEFALSE = "disableTrueFalse";
  private static final String DISABLEMATCHING = "disableMatching";
  private static final String DISABLEFILLINTHEBLANK = "disableFillInTheBlank";
  private static final String DISABLESHORTANSWERESSAY = "disableShortAnswerEssay";
  private static final String DISABLEMULTIPLECHOICESURVEY = "disableMultipleChoiceSurvey";
  private static final String DISABLEMULTIPLECHOICEMULTIPLE = "disableMultipleChoiceMultiple";
  private static final String DISABLEMULTIPLECHOICESINGLE = "disableMultipleChoiceSingle";
  private static final String TRUE = "true";

  public static void populateDisableVariables(HttpServletRequest request)
  {
    LOG.debug("populateDisableVariables(HttpServletRequest request)");

    if (ApplicationSettings.isDisableMultipleChoiceSingle())
        request.setAttribute(DISABLEMULTIPLECHOICESINGLE, TRUE);
    if (ApplicationSettings.isDisableMultipleChoiceMultiple())
        request.setAttribute(DISABLEMULTIPLECHOICEMULTIPLE, TRUE);
    if (ApplicationSettings.isDisableMultipleChoiceSurvey())
        request.setAttribute(DISABLEMULTIPLECHOICESURVEY, TRUE);
    if (ApplicationSettings.isDisableShortAnswerEssay())
        request.setAttribute(DISABLESHORTANSWERESSAY, TRUE);
    if (ApplicationSettings.isDisableFillInTheBlank())
        request.setAttribute(DISABLEFILLINTHEBLANK, TRUE);
    if (ApplicationSettings.isDisableMatching())
        request.setAttribute(DISABLEMATCHING, TRUE);
    if (ApplicationSettings.isDisableTrueFalse())
        request.setAttribute(DISABLETRUEFALSE, TRUE);
    if (ApplicationSettings.isDisableAudioRecording())
        request.setAttribute(DISABLEAUDIORECORDING, TRUE);
    if (ApplicationSettings.isDisableFileUpload())
        request.setAttribute(DISABLEFILEUPLOAD, TRUE);
    if (ApplicationSettings.isPoolingUserAdminDisabled())
        request.setAttribute(POOLINGUSERADMINDISABLED, TRUE);
  }
}