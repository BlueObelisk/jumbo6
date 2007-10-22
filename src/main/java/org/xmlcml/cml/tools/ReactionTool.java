package org.xmlcml.cml.tools;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLSpectatorList;
import org.xmlcml.cml.element.ReactionComponent;
import org.xmlcml.cml.element.CMLFormula.Sort;
import org.xmlcml.cml.element.ReactionComponent.Type;
import org.xmlcml.euclid.Real2;

/**
 * too to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public class ReactionTool extends AbstractTool {

    Logger logger = Logger.getLogger(ReactionTool.class.getName());

    CMLReaction reaction = null;

	private CMLFormula aggregateReactantFormula;
	private CMLFormula aggregateProductFormula;
	private CMLFormula differenceFormula;

    /**
     * constructor.
     * 
     * @param reaction
     */
    public ReactionTool(CMLReaction reaction) {
        this.reaction = reaction;
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

    /**
     * output the reaction analysis to a string.
     * 
     * @return the string
     * @throws CMLException
     */
    public String analyzeReaction() throws CMLException {
        StringWriter w = new StringWriter();
        try {
            this.outputReaction(w);
            w.write(S_NL);
            this.outputBalance(w);
            w.write(S_NL);
            w.close();
        } catch (IOException ioe) {
            logger.severe("BUG " + ioe);
        }
        return w.toString();
    }

    /**
     * get all molecules on reactant or product side. includes cmlSpectator
     * 
     * @param reactantProduct
     *            ReactionComponent.Type.REACTANT or ReactionComponent.Type.PRODUCT
     * @return all molecules
     */
    public List<CMLMolecule> getMoleculesIncludingSpectators(
            ReactionComponent.Type reactantProduct) {
        List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
        if (reactantProduct.equals(Type.REACTANT)) {
            CMLReactantList reactantList = (CMLReactantList) reaction
                    .getFirstCMLChild(CMLReactantList.TAG);
            if (reactantList != null) {
                moleculeList = ReactionTool.getMolecules(reactantList);
            }
        } else if (reactantProduct.equals(Type.PRODUCT)) {
            CMLProductList productList = (CMLProductList) reaction
                    .getFirstCMLChild(CMLProductList.TAG);
            if (productList != null) {
                moleculeList = ReactionTool.getMolecules(productList);
            }
        }
        CMLSpectatorList spectatorList = (CMLSpectatorList) reaction
                .getFirstCMLChild(CMLSpectatorList.TAG);
        if (spectatorList != null) {
            List<CMLMolecule> moleculex = spectatorList
                    .getMolecules(reactantProduct);
            for (int i = 0; i < moleculex.size(); i++) {
                moleculeList.add(moleculex.get(i));
            }
        }
        return moleculeList;
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

    @SuppressWarnings("unused")
     private static CMLFormula getFormulaXX(ReactionComponent element) {
         CMLFormula formula = null;
         if (element instanceof CMLProduct) {
             formula = new CMLFormula(((CMLProduct)element).getMolecule());
         } else if (element instanceof CMLReactant) {
             formula = new CMLFormula(((CMLReactant)element).getMolecule());
         }
         return formula;
     }

    /**
     * gets all descendant molecules.
     * 
     * splits any giant molecules does NOT add hydrogens, etc.
     * 
     * @param element
     *            productList or reactantList
     * @return the descendant molecules or empty list
     */
    public static List<CMLMolecule> getMolecules(CMLElement element) {
        List<CMLMolecule> moleculeList = null;
        if (1 == 1) {
            throw new CMLRuntimeException("NYI");
        }
         String name = element.getLocalName();
         if (name.endsWith("List")) {
             name = name.substring(0, name.length() - 4);
         }
         CMLReaction reaction = (CMLReaction) element.getParent();
         String reactionID = reaction.getId();
         int count = 0;
         moleculeList = new ArrayList<CMLMolecule>();
         Elements prs = element.getChildCMLElements(name);
         for (int j = 0; j < prs.size(); j++) {
             CMLElement pr = (CMLElement) prs.get(j);
             Elements childMolecules = pr.getChildCMLElements(CMLMolecule.TAG);
             List<CMLMolecule> meVector = splitAndReorganizeMolecules(pr, childMolecules, count, reactionID);
             for (CMLMolecule mol : meVector) {
                 moleculeList.add(mol);
                 String id = pr.getAttributeValue("id"); 
                 if (id == null) {
                     id = reactionID+".p";
                 }
                 mol.setId(id+".m"+(++count));
             }
         }
        return moleculeList;
    }

    /**
     * gets all descendant atoms.
     * 
     * @param element
     * @return the descendant atoms.
     * @throws CMLException
     */
    public static List<CMLAtom> getAtoms(CMLElement element)
            throws CMLException {
        List<CMLAtom> atomList = new ArrayList<CMLAtom>();
        List<CMLMolecule> molecules = getMolecules(element);
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
     * @param element
     * @return the descendant bonds.
     * @throws CMLException
     */
    public static List<CMLBond> getBonds(CMLElement element)
            throws CMLException {
        List<CMLBond> bondList = new ArrayList<CMLBond>();
        List<CMLMolecule> molecules = getMolecules(element);
        for (CMLMolecule molecule : molecules) {
            List<CMLBond> bonds = molecule.getBonds();
            for (CMLBond bond : bonds) {
                bondList.add(bond);
            }
        }
        return bondList;
    }

    /**
     * recursively splits reactant and product molecules. NYI possibly obsolete
     * as we should require prperly formed reactions
     * 
     * @throws CMLException
     */
    public void partitionIntoMolecules() throws CMLException {

        reaction.mergeReactantLists();
        CMLReactantList reactantList = (CMLReactantList) reaction
                .getFirstCMLChild(CMLReactantList.TAG);
        if (reactantList == null) {
            // logger.info("no reactantListChild: " + this.getId());
            return;
        }
        // split into reactant molecules
        // List<CMLMolecule> reactantMolecules =
        // ReactionTool.getMolecules(reactantList); // FIXME what is this for?

        reaction.mergeProductLists();

        CMLProductList productList = (CMLProductList) reaction
                .getFirstCMLChild(CMLProductList.TAG);
        if (productList == null) {
            // logger.info("no productListChild: " + this.getId());
            return;
        }
        // split into product molecules
        // List<CMLMolecule> productMolecules =
        // ProductReactantList.getMolecules(productList); // FIXME what is this
        // for?

    }

    /**
     * Calculates the mapping of reactant(s) to product(s) as a list CMLLink
     * elements.
     * 
     * linking atoms to atoms and bonds to bonds. Still experimental.
     * 
     * @param reaction
     * @param control
     *            has no reactants and/or products
     * @return list of mappings (null if no mapping found)
     * @throws CMLException
     */
    public CMLList mapReactantsToProducts(CMLReaction reaction, String control)
            throws CMLException {

        if (control == null) {
            control = S_EMPTY;
        }

        // CMLReactantList reactantList = (CMLReactantList)
        // reaction.getFirstChild(CMLReactantList.TAG);
        CMLReactantList reactantList = (CMLReactantList) reaction
                .getFirstCMLChild(CMLReactantList.TAG);
        Elements reactants = reactantList.getChildCMLElements(CMLReactant.TAG);
        if (reactants.size() == 0) {
            throw new CMLException("No reactants");
        }
        // Molecule[] cdkReactantMolecule = new Molecule[reactant.length];
        CMLProductList productList = (CMLProductList) reaction
                .getFirstCMLChild(CMLProductList.TAG);
        Elements products = productList.getChildCMLElements(CMLProduct.TAG);
        if (products.size() == 0) {
            throw new CMLException("No products");
        }

        // logger.info("*** needs editing ***");
        throw new CMLException("Reaction mapping needs editing"); // FIXME
    }

    /**
     * translate reactants and products geometrically to overlap centroids.
     * 
     * experimental
     * 
     * @param spectatorList
     */
    public void translateSpectatorProductsToReactants(
            CMLSpectatorList spectatorList) {
        List<CMLMolecule> reactantSpectator = spectatorList
                .getSpectatorMolecules(CMLReactant.TAG);
        List<CMLMolecule> productSpectator = spectatorList
                .getSpectatorMolecules(CMLProduct.TAG);
        if (reactantSpectator.size() != 0 || productSpectator.size() != 0) {
            CMLAtomSet reactantAtomSet = AtomSetTool
                    .createAtomSet(reactantSpectator);
            CMLAtomSet productAtomSet = AtomSetTool
                    .createAtomSet(productSpectator);
            Real2 reactantCentroid = reactantAtomSet.getCentroid2D();
            Real2 productCentroid = productAtomSet.getCentroid2D();
            Real2 delta = reactantCentroid.subtract(productCentroid);
            productAtomSet.translate2D(delta);
        }
    }

    /**
     * split giant molecules into components.
     * 
     * @param parent
     *            reactant or product
     * @param childMolecules
     *            of the parent
     * @param count ??
     * @param id
     *            to add to each new molecule
     * @return list of split molecules
     */
    public static List<CMLMolecule> splitAndReorganizeMolecules(
            CMLElement parent, Elements childMolecules, int count, String id) {
        Element grandParent = (Element) parent.getParent();
        List<CMLMolecule> moleculeToolVector = new ArrayList<CMLMolecule>();
        for (int j = childMolecules.size() - 1; j >= 0; j--) {
            CMLMolecule molecule = (CMLMolecule) childMolecules.get(j);
            new ConnectionTableTool(molecule).partitionIntoMolecules();
            Elements molecules = molecule.getChildCMLElements(CMLMolecule.TAG);
            if (molecules.size() == 0) {
                moleculeToolVector.add(molecule);
            } else {
                // move new molecules to be children of reactant
                for (int k = molecules.size() - 1; k >= 0; k--) {
                    CMLMolecule childMolecule = (CMLMolecule) molecules.get(k);
                    childMolecule.detach();
                    CMLElement newParent = null;
                    String type = null;
                    if (parent.getLocalName().equals(CMLReactant.TAG)) {
                        newParent = new CMLReactant();
                        type = "r";
                        ((CMLReactant) newParent).setId(id + S_PERIOD + type
                                + (k + 1 + count));
                    } else if (parent.getLocalName().equals(CMLProduct.TAG)) {
                        newParent = new CMLProduct();
                        type = "p";
                        ((CMLProduct) newParent).setId(id + S_PERIOD + type
                                + (k + 1 + count));
                    } else {
                        throw new CMLRuntimeException("BUG: " + parent.getLocalName());
                    }
                    newParent.appendChild(childMolecule);
                    grandParent.appendChild(newParent);

                    moleculeToolVector.add(molecule);
                    childMolecule.setId(id + S_PERIOD + type + (k + 1 + count) + S_PERIOD
                            + "m1");
                }
                molecule.detach();
                parent.detach();
            }
        }
        return moleculeToolVector;
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
}
