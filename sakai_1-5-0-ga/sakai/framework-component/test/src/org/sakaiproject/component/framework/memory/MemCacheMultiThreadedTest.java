/**********************************************************************************
 *
 * $Header: /cvs/sakai/framework-component/test/src/org/sakaiproject/component/framework/memory/MemCacheMultiThreadedTest.java,v 1.1 2004/08/25 13:38:25 brettm.umich.edu Exp $
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

import java.util.Iterator;
import java.util.List;

import org.sakaiproject.mock.CacheRefresherMock;
import org.sakaiproject.mock.EventMock;
import org.sakaiproject.service.legacy.event.Event;

import net.sourceforge.groboutils.junit.v1.TestRunnable;

/**
 * Test case for multiple threads operating on one MemCache object. Tests assume
 * that the new MemCache(memoryService, eventTrackingService, logger, refresher,
 * sleep) form of the MemCache constructor was used.  This is not a full JUnit
 * test of the MemCache class since there are some methods that are difficult
 * to test with multiple concurrent tests.
 */
public class MemCacheMultiThreadedTest extends TestRunnable
{
    /** Object under test */
    private MemCache memCache;

    /** 
     * Each instance of the test is passed a unique id to avoid collisions
     * with other instances of the test class.
     * */
    private int testId;

    /** The Mock object that will deal with expired entries. */
    private CacheRefresherMock refresher;

    /**
     * @param testId Unique id used in test
     * @param memCache MemCache object that will be tested
     * @param refresher CachRefresher, in this case a Mock object
     */
    public MemCacheMultiThreadedTest(int testId, MemCache memCache,
            CacheRefresherMock refresher)
    {
        this.testId = testId;
        this.memCache = memCache;
        this.refresher = refresher;
    }

    /*
     * Class under test for void put(Object, Object, int)
     */
    public final void testPutObjectObjectint()
    {
        String cachedKey = this.testId + "_testPutObjectObjectint/abcd";
        String cachedValue = "abcdefg";

        // Make sure that the refresher doesn't know about
        // this value so that it will not be reloaded when
        // the cache entry expires.
        this.refresher.remove(cachedKey);

        // Put in a value with a duration longer than
        // it will take us to get it.
        memCache.put(cachedKey, cachedValue, 1000);

        // It should be there.
        assertEquals("Cache value mismatch", cachedValue, memCache.get(cachedKey));

        // Now put in a value that will quickly go away.
        memCache.put(cachedKey, cachedValue, 1);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should be gone by now.
        assertNull("The cache value should be gone", memCache.get(cachedKey));

        //
        // This code is commented out since there is a latency between the time
        // that the
        // MemCache.run() function refreshes cache entries and the time that a
        // get()
        // is called, which causes an expired cache entry to be immediately and
        // permanently booted from the cache.
        //        
        //        // Now, if we put this value in the refresher, then
        //        // when the object expires, it will just get reloaded.
        //        this.refresher.put(cachedKey, cachedValue);
        //
        //        // Now put in a value that will quickly go away.
        //        memCache.put(cachedKey, cachedValue, 1);
        //        
        //        try
        //        {
        //            // Wait for longer than it will take to go away.
        //            delay(65000);
        //        }
        //        catch (InterruptedException e)
        //        {
        //            // Proceed anyway.
        //        }
        //        
        //        // It should still be there since it's in the cache refresher.
        //        assertEquals("Cache value mismatch", cachedValue, memCache.get(cachedKey));

        // Now put in a value with an infinite duration
        memCache.put(cachedKey, cachedValue, 0);

        // It should immediately be there.
        assertEquals("Missing cache value", cachedValue, memCache.get(cachedKey));

        try
        {
            // Wait for longer than it will take to go away.
            delay(1000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should still be there after waiting a while.
        assertEquals("Cache value mismatch", cachedValue, memCache.get(cachedKey));
    }

    /*
     * Class under test for void put(Object, Object)
     */
    public final void testPutObjectObject()
    {
        String cachedKey = this.testId + "_testPutObjectObject/abcd";
        String cachedValue = "abcdefg";

        // Put in a value with a duration longer than
        // it will take us to get it.
        memCache.put(cachedKey, cachedValue);
        assertEquals("Cache value mismatch", cachedValue, memCache.get(cachedKey));
    }

    /**
      * Class under test for void containsKeyExpiredOrNot()
    */
    public final void testContainsKeyExpiredOrNot()
    {
        String cachedKey = this.testId + "_testContainsKeyExpiredOrNot/abcd";
        String cachedValue = "abcdefg";

        // The key/value pair shouldn't be there if we haven't put it yet.
        assertFalse("Cache value shouldn't be present", memCache.containsKeyExpiredOrNot(cachedKey));

        // Put in a value with a duration shorter than
        // it will take us to get it.
        memCache.put(cachedKey, cachedValue, 1);
        assertEquals("Cache value mismatch", cachedValue, memCache.get(cachedKey));
        memCache.expire(cachedKey);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should be expired, but not gone by now.
        assertFalse("Expired cache value should be gone", memCache.containsKeyExpiredOrNot(cachedKey));

        // Put it again, but with a duration of 0, which should
        // prevent it from being purged from the cache.
        memCache.put(cachedKey, cachedValue, 0);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should not be expired/gone.
        assertTrue("Missing cache value", memCache.containsKeyExpiredOrNot(cachedKey));
    }

    /**
     *  Class under test for boolean containsKey()
     */
    public final void testContainsKey()
    {
        String cachedKey = this.testId + "_testContainsContainsKey/abcd";
        String cachedValue = "abcdefg";

        // The key/value pair shouldn't be there if we haven't put it yet.
        assertFalse(memCache.containsKey(cachedKey));

        // Put in a value with a duration longer than
        // it will take us to get it.
        memCache.put(cachedKey, cachedValue, 1);
        assertEquals(cachedValue, memCache.get(cachedKey));
        memCache.expire(cachedKey);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should be expired by now.
        assertFalse("Cache value should be gone", memCache.containsKey(cachedKey));

        // Put it in with a duration of zero, which should keep it around
        // forever.
        memCache.put(cachedKey, cachedValue, 0);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should still be there.
        assertTrue("Missing cache value", memCache.containsKey(cachedKey));
    }

    /**
     * Class under test for Object getExpiredOrNot()
     */
    public final void testGetExpiredOrNot()
    {
        String cachedKey = this.testId + "_testGetExpiredOrNot/abcd";
        String cachedValue = "abcdefg";

        // The key should not be there before we put it.
        assertNull("Cache value shouldn't be present", memCache.getExpiredOrNot(cachedKey));

        // Put in a value with a duration longer than
        // it will take us to get it.
        memCache.put(cachedKey, cachedValue, 1);
        assertEquals("Cache value mismatch", cachedValue, memCache.get(cachedKey));
        memCache.expire(cachedKey);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should be expired, but not gone by now.
        assertNull("Cache value shouldn't be present", memCache.getExpiredOrNot(cachedKey));

        // Put the key with a duration of 0, which should keep it from
        // exipiring.
        memCache.put(cachedKey, cachedValue, 0);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should still be there.
        assertNotNull("Missing cache value", memCache.getExpiredOrNot(cachedKey));
    }

    /**
     * Class under test for Object get();
     */
    public final void testGet()
    {
        String cachedKey = this.testId + "_testGet/abcd";
        String cachedValue = "abcdefg";

        // The key/value should not be there before we put it.
        assertNull(memCache.get(cachedKey));

        // Put in a value with a duration longer than
        // it will take us to get it.
        memCache.put(cachedKey, cachedValue, 1);
        assertEquals("Cache value mismatch", cachedValue, memCache.get(cachedKey));
        memCache.expire(cachedKey);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should be expired by now.
        assertNull("Cache value should be exipred", memCache.get(cachedKey));

        // Put with a duration of zero, which should prevent expiration.
        memCache.put(cachedKey, cachedValue, 0);

        try
        {
            // Wait for longer than it will take to go away.
            delay(65000);
        }
        catch (InterruptedException e)
        {
            // Proceed anyway.
        }

        // It should still be there.
        assertNotNull("Missing cache value", memCache.get(cachedKey));
    }

    /*
     * Class under test for List getAllString()
     */
    public final void testGetAllString()
    {
        String cachedKey = this.testId + "_testGetAllString/abcd";

        // To test to see if we get all the right values back,
        // sum up the values of the integers that we put in.
        // When we get back the entire list, sum them up
        // and compare them.

        int actualTotal = 0;
        int numberOfCacheItems = 20;
        
        for (int i = 0; i < numberOfCacheItems; i++)
        {
            // Put in a value with a duration longer than
            // it will take us to get it.
            memCache.put(cachedKey + i, new Integer(i), 1);

            actualTotal += i;
        }

        List entryList = memCache.getAll(memCache.referencePath(cachedKey));

        assertEquals(numberOfCacheItems, entryList.size());

        Iterator it = entryList.iterator();

        int comparisonTotal = 0;

        while (it.hasNext())
        {
            Integer val = (Integer) it.next();

            comparisonTotal += val.intValue();
        }

        assertEquals("Cache values put != cache values retrieved", actualTotal, comparisonTotal);
    }

    /**
     * Class under test for void remove() 
     */
    public final void testRemove()
    {
        String cachedKey = this.testId + "_testRemove/abcd";
        String cachedValue = "abcdefg";

        // Put in a value with a duration longer than
        // it will take us to get it.
        memCache.put(cachedKey, cachedValue, 0);

        // Remove the key from the cache.
        memCache.remove(cachedKey);

        // It should not still be there.
        assertNull("Cache value should be expired", memCache.getExpiredOrNot(cachedKey));
    }

    /**
     * Class under test for void continueUpdate() - key already present case - old key should be removed.
     */
    private void testContinueUpdateKeyAlreadyPresent()
    {
        String event = "EventtestContinueUpdateKeyAlreadyPresent";
        String sessionId = Integer.toString(this.testId);
        String userId = "bnm";
        boolean modify = true;
        int priority = 1;

        String cachedKey = this.testId
                + "_testContinueUpdateKeyAlreadyPresent/abcd";
        String cachedValue1 = "cachedValue1testContinueUpdateKeyAlreadyPresent";
        String cachedValue2 = "cachedValue2testContinueUpdateKeyAlreadyPresent";

        memCache.put(cachedKey, cachedValue1);

        // Claim that we're complete for this key.
        memCache.setComplete(memCache.referencePath(cachedKey));

        // Make sure that the value got in the table.
        assertEquals("Cache value mismatch", cachedValue1, memCache.get(cachedKey));

        // Put a different value, but with the same key in the cache refresher.
        refresher.put(cachedKey, cachedValue2);

        Event mockEvent1 = new EventMock(event, cachedKey, sessionId, userId,
                modify, priority);

        memCache.continueUpdate(mockEvent1);

        // Make sure that the value from the refresher got into the cache.
        assertEquals("Cache value mismatch", cachedValue2, (String) memCache.get(cachedKey));
    }

    /**
     * Class under test for void continueUpdate() - key already present case - old key should be removed.
     */
    private void testContinueUpdateNewKeyNotInCacheRefresher()
    {
        String event = "EventtestContinueUpdateNewKeyNotInCacheRefresher";
        String sessionId = Integer.toString(this.testId);
        String userId = "bnm";
        boolean modify = true;
        int priority = 1;

        String cachedKey = this.testId
                + "_testContinueUpdateNewKeyNotInCacheRefresher/abcd";
        Event mockEvent1 = new EventMock(event, cachedKey, sessionId, userId,
                modify, priority);

        memCache.continueUpdate(mockEvent1);

        // Make sure that the value was NOT set since it isn't present in the
        // CacheRefresher
        assertNull("Cache value should not be present", memCache.get(cachedKey));
    }

    /**
     *  Class under test for void continueUpdate() - test update for new key not in cache
     */
    private void testContinueUpdateNewKeyNotInCache()
    {
        String event = "EventtestContinueUpdateNewKeyNotInCache";
        String sessionId = Integer.toString(this.testId);
        String userId = "bnm";
        boolean modify = true;
        int priority = 1;

        String cachedKey = this.testId
                + "_testContinueUpdateNewKeyNotInCache/abcd";
        String cachedValue = "cachedValue1testContinueUpdateNewKeyNotInCache";

        memCache.put(cachedKey, cachedValue);
        
        // Add it to the refresher.
        refresher.put(cachedKey, cachedValue);

        // Claim that we're complete for this key.
        memCache.setComplete(memCache.referencePath(cachedKey));

        // Make sure that the value got in the table.
        assertEquals("Cache value mismatch", cachedValue, memCache.get(cachedKey));

        Event mockEvent1 = new EventMock(event, cachedKey, sessionId, userId,
                modify, priority);

        memCache.continueUpdate(mockEvent1);

        // Make sure that the value got into the cache.
        assertEquals("Cache value mismatch", cachedValue, (String) memCache.get(cachedKey));
    }

    /**
     * test for String ReferencePath()
     */
    public final void testReferencePath()
    {
        String refPath1 = "abc/def/ghi";
        String refPath1Processed = "abc/def/";

        refPath1Processed.equals(memCache.referencePath(refPath1));

        // TODO - try passing in a null once referencePath() can handle it.
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.groboutils.junit.v1.TestRunnable#runTest()
     */
    public void runTest() throws Throwable
    {
        testPutObjectObjectint();
        testPutObjectObject();
        testContainsKeyExpiredOrNot();
        testContainsKey();
        testGetExpiredOrNot();
        testGet();
        testGetAllString();
        testRemove();
        testContinueUpdateKeyAlreadyPresent();
        testContinueUpdateNewKeyNotInCache();
        testContinueUpdateNewKeyNotInCacheRefresher();
        testReferencePath();
    }

}