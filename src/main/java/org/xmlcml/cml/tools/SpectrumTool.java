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
		SpectrumTool spectrumTool = (SpectrumTool) spectrum.getTool();
		if (spectrumTool == null) {
			spectrumTool = new SpectrumTool(spectrum);
			spectrum.setTool(spectrumTool);
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


};