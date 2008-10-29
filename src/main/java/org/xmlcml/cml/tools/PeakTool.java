package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.lite.CMLAtom;
import org.xmlcml.cml.element.lite.CMLMolecule;
import org.xmlcml.cml.element.lite.CMLPeak;
import org.xmlcml.cml.element.lite.CMLPeakStructure;
import org.xmlcml.cml.element.main.CMLSpectrum;

/**
 * tool for managing peakList
 *
 * @author pmr
 *
 */
public class PeakTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(PeakTool.class.getName());

	CMLPeak peak = null;

	/** constructor.
	 */
	public PeakTool(CMLPeak peak) throws RuntimeException {
		init();
		this.peak = peak;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLPeak getPeak() {
		return this.peak;
	}

    
	/** gets PeakTool associated with peak.
	 * if null creates one and sets it in peak
	 * @param peak
	 * @return tool
	 */
	public static PeakTool getOrCreateTool(CMLPeak peak) {
		PeakTool peakTool = (PeakTool) peak.getTool();
		if (peakTool == null) {
			peakTool = new PeakTool(peak);
			peak.setTool(peakTool);
		}
		return peakTool;
	}

    /**
     * gets atoms referenced as targets of coupling. uses default aunt molecule
     * requires type="coupling"
     *
     * @return list of atoms (zero length if none found)
     */
    public List<List<CMLAtom>> getCouplingsFrom(CMLPeakStructure peakStructure) {
        return getCouplingsFrom(CMLSpectrum.getAuntMolecule(peakStructure));
    }

    /**
     * gets atoms referenced as targets of coupling. makes list of non-zero
     * lists returned by child peakStructure.getCouplingsFrom()
     *
     * @param molecule
     *            owning atoms; if null returns zero length List
     * @return list of atomLists (zero length if none found)
     */
    public List<List<CMLAtom>> getCouplingsFrom(CMLMolecule molecule) {
        List<List<CMLAtom>> atomListList = new ArrayList<List<CMLAtom>>();
        for (CMLPeakStructure ps : peak.getPeakStructureElements()) {
            List<CMLAtom> atomList = ps.getCouplingsFrom(molecule);
            if (atomList.size() > 0) {
                atomListList.add(atomList);
            }
        }
        return atomListList;
    }
};