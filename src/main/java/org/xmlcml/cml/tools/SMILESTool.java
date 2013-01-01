package org.xmlcml.cml.tools;

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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import nu.xom.Attribute;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomArray;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
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

	private static Logger LOG = Logger.getLogger(SMILESTool.class);
	static {
		LOG.setLevel(Level.WARN);
	}
	
    /** dewisott */
    public final static char C_SINGLE   = '-';
    /** dewisott */
    public final static char C_DOUBLE   = '=';
    /** dewisott */
    public final static char C_TRIPLE   = '#';
    /** dewisott */
    public final static char C_AROMATIC = ':';
    /** dewisott */
    public final static char C_NONE       = 0;
    /** dewisott */
    public final static char C_FORBIDDEN  = 1;
    /** dewisott */
    public final static char C_ZERO       = '0';
    /** dewisott */
    public final static char C_DOT        = '.';
    /** dewisott */
    public final static char C_LBRAK      = '(';
    /** dewisott */
    public final static char C_RBRAK      = ')';
    /** dewisott */
    public final static char C_LSQUARE    = '[';
    /** dewisott */
    public final static char C_MINUS      = '-';
    /** dewisott */
    public final static char C_PLUS       = '+';
    /** dewisott */
    public final static char C_RSQUARE    = ']';
    /** dewisott */
    public final static char C_SLASH      = '/';
    /** dewisott */
    public final static char C_BACKSLASH  = '\\';
    /** dewisott */
    public final static char C_PERC       = '%';
    /** dewisott */
    public final static String S_AT     = "@";
    /** dewisott */
    public final static String S_ATAT   = "@@";

    /** */
    public final static char C_c 		= 'c';
    /** */
    public final static char C_n 		= 'n';
    /** */
    public final static char C_o 		= 'o';
    /** */
    public final static char C_p 		= 'p';
    /** */
    public final static char C_s 		= 's';
    /** */
    public final static String S_as 	= "as";
    /** */
    public final static String S_se 	= "se";
    /** */
    public final static char C__ ='_';
    /** */
    public final static char C_$ ='$';

    private static String AROMATIC 	= "aromatic";
    private static String CHIRAL 	= "chiral";
    private static String TRUE 		= "true";

    private static String B   		= "B";
    private static String C   		= "C";
    private static String N   		= "N";
    private static String O   		= "O";
    private static String P   		= "P";
    private static String S   		= "S";
    private static String F   		= "F";
    private static String CL   		= "Cl";
    private static String BR   		= "Br";
    private static String I   		= "I";

    private static String DU   		= "Du";
    //private static String SI   		= "Si";
    private static String SLASH   	= "slash";
    
    private CMLMolecule molecule;
    private CMLAtom currentAtom;
    private CMLBond currentBond;
    private CMLAtom lastAtom;
    private char bondChar;
    private int natoms;
    private int nrs;
    private HydrogenControl hydrogenControl;
    private List<String> atomIdList;
    private List<String> atomChunkList;
//    private List<String> bondIdList;
//    private List<String> bondChunkList;
//    private List<String> ringIdList;
//    private List<String> ringChunkList;
    private String rawSmiles;
private String smilescopy;
private int sLength;
//	private String scopy;
    
    
	/**
	 * Contains an atom and the order of the bond that it will form when a corresponding ring closure is found
	 */
	private class RingOpening {
		/**The atom that will be bonded to when a corresponding ring closure is found*/
		CMLAtom atom;
		/**The order of the bond about to be formed can be either C_SINGLE, C_DOUBLE, C_TRIPLE, or C_AROMATIC.*/
		char bondChar;


		/**
		 * An Atom and a bond order
		 * @param a
		 * @param bondOrderVal
		 */
		RingOpening(CMLAtom a, char bondOrderChar) {
			atom = a;
			bondChar = bondOrderChar;
		}
	}
	
    /** constructor
     */
    public SMILESTool() {
    	hydrogenControl = HydrogenControl.NO_EXPLICIT_HYDROGENS;
    }
    
    public SMILESTool(CMLMolecule molecule) {
    	this();
    	this.setMolecule(molecule);
    }
    
	/** parse SMILES.
     * 
     * @param sss
     */
    public void parseSMILES(String sss) {

    	if (sss == null) {
    		return;
    	}
    	initSmiles(sss);

        final Stack<CMLAtom> stack = new Stack<CMLAtom>();
        final RingOpening[] rings = new RingOpening[99];
        int i = 0;
        char c = 0;
        char slashChar = C_NONE;
        boolean hasDot = false;
        while (i < sLength) {
        	lastAtom=currentAtom;
            c = rawSmiles.charAt(i);
            if (c == C_LBRAK) {
                stack.push(currentAtom);
                i++;
            } else if (c == C_RBRAK) {
                if (stack.isEmpty()) {
                    throw new RuntimeException("Unexpected "+C_RBRAK);
                }
                currentAtom = stack.pop();
                bondChar = C_NONE;
                i++;
            } else if (c == C_LSQUARE) {
                final int idx = rawSmiles.indexOf(C_RSQUARE, i);
                if (idx == -1) {
                    throw new RuntimeException("Unbalanced "+C_LSQUARE);
                }
                int atomStartChar = i;
//                String atomChunk = rawSmiles.substring(i, idx+1);
                String atomString = rawSmiles.substring(i+1, idx);
                i = idx + 1;
                currentAtom = addExtendedAtom(atomString, slashChar, atomStartChar, atomString);
                bondChar = C_NONE;
                slashChar = C_NONE;
            } else if(
                c == C_SINGLE ||
                c == C_AROMATIC ||
                c == C_DOUBLE ||
                c == C_TRIPLE
                ) {
                if (bondChar != C_NONE || i==0 || i==sLength-1) {
                    throw new RuntimeException("Bond not expected here: "+rawSmiles.substring(i));
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
            } else if(Character.isDigit(c) || c == C_PERC ) {
            	if (currentAtom ==null){
                    throw new RuntimeException("Ring: "+ c + " does not have the starting or ending atom defined!");	
            	}
            	
            	int ring;
            	if (c == C_PERC){//support for using % syntax to define ring openings
            		i++;
            		if(i +1 < sLength){
            			try{
            				ring= Integer.parseInt(rawSmiles.substring(i, i+2));
            				i++;
            			}
            			catch (NumberFormatException e){
            				throw new RuntimeException("Expected two digit number after % sign in string. Found: "
            						+rawSmiles.substring(i, i+2));
            			}
            		}
            		else{
            			throw new RuntimeException("Expected two digit number after % sign in SMILES. Found end of SMILES");	
            		}
            	}
            	else{
            		ring =c - C_ZERO;
            	}
                if (rings[ring] == null) {
                	// start of ring
                	rings[ring] = new RingOpening(currentAtom, bondChar);
                	bondChar = C_NONE;
                    
                    //Notes the order rings connect to a chiral atom
                    if (currentAtom.getAttributeValue(CHIRAL)!= null){
                    	CMLElements<CMLAtomParity> atomParity = currentAtom.getAtomParityElements();
	                	for (CMLAtomParity atomParityTag : atomParity) {
	                		String[] atomRefs4 =atomParityTag.getAtomRefs4();
	                		
	                		for (int k = 0; k < atomRefs4 .length; k++) {
	                			if (atomRefs4[k].equals("")){
	                				atomRefs4[k]="ring$#!"+ring;
	                				break;
	                			}
	        				}
	                		atomParityTag.setAtomRefs4(atomRefs4);
	                	}
                    }                   
                } else {
                	// end of ring
                	
                	//Updates chiral atom with id of atom that has joined to it
                    if (rings[ring].atom.getAttributeValue(CHIRAL)!= null){
                    	CMLElements<CMLAtomParity> atomParity = rings[ring].atom.getAtomParityElements();
	                	for (CMLAtomParity atomParityTag : atomParity) {
	                		String[] atomRefs4 =atomParityTag.getAtomRefs4();
	                		
	                		for (int k = 0; k < atomRefs4 .length; k++) {
	                			if (atomRefs4[k].equals("ring$#!"+ring)){
	                				atomRefs4[k]=currentAtom.getId();
	                				break;
	                			}
	        				}
	                		atomParityTag.setAtomRefs4(atomRefs4);
	                	}
                    }
                    if (bondChar == C_NONE){
                        currentBond = addBond(rings[ring].atom, currentAtom,  rings[ring].bondChar, rawSmiles);
                    }
                    else if (rings[ring].bondChar == C_NONE){
                    	 currentBond = addBond(rings[ring].atom, currentAtom,  bondChar, rawSmiles);
                    	 bondChar = C_NONE;
                    }
                    else if (rings[ring].bondChar == bondChar) {
                    	currentBond = addBond(rings[ring].atom, currentAtom,  bondChar, rawSmiles);
                    }
                    else{
                    	throw new RuntimeException("Ring opening " + ring + " has two specifications as to what order bond it should form: " + sss);
                    }
                    rings[ring] = null;
                }
                i++;
            } else if(Character.isLetter(c)) {
            	int atomStartChar = i;
                final String atomString = grabOrganicAtom(rawSmiles.substring(i));
                i += atomString.length();
                /*CMLAtom atom = */ addAtom(atomString, slashChar, rawSmiles, atomStartChar, atomString);
                bondChar = C_NONE;
                slashChar = C_NONE;
                
            } else if(c == C__ | c== C_$){
            	
            	final CMLAtom atom = new CMLAtom("r"+(++nrs));
            	 molecule.addAtom(atom);
                 setElementType(atom, "R");
                 if (slashChar != C_NONE) {
                     atom.setAttribute(SLASH, ""+slashChar);
                 }
             	atomIdList.add(0, atom.getId());
             	atomChunkList.add(0, "R");
                 if (currentAtom != null) {
                     addBond(currentAtom, atom, bondChar, rawSmiles);
                 }
                 currentAtom = atom;
            	i++;
            }
            else {
                throw new RuntimeException("Cannot interpret SMILES: "+rawSmiles.substring(i));
            }
            
        }
        
        for (int r = 0; r < rings.length; r++) {
			if (rings[r] != null){
				throw new RuntimeException("Ring: "+ r +" not closed! "+smilescopy);
			}
		}
        
        markupDoubleBondCisTrans();
    	removeSmilesSpecificAttributes();
        
        if (hasDot) {
        	new ConnectionTableTool(molecule).partitionIntoMolecules();
        	if (molecule.getMoleculeCount() > 0) {
        	}
        }
        makeAromaticBonds();
        addHydrogens();
//    	convertToKekule();


    }

	private void initSmiles(String sss) {
		smilescopy = sss;
		rawSmiles = expandString(sss);
        rawSmiles = rawSmiles.trim();
        molecule = new CMLMolecule();
        currentAtom = null;
        currentBond = null;
        bondChar = 0;
        atomIdList    = new ArrayList<String>();
        atomChunkList = new ArrayList<String>();
        sLength = rawSmiles.length();
        
        for (int i = 0; i < sLength; i++) {
        	atomIdList.add(null);
        	atomChunkList.add(null);
        }
	}

    //removes Chiral and Slash attributes
    private void removeSmilesSpecificAttributes() {
        CMLAtomArray atomsInMoleculeArray = molecule.getAtomArray();
        List<CMLAtom> atomsInMolecule = atomsInMoleculeArray.getAtoms(); 
        for (CMLAtom atom : atomsInMolecule) {
        	if (atom.getAttributeValue(SLASH) != null){
        		atom.removeAttribute(SLASH);
        	}
        	if (atom.getAttributeValue(CHIRAL) != null){
        		atom.removeAttribute(CHIRAL);
        	}
        }
		
	}

	//  C\C=C\C=C\C
    private void markupDoubleBondCisTrans() {
        CMLAtomArray atomsInMoleculeArray = molecule.getAtomArray();
        List<CMLAtom> atomsInMolecule = atomsInMoleculeArray.getAtoms();  
        for (CMLAtom atom : atomsInMolecule) {
        	if (atom.getAttributeValue(SLASH) != null){
        		Set<CMLAtom> atomsVisited = new HashSet<CMLAtom>();
        		List<CMLAtom> atomRefs4sInDoubleBond = new ArrayList<CMLAtom>();
        	    atomRefs4sInDoubleBond = recurseThroughAtoms(atomsVisited, atomRefs4sInDoubleBond, atom, 0);
        	    if (atomRefs4sInDoubleBond.size() ==4){
        	    	List<CMLBond> bondListWhichIncludesDoubleBond = atomRefs4sInDoubleBond.get(1).getLigandBonds();
        	    	for (CMLBond bond : bondListWhichIncludesDoubleBond) {
	        			if (CMLBond.isDouble(bond.getOrder()) && 
	        					bond.query("cml:bondStereo", CMLConstants.CML_XPATH).size() == 0) {
	        				CMLBondStereo bondstereo =new CMLBondStereo();
	        				bondstereo.setAtomRefs4(
	        						new String[]{
	        						atomRefs4sInDoubleBond.get(0).getId(),
	        						atomRefs4sInDoubleBond.get(1).getId(),
	        						
	        						atomRefs4sInDoubleBond.get(2).getId(),
	        						atomRefs4sInDoubleBond.get(3).getId()
	        						});
	        				
	        				char bond1Slashtype=atomRefs4sInDoubleBond.get(1).getAttributeValue(SLASH).charAt(0);
	        				char bond2Slashtype=atomRefs4sInDoubleBond.get(2).getAttributeValue(SLASH).charAt(0);
	        				
	        				if ((bond1Slashtype == C_SLASH  && bond2Slashtype == C_SLASH) ||
	        					(bond1Slashtype == C_BACKSLASH  && bond2Slashtype == C_BACKSLASH)){
		        					bondstereo.setXMLContent("T");	
	        				}
	        				
	        				if ((bond1Slashtype == C_BACKSLASH  && bond2Slashtype == C_SLASH) ||
		        					(bond1Slashtype == C_SLASH  && bond2Slashtype == C_BACKSLASH)){
			        					bondstereo.setXMLContent("C");	
		        			}
	        				bond.appendChild(bondstereo);
	        			}
        	    	}
        	    }
        	}
        }
	}
    


	private List<CMLAtom> recurseThroughAtoms(Set<CMLAtom> atomsVisited, 
			List<CMLAtom> atomRefs4sInDoubleBond, CMLAtom atom, int i) {
		atomsVisited.add(atom);
		atomRefs4sInDoubleBond.add(i,atom);
		if (i==3){
			return atomRefs4sInDoubleBond;
		}
		List<CMLAtom> connectedAtoms = atom.getLigandAtoms();
		List<CMLAtom> tempAtomRefs4sInDoubleBond=new ArrayList<CMLAtom>(atomRefs4sInDoubleBond);
		for (CMLAtom atom2 : connectedAtoms ) {
			if (atom2.getAttributeValue(SLASH)!=null && !atomsVisited.contains(atom2)){
				if (i==1 && !CMLBond.isDouble(molecule.getBond(atom, atom2).getOrder())){
					continue;//bond between 2nd and 3rd atom should be a double bond
				}
				atomRefs4sInDoubleBond= recurseThroughAtoms(atomsVisited, atomRefs4sInDoubleBond, atom2, i+1);
				if (atomRefs4sInDoubleBond.size()==4){
					return atomRefs4sInDoubleBond;
				}
				else{
					atomRefs4sInDoubleBond=tempAtomRefs4sInDoubleBond;
				}
			}
		}
		return atomRefs4sInDoubleBond;
	}


	/**
     * @param i
     * @return id of atom starting at character i
     */
    String getAtomIdAtChar(int i) {
    	return (atomIdList == null || atomIdList.size() <= i) ? null : atomIdList.get(i);
    }
    
    /**
     * @param i
     * @return id of atom starting at character i
     */
    String getAtomChunkAtChar(int i) {
    	return (atomChunkList == null || atomChunkList.size() <= i) ? null : atomChunkList.get(i);
    }
        
//    /**
//     * @param i
//     * @return id of bond starting at character i
//     */
//    String getBondIdAtChar(int i) {
//    	return (bondIdList == null || bondIdList.size() <= i) ? null : bondIdList.get(i);
//    }
//    
//    /**
//     * @param i
//     * @return id of bond starting at character i
//     */
//    String getBondChunkAtChar(int i) {
//    	return (bondChunkList == null || bondChunkList.size() <= i) ? null : bondChunkList.get(i);
//    }
//        
//    /**
//     * @param i
//     * @return id of ring starting at character i
//     */
//    String getRingIdAtChar(int i) {
//    	return (ringIdList == null || ringIdList.size() <= i) ? null : ringIdList.get(i);
//    }
//    
//    /**
//     * @param i
//     * @return id of ring starting at character i
//     */
//    String getRingChunkAtChar(int i) {
//    	return (ringChunkList == null || ringChunkList.size() <= i) ? null : ringChunkList.get(i);
//    }
        
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
			   throw new RuntimeException("expected * count after }");
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
    	MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    	// remember explicit H
//    	hydrogenControl = HydrogenControl.;
    	moleculeTool.adjustHydrogenCountsToValency(hydrogenControl);
    	// decrement aromatic atom H count
    	for (CMLAtom atom : molecule.getAtoms()) {
//    		if (TRUE.equals(atom.getAttributeValue(AROMATIC))) {
//    			atom.setHydrogenCount(atom.getHydrogenCount() - 1);
//    			atom.deleteAnyLigandHydrogenAtom();
//    		}
    	}
    	// make all H explicit
    	moleculeTool.expandImplicitHydrogens(hydrogenControl);
    	molecule.addNamespaceDeclaration("cmlx", "http://www.xml-cml.org/schema/cmlx");
    	molecule.addAttribute(new Attribute("cmlx:explicitHydrogens", "http://www.xml-cml.org/schema/cmlx", "true"));
    	// remove all hydrogenCounts
    	for (CMLAtom atom : molecule.getAtoms()) {
    		Attribute hcount = atom.getAttribute("hydrogenCount");
    		if (hcount != null) {
    			hcount.detach();
    		}
    	}
    }

	private void makeAromaticBonds() {
		for (CMLBond bond : molecule.getBonds()) {
    		CMLAtom atom0 = bond.getAtom(0);
    		CMLAtom atom1 = bond.getAtom(1);
    		if (TRUE.equals(atom0.getAttributeValue(AROMATIC)) &&
        		TRUE.equals(atom1.getAttributeValue(AROMATIC))) {
    			bond.setOrder(CMLBond.AROMATIC);
    		}
    	}
	}
	
//	private void convertToKekule() {
//	}

	/*
	 * Retrieves the one or two letters corresponding to the symbol for the atom
	 * Only organic elements can appear in SMILES strings outside square brackets
	 */
    private String grabOrganicAtom(final String s) {
    	if (s.length() == 0) {
    		throw new RuntimeException("empty element symbol");
    	}
        String atomString = s.substring(0, 1);
    	if (Character.isLowerCase(s.charAt(0))) {
    		char el = s.charAt(0);
    		if (el == C_c ||
    			el == C_n ||
    			el == C_o ||
    			el == C_p ||
    			el == C_s)
    		{
    			;//
    		} else {
    			throw new RuntimeException("element may not start with lowercase: "+s);
    		}
    	}
        else if (s.startsWith(CL) || s.startsWith(BR)) {
	            atomString = s.substring(0, 2);
    	} else if(
    			s.startsWith(B) ||
    			s.startsWith(C) ||
    			s.startsWith(N) ||
    			s.startsWith(O) || 
    			s.startsWith(P) ||
    			s.startsWith(S) ||
    			s.startsWith(F) ||
    			s.startsWith(I) ){
    		atomString = s.substring(0, 1);
    	}
    	else {
        	throw new RuntimeException("Unknown element encountered: "+atomString +
        			" Only organic elements may appear outside square brackets: "+s);
        }
        return atomString;
    }
    
    
	/*
	 * Retrieves the one or two letters corresponding to the symbol for the atom
	 * Presently a check is done to confirm that
	 * this corresponds to an actual element (daylight doesn't do this)
	 */
    private String grabAtom(final String s) {
    	if (s.length() == 0) {
    		throw new RuntimeException("empty element symbol");
    	}
        String atomString = s.substring(0, 1);
        String elementSymbol = atomString;
    	if (Character.isLowerCase(s.charAt(0))) {
    		
    		char el = s.charAt(0);
    		if (s.startsWith(S_as) ||
    			s.startsWith(S_se)){
    			elementSymbol =atomString.toUpperCase() +s.charAt(1);
    			atomString = s.substring(0, 2);
    		}
    		else if (el == C_c ||
    			el == C_n ||
    			el == C_o ||
    			el == C_p ||
    			el == C_s)
    		{
    			elementSymbol =atomString.toUpperCase();
    		} else {
    			throw new RuntimeException("element may not start with lowercase: "+s);
    		}
    	}
    	else {
        	if(s.length() == 2 && Character.isLowerCase(s.charAt(1))) {
        		atomString = s.substring(0, 2);
        		elementSymbol = atomString;
        	}
        }
    	if (elementSymbol.equals(S_STAR)) {
    		atomString = ChemicalElement.AS.R.value;
    	} else {
	        ChemicalElement chemicalElement = ChemicalElement.getChemicalElement(elementSymbol);
	        if (chemicalElement == null) {
	        	throw new RuntimeException("Unknown element: "+atomString);
	        }
    	}
        return atomString;
    }


//atom : '[' <mass> symbol <chiral> <hcount> <sign<charge>> ']'
//Note that chiral, hcount and sign/charge can appear in any order technically

    private CMLAtom addExtendedAtom(final String s, final char slashChar, int atomStartChar, String atomString) {
// create atom with dummy elementType
        final CMLAtom atom = addAtom(DU, slashChar, rawSmiles, atomStartChar, atomString);
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
        String ss = s.substring(i, Math.min(s.length(), i+2));
        final String elementType = grabAtom(ss);
        setElementType(atom, elementType);
        i += elementType.length();

        
        int hydrogenCount =0;
        int charge =0;
        String chiral ="";
        while (i < l){
// chirality
            if (s.substring(i).startsWith(S_ATAT)) {
                atom.setAttribute(CHIRAL, S_ATAT);
                chiral=S_ATAT;
                i=i+2;
            } else if (s.substring(i).startsWith(S_AT)) {
            	if(Pattern.matches(S_AT +"[A-Z][A-Z]\\d\\S*", s.substring(i))){
            		i=i+4;
            		LOG.trace("Currently unsupported chiral specification");
            	}
            	else{
	                atom.setAttribute(CHIRAL, S_AT);
	                chiral=S_AT;
	                i++;
            	}
            }
            else if (s.charAt(i) == 'H') {
// hydrogenCount
                i++;
                if (i < l && Character.isDigit(s.charAt(i))) {
                    int startOfCountInString=i;
            		while (i < l) {
            			if (!Character.isDigit(s.charAt(i))) {
                			break;
            			}
            			i++;
    	            }
                	hydrogenCount= Integer.parseInt(s.substring(startOfCountInString, i));
                } else {
                	hydrogenCount =1;
                }
            }
            else if (s.charAt(i) == C_PLUS){ 
// formalCharge
        		charge++;
        		i++;
        		while (i < l) {
        			char sign = s.charAt(i);
        			if (sign == C_PLUS) {
            			charge++;
            			i++;
        			}
        			else if (Character.isDigit(s.charAt(i))) {
    		            charge = s.charAt(i) - C_ZERO;
    		            i++;
    		            break;
        			}
        			else{
        				break;
        			}
        		}
        	}
        	else if (s.charAt(i) == C_MINUS) {
        		charge--;
        		i++;
        		while (i < l) {
        			char sign = s.charAt(i);
        			if (sign == C_MINUS) {
            			charge--;
            			i++;
        			}
        			else if (Character.isDigit(s.charAt(i))) {
    		            charge = s.charAt(i) - C_ZERO;
    		            charge *=-1;
    		            i++;
    		            break;
        			}
        			else{
        				break;
        			}
	            }
            }
            else{
            	throw new RuntimeException("Invalid symbol found in atom description, found: "+s.charAt(i)+" in "+smilescopy );
            }
        }
        //throw new RuntimeException("Sign must be of form - or --.. or -n or + or ++... or +n (found "+sign+") in "+scopy );
        
        atom.setFormalCharge(charge);
        atom.setHydrogenCount(hydrogenCount);
        
// CML parity tag added if applicable       
        if (chiral !=""){
	        CMLAtomParity atomParity =new CMLAtomParity();
	        String[] atomRefs4 =new String[]{"","","",""};
	        
	        if (lastAtom !=null){
	        	atomRefs4[0]=lastAtom.getId();
	        	if (hydrogenCount >=1){
	        		atomRefs4[1]=atom.getId() + "_h1";
	        	}
	        }
	        else{
	        	if (hydrogenCount >=1){
	        		atomRefs4[0]=atom.getId() + "_h1";
	        	}
	        }
        	
        	atomParity.setAtomRefs4(atomRefs4);
	        if (chiral.equals(S_ATAT)){
	        	atomParity.setXMLContent("1");
	        }
	        else if (chiral.equals(S_AT)) {
	        	atomParity.setXMLContent("-1");
	        }
        	atom.appendChild(atomParity);
        }
        
        return atom;
    }

    private void setElementType(final CMLAtom atom, String elementType) {
    	if (elementType.length() < 1 || elementType.length() > 2) {
            throw new RuntimeException("Element of wrong length :"+elementType+":");
        } else if (elementType.length() == 1) {
            if (Character.isLowerCase(elementType.charAt(0))) {
                atom.setAttribute(AROMATIC, TRUE);
                elementType = elementType.toUpperCase();
            }
	    } else if (elementType.length() == 2) {
	        if (Character.isLowerCase(elementType.charAt(0))) {
	            atom.setAttribute(AROMATIC, TRUE);
	            elementType = elementType.substring(0,1).toUpperCase() + elementType.charAt(1) ;
	        }
	    } 
        else {
            if (!Character.isUpperCase(elementType.charAt(0)) &&
                Character.isLowerCase(elementType.charAt(1))) {
                throw new RuntimeException("Bad element :"+elementType);
            }
        }
        atom.setElementType(elementType);
    }

    @SuppressWarnings("unused")
    private CMLAtom addAtom(String elementType, final char slashChar, String rawSmiles, int atomStartChar, String atomString) {
        final CMLAtom atom = new CMLAtom("a"+(++natoms));
        molecule.addAtom(atom);
        setElementType(atom, elementType);
        if (slashChar != C_NONE) {
            atom.setAttribute(SLASH, ""+slashChar);
        }
    	atomIdList.add(atomStartChar, atom.getId());
    	atomChunkList.add(atomStartChar, atomString);
        if (currentAtom != null) {
            CMLBond bond = addBond(currentAtom, atom, bondChar, rawSmiles);
        }
        currentAtom = atom;
        return atom;
    }

    
    private CMLBond addBond(final CMLAtom currentAtom, 
    		final CMLAtom atom, char bondChar, String rawSmiles) {
        final CMLBond bond = new CMLBond(currentAtom, atom);

        if (currentAtom.getAttributeValue(CHIRAL)!= null){
        	CMLElements<CMLAtomParity> atomParity = currentAtom.getAtomParityElements();
        	for (CMLAtomParity atomParityTag : atomParity) {
        		String[] atomRefs4 =atomParityTag.getAtomRefs4();
        		
        		for (int i = 0; i < atomRefs4 .length; i++) {
        			if (atomRefs4[i]==""){
        				atomRefs4[i]=atom.getId();
        				break;
        			}
				}

        		atomParityTag.setAtomRefs4(atomRefs4);
			} 
        }
        if (atom.getAttributeValue(CHIRAL)!= null){
        	CMLElements<CMLAtomParity> atomParity =atom.getAtomParityElements();
        	for (CMLAtomParity atomParityTag : atomParity) {
        		String[] atomRefs4 =atomParityTag.getAtomRefs4();
        		
        		for (int i = 0; i < atomRefs4 .length; i++) {
        			if (atomRefs4[i]==""){
        				atomRefs4[i]=currentAtom.getId();
        				break;
        			}
				}

        		atomParityTag.setAtomRefs4(atomRefs4);
			} 
        }
        
        molecule.addBond(bond);
        if (bondChar == C_NONE) {
            bondChar = C_SINGLE;
        }
        if (bondChar == C_SINGLE) {
            bond.setOrder(CMLBond.SINGLE_S);
        } else if (bondChar == C_DOUBLE) {
            bond.setOrder(CMLBond.DOUBLE_D);
        } else if (bondChar == C_TRIPLE) {
            bond.setOrder(CMLBond.TRIPLE_T);
        } else if (bondChar == C_AROMATIC) {
            bond.setOrder(CMLBond.AROMATIC);
        } else {
            throw new RuntimeException("Unknown currentBond type :"+bondChar+":");
        }
        return bond;
    }

    /** crude writer
     * @return string
     */
    public String write() {
    	SMILESWriter sWriter = new SMILESWriter(molecule);
    	return sWriter.getString();
    }


    
    /**
     * normalizes ring numbers in SMILES to be as low as possible
     * crude. assumes less than 9 rings open at any time
     * neglect isotopes
     * @param s
     * @return SMILES
     */
    public static String normalizeRings(String s) {
    	int start[] = new int[10];
    	int end[] = new int[10];
    	boolean inring[] = new boolean[10];
    	for (int i = 0; i < 10; i++) {
    		start[i] = -1;
    		end[i] = -1;
    		inring[i] = false;
    	}
    	StringBuilder sb = new StringBuilder();
    	int i = 0;
    	while (i < s.length()) {
    		char c = s.charAt(i);
    		if (c == C_LSQUARE) {
    			int idx = Util.indexOfBalancedBracket(C_LSQUARE, s.substring(i));
    			if (idx == -1) {
    				throw new RuntimeException("No balanced []");
    			}
    			i += idx;
    			sb.append(s.subSequence(i, i+idx));
    		} else if (Character.isDigit(c)) {
    			int currentRingNumber = c - '0';
    			sb.append(c);
    			if (inring[currentRingNumber]) {
    				inring[currentRingNumber] = false;
    				end[currentRingNumber] = i;
					if (start[currentRingNumber] < 0) {
						throw new RuntimeException("start not set for "+currentRingNumber);
					}
    				int lowestFreeRing = 0;
    				for (int ring = 1; ring < currentRingNumber; ring++) {
    					if (
							(end[ring] < 0 && start[ring] < 0) ||
							(start[ring] > 0 && end[ring] > 0 && end[ring] < start[currentRingNumber])) {
    						lowestFreeRing = ring;
    						if (lowestFreeRing < currentRingNumber) {
	    						start[ring] = start[currentRingNumber];
	    						end[ring] = end[currentRingNumber];
	    						sb.setCharAt(start[ring], (char) ('0'+ring));
	    						sb.setCharAt(end[ring], (char) ('0'+ring));
	    						start[currentRingNumber] = -1;
	    						end[currentRingNumber] = -1;
    						}
    						break;
    					}
    				}
    			} else {
    				inring[currentRingNumber] = true;
    				start[currentRingNumber] = i;
    			}
    			i++;
    		} else {
    			sb.append(c);
    			i++;
    		}
    	}
    	String ss = sb.toString();
    	return ss;
    }
    
	/**
	 * @return the molecule
	 */
	public CMLMolecule getMolecule() {
		if (molecule != null) {
			molecule.setNormalizedBondOrders();
		}
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

	/** convenience
	 * 
	 * @param smilesString
	 * @return
	 */
	public static CMLMolecule createMolecule(String smilesString) {
		CMLMolecule molecule = null;
		if (smilesString != null && smilesString.trim().length() != 0) {
			SMILESTool smilesTool = new SMILESTool();
			smilesTool.parseSMILES(smilesString);
			molecule = smilesTool.getMolecule();
		}
		return molecule;
	}

}	
