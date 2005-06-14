/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/test/src/org/sakaiproject/component/framework/memory/MemCacheThreadTest.java,v 1.1 2004/08/25 13:38:25 brettm.umich.edu Exp $
*
***********************************************************************************
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

package org.sakaiproject.component.framework.memory;



import org.sakaiproject.mock.CacheRefresherMock;
import org.sakaiproject.mock.EventTrackingServiceMock;
import org.sakaiproject.mock.LoggerMock;
import org.sakaiproject.mock.BasicMemoryServiceMock;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.event.EventTrackingService;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

/**
 * This is the main TestCase that spawns different threads
 * to run JUnit tests.  It requires the groboutils-4-core.jar file
 * from http://groboutils.sourceforge.net/.  Later versions may
 * be used, but this code was last used with version 4.  Unfortunately,
 * this jar doesn't yet seem to be available from any of the
 * usual Maven repositories.  Perhaps it should be added to the
 * Sakai repository after checking the GroboUtils license
 * with respect to distribution requirements.
 */
public class MemCacheThreadTest extends TestCase
{
    /** Number of threads to run concurrently. */
    private static final int numberOfThreads = 10;

    /**
     * This class creates an array of JUnit test runners (instances of MemCacheMultiThreadedTest)
     * and runs them simultaneously.
     */
    public void testExampleThread() throws Throwable
    {
        // instantiate the TestRunnable classes
        
		//
		// The MultiThreadedTestRunner takes an array of TestRunnable objects as 
		// parameters in its constructor.
		//
		// After you have built the MultiThreadedTestRunner instance, you run it 
		// with a call to the runTestRunnables() method.
		//
        
        TestRunnable threads[] = new TestRunnable[numberOfThreads];
        
        EventTrackingService eventTrackingService = new EventTrackingServiceMock();
        Logger logger = new LoggerMock();
        BasicMemoryService memoryService = new BasicMemoryServiceMock(eventTrackingService, logger);
        CacheRefresherMock refresher = new CacheRefresherMock();
        long sleep = 1;
        
        MemCache memCache = new MemCache(memoryService, eventTrackingService, logger, refresher, sleep);
   	
        for ( int i = 0; i < threads.length; i++ )
        {
            threads[i] = new MemCacheMultiThreadedTest(i, memCache, refresher);
        }

        //pass that instance to the MTTR
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(threads);

        //kickstarts the MTTR & fires off threads
        mttr.runTestRunnables();
    }

    /**
     * Standard main() and suite() methods
     */
    public static void main(String[] args)
    {
        String[] name =
        {
            MemCacheThreadTest.class.getName()
        };

        junit.textui.TestRunner.main(name);
    }

    public static Test suite()
    {
        return new TestSuite(MemCacheThreadTest.class);
    }
}
