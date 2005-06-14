<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="published_id"/>
    <xsl:template match="identifier_string[parent::generic_identifier/type_label='publishedId']">
        <xsl:copy>
            <xsl:value-of select="$published_id"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
