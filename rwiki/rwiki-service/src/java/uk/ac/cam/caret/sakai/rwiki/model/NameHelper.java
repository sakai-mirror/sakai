/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 University of Cambridge
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
package uk.ac.cam.caret.sakai.rwiki.model;



public class NameHelper {
    
    public static final char SPACE_SEPARATOR = '.';

    public static String globaliseName(String name, String defaultSpace) {
        if (name == null || name.length() == 0)
            return defaultSpace + SPACE_SEPARATOR + "Home";
            //return "Home" + REALM_SEPARATOR + defaultRealm;
        
        // Need to normalize this...
        if (name.indexOf(SPACE_SEPARATOR) == -1) {
            return defaultSpace + SPACE_SEPARATOR + normalizeName(name);
            //return normalizeName(name) + REALM_SEPARATOR + defaultRealm;
        } else {
            String space = name.substring(0, name.indexOf(SPACE_SEPARATOR));
            name = name.substring(name.indexOf(SPACE_SEPARATOR) + 1);
            
            return  space + SPACE_SEPARATOR + normalizeName(name);
        }
    }

    public static String localizeName(String pageName, String space) {
        
        if (pageName == null || pageName.length() == 0)
            return "Home";
        
        int index = pageName.indexOf(SPACE_SEPARATOR);
        
        if (index > -1 && pageName.substring(0, index).equals(space)) {
            return pageName.substring(index + 1);
        }
            
        return pageName;
    }

    public static String normalizeName(String pageName) {
        String[] words = pageName.split(" ");
        StringBuffer normalized = new StringBuffer();
        for (int i=0; i<words.length; i++) {
            normalized.append(Character.toTitleCase(words[i].charAt(0)));
            normalized.append(words[i].substring(1));
            if (i < words.length - 1) 
                normalized.append(' ');
        }
        return normalized.toString();
    }

    public static String localizeSpace(String pageName, String defaultSpace) {
        if (pageName.indexOf(SPACE_SEPARATOR) > -1) {
            return pageName.substring(0, pageName.indexOf(SPACE_SEPARATOR));
        }
        return defaultSpace;
    }

}
