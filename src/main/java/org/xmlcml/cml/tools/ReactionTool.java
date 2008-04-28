package org.xmlcml.cml.tools;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.ReactionComponent;
import org.xmlcml.cml.element.CMLFormula.Sort;
import org.xmlcml.cml.element.CMLReaction.Component;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.euclid.Real2Interval;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;

/**
 * too to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public class ReactionTool extends AbstractSVGTool {

    Logger logger = Logger.getLogger(ReactionTool.class.getName());

    private CMLReaction reaction = null;
	private ReactionDisplay reactionDisplay;

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
        this.reaction = reaction;
		this.reaction.setTool(this);
    }

	/** gets ReactionTool associated with reaction.
	 * if null creates one and sets it in reaction
	 * @param reaction
	 * @return tool
	 */
	public static ReactionTool getOrCreateTool(CMLReaction reaction) {
		ReactionTool reactionTool = (ReactionTool) reaction.getTool();
		if (reactionTool == null) {
			reactionTool = new ReactionTool(reaction);
			reaction.setTool(reactionTool);
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
     * @throws CMLException
     * @throws IOException
     */
    public void outputBalance(Writer w) throws CMLException, IOException {
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
     * @throws CMLException
     * @throws IOException
     */
    public void outputReaction(Writer w) throws CMLException, IOException {
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


//    /**
//     * get all molecules on reactant or product side. includes cmlSpectator
//     * 
//     * @param reactantProduct ReactionComponent.Type.REACTANT or ReactionComponent.Type.PRODUCT
//     * @return all molecules
//     */
//    public List<CMLMolecule> getMoleculesIncludingSpectators(
//            ReactionComponent.Type reactantProduct) {
//        List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
//        if (reactantProduct.equals(Type.REACTANT)) {
//            CMLReactantList reactantList = (CMLReactantList) reaction
//                    .getFirstCMLChild(CMLReactantList.TAG);
//            if (reactantList != null) {
//                moleculeList = ReactionTool.getMolecules(reactantList);
//            }
//        } else if (reactantProduct.equals(Type.PRODUCT)) {
//            CMLProductList productList = (CMLProductList) reaction
//                    .getFirstCMLChild(CMLProductList.TAG);
//            if (productList != null) {
//                moleculeList = ReactionTool.getMolecules(productList);
//            }
//        }
//        CMLSpectatorList spectatorList = (CMLSpectatorList) reaction
//                .getFirstCMLChild(CMLSpectatorList.TAG);
//        if (spectatorList != null) {
//            List<CMLMolecule> moleculex = spectatorList
//                    .getMolecules(reactantProduct);
//            for (int i = 0; i < moleculex.size(); i++) {
//                moleculeList.add(moleculex.get(i));
//            }
//        }
//        return moleculeList;
//    }

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
                 formula = (CMLFormula) molecule.getFirstCMLChild(CMLFormula.TAG);
                 if (formula == null) {
                     formula = molecule.calculateFormula(CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS);
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
            throw new CMLRuntimeException("null prodReact");
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
    	Element reactantProductList = (reactionComponent.equals(Component.REACTANTLIST)) ? 
    			reaction.getReactantList() :
				reaction.getProductList();
        List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
        if (reactantProductList != null) {
	        Nodes moleculeNodes = reactantProductList.query(".//cml:molecule", CML_XPATH);
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


//    /**
//     * translate reactants and products geometrically to overlap centroids.
//     * 
//     * experimental
//     * 
//     * @param spectatorList
//     */
//    public void translateSpectatorProductsToReactants(
//            CMLSpectatorList spectatorList) {
//        List<CMLMolecule> reactantSpectator = spectatorList
//                .getSpectatorMolecules(CMLReactant.TAG);
//        List<CMLMolecule> productSpectator = spectatorList
//                .getSpectatorMolecules(CMLProduct.TAG);
//        if (reactantSpectator.size() != 0 || productSpectator.size() != 0) {
//            CMLAtomSet reactantAtomSet = AtomSetTool
//                    .createAtomSet(reactantSpectator);
//            CMLAtomSet productAtomSet = AtomSetTool
//                    .createAtomSet(productSpectator);
//            Real2 reactantCentroid = reactantAtomSet.getCentroid2D();
//            Real2 productCentroid = productAtomSet.getCentroid2D();
//            Real2 delta = reactantCentroid.subtract(productCentroid);
//            productAtomSet.translate2D(delta);
//        }
//    }

    
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
            try {
                compareAtoms(atomPair, changedAtomPairList);
            } catch (CMLException e) {
                logger.severe("Atom comparison problem "+e);
            }
        }
//        List bondPairList = getBondPairList(null, moleculeTool1, moleculeTool2, serial);
        for (int i = 0; i < bondPairList.size(); i++) {
            MappedBondPair bondPair = (MappedBondPair) bondPairList.get(i);
            try {
                compareBonds(bondPair, changedBondPairList);
            } catch (CMLException e) {
                logger.severe("Bond comparison problem "+e);
            }
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
                	System.out.println("cannot find unique bond containing: "+atomPair);
                    change = false;
                    break;
                }
                addElectrons(atomPair, bondPair, electronPairList);
                changedAtomPairList.remove(atomPair);
                changedBondPairList.remove(bondPair);

                MappedAtomPair nextAtomPair = getOtherAtomPair(bondPair, atomPair, atomMap, atomPairList);
// FIXME                iterateChain(atomPairList, changedAtomPairList, changedBondPairList, nextAtomPair, atomMap, electronPairList);
            }
        }

// cycles? take an arbitrary starting point and try. Only bonds should be left
        if (changedAtomPairList.size() > 0) {
        	System.out.print(""+changedAtomPairList.size()+" unmatched atoms");
        	for (int i = 0; i < changedAtomPairList.size() && i < 3; i++) {
        		System.out.print(": "+changedAtomPairList.get(i));
        	}
    		System.out.println();
        }
        
        while (changedBondPairList.size() > 0) {
            int size = changedBondPairList.size();
            if (size == 0) {
                break;
            } else {
                MappedBondPair bondPair = (MappedBondPair) changedBondPairList.get(0);
                MappedAtomPair startingAtomPair = bondPair.getAtomPair(0, atomPairList);
// FIXME                iterateCycle(atomPairList, changedBondPairList, startingAtomPair, atomMap, electronPairList);
            }
            if (size == changedBondPairList.size()) {
                System.out.print("Cannot match "+size+" bonds");
            	for (int i = 0; i < changedBondPairList.size() && i < 3; i++) {
            		System.out.print(": "+changedBondPairList.get(i));
            	}
        		System.out.println();
                break;
            }
        }
        
        return electronPairList;
    }
    
    private void compareAtoms(MappedAtomPair atomPair, List<AtomBondPair> changedPairList) throws CMLException {
        AtomTool atomTool1 = AtomTool.getOrCreateTool(atomPair.atom1);
        AtomTool atomTool2 = AtomTool.getOrCreateTool(atomPair.atom2);
        if (atomTool1 == null) {
//            logger.severe("Null atom partner for (2): "+atomPair.atom2.getId());
            return;
        }
        if (atomTool2 == null) {
//            logger.severe("Null atom partner for (1): "+atomPair.atom1.getId());
            return;
        }
        int loneElectronCount1 = atomTool1.getLoneElectronCount();
        int loneElectronCount2 = atomTool2.getLoneElectronCount();
        if (loneElectronCount1 != loneElectronCount2) {
            changedPairList.add(atomPair);
            atomPair.setElectronChange(loneElectronCount2 - loneElectronCount1);
        }
    }

    private void compareBonds(MappedBondPair bondPair, List<AtomBondPair> changedPairList) throws CMLException {
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
        Nodes electronNodes1 = mol1.query(".//cml:electron", CML_XPATH);
        Nodes electronNodes2 = mol2.query(".//cml:electron", CML_XPATH);
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
                	System.out.println("Cannot find terminal atom ("+middleAtomPair+") in electron chain");
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
            try {
                compareAtoms(atomPair, changedPairList);
            } catch (CMLException e) {
                logger.severe("Atom comparison problem "+e);
            }
        }
// FIXME
        //        List<MappedBondPair> bondPairList = getBondPairList(null, molecule1, molecule2, serial);
        List<MappedBondPair> bondPairList = null;
        for (MappedBondPair bondPair : bondPairList) {
            try {
                compareBonds(bondPair, changedPairList);
            } catch (CMLException e) {
                logger.severe("Bond comparison problem "+e);
            }
        }
        if (changedPairList.size() > 0) {
            System.out.println("Changed atoms/bonds ("+serial+")");
            for (int i = 0; i < changedPairList.size(); i++) {
                System.out.println("BP "+changedPairList.get(i));
            }
        }
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
                    logger.severe("terminal atom cannot find unique ligand bond");
                    change = false;
                    break;
                }
                if (true) throw new RuntimeException("FIX ME");
//                removeFromChangedListAddElectrons(changedMappedAtomPairList, atomPair, bondPair);
//                System.out.println("started: "+atomPair.id1+" ==> "+bondPair);
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
                logger.info("Finished all electrons");
                break;
            } else {
                System.out.println("Cycles atoms/bonds ("+serial+")");
                AtomBondPair abp = (AtomBondPair) changedPairList.get(0);
                if (abp instanceof MappedBondPair) {
                    MappedBondPair bondPair = (MappedBondPair) abp;
                    String atomId = (bondPair.bond1 != null) ?
                        bondPair.bond1.getAtomId(0) :
                        bondPair.bond2.getAtomId(0);
// FIXME                    iterateChain(changedPairList, atomId);
                }
            }
            if (size == changedPairList.size()) {
                logger.severe("Cannot exhaust electron transfers");
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
    		System.out.println("NO FROM REFS+++++++++++++++++");
    	}
        for (String fromRef : fromRefs) {
            CMLAtom atom1 = atomMap1.get(fromRef);
            String id2 = atomMap.getToRef(fromRef);
            CMLAtom atom2 = atomMap2.get(id2);
            if (atom2 == null) {
            	System.out.println("NO MATCHED ATOM"+id2);
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
    	MoleculeDisplay moleculeDisplayx = (reactionDisplay == null) ? null :
    		reactionDisplay.getMoleculeDisplay();
    	enableReactionDisplay();
    	Transform2 transform2 = new Transform2(
    			new double[] {
    				1.,  0., 0.0,
    				0., -1., 0.0,
    				0.,  0., 1.}
    			);
    
    	reaction.debug("REACT");
    	List<CMLMolecule> molecules = reaction.getMolecules(Component.REACTANT);
    	if (molecules.size() == 0) {
    		System.out.println("No molecules to display");
    	} else if (applyScale) {
    		transform2 = scaleToBoundingBoxesAndScreenLimits(molecules);
    	}
    	
    	SVGElement g = createSVGElement(drawable, transform2);
    	g.setProperties(reactionDisplay);
    	MoleculeDisplay moleculeDisplay = reactionDisplay.getMoleculeDisplay();
    	displayMolecules(drawable, g, moleculeDisplay, molecules);
    	try {
    		drawable.output(g);
    	} catch (IOException e) {
    		throw new CMLRuntimeException(e);
    	}
    	return g;
    }

	private Transform2 scaleToBoundingBoxesAndScreenLimits(List<CMLMolecule> molecules) {
		Transform2 transform2 = null;
		try {
			Real2Range boundingBox = getBoundingBox(molecules);
			Real2Interval screenBoundingBox = reactionDisplay.getMoleculeDisplay().getScreenExtent();
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
			Real2Range molRange = moleculeTool.calculateBoundingBox();
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


    private void enableReactionDisplay() {
    	if (reactionDisplay == null) {
    		reactionDisplay = ReactionDisplay.getDEFAULT();
    	}
    }

    /** convenience method
     * 
     * @param i
     * @return i'th reactant
     */
    public CMLReactant getReactant(int i) {
    	return reaction.getReactantList().getReactantElements().get(i);
    }
    
    /** convenience method
     * 
     * @param i
     * @return i'th product
     */
    public CMLProduct getProduct(int i) {
    	return reaction.getProductList().getProductElements().get(i);
    }
}
