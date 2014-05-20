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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLSpectrum;

/**
 * tool for managing spectrum
 *
 * @author pmr
 *
 */
public class SpectrumTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(SpectrumTool.class.getName());

	CMLSpectrum spectrum = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	@Deprecated
	public SpectrumTool(CMLSpectrum spectrum) throws RuntimeException {
		init();
		this.spectrum = spectrum;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLSpectrum getSpectrum() {
		return this.spectrum;
	}

    
	/** gets SpectrumTool associated with spectrum.
	 * if null creates one and sets it in spectrum
	 * @param spectrum
	 * @return tool
	 */
	public static SpectrumTool getOrCreateTool(CMLSpectrum spectrum) {
		SpectrumTool spectrumTool = null;
		if (spectrum != null) {
			spectrumTool = (SpectrumTool) spectrum.getTool();
			if (spectrumTool == null) {
				spectrumTool = new SpectrumTool(spectrum);
				spectrum.setTool(spectrumTool);
			}
		}
		return spectrumTool;
	}

    /**
     * gets the atoms referenced by atomRefs. conveience method to get spectrum
     * and its sibling molecule if these cannot be found returns zero length
     * list
     *
     * @return atoms referred to in atomRefs or sero length list if not found
     */
    public List<CMLAtom> getDerefencedAtoms(CMLPeak peak) {
        List<CMLAtom> atomList = new ArrayList<CMLAtom>();
        String[] atomRefs = peak.getAtomRefs();
        if (atomRefs != null) {
            CMLMolecule molecule = spectrum.getSiblingMolecule();
            if (molecule != null) {
                atomList = molecule.getAtomListByIds(atomRefs);
            }
        }
        return atomList;
    }


//    /**
//     * gets atoms referenced as targets of coupling. makes list of non-zero
//     * lists returned by child peakStructure.getCouplingsFrom() uses sibling
//     * molecule of spectrum
//     *
//     * @return list of atomLists (zero length if none found)
//     */
//    public List<List<CMLAtom>> getCouplingsFrom(CMLPeak peak) {
//        return getCouplingsFrom(spectrum.getAuntMolecule(peak));
//    }

	/**
	 * checks to see if a given proton NMR can correspond to a given molecule
	 * by comparing spectrum integral to molecule hydrogen count 
	 * @param spectrum
	 * @param mol
	 *  
	 * @throws IllegalArgumentException for null spectrum/mol or mol with no atoms
	 */
	public boolean checkIntegralAgainstProtonCount(CMLMolecule mol) {
		return checkIntegralAgainstProtonCount(mol, 0.01d);
	}

	/**
	 * checks to see if a given proton NMR can correspond to a given molecule
	 * by comparing spectrum integral to molecule hydrogen count to within a given
	 * epsilon
	 * @param spectrum
	 * @param mol
	 * @param epsilon
	 * 
	 * @throws IllegalArgumentException for null spectrum/mol or mol with no atoms
	 */
	public boolean checkIntegralAgainstProtonCount(CMLMolecule mol, double epsilon) {
		if (mol == null) {
			throw new IllegalArgumentException("mol must not be null");
		}
		if (mol.getAtomCount() == 0) {
			throw new IllegalArgumentException("mol must have atoms");
		}
		double integralSum = calculateIntegralSum();
		double hydrogenCount = mol.calculateHydrogenCount();
		return integralSum == hydrogenCount ? true : Math.abs(integralSum - hydrogenCount) < epsilon;
	}


	/**
	 * calculates the sum of the integrals of the spectrum peaks. No checking that
	 * integrals are present is performed. 
	 */
	public double calculateIntegralSum() {
		double sum = 0;
		for (CMLPeak peak : CMLSpectrum.getDescendantPeaks(spectrum)) {
			if (peak.getIntegral() != null) {
				sum += Double.parseDouble(peak.getIntegral());
			}
		}
		return sum;
	}

}