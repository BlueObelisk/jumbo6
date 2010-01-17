package org.xmlcml.cml.tools;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.ReactionComponent;
import org.xmlcml.cml.element.CMLFormula.Sort;
import org.xmlcml.cml.element.CMLMap.Direction;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGCircle;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGGBox;
import org.xmlcml.cml.graphics.SVGLayout;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGRect;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.cml.tools.ReactionDisplay.Orientation;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Interval;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;

/**
 * too to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public class ReactionTool extends AbstractSVGTool {

    private static final String OMIT_REACTANTS = null;
	public static final String REACTANT_VERTICAL = null;

	Logger LOG = Logger.getLogger(ReactionTool.class);

	public static String DRAW = "draw";
	public static String MASS = "mass";
	public static String STRIP_HYD = "strip_hydrogen";
	
    private CMLReaction reaction = null;
	private ReactionDisplay reactionDisplay = new ReactionDisplay();
	private static int patternCount = 0;
	private static Element defs = null;
	private static String PATTERN = "pattern";
	

	private CMLFormula aggregateReactantFormula;
	private CMLFormula aggregateProductFormula;
	private CMLFormula differenceFormula;
    private List<String> electronIdList;
    
	/**
     * constructor.
     * 
     * @param reaction
     * @deprecated use getOrCreateTool()
     */
    public ReactionTool(CMLReaction reaction) {
    	if (reaction == null) {
    		throw new RuntimeException("null reaction");
    	}
        this.reaction = reaction;
		this.reaction.setTool(this);
    }

	/** gets ReactionTool associated with reaction.
	 * if null creates one and sets it in reaction
	 * @param reaction
	 * @return tool
	 */
	public static ReactionTool getOrCreateTool(CMLReaction reaction) {
		ReactionTool reactionTool = null;
		if (reaction != null) {
			reactionTool = (ReactionTool) reaction.getTool();
			if (reactionTool == null) {
				reactionTool = new ReactionTool(reaction);
				reaction.setTool(reactionTool);
			}
		}
		return reactionTool;
	}

	/**
	 * 
	 * @param reaction
	 * @return
	 */
	public static AbstractSVGTool getOrCreateSVGTool(CMLReaction reaction) {
		return (AbstractSVGTool) ReactionTool.getOrCreateTool(reaction);
	}

    /**
     * output and analyse aggregate formula for products and reactants.
     * 
     * @param w the output writer
     * @throws RuntimeException
     * @throws IOException
     */
    public void outputBalance(Writer w) throws IOException {
        calculateDifferenceFormula();
        if (aggregateReactantFormula != null) {
            w.write(aggregateReactantFormula.getFormattedString());
        } else {
            w.write("Null reactantList");
        }
        w.write(" = ");
        if (aggregateProductFormula != null) {
            w.write(aggregateProductFormula.getFormattedString());
        } else {
            w.write("Null productList");
        }
		if (differenceFormula != null) {
            w.write(" ; difference: ");
            differenceFormula.setAllowNegativeCounts(true);
            w.write(differenceFormula.getFormattedString(CMLFormula.Type.ELEMENT_WHITESPACE_COUNT,
    				Sort.CHFIRST, false));
        }
    }

	/**
	 * @return difference formula
	 */
	public CMLFormula calculateDifferenceFormula() {
		differenceFormula = null;
		aggregateReactantFormula = createAggregateReactantFormula();
		aggregateProductFormula = createAggregateProductFormula();
		if (aggregateReactantFormula != null && aggregateProductFormula != null) {
            differenceFormula = aggregateReactantFormula.getDifference(aggregateProductFormula);
        }
		return differenceFormula;
	}

	/**
	 * @return formula
	 */
	public CMLFormula createAggregateProductFormula() {
		List<CMLProduct> products = reaction.getDescendantProducts();
        CMLFormula aggregateProductFormula = null;
        for (CMLProduct product : products) {
            CMLFormula formula = product.getOrCreateFormula();
            if (formula != null) {
            	double thisCount = product.getCount();
            	if (!Double.isNaN(thisCount)) {
            		formula.setCount(thisCount);
            	}
                if (aggregateProductFormula == null) {
                    aggregateProductFormula = new CMLFormula();
                }
                aggregateProductFormula = aggregateProductFormula
                        .createAggregatedFormula(formula);
            }
        }
		return aggregateProductFormula;
	}

	/**
	 * @return aggregate reaction formula
	 */
	public CMLFormula createAggregateReactantFormula() {
		List<CMLReactant> reactants = reaction.getDescendantReactants();
        CMLFormula aggregateReactantFormula = null;
        for (CMLReactant reactant : reactants) {
            CMLFormula formula = reactant.getOrCreateFormula();
            if (formula != null) {
            	double thisCount = reactant.getCount();
            	if (!Double.isNaN(thisCount)) {
            		formula.setCount(thisCount);
            	}
                if (aggregateReactantFormula == null) {
                    aggregateReactantFormula = new CMLFormula();
                }
                aggregateReactantFormula = aggregateReactantFormula
                        .createAggregatedFormula(formula);
            }
        }
		return aggregateReactantFormula;
	}

    /**
     * output simple inline version of reaction.
     * 
     * of form "C2H4 + H2O = C2H6O" without newline
     * 
     * @param w
     *            the output writer
     * @throws RuntimeException
     * @throws IOException
     */
    public void outputReaction(Writer w) throws IOException {
        // get reactants (null if no formulae)
        List<CMLReactant> reactants = reaction.getDescendantReactants();
        int i = 0;
        for (CMLReactant reactant : reactants) {
            CMLFormula formula = reactant.getOrCreateFormula();
            if (i > 0) {
                w.write(" + ");
            }
            if (formula != null) {
                w.write(formula.getFormattedString());
            } else {
                w.write("NULL");
            }
            i++;
        }

        // get products (null if no formulae)
        List<CMLProduct> products = reaction.getDescendantProducts();
        i = 0;
        for (CMLProduct product : products) {
            CMLFormula formula = product.getOrCreateFormula();
            if (i > 0) {
                w.write(" + ");
            } else {
                w.write(" = ");
            }
            if (formula != null) {
                w.write(formula.getFormattedString());
            } else {
                w.write("NULL");
            }
            i++;
        }

    }

    /**
     * gets formula for product or reactant.
     * 
     * If product has a child formula, returns it, else returns the formula on
     * molecules, else null. resulting CMLFormula is not aggregated
     * @param element
     * @return the product stoichiometry
     */
     public static CMLFormula getFormula(Element element) {
         CMLFormula formula = null;
         if (element instanceof CMLReactant) {
             formula = (CMLFormula) ((CMLReactant)element).getFirstCMLChild(CMLFormula.TAG);
         } else if (element instanceof CMLProduct) {
             formula = (CMLFormula) ((CMLProduct)element).getFirstCMLChild(CMLFormula.TAG);
         } else {
        	 throw new ClassCastException("must use CMLReactant ot CMLProduct");
         }
         if (formula == null) {
             CMLMolecule molecule = null;
             if (element instanceof CMLReactant) { 
                 molecule = (CMLMolecule) ((CMLReactant)element).getFirstCMLChild(CMLMolecule.TAG);
             } else if (element instanceof CMLProduct) {
                 molecule = (CMLMolecule) ((CMLProduct)element).getFirstCMLChild(CMLMolecule.TAG);
             }
             if (molecule != null) {
            	 MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
                 formula = (CMLFormula) molecule.getFirstCMLChild(CMLFormula.TAG);
                 if (formula == null) {
                     formula = moleculeTool.calculateFormula(CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS);
                 } else {
                     formula = new CMLFormula(molecule);
                 }
             }
         }
         return formula;
     }

    /**
     * gets aggregate formula for product or reactant.
     * 
     * @param prodReact
     * @return the formula (null if no child formulas)
     */
    public static CMLFormula getAggregateFormula(ReactionComponent prodReact) {
        if (prodReact == null) {
            throw new RuntimeException("null prodReact");
        }
        List<CMLFormula> formulaList = prodReact.getFormulas();
        CMLFormula aggregateFormula = null;
        if (formulaList.size() > 0) {
            aggregateFormula = new CMLFormula();
            for (CMLFormula formula : formulaList) {
                aggregateFormula.createAggregatedFormula(formula);
            }
        }
        return aggregateFormula;
    }

    /**
     * gets all descendant molecules.
     * 
     * @param reactionComponent REACTANTLIST or PRODUCTLIST
     * @return the descendant molecules or empty list
     */
    public List<CMLMolecule> getMolecules(Component reactionComponent) {
    	Element reactantProductList = null; 
    	if (Component.REACTANTLIST.equals(reactionComponent) || Component.REACTANT.equals(reactionComponent)) {
    		reactantProductList = reaction.getReactantList();
    	} else if (Component.PRODUCTLIST.equals(reactionComponent) || Component.PRODUCT.equals(reactionComponent)) {
    		reactantProductList = reaction.getProductList();
    	} else {
    		throw new RuntimeException("Bad ReactionComponent type: "+reactionComponent);
    	}
        List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
        if (reactantProductList != null) {
	        Nodes moleculeNodes = reactantProductList.query(".//cml:molecule", CMLConstants.CML_XPATH);
	        for (int i = 0; i < moleculeNodes.size(); i++) {
	        	moleculeList.add((CMLMolecule) moleculeNodes.get(i));
	        }
        }
        return moleculeList;
    }


    /**
     * gets all descendant atoms.
     * 
     * @param type REACTANTLIST or PRODUCTLIST
     * @return the descendant atoms.
     */
    public List<CMLAtom> getAtoms(Component type) {
        List<CMLAtom> atomList = new ArrayList<CMLAtom>();
        List<CMLMolecule> molecules = getMolecules(type);
        for (CMLMolecule molecule : molecules) {
            List<CMLAtom> atoms = molecule.getAtoms();
            for (CMLAtom atom : atoms) {
                atomList.add(atom);
            }
        }
        return atomList;
    }

    /**
     * gets all descendant bonds.
     * 
     * @param type REACTANTLIST or PRODUCTLIST
     * @return the descendant bonds.
     */
    public List<CMLBond> getBonds(Component type) {
        List<CMLBond> bondList = new ArrayList<CMLBond>();
        List<CMLMolecule> molecules = getMolecules(type);
        for (CMLMolecule molecule : molecules) {
            List<CMLBond> bonds = molecule.getBonds();
            for (CMLBond bond : bonds) {
                bondList.add(bond);
            }
        }
        return bondList;
    }


    
    /** create from reaction scheme in literature.
     * 
     * @param doc
     */
    public static void createFromOSCAR(Document doc) {
        try {
            OscarTool oscar = new OscarTool(doc);
            oscar.convertToCML();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param iReaction
     * @param atomPairList
     * @param bondPairList
     * @param atomMap
     * @return electronPai list
     */
    @SuppressWarnings("unchecked")
	public List<ElectronPair> /*List<List<ElectronPair>> */ getElectronPairList(int iReaction, List<MappedAtomPair> atomPairList, List<MappedBondPair> bondPairList, CMLMap atomMap) {
        List<ElectronPair> electronPairList = new ArrayList<ElectronPair>();
        ElectronPair.currentReaction = iReaction;
        
        electronIdList = new ArrayList<String>();
        List<AtomBondPair> changedAtomPairList = new ArrayList<AtomBondPair>();
        List changedBondPairList = new ArrayList();
        for (int i = 0; i < atomPairList.size(); i++) {
            MappedAtomPair atomPair = (MappedAtomPair) atomPairList.get(i);
            compareAtoms(atomPair, changedAtomPairList);
        }
//        List bondPairList = getBondPairList(null, moleculeTool1, moleculeTool2, serial);
        for (int i = 0; i < bondPairList.size(); i++) {
            MappedBondPair bondPair = (MappedBondPair) bondPairList.get(i);
            compareBonds(bondPair, changedBondPairList);
        }

        boolean change = true;
// there may be several unconnected fragments so go on until no change
        while (change) {
            change = false;
// get start atom
            MappedAtomPair atomPair = getNextChangedAtomSource(changedAtomPairList);
            if (atomPair != null) {
                change = true;
                MappedBondPair bondPair = getUniqueBondPairContaining(changedBondPairList, atomPair);
                if (bondPair == null) {
                	LOG.debug("cannot find unique bond containing: "+atomPair);
                    change = false;
                    break;
                }
                addElectrons(atomPair, bondPair, electronPairList);
                changedAtomPairList.remove(atomPair);
                changedBondPairList.remove(bondPair);
//                @SuppressWarnings("unused")

//                MappedAtomPair nextAtomPair = getOtherAtomPair(bondPair, atomPair, atomMap, atomPairList);
// FIXME                iterateChain(atomPairList, changedAtomPairList, changedBondPairList, nextAtomPair, atomMap, electronPairList);
            }
        }

// cycles? take an arbitrary starting point and try. Only bonds should be left
        if (changedAtomPairList.size() > 0) {
        	LOG.debug(""+changedAtomPairList.size()+" unmatched atoms");
        	for (int i = 0; i < changedAtomPairList.size() && i < 3; i++) {
        		LOG.debug(": "+changedAtomPairList.get(i));
        	}
        }
        
        while (changedBondPairList.size() > 0) {
            int size = changedBondPairList.size();
            if (size == 0) {
                break;
            } else {
                MappedBondPair bondPair = (MappedBondPair) changedBondPairList.get(0);
                @SuppressWarnings("unused")
                MappedAtomPair startingAtomPair = bondPair.getAtomPair(0, atomPairList);
// FIXME                iterateCycle(atomPairList, changedBondPairList, startingAtomPair, atomMap, electronPairList);
            }
            if (size == changedBondPairList.size()) {
                LOG.debug("Cannot match "+size+" bonds");
            	for (int i = 0; i < changedBondPairList.size() && i < 3; i++) {
            		LOG.debug(": "+changedBondPairList.get(i));
            	}
                break;
            }
        }
        
        return electronPairList;
    }
    
    private void compareAtoms(MappedAtomPair atomPair, List<AtomBondPair> changedPairList) {
        AtomTool atomTool1 = AtomTool.getOrCreateTool(atomPair.atom1);
        AtomTool atomTool2 = AtomTool.getOrCreateTool(atomPair.atom2);
        if (atomTool1 == null) {
//            logger.error("Null atom partner for (2): "+atomPair.atom2.getId());
            return;
        }
        if (atomTool2 == null) {
//            logger.error("Null atom partner for (1): "+atomPair.atom1.getId());
            return;
        }
        int loneElectronCount1 = atomTool1.getLoneElectronCount();
        int loneElectronCount2 = atomTool2.getLoneElectronCount();
        if (loneElectronCount1 != loneElectronCount2) {
            changedPairList.add(atomPair);
            atomPair.setElectronChange(loneElectronCount2 - loneElectronCount1);
        }
    }

    private void compareBonds(MappedBondPair bondPair, List<AtomBondPair> changedPairList) {
        int order1 = (bondPair.bond1 == null) ? 0 : CMLElectron.getElectronCount(bondPair.bond1.getOrder());
        int order2 = (bondPair.bond2 == null) ? 0 : CMLElectron.getElectronCount(bondPair.bond2.getOrder());
        if (order1 != order2) {
            changedPairList.add(bondPair);
            bondPair.setElectronChange(order2 - order1);
        }
    }

    
    private List<ElectronPair> getElectronPairList(
    		Element parent, CMLMolecule mol1, CMLMolecule mol2, int iReaction) {
//    	 electrons
        List<ElectronPair> electronPairList = new ArrayList<ElectronPair>();
        Nodes electronNodes1 = mol1.query(".//cml:electron", CMLConstants.CML_XPATH);
        Nodes electronNodes2 = mol2.query(".//cml:electron", CMLConstants.CML_XPATH);
//    	 find electrons in first molecule or both
        MoleculeTool molTool1 = MoleculeTool.getOrCreateTool(mol1);
        MoleculeTool molTool2 = MoleculeTool.getOrCreateTool(mol2);
        for (int i = 0; i < electronNodes1.size(); i++) {
            CMLElectron electron1 = (CMLElectron) electronNodes1.get(i);
            String id = (electron1.getId());
            CMLElectron electron2 = molTool2.getElectronById(id);
            electronPairList.add(new ElectronPair(electron1, electron2, iReaction));
        }
//    	 find electrons in second molecule only
        for (int i = 0; i < electronNodes2.size(); i++) {
            CMLElectron electron2 = (CMLElectron) electronNodes2.get(i);
            String id = (electron2.getId());
            CMLElectron electron1 = molTool1.getElectronById(id);
            if (electron1 == null) {
                electronPairList.add(new ElectronPair(null, electron2, iReaction));
            }
        }
        return electronPairList;
    }
    
    
    @SuppressWarnings("unused")
    private boolean iterateChain(List<AtomBondPair> atomPairList, List<MappedAtomPair> changedAtomPairList,
    		List<MappedBondPair> changedBondPairList, MappedAtomPair nextAtomPair,
    		CMLMap atomMap, List<ElectronPair> electronPairList) {
        // iterate down chain two bonds at a time until terminal atom
        boolean change = true;
        while (true) {
            MappedBondPair bondPair = getUniqueBondPairContaining(changedBondPairList, nextAtomPair);
            if (bondPair == null) {
                change = false;
                break;
            }
            MappedAtomPair middleAtomPair = getOtherAtomPair(bondPair, nextAtomPair, atomMap, atomPairList);
            changedBondPairList.remove(bondPair);
            MappedBondPair otherBondPair = getUniqueBondPairContaining(changedBondPairList, middleAtomPair);
            if (otherBondPair == null) {
                if (!changedAtomPairList.contains(middleAtomPair)) {
                	LOG.debug("Cannot find terminal atom ("+middleAtomPair+") in electron chain");
                } else {
                    addElectrons(bondPair, middleAtomPair, electronPairList);
                    changedAtomPairList.remove(middleAtomPair);
                    changedBondPairList.remove(bondPair);
               }
               break;
            }
            addElectrons(bondPair, otherBondPair, electronPairList);
            changedAtomPairList.remove(middleAtomPair);
            changedBondPairList.remove(otherBondPair);
            nextAtomPair = getOtherAtomPair(otherBondPair, middleAtomPair, atomMap, atomPairList);
        }
        return change;
    }
    
    @SuppressWarnings("unused")
    private boolean iterateCycle(List<AtomPair> atomPairList, List<MappedBondPair> changedBondPairList, MappedAtomPair nextAtomPair, CMLMap atomMap, List<ElectronPair> electronPairList) {
        // iterate down chain two bonds at a time until terminal atom
        boolean change = true;
        while (true) {
            MappedBondPair bondPair = getUniqueBondPairContaining(changedBondPairList, nextAtomPair);
            if (bondPair == null) {
                change = false;
                break;
            }
            MappedAtomPair middleAtomPair = getOtherAtomPair(bondPair, nextAtomPair, atomMap, atomPairList);
            changedBondPairList.remove(bondPair);
            MappedBondPair otherBondPair = getUniqueBondPairContaining(changedBondPairList, middleAtomPair);
            if (otherBondPair == null) {
                changedBondPairList.remove(bondPair);
                break;
            }
            addElectrons(bondPair, otherBondPair, electronPairList);
            changedBondPairList.remove(otherBondPair);
            nextAtomPair = getOtherAtomPair(otherBondPair, middleAtomPair, atomMap, atomPairList);
        }
        return change;
    }

    /** gets next atom with a negative electron change.
     * 
     * @param changedPairList
     * @return atompair
     */
    private MappedAtomPair getNextChangedAtomSource(List<AtomBondPair> changedPairList) {
        for (AtomBondPair abp : changedPairList) {
            if (abp instanceof MappedAtomPair && abp.electronChange < 0) {
                return (MappedAtomPair) abp;
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
	private MappedAtomPair getOtherAtomPair(MappedBondPair bondPair, MappedAtomPair atomPair, CMLMap atomMap, List atomPairList) {
    	if (bondPair == null || atomPair == null || atomMap == null) {
    		return null;
    	}
    	String fromId = null;
    	String toId = null;
    	if (atomMap == null) {
    	} else if (bondPair.bond1 == null) {
        	toId = bondPair.bond2.getOtherAtomId(atomPair.id2);
        	fromId = atomMap.getRef(fromId, CMLMap.Direction.FROM);
        } else if (bondPair.bond2 == null) {
        	fromId = bondPair.bond1.getOtherAtomId(atomPair.id1);
        	toId = atomMap.getRef(toId, CMLMap.Direction.TO);
        } else {
        	fromId = bondPair.bond1.getOtherAtomId(atomPair.id1);
        	toId = bondPair.bond2.getOtherAtomId(atomPair.id2);
        }
    	MappedAtomPair otherAtomPair = MappedAtomPair.getAtomPair(toId, fromId, atomPairList);
    	return otherAtomPair;
    }


    private MappedBondPair getUniqueBondPairContaining(List<MappedBondPair> bondPairList, MappedAtomPair atomPair) {
        for (int i = 0; i < bondPairList.size(); i++) {
            MappedBondPair bondPair = bondPairList.get(i);
            if (bondPair.containsAtomPair(atomPair)) {
                return bondPair;
            }
        }
        return null;
    }
    
    /** add electrons to appropriate to/from atom/bonds as result of transfer.
     * 
     * add electron id to electronIdList
     * @param changedPairList
     * @param abp1
     * @param abp2
     */
    private void addElectrons(AtomBondPair abp1, AtomBondPair abp2, List<ElectronPair> electronPairList) {
        String electronId = "e"+(electronIdList.size()+1);
        electronIdList.add(electronId);
        // create electrons and add to appropriate atom or bond
        CMLElectron electron1 = abp1.createElectrons(abp1.electronChange, electronId);
        CMLElectron electron2 = abp2.createElectrons(abp2.electronChange, electronId);
        electronPairList.add(new ElectronPair(electron1, electron2));
    }
    
    // must redo this to cope with mapping 
    @SuppressWarnings("unchecked")
	void processElectrons(CMLMolecule molecule1, CMLMolecule molecule2, int serial) {
        List<MappedAtomPair> atomPairList = getAtomPairList(null, molecule1, molecule2, serial);
        // the generics are inconsistent here - FIXME
        List changedPairList = new ArrayList();
        for (MappedAtomPair atomPair : atomPairList) {
            compareAtoms(atomPair, changedPairList);
        }
// FIXME
        //        List<MappedBondPair> bondPairList = getBondPairList(null, molecule1, molecule2, serial);
        List<MappedBondPair> bondPairList = null;
        for (MappedBondPair bondPair : bondPairList) {
            compareBonds(bondPair, changedPairList);
        }
        if (changedPairList.size() > 0) {
            LOG.debug("Changed atoms/bonds ("+serial+")");
            for (int i = 0; i < changedPairList.size(); i++) {
                LOG.debug("BP "+changedPairList.get(i));
            }
        }
//        List<ElectronPair> electronPairList = getElectronPairList(null, molecule1, molecule2, serial);
        @SuppressWarnings("unused")
        List<ElectronPair> electronPairList = getElectronPairList(null, molecule1, molecule2, serial);

// there may be several unconnected fragments so go on until no change
        boolean change = true;
        while (change == true) {
            change = false;
// get start atom
            MappedAtomPair atomPair = getNextChangedAtomSource(changedPairList);
            if (atomPair != null) {
//                AtomBondPair abp1 = atomPair;
                change = true;
                MappedBondPair bondPair = getUniqueBondPairContaining(changedPairList, atomPair);
                if (bondPair == null) {
                    LOG.error("terminal atom cannot find unique ligand bond");
                    change = false;
                    break;
                }
                if (true) throw new RuntimeException("FIX ME");
//                removeFromChangedListAddElectrons(changedMappedAtomPairList, atomPair, bondPair);
//                LOG.debug("started: "+atomPair.id1+" ==> "+bondPair);
//
////                String nextAtomId = getOtherAtomId(bondPair, atomPair);
//                String nextAtomId = bondPair.bond1.getOtherAtomId(atomPair.id1);
//                iterateChain(changedMappedAtomPairList, nextAtomId);
            }
        }

// cycles? take an arbitrary starting point and try
        while (true) {
            int size = changedPairList.size();
            if (size == 0) {
                LOG.info("Finished all electrons");
                break;
            } else {
                LOG.debug("Cycles atoms/bonds ("+serial+")");
                AtomBondPair abp = (AtomBondPair) changedPairList.get(0);
                if (abp instanceof MappedBondPair) {
                    MappedBondPair bondPair = (MappedBondPair) abp;
                    @SuppressWarnings("unused")
                    String atomId = (bondPair.bond1 != null) ?
                        bondPair.bond1.getAtomId(0) :
                        bondPair.bond2.getAtomId(0);
// FIXME                    iterateChain(changedPairList, atomId);
                }
            }
            if (size == changedPairList.size()) {
                LOG.error("Cannot exhaust electron transfers");
                break;
            }
        }
    }
    
    /** add atoms from two molecules.
     * the mapping is done through IDs
     * @param parent
     * @param molTool1
     * @param molTool2
     * @param serial
     * @return atompair list
     */
    List<MappedAtomPair> getAtomPairList(Element parent, 
    		CMLMolecule mol1, CMLMolecule mol2, int serial) {
        List<MappedAtomPair> atomPairList = new ArrayList<MappedAtomPair>();
        List<CMLAtom> atoms1 = mol1.getAtoms();
        List<CMLAtom> atoms2 = mol2.getAtoms();
//      find atoms in first molecule or both
        for (CMLAtom atom1 : atoms1) {
            String id = (atom1.getId());
            CMLAtom atom2 = mol2.getAtomById(id);
            atomPairList.add(new MappedAtomPair(atom1, atom2));
        }
// find atoms in second molecule only
        for (CMLAtom atom2 : atoms2) {
            String id = (atom2.getId());
            if (mol1.getAtomById(id) == null) {
                atomPairList.add(new MappedAtomPair(null, atom2));
            }
        }
        return atomPairList;
    }

    /** add atoms aligned through map
     * 
     * @param parent the SVG element (maybe don't need at this stage?)
     * @param atoms1 atoms with "from" references in map
     * @param atoms2 atoms with "to" references in map
     * @param atomPairList to add the mapped atoms to
     * @param atomMapTool from atoms1 to atoms2
     * @return list
     */
    List<MappedAtomPair> addToMappedAtomPairList(Element parent, CMLAtom[] atoms1, CMLAtom[] atoms2,
    		List<MappedAtomPair> atomPairList, CMLMap atomMap, int serial) {
    	Map<String, CMLAtom> atomMap1 = createLookupTableById(atoms1);
    	Map<String, CMLAtom> atomMap2 = createLookupTableById(atoms2);
// find atoms atomMap
    	List<String> fromRefs = atomMap.getFromRefs();
    	if (fromRefs.size() == 0) {
    		LOG.debug("NO FROM REFS+++++++++++++++++");
    	}
        for (String fromRef : fromRefs) {
            CMLAtom atom1 = atomMap1.get(fromRef);
            String id2 = atomMap.getToRef(fromRef);
            CMLAtom atom2 = atomMap2.get(id2);
            if (atom2 == null) {
            	LOG.debug("NO MATCHED ATOM"+id2);
            }
            atomPairList.add(new MappedAtomPair(atom1, atom2));
        }
        return atomPairList;
    }
    
    /** create a table to lookup atoms by Id.
     * 
     * @param atoms array of atoms to index by ID
     * @return Map indexed on ID
     */
    private static Map<String, CMLAtom> createLookupTableById(CMLAtom[] atoms) {
    	Map<String, CMLAtom> map = new HashMap<String, CMLAtom>();
    	for (CMLAtom atom : atoms) {
    		map.put(atom.getId(), atom);
    	}
    	return map;
    }

    /**
     * @return reaction
     */
	public CMLReaction getReaction() {
		return reaction;
	}
	
    /** returns a "g" element
     * will require to be added to an svg element
     * @param drawable
	 * @throws IOException
     * @return null if problem
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
//    	AbstractDisplay moleculeDisplayx = (reactionDisplay == null) ? null :
//    		reactionDisplay.getMoleculeDisplay();
    	ensureReactionDisplay();
    	Transform2 transform2 = new Transform2(
    			new double[] {
    				1.,  0., 0.0,
    				0., -1., 0.0,
    				0.,  0., 1.}
    			);
    
    	List<CMLMolecule> molecules = reaction.getMolecules(Component.REACTANT);
    	if (molecules.size() == 0) {
    		LOG.debug("No molecules to display");
    	} else if (applyScale) {
    		transform2 = scaleToBoundingBoxesAndScreenLimits(molecules);
    	}
    	
    	SVGElement g = createSVGElement(drawable, transform2);
    	g.setProperties(getReactionDisplay());
    	MoleculeDisplay moleculeDisplay = getReactionDisplay().getMoleculeDisplay();
    	displayMolecules(drawable, g, moleculeDisplay, molecules);
    	try {
    		drawable.output(g);
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    	return g;
    }

	private Transform2 scaleToBoundingBoxesAndScreenLimits(List<CMLMolecule> molecules) {
		Transform2 transform2 = null;
		try {
			Real2Range boundingBox = getBoundingBox(molecules);
			Real2Interval screenBoundingBox = getReactionDisplay().getMoleculeDisplay().getScreenExtent();
			Real2Interval moleculeInterval = new Real2Interval(boundingBox);
			double scale = moleculeInterval.scaleTo(screenBoundingBox);
			double[] offsets = moleculeInterval.offsetsTo(screenBoundingBox, scale);
			transform2 = new Transform2 (
					new double[] {
						scale, 0., offsets[0],
						0.,-scale, offsets[1],
						0.,    0.,   1.}
					);
					
		} catch (NullPointerException npe) {
			// happens with small number of atoms
		}
		return transform2;
	}
    
	Real2Range getBoundingBox(List<CMLMolecule> molecules) {
		Real2Range range = new Real2Range();
		for (CMLMolecule molecule : molecules) {
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			Real2Range molRange = moleculeTool.calculateBoundingBox2D();
			range.plus(molRange);
		}
		return range;
	}

    
	private void displayMolecules(CMLDrawable drawable, SVGElement g,
			MoleculeDisplay moleculeDisplay, List<CMLMolecule> molecules) {
		for (CMLMolecule molecule : molecules) {
    		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    		moleculeTool.setMoleculeDisplay(moleculeDisplay);
// ??    		atomTool.setMoleculeTool(this);
    		GraphicsElement a = moleculeTool.createGraphicsElement(drawable);
    		if (a != null) {
    			a.detach();
    			g.appendChild(a);
    		}
		}
	}


    private void ensureReactionDisplay() {
    	if (getReactionDisplay() == null) {
    		setReactionDisplay(ReactionDisplay.getDEFAULT());
    	}
    }

    /** convenience method
     * 
     */
    public List<CMLReactant> getReactants() {
    	List<CMLReactant> reactants = new ArrayList<CMLReactant>();
    	for (CMLReactant reactant : reaction.getReactantList().getReactantElements()) {
    		reactants.add(reactant);
    	}
    	return reactants;
    }
    
    /** convenience method
     * 
     * @param i
     * @return i'th reactant
     */
    public CMLReactant getReactant(int i) {
    	return this.getReactants().get(i);
    }
    
    /** convenience method
     * 
     * @param i
     * @return i'th reactant
     */
    public CMLMolecule getProductMolecule(int i) {
    	CMLProduct product = getProduct(i);
    	return (product == null) ? null : product.getMolecule();
    }
    
    /** convenience method
     * 
     */
    public List<CMLProduct> getProducts() {
    	List<CMLProduct> products = new ArrayList<CMLProduct>();
    	for (CMLProduct product : reaction.getProductList().getProductElements()) {
    		products.add(product);
    	}
    	return products;
    }
    
    
    /** convenience method
     * 
     * @param i
     * @return i'th product
     */
    public CMLProduct getProduct(int i) {
    	return reaction.getProductList().getProductElements().get(i);
    }
    /** convenience method
     * 
     * @param i
     * @return i'th reactant
     */
    public CMLMolecule getReactantMolecule(int i) {
    	CMLReactant reactant = getReactant(i);
    	return (reactant == null) ? null : reactant.getMolecule();
    }
    
    /** convenience method
     */
    public List<CMLMolecule> getReactantMolecules() {
    	List<CMLMolecule> list = new ArrayList<CMLMolecule>();
    	for (CMLReactant reactant : this.getReactants()) {
    		CMLMolecule molecule = reactant.getMolecule();
    		list.add(molecule);
    	}
    	return list;
    }


    /** convenience method
     */
    public List<CMLMolecule> getProductMolecules() {
    	List<CMLMolecule> list = new ArrayList<CMLMolecule>();
    	for (CMLProduct product : this.getProducts()) {
    		CMLMolecule molecule = product.getMolecule();
    		list.add(molecule);
    	}
    	return list;
    }


	public void mapReactantsToProducts() {
//		List<CMLMolecule> reactantMolecules = getMolecules(Component.REACTANT);
//		List<CMLMolecule> productMolecules = getMolecules(Component.PRODUCT);
//		List<List<Long>> reactantMorgans = extractMorgans(reactantMolecules);
//		List<List<Long>> productMorgans = extractMorgans(productMolecules);
		mapSingleReactantToSingleProductWithAtomMatcher();
	}
	
	public CMLMap mapReactantsToProductsUsingAtomSets() {
		List<CMLMolecule> reactantMolecules = getMolecules(Component.REACTANT);
		if (reactantMolecules.size() == 0) {
			throw new RuntimeException("No reactants");
		}
		CMLAtomSet reactantAtomSet = MoleculeTool.createAtomSet(reactantMolecules);
		if (reactantAtomSet == null || reactantAtomSet.size() == 0) {
			throw new RuntimeException("No atoms in reactants");
		}
		List<CMLMolecule> productMolecules = getMolecules(Component.PRODUCT);
		if (reactantMolecules.size() == 0) {
			throw new RuntimeException("No products");
		}
		CMLAtomSet productAtomSet = MoleculeTool.createAtomSet(productMolecules);
		if (productAtomSet == null || productAtomSet.size() == 0) {
			throw new RuntimeException("No atoms in products");
		}
		AtomMatcher atomMatcher = new AtomTreeMatcher();
		CMLMap cmlMap = atomMatcher.mapAtomSets(reactantAtomSet, productAtomSet);
		translateMapToUseOriginalIds(cmlMap, reactantAtomSet, productAtomSet);
		reaction.addMap(cmlMap);
		
		return cmlMap;
	}

	private void translateMapToUseOriginalIds(CMLMap cmlMap,
			CMLAtomSet reactantAtomSet, CMLAtomSet productAtomSet) {
		translateMap(Direction.FROM, reactantAtomSet, cmlMap);
		translateMap(Direction.TO, productAtomSet, cmlMap);
	}

	private void translateMap(Direction direction, CMLAtomSet atomSet, CMLMap cmlMap) {
		Map<String, String> atomSet2Map = new HashMap<String, String>();
		List<CMLAtom> atoms = atomSet.getAtoms();
		for (CMLAtom atom : atoms) {
			String id = atom.getId();
			CMLLabel label = atom.getLabelElements().get(0);
			String labelValue = (label == null) ? null : label.getCMLValue();
			if (labelValue != null) {
				atomSet2Map.put(id, labelValue);
			}
		}
		MapTool cmlMapTool = MapTool.getOrCreateTool(cmlMap);
		cmlMapTool.translateIds(direction, atomSet2Map);
	}

	public void addReactant(String smiles) {
		SMILESTool smilesTool = new SMILESTool();
		addReactant(FormulaTool.calculateMolecule(smilesTool, smiles));
	}

	private void addReactant(CMLMolecule reactantMol) {
		CMLReactant reactant = new CMLReactant();
		reactant.addMolecule(reactantMol);
		this.reaction.addReactant(reactant);
	}

	public void addProduct(String smiles) {
		SMILESTool smilesTool = new SMILESTool();
		CMLMolecule productMolecule = FormulaTool.calculateMolecule(smilesTool, smiles);
		CMLProduct product = new CMLProduct();
		product.addMolecule(productMolecule);
		this.addProduct(product);
	}
	
	/** should be part of CMLReaction
	 * 
	 * @param product
	 */
	public void addProduct(CMLProduct product) {
		CMLProductList productList = reaction.getProductList();
		if (productList == null) {
			productList = new CMLProductList();
			reaction.addProductList(productList);
		}
		productList.addProduct(product);
	}

	private List<List<Long>> extractMorgans(List<CMLMolecule> molecules) {
		List<List<Long>> morgans = new ArrayList<List<Long>>();
		for (CMLMolecule molecule : molecules) {
			Morgan morgan = new Morgan(MoleculeTool.getOrCreateTool(molecule).getAtomSet());
			List<Long> morganLongs = morgan.getMorganList();
			List<CMLAtomSet> atomSets = morgan.getAtomSetList();
			for (int i = 0; i < morganLongs.size(); i++) {
				System.out.println(" "+morganLongs.get(i)+" .. "+atomSets.get(i).getValue());
			}
			System.out.println();
		}
		return morgans;
	}

	/** 
	 * if reaction has a single reactant and single product uses
	 * AtomMatcher.mapMolecules to get atom2atom match
	 * @return
	 */
	public CMLMap mapSingleReactantToSingleProductWithAtomMatcher() {
		CMLMap map = null;
		List<CMLMolecule> reactantMolecules = reaction.getMolecules(Component.REACTANT);
		List<CMLMolecule> productMolecules = reaction.getMolecules(Component.PRODUCT);
		if (reactantMolecules.size() == 1 && productMolecules.size() == 1) {
			CMLMolecule reactantMolecule = reactantMolecules.get(0);
			CMLMolecule productMolecule = productMolecules.get(0);
			AtomMatcher atomMatcher = new AtomTreeMatcher();
			map = atomMatcher.mapMolecules(reactantMolecule, productMolecule);
			CMLAtomSet reactantAtomSet = new CMLAtomSet(reactantMolecule);
			CMLAtomSet productAtomSet = new CMLAtomSet(productMolecule);			
//			reactantAtomSet.debug("react");
//			productAtomSet.debug("prod");
//			map = atomMatcher.getUniqueMatchedAtoms(reactantAtomSet, productAtomSet);			
			map.debug("FINAL MAP");
		}
		return map;
	}

	public void guessProducts() {
		CMLFormula diffFormula = this.calculateDifferenceFormula();
		String concise = diffFormula.getConcise();
		if (concise == null || concise.trim().equals(CMLConstants.S_EMPTY)) {
			// do nothing
		} else {
			CMLMolecule guessedProductMolecule = getProbableProduct(concise);
			if (guessedProductMolecule != null) {
				this.addProduct(guessedProductMolecule);
			}
		}
	}

	public static CMLMolecule getProbableProduct(String concise) {
		CMLMolecule molecule = null;
		if (concise != null) {
			concise = concise.trim();
			molecule = FormulaTool.ensureConcise2MoleculeMap().get(concise);
		}
		return molecule;
	}

	public void addProduct(CMLMolecule productMolecule) {
		CMLProduct product = new CMLProduct();
		product.addMolecule(productMolecule);
		this.addProduct(product);
	}

	private void addPatternsToLinkedAtoms(SVGSVG[] svgsvg, CMLMap map) {
		CMLElements<CMLLink> links = map.getLinkElements();
		List<CMLLink> uniqueLinks = new ArrayList<CMLLink>();
		List<CMLLink> balancedCommonLinks = new ArrayList<CMLLink>();
		List<CMLLink> otherLinks = new ArrayList<CMLLink>();
		
		for (CMLLink link : links) {
			String title = link.getTitle();
			if (title.startsWith(AtomTreeMatcher.UNIQUE_TREE)) {
				uniqueLinks.add(link);
			} else if (title.startsWith("balanced commonAtomTree")) {
				balancedCommonLinks.add(link);
			} else {
				otherLinks.add(link);
			}
		}
		addGradientsToLinkAtoms(svgsvg, uniqueLinks, 6.0, "black");
		addGradientsToLinkAtoms(svgsvg, balancedCommonLinks, 6.0, "red");
		addGradientsToLinkAtoms(svgsvg, otherLinks, 6.0, "blue");
	}
	
	private void addGradientsToLinkAtoms(SVGSVG[] svgsvg, List<CMLLink> links, double strokeWidth, String strokeColour) {
		int gradient = 0;
		int nlinks = links.size();
		for (CMLLink link : links) {
			gradient++;
			addPattern(svgsvg[0], link.getFromSet(), gradient, strokeWidth, strokeColour);
			addDefs(svgsvg[0]);
			addPattern(svgsvg[1], link.getToSet(), gradient, strokeWidth, strokeColour);
			addDefs(svgsvg[1]);
		}
	}

	private void addDefs(SVGSVG svgsvg) {
		ensurePatterns();
		if (svgsvg != null && svgsvg.query("//*[local-name()='defs']").size() == 0) {
			svgsvg.insertChild(defs.copy(), 0);
		}
	}
	
	private static Element ensurePatterns() {
		if (patternCount == 0 && defs == null) {
			defs = new Element("defs", CMLConstants.CML_NS);
			double w01 = 4.0;
			double h01 = 4.0;
			double w11 = 4.0;
			double h11 = 4.0;
			
			double w02 = 8.0;
			double h02 = 4.0;
			double w12 = 8.0;
			double h12 = 4.0;
			
			double w03 = 4.0;
			double h03 = 8.0;
			double w13 = 4.0;
			double h13 = 8.0;
			
			double w04 = 8.0;
			double h04 = 8.0;
			double w14 = 8.0;
			double h14 = 8.0;
			
			double w05 = 4.0;
			double h05 = 12.0;
			double w15 = 4.0;
			double h15 = 12.0;
			
			double w06 = 12.0;
			double h06 = 4.0;
			double w16 = 12.0;
			double h16 = 4.0;
			
			double w07 = 8.0;
			double h07 = 12.0;
			double w17 = 8.0;
			double h17 = 12.0;
			
			double w08 = 12.0;
			double h08 = 8.0;
			double w18 = 12.0;
			double h18 = 8.0;
			
			String red = "#FF7777";
			defs.appendChild(createPattern(w01, h01, w11, h11, ++patternCount, red));
			defs.appendChild(createPattern(w02, h02, w12, h12, ++patternCount, red));
			defs.appendChild(createPattern(w03, h03, w13, h13, ++patternCount, red));
			defs.appendChild(createPattern(w04, h04, w14, h14, ++patternCount, red));
			defs.appendChild(createPattern(w05, h05, w15, h15, ++patternCount, red));
			defs.appendChild(createPattern(w06, h06, w16, h16, ++patternCount, red));
			defs.appendChild(createPattern(w07, h07, w17, h17, ++patternCount, red));
			defs.appendChild(createPattern(w08, h08, w18, h18, ++patternCount, red));
			
			String blue = "#7777FF";
			defs.appendChild(createPattern(w01, h01, w11, h11, ++patternCount, blue));
			defs.appendChild(createPattern(w02, h02, w12, h12, ++patternCount, blue));
			defs.appendChild(createPattern(w03, h03, w13, h13, ++patternCount, blue));
			defs.appendChild(createPattern(w04, h04, w14, h14, ++patternCount, blue));
			defs.appendChild(createPattern(w05, h05, w15, h15, ++patternCount, blue));
			defs.appendChild(createPattern(w06, h06, w16, h16, ++patternCount, blue));
			defs.appendChild(createPattern(w07, h07, w17, h17, ++patternCount, blue));
			defs.appendChild(createPattern(w08, h08, w18, h18, ++patternCount, blue));
			
			String green = "#77FF77";
			defs.appendChild(createPattern(w01, h01, w11, h11, ++patternCount, green));
			defs.appendChild(createPattern(w02, h02, w12, h12, ++patternCount, green));
			defs.appendChild(createPattern(w03, h03, w13, h13, ++patternCount, green));
			defs.appendChild(createPattern(w04, h04, w14, h14, ++patternCount, green));
			defs.appendChild(createPattern(w05, h05, w15, h15, ++patternCount, green));
			defs.appendChild(createPattern(w06, h06, w16, h16, ++patternCount, green));
			defs.appendChild(createPattern(w07, h07, w17, h17, ++patternCount, green));
			defs.appendChild(createPattern(w08, h08, w18, h18, ++patternCount, green));
			
			defs.appendChild(createPattern(w01, h01, w11, h11, ++patternCount, "black"));
			defs.appendChild(createPattern(w02, h02, w12, h12, ++patternCount, "black"));
			defs.appendChild(createPattern(w03, h03, w13, h13, ++patternCount, "black"));
			defs.appendChild(createPattern(w04, h04, w14, h14, ++patternCount, "black"));
			defs.appendChild(createPattern(w05, h05, w15, h15, ++patternCount, "black"));
			defs.appendChild(createPattern(w06, h06, w16, h16, ++patternCount, "black"));
			defs.appendChild(createPattern(w07, h07, w17, h17, ++patternCount, "black"));
			defs.appendChild(createPattern(w08, h08, w18, h18, ++patternCount, "black"));
		}
		return defs;
	}


	private void addPattern(SVGSVG svgsvg, String[] ids, int gradient, 
			double strokeWidth, String strokeColour) {
		if (svgsvg != null && ids != null) {
			for (String id : ids) {
				String aid = id.substring("m1_".length());
				String gid = "g_"+aid;
				Nodes gNodes = svgsvg.query("//*[local-name()='g' and @id='g_"+aid+"']/*[local-name()='circle']");
				if (gNodes.size() == 1) {
					SVGCircle circle = (SVGCircle) gNodes.get(0);
					circle.setFill("url(#"+PATTERN+gradient+")");
					circle.setStrokeWidth(strokeWidth);
					circle.setStroke(strokeColour);
					Nodes textNodes = ((Element)gNodes.get(0)).getParent().query("*[local-name()='text']");
					if (textNodes.size() == 1) {
						((SVGText) textNodes.get(0)).setFill("black");
					}
				}
			}
		}
	}

	private static Element createPattern(double width0, double height0, double width,
			double height, int iid, String fill) {
		String id = PATTERN+iid;
		String patternUnits = "userSpaceOnUse";
		Element pattern = new Element("pattern", CMLConstants.SVG_NS);
		pattern.addAttribute(new Attribute("id", id));
		pattern.addAttribute(new Attribute("patternUnits", patternUnits));
		pattern.addAttribute(new Attribute("width", ""+width0));
		pattern.addAttribute(new Attribute("height", ""+height0));
		SVGRect rect = new SVGRect(0.,0., width, height);
		rect.setFill(fill);
		rect.setStroke("white");
		rect.setStrokeWidth(2.0);
		pattern.appendChild(rect);
		return pattern;
	}
	
	private void addGraphicalLinks(SVGSVG svgTot, SVGG g0, SVGG g1) {
		Real2 offset0 = getOffset(g0);
		Real2 offset1 = getOffset(g1);
		Map<String, List<SVGCircle>> map0 = indexCirclesByFill(g0);
		Map<String, List<SVGCircle>> map1 = indexCirclesByFill(g1);
		for (String fill : map0.keySet()) {
			List<SVGCircle> circles0 = map0.get(fill);
			List<SVGCircle> circles1 = map1.get(fill);
			if (circles0 != null && circles1 != null) {
				for (SVGCircle circle0 : circles0) {
					for (SVGCircle circle1 : circles1) {
						SVGLine line = createLine(offset0, offset1, circle0, circle1);
						svgTot.appendChild(line);
					}
				}
			}
		}
	}

	private Real2 getOffset(SVGG g) {
		return g.getTransform2FromAttribute().getTranslation();
	}

	private SVGLine createLine(Real2 offset0, Real2 offset1, SVGCircle circle0, SVGCircle circle1) {
		Real2 cxy0 = getCircleCentre(circle0);
		cxy0 = cxy0.plus(offset0);
		Real2 cxy1 = getCircleCentre(circle1);
		cxy1 = cxy1.plus(offset1);
		SVGLine line = new SVGLine(cxy0, cxy1);
		line.setStroke("green");
		line.setStrokeWidth(0.5);
		return line;
	}

	private Real2 getCircleCentre(SVGCircle circle) {
		SVGG g = (SVGG) circle.getParent();
		Transform2 t = g.getTransform2FromAttribute();
		Real2 cxy = t.getTranslation();
		cxy.y = -cxy.y;
		return cxy;
	}

	private Map<String, List<SVGCircle>> indexCirclesByFill(SVGG g) {
		Map<String, List<SVGCircle>> circleListByFill = new HashMap<String, List<SVGCircle>>();
		Nodes circles = g.query(".//*[local-name()='"+SVGCircle.TAG+"']");
		for (int i = 0; i < circles.size(); i++) {
			SVGCircle circle = (SVGCircle) circles.get(i);
			String fill = circle.getFill();
			List<SVGCircle> circleList = circleListByFill.get(fill);
			if (circleList == null) {
				circleList = new ArrayList<SVGCircle>();
				circleListByFill.put(fill, circleList);
			}
			circleList.add(circle);
		}
		return circleListByFill;
	}

	private Element createMenuItem(String bothName) {
		Element li;
		li = new Element("li");
		Element a = new Element("a");
		a.addAttribute(new Attribute("href", bothName));
		a.addAttribute(new Attribute("target", "right"));
		a.appendChild(bothName.substring(0, bothName.indexOf(CMLConstants.S_PERIOD)));
		li.appendChild(a);
		return li;
	}

	private SVGG addTransformedG(SVGSVG svg, double xshift, double yshift) {
		SVGG g = new SVGG();
		Transform2 transform = new Transform2(new Vector2(xshift, yshift));
		g.setAttributeFromTransform2(transform);
		// skip the defs
		g.appendChild(svg.getChild(1).copy());
		return g;
	}

	public static boolean getCommand(String[] commands, String string) {
		for (String command : commands) {
			if (command != null && command.equals(string)) {
				return true;
			}
		}
		return false;
	}

	public void alignFirstReactantProduct2D() {
		CMLMolecule reactant0 = this.getReactantMolecule(0);
		CMLMolecule product0 = this.getProductMolecule(0);
	}

	public SVGGBox drawSVG() {
		SVGGBox svgTot = new SVGGBox();
		if (reactionDisplay.getId() != null) {
			svgTot.setId(reactionDisplay.getId());
		}
		SVGGBox reactantsSVGG = drawReactants();
		svgTot.addSVGG(reactantsSVGG);
		double maxHeight = getMaxHeight(reactantsSVGG);
		SVGGBox productsSVGG = drawProducts();
		Transform2 transform = productsSVGG.getTransform2FromAttribute();
		if (transform == null) {
			transform = new Transform2();
		}
		transform = transform.concatenate(new Transform2(new Vector2(0.0, maxHeight)));
		productsSVGG.setTransform(transform);
		svgTot.addSVGG(productsSVGG);
		Transform2 transformG = svgTot.ensureTransform2();
		transformG = transformG.concatenate(
				Transform2.applyScales(getReactionDisplay().scales.x, getReactionDisplay().scales.y));
		svgTot.setTransform(transformG);
		return svgTot;
	}

	private double getMaxHeight(SVGG reactantsSVGG) {
		Nodes gs = reactantsSVGG.query("./*[local-name()='"+SVGG.TAG+"']");
		double maxHeight = -1.0;
		for (int i = 0; i < gs.size(); i++) {
			SVGG g = (SVGG) gs.get(i);
			double height = new BoundingRect(g).getHeight();
			maxHeight = (maxHeight < height) ? height : maxHeight;
		}
		return maxHeight;
	}

	private double getTotalHeight(SVGG reactantsSVGG) {
		Nodes gs = reactantsSVGG.query("./*[local-name()='"+SVGG.TAG+"']");
		double totalHeight = 0.0;
		for (int i = 0; i < gs.size(); i++) {
			SVGG g = (SVGG) gs.get(i);
			totalHeight += new BoundingRect(g).getHeight();
		}
		return totalHeight;
	}

	private double getMaxWidth(SVGG reactantsSVGG) {
		Nodes gs = reactantsSVGG.query("./*[local-name()='"+SVGG.TAG+"']");
		double maxWidth = -1.0;
		for (int i = 0; i < gs.size(); i++) {
			SVGG g = (SVGG) gs.get(i);
			double width = new BoundingRect(g).getWidth();
			maxWidth = (maxWidth < width) ? width : maxWidth;
		}
		return maxWidth;
	}

	private double getTotalWidth(SVGG reactantsSVGG) {
		Nodes gs = reactantsSVGG.query("./*[local-name()='"+SVGG.TAG+"']");
		double totalWidth = 0.0;
		for (int i = 0; i < gs.size(); i++) {
			SVGG g = (SVGG) gs.get(i);
			totalWidth += new BoundingRect(g).getWidth();
		}
		return totalWidth;
	}

	public SVGGBox drawReactants() {
		return createMoleculeSVGs(getLayout(reactionDisplay.reactantOrientation), this.getReactantMolecules());
	}

	public SVGGBox drawProducts() {
		return createMoleculeSVGs(getLayout(reactionDisplay.productOrientation),  this.getProductMolecules());
	}

	private SVGLayout getLayout(Orientation orientation) {
		SVGLayout layout = null;
		if (orientation.equals(Orientation.HORIZONTAL)) {
			layout = SVGLayout.LEFT2RIGHT;
		} else if (orientation.equals(Orientation.VERTICAL)) {
			layout = SVGLayout.TOP2BOTTOM;
		}
		return layout;
	}

	private SVGGBox createMoleculeSVGs(SVGLayout layout, List<CMLMolecule> molecules) {
		SVGGBox svggBox = new SVGGBox();
		svggBox.setLayout(layout);
		ensureReactionDisplay();
		MoleculeDisplay moleculeDisplay = getReactionDisplay().getMoleculeDisplay();
		for (CMLMolecule molecule : molecules) {
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			SVGGBox svgg = moleculeTool.drawAndTranslateToRectCorner(moleculeDisplay);
			svggBox.addSVGG(svgg);
		}
		return svggBox;
	}

	public void setReactionDisplay(ReactionDisplay reactionDisplay) {
		this.reactionDisplay = reactionDisplay;
	}

	public ReactionDisplay getReactionDisplay() {
		return reactionDisplay;
	}


}
