package org.xmlcml.cml.graphics;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLRuntimeException;

public class StyleBundle implements CMLConstants {
	
	public final static StyleBundle DEFAULT_STYLE_BUNDLE = new StyleBundle(
		"#000000",
		"#000000",
		0.5,
		"sans-serif",
		8.0,
		"normal",
		1.0
	);
	private String fill;
	private String stroke;
	private String fontFamily;
	private Double fontSize;
	private String fontWeight;
	private Double strokeWidth;
	private Double opacity;

	public StyleBundle(String style) {
		processStyle(style);
	}
	
	public StyleBundle(
		String fill,
		String Stroke,
		double strokeWidth,
		String fontFamily,
		double fontSize,
		String fontWeight,
		double opacity
		) {
		if (fill != null && !fill.trim().equals(S_EMPTY)) {
			this.fill = fill.trim();
		}
		if (stroke != null && !stroke.trim().equals(S_EMPTY)) {
			this.stroke = stroke.trim();
		}
		if (strokeWidth > 0) {
			this.strokeWidth = new Double(strokeWidth);
		}
		if (fontFamily != null && !fontFamily.trim().equals(S_EMPTY)) {
			this.fontFamily = fontFamily.trim();
		}
		if (fontSize > 0) {
			this.fontSize = new Double(fontSize);
		}
		if (fontWeight != null && !fontWeight.trim().equals(S_EMPTY)) {
			this.fontWeight = fontWeight.trim();
		}
		if (opacity > 0) {
			this.opacity = new Double(opacity);
		}
	}
	
	public StyleBundle(StyleBundle style) {
		this.fill = style.fill;
		this.stroke = style.stroke;
		this.strokeWidth = style.strokeWidth;
		this.fontFamily = style.fontFamily;
		this.fontSize = style.fontSize;
		this.fontWeight = style.fontWeight;
		this.opacity = style.opacity;
	}
	
	private void processStyle(String style) {
		if (style != null) {
			String[] ss = style.split(S_SEMICOLON);
			for (String s : ss) {
				s = s.trim();
				String[] aa = s.split(S_COLON);
				aa[0] = aa[0].trim();
				aa[1] = aa[1].trim();
				if (aa[0].equals("fill")) {
					fill = aa[1];
				} else if (aa[0].equals("stroke")) {
					stroke = aa[1];
				} else if (aa[0].equals("stroke-width")) {
					strokeWidth = getDouble(aa[1]); 
				} else if (aa[0].equals("font-family")) {
					fontFamily = aa[1]; 
				} else if (aa[0].equals("font-size")) {
					fontSize = getDouble(aa[1]); 
				} else if (aa[0].equals("font-weight")) {
					fontWeight = aa[1]; 
				} else if (aa[0].equals("opacity")) {
					opacity = getDouble(aa[1]); 
				} else {
					throw new CMLRuntimeException("unsupported style: "+aa[0]);
				}
			}
		}
	}
	
	public void setSubStyle(String subStyle, Object object) {
		if (subStyle == null) {
			throw new CMLRuntimeException("null style");
		} else if (subStyle.equals("fill")) {
			fill = (String) object;
		} else if (subStyle.equals("stroke")) {
			stroke = (String) object;
		} else if (subStyle.equals("stroke-width")) {
			strokeWidth = (Double) object; 
		} else if (subStyle.equals("font-family")) {
			fontFamily = (String) object; 
		} else if (subStyle.equals("font-size")) {
			fontSize = (Double) object; 
		} else if (subStyle.equals("font-weight")) {
			fontWeight = (String) object; 
		} else if (subStyle.equals("opacity")) {
			opacity = (Double) object; 
		} else {
			throw new CMLRuntimeException("unsupported style: "+subStyle);
		}

	}
	
	public Object getSubStyle(String ss) {
		Object subStyle = null;
		if (ss.equals("fill")) {
			subStyle = getFill();
		} else if (ss.equals("stroke")) {
			subStyle = getStroke();
		} else if (ss.equals("stroke-width")) {
			subStyle = getStrokeWidth();
		} else if (ss.equals("font-family")) {
			subStyle = getFontFamily();
		} else if (ss.equals("font-size")) {
			subStyle = getFontSize();
		} else if (ss.equals("font-weight")) {
			subStyle = getFontWeight();
		} else if (ss.equals("opacity")) {
			subStyle = getOpacity();
		} else {
			throw new CMLRuntimeException("unknown subStyle: "+ss);
		}
		return subStyle;
	}

	private double getDouble(String s) {
		double d = Double.NaN;
		try {
			d = new Double(s).doubleValue();
		} catch (NumberFormatException e) {
			throw new CMLRuntimeException("bad double in style: "+s);
		}
		return d;
	}
	
	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public String getStroke() {
		return stroke;
	}

	public void setStroke(String stroke) {
		this.stroke = stroke;
	}

	public Double getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(Double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public Double getFontSize() {
		return fontSize;
	}

	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	public Double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}
	
	public String toString() {
		String s = "";
		if (fill != null && !fill.trim().equals(S_EMPTY)) {
			s += "fill : "+fill+S_SEMICOLON;
		} 
		if (stroke != null && !stroke.trim().equals(S_EMPTY)) {
			s += " stroke : "+stroke+S_SEMICOLON;
		}
		if (strokeWidth != null) {
			s += " stroke-width : "+strokeWidth+S_SEMICOLON;
		}
		if (fontFamily != null && !fontFamily.trim().equals(S_EMPTY)) {
			s += " font-family : "+fontFamily+S_SEMICOLON;
		}
		if (fontSize != null) {
			s += " font-size : "+fontSize+S_SEMICOLON;
		}
		if (fontWeight != null && !fontWeight.trim().equals(S_EMPTY)) {
			s += " font-weight : "+fontWeight+S_SEMICOLON;
		}
		if (opacity != null) {
			s += " opacity : "+opacity+S_SEMICOLON;
		}
		return s;
	}

}
