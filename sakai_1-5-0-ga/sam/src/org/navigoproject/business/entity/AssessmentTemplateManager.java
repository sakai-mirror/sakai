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

import org.navigoproject.business.entity.properties.AssessmentTemplateProperties;

import osid.assessment.AssessmentManager;

import osid.shared.Type;

/**
   All implementors of OsidManager provide create, delete, and get methods for the various objects defined in the package.  Most managers also include methods for returning Types.  Create method implementations should both instantiate and persist objects.  The manager should always be used in place of the new operator.  The new operator makes the implementating package explicit and requires a source code change in order to use a different package. In combination with OsidLoader, applications developed using managers permit implementation substitution without source code changes.  <p>Licensed under the {@link osid.SidLicense MIT O.K.I SID Definition License}.<p>@version $Revision: 1.1.1.1 $ / $Date: 2004/07/28 21:32:06 $
 */
public interface AssessmentTemplateManager
  extends AssessmentManager
{
  /**
     Create a new Assessment Template and add it to the Assessment Template Bank.
     @param String name
     @param String description
     @param osid.shared.Type AssessmentType
     @param properties
     @return Item with its name, description, and Unique Id set.
     @throws AssessmentException if there is a general failure or if the Type is unknown
   */
  AssessmentTemplate createAssessmentTemplate(
    String name, String description, Type AssessmentType,
    AssessmentTemplateProperties properties)
    throws osid.OsidException;

  /**
   *
   * @param name
   * @param description
   * @param AssessmentType
   * @return
   * @throws osid.OsidException
   */
  AssessmentTemplate createAssessmentTemplate(
    String name, String description, Type AssessmentType)
    throws osid.OsidException;

  /**
     Delete an Assessment from the Assessment Bank.
     @param osid.shared.Id
     @throws AssessmentException if there is a general failure or if the object has not been created
   */
  void deleteAssessmentTemplate(osid.shared.Id AssessmentId)
    throws osid.OsidException;

  /**
     Get all the Assessments of a specific Type.  Iterators return a set, one at a time.  The Iterator's hasNext method returns true if there are additional objects available; false otherwise.  The Iterator's next method returns the next object.
     @param osid.shared.Type AssessmentType
     @return AssessmentIterator  The order of the objects returned by the Iterator is not guaranteed.
     @throws AssessmentException if there is a general failure or if the Type is unknown
   */
  AssessmentTemplateIterator getAssessmentTemplates(
    osid.shared.Type assessmentType)
    throws osid.OsidException;

  /**
     Get all the Assessments in the Assessment Bank.  Iterators return a set, one at a time.  The Iterator's hasNext method returns true if there are additional objects available; false otherwise.  The Iterator's next method returns the next object.
     @return AssessmentIterator  The order of the objects returned by the Iterator is not guaranteed.
     @throws AssessmentException if there is a general failure
   */
  AssessmentTemplateIterator getAssessmentTemplates()
    throws osid.OsidException;
}
