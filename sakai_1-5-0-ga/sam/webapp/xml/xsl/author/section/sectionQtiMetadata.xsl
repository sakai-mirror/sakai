<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: sectionQtiMetadata.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 	<!--****************************************************************************** -->
	<xsl:template match="qtimetadatafield">
		<xsl:param name="keyName" />
		<xsl:param name="display" />
		<xsl:param name="otherName" />
		<xsl:param name="return" />
		<xsl:param name="value" />
		
		<xsl:apply-templates select="fieldlabel">
		<xsl:with-param name="keyName" select="$keyName" />
		<xsl:with-param name="display" select="$display" />
		<xsl:with-param name="pos" select="position()" />
		<xsl:with-param name="otherName" select="$otherName"/>
		<xsl:with-param name="return" select="$return"/>
        <xsl:with-param name="value" select="$value"/>
        
		</xsl:apply-templates>
 	</xsl:template>
	<!--****************************************************************************** -->
	<xsl:template match="fieldlabel">
		<xsl:param name="keyName" /> <!-- must provide -->
		<xsl:param name="display" /> <!-- optional -->
		<xsl:param name="pos" /> <!-- optional -->
		<xsl:param name="otherName" /><!-- optional -->
	 	<xsl:param name="return" /><!-- optional // returns xpath-->
	 	<xsl:param name="value" />
	 	
		<xsl:variable name="xpath" select="concat('stxx/section/qtimetadata/qtimetadatafield[', $pos,']/fieldentry')"/>
		<xsl:variable name="fieldlabel" select="." />
		<xsl:if test="$fieldlabel=$keyName">
			<xsl:variable name="fieldentry" select="../fieldentry" />
			<xsl:choose>
			<xsl:when test="$otherName!=''">
				<input type="{$display}" name= "{$otherName}" value ="{$fieldentry}"/>
			</xsl:when>
			<xsl:when test="$display=''">
				<xsl:choose>
					<xsl:when test="$return='xpath'">
						<xsl:value-of select="$xpath" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$fieldentry" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<input type="{$display}" name= "{$xpath}" value ="{$fieldentry}"/>
			</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<!--****************************************************************************** -->
</xsl:stylesheet>