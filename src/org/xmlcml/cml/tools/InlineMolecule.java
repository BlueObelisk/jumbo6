package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLZMatrix;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement;

/** create molecule from inline representation of the atoms and bonds.
 * 
 * simple grammar to represent atoms, connecting bonds and qualifiers
 * for both either. Allows branches but not rings.
 * Syntax:
 * <pre>
 * (atomBond) * atom
 * </pre>
 * where (not yet finished):
 * <pre>
 *   atom := [A-Z][a-z]? // must be valid PT element
 *   atomQualifier := '[' id? chirality? ']'// id must be unique
 *   bond := ['-' | '=' | '#']? // 
 *   bondQualifier := '[' len? tor? ']'// must be unique
 *   branch := '(' branch* tree? ')'
 *   atomBond = (atom atomQualifier? branch* bond bondQualifier?)* atom
 *   </pre>
 * @author pm286
 *
 */
public class InlineMolecule implements CMLConstants {

    /** error messages.*/
    public enum Error {
        /** bad bond*/
        BAD_BOND("Bad bond:"),
        /** bad state*/
        BAD_STATE("Bad state:"),
        /** bad qualifier*/
        BAD_QUALIFIER("Bad qualifier:"),
        /** empty qualifier*/
        EMPTY_QUALIFIER("Empty qualifier:"),      
        /** bad symbol*/
        BAD_SYMBOL("Bad atom symbol:"),
        ;
        String s;
        private Error(String s) {
            this.s = s;
        }
    }

    /** states of parse.*/
    public enum State {
        /** start*/
        START,
        /** finished atom.*/
        ATOM,
        /** finished bond.*/
        BOND,
        /** started qualifier.*/
        QUALIFIER,
        /** started branch.*/
        BRANCH
    }
    
    CMLMolecule cmlMolecule;
    String formula;
    State state;
    InlineAtom rootAtom;
    int serial = 0;
    
    /** create from string.
     * 
     * @param formula 
     */
    public InlineMolecule(String formula) {
        this.formula = formula;
        rootAtom = null;
        createFromString(formula);
        cmlMolecule.debug();
        System.out.println("===========xxxxxxxxxxxxxx================");
        makeMolecule();
        cmlMolecule.debug();
    }
    
    /** create new molecule.
     * 
     * @param formula
     */
    public void createFromString(String formula) {
        
        cmlMolecule = new CMLMolecule();
        state = State.START;
        int i = 0;
        serial = 0;
        InlineAtom currentAtom = null;
        InlineBond currentBond = null;
        while (i < formula.length()) {
            if (state == State.START || state == State.BOND) {
                InlineAtom inlineAtom = InlineAtom.grab(formula.substring(i), this);
                inlineAtom.cmlAtom.setId("a"+(++serial));
                cmlMolecule.addAtom(inlineAtom.cmlAtom);
                if (inlineAtom == null) {
                    throw new CMLRuntimeException("NULL atom");
                }
                if (state == State.START) {
                    rootAtom = inlineAtom;
                }
                if (currentBond != null) {
                    System.out.println("III..."+inlineAtom);
                    inlineAtom.addBond(currentAtom, currentBond);
                    currentBond = null;
                }
                currentAtom = inlineAtom;
                i += inlineAtom.getLength();
                state = State.ATOM;
//                boolean checkDuplicates = true;
            } else if (state == State.ATOM) {
                if (formula.substring(i).charAt(0) == InlineBranch.START) {
                    InlineBranch branch = InlineBranch.grab(formula.substring(i), currentAtom, this, serial);
                    i += branch.getLength(formula.substring(i));
                    serial = branch.serial;
                } else {
                    currentBond = InlineBond.grab(formula.substring(i));
                    currentBond.debug();
                    if (currentBond == null) {
                        throw new CMLRuntimeException("NULL bond");
                    }
                    i += currentBond.getLength();
                    state = State.BOND;
                }
            } else {
                throw new CMLRuntimeException(InlineMolecule.Error.BAD_STATE+S_COLON+state+S_COLON);
            }
        }
    }

    void makeMolecule() {
        try {
            CMLZMatrix zMatrix = new CMLZMatrix(cmlMolecule);
            zMatrix.addCartesiansTo(cmlMolecule);
        } catch (CMLRuntimeException e) {
            System.out.println("WARN of ZMAT "+e);
        }
    }
    
    /** get the created molecule.
     * 
     * @return molecule (null if none)
     */
    public CMLMolecule getCmlMolecule() {
        return cmlMolecule;
    }
    
    /** debug.
     */
    public void debug() {
        System.out.println("XXXXXXXX>>>>>>>>");
        cmlMolecule.debug();
        System.out.println("<<<<<<<<XXXXXXXXX");
        rootAtom.debug();
    }
}
class InlineAtom {
    
    private InlineAtom greatGrandParent = null;
    private InlineAtom grandParent = null;
    private InlineAtom parent = null;
    List<InlineAtom> childAtoms = null;
    List<InlineBond> childBonds = null;
    CMLAtom cmlAtom;
    InlineMolecule molecule;
    
    /** components of qualifier.*/
    public enum Qual {
        /** chirality */
        CHIRALITY("c"),
        /** id */
        ID("id"),
        ;
        String value;
        private Qual(String v) {
            value = v;
        }
    }
    
    ChemicalElement chemicalElement;
    Qualifier qual;
    double chirality = Double.NaN;
    String id;
    
    /** constructor.
     * creates new CMLAtom as member.
     * @param molecule 
     */
    public InlineAtom(InlineMolecule molecule) {
        cmlAtom = new CMLAtom();
        childAtoms = new ArrayList<InlineAtom>();
        childBonds = new ArrayList<InlineBond>();
        this.molecule = molecule;
    }
    
    void addBond(InlineAtom atom, InlineBond bond) {
        if (atom != null) {
            this.parent = atom;
            atom.childAtoms.add(this);
            atom.childBonds.add(bond);
            CMLBond cmlBond = new CMLBond(parent.cmlAtom, this.cmlAtom);
            molecule.cmlMolecule.addBond(cmlBond);
            String parentId = parent.cmlAtom.getId();
            String atomId = cmlAtom.getId();
            CMLLength length = new CMLLength();
            length.setAtomRefs2(new String[]{parentId, atomId});
            molecule.cmlMolecule.appendChild(length);
            length.setXMLContent(bond.length);
            
            grandParent = (parent == null) ? null : parent.parent;
            String grandParentId = (grandParent == null) ? null :
                grandParent.cmlAtom.getId();
            if (grandParent != null) {
                CMLAngle angle = new CMLAngle();
                angle.setAtomRefs3(new String[]{grandParentId, parentId, atomId});
                molecule.cmlMolecule.appendChild(angle);
                angle.setXMLContent(bond.angle);
            }
            
            greatGrandParent = (grandParent == null) ? null : grandParent.parent;
            String greatGrandParentId = (greatGrandParent == null) ? null :
                greatGrandParent.cmlAtom.getId();
            if (greatGrandParent != null) {
                CMLTorsion torsion = new CMLTorsion();
                torsion.setAtomRefs4(new String[]{
                    greatGrandParentId, grandParentId, parentId, atomId});
                molecule.cmlMolecule.appendChild(torsion);
                torsion.setXMLContent(bond.torsion);
            }
        }
    }
    
    /** process token and return Atom;
     * 
     * @param s string to process
     * @return Atom (null if end of string)
     */
    static InlineAtom grab(String s, InlineMolecule molecule) {
        InlineAtom inlineAtom = new InlineAtom(molecule);
        inlineAtom.chemicalElement = ChemicalElement.grabChemicalElement(s);
        if (inlineAtom.chemicalElement == null) {
            throw new CMLRuntimeException(InlineMolecule.Error.BAD_SYMBOL+CMLUtil.S_COLON+
                    s+CMLUtil.S_COLON);
        }
        inlineAtom.cmlAtom.setElementType(inlineAtom.chemicalElement.getSymbol());
        int ll = inlineAtom.chemicalElement.getSymbol().length();
        inlineAtom.qual = new Qualifier(s.substring(ll));
        if (inlineAtom.qual != null) {
            inlineAtom.process();
        }
//        System.out.println("ATOM "+inlineAtom);
        return inlineAtom;
    }

    void process() {
        int i = 0;
        String qq = qual.q;
        while (i < qq.length()) {
            String qqq = qq.substring(i);
            if (qqq.startsWith(Qual.CHIRALITY.value+CMLUtil.S_LBRAK)) {
                int is = (Qual.CHIRALITY.value+CMLUtil.S_LBRAK).length();
                int idx = qqq.indexOf(CMLUtil.S_RBRAK);
                if (idx == -1) {
                    throw new CMLRuntimeException("Bad arg for chirality:"+qqq);
                }
                try {
                    chirality = new Double(qqq.substring(is, idx)).doubleValue();
                } catch (NumberFormatException nfe) {
                    throw new CMLRuntimeException("Bad value for chirality: "+qqq);
                }
                i += idx+1;
            } else if (qqq.startsWith(Qual.ID.value+CMLUtil.S_LBRAK)) {
                int is = (Qual.ID.value+CMLUtil.S_LBRAK).length();
                int idx = qqq.indexOf(CMLUtil.S_RBRAK);
                if (idx == -1) {
                    throw new CMLRuntimeException("Bad arg for id:"+qqq);
                }
                id = qqq.substring(is, idx);
                i += idx+1;

            } else {
                throw new CMLRuntimeException("bad qual: "+qqq+"/"+i);
            }
        }
        System.out.println("AQ:"+this.fullString());
    }

    @SuppressWarnings("unused")
    void processAtoms() {
        for (InlineAtom childAtom : childAtoms) {
            ;//
        }
    }
    
    int getLength() {
        int i = 0;
        if (chemicalElement != null) {
            i = chemicalElement.getSymbol().length();
        }
        i += qual.getLength();
        return i;
    }
    
    /** get full string.
     * @return the string with fuller interpretation
     */
    public String fullString() {
        return chemicalElement.getSymbol()+
        "{chirality="+chirality+",id="+id+"}";
    }

    /** get string.
     * atom symbol followed by elementNumber
     * @return lexical string followed by interpretation
     */
    public String toString() {
        String ss = chemicalElement.getSymbol();
        ss += CMLUtil.S_LBRAK+chemicalElement.getAtomicNumber()+CMLUtil.S_RBRAK;
        return ss;
    }
    
    void debug() {
        System.out.println("ATOM: "+chemicalElement.getSymbol());
        for (int i = 0; i < childAtoms.size(); i++) {
            if (i > 0) {
                System.out.println("(");
            }
            childBonds.get(i).debug();
            childAtoms.get(i).debug();
            if (i > 0) {
                System.out.println(")");
            }
        }
    }
}

class InlineBond implements CMLConstants {

    /** components of qualifier.*/
    public enum Qual {
        /** bond length */
        LENGTH("l"),
        /** bond angle */
        ANGLE("a"),
        /** torsion */
        TORSION("t"),
        ;
        String value;
        private Qual(String v) {
            value = v;
        }
    }
    String s;
    CMLBond bond;
    String order;
    Qualifier qual;
    double length = Double.NaN;
    double angle = Double.NaN;
    double torsion = Double.NaN;

    /** constructor.
     */
    public InlineBond() {
    }
    
    static InlineBond grab(String s) {
        s = s.trim();
        InlineBond bond = null;
        if (s.length() > 0) {
            bond = new InlineBond();
            bond.order = getOrder(s.charAt(0));
            if (bond.order == null) {
                throw new CMLRuntimeException(InlineMolecule.Error.BAD_BOND+s+CMLUtil.S_COLON);
            }
            // qualifier
            if (s.length() > 1 && Qualifier.START == s.charAt(1)) {
                bond.qual = new Qualifier(s.substring(1));
                bond.process();
            } else {
                bond.qual = null;
            }
            bond.s = s.substring(0, 
                ((bond.qual == null) ? 1 : 1 + bond.qual.getLength()));
        }
//        System.out.println("BOND "+bond);
        return bond;
    }
    
    static String getOrder(char c) {
        String order = null;
        if (c == CMLUtil.C_MINUS) {
            order = CMLBond.SINGLE;
        } else if (c == CMLUtil.C_EQUALS) {
            order = CMLBond.DOUBLE;
        } else if (c == CMLUtil.C_HASH) {
            order = CMLBond.TRIPLE;
        }
        return order;
    }
    
    void process() {
        int i = 0;
        String qq = qual.q;
        while (i < qq.length()) {
            String qqq = qq.substring(i);
            if (qqq.startsWith(Qual.LENGTH.value+CMLUtil.S_LBRAK)) {
                int is = (Qual.LENGTH.value+CMLUtil.S_LBRAK).length();
                int idx = qqq.indexOf(CMLUtil.S_RBRAK);
                if (idx == -1) {
                    throw new CMLRuntimeException("Bad arg for length:"+qqq);
                }
                try {
                    length = new Double(qqq.substring(is, idx)).doubleValue();
                } catch (NumberFormatException nfe) {
                    throw new CMLRuntimeException("Bad value for length: "+qqq);
                }
                i += idx+1;
            } else if (qqq.startsWith(Qual.ANGLE.value+CMLUtil.S_LBRAK)) {
                int is = (Qual.ANGLE.value+CMLUtil.S_LBRAK).length();
                int idx = qqq.indexOf(CMLUtil.S_RBRAK);
                if (idx == -1) {
                    throw new CMLRuntimeException("Bad arg for angle:"+qqq);
                }
                try {
                angle = new Double(qqq.substring(is, idx)).doubleValue();
                } catch (NumberFormatException nfe) {
                    throw new CMLRuntimeException("Bad value for angle: "+qqq);
                }
                i += idx+1;
            } else if (qqq.startsWith(Qual.TORSION.value+CMLUtil.S_LBRAK)) {
                int is = (Qual.TORSION.value+CMLUtil.S_LBRAK).length();
                int idx = qqq.indexOf(CMLUtil.S_RBRAK);
                if (idx == -1) {
                    throw new CMLRuntimeException("Bad arg for torsion:"+qqq);
                }
                try {
                torsion = new Double(qqq.substring(is, idx)).doubleValue();
                } catch (NumberFormatException nfe) {
                    throw new CMLRuntimeException("Bad value for torsion: "+qqq);
                }
                i += idx+1;
            } else if (qqq.startsWith(S_COMMA)) {
                i += 1;
            } else {
                throw new CMLRuntimeException("bad qual: "+qqq+"/"+i);
            }
        }
        System.out.println("BQ"+this.fullString());
    }
    
    void createBond(String s) {
        if (s.length() != 1) {
            throw new CMLRuntimeException("Bond must only be single character: "+s);
        }
        bond = new CMLBond();
        bond.setOrder(InlineBond.getOrder(s.charAt(0)));
    }
    
    int getLength() {
        return 1 + ((qual == null) ? 0 : qual.getLength());
    }

    /** get full string.
     * @return the string with fuller interpretation
     */
    public String fullString() {
        return s+" {order="+order+",length="+length+",torsion="+torsion+"}";
    }

    void debug() {
        System.out.println("BOND: "+order+"/"+length+"/"+angle+"/"+torsion);
    }
    /** get string.
     * @return exact lexical
     */
    public String toString() {
        return s;
    }
}

class Qualifier {
    
    /** qualifier string without [ and ]
     */
    String q;
    /** start of qualifier */
    public static char START = CMLUtil.C_LSQUARE;
    /** end of qualifier */
    public static char END = CMLUtil.C_RSQUARE;

    /** create qualifier.
     * @param s must be of form [...]
     */
    public Qualifier(String s) {
        q = CMLUtil.S_EMPTY;
        if (s != null && s.length() > 0 && s.charAt(0) == START) {
            int idx = (s.indexOf(END));
            if (idx == -1) {
                throw new CMLRuntimeException(InlineMolecule.Error.BAD_QUALIFIER+s+CMLUtil.S_COLON);
            }
            if (idx == 1) {
            	throw new CMLRuntimeException(InlineMolecule.Error.EMPTY_QUALIFIER+s+CMLUtil.S_COLON);
            }
            q = s.substring(1, idx);
        }
    }
    
    /** gets length including delimiters.
     * @return length (-1 if not a qualifier)
     */
    int getLength() {
        return (CMLUtil.S_EMPTY.equals(q)) ? 0 : q.length()+2; 
    }

    /** get string.
     * @return empty or qualifier surrounded by [...]
     */
    public String toString() {
        return CMLUtil.S_EMPTY.equals(q) ? CMLUtil.S_EMPTY : 
            new StringBuilder(10).append(START).append(q).append(END).toString();
    }
}

class InlineBranch {
    String b = null;
    InlineMolecule molecule;
    int serial;
    static char START = CMLUtil.C_LBRAK;
    static char END = CMLUtil.C_RBRAK;

    /** constructor.
     * @param s string to parse
     * @param molecule
     * @param serial
     */
    public InlineBranch(String s, InlineMolecule molecule, int serial) {
    	this.molecule = molecule;
    	this.serial = serial;
    }

    static InlineBranch grab(String s, InlineAtom currentAtom, InlineMolecule molecule, int serial) {
    	
    	InlineMolecule.State branchState;
    	branchState = InlineMolecule.State.START;
    	InlineBond branchBond = null;
    	int j = 1;
    	
        int idx = Util.indexOfBalancedBracket(START,s); //originally there
        InlineBranch branch = new InlineBranch(s.substring(1, idx), molecule, serial); 
        
        while ( j < idx ){
        	if (branchState == InlineMolecule.State.START || branchState == InlineMolecule.State.ATOM){
                if (s.substring(j).charAt(0) == InlineBranch.START) {
                    InlineBranch branchbranch = grab(s.substring(j), currentAtom, molecule, serial);
                    j += branchbranch.getLength(s.substring(j));
                    serial = branchbranch.serial;
                } else {      		
                	branchBond = InlineBond.grab(s.substring(j));
                	branchBond.debug();
                	if (branchBond == null){
                		throw new CMLRuntimeException("NULL bond");
                	}
               		j += branchBond.getLength();
            		branchState = InlineMolecule.State.BOND;
                }
        	}else if ( branchState == InlineMolecule.State.BOND ){
                InlineAtom inlineAtom = InlineAtom.grab(s.substring(j), molecule);
                inlineAtom.cmlAtom.setId("a"+(++serial)); 
                molecule.cmlMolecule.addAtom(inlineAtom.cmlAtom);
                if (inlineAtom == null) {
                    throw new CMLRuntimeException("NULL atom");
                }
                if (branchBond != null) {
                    System.out.println("III..."+inlineAtom);
                    inlineAtom.addBond(currentAtom, branchBond);
                    branchBond = null;
                }
                currentAtom = inlineAtom;
                j += inlineAtom.getLength();
                branchState = InlineMolecule.State.ATOM;       		
        	}
        }
        branch.serial = serial;
        return branch; 
    }
    
    /** get length of string to next balanced ) 
     * 
     * @param s the string
     * @return length of string to next bracket including () else 0 if not found
     */
    int getLength(String s) {
        int i = 1 + Util.indexOfBalancedBracket(START,s);
        return i;
    }
}