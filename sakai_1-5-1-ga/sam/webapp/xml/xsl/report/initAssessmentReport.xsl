<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
    <xsl:output doctype-system="http://www.imsglobal.org/question/qtiv1p2p1/XMLSchemav1p2p1/xmla/ims_qtiresv1p2p1schema/dtds/qtiresfullncdtd/ims_qtiresv1p2p1.dtd"/>
    <xsl:template match="/questestinterop">
        <qti_result_report>
            <result>
                <context>
                    <name/>
                    <generic_identifier>
                        <type_label>userId</type_label>
                        <identifier_string/>
                    </generic_identifier>
                    <generic_identifier>
                        <type_label>coreId</type_label>
                        <identifier_string/>
                    </generic_identifier>
                    <generic_identifier>
                        <type_label>publishedId</type_label>
                        <identifier_string/>
                    </generic_identifier>
                    <date>
                        <type_label/>
                        <datetime/>
                    </date>
                </context>
                <xsl:apply-templates select="assessment"/>
            </result>
        </qti_result_report>
    </xsl:template>
    <xsl:template match="assessment">
        <assessment_result>
            <xsl:attribute name="ident_ref">
                <xsl:value-of select="@ident"/>
            </xsl:attribute>
            <xsl:attribute name="asi_title">
                <xsl:value-of select="@title"/>
            </xsl:attribute>
            <asi_metadata>
                <xsl:apply-templates select="qtimetadata/qtimetadatafield[fieldlabel='ASSESSMENT_RELEASED_TO' or fieldlabel='ANONYMOUS_GRADING']"/>
            </asi_metadata>
            <num_sections>
                <xsl:value-of select="count(section)"/>
            </num_sections>
            <xsl:apply-templates select="section"/>
            <extension_assessment_result/>
        </assessment_result>
    </xsl:template>
    <xsl:template match="section">
        <section_result>
            <xsl:attribute name="ident_ref">
                <xsl:value-of select="@ident"/>
            </xsl:attribute>
            <xsl:attribute name="asi_title">
                <xsl:value-of select="@title"/>
            </xsl:attribute>
            <num_items>
                <xsl:value-of select="count(item)"/>
            </num_items>
            <xsl:apply-templates select="item"/>
            <extension_section_result/>
        </section_result>
    </xsl:template>
    <xsl:template match="item">
        <item_result>
            <xsl:attribute name="ident_ref">
                <xsl:value-of select="@ident"/>
            </xsl:attribute>
            <xsl:attribute name="asi_title">
                <xsl:value-of select="@title"/>
            </xsl:attribute>
            <xsl:apply-templates select="descendant::response_lid | descendant::response_str | descendant::response_grp"/>
            <xsl:apply-templates select="resprocessing/outcomes"/>
            <extension_item_result/>
        </item_result>
    </xsl:template>
    <xsl:template match="response_lid | response_str | response_grp">
        <xsl:variable name="item_ident" select="@ident"/>
        <response>
            <xsl:attribute name="ident_ref">
                <xsl:value-of select="$item_ident"/>
            </xsl:attribute>
            <response_form>
                <xsl:variable name="correct_responses">
                    <xsl:apply-templates select="ancestor::item/resprocessing/respcondition[setvar[@action='Add']>0]/conditionvar//varequal[@respident=$item_ident]"/>
                </xsl:variable>
                <xsl:apply-templates select="xalan:nodeset($correct_responses)/correct_response[not(.=following::correct_response)]"/>
            </response_form>
            <num_attempts>0</num_attempts>
        </response>
    </xsl:template>
    <xsl:template match="outcomes">
        <outcomes>
            <xsl:apply-templates select="decvar"/>
        </outcomes>    
    </xsl:template>
    <xsl:template match="decvar">
        <score>
            <xsl:attribute name="varname">
                <xsl:value-of select="@varname"/>
            </xsl:attribute>
            <score_value>
                <xsl:value-of select="@defaultval"/>
            </score_value>
            <score_max>
                <xsl:value-of select="@maxvalue"/>
            </score_max>
        </score>
    </xsl:template>
    <xsl:template match="varequal">
        <correct_response>
            <xsl:value-of select="."/>
        </correct_response>
    </xsl:template>
    <xsl:template match="correct_response">
        <xsl:copy-of select="."/>
    </xsl:template>
    <xsl:template match="qtimetadatafield">
        <asi_metadatafield>
            <field_label>
                <xsl:value-of select="fieldlabel"/>
            </field_label>
            <field_value>
                <xsl:value-of select="fieldentry"/>
            </field_value>
        </asi_metadatafield>
    </xsl:template>
</xsl:stylesheet>
