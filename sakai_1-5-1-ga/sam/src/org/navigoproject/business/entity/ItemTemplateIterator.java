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


/**
   ItemTemplateIterator returns a set, one at a time.  The purpose of all Iterators is to to offer a way for SID methods to return multiple values of a common type and not use an array.  Returning an array may not be appropriate if the number of values returned is large or is fetched remotely.  Iterators do not allow access to values by index, rather you must access values in sequence. Similarly, there is no way to go backwards through the sequence unless you place the values in a data structure, such as an array, that allows for access by index. <p>Licensed under the {@link osid.SidLicense MIT O.K.I SID Definition License}.<p>@version $Revision: 1.1.1.1 $ / $Date: 2004/07/28 21:32:06 $
 */
public interface ItemTemplateIterator
{
  /**
     Return true if there are additional  Item Templates ; false otherwise.
     @return boolean
     @throws AssessmentException if there is a general failure
   */
  boolean hasNext()
    throws osid.assessment.AssessmentException;

  /**
     Return the next Item Template.
     @return ItemTemplate
     @throws AssessmentException if there is a general failure or if all objects have already been returned.
   */
  ItemTemplate next()
    throws osid.assessment.AssessmentException;

  /**
     <p>    MIT O.K.I&#46; SID Definition License.
     </p>  <p>        <b>Copyright and license statement:</b>
     </p>  <p>        Copyright &copy; 2003 Massachusetts Institute of
      Technology &lt;or copyright holder&gt;
     </p>  <p>        This work is being provided by the copyright holder(s)
      subject to the terms of the O.K.I&#46; SID Definition
      License. By obtaining, using and/or copying this Work,
      you agree that you have read, understand, and will comply
      with the O.K.I&#46; SID Definition License.
     </p>  <p>        THE WORK IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
      KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
      THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
      PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
      MASSACHUSETTS INSTITUTE OF TECHNOLOGY, THE AUTHORS, OR
      COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
      OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
      OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
      THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
     </p>  <p>        <b>O.K.I&#46; SID Definition License</b>
     </p>  <p>        This work (the &ldquo;Work&rdquo;), including any
      software, documents, or other items related to O.K.I&#46;
      SID definitions, is being provided by the copyright
      holder(s) subject to the terms of the O.K.I&#46; SID
      Definition License. By obtaining, using and/or copying
      this Work, you agree that you have read, understand, and
      will comply with the following terms and conditions of
      the O.K.I&#46; SID Definition License:
     </p>  <p>        You may use, copy, and distribute unmodified versions of
      this Work for any purpose, without fee or royalty,
      provided that you include the following on ALL copies of
      the Work that you make or distribute:
     </p>  <ul>        <li>          The full text of the O.K.I&#46; SID Definition License
        in a location viewable to users of the redistributed
        Work.
      </li>  </ul>  <ul>        <li>          Any pre-existing intellectual property disclaimers,
        notices, or terms and conditions. If none exist, a
        short notice similar to the following should be used
        within the body of any redistributed Work:
        &ldquo;Copyright &copy; 2003 Massachusetts Institute of
        Technology. All Rights Reserved.&rdquo;
       </li>  </ul>  <p>        You may modify or create Derivatives of this Work only
      for your internal purposes. You shall not distribute or
      transfer any such Derivative of this Work to any location
      or any other third party. For purposes of this license,
      &ldquo;Derivative&rdquo; shall mean any derivative of the
      Work as defined in the United States Copyright Act of
      1976, such as a translation or modification.
     </p>  <p>        THE WORK PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
      EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
      WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
      PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
      MASSACHUSETTS INSTITUTE OF TECHNOLOGY, THE AUTHORS, OR
      COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
      OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
      OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
      THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
     </p>  <p>        The name and trademarks of copyright holder(s) and/or
      O.K.I&#46; may NOT be used in advertising or publicity
      pertaining to the Work without specific, written prior
      permission. Title to copyright in the Work and any
      associated documentation will at all times remain with
      the copyright holders.
     </p>  <p>        The export of software employing encryption technology
      may require a specific license from the United States
      Government. It is the responsibility of any person or
      organization contemplating export to obtain such a
      license before exporting this Work.
     </p>
   */
}
