/**********************************************************************************
 *
 * $Revision$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/

package org.sakaiproject.tool.content;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.cover.SqlService;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;

public class CollectionUtil
{
  private static final Log LOG = LogFactory.getLog(CollectionUtil.class);
  private static ResourceBundle rb = ResourceBundle.getBundle("content");

  /**
   * Using two arg version of concat to be compatible w/all db. 
   * MySql supports n arguments while Oracle, HSQLDB suport only 2 arg version
   */
  private static final String sql = "select ss.site_id, ss.title, cc.collection_id, sstool.registration "
      + "from sakai_site_tool sstool, sakai_site_user ssuser, "
      + "sakai_site ss, content_collection cc "
      + "where (sstool.registration = 'sakai.resources' "
      + "or sstool.registration = 'sakai.dropbox') "
      + "and sstool.site_id = ssuser.site_id "
      + "and sstool.site_id = ss.site_id "
      + "and ssuser.user_id = ? "
      + "and ("
      + "cc.collection_id = concat(concat('/group/', ss.site_id),'/') or "
      + "cc.collection_id = concat(concat('/user/', ?),'/') or "
      + "cc.collection_id = concat(concat('/attachment/', ?),'/') or "
      + "cc.collection_id = concat(concat('/group-user/', ?),'/') or "
      + "cc.collection_id = concat(concat('/public/', ?),'/')"
      + ") order by ss.title";

  private static final SqlReader sr = new SqlReader()
  {
    public Object readSqlResultRecord(ResultSet result)
    {
      try
      {
        List list = new ArrayList(2);
                
        String registration = result.getString("registration");
        String context = result.getString("site_id");
        
        if ("sakai.dropbox".equals(registration)){
          list.add(Dropbox.getCollection(context));
          list.add(result.getString("title") + " "
              + rb.getString("gen.drop"));
        }
        else{
          list.add(ContentHostingService.getSiteCollection(context));
          list.add(result.getString("title") + " "
              + rb.getString("gen.reso"));
        }                
        return list;
        
      }
      catch (Throwable t)
      {
        LOG.warn("Sql.dbRead: sql: " + sql + t);
      }
      return null;
    }
  };

  static Map getCollectionMap()
  {
    // create SqlReader
    String userId = UsageSessionService.getSessionUserId().trim();
    Object[] fields = new Object[] { userId, userId, userId, userId, userId};
    List collectionList = SqlService.dbRead(sql, fields, sr);
           
    Map collectionMap = new LinkedHashMap(collectionList.size());
    for (Iterator i = collectionList.iterator(); i.hasNext();)
    {
      List arrayList = (List) i.next();
      collectionMap.put(arrayList.get(1), arrayList.get(0)); // title, collection_id
    }
    return collectionMap;
  }
}