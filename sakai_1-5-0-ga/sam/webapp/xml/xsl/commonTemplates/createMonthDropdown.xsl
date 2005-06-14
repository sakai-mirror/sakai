<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: createMonthDropdown.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:template name="monthDropdown">
    <xsl:param name="mSelected"/>
   	<option value='1'><xsl:if test="$mSelected=1" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>JAN</option>
	<option value='2'><xsl:if test="$mSelected=2" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>FEB</option>
	<option value='3'><xsl:if test="$mSelected=3" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>MAR</option>
	<option value='4'><xsl:if test="$mSelected=4" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>APR</option>
	<option value='5'><xsl:if test="$mSelected=5" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>MAY</option>
	<option value='6'><xsl:if test="$mSelected=6" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>JUN</option>
	<option value='7'><xsl:if test="$mSelected=7" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>JUL</option>
	<option value='8'><xsl:if test="$mSelected=8" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>AUG</option>
	<option value='9'><xsl:if test="$mSelected=9" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>SEP</option>
	<option value='10'><xsl:if test="$mSelected=10" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>OCT</option>
	<option value='11'><xsl:if test="$mSelected=11" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>NOV</option>
	<option value='12'><xsl:if test="$mSelected=12" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>DEC</option>
  </xsl:template>
</xsl:stylesheet>
				