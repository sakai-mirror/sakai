package org.sakaiproject.tool.assessment.data.dao.shared;
import java.util.Date;

/**
 * This describe a site type, in this case, we are subclass
 * it from TypeData.
 *
 * @version $Id: SiteTypeData.java,v 1.1 2004/10/04 16:57:38 daisyf.stanford.edu Exp $
 */
public class SiteTypeData extends TypeData
{
  public SiteTypeData(){
  }

  /**
   * Creates a new SiteType object.
   */
  public SiteTypeData(String authority, String domain, String keyword,
                  String description, int status,
                  String createdBy, Date createdDate,
                  String lastModifiedBy, Date lastModifiedDate
                  )
  {
    super(authority, domain, keyword, description,
          status, createdBy, createdDate,
          lastModifiedBy, lastModifiedDate);
  }

}
