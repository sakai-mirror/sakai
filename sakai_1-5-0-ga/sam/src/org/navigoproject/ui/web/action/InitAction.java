package org.navigoproject.ui.web.action;
import javax.servlet.http.HttpServlet;
import org.navigoproject.osid.impl.PersistenceService;
import org.sakaiproject.tool.assessment.queries.TypeImplQueries;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueries;
import org.sakaiproject.tool.assessment.facade.QuestionPoolFacadeQueries;
import org.sakaiproject.tool.assessment.facade.AssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.facade.ItemFacadeQueries;
import org.sakaiproject.tool.assessment.facade.SectionFacadeQueries;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.facade.AssessmentGradingFacadeQueries;
import org.sakaiproject.tool.assessment.facade.authz.AuthorizationFacadeQueries;
import org.sakaiproject.tool.assessment.util.PagingUtilQueries;
import org.sakaiproject.tool.assessment.facade.AuthzQueriesFacade;

import java.util.List;

public class InitAction extends HttpServlet{

  public void init(){
    // store all types in memory
    TypeFacadeQueries typeFacadeQueries = PersistenceService.getInstance().getTypeFacadeQueries();
    System.out.println("*****#2 InitAction: typeFacadeQueries ="+typeFacadeQueries);
    if ( typeFacadeQueries != null ){
      typeFacadeQueries.setTypeFacadeMap();
      typeFacadeQueries.setFacadeItemTypes();
    }

    // questionpool facde testing
    QuestionPoolFacadeQueries questionpoolFacadeQueries = PersistenceService.getInstance().getQuestionPoolFacadeQueries();
    System.out.println("*****#3  InitAction: questionpoolFacadeQueries ="+questionpoolFacadeQueries);

    // assessment facade testing
    AssessmentFacadeQueries assessmentFacadeQueries = PersistenceService.getInstance().getAssessmentFacadeQueries();
    System.out.println("*****#4  InitAction: assessmentFacadeQueries ="+assessmentFacadeQueries);

    // item facade testing
    ItemFacadeQueries itemFacadeQueries = PersistenceService.getInstance().getItemFacadeQueries();
    System.out.println("*****#5  InitAction: itemFacadeQueries ="+itemFacadeQueries);

    // section facade testing
    SectionFacadeQueries sectionFacadeQueries = PersistenceService.getInstance().getSectionFacadeQueries();
    System.out.println("*****#6  InitAction: sectionFacadeQueries ="+sectionFacadeQueries);

    // published assessment facade testing
    PublishedAssessmentFacadeQueries publishedAssessmentFacadeQueries = PersistenceService.getInstance().getPublishedAssessmentFacadeQueries();
    System.out.println("*****#7  InitAction: publishedAssessmentFacadeQueries ="+publishedAssessmentFacadeQueries);

    // assessment grading facade testing
    AssessmentGradingFacadeQueries assessmentGradingFacadeQueries = PersistenceService.getInstance().getAssessmentGradingFacadeQueries();
    System.out.println("*****#8  InitAction: assessmentGradingFacadeQueries ="+assessmentGradingFacadeQueries);

    // authorization facade testing
    AuthorizationFacadeQueries authorizationFacadeQueries = PersistenceService.getInstance().getAuthorizationFacadeQueries();
    System.out.println("*****#9  InitAction: authorizationFacadeQueries ="+authorizationFacadeQueries);

    // PagingUtil testing
    PagingUtilQueries pagingUtilQueries = PersistenceService.getInstance().getPagingUtilQueries();
    System.out.println("*****#10  InitAction: pagingUtilQueries ="+pagingUtilQueries);

    // authorization facade testing
    AuthzQueriesFacade authzQueriesFacade = PersistenceService.getInstance().getAuthzQueriesFacade();
    System.out.println("*****#11  InitAction: authzQueriesFacade ="+authzQueriesFacade);

  }
}
