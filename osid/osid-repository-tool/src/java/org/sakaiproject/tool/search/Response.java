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
package org.sakaiproject.tool.search;

import org.sakaiproject.tool.util.*;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.xml.sax.*;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 *  SRW
 */
public class Response extends SearchResultBase 
{
	private String criteria = null;
  /**
   * Constructor
   */
    public Response() 
    {
        super();
    }

    public void initialize(QueryBase query) 
    {
        try
        {			
			this.criteria = query.getSearchString();
        }
        catch (Exception ex) {}
    }

  /**
   * Parse the response
   */
    public void doParse() 
    {
        try
        {
			org.osid.shared.Type thumbnailType = new Type("mit.edu","partStructure","thumbnail");
			org.osid.shared.Type urlType = new Type("mit.edu","partStructure","URL");

			ComponentManager.getInstance();		
			Object o = ComponentManager.get("org.osid.repository.RepositoryManager");
			if (o != null) {
				// initialize the repository manager and get its repositories
				org.osid.repository.RepositoryManager repositoryManager = (org.osid.repository.RepositoryManager)o;
				repositoryManager.assignOsidContext(new org.osid.OsidContext());
				repositoryManager.assignConfiguration(new java.util.Properties());
				org.osid.repository.RepositoryIterator repositoryIterator = repositoryManager.getRepositories();
				while (repositoryIterator.hasNextRepository()) {
					org.osid.repository.AssetIterator assetIterator = repositoryIterator.nextRepository().getAssetsBySearch(
																															this.criteria,
																															new Type("mit.edu","search","keyword"),
																															new SharedProperties());
					System.out.println("Query complete");
					while (assetIterator.hasNextAsset())
					{
						org.osid.repository.Asset asset = assetIterator.nextAsset();
						MatchItem item = new MatchItem();
						String displayName = asset.getDisplayName();
						String description = asset.getDescription();
						String content = null;
						try {
							content = (String)asset.getContent();
							item.setPreviewUrl(content);
							item.setPersistentUrl(content);
						} catch (Throwable th) {
							// ignore
						}
                    
						// Look for a thumbnail or url part and show any.  We only want one and their might be more than one in
						// different records.  To minimize what we know about the data source, we don't want to limit which record types we search.
						boolean foundThumbnail = false;
						boolean foundURL = false;
						org.osid.repository.RecordIterator recordIterator = asset.getRecords();
						while (recordIterator.hasNextRecord()) {
							org.osid.repository.PartIterator partIterator = recordIterator.nextRecord().getParts();
							while (partIterator.hasNextPart()) {
								org.osid.repository.Part part = partIterator.nextPart();
								org.osid.shared.Type partStructureType = part.getPartStructure().getType();
								if ( (!foundThumbnail) && (partStructureType.isEqual(thumbnailType)) ) {
									item.setPreviewUrl((String)part.getValue());
									foundThumbnail = true;
								} else if ( (!foundURL) && (partStructureType.isEqual(urlType)) ) {
									item.setPersistentUrl((String)part.getValue());
									foundURL = true;
								}
							}
						}
						item.setPreviewText(displayName);
						item.setDescription(description);
						item.setPersistentText(displayName);
						addItem(item);
						
						if ((item.getPreviewUrl() == null) && (item.getPersistentUrl() != null)) {
							item.setPreviewUrl(item.getPersistentUrl());
						}
					}
				}
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}