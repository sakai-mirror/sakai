package org.sakaiproject.tool.assessment.facade;

import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemFeedback;
import org.sakaiproject.tool.assessment.data.dao.assessment.ItemMetaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.Answer;
import org.sakaiproject.tool.assessment.data.dao.assessment.AnswerFeedback;
import org.sakaiproject.tool.assessment.data.dao.shared.TypeD;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.facade.DataFacadeException;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueries;
import org.sakaiproject.tool.assessment.facade.ItemFacade;
import org.osid.assessment.AssessmentException;
//import org.sakaiproject.tool.assessment.service.PersistenceService;
import org.navigoproject.osid.impl.PersistenceService;


import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import net.sf.hibernate.Hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.sakaiproject.tool.assessment.util.HibernateUtil;
import org.osid.assessment.AssessmentException;
import java.io.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


public class ItemFacadeQueries extends HibernateDaoSupport {
  private static Logger LOG =
    Logger.getLogger(QuestionPoolFacadeQueries.class.getName());

  public ItemFacadeQueries() {
  }

  public IdImpl getItemId(String id){
    return new IdImpl(id);
  }
  public IdImpl getItemId(Long id){
    return new IdImpl(id);
  }
  public IdImpl getItemId(long id){
    return new IdImpl(id);
  }


  public static void main(String[] args) throws DataFacadeException {
    ItemFacadeQueries instance = new ItemFacadeQueries();
    // add an item
    if (args[0].equals("add")) {
      Long itemId = instance.add();
      System.out.println("**Item #" + itemId);
      instance.show(itemId);
    }
    if (args[0].equals("f_add")) {
      Long itemId = new Long(-1);
      itemId = instance.facadeAdd();
      System.out.println("**Item #" + itemId);
      instance.ifcShow(itemId);
    }
    if (args[0].equals("show")) {
      instance.show(new Long(args[1]));
    }
    if (args[0].equals("showtype")) {
      instance.showType(new Long(args[1]));
    }
    if (args[0].equals("remove")) {
      instance.remove(new Long(args[1]));
    }
    if (args[0].equals("listtype")) {
      instance.listType();
    }
    if (args[0].equals("list")) {
      instance.list();
      List items = instance.list();
      for (int i = 0; i < items.size(); i++) {
        ItemData item = (ItemData) items.get(i);
        System.out.println("Item #" + item.getItemId() + " has rationale= " +
                           item.getHasRationale());
      }
    }
    if (args[0].equals("getQPItems")) {
      List items = instance.getQPItems(new Long(args[1])); // poolId
      for (int i = 0; i < items.size(); i++) {
        ItemData item = (ItemData) items.get(i);
        System.out.println("Item #" + item.getItemId() + " has rationale= " +
                           item.getHasRationale());
      }
    }
    System.exit(0);
  }

  public Long add() {
    ItemData item = new ItemData();
    item.setInstruction("Matching game");
    item.setTypeId(TypeFacade.MATCHING);
    item.setScore(new Float(10));
    item.setHasRationale(new Boolean("false"));
    item.setStatus(new Integer(1));
    item.setCreatedBy("1");
    item.setCreatedDate(new Date());
    item.setLastModifiedBy("1");
    item.setLastModifiedDate(new Date());

    // prepare itemText
    item.setItemTextSet(prepareText(item));

    // prepare MetaData
    item.setItemMetaDataSet(prepareMetaData(item));
    item.addItemMetaData("ITEM_OBJECTIVE", "the objective is to ...");

    // prepare feedback
    item.setCorrectItemFeedback("well done!");
    item.setInCorrectItemFeedback("better luck next time!");

    getHibernateTemplate().save(item);
    return item.getItemId();
  }

  public List getQPItems(Long questionPoolId) {
    return getHibernateTemplate().find("select ab from ItemData ab, QuestionPoolItem qpi where qpi.itemId=ab.itemIdString and qpi.questionPoolId = ?",new Object[] { questionPoolId }, new net.sf.hibernate.type.Type[] { Hibernate.LONG });
  }

  public List list() {
    return getHibernateTemplate().find("from ItemData");
  }

  public void show(Long itemId) {
    getHibernateTemplate().load(ItemData.class, itemId);
  }

  public ItemFacade getItem(Long itemId, String agent) {
	ItemFacade item = new ItemFacade((ItemData)getHibernateTemplate().load(ItemData.class, itemId));
	return item;
  }

  public void showType(Long typeId) {
    getHibernateTemplate().load(TypeD.class, typeId);
  }

  public void listType() {
    TypeFacadeQueries typeFacadeQueries = PersistenceService.getInstance().getTypeFacadeQueries();
    TypeFacade f = typeFacadeQueries.getTypeFacadeById(new Long(1));
    System.out.println("***facade: "+f.getAuthority());
  }

// DELETEME
  public void remove(Long itemId) {
      ItemData item = (ItemData)getHibernateTemplate().load(ItemData.class, itemId);
      if (item != null) {
        printItem(item);
      }
      getHibernateTemplate().delete(item);
      if (item != null) {
        printItem(item);
      }
  }

  public void deleteItem(Long itemId, String agent) {
      ItemData item = (ItemData)getHibernateTemplate().load(ItemData.class, itemId);
      if (item != null) {
        printItem(item);
      }
      getHibernateTemplate().delete(item);
      if (item != null) {
        printItem(item);
      }
  }



  public void deleteItemContent(Long itemId, String agent) {
 System.out.println("deleteing itemtext set for an item " + itemId);
      ItemData item = (ItemData)getHibernateTemplate().load(ItemData.class, itemId);
      if (item != null) {
        printItem(item);
      }
      getHibernateTemplate().deleteAll(item.getItemTextSet());
      getHibernateTemplate().deleteAll(item.getItemMetaDataSet());
      getHibernateTemplate().deleteAll(item.getItemFeedbackSet());
      if (item != null) {
        printItem(item);
      }
  }

  public void deleteItemMetaData(Long itemId, String label) {
    String query = "from ItemMetaData imd where imd.item.itemId=? and imd.label= ?";
    List itemmetadatalist = getHibernateTemplate().find(query,
        new Object[] { itemId, label },
        new net.sf.hibernate.type.Type[] { Hibernate.LONG , Hibernate.STRING });
    getHibernateTemplate().deleteAll(itemmetadatalist);
  }


  public void addItemMetaData(Long itemId, String label, String value) {
    System.out.println("lydiatest adding metadata " + itemId + " " + label );
    ItemData item = (ItemData)getHibernateTemplate().load(ItemData.class, itemId);
      if (item != null) {
        printItem(item);

    ItemMetaData itemmetadata = new ItemMetaData(item, label, value);
    getHibernateTemplate().save(itemmetadata);
    //item.addItemMetaData(label, value);
    //getHibernateTemplate().saveOrUpdate(item);
      }
  }

  private HashSet prepareText(ItemData item) {
    HashSet textSet = new HashSet();
    ItemText text1 = new ItemText();
    text1.setItem(item);
    text1.setSequence(new Long(1));
    text1.setText("cat has");
    HashSet answerSet1 = new HashSet();
    HashSet answerFeedbackSet1 = new HashSet();
    Answer answer1 = new Answer(text1, "2 legs", new Long(1), "i",
                                new Boolean("false"), null, new Float(0));
    answerFeedbackSet1.add(new AnswerFeedback(answer1, "incorrect", "sorry"));
    answer1.setAnswerFeedbackSet(answerFeedbackSet1);
    answerSet1.add(answer1);
    answerSet1.add(new Answer(text1, "3 legs", new Long(2), "ii",
                              new Boolean("false"), null, new Float(0)));
    answerSet1.add(new Answer(text1, "4 legs", new Long(3), "iii",
                              new Boolean("true"), null, new Float(5)));
    text1.setAnswerSet(answerSet1);

    textSet.add(text1);

    ItemText text2 = new ItemText();
    text2.setItem(item);
    text2.setSequence(new Long(2));
    text2.setText("chicken has");
    HashSet answerSet2 = new HashSet();
    answerSet2.add(new Answer(text2, "2 legs", new Long(1), "i",
                              new Boolean("true"), null, new Float(5)));
    answerSet2.add(new Answer(text2, "3 legs", new Long(2), "ii",
                              new Boolean("false"), null, new Float(0)));
    answerSet2.add(new Answer(text2, "4 legs", new Long(3), "iii",
                              new Boolean("false"), null, new Float(0)));
    text2.setAnswerSet(answerSet2);
    textSet.add(text2);

    ItemText text3 = new ItemText();
    text3.setItem(item);
    text3.setSequence(new Long(3));
    text3.setText("baby has");
    HashSet answerSet3 = new HashSet();
    answerSet3.add(new Answer(text3, "2 legs", new Long(1), "i",
                              new Boolean("false"), null, new Float(0)));
    answerSet3.add(new Answer(text3, "3 legs", new Long(2), "ii",
                              new Boolean("false"), null, new Float(0)));
    answerSet3.add(new Answer(text3, "4 legs", new Long(3), "iii",
                              new Boolean("true"), null, new Float(5)));
    text3.setAnswerSet(answerSet3);
    textSet.add(text3);
    return textSet;
  }

  private HashSet prepareMetaData(ItemData item) {
    HashSet set = new HashSet();
    set.add(new ItemMetaData(item, "qmd_itemtype", "Matching"));
    set.add(new ItemMetaData(item, "TEXT_FORMAT", "HTML"));
    set.add(new ItemMetaData(item, "MUTUALLY_EXCLUSIVE", "True"));
    return set;
  }

  private void printItem(ItemData item) {
    System.out.println("**Id = " + item.getItemId());
    System.out.println("**score = " + item.getScore());
    System.out.println("**grade = " + item.getGrade());
    System.out.println("**CorrectFeedback is lazy = " +
                       item.getCorrectItemFeedback());
    System.out.println("**Objective not lazy = " +
                       item.getItemMetaDataByLabel("ITEM_OBJECTIVE"));
  }

  public Long facadeAdd() throws DataFacadeException {
    ItemFacade item = new ItemFacade();
      item.setInstruction("Matching game");
      item.setTypeId(new Long(9));
      item.setScore(new Float(10));
      item.setHasRationale(new Boolean("false"));
      item.setStatus(new Integer(1));
      item.setCreatedBy("1");
      item.setCreatedDate(new Date());
      item.setLastModifiedBy("1");
      item.setLastModifiedDate(new Date());

      // prepare itemText
      item.setItemTextSet(prepareText((ItemData)item.getData()));
      item.addItemText("I have",new HashSet());

      // prepare MetaData
      item.setItemMetaDataSet(prepareMetaData((ItemData)item.getData()));
      item.addItemMetaData("ITEM_OBJECTIVE", "the objective is to ...");

      // prepare feedback
      item.setCorrectItemFeedback("well done!");
      item.setInCorrectItemFeedback("better luck next time!");

      getHibernateTemplate().save(item.getData());
    return item.getData().getItemId();
  }

  public void ifcShow(Long itemId) {
      ItemDataIfc itemData = (ItemDataIfc) getHibernateTemplate().load(ItemData.class, itemId);
      if (itemData != null) {
        printIfcItem(itemData);
        printFacadeItem(itemData);
        //exportXml(itemData);
      }
  }


 public ItemFacade saveItem(ItemFacade item) throws DataFacadeException {
    System.out.println("lydiatest in saveItem " + item.getScore());
    boolean insert = false;
    try{
      ItemData itemdata = (ItemData) item.getData();
      itemdata.setLastModifiedDate(new Date());
      itemdata.setLastModifiedBy(AgentFacade.getAgentString());
      getHibernateTemplate().saveOrUpdate(itemdata);

      System.out.println("lydiatest DONE saveItem " + item.getScore());
      return item;
    }
    catch(Exception e){
	e.printStackTrace();
	return null;
    }
 }



  private void printIfcItem(ItemDataIfc item) {
    System.out.println("**Id = " + item.getItemId());
    System.out.println("**score = " + item.getScore());
    System.out.println("**grade = " + item.getGrade());
    System.out.println("**CorrectFeedback is lazy = " +
                       item.getCorrectItemFeedback());
    System.out.println("**Objective not lazy = " +
                       item.getItemMetaDataByLabel("ITEM_OBJECTIVE"));
    System.out.println("**createdDate = " +
                       item.getCreatedDate());
  }

  private void printFacadeItem(ItemDataIfc item) {
    ItemFacade f = new ItemFacade(item);
    System.out.println("****Id = " + f.getItemId());
    System.out.println("****score = " + f.getScore());
    System.out.println("****grade = " + f.getGrade());
    System.out.println("****CorrectFeedback is lazy = " +
                       f.getCorrectItemFeedback());
    System.out.println("****Objective not lazy = " +
                       f.getItemMetaDataByLabel("ITEM_OBJECTIVE"));
    System.out.println("****createdDate = " +
                       f.getCreatedDate());
    System.out.println("****ItemType = " +
                       f.getItemType().getKeyword());
  }

    /**
  private void exportXml(ItemDataIfc item) {
    XStream xstream = new XStream();
    xstream = new XStream(new DomDriver());
    xstream.alias("item", ItemData.class);
    xstream.alias("itemText", ItemText.class);
    xstream.alias("itemFeedback", ItemFeedback.class);
    xstream.alias("itemMetaData", ItemMetaData.class);
    xstream.alias("answer", Answer.class);
    xstream.alias("answerFeedback", AnswerFeedback.class);
    String xml = xstream.toXML(item);
    byte[] b = xml.getBytes();
    try {
      FileOutputStream out = new FileOutputStream("out");
      out.write(b);
    }
    catch (FileNotFoundException ex) {
    }
    catch (IOException ex1) {
    }
  }
    */

  public ItemFacade getItem(Long itemId) {
    ItemData item = (ItemData) getHibernateTemplate().load(ItemData.class, itemId);
    return new ItemFacade(item);
  }

}
