<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
  <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <!-- ============================== -->
  <!-- root element: questtestinterop -->
  <!-- ============================== -->
  <xsl:template match="questestinterop">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="2cm" margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <xsl:apply-templates select="assessment" />
    </fo:root>
  </xsl:template>
  <!-- ============================= -->
  <!-- child element: assessment     -->
  <!-- ============================= -->
  <xsl:template match="assessment">
      <fo:page-sequence master-reference="simpleA4">
        <fo:flow flow-name="xsl-region-body">
          <fo:block font-size="10pt">
            <fo:table table-layout="fixed">
              <fo:table-column column-width="16cm"/>
              <fo:table-body>
                <fo:table-row keep-with-next="5" keep-with-previous="5">
                  <fo:table-cell>
                    <fo:block>
                      <xsl:value-of select="@title"/>
                    </fo:block>
 
                    <fo:block font-size="10pt">
                     <fo:table table-layout="fixed">
                      <fo:table-column column-width="15cm"/>
                       <fo:table-body>
                         <xsl:apply-templates select="section"/>
                       </fo:table-body>
                      </fo:table>
                    </fo:block>

                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
  </xsl:template>

  <!-- ========================= -->
  <!-- child element: section    -->
  <!-- ========================= -->
  <xsl:template match="section">
    <fo:table-row>
      <fo:table-cell>
        <fo:block font-size="14pt" font-weight="bold" space-after="4mm">
          Section : <xsl:value-of select="@title" />
        </fo:block>
      </fo:table-cell>
    </fo:table-row>

    <fo:table-row>
      <fo:table-cell>
        <fo:block space-after="2mm">
          <xsl:apply-templates select="presentation_material/flow_mat" />
        </fo:block>
      </fo:table-cell>
    </fo:table-row>

    <fo:table-row>
      <fo:table-cell>
        <fo:block>
            <fo:table table-layout="fixed">
              <fo:table-column column-width="14cm"/>
              <fo:table-body>
                <xsl:apply-templates select="item | section" />
              </fo:table-body>
            </fo:table>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>

  <!-- ========================= -->
  <!-- child element: item       -->
  <!-- ========================= -->
  <xsl:template match="item">
    <fo:table-row keep-together.within-page="always">
      <fo:table-cell>
        <fo:block> 
            <fo:table table-layout="fixed">
              <fo:table-column column-width="1cm"/>
              <fo:table-column column-width="12cm" />
              <fo:table-body>
               <fo:table-row  keep-together.within-page="always">
                <fo:table-cell>
                  <fo:block space-after="3mm"> 
                <xsl:value-of select="position()" />. 
                 </fo:block>
                 </fo:table-cell>
               <xsl:apply-templates select="presentation/flow | presentation" />
               </fo:table-row>
              </fo:table-body>
            </fo:table>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>

  <!-- ============================== -->
  <!-- child element: presentation    -->
  <!-- ============================== -->
<xsl:template match="presentation">
      <fo:table-cell>
        <fo:block space-after="3mm"> 
            <fo:table table-layout="fixed">
              <fo:table-column column-width="12cm"/>
              <fo:table-body>
                <xsl:apply-templates select="response_lid | material" />
              </fo:table-body>
            </fo:table>
        </fo:block>
      </fo:table-cell>
</xsl:template>

  <!-- ============================== -->
  <!-- child element: flow            -->
  <!-- ============================== -->
<xsl:template match="flow">
      <fo:table-cell>
        <fo:block space-after="3mm"> 
             <fo:table table-layout="fixed">
              <fo:table-column column-width="12cm"/>
              <fo:table-body>       
  <xsl:apply-templates select="response_lid | material | flow" />
              </fo:table-body>
            </fo:table>
        </fo:block>
      </fo:table-cell>
</xsl:template>

  <!-- ============================== -->
  <!-- child element: flow_mat        -->
  <!-- ============================== -->
<xsl:template match="flow_mat">
             <fo:table table-layout="fixed">
              <fo:table-column column-width="12cm"/>
              <fo:table-body>       
  <xsl:apply-templates select="material | flow_mat" />
              </fo:table-body>
            </fo:table>
</xsl:template>

  <!-- ============================== -->
  <!-- child element: material        -->
  <!-- ============================== -->
<xsl:template match="material">
     <xsl:apply-templates select="mattext" />
</xsl:template>

<xsl:template match="mattext">
    <fo:table-row keep-together.within-page="5" keep-with-next="5" keep-with-previous="5">
      <fo:table-cell>
        <fo:block>
  <xsl:value-of select="self::*" />
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
</xsl:template>

  <!-- ============================== -->
  <!-- child element: response_lid    -->
  <!-- ============================== -->
<xsl:template match="response_lid">
<fo:table-row keep-together.within-page="always">
      <fo:table-cell>
        <fo:block>
             <fo:table table-layout="fixed">
             <fo:table-column column-width="8mm"/>
              <fo:table-column column-width="11cm"/>
              <fo:table-body> 
  <xsl:apply-templates select="render_choice" />
              </fo:table-body>
            </fo:table>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
</xsl:template>

  <!-- ============================== -->
  <!-- child element: render_choice   -->
  <!-- ============================== -->
<xsl:template match="render_choice">
   <xsl:for-each select="response_label">
    <fo:table-row keep-together.within-page="always">
      <fo:table-cell>
        <fo:block>
   <xsl:number level="single" count="response_label" format="A" />. 
        </fo:block>
      </fo:table-cell>
      <fo:table-cell>
      <fo:block>
      <xsl:value-of select="flow_mat/material/mattext | material/mattext" />
      </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:for-each>
</xsl:template>

  <!-- ============================== -->
  <!-- child element: response_label  -->
  <!-- ============================== -->
<xsl:template match="response_label">
    <fo:table-row keep-together.within-page="always">
      <fo:table-cell>
        <fo:block>
   <xsl:value-of select="position()" />. <xsl:value-of select="flow_mat/material/mattext | material/mattext" />
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
</xsl:template>
</xsl:stylesheet>




