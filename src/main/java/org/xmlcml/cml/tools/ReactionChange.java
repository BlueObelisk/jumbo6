package org.xmlcml.cml.tools;

import org.xmlcml.cml.graphics.SVGAnimate;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.euclid.Real2;

public class ReactionChange {

	public static final String REACTANT_R = "r";
	public static final String PRODUCT_P = "p";

	public static void animateOpacity(SVGElement element, double from, double to) {
		SVGAnimate svgAnimate = new SVGAnimate();
		svgAnimate.setOpacity(from, to);
		svgAnimate.setFill(SVGAnimate.FREEZE);
		element.appendChild(svgAnimate);

	}

	protected Real2 productXY = null;
	protected Double reactantOccupancy = null;
	protected Double productOccupancy = null;
	protected Real2 reactantXY = null;
	protected int reactantElectrons;
	protected int productElectrons;
	
	protected boolean hasElectronChange() {
		return reactantElectrons != productElectrons;
	}

	protected int getElectronChange() {
		return productElectrons - reactantElectrons;
	}

}
