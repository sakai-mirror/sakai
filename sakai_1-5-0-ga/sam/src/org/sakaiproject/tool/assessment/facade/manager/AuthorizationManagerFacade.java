package org.sakaiproject.tool.assessment.facade.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Collection;
import java.util.Iterator;

import org.sakaiproject.tool.assessment.facade.DataFacadeException;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.facade.TypeIteratorFacade;
import org.sakaiproject.tool.assessment.facade.authz.FunctionFacade;
import org.sakaiproject.tool.assessment.facade.authz.FunctionIteratorFacade;
import org.sakaiproject.tool.assessment.facade.authz.QualifierFacade;
import org.sakaiproject.tool.assessment.facade.authz.QualifierIteratorFacade;
import org.sakaiproject.tool.assessment.facade.authz.AuthorizationFacade;
import org.sakaiproject.tool.assessment.facade.authz.AuthorizationIteratorFacade;
import org.sakai.osid.shared.impl.TypeLib;
import org.sakai.osid.authz.impl.Authorization;
import org.sakai.osid.authz.impl.AuthorizationManager;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.data.IdHelper;
import org.sakai.osid.authz.impl.Function;
import org.sakai.osid.authz.impl.Qualifier;

import osid.OsidException;
import osid.authorization.AuthorizationException;
import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedManager;
import osid.shared.Type;
import osid.shared.*;
import osid.authorization.FunctionIterator;
import osid.authorization.QualifierIterator;
import osid.authorization.AuthorizationIterator;

public class AuthorizationManagerFacade {
  private final static org.apache.log4j.Logger LOG =
      org.apache.log4j.Logger.getLogger(AuthorizationManagerFacade.class);

  public AuthorizationFacade createDatedAuthorization(
      String agentIdString, String functionIdString, String qualifierIdString,
      long effectiveDateInMs, long expirationDateInMs) throws
      DataFacadeException {
    AuthorizationFacade authzFacade = null;
    try {
      Id agentId = IdHelper.stringToId(agentIdString);
      Id functionId = IdHelper.stringToId(functionIdString);
      Id qualifierId = IdHelper.stringToId(qualifierIdString);
      // need effectiveDate in java.util.Calendar
      Calendar effectiveDate = getDateInCalendar(effectiveDateInMs);
      // need expirationDate in java.util.Calendar
      Calendar expirationDate = getDateInCalendar(expirationDateInMs);
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      Authorization authz = (Authorization) authzManager.
          createDatedAuthorization(
          agentId, functionId, qualifierId, effectiveDate,
          expirationDate);
      authzFacade = getAuthorizationFacade(authz);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return authzFacade;
  }

  public AuthorizationFacade createAuthorization(
      String agentIdString, String functionIdString, String qualifierIdString) throws
      DataFacadeException {
    AuthorizationFacade authzFacade = null;
    try {
      Id agentId = IdHelper.stringToId(agentIdString);
      Id functionId = IdHelper.stringToId(functionIdString);
      Id qualifierId = IdHelper.stringToId(qualifierIdString);
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      Authorization authz = (Authorization) authzManager.createAuthorization(
          agentId, functionId, qualifierId);
      authzFacade = getAuthorizationFacade(authz);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return authzFacade;

  }

  public FunctionFacade createFunction(
      String functionIdString, String displayName, String description,
      TypeFacade functionTypeFacade, String currentlyUnusedHierarchyParameter)
      throws  DataFacadeException {
    FunctionFacade functionFacade = null;
    try {
      Id functionId = IdHelper.stringToId(functionIdString);
      Type functionType = org.navigoproject.osid.TypeLib.
          FUNCTION_ACL_HTTP_REQUEST;
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      Function function = (Function) authzManager.createFunction(
          functionId, displayName, description,
          functionType, null);
      functionFacade = getFunctionFacade(function);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return functionFacade;
  }

  public QualifierFacade createRootQualifier(
      String qualifierIdString, String displayName, String description,
      TypeFacade qualifierType, Id unusedHierarchyId) throws
      DataFacadeException {
    QualifierFacade qualifierFacade = null;
    try {
      Id qualifierId = IdHelper.stringToId(qualifierIdString);
      Type qualifierType0 = TypeLib.DR_QTI_ASSESSMENT_PUBLISHED;
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      Qualifier qualifier = (Qualifier) authzManager.createRootQualifier(
          qualifierId, displayName, description,
          qualifierType0, null);
      qualifierFacade = getQualifierFacade(qualifier);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return qualifierFacade;
  }

  public QualifierFacade createQualifier(
      String qualifierIdString, String displayName, String description,
      TypeFacade qualifierType, String parentIdString)
      throws  DataFacadeException {
    QualifierFacade qualifierFacade = null;
    try {
      Id qualifierId = IdHelper.stringToId(qualifierIdString);
      Id parentId = IdHelper.stringToId(parentIdString);
      Type qualifierType0 = TypeLib.DR_QTI_ASSESSMENT_PUBLISHED;
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      Qualifier qualifier = (Qualifier) authzManager.createQualifier(
          qualifierId, displayName, description,
          qualifierType0, parentId);
      qualifierFacade = getQualifierFacade(qualifier);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return qualifierFacade;
  }

  public void deleteAuthorization(String authorizationIdString) throws
      DataFacadeException {
    try {
      Id authorizationId = IdHelper.stringToId(authorizationIdString);
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      authzManager.deleteAuthorization(authorizationId);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
  }

  public void deleteFunction(String functionIdString) throws
      DataFacadeException {
    try {
      Id functionId = IdHelper.stringToId(functionIdString);
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      authzManager.deleteFunction(functionId);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
  }

  public void deleteQualifier(String qualifierIdString) throws
      DataFacadeException {
    try {
      Id qualifierId = IdHelper.stringToId(qualifierIdString);
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      authzManager.deleteQualifier(qualifierId);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
  }

  public boolean isAuthorized(String agentIdString, String functionIdString,
                              String qualifierIdString)
      throws DataFacadeException {
    boolean returnValue = false;
    try {
      Id agentId = IdHelper.stringToId(agentIdString);
      Id functionId = IdHelper.stringToId(functionIdString);
      Id qualifierId = IdHelper.stringToId(qualifierIdString);
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      returnValue = authzManager.isAuthorized(agentId, functionId, qualifierId);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return returnValue;
  }

  public TypeIteratorFacade getFunctionTypes() throws DataFacadeException {
    TypeIteratorFacade typeIteratorFacade = null;
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      TypeIterator typeIterator = authzManager.getFunctionTypes();
      typeIteratorFacade = getTypeIteratorFacade(typeIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return typeIteratorFacade;
  }

  public FunctionIteratorFacade getFunctions(Type functionType)
      throws DataFacadeException {
    FunctionIteratorFacade functionIteratorFacade = null;
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      FunctionIterator functionIterator = authzManager.getFunctions(functionType);
      functionIteratorFacade = getFunctionIteratorFacade(functionIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return functionIteratorFacade;
  }

  public boolean agentExists(String agentIdString)
      throws DataFacadeException {
    boolean returnValue = false;
    Id agentId = IdHelper.stringToId(agentIdString);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      returnValue = authzManager.agentExists(agentId);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return returnValue;

  }

  public TypeIteratorFacade getQualifierTypes()
      throws DataFacadeException {
    TypeIteratorFacade typeIteratorFacade = null;
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      TypeIterator typeIterator = authzManager.getQualifierTypes();
      typeIteratorFacade = getTypeIteratorFacade(typeIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return typeIteratorFacade;

  }

  public QualifierIteratorFacade getRootQualifiers(
      String qualifierHierarchyIdString)
      throws DataFacadeException {
    QualifierIteratorFacade qualifierIteratorFacade = null;
    Id qualifierHierarchyId = IdHelper.stringToId(qualifierHierarchyIdString);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      QualifierIterator qualifierIterator = authzManager.getRootQualifiers(qualifierHierarchyId);
      qualifierIteratorFacade = getQualifierIteratorFacade(qualifierIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return qualifierIteratorFacade;
  }

  public QualifierIteratorFacade getQualifierChildren(
      String qualifierIdString)
      throws DataFacadeException {
    QualifierIteratorFacade qualifierIteratorFacade = null;
    Id qualifierId = IdHelper.stringToId(qualifierIdString);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      QualifierIterator qualifierIterator = authzManager.getQualifierChildren(qualifierId);
      qualifierIteratorFacade = getQualifierIteratorFacade(qualifierIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return qualifierIteratorFacade;
  }

  public QualifierIteratorFacade getQualifierDescendants(
      String qualifierIdString)
      throws DataFacadeException {
    QualifierIteratorFacade qualifierIteratorFacade = null;
    Id qualifierId = IdHelper.stringToId(qualifierIdString);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      QualifierIterator qualifierIterator = authzManager.getQualifierDescendants(qualifierId);
      qualifierIteratorFacade = getQualifierIteratorFacade(qualifierIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return qualifierIteratorFacade;
  }

  public FunctionFacade getFunction(String functionIdString)
      throws  DataFacadeException {
    FunctionFacade functionFacade = null;
    try {
      Id functionId = IdHelper.stringToId(functionIdString);
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      Function function = (Function) authzManager.getFunction(
          functionId);
      functionFacade = getFunctionFacade(function);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return functionFacade;
  }

  public QualifierFacade getQualifier(String qualifierIdString)
      throws  DataFacadeException {
    QualifierFacade qualifierFacade = null;
    try {
      Id qualifierId = IdHelper.stringToId(qualifierIdString);
      Type qualifierType0 = TypeLib.DR_QTI_ASSESSMENT_PUBLISHED;
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      Qualifier qualifier = (Qualifier) authzManager.getQualifier(qualifierId);
      qualifierFacade = getQualifierFacade(qualifier);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return qualifierFacade;
  }

  public osid.shared.AgentIterator getWhoCanDo(
      Id functionId, Id qualifier, boolean isActiveNow) throws osid.
      authorization.AuthorizationException {
    throw new AuthorizationException(OsidException.UNIMPLEMENTED);
  }

  public AuthorizationIteratorFacade getExplicitAZs(
      String agentIdString, String functionIdString, String qualifierIdString, Boolean isActiveNowBoolean)
      throws DataFacadeException {
    AuthorizationIteratorFacade authorizationIteratorFacade = null;
    Id agentId = IdHelper.stringToId(agentIdString);
    Id functionId = IdHelper.stringToId(functionIdString);
    Id qualifierId = IdHelper.stringToId(qualifierIdString);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      AuthorizationIterator authorizationIterator = authzManager.getExplicitAZs(
          agentId, functionId, qualifierId, isActiveNowBoolean.booleanValue());
      authorizationIteratorFacade = getAuthorizationIteratorFacade(authorizationIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return authorizationIteratorFacade;
  }

  public osid.shared.IdIterator getQualifierHierarchies() throws
      AuthorizationException {
    throw new AuthorizationException(OsidException.UNIMPLEMENTED);
  }

  public boolean isUserAuthorized(String functionIdString, String qualifierIdString)
      throws DataFacadeException {
    boolean returnValue = false;
    try {
      Id functionId = IdHelper.stringToId(functionIdString);
      Id qualifierId = IdHelper.stringToId(qualifierIdString);
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      returnValue = authzManager.isUserAuthorized(functionId, qualifierId);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return returnValue;
  }

  public AuthorizationIteratorFacade getAllAZsByFuncType(
      String agentIdString, TypeFacade functionTypeFacade, String qualifierIdString,
      Boolean isActiveNowBoolean)
      throws DataFacadeException {
    AuthorizationIteratorFacade authorizationIteratorFacade = null;
    Id agentId = IdHelper.stringToId(agentIdString);
    Type functionType = org.navigoproject.osid.TypeLib.
        FUNCTION_ACL_HTTP_REQUEST;
    Id qualifierId = IdHelper.stringToId(qualifierIdString);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      AuthorizationIterator authorizationIterator = authzManager.getAllAZsByFuncType(
          agentId, functionType, qualifierId, isActiveNowBoolean.booleanValue());
      authorizationIteratorFacade = getAuthorizationIteratorFacade(authorizationIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return authorizationIteratorFacade;
  }

  public AuthorizationIteratorFacade getAllUserAZs(
      String arg0, String arg1, boolean arg2)
      throws DataFacadeException {
    AuthorizationIteratorFacade authorizationIteratorFacade = null;
    Id arg0Id = IdHelper.stringToId(arg0);
    Id arg1Id = IdHelper.stringToId(arg1);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      AuthorizationIterator authorizationIterator = authzManager.getAllUserAZs(
        arg0Id, arg1Id, arg2);
      authorizationIteratorFacade = getAuthorizationIteratorFacade(authorizationIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return authorizationIteratorFacade;
  }

  public AuthorizationIteratorFacade getAllUserAZsByFuncType(
      TypeFacade arg0, String arg1, boolean arg2)
      throws DataFacadeException {
    AuthorizationIteratorFacade authorizationIteratorFacade = null;
    Type functionType = org.navigoproject.osid.TypeLib.
        FUNCTION_ACL_HTTP_REQUEST;
    Id arg1Id = IdHelper.stringToId(arg1);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      AuthorizationIterator authorizationIterator = authzManager.getAllUserAZsByFuncType(
        functionType, arg1Id, arg2);
      authorizationIteratorFacade = getAuthorizationIteratorFacade(authorizationIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return authorizationIteratorFacade;
  }

  public AuthorizationIteratorFacade getExplicitUserAZs(
      String functionIdString, String qualifierIdString, Boolean isActiveNowBoolean)
      throws DataFacadeException {
    AuthorizationIteratorFacade authorizationIteratorFacade = null;
    Id functionId = IdHelper.stringToId(functionIdString);
    Id qualifierId = IdHelper.stringToId(qualifierIdString);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      AuthorizationIterator authorizationIterator = authzManager.getExplicitUserAZs(
          functionId, qualifierId, isActiveNowBoolean.booleanValue());
      authorizationIteratorFacade = getAuthorizationIteratorFacade(authorizationIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return authorizationIteratorFacade;

  }

  /* the foloowing three unimplemented methods, I am not sure what to do with it
   * 'cos I can't tell what the paremeters represent so I can't wrap them.
   * - daisyf 10/25/04
   */
  public osid.authorization.AuthorizationIterator getExplicitAZs(
      Id arg0, Type arg1, Id arg2, boolean arg3)
      throws AuthorizationException {
    // TODO Auto-generated method stub
    throw new AuthorizationException(OsidException.UNIMPLEMENTED);
  }

  public osid.authorization.AuthorizationIterator getExplicitAZsByFuncType(
      Id agentId, Type arg1, Id arg2, boolean arg3) throws
      AuthorizationException {
    // TODO Auto-generated method stub
    throw new AuthorizationException(OsidException.UNIMPLEMENTED);
  }

  public osid.authorization.AuthorizationIterator getExplicitUserAZsByFuncType(
      Type arg0, Id arg1, boolean arg2) throws AuthorizationException {
    // TODO Auto-generated method stub
    throw new AuthorizationException(OsidException.UNIMPLEMENTED);
  }

  /* (non-Javadoc)
   * @see osid.authorization.AuthorizationManager#getAllAZs(osid.shared.Id, osid.shared.Type, osid.shared.Id, boolean)
   */
  public AuthorizationIteratorFacade getAllAZs(
      String agentIdString, String functionIdString,
      String qualifierIdString, Boolean isActiveNowBoolean)
      throws DataFacadeException {
    AuthorizationIteratorFacade authorizationIteratorFacade = null;
    Id agentId = IdHelper.stringToId(agentIdString);
    Id functionId = IdHelper.stringToId(functionIdString);
    Id qualifierId = IdHelper.stringToId(qualifierIdString);
    try {
      AuthorizationManager authzManager = (AuthorizationManager)
          OsidManagerFactory.createAuthorizationManager();
      AuthorizationIterator authorizationIterator = authzManager.getAllAZs(
          agentId, functionId, qualifierId, isActiveNowBoolean.booleanValue());
      authorizationIteratorFacade = getAuthorizationIteratorFacade(authorizationIterator);
    }
    catch (Exception e) {
      throw new DataFacadeException(e.getMessage());
    }
    return authorizationIteratorFacade;
  }

  private Calendar getDateInCalendar(long ms) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(ms));
    return cal;
  }

  private FunctionFacade getFunctionFacade(Function function) {
    FunctionFacade functionFacade = null;
    try {
      functionFacade = new FunctionFacade(
          function.getDisplayName(), function.getDisplayName(),
          function.getDescription(), "FUNCTION_ACL_HTTP_REQUEST");
    }
    catch (AuthorizationException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return functionFacade;
  }

  private QualifierFacade getQualifierFacade(Qualifier qualifier)
  {
    QualifierFacade qualifierFacade = null;
    try {
      qualifierFacade = new QualifierFacade(
//          qualifier.getId().getIdString(),
          new Long(qualifier.getId().getIdString()).longValue(),
          qualifier.getDisplayName(), qualifier.getDisplayName(),
          qualifier.getDescription(), "DR_QTI_ASSESSMENT_PUBLISHED");
    }
    catch (AuthorizationException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    catch (SharedException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return qualifierFacade;
  }

  private AuthorizationFacade getAuthorizationFacade(Authorization authz)
  {
    AuthorizationFacade authzFacade = null;
    try {
      authzFacade = new AuthorizationFacade(
          authz.getAgent().getId().getIdString(),
          authz.getFunction().getId().getIdString(),
          authz.getQualifier().getId().getIdString(),
          authz.getEffectiveDate().getTime(), authz.getExpirationDate().getTime(),
          authz.getModifiedBy().getId().getIdString(),
          authz.getModifiedDate().getTime(),
          new Boolean(authz.isExplicit()));
    }
    catch (AuthorizationException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    catch (SharedException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return authzFacade;
  }

  private TypeIteratorFacade getTypeIteratorFacade(TypeIterator typeIterator)
  {
    ArrayList a = new ArrayList();
    try {
      while (typeIterator.hasNext()) {
        Type t = (Type)typeIterator.next();
        TypeFacade typeFacade = new TypeFacade(
            t.getAuthority(),t.getDomain(), t.getKeyword());
        //typeFacade.setTypeId();
        a.add(typeFacade);
      }
    }
    catch (SharedException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return new TypeIteratorFacade(a);
  }

  private FunctionIteratorFacade getFunctionIteratorFacade(FunctionIterator functionIterator)
  {
    ArrayList a = new ArrayList();
    try {
      while (functionIterator.hasNext()) {
        Function f = (Function)functionIterator.next();
        FunctionFacade functionFacade = new FunctionFacade(
            f.getDisplayName(), f.getDisplayName(),
            f.getDescription(), f.getFunctionType().toString());
        //typeFacade.setTypeId();
        a.add(functionFacade);
      }
    }
    catch (AuthorizationException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return new FunctionIteratorFacade(a);
  }

  private QualifierIteratorFacade getQualifierIteratorFacade(QualifierIterator qualifierIterator)
  {
    ArrayList a = new ArrayList();
    try {
      while (qualifierIterator.hasNext()) {
        Qualifier q = (Qualifier)qualifierIterator.next();
        QualifierFacade qualifierFacade = new QualifierFacade(
//            q.getId().getIdString(), q.getDisplayName(), q.getDisplayName(),
            new Long(q.getId().getIdString()).longValue(), q.getDisplayName(), q.getDisplayName(),            
            q.getDescription(), q.getQualifierType().toString());
        //typeFacade.setTypeId();
        a.add(qualifierFacade);
      }
    }
    catch (SharedException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    catch (AuthorizationException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return new QualifierIteratorFacade(a);
  }

  private AuthorizationIteratorFacade getAuthorizationIteratorFacade(AuthorizationIterator authorizationIterator)
  {
    ArrayList a = new ArrayList();
    try {
      while (authorizationIterator.hasNext()) {
        Authorization authz = (Authorization)authorizationIterator.next();
        AuthorizationFacade authzFacade = new AuthorizationFacade(
            authz.getAgent().getId().getIdString(),
            authz.getFunction().getId().getIdString(),
            authz.getQualifier().getId().getIdString(),
            authz.getEffectiveDate().getTime(), authz.getExpirationDate().getTime(),
            authz.getModifiedBy().getId().getIdString(),
            authz.getModifiedDate().getTime(),
            new Boolean(authz.isExplicit()));
        a.add(authzFacade);
      }
    }
    catch (SharedException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    catch (AuthorizationException ex) {
      throw new DataFacadeException(ex.getMessage());
    }
    return new AuthorizationIteratorFacade(a);
  }

}
