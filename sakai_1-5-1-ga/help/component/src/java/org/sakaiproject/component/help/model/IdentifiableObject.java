/*
 * Created on Oct 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.sakaiproject.component.help.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: May 15, 2004
 * Time: 1:55:47 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class IdentifiableObject {
  
	  private Long id;
	  protected final Log logger = LogFactory.getLog(this.getClass());
	  
	  public boolean equals(Object in) {
	    if (in == null && this == null) return true;
	    if (in == null && this != null) return false;
	    if (this == null && in != null) return false;
	    if (!this.getClass().isAssignableFrom(in.getClass())) return false;
	    if (this.getId() == null && ((IdentifiableObject) in).getId() == null ) return true;
	    if (this.getId() == null || ((IdentifiableObject) in).getId() == null ) return false;
	    return this.getId().equals(((IdentifiableObject) in).getId());
	 }
	  
	  public int hashCode() {
	    return (id != null ? id.hashCode() : 0);
	 }
	
	 public Long getId() {
	    return id;
	 }
	
	 public void setId(Long id) {
	    this.id = id;
	 }

}
