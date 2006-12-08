package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElements;

/**
 * The structure of a peak.
 * 
 * 
 * \n A peakStructure can describe:\n \nfine structure such as coupling or
 * splitting constants\n components such as shoulders or inflexions\n \na peak
 * can have more than one peakStructure which can be used to\n describe more
 * than one coupling. A doublet of doublets might have two \npeakStructures with
 * the larger one first, but there are no controlled \nsemantics.\n
 * 
 * user-modifiable class autogenerated from schema if no class exists use as a
 * shell which can be edited the autogeneration software will not overwrite an
 * existing class file
 * 
 */
public class CMLPeakStructure extends AbstractPeakStructure {

    /**
     * must give simple documentation.
     * 
     */
    public CMLPeakStructure() {
    }

    /**
     * copy constructor.
     * 
     * @param old
     */
    public CMLPeakStructure(CMLPeakStructure old) {
        super((AbstractPeakStructure) old);
    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLPeakStructure(this);
    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLPeakStructure
     */
    public static CMLPeakStructure makeElementInContext(Element parent) {
        return new CMLPeakStructure();
    }

    /**
     * gets atoms referenced as targets of coupling. uses default aunt molecule
     * requires type="coupling"
     * 
     * @return list of atoms (zero length if none found)
     */
    public List<CMLAtom> getCouplingsFrom() {
        return getCouplingsFrom(CMLSpectrum.getAuntMolecule(this));
    }

    /**
     * gets atoms referenced as targets of coupling. requires type="coupling"
     * 
     * @param molecule
     *            owning atoms; if null returns zero length List
     * @return list of atoms (zero length if none found)
     */
    public List<CMLAtom> getCouplingsFrom(CMLMolecule molecule) {
        List<CMLAtom> atoms = new ArrayList<CMLAtom>();
        if ("coupling".equals(this.getType()) && molecule != null) {
            atoms = molecule.getAtomListByIds(this.getAtomRefs());
        }
        return atoms;
    }

    /**
     * validates nested peakStructure. must have a parent peak with an atomRefs
     * attribute of the same size as the number of children of this.
     * 
     * @return true if has children of same size as parent atomRefs
     */
    public boolean hasValidNestedPeakStructure() {
        boolean has = false;
        CMLElements<CMLPeakStructure> ps = this.getPeakStructureElements();
        if (ps.size() > 0) {
            if (this.getPeakStructureElements().size() > 0) {
                Node parent = this.getParent();
                if (parent != null && parent instanceof CMLPeak) {
                    String[] atomRefs = ((CMLPeak) parent).getAtomRefs();
                    if (atomRefs != null) {
                        has = ps.size() == atomRefs.length;
                    }
                }
            }
        }
        return has;
    }
}
