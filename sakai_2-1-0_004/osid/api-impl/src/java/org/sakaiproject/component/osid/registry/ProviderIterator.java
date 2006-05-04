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
package org.sakaiproject.component.osid.registry;

/**
 * <p>
 * ProviderIterator implements a "straw-man" registry interface.
 * </p>
 * 
 * @author Massachusetts Institute of Technology
 * @version $Id: PresenceTool.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
 */
public class ProviderIterator implements edu.mit.osid.registry.ProviderIterator {
    java.util.Iterator mIterator = null;

    /**
	 * Store away the input.
	 */
	protected ProviderIterator(java.util.Vector vector) {
        mIterator = vector.iterator();
    }

    /**
	 * Test if we have any more Providers to return.
	 */
    public boolean hasNextProvider()
		throws edu.mit.osid.registry.RegistryException
	{
        return mIterator.hasNext();
    }

    /**
	 * Return the next Provider from the original input.
	 */
    public edu.mit.osid.registry.Provider nextProvider()
		throws edu.mit.osid.registry.RegistryException
	{
        try {
            return (edu.mit.osid.registry.Provider) mIterator.next();
        } catch (java.util.NoSuchElementException e) {
            throw new edu.mit.osid.registry.RegistryException(org.osid.shared.SharedException.NO_MORE_ITERATOR_ELEMENTS);
        }
    }
}