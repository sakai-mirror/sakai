/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/util/java/src/java/org/sakaiproject/util/java/StringUtil.java $
 * $Id: StringUtil.java 2076 2005-09-27 01:22:00Z ggolden@umich.edu $
 **********************************************************************************
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

package org.sakaiproject.util.java;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.preference.Preferences;
import org.sakaiproject.service.legacy.preference.cover.PreferencesService;

import java.text.MessageFormat;
import java.util.*;

/**
 * ResourceLoader provides an alternate implementation of org.util.ResourceBundle, dynamically selecting
 * the prefered locale from either the user's session or from the user's sakai preferences
 * 
 * @author Sugiura, Tatsuki (University of Nagoya)
 * @version $Revision: $
 */
public class ResourceLoader extends DummyMap implements Map 
{
    /** This string is used by the UserPrefsTool (imitating a service) */
    public static final String SERVICE_NAME = ResourceLoader.class.getName();
   
    /** Preferences key for user's regional language locale */
    public static final String LOCALE_KEY = "locale";
   
    protected static Log M_log = LogFactory.getLog(ResourceLoader.class);
    protected String baseName = null;
    protected Hashtable bundles = new Hashtable();

   /**
    ** Default constructor (does nothing)
    **/
    public ResourceLoader()
    {
        M_log.debug("init");
    }
    
   /**
    ** Constructor: set baseName
    ** 
    ** @param name default ResourceBundle base filename
    **/
    public ResourceLoader(String name) 
    {
        M_log.debug("init");
        setBaseName(name);
    }
    
   /**
    ** Set baseName
    ** 
    ** @param name default ResourceBundle base filename
    **/
    public void setBaseName(String name) 
    {
        M_log.debug("set baseName="+name);
        this.baseName = name; 
    }

   /**
    ** Add loc (key) and bundle (value) to this.bundles hash
    ** 
    ** @param loc Language/Region Locale
    ** @param bundle properties bundle
    **/
    protected void setBundle(Locale loc, ResourceBundle bundle) 
    {
        if (bundle == null)
            throw new NullPointerException();
        this.bundles.put(loc, bundle);
    }
    
   /**
    ** Return ResourceBundle for user's preferred locale
    ** 
    ** @return user's ResourceBundle object
    **/
    protected ResourceBundle getBundle() 
    {
        Locale loc = getLocale();
        ResourceBundle bundle = (ResourceBundle) this.bundles.get(loc);
        if (bundle == null) 
        {
            M_log.debug("Load bundle name=" + this.baseName
                    + ", locale=" + getLocale().toString());
            bundle = loadBundle(loc);
        }
        return bundle;
    }
    
   /**
    ** Return user's current session
    ** 
    ** @return user's Session object
    **/
    protected Session getSession()
    {
        return SessionManager.getCurrentSession();
    }
    
    
   /**
    ** Clear bundles hashmap
    **/
    public void purgeCache()
    {
        this.bundles = new Hashtable();
        M_log.debug("purge bundle cache");
    }

   /**
    ** Return user's prefered locale
    **    First: return locale from Sakai user preferences, if available
    **    Second: return locale from user session, if available
    **    Last: return system default locale
    **
    ** @return user's Locale object
    **/
    public Locale getLocale() 
    {
        M_log.debug("checking locale");
        Locale loc = null;

        //  First: find locale from Sakai user preferences, if available
        try
        {
           String userId = SessionManager.getCurrentSessionUserId();
    	     Preferences prefs = PreferencesService.getPreferences( userId );
           ResourceProperties locProps = prefs.getProperties( SERVICE_NAME );
      
           String localeString = locProps.getProperty( LOCALE_KEY );
           if ( localeString != null )
           {
              String[] locValues = localeString.split("_");
              if (locValues.length > 1)
                 loc = new Locale( locValues[0], locValues[1] ); // language, country
              else if (locValues.length == 1 )
                 loc = new Locale( locValues[0] ); // just language
           }
        }
        catch (Exception e) {} // ignore and continue 
        
        // Second: find locale from user session, if available
        if (loc == null) 
        {
           try 
           {
               loc = (Locale) getSession().getAttribute("locale");
               M_log.debug("get locale from session: " + loc.toString());
           } 
           catch(NullPointerException e) {} // ignore and continue
        }
        
        // Last: find system default locale
        if (loc == null) 
        {
            // fallback to default.
            loc = Locale.getDefault();
        } 
        else if (!Locale.getDefault().getLanguage().equals("en")
            && loc.getLanguage().equals("en")) 
        {
            // Tweak for English: en is default locale. It has no suffix in filename.
            loc = new Locale("");
            M_log.debug("tweak for en");
        }
        
        return loc;
    }
    
   /**
    ** Return ResourceBundle for specified locale
    **
    ** @param bundle properties bundle
    **
    ** @return locale specific ResourceBundle
    **/
    protected ResourceBundle loadBundle(Locale loc)
    {
        ResourceBundle newBundle = null;
        try 
        {
            newBundle = ResourceBundle.getBundle(this.baseName, loc);
        } 
        catch (NullPointerException e) {}  // ignore
        
        setBundle(loc, newBundle);
        return newBundle;
    }
    
   /**
    ** Return string value for specified property in current locale specific ResourceBundle
    **
    ** @param key property key to look up in current ResourceBundle
    **
    ** @return String value for specified property key
    **/
    public String getString(String key) 
    {
        try 
        {
            return getBundle().getString(key);
        } 
        catch (MissingResourceException e) 
        {
            M_log.warn("Missing key: " + key);
            return "[missing key: " + key + "]";
        }
    }

   /**
    ** Return (generic object) value for specified property in current locale specific ResourceBundle
    **
    ** @param key property key to look up in current ResourceBundle
    **
    ** @return value for specified property key
    **/
    public Object get(Object key) 
    {
        return getString(key.toString());
    }

   public Object getFormattedMessage(Object key, Object[]args) {
      String pattern = (String) get(key);
      return MessageFormat.format(pattern, args);
   }

   public Set keySet() {
      return getBundleAsMap().keySet();
   }

   public Collection values() {
      return getBundleAsMap().values();
   }

   public Set entrySet() {
      return getBundleAsMap().entrySet();
   }

   protected Map getBundleAsMap() {
      Map bundle = new Hashtable();

      for (Enumeration e = getBundle().getKeys();e.hasMoreElements();) {
         Object key = e.nextElement();
         bundle.put(key, getBundle().getObject((String) key));
      }

      return bundle;
   }

}

abstract class DummyMap implements Map 
{
    public abstract Object get(Object key);
    
    public boolean containsKey(Object key) 
    {
        return true;
    }

    public boolean containsValue(Object value) 
    {
        throw new UnsupportedOperationException();
    }

    public Set keySet() 
    {
        throw new UnsupportedOperationException();
    }

    public Collection values() 
    {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() 
    {
        throw new UnsupportedOperationException();
    }

    public Object put(Object arg0, Object arg1) 
    {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key) 
    {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map arg0) 
    {
        throw new UnsupportedOperationException();
    }

    public void clear() 
    {
        throw new UnsupportedOperationException();
    }

    public int size() 
    {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() 
    {
        throw new UnsupportedOperationException();
    }
}
