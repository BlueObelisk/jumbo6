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


/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class ReactionDisplay extends AbstractDisplay {

	final static ReactionDisplay DEFAULT = new ReactionDisplay();
	static {
		DEFAULT.moleculeDisplay = MoleculeDisplay.DEFAULT;
	};
	
	public enum Orientation {
		HORIZONTAL,
		VERTICAL,
	}
	
	protected Orientation reactant2ProductOrientation = Orientation.HORIZONTAL;
	protected Orientation reactantOrientation = Orientation.HORIZONTAL;
	protected Orientation productOrientation = Orientation.HORIZONTAL;

	/** constructor.
	 */
	public ReactionDisplay() {
		super();
	}
	/** copy.
	 * @param a
	 */
	public ReactionDisplay(ReactionDisplay a) {
		super(a);
		this.moleculeDisplay = new MoleculeDisplay(a.moleculeDisplay);
	}
	private MoleculeDisplay moleculeDisplay = new MoleculeDisplay(MoleculeDisplay.DEFAULT);
	protected boolean omitHydrogens;
	private Real2 scales = new Real2(1.0, 1.0);
	private String id;
	
	public Orientation getReactant2ProductOrientation() {
		return reactant2ProductOrientation;
	}
	public void setReactant2ProductOrientation(
			Orientation reactant2ProductOrientation) {
		this.reactant2ProductOrientation = reactant2ProductOrientation;
	}
	public Orientation getReactantOrientation() {
		return reactantOrientation;
	}
	public void setReactantOrientation(Orientation reactantOrientation) {
		this.reactantOrientation = reactantOrientation;
	}
	public Orientation getProductOrientation() {
		return productOrientation;
	}
	public void setProductOrientation(Orientation productOrientation) {
		this.productOrientation = productOrientation;
	}
	public void setScales(Real2 scales) {
		this.scales = scales;
	}
	public Real2 getScales() {
		return scales;
	}
	public void setScale(double scale) {
		this.scales = new Real2(scale, scale);
	}
	public void setScales(double scalex, double scaley) {
		this.scales = new Real2(scalex, scaley);
	}
	/**
	 * @return the moleculeDisplay
	 */
	public MoleculeDisplay getMoleculeDisplay() {
		return moleculeDisplay;
	}
	/**
	 * @param moleculeDisplay the moleculeDisplay to set
	 */
	public void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}
	/**
	 * @return default
	 */
	public static ReactionDisplay getDEFAULT() {
		return DEFAULT;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
}
