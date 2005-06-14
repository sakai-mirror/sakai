<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: createDayDropdown.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template name="dayDropdown">
  <xsl:param name="dSelected"/>
   	<option value='1'><xsl:if test="$dSelected=1" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>1</option>
	<option value='2'><xsl:if test="$dSelected=2" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2</option>
	<option value='3'><xsl:if test="$dSelected=3" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>3</option>
	<option value='4'><xsl:if test="$dSelected=4" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>4</option>
	<option value='5'><xsl:if test="$dSelected=5" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>5</option>
	<option value='6'><xsl:if test="$dSelected=6" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>6</option>
  </xsl:template>
</xsl:stylesheet>
				