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

import java.io.FileReader;
import java.io.FileWriter;

import oasis.names.tc.wsrp.v1.types.ServiceDescription;

import org.apache.wsrp4j.exception.ErrorCodes;
import org.apache.wsrp4j.exception.WSRPException;
import org.apache.wsrp4j.exception.WSRPXHelper;
import org.apache.wsrp4j.log.LogManager;
import org.apache.wsrp4j.log.Logger;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;


/**
 * This class contains a ServiceDescription and implements the
 * marshal / unmarshal methods for CASTOR XML file support. Detailed
 * information on persistent organizational data has to be requested
 * with this class from the corresponding PersistentInformationProvider.
 * 
 * @author  <a href="mailto:Ralf.Altrichter@de.ibm.com">Ralf Altrichter</a>
 * 
 * @see PersistentHandler
 * @see ProducerPersistentInformationProvider
 *
 * @version 1.0
 */
public class ServiceDescriptionList extends PersistentDataObjectImpl
{

    // log and trace support
    private Logger logger = LogManager.getLogManager().getLogger(this.getClass());

    /**
     * Default Constructor
     */
    public ServiceDescriptionList()
    {

    }

    /**
     * Constructs a ServiceDescription object from a CASTOR persistent
     * XML file. 
     * 
     * @param fileReader to the input data file
     * 
     * @param unmarshaller optional, part of the CASTOR package. In case of
     *                     a null value as input parameter, the static methods
     *                     of the CASTOR unmarshaller are used. 
     *
     * @throws WSRPException
     */
    public void unMarshalFile(FileReader fileReader, Unmarshaller unmarshaller) throws WSRPException
    {

        String MN = "unMarshalFile";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        try
        {
            if (unmarshaller == null)
            {
                addObject(Unmarshaller.unmarshal(ServiceDescription.class, fileReader));

            } else
            {
                addObject(unmarshaller.unmarshal(fileReader));
            }
        } catch (Exception e)
        {
            e.printStackTrace();

            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.UNMARSHAL_ERROR, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
    }

    /**
    * Writes a ServiceDescription, which must be the first element 
    * in the PersistentDataObject map, to a persistent XML file. In case
    * of a null value as input parameter, the static methods of the CASTOR
    * Marshaller are used.
    * 
    * @param fileWriter to the output XML file 
    * @param marshaller, optional
    * 
    * @throws WSRPException
    */
    public void marshalFile(FileWriter fileWriter, Marshaller marshaller) throws WSRPException
    {

        String MN = "marshalFile";
        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.entry(Logger.TRACE_HIGH, MN);
        }

        try
        {
            if (marshaller == null)
            {
                Marshaller.marshal(_objects.get(0), fileWriter);
            } else
            {
                marshaller.marshal(_objects.get(0));
            }
        } catch (Exception e)
        {
            WSRPXHelper.throwX(logger, Logger.ERROR, MN, ErrorCodes.MARSHAL_ERROR, e);
        }

        if (logger.isLogging(Logger.TRACE_HIGH))
        {
            logger.exit(Logger.TRACE_HIGH, MN);
        }
    }
}
