package org.sakaiproject.tool.assessment.facade;

import org.sakaiproject.tool.assessment.data.dao.shared.TypeD;
import org.sakaiproject.tool.assessment.data.ifc.shared.TypeIfc;
import org.sakaiproject.tool.assessment.facade.DataFacadeException;
import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
//import org.sakaiproject.tool.assessment.service.PersistenceService;
import org.navigoproject.osid.impl.PersistenceService;
import org.osid.assessment.AssessmentException;
import org.osid.shared.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.FileOutputStream;
import org.apache.log4j.Logger;

//import org.apache.log4j.*;
import org.sakaiproject.tool.assessment.osid.shared.extension.TypeExtension;
import org.sakaiproject.tool.assessment.util.HibernateUtil;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import net.sf.hibernate.Hibernate;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


public class TypeFacadeQueries extends HibernateDaoSupport{

  private HashMap typeFacadeMap;
  private List itemTypes;

  public TypeFacadeQueries() {
  }

    /**
     * set the typeFacadeMap for TypeFacadeQueries
     */
    public void setTypeFacadeMap(){
	this.typeFacadeMap = getMapForAllTypes();
    }

    /**
     * get the typeFacadeMap
     */
    public HashMap getTypeFacadeMap(){
	return this.typeFacadeMap;
    }

    /**
     * This method returns the TypeFacade with the specified typeId found
     * in the typeFacadeMap that lives in cache.
     * @param typeId
     * @return TypeFacade
     */
    public TypeFacade getTypeFacadeById(Long typeId) {
	TypeFacade typeFacade = null;
	HashMap typeMap = getTypeFacadeMap();
	typeFacade = (TypeFacade)typeMap.get(typeId);
	return typeFacade;
    }

    /**
     * This method return Type with a specified typeId, used by
     * ItemFacade.getItemType()
     * @param typeId
     * @return org.osid.shared.Type
     */
    public Type getTypeById(Long typeId){
	TypeFacade typeFacade = getTypeFacadeById(typeId);
	TypeExtension type = new TypeExtension(typeFacade.getAuthority(),
					       typeFacade.getDomain(),
					       typeFacade.getKeyword(),
					       typeFacade.getDescription());
	return type;
    }

    /**
     * This method return an ArrayList (Long typeId, TypeFacade typeFacade)
     * with the specified authority and domain.
     * @param authority
     * @param domain
     * @return ArrayList
     */
    public ArrayList getArrayListByAuthorityDomain(String authority, String domain) {
	List typeList = getListByAuthorityDomain(authority, domain);
	ArrayList typeFacadeList = new ArrayList();
	for (int i = 0; i < typeList.size(); i++) {
	    TypeD typeData = (TypeD) typeList.get(i);
	    TypeFacade typeFacade = new TypeFacade(typeData);
	    typeFacadeList.add(typeFacade);
	}
	return typeFacadeList;
    }

    /**
     * This method returns a Hashmap (Long typeId, TypeFacade typeFacade)
     * with the specified authority and domain.
     * @param authority
     * @param domain
     * @return HashMap
     */
    public HashMap getHashMapByAuthorityDomain(String authority, String domain) {
	List typeList = getListByAuthorityDomain(authority, domain);
	return createTypeFacadeMapById(typeList);
    }

    /**
     * fetch a list of TypeD from the DB or cache (Hibernate decides)
     * @return a list of TypeD
     */
    private List getAllTypes() {
	return getHibernateTemplate().find("from TypeD");
    }

    /**
     * This method returns a HashMap (Long typeId, TypeFacade typeFacade)
     * containing all the TypeFacade available
     * @return HashMap
     */
    private HashMap getMapForAllTypes() {
	HashMap typeMap = new HashMap();
	List typeList = getAllTypes();
	return createTypeFacadeMapById(typeList);
    }

    /**
     * This method constructs a HashMap (Long typeId, TypeFacade typeFacade) with
     * the items in typeList
     * @param typeList
     * @return a HashMap
     */
    private HashMap createTypeFacadeMapById(List typeList){
	HashMap typeFacadeMap = new HashMap();
	for (int i = 0; i < typeList.size(); i++) {
	    TypeD typeData = (TypeD) typeList.get(i);
	    TypeFacade typeFacade = new TypeFacade(typeData);
	    typeFacadeMap.put(typeData.getTypeId(), typeFacade);
	    //System.out.println("Item #" + typeData.getTypeId() + " keyword= " +
	    //			       typeData.getKeyword());
	}
	return typeFacadeMap;
    }

    /**
     * This method return a List of TypeD from DB or cache (Hibernate decides)
     * with the specified authority & domain
     * @param authority
     * @param domain
     * @return List
     */
    public List getListByAuthorityDomain(String authority, String domain) {
        return getHibernateTemplate().find(
                                           "from TypeD as t where t.authority=? and t.domain=?",
                                           new Object[] { authority, domain },
                                           new net.sf.hibernate.type.Type[]{ Hibernate.STRING, Hibernate.STRING });
    }

    public List getFacadeListByAuthorityDomain(String authority, String domain) {
      ArrayList typeList = new ArrayList();
      List list =  getListByAuthorityDomain(authority, domain);
      for (int i=0; i<list.size();i++){
        TypeD type = (TypeD)list.get(i);
        TypeFacade f = new TypeFacade(type.getAuthority(), type.getDomain(),
                        type.getKeyword(), type.getDescription());
        f.setTypeId(type.getTypeId());
        typeList.add(f);
      }
      return (List)typeList;
    }

    public List getFacadeItemTypes() {
      if (this.itemTypes == null){
        //System.out.println("** item Types not null");
        this.itemTypes = getFacadeListByAuthorityDomain(TypeIfc.SITE_AUTHORITY, TypeIfc.DOMAIN_ASSESSMENT_ITEM);
      }
      return this.itemTypes;
    }

    public void setFacadeItemTypes() {
      this.itemTypes = getFacadeItemTypes();
    }
}
