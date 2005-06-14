/*
 * Created on Mar 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ItemResponseMap
{
  private HashMap map = new HashMap();
  private List keyList = new ArrayList();
  
  public void put(String key, String name)
  {
    ArrayList list = this.get(key);
    if(list == null)
    {
      list = new ArrayList();
      this.map.put(key, list);
      keyList.add(key);
    }
    list.add(name);
  }
  
  public ArrayList get(String key)
  {
    ArrayList list = null;
    Object object = map.get(key);
    if(object != null){
      list = (ArrayList)object; 
    }
    return list;
  }
  
  public int size()
  {
    return this.keyList.size();
  }
  
  public ArrayList get(int i)
  {
    String key = (String)this.keyList.get(i);
    return this.get(key);
  }
  
  public String getKey(int i)
  {
    return (String)this.keyList.get(i);
  }
}

