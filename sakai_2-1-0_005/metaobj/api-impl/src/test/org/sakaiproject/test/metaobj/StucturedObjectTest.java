package org.sakaiproject.test.metaobj;

import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactValidationService;
import org.sakaiproject.metaobj.shared.mgt.FieldValueWrapperFactory;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.ElementListBean;
import org.sakaiproject.metaobj.shared.model.ValidationError;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import java.util.List;
import java.util.Iterator;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestResult;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 19, 2005
 * Time: 9:57:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class StucturedObjectTest extends AbstractDependencyInjectionSpringContextTests {

   private static final Log log = LogFactory.getLog(StucturedObjectTest.class);

   private SchemaFactory schemaFactory;
   private StructuredArtifactValidationService validator;

   protected String[] getConfigLocations() {
       String[] configLocations = {"spring-beans-test.xml"};
       return configLocations;
   }

   protected void onSetUp() throws Exception {
      super.onSetUp();
      schemaFactory = SchemaFactory.getInstance();
      validator = (StructuredArtifactValidationService) applicationContext.getBean(
            "org.sakaiproject.metaobj.shared.mgt.StructuredArtifactValidationService");
   }

   public void testElementValidationGood() throws Exception {
      ElementBean bean = getElementBean();

      bean.put("firstName", "John");
      bean.put("middle", "d");
      bean.put("lastName", "Ellis");

      bean.put("expires", new Date());

      ElementBean emails = (ElementBean)bean.get("emails");

      // can set a simple list (eachi item in list is a data node) with a string array
      emails.put("email", new String[]{
         "johnEllis@alumni.creighton.edu",
         "john.ellis@rsmart.com"});

      ElementListBean phoneList = (ElementListBean) bean.get("phoneNumber");

      // must create an element for each item in a complex list
      ElementBean phone1 = phoneList.createBlank();
      ElementBean phone2 = phoneList.createBlank();

      phone1.put("type", "Home");
      phone1.put("number", "1234567890"); // i don't think i'll put my real number here :)
      phone1.put("contact", "true");
      phoneList.add(phone1);

      phone2.put("type", "Cell");
      phone2.put("number", "987654321"); // i don't think i'll put my real number here :)
      phoneList.add(phone2);

      List errors = validator.validate(bean);

      if (errors.size() == 0) {
         log.error("No errors");
      }

      for (Iterator i=errors.iterator();i.hasNext();) {
         ValidationError error = (ValidationError)i.next();
         log.error("found error " + error.getDefaultMessage());
      }

      dumpBean(bean);
   }

   private void dumpBean(ElementBean bean) throws IOException {
      XMLOutputter outputter = new XMLOutputter();
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      Format format = Format.getPrettyFormat();
      outputter.setFormat(format);
      outputter.output(bean.getBaseElement(),
         os);

      log.error(new String(os.toByteArray()));
   }

   public void testElementValidationBad() throws Exception {
      ElementBean bean = getElementBean();

      bean.put("firstName", "John");
      bean.put("middle", "d");

      // no last name
      // bean.put("lastName", "Ellis");

      bean.put("expires", new Date());

      ElementBean emails = (ElementBean)bean.get("emails");

      // can set a simple list (eachi item in list is a data node) with a string array
      emails.put("email", new String[]{
         "johnEllis@alumni.creighton.edu",
         "john.ellisrsmart.com"}); // malformed email

      ElementListBean phoneList = (ElementListBean) bean.get("phoneNumber");

      // must create an element for each item in a complex list
      ElementBean phone1 = phoneList.createBlank();
      ElementBean phone2 = phoneList.createBlank();

      phone1.put("type", "Home");
      phone1.put("number", "1234567890"); // i don't think i'll put my real number here :)
      phone1.put("contact", "true");
      phoneList.add(phone1);

      phone2.put("type", "Something not in enum"); // not in enum
      phone2.put("number", "987654321"); // i don't think i'll put my real number here :)
      phoneList.add(phone2);

      List errors = validator.validate(bean);

      if (errors.size() == 0) {
         log.error("No errors");
      }

      for (Iterator i=errors.iterator();i.hasNext();) {
         ValidationError error = (ValidationError)i.next();
         log.error("found error " + error.getDefaultMessage());
      }

      dumpBean(bean);
   }

   private ElementBean getElementBean() {
      SchemaNode node = schemaFactory.getSchema(getClass().getResourceAsStream("/testSchema.xsd"));
      ElementBean bean = new ElementBean("contactInfo",
            node.getChild("contactInfo"), true);
      ElementBean.setWrapperFactory((FieldValueWrapperFactory) applicationContext.getBean(
         "fieldValueWrapperFactory"));
      return bean;
   }

}