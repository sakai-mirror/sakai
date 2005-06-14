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

package test.org.navigoproject.business;


/**
 * An example Assessment in a Java String
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: ExampleXmlString.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class ExampleXmlString
{
  public final static String str =
    "" + "<?xml version = \"1.0\" encoding = \"UTF-8\" standalone = \"no\"?>" +
    " <!DOCTYPE questestinterop SYSTEM \"http://www.imsglobal.org/question/qtiv1p2p1/XMLSchemav1p2p1/xmla/ims_qtiasiv1p2p1schema/dtds/qtiasifulldtd/ims_qtiasiv1p2p1.dtd\">" +
    "" + " <!-- Date:	14th February, 2003   					-->" + "" +
    " <!-- Version 1.2 Compliant Example: AdvExampleA01 		-->" + "" +
    " <!-- Advanced Example with A(1)S(1)I(2)S(1)I(1)			-->" +
    " <questestinterop>" +
    "         <assessment title = \"European Geography\" ident = \"A01\">" +
    "                 <qticomment>A Complex Assessment example.</qticomment>" +
    "                 <objectives view = \"Candidate\">" +
    "                         <flow_mat>" +
    "                                 <material>" +
    "                                         <mattext>To test your knowledge of European geography.</mattext>" +
    "                                 </material>" +
    "                         </flow_mat>" + "                 </objectives>" +
    "                 <objectives view = \"Assessor\">" +
    "                         <flow_mat>" +
    "                                 <material>" +
    "                                         <mattext>Tests the candidate's knowledge of European geography.</mattext>" +
    "                                 </material>" +
    "                         </flow_mat>" + "                 </objectives>" +
    "                 <outcomes_processing>" +
    "                         <qticomment>Processing of the final accumulated assessment.</qticomment>" +
    "                         <outcomes>" +
    "                                 <decvar/>" +
    "                         </outcomes>" +
    "                         <outcomes_feedback_test>" +
    "                                 <test_variable>" +
    "                                         <variable_test testoperator = \"LTE\">9</variable_test>" +
    "                                 </test_variable>" +
    "                                 <displayfeedback feedbacktype = \"Response\" linkrefid = \"A01_FDB01\"/>" +
    "                         </outcomes_feedback_test>" +
    "                         <outcomes_feedback_test>" +
    "                                 <test_variable>" +
    "                                         <variable_test testoperator = \"GTE\">10</variable_test>" +
    "                                 </test_variable>" +
    "                                 <displayfeedback feedbacktype = \"Response\" linkrefid = \"A01_FDB02\"/>" +
    "                         </outcomes_feedback_test>" +
    "                 </outcomes_processing>" +
    "                 <assessfeedback title = \"Failed\" ident = \"A01_FDB01\">" +
    "                         <flow_mat>" +
    "                                 <material>" +
    "                                         <mattext>You failed the test.</mattext>" +
    "                                 </material>" +
    "                         </flow_mat>" +
    "                 </assessfeedback>" +
    "                 <assessfeedback title = \"Passed\" ident = \"A01_FDB02\">" +
    "                         <flow_mat>" +
    "                                 <material>" +
    "                                         <mattext>You passed the test.</mattext>" +
    "                                 </material>" +
    "                         </flow_mat>" +
    "                 </assessfeedback>" +
    "                 <section title = \"European Capitals\" ident = \"S01\">" +
    "                         <objectives view = \"Candidate\">" +
    "                                 <flow_mat>" +
    "                                         <material>" +
    "                                                 <mattext>To assess your knowledge of the capital cities in Europe.</mattext>" +
    "                                         </material>" +
    "                                 </flow_mat>" +
    "                         </objectives>" +
    "                         <objectives view = \"Tutor\">" +
    "                                 <flow_mat>" +
    "                                         <material>" +
    "                                                 <mattext>To ensure that the student knows the difference between the Capital cities of France, UK, Germany, Spain and Italy.</mattext>" +
    "                                         </material>" +
    "                                 </flow_mat>" +
    "                         </objectives>" +
    "                         <item title = \"Capital of France\" ident = \"I01\" maxattempts = \"6\">" +
    "                                 <qticomment>This Item is the first example to be used in the QTI XML Binding Base Document.</qticomment>" +
    "                                 <itemmetadata/>" +
    "                                 <rubric view = \"Candidate\">" +
    "                                         <flow_mat>" +
    "                                                 <material>" +
    "                                                         <mattext>Choose only one of the choices available.</mattext>" +
    "                                                 </material>" +
    "                                         </flow_mat>" +
    "                                 </rubric>" +
    "                                 <presentation label = \"Resp001\">" +
    "                                         <flow>" +
    "                                                 <material>" +
    "                                                         <mattext>What is the Capital of France ?</mattext>" +
    "                                                 </material>" +
    "                                                 <response_lid ident = \"LID01\">" +
    "                                                         <render_choice shuffle = \"Yes\">" +
    "                                                                 <response_label ident = \"LID01_A\">" +
    "                                                                         <flow_mat>" +
    "                                                                                 <material>" +
    "                                                                                         <mattext>London</mattext>" +
    "                                                                                 </material>" +
    "                                                                         </flow_mat>" +
    "                                                                 </response_label>" +
    "                                                                 <response_label ident = \"LID01_B\">" +
    "                                                                         <flow_mat>" +
    "                                                                                 <material>" +
    "                                                                                         <mattext>Paris</mattext>" +
    "                                                                                 </material>" +
    "                                                                         </flow_mat>" +
    "                                                                 </response_label>" +
    "                                                                 <response_label ident = \"LID01_C\">" +
    "                                                                         <flow_mat>" +
    "                                                                                 <material>" +
    "                                                                                         <mattext>Washington</mattext>" +
    "                                                                                 </material>" +
    "                                                                         </flow_mat>" +
    "                                                                 </response_label>" +
    "                                                                 <response_label ident = \"LID01_D\" rshuffle = \"No\">" +
    "                                                                         <flow_mat>" +
    "                                                                                 <material>" +
    "                                                                                         <mattext>Berlin</mattext>" +
    "                                                                                 </material>" +
    "                                                                         </flow_mat>" +
    "                                                                 </response_label>" +
    "                                                         </render_choice>" +
    "                                                 </response_lid>" +
    "                                         </flow>" +
    "                                 </presentation>" +
    "                                 <resprocessing>" +
    "                                         <qticomment/>" +
    "                                         <outcomes>" +
    "                                                 <decvar vartype = \"Integer\" defaultval = \"0\"/>" +
    "                                         </outcomes>" +
    "                                         <respcondition>" +
    "                                                 <qticomment>Scoring for the correct answer.</qticomment>" +
    "                                                 <conditionvar>" +
    "                                                         <varequal respident = \"LID01\">LID01_B</varequal>" +
    "                                                 </conditionvar>" +
    "                                                 <setvar action = \"Set\" varname = \"SCORE\">10</setvar>" +
    "                                                 <displayfeedback feedbacktype = \"Response\" linkrefid = \"I01_IFBK01\"/>" +
    "                                         </respcondition>" +
    "                                 </resprocessing>" +
    "                                 <itemfeedback title = \"Correct answer\" ident = \"I01_IFBK01\">" +
    "                                         <flow_mat>" +
    "                                                 <material>" +
    "                                                         <mattext>Correct answer.</mattext>" +
    "                                                 </material>" +
    "                                         </flow_mat>" +
    "                                 </itemfeedback>" +
    "                                 <itemfeedback ident = \"I01_IFBK02\">" +
    "                                         <solution>" +
    "                                                 <solutionmaterial>" +
    "                                                         <flow_mat>" +
    "                                                                 <material>" +
    "                                                                         <mattext>London is the Capital of England.</mattext>" +
    "                                                                 </material>" +
    "                                                         </flow_mat>" +
    "                                                         <flow_mat>" +
    "                                                                 <material>" +
    "                                                                         <mattext>Paris is the Capital of France.</mattext>" +
    "                                                                 </material>" +
    "                                                         </flow_mat>" +
    "                                                         <flow_mat>" +
    "                                                                 <material>" +
    "                                                                         <mattext>Washington is in the USA.</mattext>" +
    "                                                                 </material>" +
    "                                                         </flow_mat>" +
    "                                                         <flow_mat>" +
    "                                                                 <material>" +
    "                                                                         <mattext>Berlin is the Capital of Germany.</mattext>" +
    "                                                                 </material>" +
    "                                                         </flow_mat>" +
    "                                                 </solutionmaterial>" +
    "                                         </solution>" +
    "                                 </itemfeedback>" +
    "                                 <itemfeedback ident = \"I01_IFBK03\" view = \"All\">" +
    "                                         <hint feedbackstyle = \"Multilevel\">" +
    "                                                 <hintmaterial>" +
    "                                                         <flow_mat>" +
    "                                                                 <material>" +
    "                                                                         <mattext>One of the choices is not in Europe.</mattext>" +
    "                                                                 </material>" +
    "                                                         </flow_mat>" +
    "                                                 </hintmaterial>" +
    "                                                 <hintmaterial>" +
    "                                                         <flow_mat>" +
    "                                                                 <material>" +
    "                                                                         <mattext>Berlin is the Capital of Germany.</mattext>" +
    "                                                                 </material>" +
    "                                                         </flow_mat>" +
    "                                                 </hintmaterial>" +
    "                                                 <hintmaterial>" +
    "                                                         <flow_mat>" +
    "                                                                 <material>" +
    "                                                                         <mattext>The Eiffel tower can be found in the Capital of France.</mattext>" +
    "                                                                 </material>" +
    "                                                         </flow_mat>" +
    "                                                 </hintmaterial>" +
    "                                         </hint>" +
    "                                 </itemfeedback>" +
    "                         </item>" + "                 </section>" +
    "                 <section title = \"European Rivers\" ident = \"SO2\">" +
    "                         <objectives view = \"Candidate\">" +
    "                                 <flow_mat>" +
    "                                         <material>" +
    "                                                 <mattext>To assess your knowledge of the rivers in Europe.</mattext>" +
    "                                         </material>" +
    "                                 </flow_mat>" +
    "                         </objectives>" +
    "                         <objectives view = \"Assessor\">" +
    "                                 <flow_mat>" +
    "                                         <material>" +
    "                                                 <mattext>Questions on rivers in Germany, Spain, Italy and France.</mattext>" +
    "                                         </material>" +
    "                                 </flow_mat>" +
    "                         </objectives>" +
    "                         <item title = \"Rivers in France question\" ident = \"I02\">" +
    "                                 <rubric view = \"Candidate\">" +
    "                                         <flow_mat>" +
    "                                                 <material>" +
    "                                                         <mattext>Choose all of the correct answers.</mattext>" +
    "                                                 </material>" +
    "                                         </flow_mat>" +
    "                                 </rubric>" +
    "                                 <presentation label = \"Resp002\">" +
    "                                         <flow>" +
    "                                                 <material>" +
    "                                                         <mattext>Which rivers are in France ?</mattext>" +
    "                                                 </material>" +
    "                                                 <response_lid ident = \"LID02\" rcardinality = \"Multiple\">" +
    "                                                         <render_choice shuffle = \"Yes\" minnumber = \"1\" maxnumber = \"2\">" +
    "                                                                 <response_label ident = \"LID02_A\">" +
    "                                                                         <flow_mat>" +
    "                                                                                 <material>" +
    "                                                                                         <mattext>Seine</mattext>" +
    "                                                                                 </material>" +
    "                                                                         </flow_mat>" +
    "                                                                 </response_label>" +
    "                                                                 <response_label ident = \"LID02_B\">" +
    "                                                                         <flow_mat>" +
    "                                                                                 <material>" +
    "                                                                                         <mattext>Thames</mattext>" +
    "                                                                                 </material>" +
    "                                                                         </flow_mat>" +
    "                                                                 </response_label>" +
    "                                                                 <response_label ident = \"LID02_C\">" +
    "                                                                         <flow_mat>" +
    "                                                                                 <material>" +
    "                                                                                         <mattext>Danube</mattext>" +
    "                                                                                 </material>" +
    "                                                                         </flow_mat>" +
    "                                                                 </response_label>" +
    "                                                                 <response_label ident = \"LID02_D\">" +
    "                                                                         <flow_mat>" +
    "                                                                                 <material>" +
    "                                                                                         <mattext>Loire</mattext>" +
    "                                                                                 </material>" +
    "                                                                         </flow_mat>" +
    "                                                                 </response_label>" +
    "                                                         </render_choice>" +
    "                                                 </response_lid>" +
    "                                         </flow>" +
    "                                 </presentation>" +
    "                         </item>" +
    "                         <item title = \"Rivers in Germany\" ident = \"I03\">" +
    "                                 <rubric view = \"Candidate\">" +
    "                                         <flow_mat>" +
    "                                                 <material>" +
    "                                                         <mattext>Choose all of the correct answers.</mattext>" +
    "                                                 </material>" +
    "                                         </flow_mat>" +
    "                                 </rubric>" +
    "                                 <presentation label = \"Resp003\">" +
    "                                         <flow>" +
    "                                                 <material>" +
    "                                                         <matimage imagtype = \"image/jpeg\" x0 = \"500\" y0 = \"500\" height = \"200\" uri = \"image02.jpg\"/>" +
    "                                                 </material>" +
    "                                                 <flow>" +
    "                                                         <material>" +
    "                                                                 <mattext>Which rivers are in Germany ?</mattext>" +
    "                                                         </material>" +
    "                                                         <response_lid ident = \"LID03\" rcardinality = \"Multiple\">" +
    "                                                                 <render_hotspot>" +
    "                                                                         <response_label ident = \"LID03_A\" rarea = \"Ellipse\">10,10,2,2</response_label>" +
    "                                                                         <response_label ident = \"LID03_B\" rarea = \"Ellipse\">15,15,2,2</response_label>" +
    "                                                                         <response_label ident = \"LID03_C\" rarea = \"Ellipse\">30,30,2,2</response_label>" +
    "                                                                         <response_label ident = \"LID03_D\" rarea = \"Ellipse\">60,60,2,2</response_label>" +
    "                                                                         <response_label ident = \"LID03_E\" rarea = \"Ellipse\">70,70,2,2</response_label>" +
    "                                                                 </render_hotspot>" +
    "                                                         </response_lid>" +
    "                                                 </flow>" +
    "                                         </flow>" +
    "                                 </presentation>" +
    "                         </item>" + "                 </section>" +
    "         </assessment>" + " </questestinterop>" + "";
}
