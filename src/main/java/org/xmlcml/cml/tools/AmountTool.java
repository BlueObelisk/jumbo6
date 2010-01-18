package org.xmlcml.cml.tools;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants.Units;
import org.xmlcml.cml.element.CMLAmount;

/**
 * tool for managing amounts
 *
 * @author pmr
 *
 */
public class AmountTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(AmountTool.class);

	CMLAmount amount = null;

	/** constructor.
	 */
	public AmountTool(CMLAmount amount) throws RuntimeException {
		init();
		this.amount = amount;
	}


	void init() {
	}


	/**
	 * get amount.
	 *
	 * @return the amount or null
	 */
	public CMLAmount getAmount() {
		return this.amount;
	}

    
	/** gets AmountTool associated with amount.
	 * if null creates one and sets it in amount
	 * @param amount
	 * @return tool
	 */
	public static AmountTool getOrCreateTool(CMLAmount amount) {
		AmountTool amountTool = (amount == null) ? null : (AmountTool) amount.getTool();
		if (amountTool == null) {
			amountTool = new AmountTool(amount);
			amount.setTool(amountTool);
		}
		return amountTool;
	}

	public static CMLAmount createMolarAmount(double mol) {
		CMLAmount amount = new CMLAmount();
		amount.setXMLContent(mol);
		amount.setUnits(Units.MOL.toString());
		return amount;
	}
	

	public static CMLAmount createMilliMolarAmount(double mmol) {
		return createMolarAmount(0.001 * mmol);
	}
};