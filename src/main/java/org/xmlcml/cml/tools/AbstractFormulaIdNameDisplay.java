package org.xmlcml.cml.tools;

public class AbstractFormulaIdNameDisplay extends AbstractDisplay {

	private double fontSize = 20.0;
	private double fontSizeAtom = fontSize;
	private double widthFactor = 0.8;
	private double subscriptFontFactor = 0.7;
	private double superscriptFontFactor = 0.7;
	private double fontSizeCount;
	private double fontSizeCharge;
	private double subscriptShift;
	private double superscriptShift;

	public AbstractFormulaIdNameDisplay() {
		
	}
	
	public AbstractFormulaIdNameDisplay(AbstractFormulaIdNameDisplay a) {
		super(a);
	}

	public double getFontSize() {
		return fontSize;
	}

	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	public double getFontSizeAtom() {
		return fontSizeAtom;
	}

	public void setFontSizeAtom(double fontSizeAtom) {
		this.fontSizeAtom = fontSizeAtom;
	}

	public double getWidthFactor() {
		return widthFactor;
	}

	public void setWidthFactor(double widthFactor) {
		this.widthFactor = widthFactor;
	}

	public double getSubscriptFontFactor() {
		return subscriptFontFactor;
	}

	public void setSubscriptFontFactor(double subscriptFontFactor) {
		this.subscriptFontFactor = subscriptFontFactor;
	}

	public double getSuperscriptFontFactor() {
		return superscriptFontFactor;
	}

	public void setSuperscriptFontFactor(double superscriptFontFactor) {
		this.superscriptFontFactor = superscriptFontFactor;
	}

	public double getFontSizeCount() {
		return fontSizeCount;
	}

	public void setFontSizeCount(double fontSizeCount) {
		this.fontSizeCount = fontSizeCount;
	}

	public double getFontSizeCharge() {
		return fontSizeCharge;
	}

	public void setFontSizeCharge(double fontSizeCharge) {
		this.fontSizeCharge = fontSizeCharge;
	}

	public double getSubscriptShift() {
		return subscriptShift;
	}

	public void setSubscriptShift(double subscriptShift) {
		this.subscriptShift = subscriptShift;
	}

	public double getSuperscriptShift() {
		return superscriptShift;
	}

	public void setSuperscriptShift(double superscriptShift) {
		this.superscriptShift = superscriptShift;
	}

	@Override
	protected void setDefaults() {
		fontSize = 20.0;
		fontSizeAtom = fontSize;
		widthFactor = 0.8;
		subscriptFontFactor = 0.7;
		superscriptFontFactor = 0.7;
		fontSizeCount = fontSizeAtom * subscriptFontFactor;
		fontSizeCharge = fontSizeAtom * superscriptFontFactor;
		subscriptShift = fontSizeCount * 0.6;
		superscriptShift = fontSizeCharge * 0.6;
	}
}
