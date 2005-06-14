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

package org.navigoproject.business.entity.assessment.model;

import org.navigoproject.business.entity.SectionTemplate;
import org.navigoproject.business.entity.properties.SectionProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import osid.assessment.Section;

import osid.shared.Id;
import osid.shared.Type;

/**
 * The Stanford-specific Section properties.
 *
 * @author Rachel Gollub
 */
public class SectionPropertiesImpl
  implements SectionProperties
{
  // Stanford data
  private String type;
  private Collection mediaCollection;
  private String keywords;
  private String objectives;
  private String rubrics;
  private String itemOrder;
  private Type okiType = null;
  private Section pSection = null; // this is the parent section -- if it's top it's null
  private Id pSectionId = null; // this is the parent section id -- if it's top it's null
  private Collection items;
  private Collection original_items;
  private String position;

  // Question Pool Data
  private String randomPoolName;
  private Id randomPoolId;
  private int randomNumber;

  // Template data
  private SectionTemplate template;

  /**
   * Creates a new SectionPropertiesImpl object.
   */
  public SectionPropertiesImpl()
  {
    mediaCollection = new ArrayList();
    items = new ArrayList();
    original_items = new ArrayList();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getType()
  {
    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptype DOCUMENTATION PENDING
   */
  public void setType(String ptype)
  {
    type = ptype;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getMediaCollection()
  {
    return mediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getReversedMediaCollection()
  {
    List l = new ArrayList(mediaCollection);
    Collections.reverse(l);

    return l;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getShowReorderLink()
  {
    Iterator iter = mediaCollection.iterator();
    int i = 0;
    while(iter.hasNext())
    {
      MediaData data = (MediaData) iter.next();
      if(! data.getIsLink())
      {
        i++;
      }
    }

    if(i > 1)
    {
      return true;
    }

    return false;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getShowRelatedReorderLink()
  {
    Iterator iter = mediaCollection.iterator();
    int i = 0;
    while(iter.hasNext())
    {
      MediaData data = (MediaData) iter.next();
      if(data.getIsLink())
      {
        i++;
      }
    }

    if(i > 1)
    {
      return true;
    }

    return false;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getShowMedia()
  {
    Iterator iter = mediaCollection.iterator();
    int i = 0;
    while(iter.hasNext())
    {
      MediaData data = (MediaData) iter.next();
      if(! data.getIsLink())
      {
        i++;
      }
    }

    if(i > 0)
    {
      return true;
    }

    return false;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getShowRelatedMedia()
  {
    Iterator iter = mediaCollection.iterator();
    int i = 0;
    while(iter.hasNext())
    {
      MediaData data = (MediaData) iter.next();
      if(data.getIsLink())
      {
        i++;
      }
    }

    if(i > 0)
    {
      return true;
    }

    return false;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmediaCollection DOCUMENTATION PENDING
   */
  public void setMediaCollection(Collection pmediaCollection)
  {
    mediaCollection = pmediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmedia DOCUMENTATION PENDING
   */
  public void addMedia(Object pmedia)
  {
    mediaCollection.add(pmedia);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmedia DOCUMENTATION PENDING
   */
  public void removeMedia(Object pmedia)
  {
    mediaCollection.remove(pmedia);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getObjectives()
  {
    return objectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pobjectives DOCUMENTATION PENDING
   */
  public void setObjectives(String pobjectives)
  {
    objectives = pobjectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getKeywords()
  {
    return keywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pkeywords DOCUMENTATION PENDING
   */
  public void setKeywords(String pkeywords)
  {
    keywords = pkeywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRubrics()
  {
    return rubrics;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param prubrics DOCUMENTATION PENDING
   */
  public void setRubrics(String prubrics)
  {
    rubrics = prubrics;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getItemOrder()
  {
    return itemOrder;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitemOrder DOCUMENTATION PENDING
   */
  public void setItemOrder(String pitemOrder)
  {
    itemOrder = pitemOrder;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public SectionTemplate getSectionTemplate()
  {
    return template;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptemplate DOCUMENTATION PENDING
   */
  public void setSectionTemplate(SectionTemplate ptemplate)
  {
    template = ptemplate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRandomPoolName()
  {
    return randomPoolName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newName DOCUMENTATION PENDING
   */
  public void setRandomPoolName(String newName)
  {
    randomPoolName = newName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Id getRandomPoolId()
  {
    return randomPoolId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newId DOCUMENTATION PENDING
   */
  public void setRandomPoolId(Id newId)
  {
    randomPoolId = newId;
  }

  /**
   * Number of questions to randomly pull from question pool.
   *
   * @return DOCUMENTATION PENDING
   */
  public int getRandomNumber()
  {
    return randomNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newNumber DOCUMENTATION PENDING
   */
  public void setRandomNumber(int newNumber)
  {
    randomNumber = newNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getHasRelatedMedia()
  {
    Iterator iter = mediaCollection.iterator();
    while(iter.hasNext())
    {
      if(((MediaData) iter.next()).getIsLink())
      {
        return true;
      }
    }

    return false;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getSectionType()
  {
    return okiType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param psectionType DOCUMENTATION PENDING
   */
  public void setSectionType(Type psectionType)
  {
    okiType = psectionType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Section getParentSection()
  {
    return pSection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param parentSection DOCUMENTATION PENDING
   */
  public void setParentSection(Section parentSection)
  {
    pSection = parentSection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Id getParentSectionId()
  {
    return pSectionId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param parentSectionId DOCUMENTATION PENDING
   */
  public void setParentSectionId(Id parentSectionId)
  {
    pSectionId = parentSectionId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getItemList()
  {
    return items;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitems DOCUMENTATION PENDING
   */
  public void setItemList(Collection pitems)
  {
    items = pitems;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getOriginal_ItemList()
  {
    return original_items;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitems DOCUMENTATION PENDING
   */
  public void setOriginal_ItemList(Collection pitems)
  {
    original_items = pitems;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getPosition()
  {
    return position;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newPosition DOCUMENTATION PENDING
   */
  public void setPosition(String newPosition)
  {
    position = newPosition;
  }
}
