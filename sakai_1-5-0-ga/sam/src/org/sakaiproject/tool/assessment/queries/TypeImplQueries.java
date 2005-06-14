package org.sakaiproject.tool.assessment.queries;

import org.apache.log4j.Category;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.HibernateTemplate;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import org.navigoproject.osid.shared.impl.TypeImpl;
import org.navigoproject.osid.shared.impl.TypeData;
import org.navigoproject.osid.shared.impl.StanfordType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class TypeImplQueries extends HibernateDaoSupport{

  static private TypeImplQueries typeQueriesInstance = null;
  static Category errorLogger = Category.getInstance("errorLogger");
  private HashMap typeImplMap = new HashMap();
  private HashMap stanfordTypeMap = new HashMap();

  /**
   * default constructor
   */
  public TypeImplQueries() {
  }

  /**
   * set the typeImplMap for TypeImplQueries
   */
  public void setTypeImplMap(){
    this.typeImplMap = getMapForAllTypes();
    this.stanfordTypeMap = getMapForAllStanfordTypes();
  }

  /**
   * get the typeImplMap
   */
  public HashMap getTypeImplMap(){
    return this.typeImplMap;
  }

  public HashMap getStanfordTypeMap(){
    return this.stanfordTypeMap;
  }

  /**
   * This method returns the TypeImpl with the specified typeId found
   * in the typeImplMap that lives in cache.
   * @param typeId
   * @return TypeImpl
   */
  public TypeImpl getTypeImplById(String typeId) {
    TypeImpl typeImpl = (TypeImpl)typeImplMap.get(typeId);
    return typeImpl;
  }

  public TypeImpl getTypeImplByStanfordId(Long typeId) {
    TypeImpl typeImpl= (TypeImpl)stanfordTypeMap.get(typeId);
    return typeImpl;
  }

  /**
   * This method return an ArrayList (Long typeId, TypeImpl typeImpl)
   * with the specified authority and domain.
   * @param authority
   * @param domain
   * @return ArrayList
   */
  public ArrayList getArrayListByAuthorityDomain(String authority, String domain) {
    List typeList = getListByAuthorityDomain(authority, domain);
    ArrayList typeImplList = new ArrayList();
    for (int i = 0; i < typeList.size(); i++) {
      TypeData typeData = (TypeData) typeList.get(i);
      TypeImpl typeImpl = new TypeImpl(typeData.getDomain(),
				       typeData.getAuthority(),
				       typeData.getKeyword(),
				       typeData.getDescription());
      typeImplList.add(typeImpl);
    }
    return typeImplList;
  }

  /**
   * This method returns a Hashmap (Long typeId, TypeImpl typeImpl)
   * with the specified authority and domain.
   * @param authority
   * @param domain
   * @return HashMap
   */
  public HashMap getHashMapByAuthorityDomain(String authority, String domain) {
    List typeList = getListByAuthorityDomain(authority, domain);
    return createTypeMapById(typeList);
  }

  /**
   * fetch a list of TypeImpl from the DB or cache (Hibernate decides)
   * @return a list of TypeImpl
   */
  private List getAllTypes() {
    HibernateTemplate hibernateTemplate = getHibernateTemplate();
    return hibernateTemplate.find("from TypeData");
  }

  private List getAllStanfordTypes() {
    HibernateTemplate hibernateTemplate = getHibernateTemplate();
    return hibernateTemplate.find("from StanfordType");
  }

  /**
   * This method returns a HashMap (Long typeId, TypeImpl typeImpl)
   * containing all the TypeImpl available
   * @return HashMap
   */
  private HashMap getMapForAllTypes() {
    HashMap typeMap = new HashMap();
    List typeList = getAllTypes();
    return createTypeMapById(typeList);
  }

  private HashMap getMapForAllStanfordTypes() {
    HashMap typeMap = new HashMap();
    List typeList = getAllStanfordTypes();
    return createTypeMapByStanfordId(typeList);
  }

  /**
   * This method constructs a HashMap (String typeId, TypeImpl typeImpl) with
   * the items in typeList
   * @param typeList
   * @return a HashMap
   */
  private HashMap createTypeMapById(List typeList){
    HashMap typeImplMap = new HashMap();
    for (int i = 0; i < typeList.size(); i++) {
      TypeData typeData = (TypeData) typeList.get(i);
      TypeImpl typeImpl = new TypeImpl(typeData.getDomain(),
				       typeData.getAuthority(),
				       typeData.getKeyword(),
				       typeData.getDescription());
      typeImplMap.put(typeData.getTypeId(), typeImpl);
      System.out.println("typeImpl #" + typeImpl.getTypeId() + " keyword= " +
                         typeImpl.getKeyword());
    }
    return typeImplMap;
  }

  /**
   * This method constructs a HashMap (Long typeId, TypeImpl typeImpl) with
   * the items in typeList
   * @param typeList
   * @return a HashMap
   */
  private HashMap createTypeMapByStanfordId(List typeList){
    HashMap stanfordTypeMap = new HashMap();
    for (int i = 0; i < typeList.size(); i++) {
      StanfordType stanfordType = (StanfordType) typeList.get(i);
      TypeImpl typeImpl = new TypeImpl(stanfordType.getDomain(),
				      stanfordType.getAuthority(),
				      stanfordType.getKeyword(),
				      stanfordType.getDescription());
      typeImpl.setTypeId(stanfordType.getTypeId().toString());
      stanfordTypeMap.put(stanfordType.getTypeId(), typeImpl);
      System.out.println("stanfordType #" + typeImpl.getTypeId() + " keyword= " +
                         typeImpl.getKeyword());
    }
    return stanfordTypeMap;
  }

  /**
   * This method return a List of TypeImpl from DB or cache (Hibernate decides)
   * with the specified authority & domain
   * @param authority
   * @param domain
   * @return List
   */
  private List getListByAuthorityDomain(String authority, String domain) {
    HibernateTemplate hibernateTemplate = getHibernateTemplate();
    return hibernateTemplate.find(
        "from TypeData as t where t.authority=? and t.domain=?",
        new Object[] { authority, domain },
        new net.sf.hibernate.type.Type[]{ Hibernate.STRING, Hibernate.STRING });
  }
}
