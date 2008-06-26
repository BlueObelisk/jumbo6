package org.xmlcml.cml.tools;

import static org.xmlcml.euclid.EuclidConstants.S_NEWLINE;

import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.molutil.ChemicalElement.AS;

/** display parameters
 * 
 * @author pm286
 *
 */
public class AtomDisplay extends AbstractDisplay {

	final static AtomDisplay DEFAULT = new AtomDisplay();
	static {
		DEFAULT.setDefaults();
	}

	private TextDisplay elementDisplay;
	private TextDisplay chargeDisplay;
	private TextDisplay groupDisplay;
	private TextDisplay idDisplay;
	private TextDisplay isotopeDisplay;
	private TextDisplay labelDisplay;
	
	// atom
	protected double backgroundRadiusFactor;
	protected boolean display;
	protected boolean displayCarbons;
	protected boolean displayIds;
	protected boolean displayGroups;
	protected boolean displayLabels;
	protected double scale;
	protected boolean omitHydrogens;
	
	public boolean isOmitHydrogens() {
		return omitHydrogens;
	}
	public void setOmitHydrogens(boolean omitHydrogens) {
		this.omitHydrogens = omitHydrogens;
	}
	public static void setChargeDefaults() {
		
	}
	public static void setGroupDefaults() {
		
	}
	public static void setIdDefaults() {
		
	}
	public static void setIsotopeDefaults() {
		
	}
	public static void setLabelDefaults() {
		
	}

	public double getBackgroundRadiusFactor() {
		return backgroundRadiusFactor;
	}

	public void setBackgroundRadiusFactor(double backgroundRadiusFactor) {
		this.backgroundRadiusFactor = backgroundRadiusFactor;
	}

	public AtomDisplay(AtomDisplay atomDisplay) {
		super(atomDisplay);
		this.elementDisplay = atomDisplay.elementDisplay;
		this.chargeDisplay = atomDisplay.chargeDisplay;
		this.groupDisplay = atomDisplay.groupDisplay;
		this.idDisplay = atomDisplay.idDisplay;
		this.isotopeDisplay = atomDisplay.isotopeDisplay;
		this.labelDisplay = atomDisplay.labelDisplay;
	}
	
 	private void ensureElementDisplay() {
		elementDisplay = (elementDisplay == null) ? new TextDisplay() : elementDisplay;
	}


	/**
	 * @return the displayLabels
	 */
	public boolean isDisplayLabels() {
		return displayLabels;
	}

	/**
	 * @param displayLabels the displayLabels to set
	 */
	public void setDisplayLabels(boolean displayLabels) {
		this.displayLabels = displayLabels;
	}

	/** constructor.
	 */
	public AtomDisplay() {
		super();
	}
	
	protected void init() {
	}
	
	protected void setDefaults() {
		super.setDefaults();
		setLocalDefaults();
		overrideSuperDefaults();
	}

	private void overrideSuperDefaults() {
		display = true;
		color = "black";
		fill = color;
		fontFamily = FONT_SANS_SERIF;
		fontSize = 0.35;
		fontStyle = FONT_STYLE_NORMAL;
		fontWeight = FONT_WEIGHT_NORMAL;
		stroke = null;
				
		elementDisplay = new TextDisplay();
		chargeDisplay = null;
		groupDisplay = null;
		idDisplay = null;
		isotopeDisplay = null;
		labelDisplay = null;
	}

	private void setLocalDefaults() {
		display = true;
		displayCarbons = false;
		displayLabels = false;
		scale = 1.0;
	}
	
	public AtomDisplay(AbstractDisplay a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the dEFAULT
	 */
	public static AtomDisplay getDEFAULT() {
		return DEFAULT;
	}

	/** can atom be omitted?
	 * 
	 * @param atom
	 * @return omit
	 */
	public boolean omitAtom(CMLAtom atom) {
		boolean omit = false;
		if (this.isOmitHydrogens()) {
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
	 * @param displayCarbons the displayCarbons to set
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
		}
		return i;
	}

	public static void usage() {
		
		System.out.println(" AtomDisplay options ");
		System.out.println();
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

	private void enableChargeDisplay() {
		if (this.chargeDisplay == null) {
			this.chargeDisplay = new TextDisplay();
			chargeDisplay.setDefaults();
			setChargeDefaults();
		}
	}
	public TextDisplay getChargeDisplay() {
		enableChargeDisplay();
		return chargeDisplay;
	}

	public void setChargeDisplay(TextDisplay chargeDisplay) {
		this.chargeDisplay = chargeDisplay;
	}

	private void enableGroupDisplay() {
		if (this.groupDisplay == null) {
			this.groupDisplay = new TextDisplay();
			groupDisplay.setDefaults();
			setGroupDefaults();
		}
	}
	
	public TextDisplay getGroupDisplay() {
		enableGroupDisplay();
		return groupDisplay;
	}

	public void setGroupDisplay(TextDisplay groupDisplay) {
		this.groupDisplay = groupDisplay;
	}

	private void enableIdDisplay() {
		if (this.idDisplay == null) {
			this.idDisplay = new TextDisplay();
			idDisplay.setDefaults();
			setIdDefaults();
		}
	}
	
	public TextDisplay getIdDisplay() {
		enableIdDisplay();
		return idDisplay;
	}

	public void setIdDisplay(TextDisplay idDisplay) {
		this.idDisplay = idDisplay;
	}

	private void enableIsotopeDisplay() {
		if (this.isotopeDisplay == null) {
			this.isotopeDisplay = new TextDisplay();
			isotopeDisplay.setDefaults();
			setIsotopeDefaults();
		}
	}
	
	public TextDisplay getIsotopeDisplay() {
		enableIsotopeDisplay();
		return isotopeDisplay;
	}

	public void setIsotopeDisplay(TextDisplay isotopeDisplay) {
		this.isotopeDisplay = isotopeDisplay;
	}

	private void enableLabelDisplay() {
		if (this.labelDisplay == null) {
			this.labelDisplay = new TextDisplay();
			labelDisplay.setDefaults();
			setLabelDefaults();
		}
	}
	
	public TextDisplay getLabelDisplay() {
		enableLabelDisplay();
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

	public String getDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AtomDisplay:");
		sb.append(S_NEWLINE);
		
		sb.append((elementDisplay == null) ? "null elementDisplay" :
			elementDisplay.getDebugString());
		sb.append((chargeDisplay == null)  ? "null chargeDisplay" :
			chargeDisplay.getDebugString());
		sb.append((groupDisplay == null)   ? "null groupDisplay" :
			groupDisplay.getDebugString());
		sb.append((idDisplay == null)      ? "null idDisplay" :
			idDisplay.getDebugString());
		sb.append((isotopeDisplay == null) ? "null isotopeDisplay" :
			isotopeDisplay.getDebugString());
		sb.append((labelDisplay == null)   ? "null labelDisplay" :
			labelDisplay.getDebugString());
		
		sb.append("  background Radius Factor: "+backgroundRadiusFactor);
		sb.append(S_NEWLINE);
		sb.append("  display:                  "+display);
		sb.append(S_NEWLINE);
		sb.append("  displayCarbons:           "+displayCarbons);
		sb.append(S_NEWLINE);
		sb.append("  displayIds:               "+displayIds);
		sb.append(S_NEWLINE);
		sb.append("  displayGroups:            "+displayGroups);
		sb.append(S_NEWLINE);
		sb.append("  displayLabels:            "+displayLabels);
		sb.append(S_NEWLINE);
		sb.append("  scale:                    "+scale);
		sb.append(S_NEWLINE);
		sb.append(S_NEWLINE);
		return sb.toString();
	}
}
