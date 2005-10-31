<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	version="1.0">
<xsl:variable name="initday" select="schedule/month/@startdayweek"/>
<xsl:variable name="cols" select="7"/>
<xsl:template match="/">

<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <fo:layout-master-set>
    <!-- page layout -->
    
      <!-- layout for the first page -->
    <fo:simple-page-master master-name="mmschedule"
                  page-height="21cm" 
                  page-width="29.7cm"
                  margin-top="0.5cm" 
                  margin-bottom="0.5cm" 
                  margin-left="0.5cm" 
                  margin-right="0.5cm">
      <fo:region-body margin-top="1.0cm"/>	  	
      <fo:region-before precedence="true" extent="1.0cm"/>
      <fo:region-after precedence="true" extent="0.5cm"/>
    </fo:simple-page-master>

  </fo:layout-master-set>
  <!-- end: defines page layout -->
    
    
  <!-- actual layout -->
  <fo:page-sequence master-reference="mmschedule">

  <fo:static-content flow-name="xsl-region-before">
	<fo:block font-size="18pt" 
            font-family="sans-serif" 
            line-height="1cm"
            space-after.optimum="1pt"
            color="black"
            text-align="center"
            padding-top="0pt">      
         Schedule for <xsl:value-of select="schedule/uid"/> 
           - <xsl:call-template name="prMonth">
           	 <xsl:with-param name="dt" select="schedule/list[1]/@dt"/>
           </xsl:call-template>
    	</fo:block>    	
   </fo:static-content> 
   
   <fo:static-content flow-name="xsl-region-after">
		<fo:block text-align="end" 
			font-size="10pt" font-family="serif" line-height="1em + 2pt">
			Page (<fo:page-number/>)
      </fo:block>
  </fo:static-content>
   
  <fo:flow flow-name="xsl-region-body">
	<xsl:apply-templates select="schedule"/>	
    </fo:flow> 
  </fo:page-sequence>
</fo:root>
</xsl:template>

<!-- Main template -->
<xsl:template match="schedule">
 	 <xsl:variable name="line-cnt" select="ceiling((count(./list)+ $initday) div 7 )"/>
   		 
	<fo:table table-layout="fixed" width="100%">
		<fo:table-column column-width="proportional-column-width(1)" number-columns-repeated="{$cols}"/>
		<fo:table-body>
      	 <fo:table-row line-height="10pt" >
    		<fo:table-cell><fo:block text-align="center">Sunday</fo:block></fo:table-cell>
     		<fo:table-cell><fo:block text-align="center">Monday</fo:block></fo:table-cell>
     		<fo:table-cell><fo:block text-align="center">Tuesday</fo:block></fo:table-cell>
    		<fo:table-cell><fo:block text-align="center">Wednesday</fo:block></fo:table-cell>
    		<fo:table-cell><fo:block text-align="center">Thursday</fo:block></fo:table-cell>
     		<fo:table-cell><fo:block text-align="center">Friday</fo:block></fo:table-cell>
      		<fo:table-cell><fo:block text-align="center">Saturday</fo:block></fo:table-cell>
          </fo:table-row>
   		  <!-- set table height to keep most monthly calendar in  one page -->
   		  <xsl:variable name="lineHeight">
   			<xsl:choose>
   				<xsl:when test="$line-cnt = 6">
   					<xsl:value-of select="30"/>
   				</xsl:when>
   				<xsl:otherwise>
   					<xsl:value-of select="36"/>
   				</xsl:otherwise>
   			</xsl:choose> 
   		 </xsl:variable>	
   
   		 <xsl:if test="$initday &gt; 0">
   		  	  <fo:table-row line-height="{$lineHeight}mm" >
 				<xsl:call-template name="emptyday">
 					<xsl:with-param name="cnt" select="$initday -1"/>
 				</xsl:call-template>

 				<xsl:for-each select="list[position() = 1]">
 					<xsl:apply-templates select=".">
 					<xsl:with-param name="evcnt" select="$cols - $initday "/>
 					</xsl:apply-templates> 
 				</xsl:for-each>
   			  </fo:table-row>
   		  </xsl:if>	 

   		  <xsl:for-each select="list[(position() +  $initday) mod 7 = 1]">   		     		  
   	  		 <fo:table-row line-height="{$lineHeight}mm" >	  		    	  		   
 				<xsl:apply-templates select=".">
 					<xsl:with-param name="evcnt" select="7"/>
 				</xsl:apply-templates>
 			</fo:table-row>
		</xsl:for-each>
	</fo:table-body>
	</fo:table>
</xsl:template>
<!-- print each day activities -->
<xsl:template match="list">
<xsl:param name="evcnt"/> 
   <xsl:variable name="dtPt" select="substring-before(substring-after(@dt, '/'), '/')"/>
   <xsl:if test="$dtPt = 1 and $initday &gt; 0">
   		<xsl:call-template name="emptyday">
 				<xsl:with-param name="cnt" select="1"/>
 		</xsl:call-template> 
   </xsl:if>
  
   <fo:table-cell  border-color="blue" border-width="0.5pt" border-style="solid"> 
 	
 	<xsl:choose>
 	<xsl:when test="not(event)">
 		<fo:block font-size="10pt" text-align="center">
			<xsl:value-of select="substring-before(substring-after(@dt, '/'), '/')"/>
		</fo:block>
	</xsl:when>
	
	<xsl:otherwise> 
	  <fo:block> 
		<fo:table table-layout="fixed"  border-top-color="blue" border-top-width="0.5pt" border-top-style="solid"
		border-left-color="blue" border-left-width="0.5pt" border-left-style="solid"
		background-color="white">
		<fo:table-column column-width="4cm"/>
		<fo:table-body>
		 	<fo:table-row line-height="4mm" >
	   		<fo:table-cell>   
	   			<fo:block font-size="8pt" text-align="right">
				<xsl:value-of select="substring-before(substring-after(@dt, '/'), '/')"/>
			</fo:block>
			</fo:table-cell>
			</fo:table-row>			
			<xsl:apply-templates select="event[1]"/>
		</fo:table-body>
		</fo:table> 
	</fo:block>
	</xsl:otherwise>
    </xsl:choose>
 </fo:table-cell>
 	
 <xsl:if test="$evcnt &gt; 1">
 	<xsl:choose>
 		<xsl:when test="following-sibling::list[1]">
 			<xsl:apply-templates select="following-sibling::list[1]">
 				<xsl:with-param name="evcnt" select="$evcnt - 1"/>
 			</xsl:apply-templates>
 		</xsl:when> 
 		<xsl:otherwise>
 			<xsl:call-template name="emptyday">
 					<xsl:with-param name="cnt" select="$evcnt"/>
 			</xsl:call-template>
 		</xsl:otherwise>
 	</xsl:choose>	
 </xsl:if>
</xsl:template>
<!-- print each event infor -->
<xsl:template match="event">
 border-top-color="blue" border-top-width="0.5pt" border-top-style="solid"
	border-left-color="blue" border-left-width="0.5pt" border-left-style="solid"

	<fo:table-row line-height="4mm" >
	<fo:table-cell>            
	<xsl:choose>
	<xsl:when test="row">
		<xsl:for-each select="row">
			<xsl:for-each select="col">
				<xsl:for-each select="subEvent">
					<xsl:call-template name="evitem">
						<xsl:with-param name="ev" select="."/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:when>
	<xsl:otherwise>
		<xsl:call-template name="evitem">
			<xsl:with-param name="ev" select="."/>
		</xsl:call-template>
	</xsl:otherwise>
	</xsl:choose>
	</fo:table-cell>
	</fo:table-row>
	
	<xsl:if test="following-sibling::event[1]">
		<xsl:apply-templates select="following-sibling::event[1]"/>
	</xsl:if>
	
</xsl:template>

<xsl:template name="evitem">
<xsl:param name="ev"/>
	<fo:block font-size="8pt" text-align="start">
		<xsl:call-template name="dtconv">
			<xsl:with-param name="fromdt" 
				select="$ev/@from"/>
		</xsl:call-template>
		<xsl:value-of select="title"/>
	</fo:block>	
</xsl:template>

<!-- print blank date -->
<xsl:template name="emptyday">
<xsl:param name="cnt"/>

	 <xsl:if test="$cnt &gt; 0">  
   
  	<fo:table-cell background-color="#C0C0C0" border-color="blue" border-width="0.5pt" border-style="solid"> 
     <fo:block>
     <fo:leader leader-pattern="dots" 
                   rule-thickness="1.0pt"          
                   leader-length="0.1cm"/> 
      </fo:block>
      </fo:table-cell>
    
  		<xsl:call-template name="emptyday">
      		<xsl:with-param name="cnt" select="$cnt - 1"/>
     	</xsl:call-template>
  	 </xsl:if>
</xsl:template>

<!-- Reforme the time -->
<xsl:template name="dtconv">
<xsl:param name="fromdt"/>
	
	<xsl:variable name="tfromdt" select="substring-before($fromdt, ':')"/>
	<xsl:choose>
	<xsl:when test="$tfromdt = 0 ">
		12:00AM:
	</xsl:when>
	<xsl:when test="$tfromdt &lt; 12">
		<xsl:value-of select="$fromdt"/>AM:
	</xsl:when>
		
	<xsl:when test="$tfromdt = 12">
		<xsl:value-of select="$fromdt"/>PM:
	</xsl:when>
	
	<xsl:when test="$tfromdt &gt; 12">
		<xsl:value-of select="$tfromdt - 12"/>:<xsl:value-of select="substring-after($fromdt, ':')"/>PM:
	</xsl:when>
	</xsl:choose>
</xsl:template>

<!-- Print month name -->
<xsl:template name="prMonth">
<xsl:param name="dt"/>

	<xsl:variable name="mm" select="substring-before($dt, '/')"/>
	<xsl:variable name="yy" select="substring-after(substring-after($dt, '/'), '/')"/>
	
	<xsl:choose>
	<xsl:when test="$mm=1">January </xsl:when>   
   	<xsl:when test="$mm=2">February </xsl:when>    
   	<xsl:when test="$mm=3">March </xsl:when>   
    <xsl:when test="$mm=4">April </xsl:when>   
    <xsl:when test="$mm=5">May </xsl:when>   
    <xsl:when test="$mm=6">June </xsl:when>
    <xsl:when test="$mm=7">July </xsl:when>
   	<xsl:when test="$mm=8">August </xsl:when>
   	<xsl:when test="$mm=9">September </xsl:when>
  	<xsl:when test="$mm=10">October </xsl:when>
   	<xsl:when test="$mm=11">November </xsl:when>
   	<xsl:when test="$mm=12">December </xsl:when>	
	<xsl:otherwise> </xsl:otherwise>
	</xsl:choose>
	<xsl:value-of select="$yy"/>
</xsl:template>
</xsl:stylesheet>
  

