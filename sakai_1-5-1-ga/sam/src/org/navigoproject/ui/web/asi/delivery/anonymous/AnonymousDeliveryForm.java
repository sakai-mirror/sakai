package org.navigoproject.ui.web.asi.delivery.anonymous;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author lance
 */
public class AnonymousDeliveryForm extends ActionForm
{

  private String id;

  /**
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.http.HttpServletRequest)
   */
  public void reset(ActionMapping mapping, HttpServletRequest request)
  {
    super.reset(mapping, request);
    id = null;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return "{id=" + id + "}";
  }

  /**
   * @return Returns the id.
   */
  public String getId()
  {
    return id;
  }

  /**
   * @param id
   *          The id to set.
   */
  public void setId(String id)
  {
    this.id = id;
  }
}