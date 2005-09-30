package org.sakaiproject.imslaunch.util;

import javax.faces.model.SelectItem;

public class Util 
{
	public Util()
	{
		// constructor
	}
	
   public static String getOptionLabel(SelectItem[] obj, int val) 
   { 
       String label = "";        
       if (obj == null) 
           label = "NoObject";       
 
       for (int i = 0; i < obj.length; ++i)         
           if (i == val - 1)                
               label = obj[i].getLabel();
  
       return label;
    }     
}
