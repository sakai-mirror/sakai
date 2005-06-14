package org.sakaiproject.component.syllabus.data;

import java.util.Iterator;
import java.util.Set;

import org.sakaiproject.component.junit.spring.ApplicationContextBaseTest;
import org.sakaiproject.service.syllabus.data.SyllabusData;
import org.sakaiproject.service.syllabus.data.SyllabusItem;
import org.sakaiproject.service.syllabus.data.SyllabusManager;

/**
 * @author <a href="mailto:jlannan@iupui.edu">Jarrod Lannan </a>
 */
public class SyllabusItemTest extends ApplicationContextBaseTest
{
  private SyllabusManager syllabusManager;  

  /**
   *  
   */
  public SyllabusItemTest()
  {
    super();        
    this.syllabusManager = (SyllabusManager) getApplicationContext().getBean(
        SyllabusManager.class.getName());
  }

  /**
   * @param arg0
   */
  public SyllabusItemTest(String name)
  {
    super(name);        
    this.syllabusManager = (SyllabusManager) getApplicationContext().getBean(
        SyllabusManager.class.getName());
  }

  public void testSyllabusIntegration()
  {
    try
    {
      String userId = "1111";
      String contextId = "2222";
      String redirectURL = "http://www.cs.iupui.edu";
      
      SyllabusItem si = syllabusManager.createSyllabusItem(userId, contextId, redirectURL);
                  
      assertNotNull(si);
      assertEquals(userId, si.getUserId());
      assertEquals(contextId, si.getContextId());
      assertEquals(redirectURL, si.getRedirectURL());          

      // now find the item just persisted
      SyllabusItem item = syllabusManager.getSyllabusItemByContextId(contextId);
      assertNotNull(item);
      //assertEquals(userId, item.getUserId());
      assertEquals(contextId, item.getContextId());
      assertEquals(redirectURL, item.getRedirectURL());
      
      // create a new SyllabusDataItem and persist it
      
      SyllabusData sd1 = syllabusManager.createSyllabusDataObject(null, new Integer(1), null, null, null, null);
      SyllabusData sd2 = syllabusManager.createSyllabusDataObject(null, new Integer(2), null, null, null, null);
      SyllabusData sd3 = syllabusManager.createSyllabusDataObject(null, new Integer(3), null, null, null, null);
      
            
      syllabusManager.addSyllabusToSyllabusItem(item, sd1);      
      syllabusManager.addSyllabusToSyllabusItem(item, sd2);
      syllabusManager.addSyllabusToSyllabusItem(item, sd3);
      
      //Set syllabiCollection = syllabusManager.getSyllabiForSyllabusItem(item);
      assertEquals(new Integer(3), new Integer(syllabusManager.getSyllabiForSyllabusItem(item).size()));      
      
      syllabusManager.removeSyllabusFromSyllabusItem(item, sd2);
      assertEquals(new Integer(2), new Integer(syllabusManager.getSyllabiForSyllabusItem(item).size()));
      
      
      System.out.println("hereitis");
      syllabusManager.swapSyllabusDataPositions(item, sd1, sd3);
      
      for(Iterator i = syllabusManager.getSyllabiForSyllabusItem(item).iterator(); i.hasNext();){
        SyllabusData sd = (SyllabusData) i.next();
        System.out.println("sditem: " + sd);
      }
      
      // modify syllabusData
      System.out.println("Modifying a Syllabus");
      Set s = syllabusManager.getSyllabiForSyllabusItem(item);
      Iterator i = s.iterator();
      SyllabusData sd = (SyllabusData) i.next();
      
      sd.setTitle("Jarrod's Title");
      syllabusManager.saveSyllabus(sd);
      
      
      for(Iterator it = syllabusManager.getSyllabiForSyllabusItem(item).iterator(); it.hasNext();){
        SyllabusData sdata = (SyllabusData) it.next();
        System.out.println("sditem: " + sdata);
      }
      
      // find largest syllabus position
      System.out.println("largest position: " + syllabusManager.findLargestSyllabusPosition(item));
      
      
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      assertNull("No exception should be thrown", e);
    }

  }
  
}