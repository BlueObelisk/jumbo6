<!--

       Copyright 2011 Peter Murray-Rust et. al.

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

-->

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
