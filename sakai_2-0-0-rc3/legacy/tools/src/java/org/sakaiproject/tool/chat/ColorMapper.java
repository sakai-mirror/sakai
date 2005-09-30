/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/chat/ColorMapper.java,v 1.1 2005/04/14 20:22:11 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.chat;

// imports
import java.lang.reflect.Array;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

/**
* <p>ColorMapper is a wrapper for a Hashtable that maps user names (or any set of Strings) to colors.</p>
* <p>The colors are standard names for HTML colors.</p>

* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public class ColorMapper
{
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("chat");
    
	// The index of the next color in the COLORS array that will be assigned to a name
	protected int m_next = 0;
	
	// A mapping of names to colors
	protected Map m_map;

	// An array of Strings representing standard HTML colors.
	protected static final String[] COLORS = 
			{ "red", "blue", "green", "orange", "firebrick", "teal", "goldenrod", 
			  "darkgreen", "darkviolet", "lightslategray", "peru", "deeppink", "dodgerblue", 
			  "limegreen", "rosybrown", "cornflowerblue", "crimson", "turquoise", "darkorange", 
			  "blueviolet", "royalblue", "brown", "magenta", "olive", "saddlebrown", "purple", 
			  "coral", "mediumslateblue", "sienna", "mediumturquoise", "hotpink", "lawngreen", 
			  "mediumvioletred", "slateblue", "indianred", "slategray", "indigo", "darkcyan",
			  "springgreen", "darkgoldenrod", "steelblue", "darkgray", "orchid", "darksalmon", 
			  "lime", "gold", "darkturquoise", "navy", "orangered",  "darkkhaki", "darkmagenta", 
			  "darkolivegreen", "tomato", "aqua", "darkred", "olivedrab" 
			};

	// the size of the COLORS array
	protected static final int NumColors = Array.getLength(COLORS);
	
	/**
	* Construct the ColorMapper.
	*/
	public ColorMapper()
	{
		m_map = new Hashtable();
		
	}	// ColorMapper
	
	/**
	* get the color associated with a name.  if name not already associated with a color,
	* make a new association and determine the next color that will be used.
	*/
	public String getColor(String name)
	{
		String color;
		if(m_map.containsKey(name))
		{
			color = (String) m_map.get(name);
		}
		else
		{
			color = COLORS[m_next++];
			m_map.put(name, color);
			if(m_next >= NumColors)
			{
				m_next = 0;
			}
		}
		
		return color;
		
	}	// getColor
	
	/**
	* Returns the mapping of names to colors.
	*/
	public Map getMapping()
	{
		return m_map;
		
	}	// getColors
		
	/**
	* Returns the index of the next color in the COLORS array that will be assigned to a name.
	*/
	public int getNext()
	{
		return m_next;
		
	}	// getNext
	
	/**
	* Returns the entire array of color names.
	*/
	public String[] getColors()
	{
		return COLORS;
		
	}	// getColors
	
	/**
	* Returns the size of the array of color names.
	*/
	public int getNum_colors()
	{
		return NumColors;
		
	}	// getNum_colors
	
}	// ColorMapper

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/chat/ColorMapper.java,v 1.1 2005/04/14 20:22:11 ggolden.umich.edu Exp $
*
***********************************************************************************/
