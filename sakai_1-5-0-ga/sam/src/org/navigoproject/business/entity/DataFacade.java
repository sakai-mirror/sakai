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

import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.log4j.Logger;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organization: Stanford University</p>
 * <p>This class implements common methods for accessing an object as keyed
 * values in a Map.  It also looks for any Serializable data property and
 * exposes its properties as well.  It is designed as a facade for OSID
 * interface implementations where the main class properties are kept in
 * a separate property extension property called data.  The Facade pattern
 * is docuemtned in GOF 185.   This allows one to use a flat and extensible API
 * insulated from internal OSID changes changes.
 * </p>
 * <p>
 * Usage (hypothetical example):
 * <code><br><br>
 * DataFacade myClassFacade =
 *     new DataFacade(myClassInstance, myClassInstance.getData());<br>
 * String name =
 *     myClassFacade.get("fancyName");// myClassInstance.getFancyName()<br>
 * String dataStuff =
 *     myClassFacade.get("stuff");// myClassInstance.getData().getStuff()<br>
 * myClassFacade.set("fancyName",
 *     "Even fancier: " + name);// myClassInstance.setFancyName(...<br>
 * </code>
 * @author Ed Smiley
 * @version 1.0
 */
public class DataFacade
  implements Serializable
{
  public static Logger logger = Logger.getLogger(DataFacade.class.getName());

  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 3138083504059630988L;
  private Map beanInfo;
  private Map beanDataInfo;
  private Serializable bean;
  private Serializable beanData;

  /**
   * Creates a new DataFacade object.
   *
   * @param ser DOCUMENTATION PENDING
   * @param serData DOCUMENTATION PENDING
   */
  public DataFacade(Serializable ser, Serializable serData)
  {
    bean = ser;
    beanData = serData;

    // default to empty maps
    beanInfo = new HashMap();
    beanDataInfo = new HashMap();

    // now attempt to populate
    try
    {
      beanInfo = info(ser);
      beanDataInfo = info(serData);

      //      beanDataInfo = info( (Serializable) beanInfo.get("data") );
    }
    catch(Exception ex)
    {
      logger.error("DataFacade: " + ser.getClass().getName() + ": " + ex);
    }
  }

  /**
   * Look up a property.
   * @param propName the String key for the property
   * @return
   */
  public synchronized Object get(String propName)
  {
    if(beanInfo.containsKey(propName))
    {
      return beanInfo.get(propName);
    }
    else if(beanDataInfo.containsKey(propName))
    {
      return beanDataInfo.get(propName);
    }

    return null;
  }

  /**
   * Get object maintained by DataFacade.
   * @return up to date version of object internal to facade
   */
  public synchronized Serializable getBean()
  {
    return bean;
  }

  /**
   * Put a new value in a property
   * @param propName the String key for the property
   * @param propValue the property Object
   */
  public synchronized void put(String propName, Object propValue)
  {
    try
    {
      if(beanInfo.containsKey(propName))
      {
        // if this property is in the main class
        // this is a no brainer
        beanInfo.put(propName, propValue);
        BeanUtils.populate(bean, beanInfo);
      }
      else if(beanDataInfo.containsKey(propName))
      {
        // if this is in the data member, it is
        // a little more complicated.
        //
        // put the property into the Map that
        // represents the data member,
        beanDataInfo.put(propName, propValue);

        // then get the data member from the main class
        Object o = beanInfo.get("data");

        // and populate it from the Map that represents the data
        // member,
        BeanUtils.populate(o, beanDataInfo);

        // put it into the Map that
        // represents the main class and then
        beanInfo.put("data", o);

        // populate the main class from that
        // map.
        BeanUtils.populate(bean, beanInfo);
      }
    }
    catch(InvocationTargetException ex)
    {
      logger.error("DataFacade: " + bean.getClass().getName() + ": " + ex);
    }
    catch(IllegalAccessException ex)
    {
      logger.error("DataFacade: " + bean.getClass().getName() + ": " + ex);
    }
  }

  /**
   * Utility method to wrap apache BeanUtils
   * @param s Serializable object, which should be a bean
   * @return a Map where each String key corresponds to a bean property.
   */
  private Map info(Serializable s)
  {
    Map m;

    try
    {
      m = BeanUtils.describe(s);
    }
    catch(Throwable t)
    {
      throw new java.lang.UnsupportedOperationException(
        "Invalid. Object may not be Java Bean.  " + t);
    }

    return m;
  }
}
