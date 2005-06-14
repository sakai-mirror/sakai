<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:import href="../result/identity.xsl"/>
    <xsl:param name="user_id"/>
    <xsl:template match="identifier_string[parent::generic_identifier/type_label='userId']">
        <xsl:copy>
            <xsl:value-of select="$user_id"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
