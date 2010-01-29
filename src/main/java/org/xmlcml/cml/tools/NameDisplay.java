package org.xmlcml.cml.tools;



/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class NameDisplay extends AbstractFormulaIdNameDisplay {

	final static NameDisplay DEFAULT = new NameDisplay();
	static {
	};

	
	/** constructor.
	 */
	public NameDisplay() {
		super();
		setDefaults();
	}
	/** copy.
	 * @param a
	 */
	public NameDisplay(NameDisplay a) {
		super(a);
	}
	
	@Override
	protected void setDefaults() {
	}
}
