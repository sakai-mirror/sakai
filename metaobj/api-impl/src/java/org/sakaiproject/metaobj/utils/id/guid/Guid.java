/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/utils/id/guid/Guid.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.utils.id.guid;


/**
 * <p>Title: NATN Project
 * <p>Description: The <code>Guid</code> class provides services for handling GUID
 * values, which are 16 byte globally unique identifiers.
 * <p>Copyright: Copyright (c) 2002
 * <p>Company: The RSmart Group
 *
 * @author Michael De Simone
 * @version 2.0
 */

public class Guid implements java.io.Serializable {


   private static final int HEX_RADIX = 16;
   /**
    * length of a GUID, in bytes (16)
    */
   private static final short GUID_LEN = HEX_RADIX;
   private byte guid[] = new byte[GUID_LEN];

   public static final byte AUTOGEN_BY_DB = 1;
   public static final byte NO_AUTOGEN_BY_DB = 2;

   /**
    * holds the string representation of this GUID, and is computed during construction,
    * and when setGuid() is called.  Used to optimize toString performance.
    */
   private String guidStr;

   /**
    * indicates whether to let the database auto-generate the value or not
    */
   private boolean dbAutoGen = true;

   /**
    * Description: Allocate a new Guid object
    */

   public Guid() {
      boolean bSecureGuid = true;
      RandomGUID tmpGuid = new RandomGUID(bSecureGuid);
      guid = fromHexString(tmpGuid.valueAfterMD5);
      setGuid(guid);
      tmpGuid = null;
   }

   /**
    * from effective java by joshua bloch
    * step 1: perform == test
    * step 2: instanceof test
    * step 3: cast parameter to type
    * step 4: check primitives with ==, objects with equals()
    *
    * @param o
    * @return
    */
   public boolean equals(Object o) {
      //1.
      if (this == o) return true;
      //2.
      if (!(o instanceof Guid)) {
         // if o is null, we return here
         return false;
      }
      //3.
      Guid guid = (Guid) o;
      //4.
      if (this.toString().equals(guid.toString()))
         return true;

      return false;
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   /**
    * Description: Allocates a new Guid object from the passed in byte array.
    *
    * @param inGuid 16 byte array for this GUID
    * @throws IllegalArgumentException if byte array is not GUID_LEN bytes
    */
   public Guid(byte[] inGuid) {
      setGuid(inGuid);
   }

   public Guid(String sGuid) {
      if ((sGuid != null) && (sGuid.length()) == 32) {
         guid = fromHexString(sGuid);
         guidStr = internalToString();
      } else {
         throw new IllegalArgumentException("sGuid is either null or the wrong length");
      }
   }

   public boolean isValidGuid() {
      return this.guid != null && this.guid.length == GUID_LEN;
   }

   /**
    * Sets GUID to passed in value
    *
    * @param inGuid 16-byte array containing raw GUID value
    * @throws IllegalArgumentException if byte array is not GUID_LEN bytes
    */
   public void setGuid(byte[] inGuid) throws IllegalArgumentException {

      if (inGuid.length == GUID_LEN) {
         for (int i = 0; i < GUID_LEN; i++) {
            guid[i] = inGuid[i];
         }
         guidStr = internalToString();
      } else {
         throw new IllegalArgumentException("GUID Passed in is not " + GUID_LEN +
            " bytes - it is " + inGuid.length + " bytes");
      }
   }

   /**
    * Get the raw bytes for this GUID
    *
    * @return the raw GUID in a byte array
    */
   public byte[] getGuid() {
      return (byte[]) guid.clone();
   }

   public String getString() {
      return guidStr;
   }

   /**
    * Return string representation of the GUID as a hex value
    * <p>Example: returns the string 0x8f6eff8344a8e03b125590af6d21e9b2
    * <p> This can be then passed to a database as a numerical value
    *
    * @see internalToString()
    */
   public String toString() {
      return guidStr;
   }

   /**
    * Converts byte array to hex string representation
    *
    * @return a new string
    */
   // convert byte array to hex string representation
   private String internalToString() {

      StringBuffer buf = new StringBuffer();
      String hexStr;
      int val;
      for (int i = 0; i < GUID_LEN; i++) {
         //Treating each byte as an unsigned value ensures
         //that we don't str doesn't equal things like 0xFFFF...
         val = ByteOrder.ubyte2int(guid[i]);
         hexStr = Integer.toHexString(val);
         while (hexStr.length() < 2)
            hexStr = "0" + hexStr;
         buf.append(hexStr);
      }
      return buf.toString().toUpperCase();

/*
    Integer tmpInt;
    String hexChar = "";
    String hexStr2 = "";

    for(int i=0; i<guid.length; i++) {
      tmpInt = new Integer( ((int) guid[i]) & 0x000000FF);
      hexChar = tmpInt.toHexString(tmpInt.intValue());
      tmpInt = null;
      // toHexString strips leading zeroes, so add back in if necessary
      if (hexChar.length() == 1) {
        hexChar = "0" + hexChar;
      }
      hexStr2 += hexChar;
    }
    hexStr2 = "0x" + hexStr2;
    return hexStr2.toUpperCase();
*/
   }


   /**
    * Create a GUID bytes from a hex string version.
    * Throws IllegalArgumentException if sguid is
    * not of the proper format.
    */
   public static byte[] fromHexString(String sguid)
      throws IllegalArgumentException {
      byte bytes[] = new byte[GUID_LEN];
      try {
         for (int i = 0; i < GUID_LEN; i++) {
            bytes[i] =
               (byte) Integer.parseInt(sguid.substring(i * 2, (i * 2) + 2), HEX_RADIX);
         }
         return bytes;
      } catch (NumberFormatException e) {
         throw new IllegalArgumentException();
      } catch (IndexOutOfBoundsException e) {
         throw new IllegalArgumentException();
      }
   }

   public static boolean isValidGuidString(String sGuid) {
      return (sGuid != null && sGuid.length() == GUID_LEN * 2);
   }

   public boolean isDbAutoGen() {
      return dbAutoGen;
   }

   public boolean getDbAutoGen() {
      return isDbAutoGen();
   }

   public void setAutoGen(boolean dbAutoGen) {
      this.dbAutoGen = dbAutoGen;
   }

}