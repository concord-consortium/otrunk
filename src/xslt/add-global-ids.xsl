<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
  xmlns:java="http://xml.apache.org/xalan/java">

  <xsl:variable name="idLookupDoc" select="document('id-map.xml')"/>
  <xsl:key name="localIdKey" match="id-entry" use="@local_id"/>

  <xsl:template match="otrunk">
    <xsl:copy>
      <xsl:apply-templates select="$idLookupDoc"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="id-map"/>
  
  <xsl:template match="*[@local_id]">
    <xsl:copy>
      <xsl:variable name="localId" select="@local_id"/>
      <xsl:attribute name="id">
        <xsl:for-each select="$idLookupDoc">
          <xsl:variable name="globalId" select="key('localIdKey',$localId)/@id"/>
          <xsl:choose>
            <xsl:when test="$globalId">
              <xsl:value-of select="$globalId"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="java:org.concord.otrunk.datamodel.OTUUID.createOTUUID()"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:attribute>
      <xsl:apply-templates select="*|@*|comment()|text()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="*|@*|comment()|processing-instruction()|text()">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|comment()|processing-instruction()|text()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
