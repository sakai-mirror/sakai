/***********************************************************************************
 *
 * $Header: $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/

package org.sakaiproject.api.common.authorization;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is critical for the interaction with AuthorizationManager.
 * This class will be used for creating Authorizations and querying 
 * Authorizations. The implementation of this class is not thread safe.
 * 
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id$
 */
public class PermissionsMask implements Map
{
  private static final Log LOG = LogFactory.getLog(PermissionsMask.class);

  private Map map;

  /**
   * @see HashMap#HashMap()
   */
  public PermissionsMask()
  {
    map = new HashMap();
  }

  /**
   * @see HashMap#HashMap(int)
   */
  public PermissionsMask(int initialCapacity)
  {
    map = new HashMap(initialCapacity);
  }

  /**
   * @see HashMap#HashMap(int, float)
   */
  public PermissionsMask(int initialCapacity, float loadFactor)
  {
    map = new HashMap(initialCapacity, loadFactor);
  }

  /**
   * @see java.util.Map#clear()
   */
  public void clear()
  {
    map.clear();
  }

  /**
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean containsKey(Object key)
  {
    return map.containsKey(key);
  }

  /**
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  public boolean containsValue(Object value)
  {
    return map.containsValue(value);
  }

  /**
   * @see java.util.Map#entrySet()
   */
  public Set entrySet()
  {
    return map.entrySet();
  }

  /**
   * @see java.util.Map#equals(java.lang.Object)
   */
  public boolean equals(Object o)
  {
    return map.equals(o);
  }

  /**
   * @see java.util.Map#get(java.lang.Object)
   */
  public Object get(Object key)
  {
    return map.get(key);
  }

  /**
   * @see java.util.Map#hashCode()
   */
  public int hashCode()
  {
    return map.hashCode();
  }

  /**
   * @see java.util.Map#isEmpty()
   */
  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  /**
   * @see java.util.Map#keySet()
   */
  public Set keySet()
  {
    return map.keySet();
  }

  /**
   * @param key Must be of type String.
   * @param value Must be of type Boolean or null.
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public Object put(Object key, Object value)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("put(Object " + key + ", Object " + value + ")");
    }
    if (key == null || !(key instanceof String))
      throw new IllegalArgumentException("Illegal key argument passed!");
    if (value != null && !(value instanceof Boolean))
      throw new IllegalArgumentException("Illegal value argument passed!");

    return map.put(key, value);
  }

  /**
   * @throws IllegalArgumentException if the specified map is null or any of the
   * keys are not Strings.
   * @see java.util.Map#putAll(java.util.Map)
   */
  public void putAll(Map t)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("putAll(Map " + t + ")");
    }
    if (t == null)
      throw new IllegalArgumentException("Illegal map argument passed!");
    for (Iterator iter = t.entrySet().iterator(); iter.hasNext();)
    {
      Map.Entry entry = (Entry) iter.next();
      if (!(entry.getKey() instanceof String))
        throw new IllegalArgumentException(
            "Illegal key found in Map, must be a String!");
      if (entry.getValue() != null && !(entry.getValue() instanceof Boolean))
        throw new IllegalArgumentException(
            "Illegal value found in Map; must be a Boolean or null!");
    }

    map.putAll(t);
  }

  /**
   * @see java.util.Map#remove(java.lang.Object)
   */
  public Object remove(Object key)
  {
    return map.remove(key);
  }

  /**
   * @see java.util.Map#size()
   */
  public int size()
  {
    return map.size();
  }

  /**
   * @see java.util.Map#values()
   */
  public Collection values()
  {
    return map.values();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return map.toString();
  }

}
