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


package org.apache.wsrp4j.persistence.xml.driver;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import org.apache.wsrp4j.exception.ErrorCodes;
import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.apache.wsrp4j.persistence.PersistentDataObject;
import org.apache.wsrp4j.persistence.xml.PersistentDataObjectXML;
import org.apache.wsrp4j.persistence.PersistentHandler;
import org.apache.wsrp4j.persistence.xml.PersistentInformationXML;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * This class serves the store, restore and delete funtionality for
 * PersistentDataObject XML files.
 *
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 *
 * @version 1.0
 */
public class PersistentHandlerImpl implements PersistentHandler
{

    // holds this PersistentHandler
    private static PersistentHandler _persistentHandler = null;

    // map of filenames from XML files to the hashcode of the object
    // key = hashcode, object = filename
    private static HashMap _filenameMap = new HashMap();

    // log and trace support
    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());

    /**
     * Create a PersistentHandler
     */
    public static PersistentHandler create()
    {

        if (_persistentHandler == null)
        {
            _persistentHandler = new PersistentHandlerImpl();
        }

        return _persistentHandler;
    }

    /**
     * Private Constructor
     */
    private PersistentHandlerImpl()
    {

    }

    /**
     * Store a single XML file, contained in the persistentDataObject to
     * the persistent file store via the marshalFile method of the input
     * object. The filename of the object is collected with it's hashcode
     * internally.
     *
     * @param persistentDataObject
     *
     * @throws WSRPException
     */
    public void store(PersistentDataObject persistentDataObject) throws WSRPException
    {

        String MN = "store";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        Mapping mapping = null;
        Marshaller marshaller = null;
        FileWriter fileWriter = null;
        String filename = null;

        try
        {

            PersistentInformationXML persistentInformation = (PersistentInformationXML) persistentDataObject.getPersistentInformation();

            Object o = persistentDataObject.getLastElement();
            int hashCode = o.hashCode();
            String code = new Integer(hashCode).toString();
            filename = (String)_filenameMap.get(code);

            if (filename == null)
            {
                persistentInformation.updateFileName(code);
                filename = persistentInformation.getFilename();
                _filenameMap.put(code, filename);
            }

            File file = new File(filename);

            if (logger.isLogging(Logger.TRACE_MEDIUM))
            {
                logger.text(Logger.TRACE_MEDIUM, MN, "Filename for store = " + filename + "with code = " + code);
            }

            fileWriter = new FileWriter(file);

            if (persistentInformation.getMappingFileName() != null)
            {

                mapping = new Mapping();
                mapping.loadMapping(persistentInformation.getMappingFileName());
                marshaller = new Marshaller(fileWriter);
                marshaller.setMapping(mapping);
            }

            ((PersistentDataObjectXML)persistentDataObject).marshalFile(fileWriter, marshaller);

            fileWriter.close();

        } catch (Exception e)
        {
            // could not store object to persistent file
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.STORE_OBJECT_ERROR, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

    }

    /**
     * Restores a single XML file into the persistentDataObject. The filename
     * used is the filename stored in the PersistentFileInformation, contained in
     * the PersistentDataObject.
     *
     * @param persistentDataObject
     * @return PersistentDataObjct
     *
     * @throws WSRPException
     */
    public PersistentDataObject restore(PersistentDataObject persistentDataObject) throws WSRPException
    {

        String MN = "restore";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        Mapping mapping = null;
        Unmarshaller unmarshaller = null;

        try
        {

            PersistentInformationXML persistentInformation = (PersistentInformationXML) persistentDataObject.getPersistentInformation();

            if (persistentInformation.getMappingFileName() != null)
            {

                mapping = new Mapping();
                mapping.loadMapping(persistentInformation.getMappingFileName());
                unmarshaller = new Unmarshaller(mapping);
            }

            File file = new File(persistentInformation.getFilename());

            if (file.isFile())
            {

                FileReader fileReader = new FileReader(file);

                try
                {

                    ((PersistentDataObjectXML)persistentDataObject).unMarshalFile(fileReader, unmarshaller);
                    fileReader.close();

                    Object o = persistentDataObject.getLastElement();
                    int hashCode = o.hashCode();
                    String code = new Integer(hashCode).toString();
                    _filenameMap.put(code, file.getAbsolutePath());

                    if (logger.isLogging(Logger.TRACE_MEDIUM))
                    {
                        logger.text(
                            Logger.TRACE_MEDIUM,
                            MN,
                            "File " + file.getAbsolutePath() + " added with hashCode = " + code);
                    }

                } catch (Exception e)
                {

                    // could not restore a single file from persistent store
                    WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.RESTORE_OBJECT_ERROR, e);
                }

            }

        } catch (Exception e)
        {

            // castor persistent failure
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.RESTORE_OBJECT_ERROR, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return persistentDataObject;

    }

    /**
     * Restore all known XML files from the persistent store into the
     * PersistentDataObject. The class type, which is part of the filename
     * is stored in the PersistentInformation object of the PersistentDataObject.
     *
     * @param persistentDataObject
     * @return PersistentDataObject
     *
     * @throws WSRPException
     */
    public PersistentDataObject restoreMultiple(PersistentDataObject persistentDataObject) throws WSRPException
    {

        String MN = "restoreMultiple";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        Mapping mapping = null;
        Unmarshaller unmarshaller = null;

        try
        {

            PersistentInformationXML persistentInformation = (PersistentInformationXML) persistentDataObject.getPersistentInformation();

            if (persistentInformation.getMappingFileName() != null)
            {

                mapping = new Mapping();
                mapping.loadMapping(persistentInformation.getMappingFileName());
                unmarshaller = new Unmarshaller(mapping);
            }

            File file = new File(persistentInformation.getStoreDirectory());

            if (file.exists())
            {

                File[] files = file.listFiles();

                for (int x = 0; x < files.length; x++)
                {

                    if (files[x].isFile())
                    {

                        String currentFileName = files[x].getName();
                        // check for valid xml file
                        if (currentFileName.endsWith(persistentInformation.getExtension()))
                        {

                            // check for valid object
                            if (currentFileName.startsWith(persistentInformation.getFilenameStub()))
                            {
                                // load object
                                try
                                {

                                    FileReader fileReader = new FileReader(files[x]);

                                    ((PersistentDataObjectXML)persistentDataObject).unMarshalFile(fileReader, unmarshaller);

                                    fileReader.close();

                                    Object o = persistentDataObject.getLastElement();
                                    int hashCode = o.hashCode();
                                    String code = new Integer(hashCode).toString();
                                    _filenameMap.put(code, files[x].getAbsolutePath());

                                    if (logger.isLogging(Logger.TRACE_MEDIUM))
                                    {
                                        logger.text(
                                            Logger.TRACE_MEDIUM,
                                            MN,
                                            "File " + files[x].getAbsolutePath() + " added with hashCode = " + code);
                                    }

                                } catch (Exception e)
                                {

                                    // restore error
                                    WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.RESTORE_OBJECT_ERROR, e);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            // restore error
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.RESTORE_OBJECT_ERROR, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }

        return persistentDataObject;

    }

    /**
     * Delete the related XML persistent file of the input
     * persistentDataObject information.
     *
     * @param persistentDataObject
     */
    public void delete(PersistentDataObject persistentDataObject)
    {

        String MN = "delete";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        Object o = persistentDataObject.getLastElement();
        int hashCode = o.hashCode();
        String code = new Integer(hashCode).toString();
        String filename = (String)_filenameMap.get(code);
        _filenameMap.remove(code);

        File file = new File(filename);
        if (file.exists())
        {
            file.delete();
            if (logger.isLogging(Logger.TRACE_MEDIUM))
            {
                logger.text(Logger.TRACE_MEDIUM, MN, "Persistent file: " + filename + "deleted.");
            }
        } else
        {
            if (logger.isLogging(Logger.TRACE_MEDIUM))
            {
                logger.text(Logger.TRACE_MEDIUM, MN, "Try to delete persistent file: " + filename + ". Not found!");
            }
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
    }
}
