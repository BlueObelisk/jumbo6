package org.xmlcml.cml.tools;

import static org.xmlcml.euclid.EuclidConstants.S_NEWLINE;

import org.xmlcml.cml.base.CMLElement.CoordinateType;
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
	
	private AtomDisplay defaultAtomDisplay = new AtomDisplay(AtomDisplay.DEFAULT);
	private BondDisplay defaultBondDisplay = new BondDisplay(BondDisplay.DEFAULT);
	
	private double bondLength;
	private double hydrogenLengthFactor;
	private Boolean contractGroups;
	
	private boolean displayFormula;
	private boolean displayLabels;
	private boolean displayNames;
	private Real2Interval screenExtent;
	private MoleculeTool moleculeTool;
	protected boolean omitHydrogens;
	
	/**
	 */
	public MoleculeDisplay() {
		super();
	}
	
	/**
	 */
	public MoleculeDisplay(MoleculeTool moleculeTool) {
		this();
		this.setMoleculeTool(moleculeTool);
	}
	
	protected void init() {
	}
	
	protected void setDefaults() {
		super.setDefaults();

		bondLength = 50.0;
		hydrogenLengthFactor = 0.7;
		contractGroups = false;
		
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
		this.hydrogenLengthFactor = a.hydrogenLengthFactor;
		this.contractGroups = a.contractGroups;
		
		this.displayFormula = a.displayFormula;
		this.displayLabels = a.displayLabels;
		this.displayNames = a.displayNames;
		this.screenExtent = a.screenExtent;
		this.omitHydrogens = a.omitHydrogens;
		
	    this.defaultAtomDisplay = new AtomDisplay(a.defaultAtomDisplay);
		this.defaultBondDisplay = new BondDisplay(a.defaultBondDisplay);
	}
	
	/**
	 * @return the bondLength
	 */
	public double getBondLength() {
		return bondLength;
	}
	/** apply scale to current molecule.
	 * may need to fix this later
	 * @param bondLength the bondLength to set
	 * 
	 */
	public void setBondLength(double bondLength) {
		this.bondLength = bondLength;
	}
	
	public void scaleBonds(MoleculeTool moleculeTool) {
		if (moleculeTool != null) {
			double avlength = moleculeTool.getAverageBondLength(CoordinateType.TWOD);
			moleculeTool.getMolecule().multiply2DCoordsBy(bondLength / avlength);
		}
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
	
	public boolean isDisplayFormula() {
		return displayFormula;
	}

	public void setDisplayFormula(boolean displayFormula) {
		System.out.println("FORMULA "+this);
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

	public MoleculeTool getMoleculeTool() {
		return moleculeTool;
	}

	public void setMoleculeTool(MoleculeTool moleculeTool) {
		this.moleculeTool = moleculeTool;
	}

	public double getHydrogenLengthFactor() {
		return hydrogenLengthFactor;
	}

	public void setHydrogenLengthFactor(double hydrogenLengthFactor) {
		this.hydrogenLengthFactor = hydrogenLengthFactor;
	}

	public Boolean getContractGroups() {
		return contractGroups;
	}

	public void setContractGroups(Boolean contractGroups) {
		this.contractGroups = contractGroups;
	}

	public AtomDisplay getDefaultAtomDisplay() {
		return defaultAtomDisplay;
	}

	public void setDefaultAtomDisplay(AtomDisplay defaultAtomDisplay) {
		this.defaultAtomDisplay = defaultAtomDisplay;
	}

	public BondDisplay getDefaultBondDisplay() {
		return defaultBondDisplay;
	}

	public void setDefaultBondDisplay(BondDisplay defaultBondDisplay) {
		this.defaultBondDisplay = defaultBondDisplay;
	}

	public String getDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MoleculeDisplay:");
		sb.append("..."+super.getDebugString());
		sb.append(S_NEWLINE);
		sb.append("  bondLength:           "+bondLength);
		sb.append(S_NEWLINE);
		sb.append("  hydrogenLengthFactor: "+hydrogenLengthFactor);
		sb.append(S_NEWLINE);
		sb.append("  omitHydrogens:        "+omitHydrogens);
		sb.append(S_NEWLINE);
		sb.append("  contractGroups:       "+contractGroups);
		sb.append(S_NEWLINE);
		sb.append("  displayFormula:       "+displayFormula);
		sb.append(S_NEWLINE);
		sb.append("  displayLabels:        "+displayLabels);
		sb.append(S_NEWLINE);
		sb.append(  "displayNames:         "+displayNames);
		sb.append(S_NEWLINE);
		sb.append(  "screenExtent:         "+screenExtent);
		sb.append(S_NEWLINE);
		sb.append(S_NEWLINE);
		sb.append("..."+defaultAtomDisplay.getDebugString());
		sb.append(S_NEWLINE);
		sb.append(S_NEWLINE);
		sb.append("..."+defaultBondDisplay.getDebugString());
		sb.append(S_NEWLINE);

		return sb.toString();
	}
	
}
