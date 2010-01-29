package org.xmlcml.cml.tools;



/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class FormulaDisplay extends AbstractFormulaIdNameDisplay {

	final static AbstractFormulaIdNameDisplay DEFAULT = new FormulaDisplay();
	static {
	};

	/** constructor.
	 */
	public FormulaDisplay() {
		super();
		setDefaults();
	}
	/** copy.
	 * @param a
	 */
	public FormulaDisplay(AbstractFormulaIdNameDisplay a) {
		super(a);
	}
}
