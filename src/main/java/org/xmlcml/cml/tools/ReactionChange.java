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

import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGAnimate;
import org.xmlcml.graphics.svg.SVGElement;

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
