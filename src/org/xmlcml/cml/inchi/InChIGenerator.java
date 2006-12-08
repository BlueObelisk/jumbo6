package org.xmlcml.cml.inchi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jniinchi.INCHI_BOND_TYPE;
import net.sf.jniinchi.INCHI_RADICAL;
import net.sf.jniinchi.INCHI_RET;
import net.sf.jniinchi.JniInchiAtom;
import net.sf.jniinchi.JniInchiBond;
import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiInput;
import net.sf.jniinchi.JniInchiOutput;
import net.sf.jniinchi.JniInchiWrapper;
import nu.xom.Text;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * <p>This class generates the IUPAC International Chemical Identifier (InChI) for
 * a CMLMolecule. It places calls to a JNI wrapper for the InChI C++ library.
 * 
 * <p>If the molecule has 3D coordinates for all of its atoms then they will be
 * used, otherwise 2D coordinates will be used if available.
 * 
 * <p>Bond stereochemistry and atom parities are not currently
 * processed. If 3D coordinates are available then the bond stereochemistry and
 * atom parities would be ignored by InChI anyway.
 * 
 * <h3>Example usage</h3>
 * 
 * <code>// Generate factory - throws CMLException if native code does not load</code><br>
 * <code>InChIGeneratorFactory factory = new InChIGeneratorFactory();</code><br>
 * <code>// Get InChIGenerator</code><br>
 * <code>InChIGenerator gen = factory.getInChIGenerator(molecule);</code><br>
 * <code></code><br>
 * <code>INCHI_RET ret = gen.getReturnStatus();</code><br>
 * <code>if (ret == INCHI_RET.WARNING) {</code><br>
 * <code>  // InChI generated, but with warning message</code><br>
 * <code>  System.out.println("InChI warning: " + gen.getMessage());</code><br>
 * <code>} else if (ret != INCHI_RET.OKAY) {</code><br>
 * <code>  // InChI generation failed</code><br>
 * <code>  throw new CMLException("InChI failed: " + ret.toString()</code><br>
 * <code>    + " [" + gen.getMessage() + "]");</code><br>
 * <code>}</code><br>
 * <code></code><br>
 * <code>String inchi = gen.getInchi();</code><br>
 * <code>String auxinfo = gen.getAuxInfo();</code><br>
 * 
 * <p><tt><b>
 * TODO: bond stereochemistry<br>
 * TODO: atom parities.
 * </b></tt>
 * 
 * @author Sam Adams
 */
public class InChIGenerator {
	
	protected JniInchiInput input;
	
	protected JniInchiOutput output;
	
    
	/**
	 * Convention to use when constructing CMLIdentifier to hold InChI.
	 */
    protected static final String CML_INCHI_CONVENTION = "iupac:inchi";
    
    /**
     * Molecule instance refers to.
     */
    protected CMLMolecule molecule;
    
    /**
     * <p>Constructor. Generates InChI from CMLMolecule.
     * 
     * <p>Reads atoms, bonds etc from molecule and converts to format InChI library
     * requires, then calls the library.
     * 
     * @param molecule      Molecule to generate InChI for.
     * @throws CMLException
     */
    protected InChIGenerator(CMLMolecule molecule) throws CMLException {
    	try {
    		input = new JniInchiInput("");
            generateInchiFromCMLMolecule(molecule);
        } catch (JniInchiException jie) {
            throw new CMLException("InChI generation failed: " + jie.getMessage());
        }
    }
    
    
    /**
     * <p>Constructor. Generates InChI from CMLMolecule.
     * 
     * <p>Reads atoms, bonds etc from molecule and converts to format InChI library
     * requires, then calls the library.
     * 
     * @param molecule  Molecule to generate InChI for.
     * @param options   Space delimited string of options to pass to InChI library.
     * 					Each option may optionally be preceded by a command line
     * 					switch (/ or -).
     * @throws CMLException
     */
    protected InChIGenerator(CMLMolecule molecule, String options) throws CMLException {
    	try {
    		input = new JniInchiInput(options);
            generateInchiFromCMLMolecule(molecule);
        } catch (JniInchiException jie) {
            throw new CMLException("InChI generation failed: " + jie.getMessage());
        }
    }
    
    
    /**
     * <p>Constructor. Generates InChI from CMLMolecule.
     * 
     * <p>Reads atoms, bonds etc from molecule and converts to format InChI library
     * requires, then calls the library.
     * 
     * @param molecule      Molecule to generate InChI for.
     * @param options       List of INCHI_OPTION.
     * @throws CMLException
     */
    protected InChIGenerator(CMLMolecule molecule, List options) throws CMLException {
    	try {
    		input = new JniInchiInput(options);
            generateInchiFromCMLMolecule(molecule);
        } catch (JniInchiException jie) {
            throw new CMLException("InChI generation failed: " + jie.getMessage());
        }
    }
    
    
    /**
     * <p>Reads atoms, bonds etc from molecule and converts to format InChI library
     * requires, then makes call to library, generating InChI.
     * 
     * <p>Used by constructors.
     * 
     * @param molecule
     * @throws CMLException
     */
    protected void generateInchiFromCMLMolecule(CMLMolecule molecule) throws CMLException {
    	
        this.molecule = molecule;
        
        List<CMLAtom> atoms = molecule.getAtoms();
        List<CMLBond> bonds = molecule.getBonds();
        
        // Create map of atom neighbours - required to calculate implicit
        // hydrogen counts
        Map<CMLAtom, List<CMLAtom>> atomNeighbours = new HashMap<CMLAtom, List<CMLAtom>>();
        for (int i = 0; i < atoms.size(); i ++) {
            atomNeighbours.put(atoms.get(i), new ArrayList<CMLAtom>(4));
        }
        for (int i = 0; i < bonds.size(); i ++) {
            CMLBond bond = (CMLBond) bonds.get(i);
            CMLAtom at0 = bond.getAtom(0);
            CMLAtom at1 = bond.getAtom(1);
            atomNeighbours.get(at0).add(at1);
            atomNeighbours.get(at1).add(at0);
        }
        
        // Check for 3d coordinates
        boolean all3d = true;
        boolean all2d = true;
        for (int i = 0; i < atoms.size(); i ++) {
            CMLAtom atom = atoms.get(i);
            if (!atom.hasCoordinates(CMLElement.CoordinateType.CARTESIAN)) {
                all3d = false;
            }
            if (!atom.hasCoordinates(CMLElement.CoordinateType.TWOD)) {
                all2d = false;
            }
        }
        
        // Process atoms
        Map<CMLAtom, JniInchiAtom> atomMap = new HashMap<CMLAtom, JniInchiAtom>();
        for (int i = 0; i < atoms.size(); i ++) {
            CMLAtom atom = atoms.get(i);
            double x, y, z;
            if (all3d) {
                x = atom.getX3();
                y = atom.getY3();
                z = atom.getZ3();
            } else if (all2d) {
                x = atom.getX2();
                y = atom.getY2();
                z = 0;
            } else {
                x = 0;
                y = 0;
                z = 0;
            }
            String el = atom.getElementType();
            
            JniInchiAtom iatom = input.addAtom(new JniInchiAtom(x, y, z, el));
            atomMap.put(atom, iatom);
            
            int charge = atom.getFormalCharge(CMLElement.FormalChargeControl.DEFAULT);
            if (charge != 0) {
                iatom.setCharge(charge);
            }
            
            try {
                int spinMultiplicity = atom.getSpinMultiplicity();
                if (spinMultiplicity == 0) {
                    iatom.setRadical(INCHI_RADICAL.NONE);
                } else if (spinMultiplicity == 1) {
                    iatom.setRadical(INCHI_RADICAL.SINGLET);
                } else if (spinMultiplicity == 2) {
                    iatom.setRadical(INCHI_RADICAL.DOUBLET);
                } else if (spinMultiplicity == 3) {
                    iatom.setRadical(INCHI_RADICAL.TRIPLET);
                } else {
                    throw new CMLException("Failed to generate InChI: Unsupported spin multiplicity: " + spinMultiplicity);
                }
            } catch (CMLRuntimeException cre) {
                // Spin multiplicity not set 
            }
            
            try {
                int isotopeNumber = atom.getIsotopeNumber();
                iatom.setIsotopicMass(isotopeNumber);
            } catch (CMLRuntimeException cre) {
                // Isotope number not set 
            }
            
            // Calculate implicit hydrogens
            int hcount = -1;
            try {
                hcount = atom.getHydrogenCount();
            } catch (CMLRuntimeException cre) {
                // Hydrogen count not set 
            }
            
            if (hcount > -1) {
                List<CMLAtom> neighbours = atomNeighbours.get(atom);
                for (int j = 0; j < neighbours.size(); j ++) {
                	CMLAtom neigh = neighbours.get(j);
                	if (neigh.getElementType().equals("H")) {
                		hcount --;
                	}
                }
                
                if (hcount < 0) {
                	throw new CMLRuntimeException("Negative implicit hydrogen count: " + atom);
                }
                
                iatom.setImplicitH(hcount);
            }
        }
        
        // Process bonds
        for (int i = 0; i < bonds.size(); i ++) {
            CMLBond bond = (CMLBond) bonds.get(i);
            
            JniInchiAtom at0 = atomMap.get(bond.getAtom(0));
            JniInchiAtom at1 = atomMap.get(bond.getAtom(1));
            
            INCHI_BOND_TYPE order;
            String bo = bond.getOrder();
            
            if (CMLBond.SINGLE.equals(bo) || CMLBond.SINGLE_S.equals(bo)) {
                order = INCHI_BOND_TYPE.SINGLE;
            } else if (CMLBond.DOUBLE.equals(bo) || CMLBond.DOUBLE_D.equals(bo)) {
                order = INCHI_BOND_TYPE.DOUBLE;
            } else if (CMLBond.TRIPLE.equals(bo) || CMLBond.TRIPLE_T.equals(bo)) {
                order = INCHI_BOND_TYPE.TRIPLE;
            } else if (CMLBond.AROMATIC.equals(bo)) {
                order = INCHI_BOND_TYPE.ALTERN;
            } else {
                throw new CMLException("Failed to generate InChI: Unsupported bond order (" + bo + ")");
            }
            
            
            input.addBond(new JniInchiBond(at0, at1, order));
        }
        
        // TODO: Stereo chemistry
        try {
        	output = JniInchiWrapper.getInchi(input);
        } catch (JniInchiException jie) {
        	throw new CMLException("Failed to generate InChI: " + jie.getMessage());
        }
    }
    
    
    /**
     * Adds CMLIdentifier containing InChI to CMLMolecule.
     * 
     * @throws CMLException
     */
    public void appendToMolecule() throws CMLException {
        appendToElement(molecule);
    }
    
    
    /**
     * Adds CMLIdentifier containing InChI to specified element.
     * 
     * @param element
     * @throws CMLException
     */
    public void appendToElement(CMLElement element) throws CMLException {
        if (output.getInchi() == null) {
            throw new CMLException("Failed to generate InChI");
        }
        
        CMLIdentifier identifier = CMLIdentifier.makeElementInContext(molecule);
        identifier.setConvention(CML_INCHI_CONVENTION);
        identifier.appendChild(new Text(output.getInchi()));
        
        element.appendChild(identifier);
    }
    
    /**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed.
     * @return INCHI_RET
     */
    public INCHI_RET getReturnStatus() {
        return(output.getReturnStatus());
    }
    
    /**
     * Gets generated InChI string.
     * @return string
     */
    public String getInchi() {
    	return(output.getInchi());
    }
    
    /**
     * Gets generated InChI string.
     * @return string
     */
    public String getAuxInfo() {
    	return(output.getAuxInfo());
    }
    
    /**
     * Gets generated (error/warning) messages.
     * @return string
     */
    public String getMessage() {
    	return(output.getMessage());
    }
    
    /**
     * Gets generated log.
     * @return string
     */
    public String getLog() {
    	return(output.getLog());
    }
    
}
