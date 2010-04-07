package org.xmlcml.cml.tools;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.euclid.EC;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Interval;
import org.xmlcml.euclid.RealInterval;

/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class MoleculeDisplay extends AbstractDisplay {
	private static Logger LOG = Logger.getLogger(MoleculeDisplay.class);

	final static MoleculeDisplay DEFAULT = new MoleculeDisplay();
	static {
		DEFAULT.setDefaults();
	};
	
	public enum Position {
		TOP,
		RIGHT,
		LEFT,
		BOTTOM,
		CENTRE
	}
	
	// all atom displays are now defaults
	private AtomDisplay defaultAtomDisplay = new AtomDisplay();
	private BondDisplay defaultBondDisplay = new BondDisplay(BondDisplay.DEFAULT);
	private FormulaDisplay formulaDisplay = new FormulaDisplay();
	private NameDisplay nameDisplay = new NameDisplay();

	private Double bondLength;
	private Double hydrogenLengthFactor;
	private Boolean contractGroups;
	private boolean displayFormula;
	private boolean displayLabels;
	private boolean displayNames;
	private boolean omitHydrogens;
	private Double labelFontSize;
	private Double labelYSpacing;
//	private Position formulaYPosition = Position.BOTTOM;
	private Position formulaYPosition = Position.TOP;
	private Position formulaXPosition = Position.LEFT;
	private Position nameYPosition = Position.TOP;
	private Position nameXPosition = Position.CENTRE;
	private Position idYPosition = Position.TOP;
	private Position idXPosition = Position.LEFT;
	private boolean drawBoundingBox;
	
	private Real2Interval screenExtent;
	private AbstractSVGTool moleculeTool;

	private BoundingBoxBundle boundingBoxBundle = new BoundingBoxBundle();
	private Real2 maxScreenSize;
	private Real2 minScreenSize;
	private Real2 namePosition;
	private boolean displayGroups;
	
	/**
	 */
	public MoleculeDisplay() {
		super();
		init();
	}
	
	/**
	 */
	public MoleculeDisplay(AbstractSVGTool moleculeTool) {
		this();
		this.setMoleculeTool(moleculeTool);
	}
	
	protected void init() {
		setDefaults();
	}
	
	protected void setDefaults() {
		super.setDefaults();

		bondLength = getDefaultBondLength();
		hydrogenLengthFactor = 0.7;
		contractGroups = false;
		
//		displayFormula = false;
		displayFormula = true;
		displayLabels = true;
		displayNames = false;
		labelYSpacing = 1.6;
		labelFontSize = 50.0;
		drawBoundingBox = true;
		
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
//	    this.defaultAtomDisplay.displayCarbons = true;
		this.defaultBondDisplay = new BondDisplay(a.defaultBondDisplay);
	}
	
	/**
	 * @return the bondLength
	 */
	public Double getBondLength() {
		return bondLength;
	}
	/** apply scale to current molecule.
	 * may need to fix this later
	 * @param bondLength the bondLength to set
	 * 
	 */
	public void setBondLength(Double bondLength) {
		this.bondLength = bondLength;
	}
	
	public void scaleBonds(MoleculeTool moleculeTool) {
		if (moleculeTool != null) {
			double avlength = moleculeTool.getAverageBondLength(CoordinateType.TWOD);
			moleculeTool.getMolecule().multiply2DCoordsBy(bondLength / avlength);
		}
	}

	public Position getFormulaYPosition() {
		return formulaYPosition;
	}

	public void setFormulaYPosition(Position formulaYPosition) {
		this.formulaYPosition = formulaYPosition;
	}

	public Position getFormulaXPosition() {
		return formulaXPosition;
	}

	public void setFormulaXPosition(Position formulaXPosition) {
		this.formulaXPosition = formulaXPosition;
	}

	public Position getNameYPosition() {
		return nameYPosition;
	}

	public FormulaDisplay getFormulaDisplay() {
		return formulaDisplay;
	}

	public void setFormulaDisplay(FormulaDisplay formulaDisplay) {
		this.formulaDisplay = formulaDisplay;
	}

	public NameDisplay getNameDisplay() {
		return nameDisplay;
	}

	public void setNameDisplay(NameDisplay nameDisplay) {
		this.nameDisplay = nameDisplay;
	}

	public void setNameYPosition(Position nameYPosition) {
		this.nameYPosition = nameYPosition;
	}

	public Position getNameXPosition() {
		return nameXPosition;
	}

	public void setNameXPosition(Position nameXPosition) {
		this.nameXPosition = nameXPosition;
	}

//	public Position getNameXYPosition() {
//		return nameXYPosition;
//	}
//
//	public void setNameXYPosition(Real2 ) {
//		return nameXYPosition;
//	}

	public Position getIdYPosition() {
		return idYPosition;
	}

	public void setIdYPosition(Position idYPosition) {
		this.idYPosition = idYPosition;
	}

	public Position getIdXPosition() {
		return idXPosition;
	}

	public void setIdXPosition(Position idXPosition) {
		this.idXPosition = idXPosition;
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

	public AbstractSVGTool getMoleculeTool() {
		return moleculeTool;
	}

	public void setMoleculeTool(AbstractSVGTool moleculeTool) {
		this.moleculeTool = moleculeTool;
	}

	public Double getHydrogenLengthFactor() {
		return hydrogenLengthFactor;
	}

	public void setHydrogenLengthFactor(Double hydrogenLengthFactor) {
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

	public boolean isDisplayGroups() {
		return displayGroups;
	}

	public void setDisplayGroups(boolean displayGroups) {
		this.displayGroups = displayGroups;
	}

	public String getDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MoleculeDisplay:");
		sb.append("..."+super.getDebugString());
		sb.append(EC.S_NEWLINE);
		sb.append("  bondLength:           "+bondLength);
		sb.append(EC.S_NEWLINE);
		sb.append("  hydrogenLengthFactor: "+hydrogenLengthFactor);
		sb.append(EC.S_NEWLINE);
		sb.append("  omitHydrogens:        "+omitHydrogens);
		sb.append(EC.S_NEWLINE);
		sb.append("  contractGroups:       "+contractGroups);
		sb.append(EC.S_NEWLINE);
		sb.append("  displayFormula:       "+displayFormula);
		sb.append(EC.S_NEWLINE);
		sb.append("  displayLabels:        "+displayLabels);
		sb.append(EC.S_NEWLINE);
		sb.append(  "displayNames:         "+displayNames);
		sb.append(EC.S_NEWLINE);
		sb.append(  "screenExtent:         "+screenExtent);
		sb.append(EC.S_NEWLINE);
		sb.append(EC.S_NEWLINE);
		sb.append("..."+defaultAtomDisplay.getDebugString());
		sb.append(EC.S_NEWLINE);
		sb.append(EC.S_NEWLINE);
		sb.append("..."+defaultBondDisplay.getDebugString());
		sb.append(EC.S_NEWLINE);

		return sb.toString();
	}

	public Double getLabelFontSize() {
		return labelFontSize;
	}

	public void setLabelFontSize(Double labelFontSize) {
		this.labelFontSize = labelFontSize;
	}

	public Double getLabelYSpacing() {
		return labelYSpacing;
	}

	public void setLabelYSpacing(Double labelYSpacing) {
		this.labelYSpacing = labelYSpacing;
	}

	public boolean isDrawBoundingBox() {
		return drawBoundingBox;
	}

	public void setDrawBoundingBox(boolean drawBoundingBox) {
		this.drawBoundingBox = drawBoundingBox;
	}

	public BoundingBoxBundle getBoundingBoxBundle() {
		return this.boundingBoxBundle;
	}

	public void setBoundingBoxBundle(BoundingBoxBundle boundingBoxBundle) {
		this.boundingBoxBundle = boundingBoxBundle;
	}

	public Real2 getMaxScreenSize() {
		return maxScreenSize;
	}
	
	public void setMaxScreenSize(Real2 maxScreenSize) {
		this.maxScreenSize = maxScreenSize;
	}
	
	public Real2 getMinScreenSize() {
		return minScreenSize;
	}
	
	public void setMinScreenSize(Real2 minScreenSize) {
		this.minScreenSize = minScreenSize;
	}

	public static double getDefaultBondLength() {
		return 50.0;
	}

	public void setNamePosition(Real2 real2) {
		this.namePosition = real2;
	}

}
