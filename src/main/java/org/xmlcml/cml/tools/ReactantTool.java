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

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAmount;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.ReactionComponent;

/**
 * tool for managing reactants
 *
 * @author pmr
 *
 */
public class ReactantTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(ReactantTool.class);

	public static final String ID_PREFIX = "reactant";

	CMLReactant reactant = null;

	/** constructor.
	 */
	public ReactantTool(CMLReactant reactant) throws RuntimeException {
		init();
		this.reactant = reactant;
	}


	void init() {
	}


	/**
	 * get reactant.
	 *
	 * @return the reactant or null
	 */
	public CMLReactant getReactant() {
		return this.reactant;
	}

    
	/** gets ReactantTool associated with reactant.
	 * if null creates one and sets it in reactant
	 * @param reactant
	 * @return tool
	 */
	public static ReactantTool getOrCreateTool(CMLReactant reactant) {
		ReactantTool reactantTool = (reactant == null) ? null : (ReactantTool) reactant.getTool();
		if (reactantTool == null) {
			reactantTool = new ReactantTool(reactant);
			reactant.setTool(reactantTool);
		}
		return reactantTool;
	}
	
	public void ensureId(String defaultId) {
		ensureId(reactant, defaultId);
	}

	static void ensureId(CMLElement element, String defaultId) {
		String id = element.getId();
		if (id == null) {
			element.setId(defaultId);
		}
	}

	public CMLAmount getMolarAmount() {
		return ReactantTool.getMolarAmount(reactant);
	}

	public double getCountNoDefault() {
		return ReactantTool.getCount(reactant);
	}

	public double getCountDefaultToUnity() {
		return ReactantTool.getCountDefaultToUnity(reactant);
	}
	
	public double getMolesPerCount() {
		return getMolesPerCount(reactant);
	}
	
	static CMLAmount getMolarAmount(CMLElement element) {
		Nodes amountNodes = element.query("./*[local-name()='"+CMLAmount.TAG+"' and @units='"+Units.MOL+"']");
		return (amountNodes.size() == 1) ? (CMLAmount) amountNodes.get(0) : null;
	}

	static double getCount(ReactionComponent rc) {
		double count = Double.NaN;
		if (rc instanceof CMLReactant) {
			count = ((CMLReactant)rc).getCount();
		} else if (rc instanceof CMLReactant) {
			count = ((CMLReactant)rc).getCount();
		}
		return count;
	}
	
	static double getCountDefaultToUnity(ReactionComponent rc) {
		double count = getCount(rc);
		return Double.isNaN(count) ? 1.0 : count;
	}

	static double getMolesPerCount(ReactionComponent rc) {
		double molesPerCount = Double.NaN;
		CMLAmount amount = null;
		double count = Double.NaN;
		if (rc instanceof CMLReactant) {
			ReactantTool reactantTool = ReactantTool.getOrCreateTool((CMLReactant)rc);
			amount = reactantTool.getMolarAmount();
			count = reactantTool.getCountDefaultToUnity();
		} else if (rc instanceof CMLProduct) {
			ProductTool productTool = ProductTool.getOrCreateTool((CMLProduct)rc);
			amount = productTool.getMolarAmount();
			count = productTool.getCountDefaultToUnity();
		}
		if (amount != null && !Double.isNaN(count)) {
			double moles = amount.getXMLContent();
			if (!Double.isNaN(moles) && moles >= EPS && count >= 0.00001) {
				molesPerCount = moles/count;
			}
		}
		return molesPerCount;
	}

};