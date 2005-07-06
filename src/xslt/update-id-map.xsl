<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output indent="yes"/>

  <xsl:variable name="idLookupDoc" select="document('id-map.xml')"/>
  <xsl:key name="localIdKey" match="id-entry" use="@local_id"/>

  <xsl:template match="/">
    <id-map>
      <xsl:apply-templates/>
      <xsl:apply-templates select="$idLookupDoc" mode="append"/>
    </id-map>
  </xsl:template>

  <xsl:template match="id-entry" mode="append">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="append"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="id-entry/@*" mode="append">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="*[@local_id and @id]">
    <xsl:variable name="localId" select="@local_id"/>
    <xsl:variable name="id" select="@id"/>
    <xsl:for-each select="$idLookupDoc">
      <xsl:variable name="globalId" select="key('localIdKey',$localId)/@id"/>
      <xsl:if test="not($globalId)">
       <id-entry local_id="{$localId}" id="{$id}"/>
      </xsl:if>
    </xsl:for-each>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="@*|comment()|processing-instruction()|text()"/>

</xsl:stylesheet>
