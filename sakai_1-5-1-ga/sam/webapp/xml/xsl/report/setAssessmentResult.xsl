<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan" version="1.0">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="assessment_id"/>
    <xsl:param name="assessment_result"/>
    <xsl:template match="extension_assessment_result[parent::assessment_result/@ident_ref=$assessment_id]">
        <xsl:copy-of select="$assessment_result/extension_assessment_result"/>
    </xsl:template>
</xsl:stylesheet>
