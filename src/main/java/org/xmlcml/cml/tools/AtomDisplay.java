package org.xmlcml.cml.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.tools.TextDisplay.Background;
import org.xmlcml.euclid.EC;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Util;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * display parameters
 * 
 * @author pm286
 * 
 */
public class AtomDisplay extends AbstractDisplay {

	private static final Logger LOG = Logger.getLogger(AtomDisplay.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	private TextDisplay elementDisplay;
	private TextDisplay chargeDisplay;
	private TextDisplay groupDisplay;
	private TextDisplay idDisplay;
	private TextDisplay isotopeDisplay;
	private TextDisplay labelDisplay;

	private static TextDisplay defaultElementDisplay;
	private static TextDisplay defaultChargeDisplay;
	private static TextDisplay defaultGroupDisplay;
	private static TextDisplay defaultIdDisplay;
	private static TextDisplay defaultIsotopeDisplay;
	private static TextDisplay defaultLabelDisplay;

	private static void ensureDefaults() {
		ensureElementDefaults();
		ensureChargeDefaults();
		ensureGroupsDefaults();
		ensureIdDefaults();
		ensureIsotopeDefaults();
		ensureLabelDefaults();
	}

	private static void ensureLabelDefaults() {
		if (defaultLabelDisplay == null) {
			defaultLabelDisplay = new TextDisplay();
			defaultLabelDisplay.fill = "pink";
			defaultLabelDisplay.backgroundColor = "#dddddd";
			defaultLabelDisplay.setXyOffset(new Real2(0.8, -0.35));
			defaultLabelDisplay.setFontSize(1.0);
		}
	}

	private static void ensureIsotopeDefaults() {
		if (defaultIsotopeDisplay == null) {
			defaultIsotopeDisplay = new TextDisplay();
			defaultIsotopeDisplay.fill = "black";
			defaultIsotopeDisplay.backgroundColor = "#dddddd";
			defaultIsotopeDisplay.setXyOffset(new Real2(-1.2, -0.55));
			defaultIsotopeDisplay.setFontSize(25.0);
			defaultIsotopeDisplay.setBackground(Background.CIRCLE);
		}
	}

	private static void ensureIdDefaults() {
		if (defaultIdDisplay == null) {
			defaultIdDisplay = new TextDisplay();
			defaultIdDisplay.fill = "cyan";
			defaultIdDisplay.backgroundColor = "#dddddd";
			defaultIdDisplay.setXyOffset(new Real2(0.8, -0.35));
			defaultIdDisplay.setFontSize(15.0);
		}
	}

	private static void ensureGroupsDefaults() {
		if (defaultGroupDisplay == null) {
			defaultGroupDisplay = new TextDisplay();
			defaultGroupDisplay.fill = "magenta";
			defaultGroupDisplay.backgroundColor = "#dddddd";
			defaultGroupDisplay.setXyOffset(new Real2(0.8, -0.35));
			defaultGroupDisplay.setFontSize(10.0);
		}
	}

	private static void ensureChargeDefaults() {
		if (defaultChargeDisplay == null) {
			defaultChargeDisplay = new TextDisplay();
			defaultChargeDisplay.fill = "green";
			defaultChargeDisplay.backgroundColor = "#eeeeee";
			defaultChargeDisplay.backgroundColor = "white";
			defaultChargeDisplay.setXyOffset(new Real2(1.2, -0.55));
			defaultChargeDisplay.setFontSize(30.0);
			defaultChargeDisplay.setOpacity(1.0);
		}
	}

	private static void ensureElementDefaults() {
		if (defaultElementDisplay == null) {
			defaultElementDisplay = new TextDisplay();
			defaultElementDisplay.fill = "black";
			defaultElementDisplay.backgroundColor = "#aaaaaa";
			defaultElementDisplay.backgroundColor = "white";
			defaultElementDisplay.setXyOffset(new Real2(0., 0.));
			defaultElementDisplay.setFontSize(25.0);
			defaultElementDisplay.setOpacity(1.0);
		}
	};

	private MoleculeDisplay moleculeDisplay; // reference

	// atom
	protected double backgroundRadiusFactor = 0.5;
	protected boolean display = true;
	protected boolean displayCarbons;
	protected boolean displayIds;
	protected boolean displayGroups;
	protected boolean displayLabels;
	protected double scale;
	protected boolean omitHydrogens;

	public AtomDisplay(AtomDisplay atomDisplay) {
		super(atomDisplay);
		this.elementDisplay = (atomDisplay.elementDisplay == null) ? null
				: new TextDisplay(atomDisplay.elementDisplay);
		this.chargeDisplay = (atomDisplay.chargeDisplay == null) ? null
				: new TextDisplay(atomDisplay.chargeDisplay);
		this.groupDisplay = (atomDisplay.groupDisplay == null) ? null
				: new TextDisplay(atomDisplay.groupDisplay);
		this.idDisplay = (atomDisplay.idDisplay == null) ? null
				: new TextDisplay(atomDisplay.idDisplay);
		this.isotopeDisplay = (atomDisplay.isotopeDisplay == null) ? null
				: new TextDisplay(atomDisplay.isotopeDisplay);
		this.labelDisplay = (atomDisplay.labelDisplay == null) ? null
				: new TextDisplay(atomDisplay.labelDisplay);
	}

	void ensureMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		if (this.moleculeDisplay == null) {
			this.moleculeDisplay = moleculeDisplay;
		}
	}

	/**
	 * constructor.
	 */
	public AtomDisplay() {
		super();
		init();
	}

	protected void init() {
		setDefaults();
	}

	protected void setDefaults() {
		ensureDefaults();
		LOG.debug("ATOM SET DEFAULTS");
		super.setDefaults();
		display = true;
		displayCarbons = false;
		displayLabels = false;
		scale = 1.0;
		overrideSuperDefaults();
	}

	private void overrideSuperDefaults() {

		// this should go in elementDisplay
		display = true;
		color = "black";
		fill = color;
		fontFamily = FONT_SANS_SERIF;
		fontSize = 3;
		fontStyle = FONT_STYLE_NORMAL;
		fontWeight = FONT_WEIGHT_NORMAL;
		stroke = null;

		elementDisplay = new TextDisplay(defaultElementDisplay);
		chargeDisplay = new TextDisplay(defaultChargeDisplay);
		groupDisplay = new TextDisplay(defaultGroupDisplay);
		idDisplay = new TextDisplay(defaultIdDisplay);
		groupDisplay = new TextDisplay(defaultGroupDisplay);
		isotopeDisplay = new TextDisplay(defaultIsotopeDisplay);
		labelDisplay = new TextDisplay(defaultLabelDisplay);
	}

	public AtomDisplay(AbstractDisplay a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public TextDisplay getElementDisplay() {
		return elementDisplay;
	}

	public void setElementDisplay(TextDisplay elementDisplay) {
		this.elementDisplay = elementDisplay;
	}

	public TextDisplay getChargeDisplay() {
		return chargeDisplay;
	}

	public void setChargeDisplay(TextDisplay chargeDisplay) {
		this.chargeDisplay = chargeDisplay;
	}

	public TextDisplay getGroupDisplay() {
		return groupDisplay;
	}

	public void setGroupDisplay(TextDisplay groupDisplay) {
		this.groupDisplay = groupDisplay;
	}

	public TextDisplay getIdDisplay() {
		return idDisplay;
	}

	public void setIdDisplay(TextDisplay idDisplay) {
		this.idDisplay = idDisplay;
	}

	public TextDisplay getIsotopeDisplay() {
		return isotopeDisplay;
	}

	public void setIsotopeDisplay(TextDisplay isotopeDisplay) {
		this.isotopeDisplay = isotopeDisplay;
	}

	public TextDisplay getLabelDisplay() {
		return labelDisplay;
	}

	public void setLabelDisplay(TextDisplay labelDisplay) {
		this.labelDisplay = labelDisplay;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean isDisplayIds() {
		return displayIds;
	}

	public void setDisplayIds(boolean displayIds) {
		this.displayIds = displayIds;
	}

	public boolean isDisplayGroups() {
		return displayGroups;
	}

	public void setDisplayGroups(boolean displayGroups) {
		this.displayGroups = displayGroups;
	}

	public boolean isOmitHydrogens() {
		return omitHydrogens;
	}

	public void setOmitHydrogens(boolean omitHydrogens) {
		this.omitHydrogens = omitHydrogens;
	}

	public double getBackgroundRadiusFactor() {
		return backgroundRadiusFactor;
	}

	public void setBackgroundRadiusFactor(double backgroundRadiusFactor) {
		this.backgroundRadiusFactor = backgroundRadiusFactor;
	}

	public static AbstractDisplay getDefaultElementDisplay() {
		return defaultElementDisplay;
	}

	public static void setDefaultElementDisplay(
			TextDisplay defaultElementDisplay) {
		AtomDisplay.defaultElementDisplay = defaultElementDisplay;
	}

	public static AbstractDisplay getDefaultChargeDisplay() {
		return defaultChargeDisplay;
	}

	public static void setDefaultChargeDisplay(TextDisplay defaultChargeDisplay) {
		AtomDisplay.defaultChargeDisplay = defaultChargeDisplay;
	}

	public static AbstractDisplay getDefaultGroupDisplay() {
		return defaultGroupDisplay;
	}

	public static void setDefaultGroupDisplay(TextDisplay defaultGroupDisplay) {
		AtomDisplay.defaultGroupDisplay = defaultGroupDisplay;
	}

	public static AbstractDisplay getDefaultIdDisplay() {
		return defaultIdDisplay;
	}

	public static void setDefaultIdDisplay(TextDisplay defaultIdDisplay) {
		AtomDisplay.defaultIdDisplay = defaultIdDisplay;
	}

	public static AbstractDisplay getDefaultIsotopeDisplay() {
		return defaultIsotopeDisplay;
	}

	public static void setDefaultIsotopeDisplay(
			TextDisplay defaultIsotopeDisplay) {
		AtomDisplay.defaultIsotopeDisplay = defaultIsotopeDisplay;
	}

	public static AbstractDisplay getDefaultLabelDisplay() {
		return defaultLabelDisplay;
	}

	public static void setDefaultLabelDisplay(TextDisplay defaultLabelDisplay) {
		AtomDisplay.defaultLabelDisplay = defaultLabelDisplay;
	}

	/**
	 * @return the displayLabels
	 */
	public boolean isDisplayLabels() {
		return displayLabels;
	}

	/**
	 * @param displayLabels
	 *            the displayLabels to set
	 */
	public void setDisplayLabels(boolean displayLabels) {
		this.displayLabels = displayLabels;
	}

	// /**
	// * @return the dEFAULT
	// */
	// public static AtomDisplay getDEFAULT() {
	// return DEFAULT;
	// }
	//
	/**
	 * can atom be omitted?
	 * 
	 * @param atom
	 * @return omit
	 */
	public boolean omitAtom(CMLAtom atom) {
		boolean omit = false;
		if (this.isOmitHydrogens()
				|| (moleculeDisplay != null && moleculeDisplay
						.isOmitHydrogens())) {
			if (AS.H.equals(atom.getElementType())) {
				omit = true;
			}
		}
		return omit;
	}

	/**
	 * @return the displayCarbons
	 */
	public boolean isDisplayCarbons() {
		return displayCarbons;
	}

	/**
	 * @param displayCarbons
	 *            the displayCarbons to set
	 */
	public void setDisplayCarbons(boolean displayCarbons) {
		this.displayCarbons = displayCarbons;
	}

	/**
	 */
	public double getScaledFontSize() {
		return scale * fontSize;
	}

	/**
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * cascades through from calling program
	 * 
	 * @param args
	 * @param i
	 * @return increased i if args found
	 */
	public int processArgs(String[] args, int i) {
		// charge

		return i;
	}

	public static void usage() {

		Util.println(" AtomDisplay options ");
		Util.println();
	}

	public String getDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AtomDisplay:");
		sb.append(EC.S_NEWLINE);

		sb.append((elementDisplay == null) ? "null elementDisplay"
				: elementDisplay.getDebugString());
		sb.append((chargeDisplay == null) ? "null chargeDisplay"
				: chargeDisplay.getDebugString());
		sb.append((groupDisplay == null) ? "null groupDisplay" : groupDisplay
				.getDebugString());
		sb.append((idDisplay == null) ? "null idDisplay" : idDisplay
				.getDebugString());
		sb.append((isotopeDisplay == null) ? "null isotopeDisplay"
				: isotopeDisplay.getDebugString());
		sb.append((labelDisplay == null) ? "null labelDisplay" : labelDisplay
				.getDebugString());

		sb.append("  background Radius Factor: " + backgroundRadiusFactor);
		sb.append(EC.S_NEWLINE);
		sb.append("  display:                  " + display);
		sb.append(EC.S_NEWLINE);
		sb.append("  displayCarbons:           " + displayCarbons);
		sb.append(EC.S_NEWLINE);
		sb.append("  displayIds:               " + displayIds);
		sb.append(EC.S_NEWLINE);
		sb.append("  displayGroups:            " + displayGroups);
		sb.append(EC.S_NEWLINE);
		sb.append("  displayLabels:            " + displayLabels);
		sb.append(EC.S_NEWLINE);
		sb.append("  scale:                    " + scale);
		sb.append(EC.S_NEWLINE);
		sb.append(EC.S_NEWLINE);
		return sb.toString();
	}

	public MoleculeDisplay getMoleculeDisplay() {
		return moleculeDisplay;
	}

	public void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}

}
