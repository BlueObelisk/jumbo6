package org.xmlcml.cml.element;

import java.util.Stack;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.Text;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLNamespace;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;

/** user-modifiable class supporting CMLNodeFactory.
*
* autogenerated from schema
* use as a shell which can be edited

*/
public class CMLNodeFactory extends NodeFactory {

// fields;
    /** current of current node*/
    public Element current;
    /** current element stack*/
    public Stack<Element> stack = new Stack<Element>();
    /** must give simple documentation.
    *

    */

    public CMLNodeFactory() {
    }

    /** callback from element start tag.
    *
    * @param name element name
    * @param namespace namespace of element
    * @return Element
    */
    public Element startMakingElement(String name, String namespace) {
// fields;
        /** new element*/
        Element newElement;
        int idx = name.indexOf(CMLUtil.S_COLON);
        if (idx != -1) {
        	name = name.substring(idx+1);
        }
        // convert old namespaces
        namespace = CMLNamespace.guessNamespace(namespace);
        
        if (namespace.equals(null)) {
            newElement = new Element(name);
        } else if (namespace.trim().length() == 0) {
            // this seems to be what is passed if there is no namespace
            newElement = new Element(name);
        } else if (!namespace.equals(CMLUtil.CML_NS)) {
            newElement = new Element(name, namespace);
        } else if(name.equals("symmetry")) {
            newElement = org.xmlcml.cml.element.CMLSymmetry.makeElementInContext(current);
        } else if(name.equals("peakList")) {
            newElement = org.xmlcml.cml.element.CMLPeakList.makeElementInContext(current);
        } else if(name.equals("product")) {
            newElement = org.xmlcml.cml.element.CMLProduct.makeElementInContext(current);
        } else if(name.equals("tableCell")) {
            newElement = org.xmlcml.cml.element.CMLTableCell.makeElementInContext(current);
        } else if(name.equals("complexObject")) {
            newElement = org.xmlcml.cml.element.CMLComplexObject.makeElementInContext(current);
        } else if(name.equals("region")) {
            newElement = org.xmlcml.cml.element.CMLRegion.makeElementInContext(current);
        } else if(name.equals("point3")) {
            newElement = org.xmlcml.cml.element.CMLPoint3.makeElementInContext(current);
        } else if(name.equals("expression")) {
            newElement = org.xmlcml.cml.element.CMLExpression.makeElementInContext(current);
        } else if(name.equals("reactantList")) {
            newElement = org.xmlcml.cml.element.CMLReactantList.makeElementInContext(current);
        } else if(name.equals("actionList")) {
            newElement = org.xmlcml.cml.element.CMLActionList.makeElementInContext(current);
        } else if(name.equals("alternative")) {
            newElement = org.xmlcml.cml.element.CMLAlternative.makeElementInContext(current);
        } else if(name.equals("object")) {
            newElement = org.xmlcml.cml.element.CMLObject.makeElementInContext(current);
        } else if(name.equals("string")) {
            newElement = org.xmlcml.cml.element.CMLString.makeElementInContext(current);
        } else if(name.equals("tableHeader")) {
            newElement = org.xmlcml.cml.element.CMLTableHeader.makeElementInContext(current);
        } else if(name.equals("parameter")) {
            newElement = org.xmlcml.cml.element.CMLParameter.makeElementInContext(current);
        } else if(name.equals("reactionStep")) {
            newElement = org.xmlcml.cml.element.CMLReactionStep.makeElementInContext(current);
        } else if(name.equals("spectrumList")) {
            newElement = org.xmlcml.cml.element.CMLSpectrumList.makeElementInContext(current);
        } else if(name.equals("bondStereo")) {
            newElement = org.xmlcml.cml.element.CMLBondStereo.makeElementInContext(current);
        } else if(name.equals("peakStructure")) {
            newElement = org.xmlcml.cml.element.CMLPeakStructure.makeElementInContext(current);
        } else if(name.equals("atomParity")) {
            newElement = org.xmlcml.cml.element.CMLAtomParity.makeElementInContext(current);
        } else if(name.equals("mechanismComponent")) {
            newElement = org.xmlcml.cml.element.CMLMechanismComponent.makeElementInContext(current);
        } else if(name.equals("operator")) {
            newElement = org.xmlcml.cml.element.CMLOperator.makeElementInContext(current);
        } else if(name.equals("scalar")) {
            newElement = org.xmlcml.cml.element.CMLScalar.makeElementInContext(current);
        } else if(name.equals("array")) {
            newElement = org.xmlcml.cml.element.CMLArray.makeElementInContext(current);
        } else if(name.equals("stringArray")) {
            newElement = org.xmlcml.cml.element.CMLStringArray.makeElementInContext(current);
        } else if(name.equals("peakGroup")) {
            newElement = org.xmlcml.cml.element.CMLPeakGroup.makeElementInContext(current);
        } else if(name.equals("unitList")) {
            newElement = org.xmlcml.cml.element.CMLUnitList.makeElementInContext(current);
        } else if(name.equals("angle")) {
            newElement = org.xmlcml.cml.element.CMLAngle.makeElementInContext(current);
        } else if(name.equals("torsion")) {
            newElement = org.xmlcml.cml.element.CMLTorsion.makeElementInContext(current);
        } else if(name.equals("spectatorList")) {
            newElement = org.xmlcml.cml.element.CMLSpectatorList.makeElementInContext(current);
        } else if(name.equals("latticeVector")) {
            newElement = org.xmlcml.cml.element.CMLLatticeVector.makeElementInContext(current);
        } else if(name.equals("module")) {
            newElement = org.xmlcml.cml.element.CMLModule.makeElementInContext(current);
        } else if(name.equals("line3")) {
            newElement = org.xmlcml.cml.element.CMLLine3.makeElementInContext(current);
        } else if(name.equals("integerArray")) {
            newElement = org.xmlcml.cml.element.CMLIntegerArray.makeElementInContext(current);
        } else if(name.equals("kpointList")) {
            newElement = org.xmlcml.cml.element.CMLKpointList.makeElementInContext(current);
        } else if(name.equals("zMatrix")) {
            newElement = org.xmlcml.cml.element.CMLZMatrix.makeElementInContext(current);
        } else if(name.equals("observation")) {
            newElement = org.xmlcml.cml.element.CMLObservation.makeElementInContext(current);
        } else if(name.equals("enumeration")) {
            newElement = org.xmlcml.cml.element.CMLEnumeration.makeElementInContext(current);
        } else if(name.equals("fragment")) {
            newElement = org.xmlcml.cml.element.CMLFragment.makeElementInContext(current);
        } else if(name.equals("potentialForm")) {
            newElement = org.xmlcml.cml.element.CMLPotentialForm.makeElementInContext(current);
        } else if(name.equals("system")) {
            newElement = org.xmlcml.cml.element.CMLSystem.makeElementInContext(current);
        } else if(name.equals("name")) {
            newElement = org.xmlcml.cml.element.CMLName.makeElementInContext(current);
        } else if(name.equals("tableRowList")) {
            newElement = org.xmlcml.cml.element.CMLTableRowList.makeElementInContext(current);
        } else if(name.equals("arrayList")) {
            newElement = org.xmlcml.cml.element.CMLArrayList.makeElementInContext(current);
        } else if(name.equals("bondTypeList")) {
            newElement = org.xmlcml.cml.element.CMLBondTypeList.makeElementInContext(current);
        } else if(name.equals("action")) {
            newElement = org.xmlcml.cml.element.CMLAction.makeElementInContext(current);
        } else if(name.equals("vector3")) {
            newElement = org.xmlcml.cml.element.CMLVector3.makeElementInContext(current);
        } else if(name.equals("atomicBasisFunction")) {
            newElement = org.xmlcml.cml.element.CMLAtomicBasisFunction.makeElementInContext(current);
        } else if(name.equals("metadataList")) {
            newElement = org.xmlcml.cml.element.CMLMetadataList.makeElementInContext(current);
        } else if(name.equals("unit")) {
            newElement = org.xmlcml.cml.element.CMLUnit.makeElementInContext(current);
        } else if(name.equals("length")) {
            newElement = org.xmlcml.cml.element.CMLLength.makeElementInContext(current);
        } else if(name.equals("definition")) {
            newElement = org.xmlcml.cml.element.CMLDefinition.makeElementInContext(current);
        } else if(name.equals("reactiveCentre")) {
            newElement = org.xmlcml.cml.element.CMLReactiveCentre.makeElementInContext(current);
        } else if(name.equals("link")) {
            newElement = org.xmlcml.cml.element.CMLLink.makeElementInContext(current);
        } else if(name.equals("relatedEntry")) {
            newElement = org.xmlcml.cml.element.CMLRelatedEntry.makeElementInContext(current);
        } else if(name.equals("isotopeList")) {
            newElement = org.xmlcml.cml.element.CMLIsotopeList.makeElementInContext(current);
        } else if(name.equals("transitionState")) {
            newElement = org.xmlcml.cml.element.CMLTransitionState.makeElementInContext(current);
        } else if(name.equals("documentation")) {
            newElement = org.xmlcml.cml.element.CMLDocumentation.makeElementInContext(current);
        } else if(name.equals("plane3")) {
            newElement = org.xmlcml.cml.element.CMLPlane3.makeElementInContext(current);
        } else if(name.equals("spectator")) {
            newElement = org.xmlcml.cml.element.CMLSpectator.makeElementInContext(current);
        } else if(name.equals("atomSet")) {
            newElement = org.xmlcml.cml.element.CMLAtomSet.makeElementInContext(current);
        } else if(name.equals("identifier")) {
            newElement = org.xmlcml.cml.element.CMLIdentifier.makeElementInContext(current);
        } else if(name.equals("bondArray")) {
            newElement = org.xmlcml.cml.element.CMLBondArray.makeElementInContext(current);
        } else if(name.equals("floatArray")) {
            newElement = org.xmlcml.cml.element.CMLFloatArray.makeElementInContext(current);
        } else if(name.equals("lattice")) {
            newElement = org.xmlcml.cml.element.CMLLattice.makeElementInContext(current);
        } else if(name.equals("tableHeaderCell")) {
            newElement = org.xmlcml.cml.element.CMLTableHeaderCell.makeElementInContext(current);
        } else if(name.equals("table")) {
            newElement = org.xmlcml.cml.element.CMLTable.makeElementInContext(current);
        } else if(name.equals("reactionStepList")) {
            newElement = org.xmlcml.cml.element.CMLReactionStepList.makeElementInContext(current);
        } else if(name.equals("arg")) {
            newElement = org.xmlcml.cml.element.CMLArg.makeElementInContext(current);
        } else if(name.equals("potential")) {
            newElement = org.xmlcml.cml.element.CMLPotential.makeElementInContext(current);
        } else if(name.equals("xaxis")) {
            newElement = org.xmlcml.cml.element.CMLXaxis.makeElementInContext(current);
        } else if(name.equals("mechanism")) {
            newElement = org.xmlcml.cml.element.CMLMechanism.makeElementInContext(current);
        } else if(name.equals("atom")) {
            newElement = org.xmlcml.cml.element.CMLAtom.makeElementInContext(current);
        } else if(name.equals("fragmentList")) {
            newElement = org.xmlcml.cml.element.CMLFragmentList.makeElementInContext(current);
        } else if(name.equals("spectrumData")) {
            newElement = org.xmlcml.cml.element.CMLSpectrumData.makeElementInContext(current);
        } else if(name.equals("potentialList")) {
            newElement = org.xmlcml.cml.element.CMLPotentialList.makeElementInContext(current);
        } else if(name.equals("electron")) {
            newElement = org.xmlcml.cml.element.CMLElectron.makeElementInContext(current);
        } else if(name.equals("eigen")) {
            newElement = org.xmlcml.cml.element.CMLEigen.makeElementInContext(current);
        } else if(name.equals("parameterList")) {
            newElement = org.xmlcml.cml.element.CMLParameterList.makeElementInContext(current);
        } else if(name.equals("reactionScheme")) {
            newElement = org.xmlcml.cml.element.CMLReactionScheme.makeElementInContext(current);
        } else if(name.equals("float")) {
            newElement = org.xmlcml.cml.element.CMLFloat.makeElementInContext(current);
        } else if(name.equals("reactant")) {
            newElement = org.xmlcml.cml.element.CMLReactant.makeElementInContext(current);
        } else if(name.equals("yaxis")) {
            newElement = org.xmlcml.cml.element.CMLYaxis.makeElementInContext(current);
        } else if(name.equals("integer")) {
            newElement = org.xmlcml.cml.element.CMLInteger.makeElementInContext(current);
        } else if(name.equals("stmml")) {
            newElement = org.xmlcml.cml.element.CMLStmml.makeElementInContext(current);
        } else if(name.equals("property")) {
            newElement = org.xmlcml.cml.element.CMLProperty.makeElementInContext(current);
        } else if(name.equals("appinfo")) {
            newElement = org.xmlcml.cml.element.CMLAppinfo.makeElementInContext(current);
        } else if(name.equals("bond")) {
            newElement = org.xmlcml.cml.element.CMLBond.makeElementInContext(current);
        } else if(name.equals("tableContent")) {
            newElement = org.xmlcml.cml.element.CMLTableContent.makeElementInContext(current);
        } else if(name.equals("tableRow")) {
            newElement = org.xmlcml.cml.element.CMLTableRow.makeElementInContext(current);
        } else if(name.equals("formula")) {
            newElement = org.xmlcml.cml.element.CMLFormula.makeElementInContext(current);
        } else if(name.equals("particle")) {
            newElement = org.xmlcml.cml.element.CMLParticle.makeElementInContext(current);
        } else if(name.equals("substanceList")) {
            newElement = org.xmlcml.cml.element.CMLSubstanceList.makeElementInContext(current);
        } else if(name.equals("unitType")) {
            newElement = org.xmlcml.cml.element.CMLUnitType.makeElementInContext(current);
        } else if(name.equals("bondType")) {
            newElement = org.xmlcml.cml.element.CMLBondType.makeElementInContext(current);
        } else if(name.equals("reaction")) {
            newElement = org.xmlcml.cml.element.CMLReaction.makeElementInContext(current);
        } else if(name.equals("unitTypeList")) {
            newElement = org.xmlcml.cml.element.CMLUnitTypeList.makeElementInContext(current);
        } else if(name.equals("bondSet")) {
            newElement = org.xmlcml.cml.element.CMLBondSet.makeElementInContext(current);
        } else if(name.equals("map")) {
            newElement = org.xmlcml.cml.element.CMLMap.makeElementInContext(current);
        } else if(name.equals("productList")) {
            newElement = org.xmlcml.cml.element.CMLProductList.makeElementInContext(current);
        } else if(name.equals("substance")) {
            newElement = org.xmlcml.cml.element.CMLSubstance.makeElementInContext(current);
        } else if(name.equals("dimension")) {
            newElement = org.xmlcml.cml.element.CMLDimension.makeElementInContext(current);
        } else if(name.equals("sample")) {
            newElement = org.xmlcml.cml.element.CMLSample.makeElementInContext(current);
        } else if(name.equals("metadata")) {
            newElement = org.xmlcml.cml.element.CMLMetadata.makeElementInContext(current);
        } else if(name.equals("propertyList")) {
            newElement = org.xmlcml.cml.element.CMLPropertyList.makeElementInContext(current);
        } else if(name.equals("atomArray")) {
            newElement = org.xmlcml.cml.element.CMLAtomArray.makeElementInContext(current);
        } else if(name.equals("molecule")) {
            newElement = org.xmlcml.cml.element.CMLMolecule.makeElementInContext(current);
        } else if(name.equals("reactionList")) {
            newElement = org.xmlcml.cml.element.CMLReactionList.makeElementInContext(current);
        } else if(name.equals("peak")) {
            newElement = org.xmlcml.cml.element.CMLPeak.makeElementInContext(current);
        } else if(name.equals("cellParameter")) {
            newElement = org.xmlcml.cml.element.CMLCellParameter.makeElementInContext(current);
        } else if(name.equals("annotation")) {
            newElement = org.xmlcml.cml.element.CMLAnnotation.makeElementInContext(current);
        } else if(name.equals("atomType")) {
            newElement = org.xmlcml.cml.element.CMLAtomType.makeElementInContext(current);
        } else if(name.equals("entry")) {
            newElement = org.xmlcml.cml.element.CMLEntry.makeElementInContext(current);
        } else if(name.equals("atomTypeList")) {
            newElement = org.xmlcml.cml.element.CMLAtomTypeList.makeElementInContext(current);
        } else if(name.equals("band")) {
            newElement = org.xmlcml.cml.element.CMLBand.makeElementInContext(current);
        } else if(name.equals("cml")) {
            newElement = org.xmlcml.cml.element.CMLCml.makeElementInContext(current);
        } else if(name.equals("gradient")) {
            newElement = org.xmlcml.cml.element.CMLGradient.makeElementInContext(current);
        } else if(name.equals("list")) {
            newElement = org.xmlcml.cml.element.CMLList.makeElementInContext(current);
        } else if(name.equals("sphere3")) {
            newElement = org.xmlcml.cml.element.CMLSphere3.makeElementInContext(current);
        } else if(name.equals("transform3")) {
            newElement = org.xmlcml.cml.element.CMLTransform3.makeElementInContext(current);
        } else if(name.equals("kpoint")) {
            newElement = org.xmlcml.cml.element.CMLKpoint.makeElementInContext(current);
        } else if(name.equals("basisSet")) {
            newElement = org.xmlcml.cml.element.CMLBasisSet.makeElementInContext(current);
        } else if(name.equals("spectrum")) {
            newElement = org.xmlcml.cml.element.CMLSpectrum.makeElementInContext(current);
        } else if(name.equals("crystal")) {
            newElement = org.xmlcml.cml.element.CMLCrystal.makeElementInContext(current);
        } else if(name.equals("amount")) {
            newElement = org.xmlcml.cml.element.CMLAmount.makeElementInContext(current);
        } else if(name.equals("conditionList")) {
            newElement = org.xmlcml.cml.element.CMLConditionList.makeElementInContext(current);
        } else if(name.equals("isotope")) {
            newElement = org.xmlcml.cml.element.CMLIsotope.makeElementInContext(current);
        } else if(name.equals("abundance")) {
            newElement = org.xmlcml.cml.element.CMLAbundance.makeElementInContext(current);
        } else if(name.equals("matrix")) {
            newElement = org.xmlcml.cml.element.CMLMatrix.makeElementInContext(current);
        } else if(name.equals("label")) {
            newElement = org.xmlcml.cml.element.CMLLabel.makeElementInContext(current);
        } else if(name.equals("dictionary")) {
            newElement = org.xmlcml.cml.element.CMLDictionary.makeElementInContext(current);
        } else if(name.equals("description")) {
            newElement = org.xmlcml.cml.element.CMLDescription.makeElementInContext(current);
        } else if(name.equals("join")) {
            newElement = org.xmlcml.cml.element.CMLJoin.makeElementInContext(current);
        } else if(name.equals("bandList")) {
            newElement = org.xmlcml.cml.element.CMLBandList.makeElementInContext(current);
        } else if(name.equals("moleculeList")) {
            newElement = org.xmlcml.cml.element.CMLMoleculeList.makeElementInContext(current);
        } else {
            throw new CMLRuntimeException("Unknown CML element: "+name);
        }
        stack.push(current);
        current = newElement;
        return newElement;
    }
    /** callback from element end tag.
    *
    * @param element the context element
    * @return Nodes
    */
    public Nodes finishMakingElement(Element element) {
        Element parent = stack.pop();
        if (current instanceof CMLElement) {
            ((CMLElement) current).finishMakingElement(parent);
        }
        current = parent;
        Nodes nodes = new Nodes();
        nodes.append(element);
        return nodes;
    }
    /** callback from each attribute.
    *
    * @param name attribute name
    * @param URI attribute namespace
    * @param value attribute value
    * @param type attribute type (ignored)
    * @return Nodes
    */
    public Nodes makeAttribute(String name, String URI, String value, Attribute.Type type) {
        Nodes nodes = new Nodes();
        Attribute attribute = null;
        try {
            int prefixLoc = name.indexOf(":");
            if (URI != null && URI.trim().length() != 0 && prefixLoc != -1) {
        // namespaced non-cml attribute is allowed
                attribute = new Attribute(name, URI, value);
            } else if (current instanceof CMLElement) {
                CMLElement currentCML = (CMLElement) current;
                String attributeGroupName = currentCML.getAttributeGroupName(name);
                if (attributeGroupName == null) {
//                    throw new CMLRuntimeException("unknown attribute: "+name);
                	attribute = new Attribute(name, value);
//                    System.err.println("unknown attribute: "+name);
                } else {
                	attribute = CMLAttributeList.makeAttributeFromGroupName(attributeGroupName, value, URI);
                }
            } else if (prefixLoc == -1) {
        // non-prefixed non-cml element
                attribute = new Attribute(name, value);
            } else if (prefixLoc != -1) {
        // prefixed non-cml element
                attribute = new Attribute(name, URI, value);
            }
        } catch (CMLException e) {
            throw new CMLRuntimeException(""+e);
        }
        if (attribute != null) {
        	nodes.append(attribute);
        }
        return nodes;
    }
    /** FIXME text - needs to trap/check values.
    *
    * @param text String content
    * @return Nodes
    */
    public Nodes makeText(String text) {
        Nodes nodes = new Nodes();
        nodes.append(new Text(text));
        return nodes;
    }
    /** no-op.
    *
    * @param text String content

    */
    public static void main(String text) {
    }
}
