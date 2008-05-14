package org.xmlcml.cml.tools;

import java.awt.Color;

import org.xmlcml.euclid.Real2Interval;
import org.xmlcml.euclid.RealInterval;

/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class MoleculeDisplay extends AbstractDisplay {

	final static MoleculeDisplay DEFAULT = new MoleculeDisplay();
	static {
		DEFAULT.setDefaults();
	};
	
	private AtomDisplay atomDisplay = new AtomDisplay(AtomDisplay.DEFAULT);
	private BondDisplay bondDisplay = new BondDisplay(BondDisplay.DEFAULT);
	
	private double bondLength;
	private boolean displayFormula;
	private boolean displayLabels;
	private boolean displayNames;
	private Real2Interval screenExtent;
	
	/**
	 */
	public MoleculeDisplay() {
		super();
	}
	
	protected void init() {
	}
	
	protected void setDefaults() {
		super.setDefaults();

		bondLength = 2.0;
		displayFormula = false;
		displayLabels = false;
		displayNames = false;
		
		// molecule-specific
		backgroundColor = "white";
		color = "black";
		fill = "black";
		fontStyle = FONT_STYLE_NORMAL;
		fontFamily = FONT_SANS_SERIF;
		fontSize = 0.7;
		fontWeight = FONT_WEIGHT_NORMAL;
		
		omitHydrogens = true;
		screenExtent = new Real2Interval(
				new RealInterval(0., 200.), new RealInterval(0., 150.));
		stroke = null;
		showChildLabels = false;
	}

	/** copy
	 * @param a
	 */
	public MoleculeDisplay(MoleculeDisplay a) {
		super(a);
		this.bondLength = a.bondLength;
		this.displayFormula = a.displayFormula;
		this.displayLabels = a.displayLabels;
		this.displayNames = a.displayNames;
		this.screenExtent = a.screenExtent;
		
		this.atomDisplay = new AtomDisplay(a.atomDisplay);
		this.bondDisplay = new BondDisplay(a.bondDisplay);
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

	/**
	 * @return the backgroundColor
	 */
	public String getBackgroundColor() {
		return backgroundColor;
	}
	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	/**
	 * @return the screenExtent
	 */
	public Real2Interval getScreenExtent() {
		return screenExtent;
	}
	/**
	 * @param screenExtent the screenExtent to set
	 */
	public void setScreenExtent(Real2Interval screenExtent) {
		this.screenExtent = screenExtent;
	}
	/**
	 * @return the dEFAULT
	 */
	public static MoleculeDisplay getDEFAULT() {
		return DEFAULT;
	}
	/**
	 * @return the bondDisplay
	 */
	public BondDisplay getBondDisplay() {
		return bondDisplay;
	}
	/**
	 * @param bondDisplay the bondDisplay to set
	 */
	public void setBondDisplay(BondDisplay bondDisplay) {
		this.bondDisplay = bondDisplay;
	}
	/**
	 * @return the atomDisplay
	 */
	public AtomDisplay getAtomDisplay() {
		return atomDisplay;
	}
	/**
	 * @param atomDisplay the atomDisplay to set
	 */
	public void setAtomDisplay(AtomDisplay atomDisplay) {
		this.atomDisplay = atomDisplay;
	}
	
	/** cascades through from calling program
	 * @param args
	 * @param i
	 * @return increased i if args found
	 */
	public int processArgs(String[] args, int i) {
		
		if (false) {
			
			
		} else if (args[i].equalsIgnoreCase("-MOL_BONDLENGTH")) {
			this.setBondLength(new Double(args[++i])); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_DISPLAYFORMULA")) {
			this.setDisplayFormula(true); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_DISPLAYLABELS")) {
			this.setDisplayLabels(true); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_DISPLAYNAMES")) {
			this.setDisplayNames(true); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_SCREENEXTENT")) {
			System.err.println("SCREEN EXTENT NYI"); i++;
//			
		} else if (args[i].equalsIgnoreCase("-MOL_FONTSIZE")) {
			this.setFontSize(new Double(args[++i])); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_COLOR")) {
			this.setColor(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_FILL")) {
			this.setFill(args[++i]);
		} else if (args[i].equalsIgnoreCase("-MOL_STROKE")) {
			this.setStroke(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_OPACITY")) {
			this.setOpacity(new Double(args[++i])); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_FONTSTYLE")) {
			this.setFontStyle(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_FONTWEIGHT")) {
			this.setFontWeight(args[++i]);
		} else if (args[i].equalsIgnoreCase("-MOL_FONTFAMILY")) {
			this.setFontFamily(args[++i]); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_OMITHYDROGENS")) {
			this.setOmitHydrogens(true); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_SHOWHYDROGENS")) {
			this.setOmitHydrogens(false); i++;
		} else if (args[i].equalsIgnoreCase("-MOL_SHOWCHILDLABELS")) {
			this.setShowChildLabels(true); i++;
		}
		return i;
	}

	public static void usage() {
		
		System.out.println(" MoleculeDisplay options ");
		
		System.out.println("    -MOL_BONDLENGTH length(D)");
		System.out.println("    -MOL_DISPLAYFORMULA");
		System.out.println("    -MOL_DISPLAYLABELS");
		System.out.println("    -MOL_DISPLAYNAMES");
		System.out.println("    -MOL_SCREENEXTENT NYI");
		System.out.println("              ...");
		System.out.println("    -MOL_FONTSIZE size(D)");
		System.out.println("    -MOL_COLOR fontColor");
		System.out.println("    -MOL_FILL areaFill (includes text)");
		System.out.println("    -MOL_STROKE stroke (line but not text)");
		System.out.println("    -MOL_OPACITY opacity(D 0-MOL_1)");
		System.out.println("    -MOL_FONTSTYLE fontStyle");
		System.out.println("    -MOL_FONTWEIGHT fontWeight");
		System.out.println("    -MOL_FONTFAMILY fontFamily");
		System.out.println("    -MOL_OMITHYDROGENS");
		System.out.println("    -MOL_SHOWHYDROGENS");
		System.out.println("    -MOL_SHOWCHILDLABELS");
		System.out.println();
	}

	public boolean isDisplayFormula() {
		return displayFormula;
	}

	public void setDisplayFormula(boolean displayFormula) {
		this.displayFormula = displayFormula;
	}

	public boolean isDisplayLabels() {
		return displayLabels;
	}

	public void setDisplayLabels(boolean displayLabels) {
		this.displayLabels = displayLabels;
	}

	public boolean isDisplayNames() {
		return displayNames;
	}

	public void setDisplayNames(boolean displayNames) {
		this.displayNames = displayNames;
	}

	
}
