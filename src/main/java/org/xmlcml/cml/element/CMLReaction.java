package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLElement;

/**
 * user-modifiable class supporting reaction. * autogenerated from schema use as
 * a shell which can be edited
 *
 */
public class CMLReaction extends AbstractReaction implements ReactionComponent {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /** mappings */
    public enum Mapping {
        /** */
        FROM_SPECTATOR_PRODUCT_TO_REACTANT,
        /** */
        FROM_PRODUCT_TO_REACTANT,
        /** */
        MAP_REACTION_ATOM_MAP_COMPLETE,
        ;
    }

    /** not quite sure.*/
    public enum Index {
        /** reactant index CHECK */
        REACTANT_I(1),
        ;
        /** index*/
        public int index;
        private Index(int i) {
            index = i;
        }
    }
    
    private static String ANYTAG =
    	CMLReactant.TAG + " || "+
    	CMLProduct.TAG + " || "+
    	CMLSpectator.TAG;
    
    /** component type */
    public enum Component {
        /** */
        REACTANT(CMLReactant.TAG, 0),
        /** */
        PRODUCT(CMLProduct.TAG, 1),
        /** */
        SPECTATOR(CMLSpectator.TAG, 2),
        /** */
        ANY(ANYTAG, 3),
        /** */
        REACTANTLIST(CMLReactantList.TAG, 0),
        /** */
        PRODUCTLIST(CMLProductList.TAG, 1),
        /** */
        SPECTATORLIST(CMLSpectatorList.TAG, 2),
        /** */
        ANYLIST(ANYTAG, 3);
        /** */
        public String name;

        /**
         * symbolic integer, can be used in subscripts, etc.
         */
        public int number;

        private Component(String name, int number) {
            this.name = name;
            this.number = number;
        }
    }

    /**
     * Ignore bond orders in mapping.
     *
     */
    public final static String IGNORE_ORDER = "/IgnoreBondOrders";

    /**
     * a complete atom map for the reaction. often used as the dictRef value
     */
    public final static String MAP_REACTION_ATOM_MAP_COMPLETE = "REACTION ATOM MAP COMPLETE";

    /**
     * an incomplete atom map for the reaction. often used as the dictRef value
     */
    public final static String MAP_REACTION_ATOM_MAP_INCOMPLETE = "REACTION ATOM MAP INCOMPLETE";

    /**
     * direction of mapping
     *
     */
    public final static String FROM_PRODUCT_TO_REACTANT = "from product to reactant";

    /**
     * direction of mapping
     *
     */
    public final static String FROM_SPECTATOR_PRODUCT_TO_REACTANT = "from cmlSpectator product to reactant";

    /**
     * constructor.
     */
    public CMLReaction() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLReaction(CMLReaction old) {
        super((AbstractReaction) old);

    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLReaction(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLReaction
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLReaction();

    }

    final static Logger logger = Logger.getLogger(CMLReaction.class.getName());

    /**
     * gets an id for generic product or reactant context.
     *
     * For example used when there is no productList id or an atomSet
     * consistiing of product atoms
     *
     * @param type
     *            REACTANT or PRODUCT
     * @return an id including the reaction id and type
     */
    public String getId(String type) {
        return this.getId() + S_PERIOD + type;
    }

    /**
     * merge productLists into single productList.
     *
     */
    public void mergeProductLists() {
        mergePRLists(Component.PRODUCTLIST, Component.PRODUCT);
    }

    /**
     * merge reactantLists into single reactantList.
     *
     */
    public void mergeReactantLists() {
        mergePRLists(Component.REACTANTLIST, Component.REACTANT);
    }

    private void mergePRLists(Component prListC, Component prC) {
        Elements prLists = this.getChildCMLElements(prListC.name);
        if (prLists.size() > 1) {
            for (int i = 1; i < prLists.size(); i++) {
                CMLElement prList = (CMLElement) prLists.get(i);
                Elements prs = prList.getChildCMLElements(prC.name);
                for (int j = 0; j < prs.size(); j++) {
                    Element pr = prs.get(j);
                    pr.detach();
                    prLists.get(0).appendChild(pr);
                }
                prList.detach();
            }
        }
    }

    /**
     * convenience method to get ReactantList.
     *
     * merges ReactantLists
     *
     * @return the reactantListTool or null
     */
    public CMLReactantList getReactantList() {
        mergeReactantLists();
        CMLReactantList reactantList = (CMLReactantList) this
                .getFirstCMLChild(CMLReactantList.TAG);
        return reactantList;
    }

    /**
     * convenience method to get ProductList.
     *
     * merges ProductLists
     *
     * @return the productList or null
     */
    public CMLProductList getProductList() {
        mergeProductLists();
        CMLProductList productList = (CMLProductList) this
                .getFirstCMLChild(CMLProductList.TAG);
        return productList;
    }

    /**
     * gets filename from components of CMLReaction. uses
     * CMLName/@dictRef='cml:filename' content 
     * else reaction.getId()
     *
     * @return the filename or null
     */
    public String getFilename() {
        String s = null;
        Elements nameNodes = this.getChildCMLElements(CMLName.TAG);
        for (int i = 0; i < nameNodes.size(); i++) {
            CMLName name = (CMLName) nameNodes.get(i);
            if (name.getDictRef().equals(CMLReaction.CML_FILENAME)) {
                s = name.getXMLContent();
            }
        }
        return (s == null) ? this.getId() : s;
    }

    /**
     * gets cmlSpectator molecules in order.
     *
     * assumes order is spectators under spectatorList, each with two molecules,
     * identical in connection table but not necessarily ids or atom order
     *
     * @param reactOrProd
     *            0 for reactant spectators 1 for product
     * @return molecules in these spectators or null
     */
    public List<CMLMolecule> getSpectatorMolecules(int reactOrProd) {
        List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
        CMLSpectatorList spectatorList = (CMLSpectatorList) this
                .getFirstCMLChild(CMLSpectatorList.TAG);
        Elements spectators = spectatorList.getChildCMLElements("spectator");
        for (int i = 0; i < spectators.size(); i++) {
            Elements moleculeNodes = ((CMLElement) spectators.get(i))
                    .getChildCMLElements(CMLMolecule.TAG);
            moleculeList.add((CMLMolecule) moleculeNodes.get(reactOrProd));
        }
        return moleculeList;
    }

    /**
     * delete any substance with only a name. I think this is obsolescent...
     *
     */
    public void removeOrphanSubstances() {
        CMLSubstanceList substanceList = (CMLSubstanceList) this
                .getFirstCMLChild("substanceList");
        if (substanceList != null) {
            Elements substances = substanceList
                    .getChildCMLElements("substance");
            for (int i = 0; i < substances.size(); i++) {
                Elements childNodes = substances.get(i).getChildElements();
                if (childNodes.size() == 1
                        && childNodes.get(0) instanceof CMLName) {
                    substances.get(i).detach();
                }
            }
        }
    }


    /**
     * get all descendant atoms.
     *
     * @return list of descendant atoms
     */
    public List<CMLAtom> getAtoms() {
        return CMLReaction.getAtoms(this);
    }

    /**
     * get all descendant bonds.
     *
     * @return list of descendant bonds
     */
    public List<CMLBond> getBonds() {
        return CMLReaction.getBonds(this);
    }

    /**
     * get all descendant formulas.
     *
     * @return list of descendant formulas
     */
    public List<CMLFormula> getFormulas() {
        return CMLReaction.getFormulas(this);
    }

    /**
     * get all descendant molecules.
     *
     * @return list of descendant molecules
     */
    public List<CMLMolecule> getMolecules() {
        return CMLReaction.getMolecules(this);
    }

//    /**
//     * gets descendant reactionComponents. note that this will return all
//     * containers as well as contained. thus calling this on: <reaction>
//     * <reactantList> <reactant/> </reactantList> </reaction> will return 2
//     * components, reactantList, followed by reactant.
//     *
//     * @return empty if no components (some components such as CMLProduct will
//     *         always return this)
//     */
//    public List<ReactionComponent> getReactionComponentDescendants() {
//        return CMLReaction.getReactionComponentDescendants(this, true);
//    }
//
//    /**
//     * gets child reactionComponents. note that this will return containers but
//     * not their contents. thus calling this on: <reaction> <reactantList>
//     * <reactant/> </reactantList> </reaction> will return 1 components,
//     * reactantList.
//     *
//     * @return empty if no components (some components such as CMLProduct will
//     *         always return this)
//     */
//    public List<ReactionComponent> getReactionComponentChildren() {
//        return CMLReaction.getReactionComponentDescendants(this, false);
//    }

    /**
     * utility for any ReactionComponent classes.
     *
     * @param component
     * @return list of descendant atoms
     */
    static List<CMLAtom> getAtoms(ReactionComponent component) {
        List<CMLAtom> atomList = new ArrayList<CMLAtom>();
        List<CMLElement> elementList = ((CMLElement) component).getElements(".//"+CMLAtom.NS);
        for (CMLElement element : elementList) {
            atomList.add((CMLAtom) element);
        }
        return atomList;
    }

    /**
     * utility for any ReactionComponent classes.
     *
     * @param component
     * @return list of descendant bonds
     */
    static List<CMLBond> getBonds(ReactionComponent component) {
        List<CMLBond> bondList = new ArrayList<CMLBond>();
        List<CMLElement> elementList = ((CMLElement) component).getElements(".//"+CMLBond.NS);
        for (CMLElement element : elementList) {
            bondList.add((CMLBond) element);
        }
        return bondList;
    }

    /**
     * utility for any ReactionComponent classes.
     * requires <formula> element to exist
     * @param component
     * @return list of non-nested descendant formulas
     */
    static List<CMLFormula> getFormulas(ReactionComponent component) {
        List<CMLFormula> formulaList = new ArrayList<CMLFormula>();
        List<CMLElement> elementList = ((CMLElement) component).getElements(".//"+CMLFormula.NS);
        for (CMLElement element : elementList) {
            formulaList.add((CMLFormula) element);
        }
        return formulaList;
    }

    /**
     * utility for any ReactionComponent classes.
     * @param component
     * @return list of non-nested descendant formulas
     */
    static List<CMLFormula> getOrCreateFormulas(ReactionComponent component) {
    	
    	
//        List<CMLElement> elementList = ((CMLElement) component).getElements(".//"+CMLFormula.NS);
//        for (CMLElement element : elementList) {
        	// FIXME
//            formulaList.add((CMLFormula) element);
//        }
        return getFormulas(component);
    }

    /**
     * utility for any ReactionComponent classes.
     *
     * @param component
     * @return list of non-nested descendant molecules
     */
    static List<CMLMolecule> getMolecules(ReactionComponent component) {
        List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
        List<CMLElement> elementList = ((CMLElement) component).getElements(".//"+CMLMolecule.NS);
        for (CMLElement element : elementList) {
            moleculeList.add((CMLMolecule) element);
        }
        return moleculeList;
    }

    /**
     * utility for any ReactionComponent classes.
     *
     * @param component
     * @nested recurse to grandchildren
     * @return list of non-nested descendant molecules
     */
    static List<ReactionComponent> getReactionComponentDescendants(
            ReactionComponent component, boolean nested) {
        List<ReactionComponent> componentList = new ArrayList<ReactionComponent>();
        List<CMLElement> childElements = ((CMLElement) component)
                .getChildCMLElements();
        for (CMLElement child : childElements) {
            if (child instanceof ReactionComponent) {
                componentList.add((ReactionComponent) child);
                if (nested) {
                    List<ReactionComponent> descendantList = CMLReaction
                            .getReactionComponentDescendants(
                                    (ReactionComponent) child, nested);
                    componentList.addAll(descendantList);
                }
            }
        }
        return componentList;
    }

    /**
     * gets list of descendant reactants. convenience class
     *
     * @return list of descendant reactants
     */
    public List<CMLReactant> getDescendantReactants() {
        List<CMLElement> elems = this.getElements(".//"+CMLReactant.NS);
        List<CMLReactant> reactantList = new ArrayList<CMLReactant>();
        for (CMLElement elem : elems) {
            reactantList.add((CMLReactant) elem);
        }
        return reactantList;
    }

    /**
     * gets list of descendant products. convenience class
     *
     * @return list of descendant products
     */
    public List<CMLProduct> getDescendantProducts() {
        List<CMLElement> elems = this.getElements(".//"+CMLProduct.NS);
        List<CMLProduct> productList = new ArrayList<CMLProduct>();
        for (CMLElement elem : elems) {
            productList.add((CMLProduct) elem);
        }
        return productList;
    }

    /** gets all molecules of given type.
     * @param type (REACTANT, PRODUCT, SPECTATOR)
     * @return list of molecules
     */
    public List<CMLMolecule> getMolecules(Component type) {
    	String typeS = null;
    	if (Component.PRODUCT.equals(type)) {
    		typeS = "cml:product";
    	} else if (Component.REACTANT.equals(type)) {
    		typeS = "cml:reactant";
    	} else if (Component.SPECTATOR.equals(type)) {
    		typeS = "cml:spectator";
    	}
    	List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
    	Nodes nodes = null;
    	if (typeS == null) {
    	} else if(typeS.equals(CMLReaction.Component.ANY)) {
    		nodes = this.query(".//cml:molecule", CML_XPATH);
    	} else {
    		String qs = ".//"+typeS+"/cml:molecule";
    		nodes = this.query(qs, CML_XPATH);
    	}
    	if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				moleculeList.add((CMLMolecule) nodes.get(i));
			}
    	}
    	return moleculeList;
    }

    /**
     * @param type (REACTANT, PRODUCT, ANY, SPECTATOR)
     * @return list of atoms (assumed to be unique but no guarantee
     */
    public List<CMLAtom> getAtoms(Component type) {
    	List<CMLAtom> allAtomList = new ArrayList<CMLAtom>();
    	List<CMLMolecule> moleculeList = getMolecules(type);
    	for (CMLMolecule molecule : moleculeList) {
    		List<CMLAtom> atomList = molecule.getAtoms();
    		for (CMLAtom atom : atomList) {
    			allAtomList.add(atom);
    		}
    	}
    	return allAtomList;
    }

    /**
     * @param type (REACTANT, PRODUCT, ANY, SPECTATOR)
     * @return list of bonds (assumed to be unique but no guarantee
     */
    public List<CMLBond> getBonds(Component type) {
    	List<CMLBond> allBondList = new ArrayList<CMLBond>();
    	List<CMLMolecule> moleculeList = getMolecules(type);
    	for (CMLMolecule molecule : moleculeList) {
    		List<CMLBond> bondList = molecule.getBonds();
    		for (CMLBond bond : bondList) {
    			allBondList.add(bond);
    		}
    	}
    	return allBondList;
    }

    /**
     * gets list of descendant spectators. convenience class
     *
     * @return list of descendant spectators
     */
    public List<CMLSpectator> getDescendantSpectators() {
        List<CMLElement> elems = this.getElements(".//"+CMLSpectator.NS);
        List<CMLSpectator> spectatorList = new ArrayList<CMLSpectator>();
        for (CMLElement elem : elems) {
            spectatorList.add((CMLSpectator) elem);
        }
        return spectatorList;
    }

}
