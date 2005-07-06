package org.sakaiproject.metaobj.repository;

import org.sakaiproject.metaobj.shared.model.Id;

import java.util.Map;

/*
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/PostFilePickerProcessor.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */

/**
 * Allows post processing after a file has been picked.  This is useful
 * for performing javascript actions back on the parent form.  For example,
 * populating a select box with data from the selected artifact, etc.
 *
 * The getModel() method should be used to build up a model that the view page
 * will used to generate javascript.
 *
 * The getParams() is useful to send addition params to the jsp page.  For example,
 * the name of the selectBox.  This allows for configuration to occur in spring config
 * and not have to be passed through the whole file picker process.
 *
 */
public interface PostFilePickerProcessor {

   /**
    * return true if the post processor has a problem that it needs to return to the file
    * picker for.
    * @param artifactId
    * @return
    */
   public boolean keepSession(Id artifactId);

   /**
    * return a model needed to perform post processing actions necessary.
    * @param artifactId
    * @return
    */
   public Map getModel(Id artifactId);
   /**
    *
    * @return full path to a jsp file
    */
   public String getView();
   /**
    * map of params used on the view page
    * @return
    */
   public Map getParams();
}
