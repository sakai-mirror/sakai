package test.org.navigoproject.business.entity.evaluation.model.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.evaluation.model.AgentResults;
import org.navigoproject.business.entity.evaluation.model.MediaZipInfoModel;
import org.navigoproject.business.entity.evaluation.model.util.MediaDataZipArchive;
import org.navigoproject.data.dao.UtilAccessObject;


/**
 *
 *
 * @version $Id: TestMediaZip.java,v 1.1.1.1 2004/07/28 21:32:08 rgollub.stanford.edu Exp $
 */
public class TestMediaZip
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(TestMediaZip.class);

  private static final String FS = File.separator;
  private static final String DATAPATH =
    FS + "Navigo" + FS + "src" + FS + "test" + FS + "data" + FS + "zip" + FS;
  // edit these to correspond with the tests you want in your db instance
  private static final String[] bad_data = { "5", "6" };
  private static final String[] good_data = { "10", "240" };

  public static void main(String[] args) {
     System.out.println("Test of ZIP file creation for upload.");
    Collection fakes = makeFakeAgentResultsData();
    MediaZipInfoModel mzim = new MediaZipInfoModel();
    HashMap agentMap = MediaDataZipArchive.createAgentMap(fakes);
    mzim.setAgents(agentMap);
    mzim.setAssessmentName("Midterm");
    mzim.setCourseName("Exobiology 42");
    mzim.setPart("3");
    mzim.setQuestion("12");
    mzim.setPublishedAssessmentId("62fa62fac74747c48d9e8827283");
    String zipFileName = DATAPATH + "media_upload_test.zip";
    try {
      mzim.setOutputStream(new FileOutputStream(zipFileName));
    }
    catch (Exception ex) {
      System.out.println("Ouch!");
    }

    MediaDataZipArchive archive = new MediaDataZipArchive(mzim);
    archive.create(mzim.getAgents());
    System.out.println("Done.");
   }

   private static Collection makeFakeAgentResultsData(){
     ArrayList results = new ArrayList();
     String[] lastNames = {
                          "Black",
                          "Chang",
                          "Dumbledore",
                          "Granger",
                          "McGonagle",
                          "Potter",
                          "Snape",
                          "Weasley",
     };

     String[] firstNames = {
                           "Alice",
                           "Bruce",
                           "Carrie",
                           "David",
                           "Elmer",
                           "Fresia"
     };

     int m = 1;

     for (int ilast = 0; ilast < lastNames.length; ilast++) {
       for (int ifirst = 0; ifirst < firstNames.length; ifirst++) {
         for (char c = 'A'; c < 'A' + 26; c++)
         {
           AgentResults result = new AgentResults();
           result.setFirstName(firstNames[ifirst]+ " " + c + ".");
           result.setLastName(lastNames[ilast]);
           String resultId = ("" + Math.random()).substring(2);
           result.setAssessmentResultID(resultId);
           MediaData md = null;
           try
           {
               md = UtilAccessObject.getMediaData(m++);
           }
           catch(Exception e)
           {
             LOG.error(e);
           }
           result.setMediaData(md);
           System.out.println("Adding: " + result.getLastName() +
               ", " + result.getFirstName() +
               " #" + result.getAssessmentResultID());
           results.add(result);
         }
       }
     }

     return results;

     }
}