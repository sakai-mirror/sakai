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
 * AssetIterator implements an interface with that name in the Repository OSID.
 * </p>
 * 
 * @author Massachusetts Institute of Technology
 * @version $Id: PresenceTool.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
 */
public class AssetIterator implements org.osid.repository.AssetIterator {
    java.util.Iterator mIterator = null;

	/**
	 * Store away what we will return piecemeal.
	 */
    protected AssetIterator(java.util.Vector vector)
	throws org.osid.repository.RepositoryException {
        mIterator = vector.iterator();
    }

	/**
	 * Return whether there is another Asset or not.
	 */
    public boolean hasNextAsset()
	throws org.osid.repository.RepositoryException {
		try {
			return mIterator.hasNext();
		} catch (Exception ex) {
			throw new org.osid.repository.RepositoryException(org.osid.OsidException.OPERATION_FAILED);
		}
    }

	/**
	 * Return the next Asset or throw an exception if there are no more Assets.
	 * The exception's message is: org.osid.shared.SharedException.NO_MORE_ITERATOR_ELEMENTS
	 */
    public org.osid.repository.Asset nextAsset()
	throws org.osid.repository.RepositoryException {
		try {
            return (org.osid.repository.Asset) mIterator.next();
        } catch (java.util.NoSuchElementException e) {
            throw new org.osid.repository.RepositoryException(org.osid.shared.SharedException.NO_MORE_ITERATOR_ELEMENTS);
        }
    }
}
