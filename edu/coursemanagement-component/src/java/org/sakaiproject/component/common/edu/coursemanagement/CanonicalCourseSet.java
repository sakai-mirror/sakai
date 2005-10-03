package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;

public class CanonicalCourseSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long courseSetId;
	private Long canonicalCourseId;
	
        public CanonicalCourseSet(){}

        public CanonicalCourseSet(Long courseSetId, Long canonicalCourseId){
	  this.setCourseSetId(courseSetId);
	  this.setCanonicalCourseId(canonicalCourseId);
        }

	/**
	 * Return the simple primary key value that identifies this object.
	 * @return java.lang.Long
	 */
	public Long getCourseSetId()
	{
		return this.courseSetId;
	}
	
	/**
	 * Set the simple primary key value that identifies this object.
	 * @param coursesetid
	 */
	public void setCourseSetId(Long courseSetId)
	{
		this.courseSetId = courseSetId;
	}


	public Long getCanonicalCourseId()
	{
		return this.canonicalCourseId;
	}
	
	public void setCanonicalCourseId(Long canonicalCourseId)
	{
		this.canonicalCourseId = canonicalCourseId;
	}

    public boolean equals(Object o){
	boolean returnValue = false;
	if (this == o)
	    returnValue = true;
	if (o != null && o.getClass()==this.getClass()){
	    CanonicalCourseSet ccs = (CanonicalCourseSet)o;
	    if ((getCanonicalCourseId()).equals(ccs.getCanonicalCourseId())
		&& (getCourseSetId()).equals(ccs.getCourseSetId()))
		returnValue = true;
	}
	return returnValue;
    }

    public int hashCode(){
	String s = courseSetId.toString()+":"+canonicalCourseId.toString();
	return (s.hashCode());
    }
	
}
