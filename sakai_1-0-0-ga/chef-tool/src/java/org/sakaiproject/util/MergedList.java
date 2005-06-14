/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/MergedList.java,v 1.11 2004/08/12 17:10:59 brettm.umich.edu Exp $
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
package org.sakaiproject.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;


/**
 * Contains the list of merged/non-merged channels
 */
public class MergedList extends ArrayList
{
    /**
     * Used to create a reference.  This is unique to each caller, so we
     * need an interface.
     */
    public interface ChannelReferenceMaker
    {
        /**
         * @param siteId
         * @return
         */
        String makeReference(String siteId);
    }
    
	/**
	 * channel entry used to communicate with the Velocity templates when dealing with merged channels.
	 */
	public interface MergedEntry extends Comparable
	{
		/**
		 * Returns the display string for the channel.
		 * @return
		 */
		public String getDisplayName();

		/**
		 * Returns the ID of the group.  (The ID is used as a key.)
		 * @return
		 */
		public String getReference();

		/**
		 * Returns true if this channel is currently being merged.
		 * @return
		 */
		public boolean isMerged();

		/**
		 * Marks this channel as being merged or not.
		 * @param b
		 */
		public void setMerged(boolean b);

		/**
		 * This returns true if this list item should be visible to the user.
		 * @return
		 */
		public boolean isVisible();

		/**
		 * Implemented so that we can order by the group full name.
		 */
		public int compareTo(Object arg0);
	}

	/**
	 * This interface is used to describe a generic list entry provider so that
	 * a variety of list entries can be used.  This currently serves merged sites
	 * for the schedule and merged channels for announcements.
	 */
	public interface EntryProvider
	{
		/**
		 * Gets an iterator for the channels, calendars, etc.
		 * @return
		 */
		public Iterator getIterator();
		
		/**
		 * See if we can do a "get" on the calendar, channel, etc.
		 * @param obj
		 * @return
		 */
		public boolean allowGet(String ref);
		
		/**
		 * Generically access the context of the resource provided
		 * by the getIterator() call.
		 * @return The context.
		 */
		public String getContext(Object obj);
		
		/**
		 * Generically access the reference of the resource provided
		 * by the getIterator() call.
		 * @param obj
		 * @return
		 */
		public String getReference(Object obj);
		
		/**
		 * Generically access the resource's properties.
		 * @return The resource's properties.
		 */
		public ResourceProperties getProperties(Object obj);

        /**
         * @param channel
         * @return
         */
        public boolean isUserChannel(Object channel);

        /**
         * @param channel
         * @return
         */
        public boolean isSpecialSite(Object channel);

        /**
         * @param channel
         * @return
         */
        public String getSiteUserId(Object channel);

        /**
         * @param channel
         * @return
         */
        public Site getSite(Object channel);

	}

	/** This is used to separate group names in the config parameter. */
	static private final String ID_DELIMITER = "_,_";

	/**
	 * Implementation of channel entry used for rendering the list of merged channels
	 */
	private class MergedChannelEntryImpl implements MergedEntry
	{
		final private String channelReference;
		final private String channelFullName;
		private boolean merged;
		private boolean visible;

		/**
		 * @param channelReference
		 * @param channelFullName
		 * @param merged
		 * @param visible
		 */
		public MergedChannelEntryImpl(
			String channelReference,
			String channelFullName,
			boolean merged,
			boolean visible)
		{
			this.channelReference = channelReference;
			this.channelFullName = channelFullName;
			this.merged = merged;
			this.visible = visible;
		}

		/* (non-Javadoc)
		 * @see org.chefproject.actions.channelAction.MergedCalenderEntry#getchannelDisplayName()
		 */
		public String getDisplayName()
		{
			return channelFullName;
		}

		/* (non-Javadoc)
		 * @see org.chefproject.actions.channelAction.MergedCalenderEntry#getchannelReference()
		 */
		public String getReference()
		{
			return channelReference;
		}

		/* (non-Javadoc)
		 * @see org.chefproject.actions.channelAction.MergedCalenderEntry#isMerged()
		 */
		public boolean isMerged()
		{
			return merged;
		}

		/* (non-Javadoc)
		 * @see org.chefproject.actions.channelAction.MergedCalenderEntry#setMerged(boolean)
		 */
		public void setMerged(boolean b)
		{
			merged = b;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object arg0)
		{
			MergedChannelEntryImpl compObj = (MergedChannelEntryImpl) arg0;

			return this.getDisplayName().compareTo(compObj.getDisplayName());
		}

		/* (non-Javadoc)
		 * @see org.chefproject.actions.channelAction.MergedCalenderEntry#isVisible()
		 */
		public boolean isVisible()
		{
			return visible;
		}
	}
	
	/**
	 * Selects and loads channels from a list provided by the entryProvider
	 * parameter.  The algorithm for loading channels is a bit complex, and
	 * depends on whether or not the user is currently in their "MyWorkspace", etc.
	 * 
	 * This function formerly filtered through a list of all sites.  It still
	 * goes through the motions of filtering, and deciding how to flag the channels
	 * as to whether or not they are merged, hidden, etc.  However, it has been
	 * modified to take all of its information from an EntryProvider parameter,
	 * This list is now customized and is no longer "all sites in existence". 
	 * When sites are being selected for merging, this list can be quite long.  
	 * This function is more often called just to display merged events, so 
	 * passing a more restricted list makes for better performance.
	 * 
	 * At some point we could condense redundant logic, but this modification
	 * was performed under the time constraints of a release.  So, an effort was
	 * made not to tinker with the logic, so much as to reduce the set of data
	 * that the function had to process.
	 * 
	 * @param isOnWorkspaceTab
	 * @param entryProvider
	 * @param userId
	 * @param channelArray
	 * @param isSuperUser
	 * @param currentSiteId
	 */
	public void loadChannelsFromDelimitedString(boolean isOnWorkspaceTab,
            EntryProvider entryProvider, String userId, String[] channelArray,
            boolean isSuperUser, String currentSiteId)
	{
		// Remove any initial list contents.
		this.clear();

		// We'll need a map since we want to test for the
		// presence of channels without searching through a list.
		Map currentlyMergedchannels = makeChannelMap(channelArray);

		// Loop through the channels that the EntryProvider gives us.
		Iterator it = entryProvider.getIterator();

		while (it.hasNext())
		{
			Object channel = it.next();
			
			// Watch out for null channels.  Ignore them if they are there.
			if ( channel == null )
			{
			    continue;
			}

			// If true, this channel will be added to the list of
			// channels that may be merged.
			boolean addThisChannel = false;

			// If true, this channel will be marked as "merged".
			boolean merged = false;

			// If true, then this channel will be in the list, but will not
			// be shown to the user.
			boolean hidden = false;

			// If true, this is the channel associated with the current
			// user.
			boolean thisIsTheUsersMyWorkspaceChannel = false;

			// If true, this is a user channel.
			boolean thisIsUserChannel = entryProvider.isUserChannel(channel);

			// If true, this is a "special" site.
			boolean isSpecialSite = entryProvider.isSpecialSite(channel);

			if ( thisIsUserChannel
                    && userId.equals(
                            entryProvider.getSiteUserId(channel)) )
            {
                thisIsTheUsersMyWorkspaceChannel = true;
            }

			//
			// Don't put the channels of other users in the merge list.
			// Go to the next item in the loop.
			//
			if (thisIsUserChannel && !thisIsTheUsersMyWorkspaceChannel)
			{
				continue;
			}

			// Only add to the list if the user can access this channel.
			if (entryProvider.allowGet(entryProvider.getReference(channel)))
			{
				// Merge *almost* everything the user can access.
				if (thisIsTheUsersMyWorkspaceChannel)
				{
					// Don't merge the user's channel in with a
					// group channel.  If we're on the "MyWorkspace"
					// tab, then it's okay to merge.
					if (isOnWorkspaceTab)
					{
						merged = true;
					}
					else
					{
						merged = false;
					}
				}
				else
				{
					//
					// If we're the admin, and we're on our "MyWorkspace" tab, then only
					// use our channel (handled above).  We'd be overloaded if we could
					// see everyone's events.
					//
					if (isSuperUser && isOnWorkspaceTab)
					{
						merged = false;
					}
					else
					{
						// Set it to merged if the channel was specified in the merged
						// channel list that we got from the portlet configuration.
						if (isOnWorkspaceTab)
						{
							merged = true;
						}
						else
						{
							merged =
								currentlyMergedchannels.containsKey(
									entryProvider.getReference(channel));
						}
					}
				}

				addThisChannel = true;

				// Hide user or "special" sites from the user interface merge list.
				if (thisIsUserChannel || isSpecialSite)
				{
					// Hide the user's own channel from them.
					hidden = true;
				}
			}

			if (addThisChannel)
			{
				String siteDisplayName = "";
				
				// There is no point in getting the display name for hidden
                // items.
                if (!hidden)
                {
                    String displayNameProperty = entryProvider.getProperties(
                            channel).getProperty(
                            entryProvider.getProperties(channel)
                                    .getNamePropDisplayName());

                    // If the channel has a displayName property and use that
                    // instead.
                    if (displayNameProperty != null
                            && displayNameProperty.length() != 0)
                    {
                        siteDisplayName = displayNameProperty;
                    } 
                    else
                    {
                        String channelName = "";

                        Site site = entryProvider.getSite(channel);

                        if (site != null)
                        {
                            boolean isCurrentSite = currentSiteId.equals(site.getId());

                            //
                            // Hide and force the current site to be merged.
                            //
                            if (isCurrentSite)
                            {
                                hidden = true;
                                merged = true;
                            } 
                            else
                            {
                                // Else just get the name.
                                channelName = site.getTitle();
                                siteDisplayName = channelName + " ("
                                        + site.getId() + ") ";
                            }
                        }
                    }
                }

				this.add(
					new MergedChannelEntryImpl(
						entryProvider.getReference(channel),
						siteDisplayName,
						merged,
						!hidden));
			}
		}

		// MergedchannelEntry implements Comparable, so the sort will work correctly.
		Collections.sort(this);
	} // loadFromPortletConfig
	
	/**
	 * Forms an array of all channel references to which the user has read access.
	 * @param refMaker
	 * @return
	 */
	public String[] getAllPermittedChannels(ChannelReferenceMaker refMaker)
	{
	    List finalList = new ArrayList();
	    String [] returnArray = null;
	    List siteList = SiteService.getAllowedSites(true, false, null);
	    
	    Iterator it = siteList.iterator();
	    
	    // Add all the references to the list.
	    while ( it.hasNext() )
	    {
			Site site = (Site) it.next();
			finalList.add(refMaker.makeReference(site.getId()));
	    }
	    
	    // Make the array that we'll return
	    returnArray = new String[finalList.size()];
	    
	    for ( int i=0; i < finalList.size(); i++ )
	    {
	        returnArray[i] = (String) finalList.get(i);
	    }
	    
	    return returnArray;	    
	}
	
	/**
	 * This gets a list of channels from the portlet configuration information.
	 * Channels here can really be a channel or a schedule from a site.
	 * @param primarychannelReference
	 * @param mergedInitParameterValue
	 * @return
	 */
	public String[] getChannelReferenceArrayFromDelimitedString(
		String primarychannelReference,
		String mergedInitParameterValue)
	{
		String mergedChannels = null;

		// Get a list of the currently merged channels.  This is a delimited list.
		mergedChannels =
			StringUtil.trimToNull(
				mergedInitParameterValue);

		String[] mergedChannelArray = null;

		// Split the configuration string into an array of channel references.
		if (mergedChannels != null)
		{
			mergedChannelArray = mergedChannels.split(ID_DELIMITER);
		}
		else
		{
			// If there are no merged channels, default to the primary channel.
			mergedChannelArray = new String[1];
			mergedChannelArray[0] = primarychannelReference;
		}

		return mergedChannelArray;
	} // getChannelReferenceArrayFromDelimitedString
	
	/**
     * Create a channel reference map from an array of channel references.
     * @param mergedChannelArray
     * @return
     */
    private Map makeChannelMap(String[] mergedChannelArray)
    {
        // Make a map of those channels that are currently merged.
		Map currentlyMergedchannels = new HashMap();

		if (mergedChannelArray != null)
		{
			for (int i = 0; i < mergedChannelArray.length; i++)
			{
				currentlyMergedchannels.put(
					mergedChannelArray[i],
					Boolean.valueOf(true));
			}
		}
        return currentlyMergedchannels;
    }

    /**
	 * Loads data input by the user into this list and then saves the list to
	 * the portlet config information.  The initContextForMergeOptions() function
	 * must have previously been called.
	 * @param runData
	 */
	public void loadFromRunData(RunData runData)
	{
		Iterator it = this.iterator();

		while (it.hasNext())
		{
			MergedEntry entry = (MergedEntry) it.next();

			// If the group is even mentioned in the parameters, then
			// it means that the checkbox was selected.  Deselected checkboxes
			// will not be present in the parameter list.
			if (runData.getParameters().getString(entry.getReference())
				!= null)
			{
				entry.setMerged(true);
			}
			else
			{
				//
				// If the entry isn't visible, then we can't "unmerge" it due to
				// the lack of a checkbox in the user interface.
				//
				if (entry.isVisible())
				{
					entry.setMerged(false);
				}
			}
		}
	}

	/**
	 * Loads data input by the user into this list and then saves the list to
	 * the portlet config information.  The initContextForMergeOptions() function
	 * must have previously been called.
	 * @param runData
	 */
	public String getDelimitedChannelReferenceString()
	{
		StringBuffer mergedReferences = new StringBuffer("");
			
		Iterator it = this.iterator();
		boolean firstEntry = true;
			
		while (it.hasNext())
		{
			MergedEntry entry = (MergedEntry) it.next();
				
			if (entry.isMerged())
			{
				// Add a delimiter, if appropriate.
				if ( !firstEntry )
				{
					mergedReferences.append(ID_DELIMITER);
				}
				else
				{
					firstEntry = false;
				}
					
				// Add to our list
				mergedReferences.append(entry.getReference());
			}
		}
			
		// Return the delimited list of merged references
		return mergedReferences.toString();
	}
		
	/**
	 * Returns an array of merged references.
	 * @return
	 */
	public List getReferenceList()
	{
		List references = new ArrayList();
			
		Iterator it = this.iterator();
			
		while (it.hasNext())
		{
			MergedEntry mergedEntry = (MergedEntry) it.next();
				
			// Only add it to the list if it has been merged.
			if (mergedEntry.isMerged())
			{
				references.add(mergedEntry.getReference());
			}
		}
			
		return references;
	}
}
