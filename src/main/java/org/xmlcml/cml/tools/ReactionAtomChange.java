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

package org.xmlcml.cml.tools;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.graphics.SVGAnimate;
import org.xmlcml.cml.graphics.SVGAnimateTransform;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.euclid.Real2;

public class ReactionAtomChange extends ReactionChange {
	public static final double MINDIST = 1.0;
	
	double minDist;
	private Integer reactantCharge = null;
	private Integer productCharge = null;
	private CMLAtom reactantAtom = null;
	private CMLAtom productAtom = null;
	public void setMinDist(double minDist) {
		this.minDist = minDist;
	}

	private ReactionAtomChange(CMLAtom reactantAtom, CMLAtom productAtom) {
		setDefaults();
		this.reactantAtom = reactantAtom;
		this.productAtom  = productAtom;
		getAtomProperties();

	}

	private void setDefaults() {
		minDist = MINDIST;
	}

	private void getAtomProperties() {
		reactantXY = reactantAtom.getXY2();
		productXY = productAtom.getXY2();
		reactantCharge = getFormalCharge(reactantAtom);
		productCharge = getFormalCharge(productAtom);
		reactantOccupancy = getOccupancy(reactantAtom);
		productOccupancy = getOccupancy (productAtom);
		reactantElectrons = ElectronTool.getElectronCount(reactantAtom, ElectronTool.LONE_ELECTRONS);
		productElectrons = ElectronTool.getElectronCount(productAtom, ElectronTool.LONE_ELECTRONS);
	}


	private Integer getFormalCharge(CMLAtom atom) {
		Integer charge = null;
		if (atom != null && atom.getFormalChargeAttribute() != null) {
			charge = atom.getFormalCharge();
		}
		return charge;
	}

	private Double getOccupancy(CMLAtom atom) {
		Double occupancy = null;
		if (atom != null && atom.getOccupancyAttribute() != null) {
			occupancy = atom.getOccupancy();
		} else {
			occupancy = 1.0;
		}
		return occupancy;
	}

	public static ReactionAtomChange getReactionAtomChange(
			CMLAtom reactantAtom, CMLAtom productAtom) {
		if (reactantAtom == null && productAtom == null) {
			return null;
		}
		ReactionAtomChange atomChange = new ReactionAtomChange(reactantAtom, productAtom);
		if (!atomChange.hasDisplayChange() && 
			!atomChange.hasXY2Change()) {
			atomChange = null;
		}
		return atomChange;
		
	}
	
	boolean hasDisplayChange() {
		return hasTypeChange() || 
		hasOccupancyChange() ||
		hasElectronChange();
	}
	
	boolean hasTypeChange() {
		return hasChargeChange() /** || hasIsotopeChange()*/ ;
	}
	
	private boolean hasOccupancyChange() {
		boolean change = false;
		if (reactantOccupancy != null) {
			change = !reactantOccupancy.equals(productOccupancy);
		} else if (productOccupancy != null) {
			change = !productOccupancy.equals(reactantOccupancy);
		}
		return change;
	}

	boolean hasXY2Change() {
		boolean change = false;
		if (reactantXY != null && productXY != null) {
			double d = reactantXY.getDistance(productXY);
			change =  d > minDist;
		} 
		return change;
	}

	boolean hasChargeChange() {
		boolean change = false;
		if (reactantCharge != null) {
			change = !reactantCharge.equals(productCharge);
		} else if (productCharge != null) {
			change = !productCharge.equals(reactantCharge);
		}
		return change;
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
		
		if (hasXY2Change()) {
			animateXY2ForNonNullElement(svgReactant);
			animateXY2ForNonNullElement(svgProduct);
		}
	}


	public void animateXY2ForNonNullElement(SVGElement svgElement) {
		if (svgElement != null) {
			addTransformAnimate(svgElement, SVGAnimateTransform.TRANSLATE, reactantXY, productXY, null);
			addTransformAnimate(svgElement, SVGAnimateTransform.SCALE, new Real2(1.0, -1.0), new Real2(1.000001, -1.000001), SVGAnimate.SUM);
		}
	}

	private void addTransformAnimate(SVGElement svgElement, String type, Real2 from, Real2 to, String additive) {
		SVGAnimateTransform animateTransform = new SVGAnimateTransform();
		animateTransform.setTransform(type, from, to);
		animateTransform.setFill(SVGAnimate.FREEZE);
		if (additive != null) {
			animateTransform.setAdditive(additive);
		}
		svgElement.appendChild(animateTransform);
	}
	
	static Map<String, ReactionAtomChange> addAtomChangeById(CMLAtomSet productAtomSet, CMLAtomSet reactantAtomSet) {
		Map<String, ReactionAtomChange> atomChangeById = new HashMap<String, ReactionAtomChange>();
		addAtomChangeByProductId(atomChangeById, productAtomSet, reactantAtomSet);
		addAtomChangeByReactantId(atomChangeById, productAtomSet, reactantAtomSet);
		return atomChangeById;
	}

	private static void addAtomChangeByReactantId(Map<String, ReactionAtomChange> atomChangeById, 
			CMLAtomSet productAtomSet, CMLAtomSet reactantAtomSet) {
		for (CMLAtom reactantAtom : reactantAtomSet.getAtoms()) {
			String reactantId = reactantAtom.getId();
			CMLAtom productAtom = productAtomSet.getAtomById(reactantId);
			if (!atomChangeById.containsKey(reactantId)) {
				atomChangeById.put(reactantId, 
						ReactionAtomChange.getReactionAtomChange(reactantAtom, productAtom));
			}
		}
	}

	private static void addAtomChangeByProductId(Map<String, ReactionAtomChange> atomChangeById,
			CMLAtomSet productAtomSet, CMLAtomSet reactantAtomSet) {
		for (CMLAtom productAtom : productAtomSet.getAtoms()) {
			String productId = productAtom.getId();
			CMLAtom reactantAtom = reactantAtomSet.getAtomById(productId);
			atomChangeById.put(productId, 
					ReactionAtomChange.getReactionAtomChange(reactantAtom, productAtom));
		}
	}


	public SVGElement createAndAddReactantDisplay(SVGG g, MoleculeDisplayList displayList) {
		SVGElement svgReactant = createSVGElement(PRODUCT_P, this.reactantAtom, displayList);
		if ((productAtom == null || this.hasDisplayChange())  && svgReactant != null) {
			g.appendChild(svgReactant);
		}
		return svgReactant;
	}

	public SVGElement createAndAddProductDisplay(SVGG g, MoleculeDisplayList displayList) {
		SVGElement svgProduct = createSVGElement(PRODUCT_P, this.productAtom, displayList);
		if ((reactantAtom == null || this.hasDisplayChange()) && svgProduct != null) {
			g.appendChild(svgProduct);
		}
		return svgProduct;
	}

	public static SVGElement createSVGElement(String type, CMLAtom atom, MoleculeDisplayList displayList) {
		SVGElement atomElement = AtomTool.getOrCreateTool(atom).createGraphicsElement(displayList);
		if (atomElement != null) {
			atomElement.setId(type+"_"+atom.getId());
		}
		return atomElement;
	}
	
	static void cleanPositiveAndNegative(SVGElement svgElement) {
		/**
		<text x="-3.8499999999999996" y="4.07" style=" fill : #ff7700; stroke : none; font-size : 11.0;">Br</text>
		<circle cx="36.0" cy="-16.5" r="16.5" style=" fill : white; stroke : none; stroke-width : 0.5; opacity : 1.0;"/>
		<text x="25.5" y="-5.400000000000001" style=" fill : green; stroke : none; font-size : 30.0;">-</text>
transforms to
		<text x="-3.8499999999999996" y="4.07" style=" fill : #ff7700; stroke : none; font-size : 11.0;">Br-</text>

for PLUS the sign seems to have got lost, else the same
*/
		if (svgElement != null) {
			Nodes texts = svgElement.query("./*[local-name()='text']");
			Nodes circles = svgElement.query("./*[local-name()='circle']");
			if (texts.size() == 2 && circles.size() == 2) {
				Element elsym = (Element) texts.get(0);
				Element sign = (Element) texts.get(1);
				String elsymS = elsym.getValue();
				String signS = sign.getValue();
				// this is a bug? PLUS should be included
				if (signS.equals("")) {
					signS = "+";
				}
				if (signS.startsWith("-") || signS.startsWith("+")) {
					sign.detach();	
					circles.get(1).detach();
					elsym.getChild(0).detach();
					elsym.appendChild(elsymS+signS);
				}
			}
		}
	}



	public void processElectrons() {
		if (this.hasElectronChange()) {
			System.err.println(this.reactantAtom.getId()+": atom e "+this.getElectronChange());
		}
	}




}
