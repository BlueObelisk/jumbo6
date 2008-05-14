package org.xmlcml.cml.tools;

import sun.security.action.GetBooleanAction;


/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class BondDisplay extends AbstractDisplay {

	private String multipleColor = "white";
	private double width = 1.0;
	private double scale = 1.0;
	
	final static BondDisplay DEFAULT = new BondDisplay();
	static {
		DEFAULT.setDefaults();
		
	};

	/** constructor.
	 */
	public BondDisplay() {
		super();
	}
	
	protected void init() {
	}
	
	protected void setDefaults() {
		super.setDefaults();
		//
		multipleColor = backgroundColor;
		scale = 1.0;
		width = 1.0;
		// 
		width = 0.08; // which?
		color = "black";
		stroke = null; // because bonds are filled lines
	}
	
	/** copy constructor.
	 * 
	 * @param a
	 */
	public BondDisplay(BondDisplay a) {
		super(a);
		this.multipleColor = a.multipleColor;
		this.scale = a.scale;
		this.width = a.width;
	}
	
	/**
	 * @return the multipleColor
	 */
	public String getMultipleColor() {
		return multipleColor;
	}
	/**
	 * @param multipleColor the multipleColor to set
	 */
	public void setMultipleColor(String multipleColor) {
		this.multipleColor = multipleColor;
	}
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}
	
	/**
	 * @return the scaled width
	 */
	public double getScaledWidth() {
//		System.out.println("WID "+width+" SCA "+scale);
		return width * scale;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	
	/**
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}
	/**
	 * @param width the width to set
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	/** cascades through from calling program
	 * @param args
	 * @param i
	 * @return increased i if args found
	 */
	public int processArgs(String[] args, int i) {
		// charge
		
		if (false) {
		} else if (args[i].equalsIgnoreCase("-BOND_MULTIPLECOLOR")) {
			this.setMultipleColor(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-BOND_WIDTH")) {
			this.setWidth(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-BOND_SCALE")) {
			this.setScale(new Double(args[++i]).doubleValue()); i++;
		} else if (args[i].equalsIgnoreCase("-BOND_STROKE")) {
			this.setStroke(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-BOND_OPACITY")) {
			this.setOpacity(new Double(args[++i])); i++;
		} else if (args[i].equalsIgnoreCase("-BOND_SHOWCHILDLABELS")) {
			this.setShowChildLabels(true); i++;
		}
		return i;
	}

	public static void usage() {
		
		System.out.println(" BondDisplay options ");
		System.out.println("    -BOND_MULTIPLECOLOR color");
		System.out.println("    -BOND_WIDTH width(D)");
		System.out.println("    -BOND_SCALE scale(D)");
		System.out.println("    -BOND_STROKE stroke");
		System.out.println("    -BOND_OPACITY opacity(D 0-BOND_1)");
		System.out.println("    -BOND_SHOWCHILDLABELS");
		System.out.println();
	}
}
