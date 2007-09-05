/**
 * 
 */
package org.xmlcml.cml.tools;

/**
 * Allows drawing chemical objects
 * @author pm286
 *
 */
public class Molecule2DParameters {

	private double bondLength = 2.0;
	private boolean omitHydrogens = true;

	/** new parameters
	 */
	public Molecule2DParameters() {
		
	}
	
	/**
	 * @return the bondLength
	 */
	public double getBondLength() {
		return bondLength;
	}
	/**
	 * @param bondLength the bondLength to set
	 */
	public void setBondLength(double bondLength) {
		this.bondLength = bondLength;
	}

	/**
	 * @return the omitHydrogens
	 */
	public boolean isOmitHydrogens() {
		return omitHydrogens;
	}

	/**
	 * @param omitHydrogens the omitHydrogens to set
	 */
	public void setOmitHydrogens(boolean omitHydrogens) {
		this.omitHydrogens = omitHydrogens;
	}

}
