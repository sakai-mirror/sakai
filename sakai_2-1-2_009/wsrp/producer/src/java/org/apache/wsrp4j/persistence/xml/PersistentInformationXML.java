/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 

 */


package org.apache.wsrp4j.persistence.xml;

import org.apache.wsrp4j.persistence.PersistentInformation;

/**
 * This class defines the interface for persistent information needed 
 * to store and retrieve PersistentDataObjects with castor XML support.
 * 
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 */
public interface PersistentInformationXML extends PersistentInformation
{

    /**
     * Set the Store directory for the persistent XML files 
     * 
     * @param storeDirectory String name of the store
     */
    public void setStoreDirectory(String storeDirectory);

    /**
     * Returns the directory for the persistent XML files
     * 
     * @return String nanme of the store
     */
    public String getStoreDirectory();

    /**
     * Set the Castor XML mapping file name, fully qualified
     * 
     * @param mappingFileName String fully qualified filename
     */
    public void setMappingFileName(String mappingFileName);

    /**
     * Returns the XML mapping file name, fully qualified
     * 
     * @return String fully qualified filename
     */
    public String getMappingFileName();

    /**
     * Set the file name stub for persistent XML files. The name contains the 
     * store directory followed by a file separator and the class name of the 
     * object to be restored.
     * 
     * @param stub String file name stub
     */
    public void setFilenameStub(String stub);

    /**
     * Returns the file name stub for persistent XML files. @see setFilenameStub
     * 
     * @return String file name stub
     */
    public String getFilenameStub();

    /**
     * Returns a fully qualified file name for a persistent XML file. 
     * 
     * @return String file name
     */
    public String getFilename();

    /**
     *  Set the fully qualified file name for a persistent XML file.
     * 
     * @param filename String file name
     */
    public void setFilename(String filename);


    /**
     * Updates the file name, enhanced by a string token, like a handle to 
     * idportlet a unique persistent XML file. If a groupID is set, the 
     * groupID is used instead of the token to build the filename. 
     * 
     * @param token String token, like a handle
     */
    public void updateFileName(String token);

    /**
     * Returns the file extension used for persistent XML files
     */
    public String getExtension();

    /**
     * Set the file extension for persistent XML files.
     * 
     * @param extension String file extension
     */
    public void setExtension(String extension);

    /**
     * Set the Separator, to be used in a fully qualified file name.
     * 
     * @return String Separator character
     */
    public String getSeparator();

    /**
     * Set the separator character. (e.g. '@')
     * 
     * @param separator String Separator character
     */
    public void setSeparator(String separator);

    /**
     * @return this object as String
     */
    public String toString();

}
