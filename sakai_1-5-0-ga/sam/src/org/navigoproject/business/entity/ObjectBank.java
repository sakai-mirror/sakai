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

/*
 * Created on Oct 17, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity;

import org.navigoproject.QTIConstantStrings;
import org.navigoproject.data.RepositoryManager;

import java.util.ArrayList;

import org.w3c.dom.Document;

import osid.dr.DigitalRepositoryException;

import osid.shared.Id;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ObjectBank
  extends Section
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(Section.class);
  public final String basePath = QTIConstantStrings.OBJECTBANK;

  /**
   * Explicitly setting serialVersionUID insures future versions can be
   * successfully restored. It is essential this variable name not be changed
   * to SERIALVERSIONUID, as the default serialization methods expects this
   * exact name.
   */
  private static final long serialVersionUID = 1;
  private String bankId;

  /**
   * Creates a new ObjectBank object.
   */
  public ObjectBank()
  {
    super();
  }

  /**
   * Creates a new Section object.
   *
   * @param document DOCUMENTATION PENDING
   */
  public ObjectBank(Document document)
  {
    super(document);
  }

  /**
   * Creates a new Section object.
   *
   * @param xml DOCUMENTATION PENDING
   */
  public ObjectBank(String xml)
  {
    super(xml);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param bankId DOCUMENTATION PENDING
   */
  public void setBankId(String bankId)
  {
    this.bankId = bankId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getAllItems()
  {
    if(bankId == null)
    {
      //Throw an exception
    }

    ArrayList list = new ArrayList();
    RepositoryManager rm = new RepositoryManager();
    Id id;
    try
    {
      id = rm.getId(bankId);
      Object bankContent = rm.getAsset(id).getContent();
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param number DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getNumberofItems(int number)
  {
    if(bankId == null)
    {
      throw new IllegalStateException("bankId == null");
    }

    RepositoryManager rm = new RepositoryManager();
    Id id;
    try
    {
      id = rm.getId(bankId);
      Object bankContent = rm.getAsset(id).getContent();
    }
    catch(DigitalRepositoryException e)
    {
      LOG.error(e); throw new Error(e);
    }

    return null;
  }
}
