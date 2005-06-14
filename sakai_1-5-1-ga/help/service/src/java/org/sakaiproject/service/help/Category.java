/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.service.help;

import java.util.List;

/**
 * @author casong
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Category
{
  public String getName();
  
  public void setName(String name);
  
  public List getResources();
  
  public void setResources(List resources);
;
  public List getCategories();
  
  public void setCategories(List categories);
}
