<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template name="itemSurvey">
<xsl:variable name="scale" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='PREDEFINED_SCALE']/following-sibling::fieldentry"/>
              
 <table width ="95%" align="right">
     
   <tr>  
      <td>  <input type="radio" name="scaleName" value="YES"><xsl:if test="$scale='YES'"><xsl:attribute name="checked">True</xsl:attribute></xsl:if> </input> <font class="bold">Yes, No </font>    </td> 
      <td>   <input type="radio" name="scaleName" value="STRONGLY_AGREE"><xsl:if test="$scale='STRONGLY_AGREE'"><xsl:attribute name="checked">True</xsl:attribute></xsl:if> </input><font class="bold"> Strongly Disagree --> Strongly Agree</font></td>    
   </tr>
   <tr>  
        <td>      <input type="radio" name="scaleName" value="AGREE"><xsl:if test="$scale='AGREE'"><xsl:attribute name="checked">True</xsl:attribute></xsl:if> </input> <font class="bold">  Disagree , Agree  </font> </td> 
        <td>  <input type="radio" name="scaleName" value="EXCELLENT"><xsl:if test="$scale='EXCELLENT'"><xsl:attribute name="checked">True</xsl:attribute></xsl:if> </input><font class="bold">  Unacceptable --> Excellent     </font></td>     
   </tr>
   <tr>  
      <td> <input type="radio" name="scaleName" value="UNDECIDED"><xsl:if test="$scale='UNDECIDED'"><xsl:attribute name="checked">True</xsl:attribute></xsl:if> </input><font class="bold"> Disagree, Undecided, Agree    </font> </td>    
      <td> <input type="radio" name="scaleName" value="5"><xsl:if test="$scale='5'"><xsl:attribute name="checked">True</xsl:attribute></xsl:if> </input><font class="bold">  1 --> 5      </font> </td>    
   </tr>
   
   <tr>   
       <td> <input type="radio" name="scaleName" value="AVERAGE"><xsl:if test="$scale='AVERAGE'"><xsl:attribute name="checked">True</xsl:attribute></xsl:if> </input><font class="bold"> Below Average  -->   Above Average  </font></td>    
       <td> <input type="radio" name="scaleName" value="10"><xsl:if test="$scale='10'"><xsl:attribute name="checked">True</xsl:attribute></xsl:if> </input><font class="bold"> 1 --> 10     </font> </td>     
   </tr>
 </table> 
</xsl:template>
</xsl:stylesheet>