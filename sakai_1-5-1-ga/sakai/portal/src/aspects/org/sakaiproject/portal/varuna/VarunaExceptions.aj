/*
 *  $Header: /cvs/sakai/portal/src/aspects/org/sakaiproject/portal/varuna/VarunaExceptions.aj,v 1.2 2004/12/15 20:10:21 dlhaines.umich.edu Exp $
 * 
 * Created on Nov 8, 2004
 *
 * This is a specific aspect to instanciate the abstract general TraceExceptions
 * for varuna.  You can adjust which exceptions are caught by modifying 
 * these cutpoints.
 * 
 */

package org.sakaiproject.portal.varuna;

/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
  aspect VarunaExceptions extends TraceExceptions {
	
      // These will turn off the exception catches.
//      pointcut traceExceptionsCut(Throwable t);
//      pointcut catchThrowable();
      
      pointcut traceExceptionsCut(Throwable t);
      
 	 pointcut catchThrowable() :
	     (call(* org.sakaiproject..*.*(..)) 
	             || call(* java.io..*.*(..)))
	     && !within(TraceExceptions+);
      
 	 // This will track calls to exception handlers, 
 	 // even those that are handled.
//	 pointcut traceExceptionsCut(Throwable e) :
//	 	handler(Throwable+) && args(e)
//	 	&& !within(TraceExceptions+);
	 
	 // This will catch all thrown exceptions within
	 // sakaiproject packages.
//	 pointcut catchThrowable() :
//	     call(* org.sakaiproject..*.*(..))
//	     && !within(TraceExceptions+);

}
