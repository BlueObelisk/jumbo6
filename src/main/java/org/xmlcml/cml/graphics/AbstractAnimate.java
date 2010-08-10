package org.xmlcml.cml.graphics;

import nu.xom.Attribute;

public abstract class AbstractAnimate extends SVGElement {
	protected static final String DUR = "dur";
	protected static final String ATTRIBUTE_NAME = "attributeName";
	protected static final String FROM = "from";
	protected static final String TO = "to";
	protected static final String BEGIN = "begin";
	protected static final String OPACITY = "opacity";
	protected static final String Y = "y";
	protected static final String X = "x";
	public static final String FREEZE = "freeze";
	private static final String FILL = "fill";

	
	protected AbstractAnimate(SVGElement element) {
		super(element);
	}
	protected AbstractAnimate(String tag) {
		super(tag);
	}
	protected void init() {
		this.setBegin("0.0s");
		this.setDur("5.0s");
	}
	public void setBegin(Double begin) {
		this.setBegin(""+begin+"s");
	}
	public void setBegin(String begin) {
		this.setAttribute(BEGIN, begin);
	}
	public void setDur(Double dur) {
		this.setDur(""+dur);
	}
	public void setDur(String dur) {
		this.setAttribute(DUR, dur);
	}
	
	public void setAttribute(String name, String value) {
		this.addAttribute(new Attribute(name, value));
	}
	public void setAttributeName(String name) {
		this.setAttribute(ATTRIBUTE_NAME, name);
	}
	public void setOpacity(double from, double to) {
		setFromTo(OPACITY, from, to);
	}
	public void setFrom(String from) {
		this.addAttribute(new Attribute(FROM, from));
	}
	public void setTo(String to) {
		this.addAttribute(new Attribute(TO, to));
	}
	
	public void setFill(String freeze) {
		this.addAttribute(new Attribute(FILL, freeze));
	}
	public void setX(double to, double from) {
		setFromTo(X, to, from);
	}

	public void setY(double to, double from) {
		setFromTo(Y, from, to);
	}
	private void setFromTo(String name, double to, double from) {
		this.setAttributeName(name);
		this.setTo(""+to);
		this.setFrom(""+from);
	}

}
