package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElements;

/**
 * user-modifiable class supporting spectatorList. * autogenerated from schema
 * use as a shell which can be edited
 * 
 */
public class CMLSpectatorList extends AbstractSpectatorList implements
        ReactionComponent {

    /**
     * contructor.
     */
    public CMLSpectatorList() {
    }

    /**
     * contructor.
     * 
     * @param old
     */
    public CMLSpectatorList(CMLSpectatorList old) {
        super((AbstractSpectatorList) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLSpectatorList(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLSpectatorList
     */
    public static CMLSpectatorList makeElementInContext(Element parent) {
        return new CMLSpectatorList();

    }

    /**
     * gets CMLSpectatorList from SpectatorListTool.
     * 
     * gets molecules from all child spectators, distinguishing between
     * instances on product or reactant side
     * 
     * @param productReactant
     *            either SpectatorTool.PRODUCT or SpectatorTool.REACTANT
     * @return the molecules within all spectators or empty array if none
     */
    public List<CMLMolecule> getSpectatorMolecules(String productReactant) {
        Elements spectators = this.getChildCMLElements("spectator");
        int serial = (productReactant.equals(CMLReactant.TAG)) ? 0 : 1;
        List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
        for (int i = 0; i < spectators.size(); i++) {
            CMLMolecule molecule = (CMLMolecule) ((CMLSpectator) spectators
                    .get(i)).getChildCMLElement(CMLMolecule.TAG, serial);
            if (molecule != null) {
                moleculeList.add(molecule);
            }
        }
        return moleculeList;
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

    /** gets CMLSpectatorList from SpectatorListTool.
    *
    * gets  molecules from all child spectators, distinguishing between instances on
    * product or reactant side
    * @param productReactant either SpectatorTool.PRODUCT or SpectatorTool.REACTANT
    * @return the molecules within all spectators or empty array if none
    */
    public List<CMLMolecule> getMolecules(ReactionComponent.Type productReactant) {
        CMLElements<CMLSpectator> spectators = this.getSpectatorElements();
        int serial = (productReactant.equals(ReactionComponent.Type.REACTANT)) ? 0 : 1;
        List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
        for (CMLSpectator spectator : spectators) {
            CMLMolecule molecule = spectator.getMoleculeElements().get(serial);
            if (molecule != null) {
                moleculeList.add(molecule);
            }
        }
        return moleculeList;
    }
    

    /**
     * gets descendant reactionComponents. note that this will return all
     * containers as well as contained. thus calling this on: <reaction>
     * <reactantList> <reactant/> </reactantList> </reaction> will return 2
     * components, reactantList, followed by reactant.
     * 
     * @return empty if no components (some components such as CMLProduct will
     *         always return this)
     */
    public List<ReactionComponent> getReactionComponentDescendants() {
        return CMLReaction.getReactionComponentDescendants(this, true);
    }

    /**
     * gets child reactionComponents. note that this will return containers but
     * not their contents. thus calling this on: <reaction> <reactantList>
     * <reactant/> </reactantList> </reaction> will return 1 components,
     * reactantList.
     * 
     * @return empty if no components (some components such as CMLProduct will
     *         always return this)
     */
    public List<ReactionComponent> getReactionComponentChildren() {
        return CMLReaction.getReactionComponentDescendants(this, false);
    }

}
