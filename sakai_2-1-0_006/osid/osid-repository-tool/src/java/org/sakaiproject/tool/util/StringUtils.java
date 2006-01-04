/**********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.util;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class StringUtils {
	/**
	 * Minimum length supported by <code>truncateAtWhitespace()</code>
	 */
	private static final int MINIMUM_SUPPORTED_LENGTH	= 4;

	private StringUtils() {
	}

  /**
   * Replace all occurances of the target text with the provided replacement
   * text.  Both target and replacement may be regular expressions - see
   * <code>java.util.regex.Matcher</code>.
   * @param text Text to modify
   * @param targetText Text to find and replace
   * @param newText New text
   * @return Updated text
   */
	public static String replace(String text, String targetText, String newText) {

  	Pattern pattern = Pattern.compile(targetText, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);

    return matcher.replaceAll(newText);
	}

	/**
	 * Null (or zero-length) String?
	 */
	public static boolean isNull(String string) {
	 	return (string == null) || (string.length() == 0);
	}

	/**
	 * Truncate text on a whitespace boundary (near a specified length).
	 * The length of the resultant string will be in the range:<br>
	 * <code>   (requested-length * .25) ~ (requested-length * 1.5) </code>
	 * @param text Text to truncate
	 * @param length Target length
	 * @return Truncated text
	 */
	public static String truncateAtWhitespace(String text, int length) {
		int	desired, lowerBound, upperBound;
		/*
		 * Make sure we have a reasonable length to work with
		 */
		if (length < MINIMUM_SUPPORTED_LENGTH) {
			 throw new IllegalArgumentException("Requested length too short (must be "
			 																	+ MINIMUM_SUPPORTED_LENGTH
			 																	+ " or greated)");
		}
		/*
		 * No need to truncate - the original string "fits"
		 */
		if (text.length() <= length) {
			return text;
		}
		/*
		 * Try to find whitespace befor the requested maximum
		 */
		lowerBound 	= length / 4;
		upperBound	= length + (length / 2);

		for (int i = length - 1; i > lowerBound; i--) {
			if (Character.isWhitespace(text.charAt(i))) {
				return text.substring(0, i);
			}
		}
		/*
		 * No whitespace - look beyond the desired maximum
		 */
		for (int i = (length); i < upperBound; i++) {
			if (Character.isWhitespace(text.charAt(i))) {
				return text.substring(0, i);
			}
		}
		/*
		 * No whitespace, just truncate the text at the requested length
		 */
		return text.substring(0, length);
  }

	/**
	 * Capitlize each word in a string (journal titles, etc)
	 * @param text Text to inspect
	 * @return Capitalized text
	 */
	public static String capitalize(String text) {
		StringBuffer	resultText;
		char 					previousC;

    resultText	= new StringBuffer();
    previousC 	= '.';

    for (int i = 0;  i < text.length();  i++ ) {
       char c = text.charAt(i);

       if (Character.isLetter(c) && !Character.isLetter(previousC)) {
          resultText.append(Character.toUpperCase(c));
       } else {
          resultText.append(c);
       }
       previousC = c;
    }
    return resultText.toString();
  }

	/*
	 * Test
	 */
  public static void main(String[] args)
                         throws Exception {
		System.out.println(StringUtils.replace(args[0], args[1], args[2]));
	}
}