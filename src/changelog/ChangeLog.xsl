<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"
    version="4.01"
    encoding="ascii"
    indent="yes"/>

  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
    <html>
      <head>
        <style type="text/css">
	body {	background-color: white; font-size: 10pt; font-family: 'Bitstream Vera Sans',Verdana,sans-serif; }
	hr.release { margin: 4px 0px 0px 0px; }
	h4.releasedesc { margin: 4px 0px 4px 0px; }
        </style>
      </head>
      <body>
        <xsl:apply-templates select="product"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="product">
    <xsl:for-each select="release/releaseinfo">
      <xsl:if test="position()!=1">
        <hr class="release"></hr>
      </xsl:if>
      <h4 class="releasedesc"><xsl:value-of select="releasedesc"/></h4>
      <span class="releasedate">Released at <xsl:value-of select="releasedate"/>.</span>
      <div class="changelog">
        <xsl:apply-templates select="changelog"/>
      </div>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="itemize">
    <ul class="itemize">
      <xsl:apply-templates/>
    </ul>
  </xsl:template>

  <xsl:template match="item">
    <li class="item">
      <xsl:apply-templates/>
    </li>
  </xsl:template>
 
</xsl:stylesheet>
