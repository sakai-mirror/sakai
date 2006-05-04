/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/trunk/sakai/portal/presence/src/java/org/sakaiproject/tool/portal/PresenceTool.java $
* $Id: PresenceTool.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
**********************************************************************************
*
* Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.component.osid.repository.registry;

/**
 * <p>
 * LongValueIterator implements an interface with this name in the Repository OSID.
 * </p>
 * 
 * @author Massachusetts Institute of Technology
 * @version $Id: PresenceTool.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
 */
public class LongValueIterator implements org.osid.shared.LongValueIterator {
    java.util.Iterator mIterator = null;

	/**
	 * Store away what we will return piecemeal.
	 */
    protected LongValueIterator(java.util.Vector vector)
	throws org.osid.shared.SharedException {
        mIterator = vector.iterator();
    }

	/**
	 * Return whether there is another long or not.
	 */
    public boolean hasNextLongValue() throws org.osid.shared.SharedException {
        return mIterator.hasNext();
    }

	/**
	 * Return the next long or throw an exception if there are no more longs.
	 * The exception's message is: org.osid.shared.SharedException.NO_MORE_ITERATOR_ELEMENTS
	 */
    public long nextLongValue()
	throws org.osid.shared.SharedException {
        try {
            return ((Long)mIterator.next()).longValue();
        } catch (java.util.NoSuchElementException e) {
            throw new org.osid.shared.SharedException(org.osid.shared.SharedException.NO_MORE_ITERATOR_ELEMENTS);
        }
    }
}
