package org.sakaiproject.tool.assessment.data.dao.questionpool;

import java.io.Serializable;
import org.navigoproject.osid.impl.PersistenceService;
/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolItem.java,v 1.1 2004/10/04 17:55:51 daisyf.stanford.edu Exp $
 */
public class QuestionPoolItem
  implements Serializable
{
  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = 9180085666292824370L;

  private Long questionPoolId;
  private String itemId;
  private AssetBeanie assetBeanie; //<-- is the item
    //private QuestionPool questionPool;

  public QuestionPoolItem(){
  }

  public QuestionPoolItem(Long questionPoolId, String itemId){
    this.questionPoolId = questionPoolId;
    this.itemId = itemId;  
    //this.assetBeanie = PersistenceService.getInstance().getQuestionPoolQueries().getAsset(itemId);
  }

  public QuestionPoolItem(Long questionPoolId, String itemId, AssetBeanie assetBeanie){
    this.questionPoolId = questionPoolId;
    this.itemId = itemId;
    this.assetBeanie = assetBeanie;
  }

  public QuestionPoolItem(AssetBeanie assetBeanie, QuestionPoolProperties questionPoolProperties){
    this.assetBeanie = assetBeanie;
    //this.questionPool = questionPool;
    setItemId(assetBeanie.getId());
    setQuestionPoolId(questionPoolProperties.getId());
  }

  public Long getQuestionPoolId()
  {
    return questionPoolId;
  }

  public void setQuestionPoolId(Long questionPoolId)
  {
    this.questionPoolId = questionPoolId;
  }

  public String getItemId()
  {
    return itemId;
  }

  public void setItemId(String itemId)
  {
    this.itemId = itemId;
  }

  public boolean equals(Object questionPoolItem){
    boolean returnValue = false;
    if (this == questionPoolItem)
      returnValue = true;  
    if (questionPoolItem != null && questionPoolItem.getClass()==this.getClass()){
      QuestionPoolItem qpi = (QuestionPoolItem)questionPoolItem;
      if ((this.getItemId()).equals(qpi.getItemId())
          && (this.getQuestionPoolId()).equals(qpi.getQuestionPoolId()))
        returnValue = true;
    }
    return returnValue;
  }

  public int hashCode(){
    String s = this.itemId+":"+(this.questionPoolId).toString();
    return (s.hashCode());
  }
}
