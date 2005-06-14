<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="core_id"/>
    <xsl:template match="identifier_string[parent::generic_identifier/type_label='coreId']">
        <xsl:copy>
            <xsl:value-of select="$core_id"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
