package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;


/**
 * additional tools for currentBond. not fully developed
 * 
 * @author pmr
 * 
 */
public class SMILESTool extends AbstractTool {

	/** */
    public final static char C_SINGLE   = '-';
	/** */
    public final static char C_DOUBLE   = '=';
	/** */
    public final static char C_TRIPLE   = '#';
	/** */
    public final static char C_AROMATIC = '-';
	/** */
    public final static char C_NONE       = 0;
	/** */
    public final static char C_FORBIDDEN  = 1;
	/** */
    public final static char C_ZERO       = '0';
	/** */
    public final static char C_DOT        = '.';
	/** */
    public final static char C_LBRAK      = '(';
	/** */
    public final static char C_RBRAK      = ')';
	/** */
    public final static char C_LSQUARE    = '[';
	/** */
    public final static char C_MINUS      = '-';
	/** */
    public final static char C_PLUS       = '+';
	/** */
    public final static char C_RSQUARE    = ']';
	/** */
    public final static char C_SLASH      = '/';
	/** */
    public final static char C_BACKSLASH  = '\\';
	/** */
    public final static String S_AT     = "@";
	/** */
    public final static String S_ATAT   = "@@";

    /** */
    public final static char C_c 		= 'c';
    /** */
    public final static char C_n 		= 'n';
    /** */
    public final static char C_o 		= 'o';
    /** */
    public final static char C_p 		= 'p';

    private static String AROMATIC 	= "aromatic";
    private static String CHIRAL 	= "chiral";
    private static String TRUE 		= "true";
    private static String BR   		= "Br";
    private static String CL   		= "Cl";
    private static String DU   		= "Du";
    private static String SI   		= "Si";
    private static String SLASH   	= "slash";
    
    private CMLMolecule molecule;
    private CMLAtom currentAtom;
    private CMLBond currentBond;
    private char bondChar;
    private int natoms;
    private HydrogenControl hydrogenControl;
    private Map<Integer, String> atomCharacterMap = new HashMap<Integer, String>();
    /** constructor
     */
    public SMILESTool() {
    	hydrogenControl = HydrogenControl.NO_EXPLICIT_HYDROGENS;
    }
    
	/** parse SMILES.
     * 
     * @param ss
     */
    public void parseSMILES(String ss) {

    	ss = expandString(ss);
        final String s = ss.trim();
        molecule = new CMLMolecule();
        currentAtom = null;
        currentBond = null;
        bondChar = 0;

        final Stack<CMLAtom> stack = new Stack<CMLAtom>();
        final CMLAtom[] rings = new CMLAtom[10];
//        boolean atomOrBracket = true;
        final int l = s.length();
        int i = 0;
        char c = 0;
        char slashChar = C_NONE;
        boolean hasDot = false;
        while (i < l) {
            c = s.charAt(i);
            if (c == C_LBRAK) {
                stack.push(currentAtom);
                i++;
            } else if (c == C_RBRAK) {
                if (stack.isEmpty()) {
                    throw new CMLRuntimeException("Unexpected "+C_RBRAK);
                }
                currentAtom = stack.pop();
                bondChar = C_NONE;
                i++;
            } else if (c == C_LSQUARE) {
                final int idx = s.indexOf(C_RSQUARE, i);
                if (idx == -1) {
                    throw new CMLRuntimeException("Unbalanced "+C_LSQUARE);
                }
                int atomStartChar = i;
                String atomString = s.substring(i+1, idx);
                i = idx + 1;
                currentAtom = addExtendedAtom(atomString, slashChar);
            	atomCharacterMap.put(new Integer(atomStartChar), currentAtom.getId());
                bondChar = C_NONE;
            } else if(
                c == C_SINGLE ||
                c == C_AROMATIC ||
                c == C_DOUBLE ||
                c == C_TRIPLE
                ) {
                if (bondChar != C_NONE) {
                    throw new CMLRuntimeException("Bond not expected here: "+s.substring(i));
                }
                bondChar = c;
                i++;
            } else if(
                c == C_SLASH ||
                c == C_BACKSLASH
                ) {
                slashChar = c;
                currentAtom.setAttribute(SLASH, ""+c);
                i++;
            } else if(c == C_DOT) {
            	hasDot = true;
                currentAtom = null;
                i++;
            } else if(Character.isDigit(c)) {
                final int ring = c - C_ZERO;
                if (rings[ring] == null) {
                    rings[ring] = currentAtom;
                } else {
                    currentBond = addBond(rings[ring], currentAtom, bondChar);
                    currentBond.setOrder(CMLBond.SINGLE);
                    rings[ring] = null;
                }
                i++;
            } else if(Character.isLetter(c)) {
            	int atomStartChar = i;
                final String atomString = grabAtom(s.substring(i));
                i += atomString.length();
                CMLAtom atom = addAtom(atomString, slashChar);
                bondChar = C_NONE;
            	atomCharacterMap.put(new Integer(atomStartChar), atom.getId());
            } else {
                throw new CMLRuntimeException("Cannot interpret SMILES: "+s.substring(i));
            }
        }
        if (hasDot) {
        	new ConnectionTableTool(molecule).partitionIntoMolecules();
        	if (molecule.getMoleculeCount() > 0) {
        		molecule.debug();
        	}
        }
        addHydrogens();
    }
    
    /**
     * @param i
     * @return id of atom starting at character i
     */
    public String getAtomIdAtChar(int i) {
    	return atomCharacterMap.get(new Integer(i));
    }
    private String expandString(String s) {
	   String ss = s;
	   while (true) {
		   int ii = ss.indexOf(C_LCURLY);
		   if (ii == -1) {
			   break;
		   }
		   String startS = ss.substring(0, ii);
		   ss = ss.substring(ii);
		   int jj = Util.indexOfBalancedBracket(C_LCURLY, ss);
		   if (jj == -1) {
			   throw new RuntimeException("Unbalanced {}");
		   }
		   String midS = ss.substring(1, jj);
		   String endS = ss.substring(jj+1);
		   if (!endS.startsWith(S_STAR+S_LBRAK)) {
			   throw new CMLRuntimeException("expected * count after }");
		   }
		   endS = endS.substring(2);
		   int idx = endS.indexOf(S_RBRAK);
		   if (idx == -1) {
			   throw new RuntimeException("Unbalanced brackets round count");
		   }
		   int count = Integer.parseInt(endS.substring(0, idx));
		   midS = expandString(midS);
		   endS = endS.substring(idx+1);
		   ss = startS;
		   for (int i = 0; i < count; i++) {
			   ss += midS;
		   }
		   ss += endS;
	   }
	   return ss;
   }
    
    private void addHydrogens() {
    	MoleculeTool moleculeTool = MoleculeTool.getOrCreateMoleculeTool(molecule);
    	// remember explicit H
//    	hydrogenControl = HydrogenControl.;
    	moleculeTool.adjustHydrogenCountsToValency(hydrogenControl);
    	// decrement aromatic atom H count
    	for (CMLAtom atom : molecule.getAtoms()) {
    		if (TRUE.equals(atom.getAttributeValue(AROMATIC))) {
    			atom.setHydrogenCount(atom.getHydrogenCount() - 1);
    		}
    	}
    }

    private String grabAtom(final String s) {
    	if (s.length() == 0) {
    		throw new CMLRuntimeException("empty element symbol");
    	}
        String atomString = s.substring(0, 1);
    	if (Character.isLowerCase(s.charAt(0))) {
    		char el = s.charAt(0);
    		if (el == C_c ||
    			el == C_n ||
    			el == C_o ||
    			el == C_p)
    		{
    			;//
    		} else {
    			throw new CMLRuntimeException("element may not start with lowercase: "+s);
    		}
    	} else 
	// Cl and Br are hardcoded
	        if (s.startsWith(BR) ||
	            s.startsWith(SI) ||
	            s.startsWith(CL)) {
	            atomString = s.substring(0, 2);
        } else {
        	if(s.length() == 2 && Character.isLowerCase(s.charAt(1))) {
        		atomString = s.substring(0, 2);
        	}
        }
    	String elementSymbol = (atomString.length() == 1) ? atomString.toUpperCase() : atomString;
        ChemicalElement chemicalElement = ChemicalElement.getChemicalElement(elementSymbol);
        if (chemicalElement == null) {
        	throw new CMLRuntimeException("Unknown element: "+atomString);
        }
        return atomString;
    }


//atom : '[' <mass> symbol <chiral> <hcount> <sign<charge>> ']'

    private CMLAtom addExtendedAtom(final String s, final char slashChar) {
// create atom with dummy elementType
        final CMLAtom atom = addAtom(DU, slashChar);
        final int l = s.length();
        int i = 0;
// isotope
        while (true) {
            if (!Character.isDigit(s.charAt(i))) {
                break;
            }
            i++;
        }
        if (i != 0) {
            atom.setIsotopeNumber(Integer.parseInt(s.substring(0, i)));
        }
// elementType
        final String elementType = grabAtom(s.substring(i));
        setElementType(atom, elementType);
        i += elementType.length();

// chirality
        if (i < l) {
            if (s.substring(i).startsWith(S_ATAT)) {
                atom.setAttribute(CHIRAL, S_ATAT);
                i++; i++;
            } else if (s.substring(i).startsWith(S_AT)) {
                atom.setAttribute(CHIRAL, S_AT);
                i++;
            }
        }
// hydrogenCount
        if (i < l) {
            if (s.charAt(i) == 'H') {
                i++;
                if (i < l && Character.isDigit(s.charAt(i))) {
                    atom.setHydrogenCount(Integer.parseInt(s.substring(i, i+1)));
                    i++;
                } else {
                    atom.setHydrogenCount(1);
                }
            }
        }
// formalCharge
        if (i < l) {
            final char sign = s.charAt(i);
        	if (sign != C_PLUS && sign != C_MINUS) {
        		throw new CMLRuntimeException("Sign must be of form -[n] or +[n]");
        	}
            i++;
            int charge = 1;
            if (i >= l) {
            	charge = (sign == C_MINUS) ? -1 : 1;
            } else if (!Character.isDigit(s.charAt(i))) {
                throw new CMLRuntimeException("Sign must be of form -n or +n");
            } else {
	            charge = s.charAt(i) - C_ZERO;
	            if (sign == C_MINUS) {
	                charge *= -1;
	            }
            }
            atom.setFormalCharge(charge);
        }
        return atom;
    }

    private void setElementType(final CMLAtom atom, String elementType) {
    	if (elementType.length() < 1 || elementType.length() > 2) {
            throw new CMLRuntimeException("Element of wrong length :"+elementType+":");
        } else if (elementType.length() == 1) {
            if (Character.isLowerCase(elementType.charAt(0))) {
                atom.setAttribute(AROMATIC, TRUE);
                elementType = elementType.toUpperCase();
            }
        } else {
            if (!Character.isUpperCase(elementType.charAt(0)) &&
                Character.isLowerCase(elementType.charAt(1))) {
                throw new CMLRuntimeException("Bad element :"+elementType);
            }
        }
        atom.setElementType(elementType);
    }

    @SuppressWarnings("unused")
    private CMLAtom addAtom(String elementType, final char slashChar) {
        final CMLAtom atom = new CMLAtom("a"+(++natoms));
        molecule.addAtom(atom);
        setElementType(atom, elementType);
        if (slashChar != C_NONE) {
            atom.setAttribute(SLASH, ""+slashChar);
        }
        if (currentAtom != null) {
        	final
            CMLBond bond = addBond(currentAtom, atom, bondChar);
        }
        currentAtom = atom;
        return atom;
    }

    private CMLBond addBond(final CMLAtom currentAtom, final CMLAtom atom, char bondChar) {
        final CMLBond bond = new CMLBond(currentAtom, atom);
        molecule.addBond(bond);
        if (bondChar == C_NONE) {
            bondChar = C_SINGLE;
        }
        if (bondChar == C_SINGLE) {
            bond.setOrder(CMLBond.SINGLE);
        } else if (bondChar == C_DOUBLE) {
            bond.setOrder(CMLBond.DOUBLE);
        } else if (bondChar == C_TRIPLE) {
            bond.setOrder(CMLBond.TRIPLE);
        } else if (bondChar == C_AROMATIC) {
            bond.setOrder(CMLBond.AROMATIC);
        } else {
            throw new CMLRuntimeException("Unknown currentBond type :"+bondChar+":");
        }
        return bond;
    }
	/**
	 * @return the molecule
	 */
	public CMLMolecule getMolecule() {
		return molecule;
	}
	/**
	 * @param molecule the molecule to set
	 */
	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}

    /**
	 * @return the hydrogenControl
	 */
	public HydrogenControl getHydrogenControl() {
		return hydrogenControl;
	}

	/**
	 * @param hydrogenControl the hydrogenControl to set
	 */
	public void setHydrogenControl(HydrogenControl hydrogenControl) {
		this.hydrogenControl = hydrogenControl;
	}

}	
