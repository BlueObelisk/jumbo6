/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.tools;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CC;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.euclid.EC;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGText;

/** display properties for bond.
 * 
 * @author pm286
 *
 */
public class TextDisplay extends AbstractDisplay {

	static Logger LOG = Logger.getLogger(TextDisplay.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	public enum Background {
		NONE,
		CIRCLE,
		RECTANGLE
	};
	
	private SVGElement g;
	private Background background;
	private Real2 xyOffset = new Real2(0., 0.);
	private double backgroundRadiusFactor = 0.55; // empirical
	private Real2 textXyOffset = new Real2(-0.35, 0.37); // empirical
	
	private SVGText text;
	private String textS;
	public TextDisplay(AbstractDisplay a) {
		super(a);
	}

	public TextDisplay(TextDisplay a) {
		super(a);
		this.xyOffset = new Real2(a.xyOffset);
		this.backgroundRadiusFactor = a.backgroundRadiusFactor;
		this.textS = a.textS;
		this.background = a.background;
	}

	/** constructor.
	 */
	public TextDisplay() {
		super();
		setDefaults();
	}
	
	protected void init() {
	}
	
	protected void setDefaults() {
		super.setDefaults();
		background = Background.NONE;
		fill = "black";
		stroke = null;
		fontSize = 19;
		xyOffset = new Real2(0., 0.);
		backgroundColor = "yellow";
	}
	
	public void displayElement(SVGElement g, String s) {
		background = Background.CIRCLE;
		display(g, s);
	}
	
	public void display(SVGElement g, String s) {
		this.g = g;
		textS = s;
		display();
	}
	
	private void display() {
		if (g != null) {
			if (Background.CIRCLE == background) {
				drawBackgroundCircle();
			}
			if (Background.RECTANGLE == background) {
				drawBackgroundRectangle();
			}
			// display text even if empty
			Real2 xyOffsetPix = (xyOffset.plus(textXyOffset)).multiplyBy(fontSize);
			text = new SVGText(xyOffsetPix, textS);
			text.setFontSize(fontSize);
			text.setFill(fill);
			g.appendChild(text);
		}
	}
	
	private void drawBackgroundCircle() {
		double rad = backgroundRadiusFactor*fontSize;
		LOG.debug("RAD "+rad);
		SVGElement circle = new SVGCircle(xyOffset.multiplyBy(fontSize), rad);
		circle.setStroke("none");
		if (userElement != null) {
			circle.setUserElement(userElement);
		}
		 // should be background
		g.appendChild(circle);
		circle.setOpacity(opacity);
		circle.setFill(backgroundColor);
	}

	private void drawBackgroundRectangle() {
//		double rad = backgroundRadiusFactor*fontSize;
		// not yet written
		
	}

	public void displaySignedInteger(SVGElement g, int i) {
		textS = CC.S_EMPTY;
		if (i < 0) {
			textS += CC.S_MINUS;
		} else if (i > 1) {
			textS += CC.S_PLUS;
		}
		if (i < -1) {
			textS += -i;
		} else if (i > 1) {
			textS += i;
		}
		background = Background.CIRCLE;
		display(g, textS);
	}
	
	public void displayLabel(SVGElement g, CMLLabel label) {
		textS = label.getCMLValue();
		background = Background.RECTANGLE;
		display(g, textS);
	}
	
	public void displayId(SVGElement g, String id) {
		background = Background.RECTANGLE;
		display(g, id);
	}
	
	/** 
	 * @param label
	 */
	public void displayGroup(SVGElement g, CMLLabel label) {
		textS = label.getCMLValue();
		background = Background.RECTANGLE;
		display(g, textS);
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
		sb.append(EC.S_NEWLINE);
		sb.append("..."+super.getDebugString());
		sb.append("text:                      "+textS);
		sb.append(EC.S_NEWLINE);
		sb.append("xyOffset:                  "+xyOffset);
		sb.append(EC.S_NEWLINE);
		sb.append(EC.S_NEWLINE);
		return sb.toString();
	}

	public double getBackgroundRadiusFactor() {
		return backgroundRadiusFactor;
	}

	public void setBackgroundRadiusFactor(double backgroundRadiusFactor) {
		this.backgroundRadiusFactor = backgroundRadiusFactor;
	}

	public Real2 getXyOffset() {
		return xyOffset;
	}

	public void setXyOffset(Real2 xyOffset) {
		this.xyOffset = xyOffset;
	}

	public Background getBackground() {
		return background;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	public Real2 getTextXyOffset() {
		return textXyOffset;
	}

	public void setTextXyOffset(Real2 textXyOffset) {
		this.textXyOffset = textXyOffset;
	}
}
