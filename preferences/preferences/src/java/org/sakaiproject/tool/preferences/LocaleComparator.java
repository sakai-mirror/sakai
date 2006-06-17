/**********************************************************************************
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
 
package org.sakaiproject.tool.preferences;

import java.util.Comparator;
import java.util.Locale;

/**
 ** Comparator for sorting locale by DisplayName
 **/
public final class LocaleComparator implements Comparator 
{
   /**
    ** Compares Locale objects by comparing the DisplayName
    **
    ** @param  obj1  1st Locale Object for comparison
    ** @param  obj2  2nd Locale Object for comparison
    ** @return   negative, zero, or positive integer 
    **           (obj1 charge is less than, equal to, or greater than the obj2 charge)
    **/
   public int compare(Object obj1, Object obj2) 
   {
      if (obj1 instanceof Locale && obj2 instanceof Locale) 
      {
         Locale localeOne = (Locale) obj1;
         Locale localeTwo = (Locale) obj2;
              
         String displayNameOne = localeOne.getDisplayName();
         String displayNameTwo = localeTwo.getDisplayName();
         
         return displayNameOne.compareTo( displayNameTwo );
      } 
      else 
      {
         throw new ClassCastException("Inappropriate object class for LocaleComparator");
      }
   }

   /**
    ** Override of equals method
    **
    ** @param  obj LocaleComparator object
    ** @return     true if equal, false if not equal
    **/
   public boolean equals(Object obj) 
   {
      if (obj instanceof LocaleComparator) 
         return super.equals(obj);
      else
         return false;
   }
}

