/*
 * $Header: /cvs/sakai/portal/src/aspects/org/sakaiproject/portal/varuna/mockVaruna.aj,v 1.3 2004/12/15 20:10:21 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 11, 2004
 *
 * This aspect is to create a way to insert mocks even when there are
 * issues such as using static cover classes and mixed interface and 
 * concrete class implementations.
 * 
 * We just intercept the call and return the mocked value.  Since
 * we need to be able to have the junit code reference the mocks 
 * to set them up they are kept as statics in the test class
 * MockInstances.
 * 
 */
package org.sakaiproject.portal.varuna;

import java.io.*;

import org.sakaiproject.portal.varuna.MockInstances;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
//import org.sakaiproject.service.legacy.site.SiteService;
//import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.*;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.util.ParameterParser;
import javax.servlet.http.HttpServletRequest;
import org.sakaiproject.vm.VmServletInterface;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.portal.PortalService;


//

/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public aspect mockVaruna {
     
    pointcut VarunaCode () :
       ( within(VarunaServlet) 
        || within(PortalUrl))
        && ! within(mockVaruna);
    
   // ToolConfig tc = PortalService.getToolConfigFromPid(pathTool);
    ///////// mock getSiteId
    
    // not necessary since now within Sakai-portal
    
//    pointcut getToolConfigFromPid(String s) :
//        call(* org..VarunaServlet.getToolConfigFromPid(String)) 
//        && args(s)
//        && VarunaCode();

    
//    ToolConfiguration around(String s) : getToolConfigFromPid(s) {
//        return ((ToolConfiguration)((PortalService)MockInstances
//        .mockPortalService.proxy()).getToolConfigFromPid(s));
//    }

    pointcut TCgetPageId() :
        call(* org..ToolConfiguration.getPageId()) 
        && VarunaCode();
//        && within(VarunaCode)
//        && !within(mockVaruna);
    
    String around() : TCgetPageId() {
        return ((String)((ToolConfiguration)MockInstances
        .mockToolConfiguration.proxy()).getPageId());
    }

    
    
    ///////// mock getSiteId
    
    pointcut getUserSiteId(String s) :
        call(* org..SiteService.getUserSiteId(String)) 
        && args(s)
        && VarunaCode();
//        && within(VarunaCode)
//        && !within(mockVaruna);
    
    String around(String s) : getUserSiteId(s) {
        return ((String)((SiteService)MockInstances
        .mockSiteService.proxy()).getUserSiteId(s));
    }

    //////////////// mock getSiteVisit
    
    pointcut getSiteVisit(String s) :
        call(* org..SiteService.getSiteVisit(String)) 
        && args(s)
        && VarunaCode();
 //       && !within(mockVaruna);
    
    Site around(String s) throws IdUnusedException, PermissionException : getSiteVisit(s) { 
        Site site = null;
            site = ((Site)
                    ((SiteService)MockInstances
                            .mockSiteService.proxy()).getSiteVisit(s));        
        return site;
  
    }
    
    //////////////// mock findPage
    
    pointcut findPage(String s) :
        call(* org..SiteService.findPage(String)) 
        && args(s)
        && VarunaCode();
        //&& !within(mockVaruna);
    
    SitePage around(String s)  : findPage(s) { 
        SitePage page = null;
            page = ((SitePage)
                    ((SiteService)MockInstances
                            .mockSiteService.proxy()).findPage(s));        
        return page;
  
    }

    //////////////// mock getContainingSite 
    
    pointcut getContainingSite() :
        call(* org.sakaiproject..SitePage.getContainingSite()) 
        && VarunaCode();
    
    Site around()  : getContainingSite() { 
        Site site = null;
            site = ((Site)
                    ((SitePage)MockInstances
                            .mockSitePage.proxy()).getContainingSite());        
        return site; 
  
    }
    
    //////////////// mock server configuration service getBoolean
    
    pointcut getBoolean(String s,boolean b) :
        call(* org..ServerConfigurationService.getBoolean(String,boolean)) 
        && args(s,b)
        && VarunaCode();
    
    boolean around(String s, boolean b) : getBoolean(s,b) { 
       boolean flag = false;
            flag = ((boolean)
                    ((ServerConfigurationService)MockInstances
                            .mockServerConfigurationService.proxy()).getBoolean(s,b));      
        return flag;
  
    }
    
    /////////////// mock the parameter methods.
    
  pointcut getPath() :
      call(* org.sakaiproject.util.ParameterParser.getPath()) 
      && VarunaCode();
  //    && !within(mockVaruna);

  String around() : getPath() { 
    String path = null;
        path = ((ParameterParser) MockInstances.mockParameterParser.proxy()).getPath(); 
    return path;
  }
    
  pointcut getInt(String s) :
      call(* org.sakaiproject.util.ParameterParser.getInt(String)) 
      && args(s)
      && VarunaCode();

      int around(String s) : getInt(s) { 
          int intValue = 0;
            intValue = ((ParameterParser) MockInstances.mockParameterParser.proxy()).getInt(s); 
            return intValue;
      }
  
  pointcut doSetVmReference(String key, Object val,HttpServletRequest req) :
      call(* org.sakaiproject.portal..VarunaServlet.setVmReference(String, Object,HttpServletRequest))
      && args(key,val,req)
      && VarunaCode();

      void around(String key, Object val,HttpServletRequest req) : doSetVmReference(key,val,req) { 
        ((org.sakaiproject.vm.VmServletInterface)
                MockInstances.mockVmServletInterface.proxy()).setVmReference(key,val,req);
      }
      
      
      ////////////////////// testing respondRedirectToLogin
      
      // We mock lots of stuff that won't change because the mocks
      // will check that the expected things show up when these things
      // are used.
      
      pointcut getSessionState(String name) :
          call(* org.sakaiproject..UsageSessionService.getSessionState(String))
          && args(name) 
   && VarunaCode();
      	
      SessionState around(String name) : getSessionState(name) {
         return (org.sakaiproject.service.framework.session.SessionState) 
         ((org.sakaiproject.service.framework.session.UsageSessionService)
                 MockInstances.mockUsageSessionService.proxy()).getSessionState(name);
      }
      
      
      pointcut getPortalUrl() :
          call(* org.sakaiproject..ServerConfigurationService.getPortalUrl())
          && VarunaCode();
      	
      String around() : getPortalUrl() {
         return (String) 
         ((org.sakaiproject.service.framework.config.ServerConfigurationService)
                 MockInstances.mockServerConfigurationService.proxy()).getPortalUrl();
      }
      
      
      pointcut getLoginUrl() :
          call(* org.sakaiproject..ServerConfigurationService.getLoginUrl())
   && VarunaCode();
      	
      String around() : getLoginUrl() {
         return (String) 
         ((org.sakaiproject.service.framework.config.ServerConfigurationService)
                 MockInstances.mockServerConfigurationService.proxy()).getLoginUrl();
      }
      
      pointcut getGatewaySiteId() :
          call(* org.sakaiproject..ServerConfigurationService.getGatewaySiteId())
   && VarunaCode();
      	
      String around() : getGatewaySiteId() {
         return (String) 
         ((org.sakaiproject.service.framework.config.ServerConfigurationService)
                 MockInstances.mockServerConfigurationService.proxy()).getGatewaySiteId();
      }
      
      pointcut sendRedirect(String url) :
          call(* javax.servlet.http.HttpServletResponse.sendRedirect(String))
          && args(url)
   && VarunaCode();
      	
      void around(String url) throws IOException : sendRedirect(url) {
         ((javax.servlet.http.HttpServletResponse)
                 MockInstances.mockHttpServletResponse.proxy()).sendRedirect(url);
      }
      
      pointcut paramsGetString(String s) :
          call(* org.sakaiproject.util.ParameterParser.getString(String))
          && args(s)
   && VarunaCode();
      	
      String around(String s) : paramsGetString(s) {
         return (String) 
         ((org.sakaiproject.util.ParameterParser)
                 MockInstances.mockParameterParser.proxy()).getString(s);
      }
      
      

      pointcut doStringUtilSplitCut(String path, String sep):
          call(* org.sakaiproject..StringUtil.split(String,String))
          && args(path,sep)
  //        && within(VarunaCode)
   && VarunaCode();
      
     String[] around(String path, String sep) : doStringUtilSplitCut(path, sep) {
         if (MockInstances.pleaseMockStringUtil) {
  //        return (String []) ((org.sakaiproject.util.StringUtil)
             return (String []) ((org.sakaiproject.util.StringUtilInterface)
          	MockInstances.mockStringUtil.proxy()).split(path,sep);
         }
         else {
             return proceed(path,sep);
         }
      }
      
     
     ////////////// logging ///////////////////////////
     
     // divert warn calls to the mock
     pointcut doLoggerWarn(Object msg, Throwable t):
         call(* org.sakaiproject..Logger.warn(Object,Throwable))
         && args(msg,t)
   //      && within(VarunaCode)
  && VarunaCode();
     
    void around(Object msg, Throwable t) : doLoggerWarn(msg, t) {
        ((org.sakaiproject.service.framework.log.Logger)
         	MockInstances.mockLogger.proxy()).warn(msg,t);
     }
     
    
    // divert warn calls to the mock
    pointcut doLoggerWarnSingle(Object msg):
        call(* org.sakaiproject..Logger.warn(Object))
        && args(msg)
        && VarunaCode();
  //      && !within(mockVaruna);
    
   void around(Object msg) : doLoggerWarnSingle(msg) {
       ((org.sakaiproject.service.framework.log.Logger)
        	MockInstances.mockLogger.proxy()).warn(msg);
    }
    
    // divert error calls to the mock
    pointcut doLoggerError(Object msg):
        call(* org.sakaiproject..Logger.error(Object))
        && args(msg)
        && VarunaCode();
 //       && !within(mockVaruna);
    
   void around(Object msg) : doLoggerError(msg) {
       ((org.sakaiproject.service.framework.log.Logger)
        	MockInstances.mockLogger.proxy()).error(msg);
    }
    
    // state that debug logging is turned off
    pointcut doLoggerIsDebugEnabled():
        call(* org.sakaiproject..Logger.isDebugEnabled())
        && VarunaCode();
 //       && !within(mockVaruna);
    
   boolean around() : doLoggerIsDebugEnabled() {
	  return false;
    }
      
   // intercept the Site.getId call
   pointcut doGetId():
       call(* org.sakaiproject..Site.getId())
       && VarunaCode();
       //&& !within(mockVaruna);
   
  String around() : doGetId() { 
      return (String) ((org.sakaiproject.service.legacy.site.Site)
       	MockInstances.mockSite.proxy()).getId();
   }
  
    
      ////////////////////////////////////////
      ////////////////////////////////////////
      // This is an approach to putting debugging information from the object
      // into the aspect, but need to be able to access from the object
      // level.  Local variables don't cut it.
//      pointcut doStringUtilSplit(VarunaServlet v) :
//          call(* java..StringUtil.split(String, String))
//          && this(v)
//          && within(VarunaServlet)
//          && !within(mockVaruna);
//
//          after(VarunaServlet v) : doStringUtilSplit(v) { 
//                  System.out.println("path: [" + v.path + "]");
//                  for (int i = 0; i < v.parts.length; i++) {
//                      System.out.println("parts[" + i + "]: [" + v.parts[i]
//                                                                       + "]");
//                  }
//          }
      
}
