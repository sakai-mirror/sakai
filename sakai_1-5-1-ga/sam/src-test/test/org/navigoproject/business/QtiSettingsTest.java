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

package test.org.navigoproject.business;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.xerces.dom.TextImpl;
import org.navigoproject.business.entity.QtiIpAddressData;
import org.navigoproject.business.entity.QtiIpAddressDataIterator;
import org.navigoproject.business.entity.QtiSettingsData;
import org.navigoproject.business.entity.QtiSettingsDataIterator;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.QtiSettingsBean;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import osid.OsidException;
import osid.OsidOwner;
import osid.assessment.Assessment;
import osid.assessment.AssessmentPublished;
import osid.shared.Id;
import osid.shared.SharedManager;
import test.org.navigoproject.fixture.BootStrapFixture;
import test.org.navigoproject.osid.MockHttpServletRequest;

/**
 * DOCUMENT ME!
 *
 * @author chmaurer
 * @version $Id: QtiSettingsTest.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class QtiSettingsTest
  extends TestCase
{
  private final static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(QtiSettingsTest.class);
  private org.navigoproject.business.entity.Assessment assessXml = null;
  private XmlStringBuffer xmlBuffer = null;
  BootStrapFixture bsf = null;
  AuthoringHelper comFunction = null;
  private static final Class CLAZZ = QtiSettingsBean.class;

  /**
   * Creates a new XMLSettingsTest object.
   *
   * @param name
   */
  public QtiSettingsTest(String name)
  {
    super(name);
  }

  /**
   * DOCUMENT ME!
   *
   * @throws Exception
   */
  protected void setUp()
    throws Exception
  {
    super.setUp();
    bsf = new BootStrapFixture();
    comFunction = new AuthoringHelper();

    //xmlBuffer = comFunction.readFile("c:\\asi.xml");
    xmlBuffer =
      comFunction.readFile(
        "C:\\Documents and Settings\\jlannan\\Desktop\\foo.xml");
  }

  /**
   * DOCUMENT ME!
   *
   * @throws Exception
   */
  protected void tearDown()
    throws Exception
  {
    comFunction = null;
    bsf = null;
    xmlBuffer = null;
    super.tearDown();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @throws OsidException DOCUMENTATION PENDING
   */
  public void testCreateAssessmentPublished()
    throws OsidException
  {
    Assessment assessment = null;
    AssessmentPublished ap = null;
    Id id = null;

    MockHttpServletRequest request = new MockHttpServletRequest(null);

    OsidOwner myOwner = OsidManagerFactory.getOsidOwner();
    SharedManager sm = OsidManagerFactory.createSharedManager(myOwner);

    //id = sm.getId("ef098c2d8644123400f04dae605f8060");
    id = sm.getId("ef076c588644123401f9f0f2516f2047");

    AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();

    assessment = assessmentManager.getAssessment(id);

    ap = assessmentManager.createAssessmentPublished(assessment);
  }

  /**
   * DOCUMENT ME!
   *
   * @throws Exception
   */
  public void ftestStoreSettings()
    throws Exception
  {
    Integer maxAttempts = null;
    String autoSubmit = "";
    String testDisabled = "";
    Timestamp startDate = null;
    Timestamp endDate = null;
    PersistenceBroker broker = null;

    String id = "fb00a2088644127300dc91e27743b5f3";
    assessXml =
      new org.navigoproject.business.entity.Assessment(xmlBuffer.getDocument());
    maxAttempts = Integer.valueOf(assessXml.getFieldentry("MAX_ATTEMPTS"));
    autoSubmit = assessXml.getFieldentry("AUTO_SUBMIT");
    testDisabled = assessXml.getFieldentry("TEST_DISABLED");
    startDate =
      Timestamp.valueOf(
        assessXml.getFieldentry("START_DATE").replaceFirst("T", " "));
    endDate =
      Timestamp.valueOf(
        assessXml.getFieldentry("END_DATE").replaceFirst("T", " "));

    QtiSettingsData setting =
      new QtiSettingsData(
        id, maxAttempts, autoSubmit, testDisabled, startDate, endDate, null, null);

    try
    {
      broker = PersistenceBrokerFactory.defaultPersistenceBroker();
      broker.beginTransaction();
      setting.store();
      broker.commitTransaction();

      setIpAddressData(id, xmlBuffer, broker);

      isIpAuthorized(id, "134.068.018.131");
    }
    catch(Exception ex)
    {
      LOG.error(ex + ": " + ex.getMessage());
      throw new Exception(ex);
    }
    finally
    {
      if(broker != null)
      {
        broker.close();
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   * @param xmlBuffer DOCUMENTATION PENDING
   * @param broker DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  private void setIpAddressData(
    String id, XmlStringBuffer xmlBuffer, PersistenceBroker broker)
    throws Exception
  {
    deleteIpAddressData(id, broker);

    getIpDataFromXml(id, xmlBuffer, broker, "range", "start", "stop");
    getIpDataFromXml(id, xmlBuffer, broker, "address", "ip", "ip");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   * @param xmlBuffer DOCUMENTATION PENDING
   * @param broker DOCUMENTATION PENDING
   * @param tag DOCUMENTATION PENDING
   * @param attribute DOCUMENTATION PENDING
   * @param attribute2 DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  private void getIpDataFromXml(
    String id, XmlStringBuffer xmlBuffer, PersistenceBroker broker, String tag,
    String attribute, String attribute2)
    throws Exception
  {
    NodeList nodes = xmlBuffer.getDocument().getElementsByTagName(tag);
    Element element = (Element) nodes.item(0);
    int size = nodes.getLength();
    for(int i = 0; i < size; i++)
    {
      Element node = (Element) nodes.item(i);

      QtiIpAddressData ipData =
        new QtiIpAddressData(
          id, node.getAttribute(attribute), node.getAttribute(attribute2));

      storeIpAddressData(ipData, broker);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ipData DOCUMENTATION PENDING
   * @param broker DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  private void storeIpAddressData(
    QtiIpAddressData ipData, PersistenceBroker broker)
    throws Exception
  {
    try
    {
      if(broker == null)
      {
        broker = PersistenceBrokerFactory.defaultPersistenceBroker();
      }

      broker.beginTransaction();
      ipData.store(broker);
      broker.commitTransaction();
    }
    catch(Exception ex)
    {
      LOG.error(ex + ": " + ex.getMessage());
      throw new Exception(ex);
    }

    //    finally
    //    {
    //      if(broker != null)
    //      {
    //        broker.close();
    //      }
    //    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   * @param broker DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  private void deleteIpAddressData(String id, PersistenceBroker broker)
    throws Exception
  {
    QtiIpAddressData tempIpData = new QtiIpAddressData();
    QtiIpAddressDataIterator ii = null;

    //PersistenceBroker broker = null;
    tempIpData.setId(id);
    ii = findByExample(tempIpData);

    /* delete with transaction support */
    try
    {
      if(broker == null)
      {
        broker = PersistenceBrokerFactory.defaultPersistenceBroker();
      }

      broker.beginTransaction();
      while(ii.hasNext())
      {
        QtiIpAddressData delIpData = ii.next();
        delIpData.delete();
      }

      broker.commitTransaction();
    }
    catch(Exception ex)
    {
      LOG.error(ex); throw new Error(ex);       
    }

    //    finally // always remember to close open resources in the finally block
    //    {
    //      if(broker != null)
    //      {
    //        broker.close();
    //      }
    //    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   * @param ip DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private boolean isIpAuthorized(String id, String ip)
  {
    QtiIpAddressData ipData = new QtiIpAddressData();
    QtiIpAddressDataIterator ii = null;
    boolean retVal = false;

    ipData.setId(id);
    ii = findByExample(ipData);

    while(ii.hasNext())
    {
      QtiIpAddressData tempIpData = ii.next();
      int compareStart = ip.compareTo(tempIpData.getStartIp());
      int compareEnd = ip.compareTo(tempIpData.getEndIp());
      if((compareStart >= 0) && (compareEnd <= 0))
      {
        retVal = true;

        break;
      }
    }

    return retVal;
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void ftestRetrieveAllSettings()
  {
    LOG.debug("Retrieve All:");
    QtiSettingsDataIterator qtiI = findAllSettings();
    while(qtiI.hasNext())
    {
      QtiSettingsData qti = qtiI.next();
      LOG.debug(qti);
    }
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void ftestRetrieveSettingsByExample()
  {
    LOG.debug("Retrieve By Example:");
    QtiSettingsData example = new QtiSettingsData();
    example.setTestDisabled("True");

    QtiSettingsDataIterator qtiI = findByExample(example);
    while(qtiI.hasNext())
    {
      QtiSettingsData qti = qtiI.next();
      LOG.debug(qti);
    }
  }

  /**
   * DOCUMENTATION PENDING
   */
  public void ftestRetrieveSettingsByCriteria()
  {
    LOG.debug("Retrieve By Criteria:");
    Criteria criteria = new Criteria();
    criteria.addEqualTo("test_disabled", "True");
    QtiSettingsDataIterator qtiI = findByCriteria(CLAZZ, criteria);
    while(qtiI.hasNext())
    {
      QtiSettingsData qti = qtiI.next();
      LOG.debug(qti);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static QtiSettingsDataIterator findAllSettings()
  {
    return findByCriteria(CLAZZ, null);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param example DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws IllegalArgumentException DOCUMENTATION PENDING
   */
  public static QtiSettingsDataIterator findByExample(QtiSettingsData example)
  {
    Iterator i = null;
    PersistenceBroker persistenceBroker =
      PersistenceBrokerFactory.defaultPersistenceBroker();
    if(example == null)
    {
      throw new IllegalArgumentException("Illegal Object argument: " + example);
    }

    if(persistenceBroker == null)
    {
      throw new IllegalArgumentException(
        "Illegal PersistenceBroker argument: " + persistenceBroker);
    }

    try
    {
      Query query = new QueryByCriteria(example.getBean());
      i = persistenceBroker.getIteratorByQuery(query);
    }
    catch(PersistenceBrokerException ex)
    {
      LOG.warn(ex.getMessage(), ex);
      throw ex;
    }

    return new QtiSettingsDataIterator(i);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param example DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static QtiIpAddressDataIterator findByExample(
    QtiIpAddressData example)
  {
    Iterator i = null;
    PersistenceBroker persistenceBroker =
      PersistenceBrokerFactory.defaultPersistenceBroker();
    if(example == null)
    {
      throw new IllegalArgumentException("Illegal Object argument: " + example);
    }

    if(persistenceBroker == null)
    {
      throw new IllegalArgumentException(
        "Illegal PersistenceBroker argument: " + persistenceBroker);
    }

    try
    {
      Query query = new QueryByCriteria(example.getBean());
      i = persistenceBroker.getIteratorByQuery(query);
    }
    catch(PersistenceBrokerException ex)
    {
      LOG.warn(ex.getMessage(), ex);
      throw ex;
    }

    return new QtiIpAddressDataIterator(i);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param clazz DOCUMENTATION PENDING
   * @param criteria DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static QtiSettingsDataIterator findByCriteria(
    Class clazz, Criteria criteria)
  {
    Iterator i = null;
    PersistenceBroker persistenceBroker = null;
    try
    {
      Query query = new QueryByCriteria(clazz, criteria);
      persistenceBroker = PersistenceBrokerFactory.defaultPersistenceBroker();
      i = persistenceBroker.getIteratorByQuery(query);
    }
    catch(PersistenceBrokerException ex)
    {
      LOG.warn(ex.getMessage(), ex);
      throw ex;
    }

    return new QtiSettingsDataIterator(i);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param fileName DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */

  /*
     private XmlStringBuffer readFile(String fileName)
     {
       LOG.debug("readFile");
       LOG.debug("readFile :" + fileName);
       XmlStringBuffer xmlString = null;
       FileReader reader;
       String xmlExample;
       try
       {
         reader = new FileReader(fileName);
         StringWriter out = new StringWriter();
         int c;
         while((c = reader.read()) != -1)
         {
           out.write(c);
         }
         reader.close();
         xmlExample = (String) out.toString();
         xmlString = new XmlStringBuffer(xmlExample);
       }
       catch(FileNotFoundException e)
       {
         LOG.error(e); throw new Error(e);
       }
       catch(IOException e1)
       {
         LOG.error(e1); throw new Error(e1);       
       }
       return xmlString;
     }
   */

  /**
   * DOCUMENTATION PENDING
   *
   * @param xmlString DOCUMENTATION PENDING
   * @param fieldlabel DOCUMENTATION PENDING
   * @param setValue DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */

  /*
     private String getFieldentry(
       XmlStringBuffer xmlString, String fieldlabel, String setValue)
     {
       LOG.debug("getFieldentry");
       LOG.debug("getFieldentry" + setValue + "= value");
       String val = null;
       int no = 0;
       List metadataList;
       try
       {
         String xpath =
           "questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='" +
           fieldlabel + "']";
         LOG.debug("xpath :" + xpath);
         metadataList = xmlString.selectNodes(xpath);
         no = metadataList.size();
         LOG.debug("No of items in the  list: " + no);
         if(metadataList.size() > 0)
         {
           // LOG.debug(no);
           Document document = xmlString.getDocument();
           ElementImpl pfieldlabel = (ElementImpl) metadataList.get(0);
           Node testfieldentry = pfieldlabel.getNextSibling();
           Node fieldentry = pfieldlabel.getNextSibling().getNextSibling();
           CharacterDataImpl fieldentryText =
             (CharacterDataImpl) fieldentry.getFirstChild();
           Integer getTime = null;
           if(
             (fieldentryText != null) && (fieldentryText.getNodeValue() != null) &&
               (fieldentryText.getNodeValue().trim().length() > 0))
           {
             LOG.debug(
               "fieldentry Value value not null :" +
               fieldentryText.getNodeValue());
             val = fieldentryText.getNodeValue();
           }
           if(setValue != null)
           {
             if(fieldentryText == null)
             {
               CoreDocumentImpl newdocument = new CoreDocumentImpl();
               TextImpl newElementText =
                 new TextImpl(newdocument, fieldentry.getNodeName());
               newElementText.setNodeValue(setValue);
               TextImpl clonedText =
                 (TextImpl) fieldentry.getOwnerDocument().importNode(
                   newElementText, true);
               fieldentry.appendChild(clonedText);
               fieldentryText = (CharacterDataImpl) fieldentry.getFirstChild();
               LOG.debug(
                 "fieldentry Value , when fieldentryText is null:" +
                 fieldentryText.getNodeValue());
             }
             else
             {
               fieldentryText.setNodeValue(setValue);
             }
             LOG.debug(
               "fieldentry Value  final after set :" +
               fieldentryText.getNodeValue());
           }
         }
       }
       catch(DOMException ex)
       {
         LOG.error(ex); throw new Error(ex);       
       }
       catch(Exception ex)
       {
         LOG.error(ex); throw new Error(ex);       
       }
       return val;
     }
   */

  /**
   * DOCUMENTATION PENDING
   *
   * @param xpath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected String getIpEntry(XmlStringBuffer xmlString)
  {
    String xpath =
      "questestinterop/assessment/assessproc_extension/ip-restrictions";

    if(LOG.isDebugEnabled())
    {
      LOG.debug("getFieldentry(String " + xpath + ")");
    }

    String val = null;
    int no = 0;

    List metadataList;
    try
    {
      metadataList = xmlString.selectNodes(xpath);
      no = metadataList.size();
      LOG.debug("No of items in the  list: " + no);
      if(metadataList.size() > 0)
      {
        // LOG.debug(no);
        Document document = xmlString.getDocument();
        Element pfieldlabel = (Element) metadataList.get(0);
        Node fieldentry = pfieldlabel.getNextSibling().getNextSibling();
        TextImpl fieldentryText = (TextImpl) fieldentry.getFirstChild();

        Integer getTime = null;
        if(
          (fieldentryText != null) && (fieldentryText.getNodeValue() != null) &&
            (fieldentryText.getNodeValue().trim().length() > 0))
        {
          LOG.debug(
            "fieldentry Value value not null :" +
            fieldentryText.getNodeValue());
          val = fieldentryText.getNodeValue();
        }
      }
    }
    catch(DOMException ex)
    {
      LOG.debug("DOMException occured");
      LOG.error(ex); throw new Error(ex);       
    }

    catch(Exception ex)
    {
      LOG.debug("Exception occured");
      LOG.error(ex); throw new Error(ex);       
    }

    return val;
  }
}
