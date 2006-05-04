<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://purl.org/atom/ns#" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <xsl:variable name="baseurl" select="/entity-service/request-properties/@server-url"/>
        <feed version="0.3" >
            <title>
                <xsl:value-of select="/entity-service/entity/properties/property[@name='_title']"/>
            </title>
            <tagline>Sakai Wiki Changes</tagline>
            <link rel="alternate" type="text/html" href="{$baseurl}"/>
            <id>
                <xsl:value-of select="$baseurl"/>
            </id>
            <modified>
                <xsl:value-of
                    select="/entity-service/entity/properties/property[@name='_datestamp']"/>
            </modified>
            <generator>Sakai Wiki Atom Generator</generator>
            <xsl:for-each select="/entity-service/entity/changes/change">
                <entry>
                    <title>
                        <xsl:value-of select="@name"/>
                    </title>
                    <link rel="alternate" type="text/html"
                        href="{concat($baseurl,'/access/wiki',@name,',',@revision,'.html')}"/>
                    <created>
                        <xsl:value-of select="@last-modified"/>
                    </created>
                    <issued>
                        <xsl:value-of select="@last-modified"/>
                    </issued>
                    <modified>
                        <xsl:value-of select="@last-modified"/>
                    </modified>
                    <id>
                        <xsl:value-of
                            select="concat($baseurl,'/access/wiki',@name,',',@revision,'.html')"/>
                    </id>
                    <summary><xsl:value-of select="content/contentdigest"/>...</summary>
                </entry>
            </xsl:for-each>
        </feed>
    </xsl:template>
</xsl:stylesheet>
