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

package org.xmlcml.cml.tools;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real2;

public class MoleculeLayoutTest {
	
    @Test
    public void adjustValenceAnglesOnAcyclicAtom2() {
    	CMLMolecule molecule = SMILESTool.createMolecule("FOBr");
    	Assert.assertEquals("F", molecule.getAtom(0).getElementType());
    	Assert.assertEquals("O", molecule.getAtom(1).getElementType());
    	Assert.assertEquals("Br", molecule.getAtom(2).getElementType());
    }
    
    @Test
    public void adjustValenceAnglesOnAcyclicAtom2a() {
    	CMLMolecule molecule = SMILESTool.createMolecule("FOBr");
    	CMLAtom centralAtom = molecule.getAtom(1);
    	molecule.getAtom(0).setXY2(new Real2(1.0, 0.0));
    	centralAtom.setXY2(new Real2(0.0, 0.0));
    	molecule.getAtom(2).setXY2(new Real2(0.0, 1.0));
    	assertAngle(molecule, 0,1,2, Math.PI/2);
    }
    
    @Test
    public void adjustValenceAnglesOnAcyclicAtom2a1() {
    	CMLMolecule molecule = SMILESTool.createMolecule("FOBr");
    	CMLAtom centralAtom = molecule.getAtom(1);
    	molecule.getAtom(0).setXY2(new Real2(1.0, 0.0));
    	centralAtom.setXY2(new Real2(0.0, 0.0));
    	molecule.getAtom(2).setXY2(new Real2(0.0, 1.0));
    	
    }
    
    @Test
    public void adjustValenceAnglesOnAcyclicAtom2b() {
    	CMLMolecule molecule = SMILESTool.createMolecule("FOBr");
    	CMLAtom centralAtom = molecule.getAtom(1);
    	molecule.getAtom(0).setXY2(new Real2(1.0, 0.0));
    	centralAtom.setXY2(new Real2(0.0, 0.0));
    	molecule.getAtom(2).setXY2(new Real2(0.0, -1.0));
    	assertAngle(molecule, 0,1,2, -Math.PI/2);
    	MoleculeLayout.adjustValenceAnglesOnAcyclicAtom2(centralAtom);
    	assertAngle(molecule, 0,1,2, Math.PI);
    }
    
	@Test
    public void adjustValenceAnglesOnAcyclicAtom3() {
    	CMLMolecule molecule = SMILESTool.createMolecule("FN(Cl)Br");
    	Assert.assertEquals("F", molecule.getAtom(0).getElementType());
    	Assert.assertEquals("N", molecule.getAtom(1).getElementType());
    	Assert.assertEquals("Cl", molecule.getAtom(2).getElementType());
    	Assert.assertEquals("Br", molecule.getAtom(3).getElementType());
    }
    
    @Test
    public void adjustValenceAnglesOnAcyclicAtom3a() {
    	CMLMolecule molecule = SMILESTool.createMolecule("FN(Cl)Br");
//    	MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    	CMLAtom centralAtom = molecule.getAtom(1);
    	molecule.getAtom(0).setXY2(new Real2(1.0, 0.0));
    	centralAtom.setXY2(new Real2(0.0, 0.0));
    	molecule.getAtom(2).setXY2(new Real2(0.0, 1.0));
    	molecule.getAtom(3).setXY2(new Real2(0.0, -1.0));
    	assertAngle(molecule, 0, 1, 2, Math.PI/2.);
    	assertAngle(molecule, 0, 1, 3, -Math.PI/2.);
    	MoleculeLayout.adjustValenceAnglesOnAcyclicAtom3(centralAtom);
    	assertAngle(molecule, 0, 1, 2, 2*Math.PI/3.);
    	assertAngle(molecule, 0, 1, 3, 4*Math.PI/3.);
    }
    
    @Test
    public void adjustValenceAnglesOnAcyclicAtom4() {
    	CMLMolecule molecule = SMILESTool.createMolecule("FC(C)(NC)OCCC");
    	Assert.assertEquals("F", molecule.getAtom(0).getElementType());
    	Assert.assertEquals("C", molecule.getAtom(1).getElementType());
    	Assert.assertEquals("C", molecule.getAtom(2).getElementType());
    	Assert.assertEquals("N", molecule.getAtom(3).getElementType());
    	Assert.assertEquals("O", molecule.getAtom(5).getElementType());
    }
    
    @Test
    public void adjustValenceAnglesOnAcyclicAtom4a() {
    	CMLMolecule molecule = SMILESTool.createMolecule("FC(Cl)(N(C)C)OCCC");
//    	molecule.debug();
    	CMLAtom centralAtom = molecule.getAtom(1);
    	molecule.getAtom(0).setXY2(new Real2(-1.0, 0.0));
    	centralAtom.setXY2(new Real2(0.0, 0.0));
    	molecule.getAtom(2).setXY2(new Real2(1.0, 1.0));
    	molecule.getAtom(3).setXY2(new Real2(0.0, 1.0));
    	molecule.getAtom(6).setXY2(new Real2(1.0, 0.0));
    	assertAngle(molecule, 0,1,2, -3*Math.PI/4);
    	assertAngle(molecule, 0,1,3, -Math.PI/2.);
    	assertAngle(molecule, 0,1,6, -Math.PI);
    	MoleculeLayout.adjustValenceAnglesOnAcyclicAtom4(centralAtom);
//    	molecule.debug();
    	assertAngle(molecule, 0,1,2, -Math.PI);
    	assertAngle(molecule, 0,1,3, Math.PI/2.);
    	assertAngle(molecule, 0,1,6, -Math.PI/2.);
    }
    
    
    private void assertAngle(CMLMolecule molecule, int i, int j, int k, double d) {
    	Angle angle = MoleculeTool.getCalculatedAngle2D(
    			molecule.getAtom(i),
    			molecule.getAtom(j),
    			molecule.getAtom(k)
    		);
    	Assert.assertEquals(d, angle.getRadian(), 0.00001);
	}

}
