 
 /**
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author jlannan
 * @version $Id: PublishedAssessmentBean.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
package org.navigoproject.data;


/**
 * @hibernate.class
 *            table="assessment_published"
 */
public class PublishedAssessmentBean {
  
	private String publishedId, coreId;  
	  
	public PublishedAssessmentBean(){		
	}

	public PublishedAssessmentBean (String publishedId, String coreId){
										                       	
		this.publishedId = publishedId;
		this.coreId = coreId;
	}
	
	/**
	 * @hibernate.id
	 *            generator-class="assigned"
	 *            column="published_id"                
	 */	
	public String getPublishedId(){
		return this.publishedId;
	}

	public void setPublishedId(String pk){
		this.publishedId = pk;
	}
	
	/**
	 * @hibernate.property
	 *            column="core_id"
	 */
	public String getCoreId(){
		return this.coreId;
	}

	public void setCoreId(String coreId){
		this.coreId = coreId;
	}	
  
  public String toString() {
    
    return "{PublishedAssessmentBean coreId: "+coreId+", publishedId: "+publishedId+"}";
    
    
    
  }
}
