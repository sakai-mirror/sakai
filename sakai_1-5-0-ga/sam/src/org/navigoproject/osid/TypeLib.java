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

package org.navigoproject.osid;

import org.navigoproject.osid.shared.impl.TypeImpl;

/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: TypeLib.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class TypeLib
{
  // AuthN Types
  public static final osid.shared.Type AUTHN_WEB_BASIC =
    new TypeImpl("HTTP", "AUTHN", "BASIC", "Basic Web Server Authentication");
  public static final osid.shared.Type AUTHN_ANONYMOUS =
    new TypeImpl(
      "ROOT", "AUTHN", "ANONYMOUS", "Basic Anonymous Authentication");
  public static final osid.shared.Type AUTHN_AUTHENTICATED_USERS =
     new TypeImpl("ROOT", "AUTHN", "AUTHENTICATED_USERS", "Any Authenticated User");

  // Agent Types
  public static final osid.shared.Type AGENT_HTTP_BASIC_PERSON =
     new TypeImpl("IU.EDU", "AGENT", "HTTP_BASIC", "HTTP Basic Authentication Person");
   
  
  public static final osid.shared.Type AGENT_PERSON =
    new TypeImpl("IU.EDU", "AGENT", "PERSON", "Person");
  public static final osid.shared.Type AGENT_MACHINE =
    new TypeImpl("IU.EDU", "AGENT", "MACHINE", "Machine");
  public static final osid.shared.Type AGENT_ANONYMOUS =
    new TypeImpl("IU.EDU", "AGENT", "ANONYMOUS", "Anonymous");
  public static final osid.shared.Type AGENT_AUTHENTICATED_USERS =
      new TypeImpl("IU.EDU", "AGENT", "AUTHENTICATED_USERS", "Authenticated Users");


// Filter Types

  public static final osid.shared.Type FILTER_COURSE_AUTHOR =
          new TypeImpl("IU.EDU", "FILTER", "COURSE_AUTHOR", "Author of a course");
      public static final String FILTER_COURSE_AUTHOR_ID = "course_author_filter_pk";
      
  public static final osid.shared.Type FILTER_ALL =
           new TypeImpl("IU.EDU", "FILTER", "ALL", "All assessments");
   
  public static final osid.shared.Type FILTER_ACTIVE =
           new TypeImpl("IU.EDU", "FILTER", "ACTIVE", "Active Assessments");
     
  public static final osid.shared.Type FILTER_EXPIRED =
           new TypeImpl("IU.EDU", "FILTER", "EXPIRED", "Expired assessments");
           
  public static final osid.shared.Type FILTER_COMPLETED =
              new TypeImpl("IU.EDU", "FILTER", "COMPLETED", "Completed assessments");
     

//Enrollment Status Types
  
  public static final osid.shared.Type STUDENT_STATUS = 
      new TypeImpl("IU.EDU" ,"STATUS", "STUDENT_STATUS", "Student Status");
  public static final osid.shared.Type AUTHOR_STATUS = 
       new TypeImpl("IU.EDU" ,"STATUS", "AUTHOR_STATUS", "Author Status");

  // Function Types
  public static final osid.shared.Type FUNCTION_ACL_HTTP_REQUEST =
    new TypeImpl(
      "HTTP", "AUTHZ_FUNCTION", "ACL_HTTP_REQUEST",
      "Access Control List for HTTP requests");

  // Qualifier Types
  public static final osid.shared.Type QUALIFIER_HTTP_REQUEST =
    new TypeImpl(
      "HTTP", "AUTHZ_QUALIFIER", "HTTP_REQUEST", "HTTP request Qualifier");
  public static final osid.shared.Type QUALIFIER_REALIZED_ASSESSMENT =
    new TypeImpl(
      "ASSESSMENT", "AUTHZ_QUALIFIER", "REALIZED_ASSESSMENT",
      "Realized Assessment");
  public static final osid.shared.Type QUALIFIER_PUBLISHED_ASSESSMENT =
     new TypeImpl(
       "ASSESSMENT", "AUTHZ_QUALIFIER", "PUBLISHED_ASSESSMENT",
       "Published Assessment");

  // Search Types
  public static final osid.shared.Type SEARCH_USERNAME =
    new TypeImpl("IDENTITY", "SEARCH", "USERNAME", "Username Search");
  public static final osid.shared.Type SEARCH_UNIQUE_ID =
    new TypeImpl("IDENTITY", "SEARCH", "UNIQUE_ID", "Unique Id Search");
	public static final osid.shared.Type SEARCH_CORE_ASSETS =
		new TypeImpl("CORE", "SEARCH", "CORE_IDS", "CORE TYPE Search");    

  // Digital Repository Types
  public static final osid.shared.Type DR_STRING_XML_TEST =
    new TypeImpl(
      "Test", "DR", "String",
      "TEST for testing, short term storage that is periodically purged");
  public static final osid.shared.Type DR_QTI_ASSESSMENT =
    new TypeImpl(
      "QTI", "DR", "Assessment", "String containing QTI Assessment XML");
  public static final osid.shared.Type DR_QTI_SECTION =
    new TypeImpl("QTI", "DR", "Section", "String containing QTI Section XML");
  public static final osid.shared.Type DR_QTI_ITEM =
    new TypeImpl("QTI", "DR", "Item", "String containing QTI Item XML");
  public static final osid.shared.Type DR_QTI_ASSESSMENT_TAKEN =
    new TypeImpl(
      "QTI", "DR", "AssessmentTaken",
      "String containing QTI AssessmentTaken XML");
  public static final osid.shared.Type DR_QTI_ASSESSMENT_PUBLISHED =
    new TypeImpl(
      "QTI", "DR", "AssessmentPublished",
      "String containing QTI AssessmentPublished XML");
  public static final osid.shared.Type DR_QTI_RESULTS =
    new TypeImpl("QTI", "DR", "Results", "String containing QTI Results XML");
  public static final osid.shared.Type DR_QTI_SETTINGS =
    new TypeImpl("QTI", "DR", "Settings", "String containing QTI Settings XML");

  // Test Types
  public static final osid.shared.Type ERROR =
    new TypeImpl("IU.EDU", "TEST", "ERROR", "Used for test cases");
  public static final osid.shared.Type TEST =
    new TypeImpl("STANFORD.EDU", "TEST", "TEST", "Used for testing");
  public static final osid.shared.Type TEST2 =
    new TypeImpl("IU.EDU", "TEST", "TEST2", "A 2nd type for testing");
  public static final osid.shared.Type TEST3 =
    new TypeImpl("IU.EDU", "TEST", "TEST3", "A 3rd type for testing");

  /**
   * Should only contain constants; do not expose any public constructors
   */
  private TypeLib()
  {
    throw new java.lang.UnsupportedOperationException(
      "Object should never be instantiated");
  }
}
