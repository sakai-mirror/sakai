
 /**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author jlannan
 * @version $Id: PersistenceService.java,v 1.2 2005/02/11 15:13:06 cwen.iupui.edu Exp $
 */
package org.navigoproject.osid.impl;

import org.navigoproject.data.AssessmentQueries;
import org.navigoproject.data.PublishedAssessmentQueries;
import org.navigoproject.data.QtiQueries;
import org.navigoproject.osid.assessment.impl.PublishingService;
import org.sakaiproject.framework.ApplicationContextLocator;
import org.sakaiproject.tool.assessment.queries.TypeImplQueries;
import org.sakaiproject.tool.assessment.queries.QuestionPoolQueries;
import org.sakaiproject.tool.assessment.facade.QuestionPoolFacadeQueries;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueries;
import org.sakaiproject.tool.assessment.facade.SectionFacadeQueries;
import org.sakaiproject.tool.assessment.facade.ItemFacadeQueries;
import org.sakaiproject.tool.assessment.facade.AssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.facade.AssessmentGradingFacadeQueries;
import org.sakaiproject.tool.assessment.facade.authz.AuthorizationFacadeQueries;
import org.sakaiproject.tool.assessment.util.PagingUtilQueries;
import org.sakaiproject.tool.assessment.facade.AuthzQueriesFacade;

//for unit test - chen
//import test.org.sakaiproject.tool.assessment.facade.junit.spring.ApplicationContextBaseTest;
//import test.org.sakaiproject.tool.assessment.facade.TestQueryFacade;

/**
 * @author jlannan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PersistenceService{

	private final static org.apache.log4j.Logger LOG =
			org.apache.log4j.Logger.getLogger(PersistenceService.class);
			
	private QtiQueries qtiQueries;
	private AssessmentQueries assessmentQueries;
	private PublishedAssessmentQueries publishedAssessmentQueries;
	private PublishingService publishingService;
	private QuestionPoolQueries questionPoolQueries;
	private TypeImplQueries typeImplQueries;
	private QuestionPoolFacadeQueries questionPoolFacadeQueries;
	private TypeFacadeQueries typeFacadeQueries;
	private SectionFacadeQueries sectionFacadeQueries;
	private ItemFacadeQueries itemFacadeQueries;
	private AssessmentFacadeQueries assessmentFacadeQueries;
	private PublishedAssessmentFacadeQueries publishedAssessmentFacadeQueries;
	private AssessmentGradingFacadeQueries assessmentGradingFacadeQueries;
        private AuthorizationFacadeQueries authorizationFacadeQueries;
        private PagingUtilQueries pagingUtilQueries;
//chen change
        private AuthzQueriesFacade authzQueriesFacade;
//chen - for junit test
//        private TestQueryFacade testQueryFacade; 
 
	private static PersistenceService INSTANCE;
	
	public static PersistenceService getInstance(){		
		if (INSTANCE != null){
			return INSTANCE;
		}		  
		else{
//chen - for junit test		  
			return INSTANCE = (PersistenceService) ApplicationContextLocator.getInstance().getBean("PersistenceService");
//		  return INSTANCE = ApplicationContextBaseTest.getPersistenceService();
		}		  			
	}
			
	public QtiQueries getQtiQueries(){
		return qtiQueries;
	}
	
	public void setQtiQueries(QtiQueries qti){		
	  qtiQueries = qti;	
	}
	
	public AssessmentQueries getAssessmentQueries(){
		return assessmentQueries;
	}					  
	
	public void setAssessmentQueries(AssessmentQueries aq){		
		assessmentQueries = aq;		
	}
	
	public PublishedAssessmentQueries getPublishedAssessmentQueries(){
		return publishedAssessmentQueries;
	}					  
	
	public void setPublishedAssessmentQueries(PublishedAssessmentQueries paq){		
		publishedAssessmentQueries = paq;
	}

        public PublishingService getPublishingService(){
               return publishingService;
        }
  
        public void setPublishingService(PublishingService ps){
  	       publishingService = ps;
        }

	public QuestionPoolQueries getQuestionPoolQueries(){
		return questionPoolQueries;
	}
	
	public void setQuestionPoolQueries(QuestionPoolQueries questionPoolQueries){		
	        this.questionPoolQueries = questionPoolQueries;	
	}

	public TypeImplQueries getTypeImplQueries(){
		return typeImplQueries;
	}
	
	public void setTypeImplQueries(TypeImplQueries typeImplQueries){		
	    this.typeImplQueries = typeImplQueries;
	}
 	
	public QuestionPoolFacadeQueries getQuestionPoolFacadeQueries(){
		return questionPoolFacadeQueries;
	}
	
	public void setQuestionPoolFacadeQueries(QuestionPoolFacadeQueries questionPoolFacadeQueries){		
	        this.questionPoolFacadeQueries = questionPoolFacadeQueries;	
	}

	public TypeFacadeQueries getTypeFacadeQueries(){
		return typeFacadeQueries;
	}
	
	public void setTypeFacadeQueries(TypeFacadeQueries typeFacadeQueries){		
	    this.typeFacadeQueries = typeFacadeQueries;
	}
 	
	public SectionFacadeQueries getSectionFacadeQueries(){
		return sectionFacadeQueries;
	}
	
	public void setSectionFacadeQueries(SectionFacadeQueries sectionFacadeQueries){		
	    this.sectionFacadeQueries = sectionFacadeQueries;
	}
 	
	public ItemFacadeQueries getItemFacadeQueries(){
		return itemFacadeQueries;
	}
	
	public void setItemFacadeQueries(ItemFacadeQueries itemFacadeQueries){		
	    this.itemFacadeQueries = itemFacadeQueries;
	}
 	
	public AssessmentFacadeQueries getAssessmentFacadeQueries(){
		return assessmentFacadeQueries;
	}
	
	public void setAssessmentFacadeQueries(AssessmentFacadeQueries assessmentFacadeQueries){		
	    this.assessmentFacadeQueries = assessmentFacadeQueries;
	}
 	
	public PublishedAssessmentFacadeQueries getPublishedAssessmentFacadeQueries(){
		return publishedAssessmentFacadeQueries;
	}
	
	public void setPublishedAssessmentFacadeQueries(PublishedAssessmentFacadeQueries publishedAssessmentFacadeQueries){
	    this.publishedAssessmentFacadeQueries = publishedAssessmentFacadeQueries;
	}
 	
	public AssessmentGradingFacadeQueries getAssessmentGradingFacadeQueries(){
		return assessmentGradingFacadeQueries;
	}
	
	public void setAssessmentGradingFacadeQueries(AssessmentGradingFacadeQueries assessmentGradingFacadeQueries){		
	    this.assessmentGradingFacadeQueries = assessmentGradingFacadeQueries;
	}

        public AuthorizationFacadeQueries getAuthorizationFacadeQueries(){
	  return authorizationFacadeQueries;
        }

        public void setAuthorizationFacadeQueries(AuthorizationFacadeQueries authorizationFacadeQueries){
	  this.authorizationFacadeQueries = authorizationFacadeQueries;
        }
 	
        public PagingUtilQueries getPagingUtilQueries(){
	  return pagingUtilQueries;
        }

        public void setPagingUtilQueries(PagingUtilQueries pagingUtilQueries){
	  this.pagingUtilQueries = pagingUtilQueries;
        }
 	
//chen change        
        public final AuthzQueriesFacade getAuthzQueriesFacade()
        {
          return authzQueriesFacade;
        }
        public final void setAuthzQueriesFacade(AuthzQueriesFacade authzQueriesFacade)
        {
          this.authzQueriesFacade = authzQueriesFacade;
        }
//chen - for junit test        
/*        public final TestQueryFacade getTestQueryFacade()
        {
          return testQueryFacade;
        }
        public final void setTestQueryFacade(TestQueryFacade testQueryFacade)
        {
          this.testQueryFacade = testQueryFacade;
        }*/
}
