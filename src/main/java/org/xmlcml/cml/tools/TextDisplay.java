package org.xmlcml.cml.tools;

import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_MINUS;
import static org.xmlcml.euclid.EuclidConstants.S_NEWLINE;
import static org.xmlcml.euclid.EuclidConstants.S_PLUS;

import java.util.List;

import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Real2;

/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class TextDisplay extends AbstractDisplay {

	
	private double backgroundRadiusFactor = 1.0;
	private String textS;
	private SVGG g;
	private Real2 xyOffset = new Real2(0., 0.);
	
	final static TextDisplay DEFAULT = new TextDisplay();
	private SVGText text;
	protected boolean omitHydrogens;
	
	static {
		DEFAULT.setDefaults();
	};
	
	public TextDisplay(AbstractDisplay a) {
		super(a);
	}

//	public TextDisplay(String color, String fill, double fontSize,
//			String fontStyle, String fontWeight, String fontFamily,
//			boolean omitHydrogens, double opacity, boolean showChildLabels,
//			String stroke, String backgroundColor, Real2 xyOffset) {
//		super(color, fill, fontSize, fontStyle, fontWeight, fontFamily,
//				opacity, showChildLabels, stroke, backgroundColor);
//		this.xyOffset = new Real2(xyOffset);
//	}

	public TextDisplay(TextDisplay a) {
		super(a);
		this.xyOffset = new Real2(a.xyOffset);
	}

	/** constructor.
	 */
	public TextDisplay() {
		super();
	}
	
	protected void init() {
	}
	
	protected void setDefaults() {
		super.setDefaults();
		//
		fill = "black";
		stroke = null;
		fontSize = 19;
		xyOffset = new Real2(0., 0.);
	}
	
	public void display(SVGG g, String s) {
		this.g = g;
		textS = s;
		display();
	}
	
	private void display() {
		if (g != null) {
			text = new SVGText(xyOffset, textS);
			text.setFontSize(fontSize);
			text.setFill(fill);
//			text.debug("TTT");
			g.appendChild(text);
		}
	}
	
	public void displaySignedInteger(int i) {
		textS = S_EMPTY;
		if (i < -1) {
			textS += S_MINUS;
		} else if (i > 1) {
			textS += S_PLUS;
		}
		if (i < 0) {
			textS += -i;
		} else if (i > 0) {
			textS += i;
		}
		display();
	}
	
	public void displayLabel(CMLLabel label) {
		textS = label.getCMLValue();
		display();
	}
	
	/** 
	 * @param label
	 */
	// FIXME
	public void displayGroup(CMLLabel label) {
		textS = label.getCMLValue();
		display();
	}
	
	public void setValues(List<String> ss) {
		if (ss == null) {
			throw new RuntimeException("bad list of values");
		} else if (ss.size() < 3) {
			throw new RuntimeException("must have 3-4 values");
		} else if (ss.size() > 4) {
			throw new RuntimeException("must have 3-4 values");
		} else {
			new Real2(new Double(ss.get(0)), new Double(ss.get(1)));
			fontSize = new Double(ss.get(2));
			if (ss.size() == 4) {
				fill = (String) ss.get(3);
			}
		}
		display();
	}

	public SVGText getText() {
		return text;
	}

	public void setText(SVGText text) {
		this.text = text;
	}
	
	public String getDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TextDisplay:");
		sb.append(S_NEWLINE);
		sb.append("..."+super.getDebugString());
		sb.append("text:                      "+textS);
		sb.append(S_NEWLINE);
		sb.append("xyOffset:                  "+xyOffset);
		sb.append(S_NEWLINE);
		sb.append(S_NEWLINE);
		return sb.toString();
	}
}
