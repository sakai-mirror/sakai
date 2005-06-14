<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: createYearDropdown.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:template name="yearDropdown">
  <xsl:param name="ySelected"/>
 <!-- 	<option value='1997'><xsl:if test="$ySelected=1997" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>1997</option>
	<option value='1998'><xsl:if test="$ySelected=1998" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>1998</option>
	<option value='1999'><xsl:if test="$ySelected=1999" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>1999</option>
	<option value='2000'><xsl:if test="$ySelected=2000" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2000</option>
	<option value='2001'><xsl:if test="$ySelected=2001" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2001</option>
	<option value='2002'><xsl:if test="$ySelected=2002" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2002</option>
	 -->
	 <option value='2003'><xsl:if test="$ySelected=2003" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2003</option>
	<option value='2004'><xsl:if test="$ySelected=2004" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2004</option>
	<option value='2005'><xsl:if test="$ySelected=2005" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2005</option>
	<option value='2006'><xsl:if test="$ySelected=2006" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2006</option>
	<option value='2007'><xsl:if test="$ySelected=2007" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2007</option>
  </xsl:template>
</xsl:stylesheet>
				