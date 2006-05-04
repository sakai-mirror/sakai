/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2006 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.metaobj.shared.mgt.impl;

import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;
import org.sakaiproject.metaobj.shared.mgt.MetaobjEntityManager;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.sakaiproject.service.legacy.entity.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 21, 2006
 * Time: 1:23:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetaobjEntityProducer extends EntityProducerBase {

   public String getLabel() {
      return MetaobjEntityManager.METAOBJ_ENTITY_PREFIX;
   }

   public void init() {
      getEntityManager().registerEntityProducer(this, Entity.SEPARATOR + MetaobjEntityManager.METAOBJ_ENTITY_PREFIX);
   }

   protected ReferenceParser parseReference(String wholeRef) {
      return new ReferenceParser(wholeRef, this, false);
   }

}
