package org.xmlcml.cml.tools;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.euclid.EC;



/** display properties for bond.
 * 
 * @author pm286
 * 
 */
public class BondDisplay extends AbstractDisplay {

	private String multipleColor = "white";
	private double width = 0.7;
	private double scale = 1.0;
	private double doubleMiddleFactor = 0.9;
	private double wedgeFactor = 0.2;
	private int hatchCount = 6;
	private MoleculeDisplay moleculeDisplay;
	
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
//		LOG.debug("WID "+width+" SCA "+scale);
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
	
	/** can bond be omitted?
	 * 
	 * @param atom
	 * @return omit
	 */
	public boolean omitBond(CMLBond bond) {
		boolean omit = false;
		CMLAtom atom0 = bond.getAtom(0);
		CMLAtom atom1 = bond.getAtom(1);
		AtomTool atomTool0 = AtomTool.getOrCreateTool(atom0);
		AtomTool atomTool1 = AtomTool.getOrCreateTool(atom1);
		if (atomTool0.getAtomDisplay().omitAtom(atom0) ||
			atomTool1.getAtomDisplay().omitAtom(atom1)) {
			omit = true;
		}
		return omit;
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

//	public static void usage() {
//		
//		Util.sysout(" BondDisplay options ");
//		Util.sysout("    -BOND_MULTIPLECOLOR color");
//		Util.sysout("    -BOND_WIDTH width(D)");
//		Util.sysout("    -BOND_SCALE scale(D)");
//		Util.sysout("    -BOND_STROKE stroke");
//		Util.sysout("    -BOND_OPACITY opacity(D 0-BOND_1)");
//		Util.sysout("    -BOND_SHOWCHILDLABELS");
//		Util.sysout();
//	}
	
	public String getDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BondDisplay:");
		sb.append(EC.S_NEWLINE);
		sb.append(super.getDebugString());
		sb.append(EC.S_NEWLINE);
		
		sb.append("  multipleColor: "+multipleColor);
		sb.append(EC.S_NEWLINE);
		sb.append("  width:         "+width);
		sb.append(EC.S_NEWLINE);
		sb.append("  scale:         "+scale);
		sb.append(EC.S_NEWLINE);
		sb.append(EC.S_NEWLINE);
		return sb.toString();
	}

	public double getDoubleMiddleFactor() {
		return doubleMiddleFactor;
	}

	public void setDoubleMiddleFactor(double doubleMiddleFactor) {
		this.doubleMiddleFactor = doubleMiddleFactor;
	}

	public double getWedgeFactor() {
		return wedgeFactor;
	}

	public void setWedgeFactor(double wedgeFactor) {
		this.wedgeFactor = wedgeFactor;
	}

	public int getHatchCount() {
		return hatchCount;
	}

	public void setHatchCount(int hatchCount) {
		this.hatchCount = hatchCount;
	}

	public MoleculeDisplay getMoleculeDisplay() {
		return moleculeDisplay;
	}

	public void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}
}
