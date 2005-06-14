/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test.org.navigoproject.business.entity;

import org.navigoproject.business.entity.Item;

import test.org.navigoproject.fixture.Log4jFixture;
import test.org.navigoproject.fixture.PathInfoFixture;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestItem
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(TestItem.class);
  private Log4jFixture log4jFixture;
  private PathInfoFixture pathInfoFixture;
  private Item item = null;
//  <item ident="33eee1228644126700ea95e051022e9b" title="True - False" displayIndex="A">
//   <itemmetadata>
//                          <qtimetadata>
//                              <qtimetadatafield>
//                                  <fieldlabel>qmd_itemtype</fieldlabel>
//                                  <fieldentry>True False</fieldentry>
//                              </qtimetadatafield>
//                              <qtimetadatafield>
//                                  <fieldlabel>TEXT_FORMAT</fieldlabel>
//                                  <fieldentry>PLAIN</fieldentry>
//                              </qtimetadatafield>
//                          </qtimetadata>
//                      </itemmetadata>
//                      <presentation label="Resp001">
//                          <flow class="Block">
//                              <material>
//                                  <mattext charset="ascii-us"
//                                      texttype="text/plain" xml:space="default">IU
//                                      was established in year 1920</mattext>
//                              </material>
//                              <material>
//                                  <matimage embedded="base64" imagtype="text/html" uri=""/>
//                              </material>
//                          </flow>
//                      </presentation>
//                  </item>

  public TestItem(String name)
  {
    super(name);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  protected void setUp()
    throws Exception
  {
    super.setUp();
    log4jFixture = new Log4jFixture(this);
    pathInfoFixture = new PathInfoFixture(this);

    log4jFixture.setUp();
    pathInfoFixture.setUp();

    /**
     * @todo remove this configurator:
     */
    org.apache.log4j.BasicConfigurator.configure();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  protected void tearDown()
    throws Exception
  {
    log4jFixture.tearDown();
    pathInfoFixture.tearDown();

    log4jFixture = null;
    pathInfoFixture = null;
    super.tearDown();
  }

  /* (non-Javadoc)
   * @see junit.framework.TestCase#run()
   */
  public TestResult run()
  {
    // TODO Auto-generated method stub
    return super.run();
  }
  
  private Item getItem(){
    if(item == null)
    {
    String itemString = "<item><itemmetadata><qtimetadata><qtimetadatafield>" +
                        "<fieldlabel>qmd_itemtype</fieldlabel><fieldentry>True False</fieldentry>" +
                         "</qtimetadatafield><qtimetadatafield><fieldlabel>TEXT_FORMAT</fieldlabel><fieldentry>PLAIN</fieldentry>" +
                         "</qtimetadatafield></qtimetadata></itemmetadata>" +
                        "<presentation><flow><material><mattext>" +
                        "IU was established in year 1920 </mattext></material></flow></presentation></item>";
    item = new Item(itemString); 
    }
    return this.item;                   
  }
  
  public void testGetItemType()
    throws Exception
    {
      Item item = this.getItem();
      String type = item.getItemType();
      assertEquals(type, "True False");
    }
    
    public void testGetItemText()
      throws Exception
    {
      Item item = this.getItem();
      String text = item.getItemText();
      assertEquals(text.trim(),"IU was established in year 1920");
    }
}
