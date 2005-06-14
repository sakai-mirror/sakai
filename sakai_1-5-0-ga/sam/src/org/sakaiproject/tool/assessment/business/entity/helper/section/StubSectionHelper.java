// temporary development scaffold for factory
// remove later.
package org.sakaiproject.tool.assessment.business.entity.helper.section;

import org.sakaiproject.tool.assessment.business.entity.asi.Section;
import java.io.InputStream;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

public class StubSectionHelper implements SectionHelperIfc
{
  public Section readXMLDocument(InputStream inputStream)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.section.SectionHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method readXMLDocument() not yet implemented.");
  }
  public ArrayList getSectionItems(String sectionID, boolean itemRefs)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.section.SectionHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method getSectionItems() not yet implemented.");
  }
  public Section getSectionXml(String sec_str_id)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.section.SectionHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method getSectionXml() not yet implemented.");
  }
  public void sectionUpdateData(String sectionID, Section sectionXml)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.section.SectionHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method sectionUpdateData() not yet implemented.");
  }
  public Section updateSectionXml(Section sectionXml, String xpath, String value)
  {
    /**@todo Implement this org.sakaiproject.tool.assessment.business.entity.helper.section.SectionHelperIfc method*/
    throw new java.lang.UnsupportedOperationException("Method updateSectionXml() not yet implemented.");
  }
}