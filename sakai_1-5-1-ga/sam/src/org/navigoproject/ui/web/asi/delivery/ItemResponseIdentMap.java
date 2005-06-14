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

package org.navigoproject.ui.web.asi.delivery;

import org.navigoproject.business.exception.FormatException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;

/**
 * @author Lance Speelmon
 * @version $Id: ItemResponseIdentMap.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class ItemResponseIdentMap
  implements Map
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemResponseIdentMap.class);
  public static final String ITEM_RESULT_IDENT_REF = "item_result_ident_ref";
  public static final String RESPONSE_IDENT_REF = "response_ident_ref";
  public static final String RESPONSE_VALUE = "response_value";

  // i.e.:  "item_result_ident_ref=1234abc&response_ident_ref=56789def&response_value=a"
  private static final Pattern PATTERN =
    Pattern.compile(
      "^(?:" + ITEM_RESULT_IDENT_REF + "=(.+?))?(?:&" + RESPONSE_IDENT_REF +
      "=(.+?))?" + "(?:&" + RESPONSE_VALUE + "=(.+?))?" + "$");
  private Map map;

  /**
   * DOCUMENTATION PENDING
   *
   * @param str DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws FormatException DOCUMENTATION PENDING
   */
  public static ItemResponseIdentMap parseItemResponseIdentMap(String str)
    throws FormatException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("parseItemResponseIdentMap(String " + str + ")");
    }

    return new ItemResponseIdentMap(str);
  }

  /**
   * Creates a new ItemResponseIdentMap object.
   *
   * @param str DOCUMENTATION PENDING
   *
   * @throws FormatException DOCUMENTATION PENDING
   */
  public ItemResponseIdentMap(String str)
    throws FormatException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("new ItemResponseIdentMap(String " + str + ")");
    }

    Matcher matcher = PATTERN.matcher(str);
    if(matcher.matches())
    {
      if(LOG.isDebugEnabled())
      {
        for(int i = 1; i <= matcher.groupCount(); i++)
        {
          LOG.debug("matcher.group(" + i + ")=" + matcher.group(i));
        }
      }

      map = new HashMap(3);
      map.put(ITEM_RESULT_IDENT_REF, matcher.group(1));
      map.put(RESPONSE_IDENT_REF, matcher.group(2));
      map.put(RESPONSE_VALUE, matcher.group(3));
    }
    else
    {
      throw new FormatException("Invalid format: " + str);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getItemResultIdentRef()
  {
    return (String) map.get(ITEM_RESULT_IDENT_REF);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getResponseIdentRef()
  {
    return (String) map.get(RESPONSE_IDENT_REF);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getResponseValue()
  {
    return (String) map.get(RESPONSE_VALUE);
  }

  /* (non-Javadoc)
   * @see java.util.Map#size()
   */
  public int size()
  {
    return map.size();
  }

  /* (non-Javadoc)
   * @see java.util.Map#clear()
   */
  public void clear()
  {
    map.clear();
  }

  /* (non-Javadoc)
   * @see java.util.Map#isEmpty()
   */
  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  /* (non-Javadoc)
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean containsKey(Object key)
  {
    return map.containsKey(key);
  }

  /* (non-Javadoc)
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  public boolean containsValue(Object value)
  {
    return map.containsValue(value);
  }

  /* (non-Javadoc)
   * @see java.util.Map#values()
   */
  public Collection values()
  {
    return map.values();
  }

  /* (non-Javadoc)
   * @see java.util.Map#putAll(java.util.Map)
   */
  public void putAll(Map t)
  {
    map.putAll(t);
  }

  /* (non-Javadoc)
   * @see java.util.Map#entrySet()
   */
  public Set entrySet()
  {
    return map.entrySet();
  }

  /* (non-Javadoc)
   * @see java.util.Map#keySet()
   */
  public Set keySet()
  {
    return map.keySet();
  }

  /* (non-Javadoc)
   * @see java.util.Map#get(java.lang.Object)
   */
  public Object get(Object key)
  {
    return map.get(key);
  }

  /* (non-Javadoc)
   * @see java.util.Map#remove(java.lang.Object)
   */
  public Object remove(Object key)
  {
    return map.remove(key);
  }

  /* (non-Javadoc)
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public Object put(Object key, Object value)
  {
    return map.put(key, value);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    return map.equals(obj);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return map.hashCode();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return map.toString();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param args DOCUMENTATION PENDING
   */
  public static void main(String[] args)
  {
    BasicConfigurator.configure();
    String str = "item_result_ident_ref=12345abc&response_ident_ref=67890def";
    String str2 =
      "item_result_ident_ref=12345abc&response_ident_ref=67890def&response_value=A";
    ItemResponseIdentMap irimap =
      ItemResponseIdentMap.parseItemResponseIdentMap(str);
    LOG.debug(irimap.getItemResultIdentRef());
    LOG.debug(irimap.getResponseIdentRef());
    LOG.debug(irimap.getResponseValue());

    ItemResponseIdentMap irimap2 =
      ItemResponseIdentMap.parseItemResponseIdentMap(str2);
    LOG.debug(irimap2.getItemResultIdentRef());
    LOG.debug(irimap2.getResponseIdentRef());
    LOG.debug(irimap2.getResponseValue());
  }
}
