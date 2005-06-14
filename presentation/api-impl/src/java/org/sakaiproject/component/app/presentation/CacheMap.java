/*
 * Created on Jan 10, 2005
 *
 * @author Charles Severance
 *
 * This provides a simple caching mechanism.  The CacheMap is a key, value store stored
 * in a Hashtable.  A LinkedList is used to track the order of addition so that the entries
 * are expired in a FIFO manner.
 * 
 * The goal is a simple structure which is self-maintaining and that all maintenance of the cache is
 * done during the calls to put and get.
 * 
 * The application of this is to produce relatively small caches (<1000 entries) with relatively short
 * lifetimes (~ a few minutes).
 * 
 * Sample Usage:
 *      CacheMap m_cacheMap;
 * 
 *      // 60 seconds, 30 entries, title of this cache, how often log prints, verbose mode
 * 		m_cacheMap = new CacheMap(60,30,"XMLFileRealm.unlock(Realms)",10,false);
 * 
 *      m_cacheMap.put("123","Value");
 *      m_cacheMap.put("456","Another Value");
 * 
 * 	    String str = (String) m_cacheMap.get("123");
 * 
 * 		m_cacheMap.expireTop();
 * 		m_cacheMap.expireAll();
 * 		m_cacheMap.clear();
 * 
 *  Keys and values can be any type of Object which can be stored in a LinkedList and HashTable.
 * 
 * PERFORMANCE
 * 
 * This code was designed to amortize maintenance over the get and put calls so as not to require
 * a timer, thread or any other mechanisms. 
 * 
 * During a get, up to two maintenance operations will happen:
 * 
 * (1) the oldest entry may be expired 
 *     O(LinkedList top look up and removal + HashTable removal) = O(1)
 *  
 * (2) the entry being accessed may be expired at a cost of
 *     O(LinkedList removal + HashTable Removal) = O(N) because of LinkedList Removal 
 * 
 * During a put, the oldest entry may be expired at a cost of O(1).
 * 
 * expireTop is O(1)
 * 
 * expireAll is O(N ** 2) - Probably to be avoided unless memory structures are very large.
 * An alternative to expireAll() is to simply discard the whole CacheMap when the application
 * knows that things will be quiet for a while and wants to recover memory.
 * 
 * ISSUES
 * 
 * Concurrent access to these structures by multiple competing threads has not been evaluated or tested.
 * There could be two approaches: (1) synchronize the public methods or (2) allow things to get confused and
 * silently repair them.  In a way inconsistent data is not a problem as long as you never mistakenly
 * return data that is too old.  Losing data will just cause it to be reloaded.
 */

package org.sakaiproject.component.app.presentation;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class CacheMap {
    /** A Hash Table to store the actual proxies */
    private Hashtable m_map = null;

    /** A linked list to store the IDs in a "FIFO" queue */
    private LinkedList m_queue = null;
    
    /** The time to cache the objects (seconds) */
    private int m_cacheTime = 300;
    
    /** The maximum size of the cachemap */
    private int m_cacheSize = 20;
    
    /** The title for this Cache */
    private String m_title = null;
    
    /** Whether or not we log debug messages */
    private boolean m_debug = false;
    
    /** The count between status messages */
    private long m_messageCount = -1;
    
    /** Used to record the recent activity */
    private long totalHits = 0;
    private long totalGets = 0;
    private long totalPuts = 0;
    private final long TRY_HISTORY = 1000000;   // Avoid overflow
    
    
    public CacheMap(int cacheTime, int cacheSize, String title, int messageCount, boolean debug )
	{
        // Create the data structures
    		if ( cacheSize < 10 ) cacheSize = 10;
        m_map = new Hashtable(cacheSize);
        m_queue = new LinkedList();
        
        if ( cacheTime < 1 ) cacheTime = 1; 
        m_cacheTime = cacheTime;
        m_cacheSize = cacheSize;
        m_title = title;
        m_debug = debug;
        if ( messageCount <= 0 )
        		m_messageCount = -1;
        else
        		m_messageCount = messageCount;
	}
    
    public CacheMap(int cacheTime, int cacheSize, String title)
	{
       this(cacheTime, cacheSize, title, -1 , false);
	}

    private class CacheMapEntry
	{
    		private Object obj;
    		private long time;
    		private int hits;
    		
    		CacheMapEntry(Object obj)
		{
    			this.obj = obj;
    			this.time = System.currentTimeMillis()/1000;
    			hits = 0;
		}
    		
    		public long getTime() { return time; }
    		public Object getObject() { return obj; }
    		public int getHits() { return hits; }
    		public void recordHit() { hits++; }
	}
    
    private boolean checkAndExpire(Object key, CacheMapEntry entry)
    {
        // Check to see if it is old
        long starttime = (System.currentTimeMillis()/1000);
        long timeDifference = Math.abs(entry.getTime() - starttime);
 
        boolean expired = ( timeDifference > m_cacheTime) ;

        /*
        System.out.println("Found id="+key+" time="+entry.getTime()+" hits="+entry.getHits()+
		   " starttime="+starttime+" diff="+timeDifference+" expired="+expired);
        */
        
        if (expired)
        {
            	Object removed = m_map.remove(key);
        		m_queue.remove(key);
        }
        return expired;
    }
    
    private void sanityCheck()
    { 		
        if (m_map.isEmpty()== true) return;
        if (m_queue.isEmpty()== true) return;
        
        if (m_map.size() != m_queue.size() )
        {	
        		System.out.println(toString()+" Error: FIFO/Hash length mis-match");
        		clear();
        }

        // Extreme sanity check - only run in test mode and on small Caches
        if ( ! m_debug ) return;
        
        if ( m_cacheSize > 200 ) return;

		for (Iterator i = m_queue.iterator(); i.hasNext();)
		{
			Object key = i.next();
	        
            if (! m_map.containsKey(key))
            {
            		System.out.println(toString());
            		System.out.println("**** ERROR **** Missing key:"+key);
            }
		}

    }

    /** 
     * Retrieve an element
     */
     private CacheMapEntry internalGet(Object key)
     {
             if (m_map.isEmpty()== true) return null;

             if (m_map.containsKey(key)== false)
             {
                     return null;
             }
             else
             {
                     CacheMapEntry entry = (CacheMapEntry) m_map.get(key);
                     entry.recordHit();
                     return entry;
             }
     }
     
     private void doStatus()
     {
     	if ( m_messageCount != -1 && ((totalGets+totalPuts) % m_messageCount) == 0 ) 
     	{
     		System.out.println(toString());
     	}
     		
        if ( totalGets >= TRY_HISTORY || totalPuts >= TRY_HISTORY) 
        {
        		totalGets = 0;
        		totalPuts = 0;
        		totalHits = 0;
        }
     }
     
     public String toString()
     {
 		long hits;
 		if (totalGets == 0 ) 
 			hits = 0;
 		else
 			hits = ( totalHits * 100 ) / totalGets;
 		
     	return "CacheMap:"+m_title+" Put/Get/Hit="+totalPuts+"/"+totalGets+"/"+totalHits+" "+hits+"% size="+m_map.size()+"/"+m_queue.size ();
     }

     /**
      * Check to see if the oldest entry in the table needs to expire.
      */
     public void expireTop()
     {
             if (m_map.isEmpty()== true) return;
             if (m_queue.isEmpty()== true) return;

             // Check the first entry in the FIFO list
             Object topEntry = m_queue.getFirst();
             
             // If we are too large, simply expire the top entry
             if ( m_queue.size() >= m_cacheSize ) 
             {
         			Object entry = m_map.remove(topEntry);
         			m_queue.remove(topEntry);
         			// if ( m_debug ) System.out.println(toString()+" List length exceeded "+m_cacheSize);
             }

             // Retrieve it from the cache to check expiration time
             CacheMapEntry entry = (CacheMapEntry)internalGet(topEntry);
             if ( entry == null ) {
                     System.out.println(toString()+" Top FIFO entry not in Hash:"+topEntry);
                     topEntry = m_queue.removeFirst();
                     return;
             }

             if ( checkAndExpire(topEntry, entry) )  return;
     }
     
     /*
      * Run through all entries and expire the entries - probably not a good idea
      * 
      * In general this should not be needed as the structures are self-maintaining in the
      * normal get and put methods, but for a large cache where the application knows that it
      * will not be using the contents for a while, this may be useful.
      * 
      * The performance of this method is O(N ** 2) because it copies and then loops through 
      * the LinkedList O(N) and then potentially deletes every expired item 
      * separately (each O(N))
      * 
      * Another option is just use clear() or throw the cache away and re-create it - other than 
      * garbage collection issues, throwing the structure away and re-creating it is 
      * nominally O(1) - but with GC factored should be thought of as O(N) - which is still
      * better than O(N ** 2).
      */
     
     public void expireAll()
     {
        if (m_map.isEmpty()== true) return;
        if (m_queue.isEmpty()== true) return;
        
        // Make copy of linked list so as we clean up, we can safely continue to iterate
     	LinkedList localList = new LinkedList(m_queue);
     	
		for (Iterator i = localList.iterator(); i.hasNext();)
		{
			Object key = i.next();
			
            CacheMapEntry entry = (CacheMapEntry)internalGet(key);
            if ( entry == null ) {
                    System.out.println(toString()+" expireAll() Not in Hash:"+key);
                    m_queue.remove(key);
                    continue;
            }
            checkAndExpire(key,entry);
		}
     
     }
     
     /*
      * Clear the underlying structures
      */
     
     public void clear()
     {
		m_queue.clear();
		m_map.clear();
		
		totalGets = 0;
		totalPuts = 0;
		totalHits = 0;
     }
     
     public void put(Object key, Object value)
     {
     	if ( m_debug ) System.out.println("Adding key="+key);
     	doStatus();
     	sanityCheck();
     	expireTop();            // Expire top on gets and puts to "catch up"
     
     	totalPuts++;
     	m_queue.remove(key);
     	m_map.put(key,new CacheMapEntry(value));  // Replaces if already there
     	m_queue.add(key);
     }

     /** 
      * Retrieve an element
      */
     public Object get(Object key)
	 {
	 		if ( m_debug ) System.out.println(" Getting key="+key);
	 		doStatus();
	 		sanityCheck();
	         expireTop();  // First, expire the latest entry, if it exists

	         totalGets ++;	         
	         CacheMapEntry entry = internalGet(key);

	         if ( entry == null ) return null;
	         if ( checkAndExpire(key, entry) ) return null;

	         totalHits++;
	         return entry.getObject();
	 }
}	