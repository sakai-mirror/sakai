/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

package org.navigoproject.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * Take an array of sources and produce an archive.
 * Specify the computation of the name and the
 * @author esmiley@stanford.edu
 */
public abstract class ZipArchiver
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ZipArchiver.class);

  protected OutputStream output;

  /**
   * Creates a new ZipArchiver object.
   *
   * @param zipOut DOCUMENTATION PENDING
   */
  public ZipArchiver(OutputStream zipOut)
  {
    output = zipOut;
  }

  /**
   * Take an array of sources and produce an archive.
   * @param zipHandles an array with an identifier for each entry such as a
   * file name, database key, etc.
   * @param <any>
   */
  public void create(String[] zipHandles)
  {
    byte[] buffer = new byte[18024];

    try
    {
      ZipOutputStream out = new ZipOutputStream(output);

      // Set the compression ratio
      out.setLevel(Deflater.DEFAULT_COMPRESSION);

      // iterate through the array of sources, adding each to the zip file
      for(int i = 0; i < zipHandles.length; i++)
      {
        // Associate an input stream for the current source
        InputStream in = getZipInputSource(zipHandles[i]);
//        LOG.debug("debug");

        // Add ZIP entry to output stream.
        out.putNextEntry(new ZipEntry(getArchivableName(zipHandles[i])));
System.out.println("debug " + i + ":" + zipHandles.length + ":" + zipHandles[i]);
        // Transfer bytes from the current file to the ZIP file
        try {
          out.write(buffer, 0, in.read(buffer));

          int len;
          while((len = in.read(buffer)) > 0)
          {
            out.write(buffer, 0, len);
          }

          // Close the current entry
          out.closeEntry();

          // Close the current file input stream
          in.close();
        }
        catch (Exception ex) {
          System.out.println("Exception caught, unable to create file for "+
                             zipHandles[i] + ": " + ex);
//          throw new RuntimeException("unable to create file for "+
//                             zipHandles[i] + ": " + ex);

        }

      }

      // Close the ZipOutPutStream
      out.close();
    }
    catch(IllegalArgumentException e)
    {
      LOG.error(e); throw new Error(e);
    }
    catch(FileNotFoundException e)
    {
      LOG.error(e); throw new Error(e);
    }
    catch(IOException e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Get input from source.
   * @param zipHandle the unique identifier of the source to be zipped
   * @return an InputStream into it
   */
  public abstract InputStream getZipInputSource(String zipHandle);

  /**
   * Get name under which archive is to be zipped.
   * @param zipHandle the unique identifier of the source to be zipped
   * @return the name
   */
  public abstract String getArchivableName(String zipHandle);
}
