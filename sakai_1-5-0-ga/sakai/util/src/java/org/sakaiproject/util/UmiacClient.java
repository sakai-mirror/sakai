/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/UmiacClient.java,v 1.14 2005/02/08 20:45:30 janderse.umich.edu Exp $
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

// package
package org.sakaiproject.util;

// imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.cover.MemoryService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.user.UserEdit;

/**
 * <p>UmiacClient is the Interface to the UMIAC service (classlist directory services).
 * This class translates calls from UmiacResponse to the UMIAC protocol and sends the protocol
 * via a TCP m_socket to a UMIAC server.</p>
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.14 $
 */
public class UmiacClient
	implements CacheRefresher
{
	/** Umiac's network port address. */
	protected int m_port = -1;

	/** Umiac's network host address. */
	protected String m_host = null;
	
	/** A cache of calls to umiac and the results. */
	protected Cache m_callCache = null;

	/** The one and only Umiac client. */
	protected static UmiacClient M_instance = null;

	/** Get the umiac client. */
	public static UmiacClient getInstance()
	{
		if (M_instance == null) new UmiacClient();
		return M_instance;

	}	// getInstance

	/**
	* Construct, using the default production UMIAC instance.
	*/
	protected UmiacClient()
	{
		// get the umiac address and port from the configuration service
		m_host = ServerConfigurationService.getString("umiac.address", null);
		try
		{
			m_port = Integer.parseInt(ServerConfigurationService.getString("umiac.port", "-1"));
		}
		catch (Exception ignore) {}

		if (m_host == null)
		{
			Log.warn("chef", this + " - no 'umiac.address' in configuration");
		}
		if (m_port == -1)
		{
			Log.warn("chef", this + " - no 'umiac.port' in configuration (or invalid integer)");
		}

		// build a synchronized map for the call cache, automatiaclly checking for expiration every 15 mins.
		m_callCache = MemoryService.newHardCache(this, 15 * 60);

		if (M_instance == null)
		{
			M_instance = this;
		}

	}	// UmiacClient

	/**
	* finalize
	*/
	protected void finalize()
	{
		if (this == M_instance)
		{
			M_instance = null;
		}

	}	// finalize

	/**
	* Sets the port for the target UMIAC server.
	* @param port The UMIAC port.
	*/
	public void setPort(int port)
	{
		m_port = port;

	}	// setPort
	
	/**
	* Gets the port for the target UMIAC server.
	* @return The UMIAC port
	*/
	public int getPort()
	{
		return m_port;

	}	// getPort
	
	/**
	* Sets the host name for the target UMIAC server.
	* @param host The UMIAC host name.
	*/
	public void setHost(String host)
	{
		m_host = host;

	}	// setHost
	
	/**
	* Gets the host name for the target UMIAC server.
	* @return The host name for UMIAC.
	*/
	public String getHost()
	{
		return m_host;

	}	// getHost

	/**
	* Get a name for a user by id, setting a first name and last name into the UserEdit.
	* @param edit The UserEdit with the id to check, and to be filled in with the information found.
	* @exception IdUnusedException If there is no user by this id.
	*/
	public void setUserNames(UserEdit edit)
		throws IdUnusedException
	{
		String[] name = getUserName(edit.getId());
		if (name == null)
		{
			throw new IdUnusedException(edit.getId());
		}

		edit.setLastName(name[2]);
		edit.setFirstName(name[3]);

	}	// setUserNames

	/**
	* See if a user by this id exists.
	* @param id The user uniqname.
	* @return true if a user by this id exists, false if not.
	*/
	public boolean userExists(String id)
	{
		return (getUserName(id) != null);
	}

	/**
	* Get the names for this user.
	* @param id The user uniqname.
	* @return String[]: [0] sort name, [1] display name, [2] last name, and [3] first name,
	* or null if the user does not exist.
	*/
	public String[] getUserName(String id)
	{
		String command = "getSortName," + id;

		// check the cache - still use expired entries
		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			return (String[]) m_callCache.getExpiredOrNot(command);
		}

		Vector result = makeRawCall(command);

		// if there are no results, then we don't know the user
		if (	(result == null)
			||	(result.size() != 1)
			||	(((String) result.elementAt(0)).trim().length() == 0)
			||	(((String) result.elementAt(0)).trim().equals(",")))
		{
			// cache the miss for 60 minutes
			if (m_callCache != null) m_callCache.put(command, null, 60 * 60);

			return null;
		}

		String[] rv = new String[4];

		// get the result string for the sort name [0]
		//Fred, Farley Fish
		rv[0] = ((String) result.elementAt(0)).trim();

		// parse the result string for the display name [1]
		// Farley Fish Fred
		String[] res = StringUtil.split(rv[0], ",");
		String displayName = "";
		if (res.length > 1)
		{
			displayName = res[1] + " ";
		}
		rv[1] = displayName.concat(res[0]);

		// set the last name [2]
		rv[2] = res[0];
	
		// set the first name [3]
		if (res.length > 1)
		{
			rv[3] = res[1];
		}
		else
		{
			rv[3] = "";
		}

		// cache the results for 60 minutes
		if (m_callCache != null) m_callCache.put(command, rv, 60 * 60);

		return rv;

	}	// getUserName

	/**
	* Get a name for a group (class) by id.
	* @param id The group id.
	* @return The group's full display name.
	* @exception IdUnusedException If there is no group by this id.
	*/
	public String getGroupName(String id)
		throws IdUnusedException
	{
		String command = "getClassInfo," + id;

		// check the cache - still use expired entries
		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			String rv = (String) m_callCache.getExpiredOrNot(command);
			if (rv == null)
			{
				throw new IdUnusedException(id);
			}
			return rv;
		}

		Vector result = makeRawCall(command);
		
		// if there are no results, or the one has no "|", then we don't know the user
		if (	(result == null)
			||	(result.size() < 1)
			||	(((String)result.elementAt(0)).indexOf("|") == -1))
		{
			// cache the miss for 60 minutes
			if (m_callCache != null) m_callCache.put(command, null, 60 * 60);

			throw new IdUnusedException(id);
		}
		
		// parse the result string for the name
		// 0: 1410|32220|Teach with Tech|https://chefproject.org/chef/portal/group/ED504|SEM|2002-09-03|2002-12-11|405000|School Of Education|EDU|Education
		String[] res = StringUtil.split((String)result.elementAt(0),"|");
		String name = res[2];

		// cache the result for 60 minutes
		if (m_callCache != null) m_callCache.put(command, name, 60 * 60);

		return name;

	}	// getGroupName

	/**
	* Get all the members of a group, by their group role
	* @param id The group id.
	* @return The group's full display name.
	* @exception IdUnusedException If there is no group by this id.
	*/
	public Map getGroupRoles(String id)
		throws IdUnusedException
	{
		String command = "getClasslist," + id;

		// check the cache - still use expired entries
		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			Map rv = (Map) m_callCache.getExpiredOrNot(command);
			if (rv == null)
			{
				throw new IdUnusedException(id);
			}
			return rv;
		}

		Vector result = makeRawCall(command);
		
		// if there are no results, or the one has no "|", then we don't know the user
		if (	(result == null)
			||	(result.size() < 1)
			||	(((String)result.elementAt(0)).indexOf("|") == -1))
		{
			// cache the miss for 60 minutes
			if (m_callCache != null) m_callCache.put(command, null, 60 * 60);

			throw new IdUnusedException(id);
		}

		// store user id key to role value in return table
		HashMap map = new HashMap();

		// parse each line of the result
		// 1: Burger,Ham F|HBURGER|11234541|-||Instructor|E
		for (int i = 0; i < result.size(); i++)
		{
			String[] res = StringUtil.split((String)result.elementAt(i),"|");
			String uid = res[1].toLowerCase();
			String role = res[5];
			map.put(uid, role);
		}
		
		// cache the result for 60 minutes
		if (m_callCache != null) m_callCache.put(command, null, 60 * 60);

		return map;

	}	// getGroupRoles

	/**
	* Get all the external realm ids the user has a role in, and the role
	* @param id The user id.
	* @return A map of he realm id to the role for this user.
	*/
	public Map getUserSections(String id)
	{
		String command = "getUserSections," + id;

		// check the cache - still use expired entries
		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			return (Map) m_callCache.getExpiredOrNot(command);
		}

		// make the call		
		Vector result = makeRawCall(command);

		// parse the results
		Map map = new HashMap();
	
		// if there are no results, or the one has no "|", then we have no class roles for the user
		if (	!(	(result == null)
				||	(result.size() < 1)
				||	(((String)result.elementAt(0)).indexOf("|") == -1)))
		{
			// parse each line of the result
			// ex: 2004,2,A,SI,653,001|Instructor|Primary
			for (int i = 0; i < result.size(); i++)
			{
				String[] res = StringUtil.split((String)result.elementAt(i),"|");
				String exId = res[0];
				String role = res[1];
				map.put(exId, role);
			}
		}

		// cache the results for 60 minutes
		if (m_callCache != null) m_callCache.put(command, map, 60 * 60);

		return map;

	}	// getUserSections

	/**
	* Get all the members of a group, by their group role.
	* A group is defined by multiple external ids.
	* @param id The group id.
	* @return The group's full display name.
	* @exception IdUnusedException If there is no group by this id.
	*/
	public Map getGroupRoles(String[] id)
		throws IdUnusedException
	{
		StringBuffer commandBuf = new StringBuffer();
		commandBuf.append("getGroupMemberships");
		for (int i = 0; i < id.length; i++)
		{
			commandBuf.append(",");
			
			// convert the comma separated id string to a "|" separated one
			String[] parts = StringUtil.split(id[i], ",");
			commandBuf.append(parts[0]);
			for (int p = 1; p < parts.length; p++)
			{
				commandBuf.append("|");
				commandBuf.append(parts[p]);
			}
		}
		String command = commandBuf.toString();

		// check the cache - still use expired entries
		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			Map rv = (Map) m_callCache.getExpiredOrNot(command);
			if (rv == null)
			{
				throw new IdUnusedException(id[0]);
			}
			return rv;
		}

		Vector result = makeRawCall(command);

		// if there are no results, or the one has no "|", then we don't know the user
		if (	(result == null)
			||	(result.size() < 1)
			||	(((String)result.elementAt(0)).indexOf("|") == -1))
		{
			// cache this miss for 60 minutes
			if (m_callCache != null) m_callCache.put(command, null, 60 * 60);

			throw new IdUnusedException(id[0]);
		}

		// store user id key to role value in return table
		HashMap map = new HashMap();

		// parse each line of the result
		// 2002,2,A,EDUC,547,003|Arrdvark Axis Betterman|Betterman,Arrdvark Axis|BOBOBOBOB|12345678|3|Student|E
		for (int i = 0; i < result.size(); i++)
		{
			String[] res = StringUtil.split((String)result.elementAt(i),"|");

			String uid = res[3].toLowerCase();
			String role = res[6];

			// the user res[3] has id res[3], display name res[1], sort name res[2]
			// TODO: do we really need to store the user names? -ggolden
			//m_userNames.put(uid, res[1]);
			//m_userSortNames.put(uid, res[2]);

			// if there's a role for this user already, and it's instructor, just leave it
			String roleAlready = (String) map.get(uid);
			if (	(roleAlready == null)
				||	(roleAlready.equals("Student") && role.equals("Instructor")))
			{
				map.put(uid, role);
			}
		}

		// cache the results for 60 minutes
		if (m_callCache != null) m_callCache.put(command, map, 60 * 60);

		return map;

	}	// getGroupRoles
	
	/**
	* Send a getClasslist command to UMIAC and return the resulting
	* output as a Vector of String[] objects (one String[] per output line:
	* sort_name|uniqname|umid|level (always "-")|credits|role|enrl_status
	*
	*/
	public Vector getClassList (String year, String term, String campus, String subject, String course, String section)
	{
		String command = "getClasslist," + year + "," + term + "," + campus + "," + subject + "," + course + "," + section + "\n\n";

		// check the cache - still use expired entries
		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			return (Vector) m_callCache.getExpiredOrNot(command);
		}

		Vector result = makeRawCall(command);
		
		// if there are no results
		if (	(result == null)
			||	(result.size() < 1))
		{
			// Log.warn("chef", "Cannot get the class record for " + year + "," + term + "," + campus + "," + subject + "," + course + "," + section);

			// TODO: NOTE: no cache miss, cause this might be a transitory error? -ggolden
			return null;
		}

		Vector rv = new Vector();
		for (int i = 0; i < result.size(); i++)
		{
			String[] res = StringUtil.split((String)result.elementAt(i),"|");
			rv.add(res);
		}

		// cache the results for 60 minutes
		if (m_callCache != null) m_callCache.put(command, rv, 60 * 60);

		return rv;
		
	} // getClassList
	
	/**
	* Send a command to the UMIAC using the getUserInfo batch API and return
	* the output as a Vector of String[] objects (one String[] per output line):
	* // 0: |Fred Farley Fish|70728384|FFISH|08-25-2000 01:46:30 PM
	*
	*/
	public Vector getUserInformation (String ids)
		throws Exception
	{
		String command = "getUserInfo," + ids + "\n\n";

		// check the cache - still use expired entries
		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			return (Vector) m_callCache.getExpiredOrNot(command);
		}

		Vector result = makeRawCall(command);
		
		// if there are no results
		if (	(result == null)
			||	(result.size() < 1))
		{
			// TODO: NOTE: no cache miss, cause this might be a transitory error? -ggolden
			throw new Exception("UMIAC: no results: " + command);
		}

		Vector rv = new Vector();
		for (int i = 0; i < result.size(); i++)
		{
			// 0: |Fred Farley Fish|70728384|FFISH|08-25-2000 01:46:30 PM
			String[] res = StringUtil.split((String)result.elementAt(i),"|");
			rv.add(res);
		}

		// cache the results for 60 minutes
		if (m_callCache != null) m_callCache.put(command, rv, 60 * 60);

		return rv;
		
	} // getUserInformation
	
	
	/**
	* getInstructorSections
	*
	* Sends a getInstructorSections command to UMIAC and returns the resulting
	* output as a Vector of String[] objects (one String[] per output line of
	* year|term_id|campus_code|subject|catalog_nbr|class_section|title|url|
	* component (e.g., LAB, DISC)|role|subrole|crosslist ("CL" if cross-listed,
	* blank if not)  Note: String[] will have 12 elements if CL is appended and
	* 11 elements if it is not.
	* Output is terminated by EOT
	* As opposed to getInstructorClasses, the output is in data format rather
	* that text (e.g., A rather than Ann Arbor, D rather than Dearborn).
	* The results include cross-listed sections.
	* @param id is the Instructor's uniqname
	* @param term_year is expressed in four-digit numbers: 2003
	* @param term is expressed as a single-digit number:
	* 1 - SUMMER
	* 2 - FALL
	* 3 - WINTER
	* 4 - SPRING
	* 5 - SPRING/SUMMER
	*/
	public Vector getInstructorSections (String id, String term_year, String term)
		throws IdUnusedException
	{
		String command = "getInstructorSections," + id + "," + term_year + "," + term + "\n\n";

		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			Vector rv = (Vector) m_callCache.getExpiredOrNot(command);
			if (rv == null)
			{
				throw new IdUnusedException(id);
			}

			return (Vector) m_callCache.getExpiredOrNot(command);
		}

		Vector result = makeRawCall(command);
		
		// if there are no results
		if (	(result == null)
			||	(result.size() < 1))
		{
			// cache the miss for 60 minutes
			if (m_callCache != null) m_callCache.put(command, null, 60 * 60);

			throw new IdUnusedException(id);
		}

		Vector rv = new Vector();
		for (int i = 0; i < result.size(); i++)
		{
			String[] res = StringUtil.split((String)result.elementAt(i),"|");
			rv.add(res);
		}

		// cache the results for 60 minutes
		if (m_callCache != null) m_callCache.put(command, rv, 60 * 60);

		return rv;
		
	} // getInstructorSections
	
	/**
	* getInstructorClasses
	*
	* Sends a getInstructorClasses command to UMIAC and returns the resulting
	* output as a Vector of String[] objects (one String[] per output line of 
	* 14 fields: year|term (text)|campus (text)|subject|catalog_nbr|title|(legacy, always blank)|
	* class_section|url|units taken (blank for instructors)|component (LEC, DIS, LAB, etc)|
	* role|subrole|enrl_status)
	* Output is terminated by EOT
	* As opposed to getInstructorSections, the output is in a human-readable format
	* rather than data (e.g., Ann Arbor rather than A, Dearborn rather than D).
	* The results do not include cross-listed sections.
	* @param id is the Instructor's uniqname
	* @param term_year is expressed in four-digit numbers: 2003
	* @param term is expressed as a single-digit number:
	* 1 - SUMMER
	* 2 - FALL
	* 3 - WINTER
	* 4 - SPRING
	* 5 - SPRING/SUMMER
	*/
	public Vector getInstructorClasses (String id, String term_year, String term)
		throws IdUnusedException
	{
		String command = "getInstructorClasses," + id + "," + term_year + "," + term + "\n\n";

		if ((m_callCache != null) && (m_callCache.containsKeyExpiredOrNot(command)))
		{
			Vector rv = (Vector) m_callCache.getExpiredOrNot(command);
			if (rv == null)
			{
				throw new IdUnusedException(id);
			}

			return (Vector) m_callCache.getExpiredOrNot(command);
		}

		Vector result = makeRawCall(command);
		
		// if there are no results
		if (	(result == null)
			||	(result.size() < 1))
		{
			// cache the miss for 60 minutes
			if (m_callCache != null) m_callCache.put(command, null, 60 * 60);

			throw new IdUnusedException(id);
		}

		Vector rv = new Vector();
		for (int i = 0; i < result.size(); i++)
		{
			String[] res = StringUtil.split((String)result.elementAt(i),"|");
			rv.add(res);
		}

		// cache the results for 60 minutes
		if (m_callCache != null) m_callCache.put(command, rv, 60 * 60);

		return rv;
		
	} // getInstructorClasses

	/**
	* Send a command to UMIAC and returns the resulting
	* output as a vector of strings (one per response line).
	* No caching is attempted.
	* @param umiacCommand The UMIAC command string.
	* @return A Vector of Strings, one per response line.
	*/
	public Vector makeRawCall(String umiacCommand)
	{
		if (Log.getLogger("chef").isDebugEnabled())
		{
			Log.debug("chef", this + ".makeCall: " + umiacCommand);
		}
		PrintWriter out = null;
		BufferedReader in = null;
		Socket socket = null;
		
		Vector v = new Vector();

		// Open up a m_socket and write out the command to UMIAC.
		try
		{
			socket = new Socket(m_host,m_port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(umiacCommand);
			String inString;

			// Now that we have a m_socket open and have thrown our command to UMIAC,
			// we listen for data returning on the m_socket.  UMIAC returns pipe-
			// delimited lines of data. When finished, UMIAC sends a single line
			// of "EOT".
			inString = in.readLine();
			while ((inString != null) && !inString.equalsIgnoreCase("EOT"))
			{
				// Fill up the vector with the strings being returned
				v.add(inString);
				
				// get the next line
				inString = in.readLine();
			}
		}
		catch (IOException e){Logger.warn("UMIAC: " + e);}
		finally
		{
			// Close all the m_sockets, regardless of what happened
			try
			{
				if (in != null) in.close();
				if (out != null) out.close();
				if (socket != null) socket.close();
			}
			catch (Exception ignore){Logger.warn("UMIAC: " + ignore);}
		}
		
		return v;

	}	// makeRawCall

	/*******************************************************************************
	* CacheRefresher implementation
	*******************************************************************************/

	/**
	* Get a new value for this key whose value has already expired in the cache.
	* @param key The key whose value has expired and needs to be refreshed.
	* @param oldValue The old exipred value of the key.
	* @param event The event which triggered this refresh.
	* @return a new value for use in the cache for this key; if null, the entry will be removed.
	*/
	public Object refresh(Object key, Object oldValue, Event event)
	{
		// instead of refreshing when an entry expires, let it go and we'll get it again if needed -ggolden
		// return makeRawCall((String)key);

		return null;

	}	// refresh

}	// class UmiacClient

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/UmiacClient.java,v 1.14 2005/02/08 20:45:30 janderse.umich.edu Exp $
*
**********************************************************************************/
