<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!--  numer of repeat units -->
  <xsl:param name="count">10</xsl:param>
<!-- id of repeat unit -->
  <xsl:param name="molecule">g:eo</xsl:param>
  <xsl:template match="/">
	<fragment convention='cml:PML-basic' 
	   xmlns='http://www.xml-cml.org/schema' 
	   xmlns:g='http://www.xml-cml.org/mols/geom1'>
	  <fragment countExpression='*({$count})'>
	    <join atomRefs2='r2 r1' moleculeRefs2='PREVIOUS NEXT'/>
	    <fragment> 
	      <molecule ref='{$molecule}'/> 
	    </fragment>
	  </fragment>
	</fragment>
  </xsl:template>
</xsl:stylesheet>
