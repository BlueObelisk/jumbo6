package org.xmlcml.cml.inchi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jniinchi.INCHI_BOND_TYPE;
import net.sf.jniinchi.INCHI_RET;
import net.sf.jniinchi.JniInchiAtom;
import net.sf.jniinchi.JniInchiBond;
import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiInputInchi;
import net.sf.jniinchi.JniInchiOutputStructure;
import net.sf.jniinchi.JniInchiWrapper;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * <p>This class generates a CMLMolecule from an InChI string.  It places 
 * calls to a JNI wrapper for the InChI C++ library.
 * 
 * <p>The generated IAtomContainer will have all 2D and 3D coordinates set to 0.0,
 * but may have atom parities set.  Double bond and allene stereochemistry are
 * not currently recorded.
 * 
 * <h3>Example usage</h3>
 * 
 * <code>// Generate factory - throws CMLException if native code does not load</code><br>
 * <code>InChIGeneratorFactory factory = new InChIGeneratorFactory();</code><br>
 * <code>// Get InChIToStructure</code><br>
 * <code>InChIToStructure intostruct = factory.getInChIToStructure(inchi);</code><br>
 * <code></code><br>
 * <code>INCHI_RET intostruct = gen.getReturnStatus();</code><br>
 * <code>if (ret == INCHI_RET.WARNING) {</code><br>
 * <code>  // Structure generated, but with warning message</code><br>
 * <code>  System.out.println("InChI warning: " + intostruct.getMessage());</code><br>
 * <code>} else if (ret != INCHI_RET.OKAY) {</code><br>
 * <code>  // Structure generation failed</code><br>
 * <code>  throw new CMLxception("Structure generation failed failed: " + ret.toString()</code><br>
 * <code>    + " [" + intostruct.getMessage() + S_RSQUARE);</code><br>
 * <code>}</code><br>
 * <code></code><br>
 * <code>CMLMolecule molecule = intostruct.getMolecule();</code><br>
 * 
 * @author Sam Adams
 */
public class InChIToStructure implements CMLConstants {
	
	protected JniInchiInputInchi input;
	
	protected JniInchiOutputStructure output;
	
	protected CMLMolecule molecule;
	
	/**
	 * Constructor. Generates CMLMolecule from InChI.
	 * @param inchi
	 * @throws CMLException
	 */
	public InChIToStructure(String inchi) throws CMLException {
		try {
			input = new JniInchiInputInchi(inchi, S_EMPTY);
		} catch (JniInchiException jie) {
			throw new CMLException("Failed to convert InChI to molecule: " + jie.getMessage());
		}
        generateCMLMoleculeFromInchi();
	}
	
	/**
	 * Constructor. Generates CMLMolecule from InChI.
	 * @param inchi
	 * @param options
	 * @throws CMLException
	 */
	public InChIToStructure(String inchi, String options) throws CMLException {
		try {
			input = new JniInchiInputInchi(inchi, options);
		} catch (JniInchiException jie) {
			throw new CMLException("Failed to convert InChI to molecule: " + jie.getMessage());
		}
        generateCMLMoleculeFromInchi();
	}
	
	/**
	 * Constructor. Generates CMLMolecule from InChI.
	 * @param inchi
	 * @param options
	 * @throws CMLException
	 */
	public InChIToStructure(String inchi, List options) throws CMLException {
		try {
			input = new JniInchiInputInchi(inchi, options);
		} catch (JniInchiException jie) {
			throw new CMLException("Failed to convert InChI to molecule: " + jie.getMessage());
		}
        generateCMLMoleculeFromInchi();
	}
	
	protected void generateCMLMoleculeFromInchi() throws CMLException {
		try {
			output = JniInchiWrapper.getStructureFromInchi(input);
        } catch (JniInchiException jie) {
        	throw new CMLException("Failed to convert InChI to molecule: " + jie.getMessage());
        }
		
        molecule = new CMLMolecule();
        
        Map<JniInchiAtom, CMLAtom> inchiCmlAtomMap = new HashMap<JniInchiAtom, CMLAtom>();
        
        for (int i = 0; i < output.getNumAtoms(); i ++) {
        	JniInchiAtom iAt = output.getAtom(i);
        	CMLAtom cAt = new CMLAtom();
        	
        	inchiCmlAtomMap.put(iAt, cAt);
        	
        	cAt.setId("a" + i);
        	cAt.setElementType(iAt.getElementType());
        	
        	// Ignore coordinates - all zero
        	
        	int charge = iAt.getCharge();
        	if (charge != 0) {
        		cAt.setFormalCharge(charge);
        	}
        	
        	int numH = iAt.getImplicitH();
        	if (numH != 0) {
        		cAt.setHydrogenCount(numH);
        	}
        	
        	molecule.addAtom(cAt);
        }
        
        for (int i = 0; i < output.getNumBonds(); i ++) {
        	JniInchiBond iBo = output.getBond(i);
        	
        	CMLAtom atO = inchiCmlAtomMap.get(iBo.getOriginAtom());
        	CMLAtom atT = inchiCmlAtomMap.get(iBo.getTargetAtom());
        	CMLBond cBo = new CMLBond(atO, atT);
        	
        	INCHI_BOND_TYPE type = iBo.getBondType();
        	if (type == INCHI_BOND_TYPE.SINGLE) {
        		cBo.setOrder(CMLBond.SINGLE);
        	} else if (type == INCHI_BOND_TYPE.DOUBLE) {
        		cBo.setOrder(CMLBond.DOUBLE);
        	} else if (type == INCHI_BOND_TYPE.TRIPLE) {
        		cBo.setOrder(CMLBond.TRIPLE);
        	} else if (type == INCHI_BOND_TYPE.ALTERN) {
        		cBo.setOrder(CMLBond.AROMATIC);
        	} else {
        		throw new CMLException("Unknown bond type: " + type);
        	}
        	
        	// TODO: bond sterochemistry
        	
        	molecule.addBond(cBo);
        }
        
        // Add explict hydrogens to hydrogen counts
        for (int i = 0; i < molecule.getAtomCount(); i ++) {
        	CMLAtom at = molecule.getAtom(i);
        	if (at.getHydrogenCountAttribute() != null) {
	        	List<CMLAtom> ligandList = at.getLigandAtoms();
	        	int hLigands = 0;
	        	for (int j = 0; j < ligandList.size(); j ++) {
	        		if (AS.H.equals(ligandList.get(j).getElementType())) {
	        			hLigands ++;
	        		}
	        	}
	        	
	        	if (hLigands > 0) {
	        		int numH = at.getHydrogenCount() + hLigands;
	        		at.setHydrogenCount(numH);
	        	}
        	}
        }
	}
	
	/**
	 * Returns generated molecule.
	 * @return CMLMolecule
	 */
	public CMLMolecule getMolecule() {
		return(molecule);
	}
	
	
	/**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed.
     * @return status
     */
    public INCHI_RET getReturnStatus() {
        return(output.getReturnStatus());
    }
    
    /**
     * Gets generated (error/warning) messages.
     * @return message
     */
    public String getMessage() {
        return(output.getMessage());
    }
    
    /**
     * Gets generated log.
     * @return log
     */
    public String getLog() {
        return(output.getLog());
    }
	
    /**
	 * <p>Returns warning flags, see INCHIDIFF in inchicmp.h.
	 * 
	 * <p>[x][y]:
	 * <br>x=0 => Reconnected if present in InChI otherwise Disconnected/Normal
	 * <br>x=1 => Disconnected layer if Reconnected layer is present
	 * <br>y=1 => Main layer or Mobile-H
	 * <br>y=0 => Fixed-H layer
     * @return flags
	 */
    public long[][] getWarningFlags() {
    	return(output.getWarningFlags());
    }
}
