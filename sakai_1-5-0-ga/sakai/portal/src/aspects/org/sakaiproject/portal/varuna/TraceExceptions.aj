/*
 * Created on Nov 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
//package org.sakaiproject.debug.aspects;
package org.sakaiproject.portal.varuna;

import org.sakaiproject.service.framework.log.Logger;

/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
 abstract aspect TraceExceptions {
	
	abstract pointcut traceExceptionsCut(Throwable e);
	abstract pointcut catchThrowable();
	
	/********************************************************
	* Dependencies and their setter methods 
	*********************************************************/
	
	protected Logger m_logger = null;
	
	/**
	 * Dependency: logging service.
	 * *param service The logging service.
	 */
	
	// Spring 1.1 can set these, but 1.0.1 doesn't.
//	public void setLogger(Logger service) {
//		m_logger = service;
//	}
//	

	before(Throwable e) : traceExceptionsCut(e) {
		System.out.println("traceException: "+thisJoinPoint.toString()+e.getCause());
		System.out.println("handler location: "+thisJoinPoint.getSourceLocation().toString());
		
		e.printStackTrace(System.out);
	}
	
	after() throwing(Throwable t): catchThrowable() {
		System.out.println("throwing: "+thisJoinPoint.toString()+t.getCause());
		System.out.println("source location: "+thisJoinPoint.getSourceLocation().toString());
		
		t.printStackTrace(System.out);
	}
}
