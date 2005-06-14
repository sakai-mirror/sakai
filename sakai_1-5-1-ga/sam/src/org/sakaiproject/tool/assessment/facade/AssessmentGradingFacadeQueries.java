package org.sakaiproject.tool.assessment.facade;

import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentBaseIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTextIfc;
import org.sakaiproject.tool.assessment.data.ifc.grading.AssessmentGradingIfc;
import org.sakaiproject.tool.assessment.data.ifc.grading.ItemGradingIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedSectionData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAnswer;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemText;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAssessmentData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingSummaryData;
import org.sakaiproject.tool.assessment.data.dao.grading.ItemGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.MediaData;

import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import net.sf.hibernate.Hibernate;
import org.navigoproject.osid.impl.PersistenceService;
import net.sf.hibernate.type.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.Float;

import org.apache.log4j.Logger;
import org.sakaiproject.tool.assessment.util.HibernateUtil;
import org.osid.assessment.AssessmentException;
import java.io.*;

public class AssessmentGradingFacadeQueries extends HibernateDaoSupport{
  private static Logger LOG =
    Logger.getLogger(AssessmentGradingFacadeQueries.class.getName());

  public AssessmentGradingFacadeQueries () {
  }

  public List getTotalScores(String publishedId, String which) {
    try {
      // sectionSet of publishedAssessment is defined as lazy loading in
      // Hibernate OR map, so we need to initialize them. Unfortunately our
      // spring-1.0.2.jar does not support HibernateTemplate.intialize(Object)
      // so we need to do it ourselves
      PublishedAssessmentData assessment =PersistenceService.getInstance().getPublishedAssessmentFacadeQueries().
        loadPublishedAssessment(new Long(publishedId));
      HashSet sectionSet = PersistenceService.getInstance().
          getPublishedAssessmentFacadeQueries().getSectionSetForAssessment(assessment);
      assessment.setSectionSet(sectionSet);
      // proceed to get totalScores
      Object[] objects = new Object[2];
      objects[0] = new Long(publishedId);
      objects[1] = new Boolean(true);
      Type[] types = new Type[2];
      types[0] = Hibernate.LONG;
      types[1] = Hibernate.BOOLEAN;
      List list = getHibernateTemplate().find("from AssessmentGradingData a where a.publishedAssessment.publishedAssessmentId=? and a.forGrade=? order by agentId ASC, submittedDate DESC", objects, types);
      if (which.equals("true"))
        return list;
      else {
        Iterator items = list.iterator();
        ArrayList newlist = new ArrayList();
        String agentid = null;
        AssessmentGradingData data = (AssessmentGradingData) items.next();
        // daisyf add the following line on 12/15/04
        data.setPublishedAssessment(assessment);
        agentid = data.getAgentId();
        newlist.add(data);
        while (items.hasNext()) {
          while (items.hasNext()) {
            data = (AssessmentGradingData) items.next();
            if (!data.getAgentId().equals(agentid)) {
              agentid = data.getAgentId();
              newlist.add(data);
              break;
            }
          }
        }
        return newlist;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList();
    }
  }

  public HashMap getItemScores(Long publishedId, Long itemId, String which)
  {
    try {
      ArrayList scores = (ArrayList)
        getTotalScores(publishedId.toString(), which);
      HashMap map = new HashMap();
      List list = new ArrayList();
      Iterator iter = scores.iterator();
      while (iter.hasNext())
      {
        AssessmentGradingData data = (AssessmentGradingData) iter.next();
        List temp = null;
        if (itemId.equals(new Long(0)))
          temp = getHibernateTemplate().find("from ItemGradingData a where a.assessmentGrading.assessmentGradingId=? order by agentId ASC, submittedDate DESC", data.getAssessmentGradingId(), Hibernate.LONG);
        else
        {
          Object[] objects = new Object[2];
          objects[0] = data.getAssessmentGradingId();
          objects[1] = itemId;
          Type[] types = new Type[2];
          types[0] = Hibernate.LONG;
          types[1] = Hibernate.LONG;
          temp = getHibernateTemplate().find("from ItemGradingData a where a.assessmentGrading.assessmentGradingId=? and a.publishedItem.itemId=? order by agentId ASC, submittedDate DESC", objects, types);

          // To avoid lazy loading, load them with the objects that have
          // the sections filled in already from total scores
          Iterator tmp = temp.iterator();
          while (tmp.hasNext())
          {
            ItemGradingData idata = (ItemGradingData) tmp.next();
            idata.setAssessmentGrading(data);
          }
        }
        list.addAll(temp);
      }
      iter = list.iterator();
      while (iter.hasNext())
      {
        ItemGradingData data = (ItemGradingData) iter.next();
        ArrayList thisone = (ArrayList)
          map.get(data.getPublishedItem().getItemId());
        if (thisone == null)
          thisone = new ArrayList();
        thisone.add(data);
        map.put(data.getPublishedItem().getItemId(), thisone);
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      return new HashMap();
    }
  }

  /**
   * This returns a hashmap of all the latest item entries, keyed by
   * item id for easy retrieval.
   */
  public HashMap getLastItemGradingData(Long publishedId, String agentId)
  {
    try {
      Object[] objects = new Object[2];
      objects[0] = publishedId;
      objects[1] = agentId;
      Type[] types = new Type[2];
      types[0] = Hibernate.LONG;
      types[1] = Hibernate.STRING;
      ArrayList scores = (ArrayList) getHibernateTemplate().find("from AssessmentGradingData a where a.publishedAssessment.publishedAssessmentId=? and a.agentId=? order by submittedDate DESC", objects, types);
      HashMap map = new HashMap();
      if (scores.isEmpty())
        return new HashMap();
      AssessmentGradingData gdata = (AssessmentGradingData) scores.toArray()[0];
      if (gdata.getForGrade().booleanValue())
        return new HashMap();
      Iterator iter = gdata.getItemGradingSet().iterator();
      while (iter.hasNext())
      {
        ItemGradingData data = (ItemGradingData) iter.next();
        ArrayList thisone = (ArrayList)
          map.get(data.getPublishedItem().getItemId());
        if (thisone == null)
          thisone = new ArrayList();
        thisone.add(data);
        map.put(data.getPublishedItem().getItemId(), thisone);
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      return new HashMap();
    }
  }

  /**
   * This returns a hashmap of all the submitted items, keyed by
   * item id for easy retrieval.
   */
  public HashMap getStudentGradingData(String assessmentGradingId)
  {
    try {
      HashMap map = new HashMap();
      ArrayList list = (ArrayList) getHibernateTemplate().find("from AssessmentGradingData a where a.assessmentGradingId=?", new Long(assessmentGradingId), Hibernate.LONG);
      AssessmentGradingData gdata = null;
      if (!list.isEmpty())
        gdata = (AssessmentGradingData) list.toArray()[0];
      Iterator iter = gdata.getItemGradingSet().iterator();
      while (iter.hasNext())
      {
        ItemGradingData data = (ItemGradingData) iter.next();
        ArrayList thisone = (ArrayList)
          map.get(data.getPublishedItem().getItemId());
        if (thisone == null)
          thisone = new ArrayList();
        thisone.add(data);
        map.put(data.getPublishedItem().getItemId(), thisone);
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      return new HashMap();
    }
  }

  public HashMap getSubmitData(Long publishedId, String agentId)
  {
    try {
      Object[] objects = new Object[3];
      objects[0] = publishedId;
      objects[1] = agentId;
      objects[2] = new Boolean(true);
      Type[] types = new Type[3];
      types[0] = Hibernate.LONG;
      types[1] = Hibernate.STRING;
      types[2] = Hibernate.BOOLEAN;
      ArrayList scores = (ArrayList) getHibernateTemplate().find("from AssessmentGradingData a where a.publishedAssessment.publishedAssessmentId=? and a.agentId=? and a.forGrade=? order by submittedDate DESC", objects, types);
      HashMap map = new HashMap();
      if (scores.isEmpty())
        return new HashMap();
      AssessmentGradingData gdata = (AssessmentGradingData) scores.toArray()[0];
      Iterator iter = gdata.getItemGradingSet().iterator();
      while (iter.hasNext())
      {
        ItemGradingData data = (ItemGradingData) iter.next();
        ArrayList thisone = (ArrayList)
          map.get(data.getPublishedItem().getItemId());
        if (thisone == null)
          thisone = new ArrayList();
        thisone.add(data);
        map.put(data.getPublishedItem().getItemId(), thisone);
      }
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      return new HashMap();
    }
  }

  public void saveTotalScores(ArrayList data) {
    try {
      Iterator iter = data.iterator();
      while (iter.hasNext())
      {
        AssessmentGradingData gdata = (AssessmentGradingData) iter.next();
        getHibernateTemplate().saveOrUpdate(gdata);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void saveItemScores(ArrayList data) {
    try {
      Iterator iter = data.iterator();
      while (iter.hasNext())
      {
        ItemGradingData gdata = (ItemGradingData) iter.next();
        if (gdata.getItemGradingId() == null)
          gdata.setItemGradingId(new Long(0));
        if (gdata.getPublishedItemText() == null)
        {
          //System.out.println("Didn't save -- error in item.");
        }
        else
        {
          // This bit gets around a Hibernate weirdness -- even though
          // we got the assessmentgradingid, and changed info in the
          // itemgradingid, the assessmentgrading still has the original
          // set of itemgradingid's (from before we modified it) in
          // memory.  So we need to either get it all from the database
          // again, which is a heavyweight solution, or just manually
          // swap out the obsolete data before going forward. -- RMG
          Iterator iter2 = gdata.getAssessmentGrading().getItemGradingSet()
            .iterator();
          while (iter2.hasNext())
          {
            ItemGradingData idata = (ItemGradingData) iter2.next();
            if (idata.getItemGradingId().equals(gdata.getItemGradingId()))
            {
              gdata.getAssessmentGrading().getItemGradingSet().remove(idata);
              gdata.getAssessmentGrading().getItemGradingSet().add(gdata);
              break;
            }
          }
   
          // Now we can move on.
          getHibernateTemplate().saveOrUpdate(gdata);
          storeGrades(gdata.getAssessmentGrading(), true);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Assume this is a new item.
   */
  public void storeGrades(AssessmentGradingIfc data)
  {
    storeGrades(data, false);
  }

  /**
   * This is the big, complicated mess where we take all the items in
   * an assessment, store the grading data, auto-grade it, and update
   * everything.
   *
   * If regrade is true, we just recalculate the graded score.  If it's
   * false, we do everything from scratch.
   */
  public void storeGrades(AssessmentGradingIfc data, boolean regrade) {
    try {
      String agent = data.getAgentId();
      if (!regrade)
      {
        //data.setAssessmentGradingId(new Long(0)); // Always create a new one
        data.setSubmittedDate(new Date());
        if (data.getPublishedAssessment().getAssessmentAccessControl() != null
         && data.getPublishedAssessment().getAssessmentAccessControl()
            .getDueDate() != null &&
            data.getPublishedAssessment().getAssessmentAccessControl()
          .getDueDate().before(new Date()))
          data.setIsLate(new Boolean(true));
        else
          data.setIsLate(new Boolean(false));
        if (data.getForGrade().booleanValue())
          data.setStatus(new Integer(1));
        else
          data.setStatus(new Integer(0));
        data.setTotalOverrideScore(new Float(0));
      }
      Set itemgrading = data.getItemGradingSet();
      Iterator iter = itemgrading.iterator();
      float totalAutoScore = 0;
      while (iter.hasNext())
      {
        ItemGradingIfc itemdata = (ItemGradingIfc) iter.next();
        ItemDataIfc item = (ItemDataIfc) itemdata.getPublishedItem();
        float autoScore = (float) 0;
        if (!regrade)
        {
          itemdata.setAssessmentGrading(data);
          itemdata.setSubmittedDate(new Date());
          itemdata.setAgentId(agent);
          //itemdata.setItemGradingId(new Long(0)); // Always create a new one
          itemdata.setOverrideScore(new Float(0));

          // Autograde
          if (item.getTypeId().intValue() == 1 || // MCSS
              item.getTypeId().intValue() == 3 || // MCSS
              item.getTypeId().intValue() == 4) // True/False
            autoScore = getAnswerScore(itemdata);
          if (item.getTypeId().intValue() == 2) // MCMS
            autoScore = getAnswerScore(itemdata); // How should we do this?
          if (item.getTypeId().intValue() == 9) // Matching
              autoScore = (getAnswerScore(itemdata) / (float) item.getItemTextSet().size());
            // Skip 5/6/7, since they can't be autoscored
          else if (item.getTypeId().intValue() == 8) // FIB
            autoScore = getFIBScore(itemdata) / 
              (float) ((ItemTextIfc) item.getItemTextSet().toArray()[0]).getAnswerSet().size();

         }
         else
         {
           autoScore = itemdata.getAutoScore().floatValue();
         }
        if (itemdata.getOverrideScore() == null)
          totalAutoScore += autoScore;
        else
          totalAutoScore += 
            autoScore + itemdata.getOverrideScore().floatValue();
        itemdata.setAutoScore(new Float(autoScore));
      }
      data.setTotalAutoScore(new Float(totalAutoScore));
      data.setFinalScore(new Float(totalAutoScore +
        data.getTotalOverrideScore().floatValue()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    getHibernateTemplate().saveOrUpdate(data);
  }

  /**
   * This grades multiple choice and true false questions.  Since
   * multiple choice/multiple select has a separate ItemGradingIfc for
   * each choice, they're graded the same way the single choice are.
   * Choices should be given negative score values if one wants them
   * to lose points for the wrong choice.
   */
  public float getAnswerScore(ItemGradingIfc data)
  {
    AnswerIfc answer = (AnswerIfc) data.getPublishedAnswer();
    if (answer == null || answer.getScore() == null)
      return (float) 0;
    if (answer.getIsCorrect() == null || !answer.getIsCorrect().booleanValue())
      return (float) 0;
    return answer.getScore().floatValue();
  }

  public float getFIBScore(ItemGradingIfc data)
  {
    String answertext = data.getPublishedAnswer().getText();
    StringTokenizer st = new StringTokenizer(answertext, "|");
    float totalScore = (float) 0;
    while (st.hasMoreTokens())
    {
      String answer = st.nextToken().trim();
      if (data.getAnswerText() != null &&
          data.getAnswerText().trim().equalsIgnoreCase(answer))
        totalScore += data.getPublishedAnswer().getScore().floatValue();
    }
    return totalScore;
  }

  public static void main(String[] args) throws DataFacadeException {
    AssessmentGradingFacadeQueries instance = new AssessmentGradingFacadeQueries ();
    if (args[0].equals("submit")) {
      PublishedAssessmentFacadeQueries pafQ = new PublishedAssessmentFacadeQueries ();
      PublishedAssessmentData p = pafQ.loadPublishedAssessment(new Long(args[1]));
      print(p);
      Boolean forGrade = new Boolean("true");
      AssessmentGradingData r = instance.prepareRealizedAssessment(p, forGrade);
      Long rAssessmentId = instance.add(r);
    }
    System.exit(0);
  }

  public AssessmentGradingData prepareRealizedAssessment(PublishedAssessmentData p, Boolean forGrade){
    Float totalAutoScore = new Float("0");
    AssessmentGradingData a = new AssessmentGradingData();
    Set itemSet = getAllItems(p.getSectionSet());
    Set itemGradingSet = createItemGradingSet(a, itemSet, totalAutoScore);
    a.setAgentId("1");
    a.setForGrade(forGrade);
    if (p.getAssessmentAccessControl().getDueDate().before(new Date()))
      a.setIsLate(new Boolean ("true"));
    else
      a.setIsLate(new Boolean ("false"));
    a.setItemGradingSet(itemGradingSet);
    a.setPublishedAssessment(p);
    a.setTotalAutoScore(totalAutoScore);
    return a;
  }

  public Set getAllItems(Set sectionSet){
    HashSet h = new HashSet();
    Iterator i = sectionSet.iterator();
    while (i.hasNext()) {
      PublishedSectionData section = (PublishedSectionData) i.next();
      Set itemSet = section.getItemSet();
      h.addAll(itemSet);
    }
    return h;
  }

  public Set createItemGradingSet(AssessmentGradingData a, Set itemSet, Float totalAutoScore){
    HashSet h = new HashSet();
    Iterator i = itemSet.iterator();
    while (i.hasNext()) {
      PublishedItemData item = (PublishedItemData) i.next();
      ItemGradingData d = createItemGrading(a, item,
        item.getItemTextSet(), totalAutoScore);
      h.add(d);
    }
    return h;
  }

  public ItemGradingData createItemGrading(AssessmentGradingData a, PublishedItemData p, Set itemTextSet, Float totalAutoScore){
    ItemGradingData d = new ItemGradingData();
    String answerText = null;
    Iterator i = itemTextSet.iterator();
    while (i.hasNext()) {
      PublishedItemText itemText = (PublishedItemText) i.next();
      Set answerSet = itemText.getAnswerSet();
      if (answerSet == null || answerSet.iterator() == null){
        d.setAnswerText("hello Daisy!!");
      }
      else{
        PublishedAnswer ans = (PublishedAnswer)answerSet.iterator().next();
        d.setPublishedAnswer(ans);
        d.setRationale("this is the rationale");
        if (ans.getIsCorrect() != null &&
            ans.getIsCorrect().equals(new Boolean("true")))
          d.setAutoScore(ans.getScore());
          totalAutoScore = new Float(totalAutoScore.floatValue() + ans.getScore().floatValue());
      }
    }
    d.setPublishedItem(p);
    d.setAgentId("1");
    d.setSubmittedDate(new Date());
    d.setAssessmentGrading(a);
    return d;
  }

  public Long add(AssessmentGradingData a) {
    getHibernateTemplate().save(a);
    return a.getAssessmentGradingId();
  }

  public static void print(AssessmentBaseIfc a) {
    //System.out.println("**assessmentId #" + a.getAssessmentBaseId());
    //System.out.println("**assessment is template? " + a.getIsTemplate());
    if (a.getIsTemplate().equals(Boolean.FALSE)){
      //System.out.println("**assessmentTemplateId #" + ((AssessmentData)a).getAssessmentTemplateId());
      //System.out.println("**section: " +
      //               ((AssessmentData)a).getSectionSet());
    }
    /**
    System.out.println("**assessment due date: " +
                     a.getAssessmentAccessControl().getDueDate());
    System.out.println("**assessment control #" +
                       a.getAssessmentAccessControl());
    System.out.println("**assessment metadata" +
                       a.getAssessmentMetaDataSet());
    System.out.println("**Objective not lazy = " +
                   a.getAssessmentMetaDataByLabel("ASSESSMENT_OBJECTIVE"));
    */
  }

  public int getSubmissionSizeOfPublishedAssessment(Long publishedAssessmentId){
    List size = getHibernateTemplate().find(
        "select count(i) from AssessmentGradingData a where a.forGrade=1 and a.publishedAssessment.publishedAssessmentId=?"+ publishedAssessmentId);
    Iterator iter = size.iterator();
    if (iter.hasNext()){
      int i = ((Integer)iter.next()).intValue();
      //System.out.println("** Submission size = "+i);
      return i;
    }
    else{
	//System.out.println("** no submission");
      return 0;
    }
  }

  public HashMap getSubmissionSizeOfAllPublishedAssessments(){
    HashMap h = new HashMap();
    List list = getHibernateTemplate().find(
        "select new PublishedAssessmentData(a.publishedAssessment.publishedAssessmentId, count(a)) from AssessmentGradingData a where a.forGrade=1 group by a.publishedAssessment.publishedAssessmentId");
    Iterator iter = list.iterator();
    while (iter.hasNext()){
      PublishedAssessmentData o = (PublishedAssessmentData)iter.next();
      //System.out.println("** assessment grading ="+o.getPublishedAssessmentId()+":"+o.getSubmissionSize());
      h.put(o.getPublishedAssessmentId(), new Integer(o.getSubmissionSize()));
    }
    return h;
  }

  public Long saveMedia(byte[] media, String mimeType){
    MediaData mediaData = new MediaData(media, mimeType);
    getHibernateTemplate().save(mediaData);
    return mediaData.getMediaId();
  }

  public Long saveMedia(MediaData mediaData){
    getHibernateTemplate().save(mediaData);
    return mediaData.getMediaId();
  }

  public MediaData getMedia(Long mediaId){
    return (MediaData) getHibernateTemplate().load(MediaData.class,mediaId);
  }

/**
  public ArrayList getMediaArray(Long itemGradingId){
    ArrayList a = new ArrayList();
    List list = getHibernateTemplate().find(
        "from MediaData m where m.itemGradingData.itemGradingId=?",
        new Object[] { itemGradingId },
        new net.sf.hibernate.type.Type[] { Hibernate.LONG });
    for (int i=0;i<list.size();i++){
      a.add((MediaData)list.get(i));
    }
    return a;
  }
*/
  public ArrayList getMediaArray(ItemGradingData item){
    ArrayList a = new ArrayList();
    List list = getHibernateTemplate().find(
        "from MediaData m where m.itemGradingData=?", item );
    for (int i=0;i<list.size();i++){
      a.add((MediaData)list.get(i));
    }
    return a;
  }

  public ItemGradingData getLastItemGradingDataByAgent(
      Long publishedItemId, String agentId)
  {
    List itemGradings = getHibernateTemplate().find(
        "from ItemGradingData i where i.publishedItem.itemId=? and i.agentId=?",
        new Object[] { publishedItemId, agentId },
        new net.sf.hibernate.type.Type[] { Hibernate.LONG, Hibernate.STRING });
    if (itemGradings.size() == 0)
      return null;
    return (ItemGradingData) itemGradings.get(0);
  }

/* Dummy Data
      AssessmentGradingSummaryData summary = new AssessmentGradingSummaryData();
      AssessmentGradingData data = new AssessmentGradingData();
      data.setTotalAutoScore(new Float(8.0));
      data.setTotalOverrideScore(new Float(5.0));
      data.setFinalScore(new Float(3.0));
      data.setComments("Good job!");
      data.setIsLate(new Boolean(false));
      data.setForGrade(new Boolean(true));
      data.setSubmittedDate(new Date());
      data.setAgentId("admin");
      AssessmentData assess = new AssessmentData(new Long(0), "Assessment", "An
assessment", "comment", new Long(1), new Long(1), new Integer(1), new Integer(1)
, new Integer(1), new Integer(0), "admin", new Date(), "admin", new Date());
      PublishedAssessmentData pub = new PublishedAssessmentData("Assessment", "A
n assessment", "comment", new Long(1), new Integer(1), new Integer(1), new Integ
er(1), new Integer(0), "admin", new Date(), "admin", new Date());
      pub.setPublishedAssessmentId(new Long(1));
      assess.setAssessmentBaseId(new Long(1));
      pub.setAssessment(assess);
      data.setPublishedAssessment(pub);
      data.setAgentId("admin");
      ArrayList list = new ArrayList();
      summary.setAcceptedAssessmentGrading(data);
      list.add(summary);
      return list;
*/
}
