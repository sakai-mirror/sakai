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

package org.navigoproject.ui.web.form.edit;

import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.navigoproject.business.entity.assessment.model.ItemPropertiesImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import osid.assessment.Item;

/**
 * This form takes a list and allows the user to reorder it.  It is used for
 * media and questions right now.
 *
 * @author <a href="mailto:rgollub@stanford.edu">Rachel Gollub</a>
 */
public class QuestionReorderForm
  extends ActionForm
{
  private String formid;
  private Collection list;
  private Collection order;
  private static final Logger LOG = Logger.getLogger(ReorderForm.class.getName());

  /**
   * Creates a new QuestionReorderForm object.
   */
  public QuestionReorderForm()
  {
    super();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionErrors validate(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    ActionErrors errors = new ActionErrors();

    return errors;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void reset(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    // Not used -- no checkboxes on reorder page.
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFormId()
  {
    return formid;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newId DOCUMENTATION PENDING
   */
  public void setFormId(String newId)
  {
    if(newId != null)
    {
      formid = newId;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getInitialList()
  {
    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newList DOCUMENTATION PENDING
   */
  public void setInitialList(Collection newList)
  {
    list = newList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object[] getListAsArray()
  {
    return list.toArray();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param obj DOCUMENTATION PENDING
   */
  public void setListAsArray(Object[] obj)
  {
    // Not used
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getList()
  {
    try
    {
      ArrayList alist = (ArrayList) list;
      int i = 1;
      while(i <= alist.size())
      {
        /* This is what it *should* do.  It is not, however, what
         * coursework does.
                             for (int j=0; j<alist.size(); j++)
                             {
                               if (((ItemImpl) alist.get(j)).getPosition()
                                 .equals(new Integer(i).toString()))
                               {
                                 orderedList.add(alist.get(j));
                                 logger.debug("Adding(" + i + ") " +
                                 ((ItemImpl) alist.get(j)).getName());
                               }
                             }
                             i++;
         */
        ItemPropertiesImpl iprops =
          (ItemPropertiesImpl) ((Item) alist.get(i - 1)).getData();
        if(! iprops.getPosition().equals(new Integer(i).toString()))
        {
          ItemImpl tmp = (ItemImpl) alist.get(i - 1);
          ItemPropertiesImpl tmpprops = (ItemPropertiesImpl) tmp.getData();
          ItemImpl tmp2 =
            (ItemImpl) alist.get(Integer.parseInt(tmpprops.getPosition()) - 1);
          ItemPropertiesImpl tmp2props = (ItemPropertiesImpl) tmp2.getData();
          alist.remove(tmp);
          tmpprops.setPosition(tmp2props.getPosition());
          alist.remove(tmp2);
          tmp2props.setPosition(new Integer(i).toString());
          if(Integer.parseInt(tmpprops.getPosition()) < i)
          {
            alist.add(Integer.parseInt(tmpprops.getPosition()) - 1, tmp);
            alist.add(i - 1, tmp2);
          }
          else
          {
            alist.add(i - 1, tmp2);
            alist.add(Integer.parseInt(tmpprops.getPosition()) - 1, tmp);
          }

          break;
        }

        i++;
      }

      //return orderedList;
      list = alist;

      return alist;
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getReversedList()
  {
    List l = new ArrayList(getList());
    Collections.reverse(l);

    return l;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newList DOCUMENTATION PENDING
   */
  public void setList(Collection newList)
  {
    try
    {
      if(newList != null)
      {
        list = newList;
      }

      order = new ArrayList();
      for(int i = 0; i < list.size(); i++)
      {
        ItemPropertiesImpl iprops =
          (ItemPropertiesImpl) ((ItemImpl) ((ArrayList) list).get(i)).getData();
        iprops.setPosition(new Integer(i + 1).toString());
        order.add(new Integer(i + 1).toString());
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getOrder()
  {
    return order;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newOrder DOCUMENTATION PENDING
   */
  public void setOrder(Collection newOrder)
  {
    order = newOrder;
  }
}