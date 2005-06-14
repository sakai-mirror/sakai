// temporary development scaffold for factory
// remove later.
package org.sakaiproject.tool.assessment.business.entity.helper.item;

import org.sakaiproject.tool.assessment.business.entity.asi.Item;
import java.io.InputStream;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;

public class StubItemHelper implements ItemHelperIfc
{
  public Item readXMLDocument(InputStream inputStream)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method readXMLDocument() not yet implemented.");
  }
  public String getTemplateFromType(TypeIfc type)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method getTemplateFromType() not yet implemented.");
  }
  public String getItemTypeString(TypeIfc type)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method getItemTypeString() not yet implemented.");
  }
  public void addResponseEntry(Item itemXml, String xpath, String value, boolean isInsert, String responseNo, String responseLabel)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method addResponseEntry() not yet implemented.");
  }
  public Item updateItemXml(Item itemXml, String xpath, String value)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method updateItemXml() not yet implemented.");
  }
}