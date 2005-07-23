package org.sakaiproject.service.legacy.filepicker;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 20, 2005
 * Time: 1:07:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FilePickerHelper {

   /** Name of the attribute used in the tool session to pass a
    * org.sakaiproject.service.legacy.resource.ReferenceVector
    * back and forth to the picker.
    **/
   public static final String FILE_PICKER_ATTACHMENTS = "sakaiproject.filepicker.attachments";

   /** Name of the attribute used in the tool session to tell the
    * consumer if this resulted in a cancel.  This will be "true" or non-existent
    **/
   public static final String FILE_PICKER_CANCEL = "sakaiproject.filepicker.cancel";

}
