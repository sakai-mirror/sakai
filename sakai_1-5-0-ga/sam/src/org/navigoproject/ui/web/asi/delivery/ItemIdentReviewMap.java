/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.navigoproject.business.exception.FormatException;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ItemIdentReviewMap implements Map
{
    private static final org.apache.log4j.Logger LOG =
      org.apache.log4j.Logger.getLogger(ItemResponseIdentMap.class);
    public static final String ITEM_IDENT_REF = "item_ident_ref";
    public static final String MARK_FOR_REVIEW = "markForReview";

    // i.e.:  "item_result_ident_ref=1234abc&response_ident_ref=56789def&response_value=a"
    private static final Pattern PATTERN =
      Pattern.compile(
        "^(?:" + ITEM_IDENT_REF + "=(.+?))?(?:&" + MARK_FOR_REVIEW +
        "=(.+?))?" + "$");
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
    public static ItemIdentReviewMap parseItemIdentReviewMap(String str)
      throws FormatException
    {
      if(LOG.isDebugEnabled())
      {
        LOG.debug("parseItemResponseIdentMap(String " + str + ")");
      }

      return new ItemIdentReviewMap(str);
    }

    /**
     * Creates a new ItemResponseIdentMap object.
     *
     * @param str DOCUMENTATION PENDING
     *
     * @throws FormatException DOCUMENTATION PENDING
     */
    public ItemIdentReviewMap(String str)
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
        map.put(ITEM_IDENT_REF, matcher.group(1));
        map.put(MARK_FOR_REVIEW, matcher.group(2));
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
    public String getItemIdentRef()
    {
      return (String) map.get(ITEM_IDENT_REF);
    }

    /**
     * DOCUMENTATION PENDING
     *
     * @return DOCUMENTATION PENDING
     */
    public String getMarkForReview()
    {
      return (String) map.get(MARK_FOR_REVIEW);
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
      String str = "item_ident_ref=12345abc&markForReview=1";
      String str2 =
        "item_ident_ref=12345abc&markForReview=67890def";
      ItemIdentReviewMap irimap =
        ItemIdentReviewMap.parseItemIdentReviewMap(str);
      LOG.debug(irimap.getItemIdentRef());
      LOG.debug(irimap.getMarkForReview());

      ItemIdentReviewMap irimap2 =
        ItemIdentReviewMap.parseItemIdentReviewMap(str2);
      LOG.debug(irimap2.getItemIdentRef());
      LOG.debug(irimap2.getMarkForReview());
    }
}
