// /*======AUTOGENERATED FROM SCHEMA; DO NOT EDIT BELOW THIS LINE ======*/
package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

/** A container for one or more molecules.
*
* 
* \n \nmoleculeList can contain several molecules. \nThese may be related in many ways and there is are controlled\n semantics. However it should not be used for a molecule\n consisting of descendant molecules for which molecule\n should be used.\n A moleculeList can contain nested moleculeLists.\n 
* 
* user-modifiable class autogenerated from schema if no class exists
* use as a shell which can be edited
* the autogeneration software will not overwrite an existing class file

*/
public class CMLMoleculeList extends org.xmlcml.cml.element.AbstractMoleculeList {

    /** argument name to identify id.
     */
    public final static String IDX = "idx";
    
    /** must give simple documentation.
    *

    */

    public CMLMoleculeList() {
    }
    /** must give simple documentation.
    *
    * @param old CMLMoleculeList to copy

    */

    public CMLMoleculeList(CMLMoleculeList old) {
        super((org.xmlcml.cml.element.AbstractMoleculeList) old);
    }

    /** copy node .
    *
    * @return Node
    */
    public Node copy() {
        return new CMLMoleculeList(this);
    }
    /** create new instance in context of parent, overridable by subclasses.
    *
    * @param parent parent of element to be constructed (ignored by default)
    * @return CMLMoleculeList
    */
    public static CMLMoleculeList makeElementInContext(Element parent) {
        return new CMLMoleculeList();
    }
    
//    /** iterate molecules children and process.
//     * all child molecules are assumed to be joined
//     * for first molecule only looks for right join (left is error)
//     * for last molecule only looks for left join (right is error)
//     * for all others looks for both. At present adds default joins
//     *  
//     * THIS IS MESSY AND NEEDS REWRITE PROBABLY
//     */
//    private void processChildMolecules1() {
//        this.debug("PROCESS JOIN MOLECULES");
//        Elements moleculeElements = this.getChildCMLElements(CMLMolecule.TAG);
//        int nMolecules = moleculeElements.size();
//        // join direct molecule children
//        for (int i = 0; i < nMolecules; i++) {
//            CMLMolecule molecule = (CMLMolecule) moleculeElements.get(i);
//            molecule.debug("====SUBMOL=====");
//            MoleculeTool moleculeTool = new MoleculeTool(molecule);
//            // process branches recursively
//            List<CMLMoleculeList> branchingMoleculeListList = moleculeTool.getBranchingJoinList();
//            for (CMLMoleculeList branchingMoleculeList : branchingMoleculeListList) {
//                System.out.println("+++++++++++++BR+++++++++++");
//                branchingMoleculeList.processChildMolecules1();
//            }
//            CMLMolecule leftMolecule = (i == 0) ? null :
//                (CMLMolecule) moleculeElements.get(i-1);
//            // joins without child molecules
//            CMLMoleculeList moleculeList = getChildlessMoleculeList(molecule);
//            CMLMoleculeList leftMoleculeList = getChildlessMoleculeList(leftMolecule);
//            CMLJoin newJoin = null;
//            if (i == 0) {
//                newJoin = processFirstMolecule(molecule, moleculeList, nMolecules);
//            } else if (i < nMolecules-1) {
//                newJoin = processMiddleMolecule(molecule, moleculeList, leftMolecule, leftMoleculeList);
//            } else {
//                newJoin = processLastMolecule(molecule, moleculeList, leftMolecule, leftMoleculeList);
//            }
//            
//            // transfer contents of previous join
//            int idx = this.indexOf(newJoin);
//            if (idx > 0) {
//                if (this.getChild(idx-1) instanceof CMLJoin) {
//                    CMLJoin previousJoin = (CMLJoin) this.getChild(idx-1);
//                    int previousChildCount = previousJoin.getChildCount();
//                    for (int j = previousChildCount-1; j >= 0; j--) {
//                        Node child = previousJoin.getChild(j);
//                        child.detach();
//                        newJoin.appendChild(child);
//                    }
//                    // transfer order
//                    String order = previousJoin.getOrder();
//                    if (order != null) {
//                        newJoin.setOrder(order);
//                    }
//                }
//            }
//        }
//        this.tidyLeftAndRightAttributes(moleculeElements);
//        // remove all joins not join/@atomRefs2
//        this.removeNonAtomRefs2();
//
//    }
//    
//    /** iterate molecules children and process.
//     * all child molecules are assumed to be joined
//     * for first molecule only looks for right join (left is error)
//     * for last molecule only looks for left join (right is error)
//     * for all others looks for both. At present adds default joins
//     *  
//     *  @param parent
//     * THIS IS MESSY AND NEEDS REWRITE PROBABLY
//     */
//    private void processChildMolecules(CMLMolecule parent) {
//        this.debug("PROCESS JOIN MOLECULES");
//        Elements moleculeElements = this.getChildCMLElements(CMLMolecule.TAG);
//        int nMolecules = moleculeElements.size();
//        // join direct molecule children
//        for (int i = 0; i < nMolecules; i++) {
//            CMLMolecule molecule = (CMLMolecule) moleculeElements.get(i);
//            molecule.debug("====SUBMOL=====");
//            MoleculeTool moleculeTool = new MoleculeTool(molecule);
//            // process branches recursively
//            List<CMLMoleculeList> branchingMoleculeListList = moleculeTool.getBranchingJoinList();
//            for (CMLMoleculeList branchingMoleculeList : branchingMoleculeListList) {
//                System.out.println("+++++++++++++BR+++++++++++");
//                branchingMoleculeList.processChildMolecules(molecule);
//            }
//            CMLMolecule leftMolecule = (i == 0) ? null :
//                (CMLMolecule) moleculeElements.get(i-1);
//            // joins without child molecules
//            CMLMoleculeList moleculeList = getChildlessMoleculeList(molecule);
//            CMLMoleculeList leftMoleculeList = getChildlessMoleculeList(leftMolecule);
//            CMLJoin newJoin = null;
//            if (i == 0) {
//                newJoin = processFirstMolecule(molecule, moleculeList, nMolecules);
//            } else if (i < nMolecules-1) {
//                newJoin = processMiddleMolecule(molecule, moleculeList, leftMolecule, leftMoleculeList);
//            } else {
//                newJoin = processLastMolecule(molecule, moleculeList, leftMolecule, leftMoleculeList);
//            }
//            
//            // transfer contents of previous join
//            int idx = this.indexOf(newJoin);
//            if (idx > 0) {
//                if (this.getChild(idx-1) instanceof CMLJoin) {
//                    CMLJoin previousJoin = (CMLJoin) this.getChild(idx-1);
//                    int previousChildCount = previousJoin.getChildCount();
//                    for (int j = previousChildCount-1; j >= 0; j--) {
//                        Node child = previousJoin.getChild(j);
//                        child.detach();
//                        newJoin.appendChild(child);
//                    }
//                    // transfer order
//                    String order = previousJoin.getOrder();
//                    if (order != null) {
//                        newJoin.setOrder(order);
//                    }
//                }
//            }
//        }
//        this.tidyLeftAndRightAttributes(moleculeElements);
//        // remove all joins not join/@atomRefs2
//        this.removeNonAtomRefs2();
//
//    }
    
//    private CMLJoin processFirstMolecule(CMLMolecule molecule, CMLMoleculeList moleculeList, int nMolecules) {
//        CMLJoin join = null;
//        // left molecule, special case
//        // if first in a branch, make sure we have a left join
//        String linkOnParent = CMLLabel.getLabelValue(this, Position.PARENT);
//        String leftLabel = CMLLabel.getLabelValue(molecule, Position.LEFT);
//        if (linkOnParent != null) {
//            if (leftLabel == null) {
//                throw new CMLRuntimeException("need left link for leading branch on: "+molecule.getId());
//            } else {
//                CMLMolecule parentMolecule = this.getParentMolecule();
//                join = createAndAddJoinAtomRefs2(
//                        this, moleculeList, parentMolecule, molecule);
//                if (nMolecules == 1) {
//                    moleculeList.detach();
//                }
//            }
//        } else if (leftLabel != null) {
//            throw new CMLRuntimeException("left link without preceding molecule or parent: "+molecule.getId());
//        } else {
////            this.debug("QQQ");
////            throw new CMLRuntimeException("lefthand molecule cannot have left link:"+leftLabel+":");
//        }
//        if (nMolecules > 1 && CMLLabel.getLabelValue(molecule, Position.RIGHT) == null) {
//            molecule.debug("LABEL");
//            throw new CMLRuntimeException("missing right label for: "+molecule.getId());
//        }
//        return join;
//    }

//    private CMLJoin processMiddleMolecule(
//        CMLMolecule molecule, CMLMoleculeList moleculeList,
//        CMLMolecule leftMolecule, CMLMoleculeList leftMoleculeList
//        ) {
//        if (CMLLabel.getLabelValue(molecule, Position.LEFT) == null) {
//            throw new CMLRuntimeException("missing left label on: "+molecule.getId());
//        }
//        if (CMLLabel.getLabelValue(molecule, Position.RIGHT) == null) {
//            throw new CMLRuntimeException("missing right label on: "+molecule.getId());
//        }
//        CMLJoin newJoin = createAndAddJoinAtomRefs2(leftMoleculeList, moleculeList, leftMolecule, molecule);
//        leftMoleculeList.detach();
//        return newJoin;
//    }
//    
//    private CMLJoin processLastMolecule(
//            CMLMolecule molecule, CMLMoleculeList moleculeList,
//            CMLMolecule leftMolecule, CMLMoleculeList leftMoleculeList
//           ) {
//        // right molecule, special case
//        if (CMLLabel.getLabelValue(molecule, Position.RIGHT) != null) {
//            throw new CMLRuntimeException("last molecule cannot have right link:"+
//                    CMLLabel.getLabelValue(molecule, Position.RIGHT)+":");
//        }
//        if (CMLLabel.getLabelValue(molecule, Position.LEFT) == null) {
//            throw new CMLRuntimeException("last molecule must have left link: "+ molecule.getId());
//        }
//        CMLJoin newJoin = createAndAddJoinAtomRefs2(
//                leftMoleculeList, moleculeList, leftMolecule, molecule);
//        leftMoleculeList.detach();
//        moleculeList.detach();
//        return newJoin;
//    }
    
//    private CMLJoin createAndAddJoinAtomRefs2(
//        CMLMoleculeList leftMoleculeList, CMLMoleculeList moleculeList, 
//        CMLMolecule leftMolecule, CMLMolecule molecule) {
//        // create new join with atomRefs2
//        CMLJoin join = new CMLJoin();
//        String linkOnParent = CMLLabel.getLabelValue(leftMoleculeList, Position.PARENT);
//        String leftJoinRight = CMLLabel.getLabelValue(leftMolecule, Position.RIGHT);
//        String right = null;
//        if (linkOnParent != null) {
//            right = linkOnParent;
//        } else if (leftJoinRight != null) {
//            right = leftJoinRight;
//        } else {
//            this.debug("JOINX");
//            throw new CMLRuntimeException("left join has no explict branch or right: "+molecule.getId());
//        }
//        // transfer order
//        String order = this.getOrder();
//        if (order != null) {
//            join.setOrder(order);
//        }
//        Nodes nodes = leftMolecule.query("cml:arg[@name='"+IDX+"']",X_CML);
//        String leftArg = ((CMLArg)nodes.get(0)).getValue();
//        nodes = molecule.query("cml:arg[@name='"+IDX+"']",X_CML);
//        if (nodes.size() == 0) {
//            molecule.debug("NODES");
//            throw new CMLRuntimeException("No NODES: ");
//        }
//        String thisArg = ((CMLArg)nodes.get(0)).getValue();
//        String[] atomRefs2 = new String[]{
//            leftMolecule.getRef()+S_UNDER+leftArg+S_UNDER+right, 
//            molecule.getRef()+S_UNDER+thisArg+S_UNDER+CMLLabel.getLabelValue(molecule, Position.LEFT)};
//        join.setAtomRefs2(atomRefs2);
//        join.setId(atomRefs2[0]+S_UNDER+atomRefs2[1]);
//        int idx = this.indexOf(molecule);
//        this.insertChild(join, idx);
//        return join;
//    }
//    
//    private static CMLMoleculeList getChildlessMoleculeList(CMLMolecule molecule) {
//        CMLMoleculeList moleculeList = null;
//        if (molecule != null) {
//            Nodes nodes = molecule.query("cml:moleculeList[count(*) = 0]", X_CML);
//            if (nodes.size() == 0) {
//                moleculeList = new CMLMoleculeList();
//                molecule.appendChild(moleculeList);
//            } else if (nodes.size() == 1) {
//                moleculeList = (CMLMoleculeList) nodes.get(0);
//            } else {
//                molecule.debug("JOIN-1");
//                throw new CMLRuntimeException("Exactly 1 join child required");
//            }
//        }
//        return moleculeList;
//    }
//
//    /** remove and flatten joins without atomRefs2.
//     * currently used in building molecules
//     * @param moleculeList parent
//     */
//    private void removeNonAtomRefs2() {
//        Nodes nodes = 
//            this.query(".//cml:join[not(@atomRefs2)]", X_CML);
//        for (int i = 0; i < nodes.size(); i++) {
//            CMLJoin join1 = (CMLJoin) nodes.get(i);
//            join1.replaceByChildren();
//        }
//        this.replaceByChildren();
//    }
//    
//    private void tidyLeftAndRightAttributes(Elements moleculeElements) {
//        for (int i = 0; i < moleculeElements.size(); i++) {
//            CMLMolecule molecule = (CMLMolecule) moleculeElements.get(i);
//            CMLLabel.removeLabel(molecule, CMLLabel.Position.LEFT);
//            CMLLabel.removeLabel(molecule, CMLLabel.Position.RIGHT);
//       }
//    }
//
////    /** getLinkOnParent.
////     * 
////     * @return value
////     */
////    public String getLinkOnParent() {
////        return this.getAttributeValue("linkOnParent");
////    }
////    
////    /** setLinkOnParent.
////     * 
////     * @param link
////     */
////    public void setLinkOnParent(String link) {
////        this.addAttribute(new Attribute("linkOnParent", link));
////    }
    
//    private String getOrder() {
//        return "DUMMYORDER";
//    }
//    
//    private void setOrder(String ss) {
////        return "DUMMYORDER";
//    }
    
//    /** gets parent.
//     * checks it is a CMLMolecule.
//     * @return the molecule
//     * @exception CMLRuntimeException if parent is not a molecule
//     */
//    private CMLMolecule getParentMolecule() {
//        Node parent = this.getParent();
//        if (!(parent instanceof CMLMolecule)) {
//            throw new CMLRuntimeException("join must have parent molecule");
//        }
//        return (CMLMolecule) parent;
//    }

    /** example code.
     */
    public static void example() {
        CMLMoleculeList moleculeList = new CMLMoleculeList();
        CMLMolecule molecule = new CMLMolecule();
        moleculeList.addMolecule(molecule);
        moleculeList.debug("moleculeList");
    }

    /** runs example.
     * @param args
     */
    public static void main(String[] args) {
        example();
    }
    
}
