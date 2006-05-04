package org.sakaiproject.component.common.edu.coursemanagement;

import java.io.Serializable;

public class CanonicalCourseSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private Long canonicalCourseSetId;
	private String courseSetUuid;
	private String canonicalCourseUuid;
	
        public CanonicalCourseSet(){}

        public CanonicalCourseSet(String courseSetUuid, String canonicalCourseUuid){
	  this.setCourseSetUuid(courseSetUuid);
	  this.setCanonicalCourseUuid(canonicalCourseUuid);
        }

    public Long getCanonicalCourseSetId()
    {
	return this.canonicalCourseSetId;
    }

    public void setCanonicalCourseSetId(Long canonicalCourseSetId)
    {
	this.canonicalCourseSetId = canonicalCourseSetId;
    }


	/**
	 * Return the simple primary key value that identifies this object.
	 * @return java.lang.String
	 */
	public String getCourseSetUuid()
	{
		return this.courseSetUuid;
	}
	
	/**
	 * Set the simple primary key value that identifies this object.
	 * @param courseSetUuid
	 */
	public void setCourseSetUuid(String courseSetUuid)
	{
		this.courseSetUuid = courseSetUuid;
	}


	public String getCanonicalCourseUuid()
	{
		return this.canonicalCourseUuid;
	}
	
	public void setCanonicalCourseUuid(String canonicalCourseUuid)
	{
		this.canonicalCourseUuid = canonicalCourseUuid;
	}

    public boolean equals(Object o){
	boolean returnValue = false;
	if (this == o)
	    returnValue = true;
	if (o != null && o.getClass()==this.getClass()){
	    CanonicalCourseSet ccs = (CanonicalCourseSet)o;
	    if ((getCanonicalCourseUuid()).equals(ccs.getCanonicalCourseUuid())
		&& (getCourseSetUuid()).equals(ccs.getCourseSetUuid()))
		returnValue = true;
	}
	return returnValue;
    }

    public int hashCode(){
	String s = courseSetUuid+":"+canonicalCourseUuid;
	return (s.hashCode());
    }
	
}
