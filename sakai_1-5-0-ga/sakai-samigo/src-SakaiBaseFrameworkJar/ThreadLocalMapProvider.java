package org.sakaiproject.framework;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.service.framework.current.cover.CurrentService;

/**
 * Provides access to thread-scoped variables, delegating to the Sakai CurrentService.
 * This implementation should only be used in a Sakai-Samigo integrated installation.
 * @author <a href="mailto:jonandersen@umich.edu">Jon Andersen</a>
 * @version $Id: ThreadLocalMapProvider.java,v 1.1 2005/01/10 19:53:18 janderse.umich.edu Exp $
 */
public class ThreadLocalMapProvider
{
  private static final Log LOG = LogFactory
      .getLog(ThreadLocalMapProvider.class);

  /** A Map that delegates to the CurrentService for get() and put() (only supported operations). */
  private static Map CURRENT_SERVICE_MAPPER = new AbstractMap()
  {
        public Object get(Object key)
        { 
            return CurrentService.getInThread((String) key);
        }

        public Object put(Object key, Object value)
        {
            CurrentService.setInThread((String) key, value);
            return null;
        }

        public Set entrySet()
        {
            throw new UnsupportedOperationException(); 
        }
  };

  private ThreadLocalMapProvider()
  {
    throw new IllegalAccessError();
  }

  public static Map getMap()
  {
    LOG.debug("getMap()");

    return CURRENT_SERVICE_MAPPER;
  }

  public static void clearMap()
  {
    LOG.debug("clearMap()");
    CurrentService.clearInThread();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return getMap().toString();
  }

}