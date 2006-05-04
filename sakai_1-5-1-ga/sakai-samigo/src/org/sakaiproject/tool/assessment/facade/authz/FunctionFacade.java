package org.sakaiproject.tool.assessment.facade.authz;

import org.sakaiproject.tool.assessment.facade.DataFacadeException;
import org.sakaiproject.tool.assessment.facade.TypeFacadeQueries;
import org.sakaiproject.tool.assessment.data.dao.authz.FunctionData;
import org.sakaiproject.tool.assessment.data.ifc.authz.FunctionIfc;
import org.osid.authorization.Function;
import org.osid.shared.Id;
import org.osid.shared.Type;
//import org.sakaiproject.tool.assessment.service.PersistenceService;
import org.navigoproject.osid.impl.PersistenceService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class FunctionFacade implements FunctionIfc {
  private Id id;
  private String referenceName;
  private String description;
  private Type functionType;
  private FunctionIfc data;
//  private String functionId;
  private long functionId;
  private String functionTypeId;
  private String displayName;

  public FunctionFacade() {
    this.data = new FunctionData(); //should do a create method later
  }

  public FunctionFacade(String referenceName, String displayName, String description,
                      String functionTypeId) {
    this.data = new FunctionData(); //should do a create method later
    setReferenceName(referenceName);
    setDisplayName(displayName);
    setDescription(description);
    setFunctionTypeId(functionTypeId);
  }

  public FunctionFacade(FunctionIfc data){
    this.data = data;
    this.id = getId();
    this.referenceName = data.getDisplayName();
    this.description = data.getDescription();
    this.functionTypeId = data.getFunctionTypeId();
    this.functionType = getFunctionType();
    this.displayName = data.getDisplayName();
  }

  public Id getId(){
    return id;
  }
  public String getReferenceName() {
    return getDisplayName();
  }
  public void setReferenceName(String referenceName)
  {
    this.referenceName = referenceName;
    setDisplayName(referenceName);
  }

  public Type getFunctionType(){
    TypeFacadeQueries typeFacadeQueries = PersistenceService.getInstance().getTypeFacadeQueries();
    return typeFacadeQueries.getTypeById(new Long(this.data.getFunctionTypeId()));
  }

  public String getDescription(){
    return data.getDescription();
  }

  public void updateDescription(String description){
    setDescription(description);
  }

  public void setDescription(String description)
  {
    this.description = description;
    data.setDescription(description);
  }

  public FunctionIfc getData(){
    return this.data;
  }

//  public String getFunctionId()
  public long getFunctionId()
  {
    return data.getFunctionId();
  }

//  public void setFunctionId(String id)
  public void setFunctionId(long id)
  {
    this.functionId = id;
    data.setFunctionId(id);
  }

  public String getDisplayName() {
    return data.getDisplayName();
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
    data.setDisplayName(displayName);
  }

  public String getFunctionTypeId()
  {
    return data.getFunctionTypeId();
  }

  public void setFunctionTypeId(String id)
  {
    this.functionTypeId = id;
    data.setFunctionTypeId(id);
  }


}