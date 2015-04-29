/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * 
 */
package org.xmlcml.cml.tools;

import org.junit.Test;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.testutils.CMLXOMTestUtils;

/**
 * @author pm286
 *
 */

public class FragmentConverterTest {

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentConverter#convertToFragment()}.
	 */
	@Test
	public void testConvertToFragment() {
		String moleculeS = ""+
		"<molecule "+CMLConstants.CML_XMLNS+" id='m1'>" +
		"  <atomArray>" +
		"    <atom id='a1'/>" +
		"    <atom id='a2'/>" +
		"  </atomArray>" +
		"  <bondArray>" +
		"    <bond atomRefs2='a1 a2'/>" +
		"  </bondArray>" +
		"</molecule>" +
		"";
		CMLMolecule molecule = (CMLMolecule)CMLXOMTestUtils.parseValidString(moleculeS);
		FragmentConverter fragmentConverter = new FragmentConverter(molecule);
		CMLFragment fragment = fragmentConverter.convertToFragment();

		String fragmentS = 
		"<fragment id='m1' xmlns='http://www.xml-cml.org/schema'>"+
		  "<molecule role='fragment'>"+
		    "<atomArray>"+
		      "<atom id='a1'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a1</arg>"+
		      "</atom>"+
		      "<atom id='a2'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a2</arg>"+
		      "</atom>"+
		    "</atomArray>"+
		    "<bondArray>"+
		      "<bond atomRefs2='a1 a2'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a1_m1_{$idx}_a2</arg>"+
		        "<arg parentAttribute='atomRefs2'>m1_{$idx}_a1 m1_{$idx}_a2</arg>"+
		      "</bond>"+
		    "</bondArray>"+
		    "<arg parameterName='idx'/>"+
		    "<arg parentAttribute='id'>m1_{$idx}</arg>"+
		  "</molecule>"+
		"</fragment>";
		CMLFragment fragmentE = (CMLFragment)CMLXOMTestUtils.parseValidString(fragmentS);
		CMLXOMTestUtils.assertEqualsCanonically("fragment", fragmentE, fragment, true);
		
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.FragmentConverter#convertToFragment()}.
	 */
	@Test
	public void testConvertToFragment1() {
		String moleculeS = ""+
		"<molecule "+CMLConstants.CML_XMLNS+" id='m1'>" +
		"  <atomArray>" +
		"    <atom id='a1'>" +
		"    </atom>" +
		"    <atom id='a2'>" +
		"    </atom>" +
		"    <atom id='a3'>" +
		"    </atom>" +
		"    <atom id='a4'>" +
		"    </atom>" +
		"    <atom id='a5'>" +
		"    </atom>" +
		"  </atomArray>" +
		"  <bondArray>" +
		"    <bond atomRefs2='a1 a2'>" +
		"    </bond>" +
		"    <bond atomRefs2='a2 a3'>" +
		"    </bond>" +
		"    <bond atomRefs2='a3 a4'>" +
		"    </bond>" +
		"    <bond atomRefs2='a4 a5'>" +
		"    </bond>" +
		"  </bondArray>" +
		"  <length id='l1' atomRefs2='a1 a2'>" +
		"  </length>" +
		"  <length id='len2' atomRefs2='a2 a3'>1.23</length>" +
		"  <angle id='a1' atomRefs3='a1 a2 a3'>" +
		"  </angle>" +
		"  <angle id='ang2' atomRefs3='a2 a3 a4'>102</angle>" +
		"  <torsion id='t1' atomRefs4='a1 a2 a3 a4'>" +
		"  </torsion>" +
		"  <torsion id='tor2' atomRefs4='a2 a3 a4 a5'>-34</torsion>" +
		"</molecule>" +
		"";
		CMLMolecule molecule = (CMLMolecule)CMLXOMTestUtils.parseValidString(moleculeS);
		FragmentConverter fragmentConverter = new FragmentConverter(molecule);
		CMLFragment fragment = fragmentConverter.convertToFragment();
		String fragmentS = 
		"<fragment id='m1' xmlns='http://www.xml-cml.org/schema'>"+
		  "<molecule role='fragment'>"+
		    "<atomArray>"+
		      "<atom id='a1'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a1</arg>"+
		      "</atom>"+
		      "<atom id='a2'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a2</arg>"+
		      "</atom>"+
		      "<atom id='a3'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a3</arg>"+
		      "</atom>"+
		      "<atom id='a4'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a4</arg>"+
		      "</atom>"+
		      "<atom id='a5'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a5</arg>"+
		      "</atom>"+
		    "</atomArray>"+
		    "<bondArray>"+
		      "<bond atomRefs2='a1 a2'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a1_m1_{$idx}_a2</arg>"+
		        "<arg parentAttribute='atomRefs2'>m1_{$idx}_a1 m1_{$idx}_a2</arg>"+
		      "</bond>"+
		      "<bond atomRefs2='a2 a3'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a2_m1_{$idx}_a3</arg>"+
		        "<arg parentAttribute='atomRefs2'>m1_{$idx}_a2 m1_{$idx}_a3</arg>"+
		      "</bond>"+
		      "<bond atomRefs2='a3 a4'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a3_m1_{$idx}_a4</arg>"+
		        "<arg parentAttribute='atomRefs2'>m1_{$idx}_a3 m1_{$idx}_a4</arg>"+
		      "</bond>"+
		      "<bond atomRefs2='a4 a5'>"+
		        "<arg parentAttribute='id'>m1_{$idx}_a4_m1_{$idx}_a5</arg>"+
		        "<arg parentAttribute='atomRefs2'>m1_{$idx}_a4 m1_{$idx}_a5</arg>"+
		      "</bond>"+
		    "</bondArray>"+
		    "<length id='l1' atomRefs2='a1 a2'>"+
		      "<arg parentAttribute='id'>m1_{$idx}_l1</arg>"+
		      "<arg parentAttribute='atomRefs2'>m1_{$idx}_a1 m1_{$idx}_a2</arg>"+
		      "<arg substitute='.'>{$l1}</arg>"+
		    "</length>"+
		    "<length id='len2' atomRefs2='a2 a3'>1.23"+
		      "<arg parentAttribute='id'>m1_{$idx}_len2</arg>"+
		      "<arg parentAttribute='atomRefs2'>m1_{$idx}_a2 m1_{$idx}_a3</arg>"+
		      "<arg substitute='.'>{$len2}</arg>"+
		    "</length>"+
		    "<angle id='a1' atomRefs3='a1 a2 a3'>"+
		      "<arg parentAttribute='id'>m1_{$idx}_a1</arg>"+
		      "<arg parentAttribute='atomRefs3'>m1_{$idx}_a1 m1_{$idx}_a2 m1_{$idx}_a3</arg>"+
		      "<arg substitute='.'>{$a1}</arg>"+
		    "</angle>"+
		    "<angle id='ang2' atomRefs3='a2 a3 a4'>102"+
		      "<arg parentAttribute='id'>m1_{$idx}_ang2</arg>"+
		      "<arg parentAttribute='atomRefs3'>m1_{$idx}_a2 m1_{$idx}_a3 m1_{$idx}_a4</arg>"+
		      "<arg substitute='.'>{$ang2}</arg>"+
		    "</angle>"+
		    "<torsion id='t1' atomRefs4='a1 a2 a3 a4'>"+
		      "<arg parentAttribute='id'>m1_{$idx}_t1</arg>"+
		      "<arg parentAttribute='atomRefs4'>m1_{$idx}_a1 m1_{$idx}_a2 m1_{$idx}_a3 m1_{$idx}_a4</arg>"+
		      "<arg substitute='.'>{$t1}</arg>"+
		    "</torsion>"+
		    "<torsion id='tor2' atomRefs4='a2 a3 a4 a5'>-34"+
		      "<arg parentAttribute='id'>m1_{$idx}_tor2</arg>"+
		      "<arg parentAttribute='atomRefs4'>m1_{$idx}_a2 m1_{$idx}_a3 m1_{$idx}_a4 m1_{$idx}_a5</arg>"+
		      "<arg substitute='.'>{$tor2}</arg>"+
		    "</torsion>"+
		    "<arg parameterName='idx'/>"+
		    "<arg parentAttribute='id'>m1_{$idx}</arg>"+
		    "<arg parameterName='l1'/>"+
		    "<arg parameterName='len2'/>"+
		    "<arg parameterName='a1'/>"+
		    "<arg parameterName='ang2'/>"+
		    "<arg parameterName='t1'/>"+
		    "<arg parameterName='tor2'/>"+
		  "</molecule>"+
		"</fragment>";
		CMLFragment fragmentE = (CMLFragment)CMLXOMTestUtils.parseValidString(fragmentS);
		CMLXOMTestUtils.assertEqualsCanonically("fragment", fragmentE, fragment, true);
	}
	
}
