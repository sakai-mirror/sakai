<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	version="1.0">
<!-- Columne -->	
<xsl:variable name="col-cnt" select="count(/schedule/list)"/>
<xsl:variable name="col5-len" select="5.6"/>
<xsl:variable name="col7-len" select="4"/>
<xsl:variable name="frmlen">
   <xsl:choose>
   	 <xsl:when test="$col-cnt &gt; 1">
   	 	<xsl:value-of select="28"/>
   	 </xsl:when>
   	 <xsl:otherwise><xsl:value-of select="18"/>
   </xsl:otherwise>
   </xsl:choose>
</xsl:variable>
<xsl:variable name="begin-st" select="/schedule/start-time"/>
<xsl:variable name="begin" select="number(substring-before($begin-st, ':'))"/>
<xsl:variable name="maxConcurrentEvents" select="/schedule/maxConcurrentEvents"/>

<xsl:variable name="tbPt" select="4"/>
<xsl:variable name="leftBarPt">
	<xsl:choose>
 		<xsl:when test="$col-cnt &gt; 1">
     		<xsl:value-of select="1.5"/>
 		</xsl:when>
 		<xsl:otherwise>
 			<xsl:value-of select="1.8"/>
 		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
<xsl:variable name="rightMarginPt">
	<xsl:choose>
 		<xsl:when test="$col-cnt &gt; 1">
     		<xsl:value-of select="0"/>
 		</xsl:when>
 		<xsl:otherwise>
 			<xsl:value-of select="0.5"/>
 		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<xsl:variable name="topMarginPt">
	<xsl:choose>
 		<xsl:when test="$col-cnt &gt; 1">
     		<xsl:value-of select="0.5"/>
 		</xsl:when>
 		<xsl:otherwise>
 			<xsl:value-of select="1"/>
 		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<xsl:variable name="hoursPerPage" select="10"/>
<xsl:variable name="lengthPerHour" select="2.0"/> 
<xsl:variable name="heightPerTimeslot" select="0.5"/>
<xsl:variable name="heightLineHeader" select="0.4"/>

<xsl:variable name="borderSize" select="0.8"/> 


<xsl:variable name="evFontSize" select="8"/>   
<xsl:variable name="ev2FontSize" select="8"/>   


<xsl:variable name="begin-st" select="/schedule/start-time"/>
<xsl:variable name="begin" select="number(substring-before($begin-st, ':'))"/>  
<xsl:variable name="end" select="$begin + 10"/>
<xsl:variable name="end-st" select="concat(string($end), ':00')"/>	

<!-- Reformat time from minutes to the hour in decimal -->
<xsl:template name="setTimeslot">
<xsl:param name="timePt"/>
<xsl:variable name="timeHH" select="number(substring-before($timePt, ':'))"/>
<xsl:variable name="timeMM" select="ceiling(number(substring-after($timePt, ':')) * 100 div 60)"/>
<xsl:variable name="tmpTimePt" select="$timeHH + $timeMM div 100"/>

<xsl:choose>
	<xsl:when test="$tmpTimePt &lt; $begin">
		<xsl:value-of select="$begin"/>
	</xsl:when>
	<xsl:when test="$tmpTimePt &gt; $end">
		<xsl:value-of select="$end"/>
	</xsl:when>
	<xsl:otherwise><xsl:value-of select="$tmpTimePt"/>
	</xsl:otherwise>
</xsl:choose>	
</xsl:template>	

<!-- Template to process weekly data day by day -->
<xsl:template match="list">
	<xsl:param name="containerPt"/>

	<!-- Set columns length -->	
	<xsl:variable name="col-len">
	<xsl:choose>
     	<xsl:when test="@maxConcurrentEvents &gt; 1 ">
     		<xsl:choose>
     		<xsl:when test="$col-cnt = 1">
     			<xsl:value-of select="$frmlen"/>
     		</xsl:when>
     		<xsl:otherwise>
     			<xsl:value-of select="$col7-len * @maxConcurrentEvents"/>
     		</xsl:otherwise>
     		</xsl:choose>	
     	</xsl:when>
     	<xsl:when test="$col-cnt = 7">
     		 <xsl:value-of select="$col7-len"/>
   		</xsl:when>
   		<xsl:when test="$col-cnt = 5">
   			<xsl:choose>
   			<xsl:when test="$maxConcurrentEvents &gt; 1">
   				<xsl:value-of select="$col7-len"/>
   			</xsl:when>
   			<xsl:otherwise> 
   				<xsl:value-of select="$col5-len"/>
   			</xsl:otherwise>
   			</xsl:choose>
   		</xsl:when>
   		<xsl:otherwise>18</xsl:otherwise>
   	</xsl:choose>
	</xsl:variable>
   		
	<xsl:variable name="leftPt">
	<xsl:choose>
 	   <xsl:when test="(($containerPt mod $frmlen )+ $col-len) &gt; $frmlen">
 	    	<xsl:value-of select="0"/> 
 	   </xsl:when>
 	   <xsl:otherwise>
 	 		<xsl:value-of select="$containerPt mod $frmlen"/>
 	   </xsl:otherwise>
 	</xsl:choose>
	</xsl:variable>
 	 
	<xsl:if test="($containerPt mod $frmlen = 0) or ($containerPt mod $frmlen + $col-len) &gt; $frmlen">
  			<fo:block break-before="page"/>
  	</xsl:if>
  	
  	<xsl:call-template name="evspace">
				<xsl:with-param name="fromPt" select="$begin-st"/>
				<xsl:with-param name="toPt" select="$end-st"/>
				<xsl:with-param name="leftPt" select="$leftPt"/>
				<xsl:with-param name="col-len" select="$col-len"/>
	</xsl:call-template>
	
  	<xsl:call-template name="lineHeader">
  			<xsl:with-param name="dt" select="@dt"/>
  			<xsl:with-param name="dayofweek" select="@dayofweek"/>
  			<xsl:with-param name="leftPt" select="$leftPt"/>
  			<xsl:with-param name="col-len" select="$col-len"/>
	</xsl:call-template>
	

	<xsl:call-template name="evBlock">
				<xsl:with-param name="fromPt" select="$begin-st"/>
				<xsl:with-param name="toPt" select="$end-st"/>
				<xsl:with-param name="leftPt" select="$leftPt"/>
				<xsl:with-param name="col-len" select="$col-len"/>
	</xsl:call-template>
	
	<xsl:for-each select="event">
		<xsl:choose>
		<xsl:when test="./row">
			<xsl:call-template name="evoverlap">
				<xsl:with-param name="ev" select="."/>
				<xsl:with-param name="col-len" select="$col-len"/>
				<xsl:with-param name="leftPt" select="$leftPt"/>
			</xsl:call-template>
		</xsl:when>	
		<xsl:otherwise>
			<xsl:call-template name="evitem">
				<xsl:with-param name="ev" select="."/>
				<xsl:with-param name="col-len" select="$col-len"/>
				<xsl:with-param name="leftPt" select="$leftPt"/>
			</xsl:call-template>
		</xsl:otherwise>
		
		
		</xsl:choose>	
		
	
	</xsl:for-each>
	 
	 <xsl:variable name="adjLen">
 	   <xsl:choose>
 	   <xsl:when test="($containerPt mod $frmlen + $col-len) &gt; $frmlen">
 	    	<xsl:value-of select="$frmlen - ($containerPt mod $frmlen)"/>  
 	   </xsl:when>
 	   <xsl:otherwise>
 	 		<xsl:value-of select="0"/>
 	   </xsl:otherwise>
 	   </xsl:choose>
 	 </xsl:variable>
 	 <xsl:text>>&#x000a;</xsl:text>
 	 <!-- Recursively print next date's events -->
   	<xsl:apply-templates select="following-sibling::list[1]">
    	<xsl:with-param name="containerPt" select="$containerPt + $col-len + $adjLen"/>	
	</xsl:apply-templates>

</xsl:template>


<!-- 
	Print regular event
 -->
<xsl:template name="evitem">
<xsl:param name="ev"/>
<xsl:param name="col-len"/>	
<xsl:param name="leftPt"/>

<xsl:variable name="fromPt">
 <xsl:call-template name="setTimeslot">
 	<xsl:with-param name="timePt" select="$ev/@from"/>
 </xsl:call-template>
</xsl:variable>

<xsl:variable name="toPt">
 <xsl:call-template name="setTimeslot">
 	<xsl:with-param name="timePt" select="$ev/@to"/>
 </xsl:call-template>
</xsl:variable>

<xsl:variable name="blHeight">
<xsl:choose>
   <xsl:when test="$toPt = $fromPt">0.25</xsl:when>
   <xsl:otherwise>
	<xsl:value-of select="$toPt - $fromPt"/>
   </xsl:otherwise>
</xsl:choose>
</xsl:variable>

<xsl:variable name="timePt">
		<xsl:value-of select="$fromPt - $begin"/>
</xsl:variable>

<fo:block-container  background-color="white" 
	border-color="black"  border-style="solid" border-width="0.1pt"
    position="absolute">
 
	<xsl:attribute name="height">
		<xsl:value-of select="$blHeight * $lengthPerHour"/>cm
	</xsl:attribute>
	<xsl:attribute name="width">
		<xsl:value-of select="$col-len"/>cm
	</xsl:attribute>	
	<xsl:attribute name="top">
    	<xsl:value-of 
		select="$timePt * $lengthPerHour + $heightLineHeader"/>cm	
	</xsl:attribute>
	<xsl:attribute name="left">
		<xsl:value-of select="$leftPt"/>cm
	</xsl:attribute>
	
	 <fo:table table-layout="fixed" background-color="white"
	border-top-color="black" border-top-style="solid" border-top-width="0.1pt"
	border-left-color="black" border-left-style="solid" border-left-width="0.1pt"
	border-right-color="black" border-right-style="solid" border-right-width="0.1pt"
	>
		
		<fo:table-column column-width="{$col-len}cm"/>
      	<fo:table-body>
		<!-- Print sequence name -->
		<fo:table-row line-height="{$tbPt}mm" ><fo:table-cell>
			<fo:block font-weight="bold" font-size="{$evFontSize}pt" text-align="start">
				<xsl:value-of select="$ev/grp"/>
			</fo:block>
		</fo:table-cell></fo:table-row>
		<!-- Print Topic -->
		<fo:table-row line-height="{$tbPt}mm" >
		  <fo:table-cell>
			<fo:block font-size="{$evFontSize}pt" text-align="start">
				<fo:inline keep-together.within-line="always">
					<xsl:value-of select="$ev/title"/>
				</fo:inline> 
			</fo:block>
		    </fo:table-cell>
		  </fo:table-row>
		<!-- Print location -->
		<fo:table-row line-height="{$tbPt}mm" ><fo:table-cell>
			<fo:block font-size="{$evFontSize}pt" text-align="start">
			<xsl:value-of select="$ev/place"/></fo:block>
		</fo:table-cell></fo:table-row>
		<!-- Print faculty -->
		<fo:table-row line-height="{$tbPt}mm" ><fo:table-cell>
			<fo:block font-size="{$evFontSize}pt" text-align="start"><xsl:value-of select="$ev/faculty"/></fo:block>
		</fo:table-cell></fo:table-row>
		<!-- Print action type -->
		<fo:table-row line-height="{$tbPt}mm" ><fo:table-cell>
		<fo:block font-size="{$evFontSize}pt" text-align="start"><xsl:value-of select="$ev/type"/></fo:block>
		</fo:table-cell></fo:table-row>
    	</fo:table-body>
    </fo:table> 
 
  </fo:block-container>

</xsl:template>
<!-- 
 Print overlapping event
 -->
<xsl:template name="evoverlap">
<xsl:param name="ev"/>
<xsl:param name="col-len"/>
<xsl:param name="leftPt"/>

	<xsl:variable name="fromPt">
 	<xsl:call-template name="setTimeslot">
 	<xsl:with-param name="timePt" select="$ev/@from"/>
 	</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="toPt">
 	<xsl:call-template name="setTimeslot">
 		<xsl:with-param name="timePt" select="$ev/@to"/>
 	</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="subcol-len" select="round($col-len div count($ev/row[1]/col))"/> 
	
	<xsl:for-each select="$ev">		
		<xsl:for-each select="row/col">
		    <xsl:variable name="subPos" select="position()"/>
		    <xsl:for-each select="subEvent">
				<xsl:call-template name="evitem">
					<xsl:with-param name="ev" select="."/>
					<xsl:with-param name="col-len" select="$subcol-len"/>
					<xsl:with-param name="leftPt" select="$leftPt + ($subPos - 1) * $subcol-len"/>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:for-each>
</xsl:template>

<xsl:template name="evspace">
<xsl:param name="fromPt"/>
<xsl:param name="toPt"/>
<xsl:param name="leftPt"/>
<xsl:param name="col-len"/>

<xsl:variable name="startPt">
 <xsl:call-template name="setTimeslot">
 	<xsl:with-param name="timePt" select="$fromPt"/>
 </xsl:call-template>
</xsl:variable>

<xsl:variable name="endPt">
 <xsl:call-template name="setTimeslot">
 	<xsl:with-param name="timePt" select="$toPt"/>
 </xsl:call-template>
</xsl:variable>

<fo:block-container  border-color="black" 
	border-style="solid" border-width="0.1pt" position="absolute">
			
	<xsl:attribute name="width">
		<xsl:value-of select="$col-len"/>cm
	</xsl:attribute>
	
	<xsl:attribute name="left">
		<xsl:value-of select="$leftPt"/>cm
	</xsl:attribute>

	<xsl:attribute name="height">
		<xsl:value-of select="($endPt - $startPt) * $lengthPerHour "/>cm
	</xsl:attribute>
	
	<xsl:attribute name="top">
		<xsl:value-of 
		select=" ($startPt - $begin) * $lengthPerHour + $heightLineHeader "/>cm
	</xsl:attribute>
		
	<xsl:variable name="initcnt" select="($endPt - $startPt) * 4"/>	
	<xsl:call-template name="meridan">
		<xsl:with-param name="cnt" select="ceiling($initcnt)"/>	
	</xsl:call-template>
        
</fo:block-container>
</xsl:template>

<xsl:template name="meridan">
<xsl:param name="cnt"/>

	<xsl:if test="$cnt &gt; 0">  
  		<fo:block line-height="{$heightPerTimeslot}cm"  text-align="center"  border-color="white" border-style="solid"  
		border-collapse="collapse"  border-width="0.4pt" background-color="#C0C0C0"> 
     	<fo:leader leader-pattern="dots" 
                   rule-thickness="1.0pt"          
                   leader-length="0.1cm"/> 
      	</fo:block>
  		<xsl:call-template name="meridan">
      		<xsl:with-param name="cnt" select="$cnt - 1"/>
     	</xsl:call-template>
  	 </xsl:if>
</xsl:template>


<xsl:template name="lineHeader">
<xsl:param name="dt"/>
<xsl:param name="dayofweek"/>
<xsl:param name="leftPt"/>
<xsl:param name="col-len"/>

<fo:block-container background-color="white"  position="absolute">
			
	<xsl:attribute name="width">
		<xsl:value-of select="$col-len"/>cm
	</xsl:attribute>
	
	<xsl:attribute name="left">
		<xsl:value-of select="$leftPt"/>cm
	</xsl:attribute>

	<xsl:attribute name="height">
		<xsl:value-of select="$heightLineHeader"/>cm
	</xsl:attribute>
	
	<xsl:attribute name="top">
		<xsl:value-of 
		select="0"/>cm
	</xsl:attribute>
    <fo:block font-size="8pt" text-align="center" vertical-align="bottom">
        <xsl:call-template name="weekname">
           <xsl:with-param name="day" select="$dayofweek"/>
        </xsl:call-template> <xsl:value-of select="$dt"/>
	</fo:block> 
</fo:block-container>
</xsl:template>	
<xsl:template name="weekname">
<xsl:param name="day"/>
	
	<xsl:choose>
	<xsl:when test="$day=0">Sunday </xsl:when>   
   	<xsl:when test="$day=1">Monday </xsl:when>   
   	<xsl:when test="$day=2">Tuesday </xsl:when>   
   	<xsl:when test="$day=3">Wednesday </xsl:when>   
    <xsl:when test="$day=4">Thursday </xsl:when>   
    <xsl:when test="$day=5">Friday </xsl:when>   
    <xsl:when test="$day=6">Saturday </xsl:when>   
	<xsl:otherwise> </xsl:otherwise>
	</xsl:choose>
</xsl:template>


<xsl:template name="evBlock">
<xsl:param name="fromPt"/>
<xsl:param name="toPt"/>
<xsl:param name="leftPt"/>
<xsl:param name="col-len"/>

<xsl:variable name="startPt">
 <xsl:call-template name="setTimeslot">
 	<xsl:with-param name="timePt" select="$fromPt"/>
 </xsl:call-template>
</xsl:variable>

<xsl:variable name="endPt">
 <xsl:call-template name="setTimeslot">
 	<xsl:with-param name="timePt" select="$toPt"/>
 </xsl:call-template>
</xsl:variable>
<!-- border-collapse="collapse"   -->
<fo:block-container  border-color="black" 
	border-style="solid" border-width="0.1pt" position="absolute">
			
	<xsl:attribute name="width">
		<xsl:value-of select="$col-len"/>cm
	</xsl:attribute>
	
	<xsl:attribute name="left">
		<xsl:value-of select="$leftPt"/>cm
	</xsl:attribute>

	<xsl:attribute name="height">
		<xsl:value-of select="($endPt - $startPt) * $lengthPerHour "/>cm
	</xsl:attribute>
	
	<xsl:attribute name="top">
		<xsl:value-of 
		select=" ($startPt - $begin) * $lengthPerHour + $heightLineHeader "/>cm
	</xsl:attribute>
		
</fo:block-container>
</xsl:template>

<xsl:template name="left-bar">
<xsl:param name="intimePt"/>
<xsl:if test="$intimePt &gt; 0">
 	<xsl:call-template name="left-bar">
      	<xsl:with-param name="intimePt" select="$intimePt - 1"/>
     	</xsl:call-template>
</xsl:if>
 	
<xsl:variable name="timePt" select="$intimePt + $begin"/>

<xsl:variable name="in-time">
 	<xsl:choose>
		<xsl:when test="$timePt = 0 ">
			<xsl:value-of select="$timePt + 12"/>:00AM
		</xsl:when>
 		<xsl:when test="$timePt  &lt; 12">
			<xsl:value-of select="$timePt"/>:00AM
		</xsl:when>
		<xsl:when test="$timePt = 12">
			<xsl:value-of select="$timePt"/>:00PM
		</xsl:when>
		<xsl:when test="$timePt &gt; 23.55">12:00AM
	      </xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$timePt - 12"/>:00PM
		</xsl:otherwise>
	</xsl:choose>
 </xsl:variable>
 
 <xsl:variable name="top-val" select="$intimePt * 2"/>

	<fo:block line-height="{$heightPerTimeslot}cm" font-size="{$evFontSize}pt" text-align="right" vertical-align="top"
	border-top-color="white" border-top-width="{$borderSize}pt" border-top-style="solid">
		<xsl:value-of select="$in-time"/>
	</fo:block>
		
	<fo:block line-height="{$heightPerTimeslot}cm"  text-align="center"
	border-top-color="white" border-top-width="{$borderSize}pt" border-top-style="solid"> 
     	<fo:leader leader-pattern="dots" 
                   rule-thickness="1.0pt"          
                   leader-length="0.1cm"/> 
    </fo:block>
	<fo:block line-height="{$heightPerTimeslot}cm"  text-align="center"
	border-top-color="white" border-top-width="{$borderSize}pt" border-top-style="solid"> 
     	<fo:leader leader-pattern="dots" 
                   rule-thickness="1.0pt"          
                   leader-length="0.1cm"/> 
     </fo:block>
    <fo:block line-height="{$heightPerTimeslot}cm"  text-align="center"
    border-top-color="white" border-top-width="{$borderSize}pt" border-top-style="solid"> 
     	<fo:leader leader-pattern="dots" 
                   rule-thickness="1.0pt"          
                   leader-length="0.1cm"/> 
      	</fo:block>

</xsl:template>

</xsl:stylesheet>