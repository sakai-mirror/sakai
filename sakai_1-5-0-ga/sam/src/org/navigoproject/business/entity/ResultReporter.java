/*
 * Created on Feb 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;

import org.navigoproject.data.AssessmentResult;
import org.navigoproject.data.SectionResult;
import org.navigoproject.data.ItemResult;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import osid.OsidException;
import osid.dr.DigitalRepositoryException;
import osid.shared.Id;
import osid.shared.SharedException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResultReporter
{
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResultReporter.class);

    public static Document getAssessmentResult(String takenAssessmentId)
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("getResultReport(String " + takenAssessmentId);
        }
        
        return AssessmentReport.getAssessmentReport(takenAssessmentId).getDocument();
    }
    
    public static void saveResults(Document resultReport,
        String takenAssessmentId)
    {
        try
        {
            //select all the item results
            XPath itemResultsPath = new DOMXPath(
                    "/qti_result_report/result//item_result");
            List itemResults = itemResultsPath.selectNodes(resultReport);
            RepositoryManager rm = new RepositoryManager();
            Id assessmentId = rm.getId(takenAssessmentId);

            //save each item result
            for (Iterator iter = itemResults.iterator(); iter.hasNext();)
            {
                Element itemResult = (Element) iter.next();
                Id itemId = rm.getId(itemResult.getAttribute("ident_ref"));
                //rm.setItemResult(assessmentId, itemId, itemResult);
            }
        }
        catch (JaxenException e)
        {
            LOG.error(e); throw new Error(e);
        }
//        catch (DigitalRepositoryException e)
//        {
//            LOG.error(e); throw new Error(e);
//        }
        catch (OsidException e)
        {
            LOG.error(e); throw new Error(e);
        }
    }
}
