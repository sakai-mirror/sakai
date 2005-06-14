package test.org.navigoproject.util;
/***
 * This is a test program for zip files
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.navigoproject.util.FileZipArchive;

public class TestFileZip {
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(TestFileZip.class);
    
  private static final String FS = File.separator;
  private static final String DATAPATH =
      FS + "Navigo" + FS + "src" + FS + "test" + FS + "data" + FS + "zip" + FS;

   public static void main(String[] args) {
     LOG.debug("Example of ZIP file creation.");

     // Specify files to be zipped
     String[] filesToZip = new String[3];
     filesToZip[0] = DATAPATH + "firstfile.txt";
     filesToZip[1] = DATAPATH + "secondfile.txt";
     filesToZip[2] = DATAPATH + "thirdfile.txt";

     byte[] buffer = new byte[18024];

     // Specify zip file name
     String zipFileName = DATAPATH + "example.zip";

     try {

       FileZipArchive fza = new FileZipArchive(new FileOutputStream(zipFileName));
       fza.create(filesToZip);
     }
     catch (IllegalArgumentException e) {
       LOG.error(e); throw new Error(e);
     }
     catch (FileNotFoundException e) {
       LOG.error(e); throw new Error(e);
     }
//   Warning     Catch block is hidden by another one in the same try statement  MediaDataZipArchive.java  Navigo/src/org/navigoproject/business/entity/evaluation/model/util  line 216
//     catch (IOException e)
//     {
//     LOG.error(e); throw new Error(e);
//     }

   }
}
