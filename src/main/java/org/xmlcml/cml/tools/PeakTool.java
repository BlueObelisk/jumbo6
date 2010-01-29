package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLPeakStructure;
import org.xmlcml.cml.element.CMLSpectrum;

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
		PeakTool peakTool = null;
		if (peak != null) {
			peakTool = (PeakTool) peak.getTool();
			if (peakTool == null) {
				peakTool = new PeakTool(peak);
				peak.setTool(peakTool);
			}
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

    private static Map<String, String> shapeMap;
    static {
    	shapeMap = new HashMap<String, String>();
    	shapeMap.put("br", "cmlx:broad");
    	shapeMap.put("bs", "cmlx:broad");
    	shapeMap.put("bd", "cmlx:broad");
    	shapeMap.put("broad", "cmlx:broad");
    	shapeMap.put("sharp", "cmlx:sharp");
    };

	public static String guessShape(String peakType) {
		String shape = null;
		if (peakType != null) {
			shape = shapeMap.get(peakType);
		}
		return shape;
	}
	
    private static Map<String, String> multiplicityMap;
    static {
    	multiplicityMap = new HashMap<String, String>();
    	multiplicityMap.put("s", "cmlx:singlet");
    	multiplicityMap.put("bs", "cmlx:singlet");
    	multiplicityMap.put("d", "cmlx:doublet");
    	multiplicityMap.put("bd", "cmlx:doublet");
    	multiplicityMap.put("t", "cmlx:triplet");
    	multiplicityMap.put("q", "cmlx:quartet");
    	multiplicityMap.put("quintet", "cmlx:quintet");
    	multiplicityMap.put("m", "cmlx:multiplet");
    	multiplicityMap.put("dd", "cmlx:doubletdoublet");
    	multiplicityMap.put("dt", "cmlx:doublettriplet");
    	multiplicityMap.put("td", "cmlx:tripletdoublet");
    };

	public static String guessMultiplicity(String peakType) {
		String multiplicity = null;
		if (peakType != null) {
			multiplicity = multiplicityMap.get(peakType);
		}
		return multiplicity;
	}
	
};