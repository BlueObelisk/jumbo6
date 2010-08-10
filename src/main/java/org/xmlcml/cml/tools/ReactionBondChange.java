package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.Map;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.graphics.SVGAnimate;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGLine;

public class ReactionBondChange extends ReactionChange {
	
	CMLBond reactantBond = null;
	CMLBond productBond  = null;
	private String reactantOrder;
	private String productOrder;
	private CMLAtom[] reactantAtom = new CMLAtom[2];
	private CMLAtom[] productAtom = new CMLAtom[2];
	private ReactionAtomChange[] reactionAtomChange = new ReactionAtomChange[2];

	private ReactionBondChange(CMLBond reactantBond, CMLBond productBond) {
		setDefaults();
		this.reactantBond = reactantBond;
		this.productBond  = productBond;
		getBondProperties(reactantBond, productBond);

	}

	private void setDefaults() {
	}

	private void getBondProperties(CMLBond reactantBond, CMLBond productBond) {
		reactantOrder = getNormalizedOrder(reactantBond);
		productOrder = getNormalizedOrder(productBond);
		reactantAtom[0] = (reactantBond == null) ? null : reactantBond.getAtom(0);
		reactantAtom[1] = (reactantBond == null) ? null : reactantBond.getAtom(1);
		productAtom[0] = (productBond == null) ? null : productBond.getAtom(0);
		productAtom[1] = (productBond == null) ? null : productBond.getAtom(1);
		reactionAtomChange[0] = ReactionAtomChange.getReactionAtomChange(reactantAtom[0], productAtom[0]);
		reactionAtomChange[1] = ReactionAtomChange.getReactionAtomChange(reactantAtom[1], productAtom[1]);
		reactantOccupancy = isZero(reactantOrder) ? 0.0 : 1.0;
		productOccupancy = isZero(productOrder) ? 0.0 : 1.0;
		reactantElectrons = ElectronTool.getElectronCount(reactantBond, ElectronTool.ELECTRONS);
		productElectrons = ElectronTool.getElectronCount(productBond, ElectronTool.ELECTRONS);
	}

	private String getNormalizedOrder(CMLBond bond) {
		String order = bond.getOrder();
		if (order == null) {
			order = CMLBond.UNKNOWN_ORDER;
		}
		return order;
	}
	
	public static ReactionBondChange getReactionBondChange(
			CMLBond reactantBond, CMLBond productBond) {
		if (reactantBond == null && productBond == null) {
			return null;
		}
		ReactionBondChange bondChange = new ReactionBondChange(reactantBond, productBond);
		if (bondChange.hasChange()) {
			return bondChange;	
		} else {
			return null;
		}
		
	}
	
	boolean hasDisplayChange() {
		return hasTypeChange() || hasOccupancyChange();
	}
	
	boolean hasTypeChange() {
		return hasNormalOrderChange();
	}

	boolean hasOccupancyChange() {
		return isZero(reactantOrder) || isZero(productOrder);
	}

	private boolean hasChange() {
		return hasTypeChange() ||
				reactionAtomChange[0] != null && reactionAtomChange[0].hasXY2Change() ||
				reactionAtomChange[1] != null && reactionAtomChange[1].hasXY2Change();
	}

	private boolean hasNormalOrderChange() {
		return !reactantOrder.equals(productOrder);
	}

	public void applyAnimation(SVGElement svgReactant, SVGElement svgProduct) {
		if (hasOccupancyChange()) {
			if (reactantOccupancy > productOccupancy) {
				svgProduct.detach();
				svgProduct = null;
				animateOpacity(svgReactant, reactantOccupancy, productOccupancy);
			} else {
				svgReactant.detach();
				svgReactant = null;
				animateOpacity(svgProduct, reactantOccupancy, productOccupancy);
			}
		} else {
			if (hasTypeChange()) {
				animateOpacity(svgReactant, 1.0, 0.0);
				animateOpacity(svgProduct, 0.0, 1.0);
			}
		}

		animateBondEnd(0, svgReactant, svgProduct);
		animateBondEnd(1, svgReactant, svgProduct);
	}

	private void animateBondEnd(int bondEnd, SVGElement svgReactant, SVGElement svgProduct) {
		ReactionAtomChange atomChange = reactionAtomChange[bondEnd];
		if (atomChange != null && atomChange.hasXY2Change()) {
			animateXY2ForNonNullElement(atomChange, bondEnd, svgReactant);
			if (svgProduct != null) {
				animateXY2ForNonNullElement(atomChange, bondEnd, svgProduct);
			}
		}
	}

	private void animateXY2ForNonNullElement(ReactionChange atomChange, int bondEnd, SVGElement svgComponent) {
		if (svgComponent != null) {
			for (int i = 0; i < svgComponent.getChildCount(); i++) {
				SVGElement childElement = (SVGElement) svgComponent.getChildElements().get(i);
				if (childElement.getLocalName().equals(SVGLine.TAG)) {
					addAnimateChild(atomChange, SVGElement.X+(bondEnd+1), 
							atomChange.reactantXY.getX(), atomChange.productXY.getX(), childElement);
					addAnimateChild(atomChange, SVGElement.Y+(bondEnd+1), 
							atomChange.reactantXY.getY(), atomChange.productXY.getY(), childElement);
				}
			}
		}
	}

	private void addAnimateChild(ReactionChange atomChange, String coordName, double coord0, double coord1,
			SVGElement svgComponent) {
		SVGAnimate svgAnimate = new SVGAnimate();
		svgAnimate.setFill(SVGAnimate.FREEZE);
		svgAnimate.setAttribute(coordName, coord0, coord1);
		svgComponent.appendChild(svgAnimate);
	}

	private boolean isZero(String order) {
		return order == null || order.equals("") || order.equals(CMLBond.ZERO);
	}

	static Map<String, ReactionBondChange> addBondChangeById(CMLBondSet productBondSet, CMLBondSet reactantBondSet) {
		Map<String, ReactionBondChange> bondChangeById = new HashMap<String, ReactionBondChange>();
		addBondChangeByProductId(bondChangeById, productBondSet, reactantBondSet);
		addBondChangeByReactantId(bondChangeById, productBondSet, reactantBondSet);
		return bondChangeById;
	}

	private static void addBondChangeByReactantId(Map<String, ReactionBondChange> bondChangeById, 
			CMLBondSet productBondSet, CMLBondSet reactantBondSet) {
		for (CMLBond reactantBond : reactantBondSet.getBonds()) {
			String reactantId = reactantBond.getId();
			CMLBond productBond = productBondSet.getBondById(reactantId);
			if (!bondChangeById.containsKey(reactantId)) {
				bondChangeById.put(reactantId, 
						ReactionBondChange.getReactionBondChange(reactantBond, productBond));
			}
		}
	}

	private static void addBondChangeByProductId(Map<String, ReactionBondChange> bondChangeById,
			CMLBondSet productBondSet, CMLBondSet reactantBondSet) {
		for (CMLBond productBond : productBondSet.getBonds()) {
			String productId = productBond.getId();
			CMLBond reactantBond = reactantBondSet.getBondById(productId);
			bondChangeById.put(productId, 
					ReactionBondChange.getReactionBondChange(reactantBond, productBond));
		}
	}


	public SVGElement createAndAddReactantDisplay(SVGG g, MoleculeDisplayList displayList) {
		SVGElement svgReactant = createSVGElement(PRODUCT_P, this.reactantBond, displayList);
		if ((productBond == null || this.hasDisplayChange()) && svgReactant != null) {
			g.appendChild(svgReactant);
		}
		return svgReactant;
	}

	public SVGElement createAndAddProductDisplay(SVGG g, MoleculeDisplayList displayList) {
		SVGElement svgProduct = createSVGElement(PRODUCT_P, this.productBond, displayList);
		if ((productBond == null || this.hasDisplayChange()) && svgProduct != null) {
			g.appendChild(svgProduct);
		}
		return svgProduct;
	}

	public static SVGElement createSVGElement(String type, CMLBond bond, MoleculeDisplayList displayList) {
		SVGElement bondElement = BondTool.getOrCreateTool(bond).createGraphicsElement(displayList);
		if (bondElement != null) {
			bondElement.setId(type+"_"+bond.getId());
		}
		return bondElement;
	}

	public void processElectrons() {
//		System.err.println("BB "+this.reactantBond.getId());
		if (this.hasElectronChange()) {
			System.err.println(this.reactantBond.getId()+": ...bond e "+this.getElectronChange());
		}
	}

}
