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

package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Transform2;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGPath extends SVGElement {

	private static Logger LOG = Logger.getLogger(SVGPath.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	public final static String TAG ="path";
	private GeneralPath path2;

	/** constructor
	 */
	public SVGPath() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGPath(SVGPath element) {
        super((SVGElement) element);
	}
	
	
	/** constructor
	 */
	public SVGPath(Element element) {
        super((SVGElement) element);
	}
	
	public SVGPath(Real2Array xy) {
		this(createD(xy));
	}
	
	public SVGPath(String d) {
		this();
		setDString(d);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGPath(this);
    }

	protected void init() {
		super.setDefaultStyle();
		setDefaultStyle(this);
	}
	
	
	public static void setDefaultStyle(SVGPath path) {
		path.setStroke("black");
		path.setStrokeWidth(0.5);
		path.setFill("none");
	}
	
	public static String createD(Real2Array xy) {
		String s = CMLConstants.S_EMPTY;
		StringBuilder sb = new StringBuilder();
		if (xy.size() > 0) {
			sb.append("M");
			sb.append(xy.get(0).getX()+S_SPACE);
			sb.append(xy.get(0).getY()+S_SPACE);
		}
		if (xy.size() > 1) {
			for (int i = 1; i < xy.size(); i++ ) {
				sb.append("L");
				sb.append(xy.get(i).getX()+S_SPACE);
				sb.append(xy.get(i).getY()+S_SPACE);
			}
			sb.append("Z");
		}
		s = sb.toString();
		return s;
	}
	
	public void setD(Real2Array r2a) {
		this.setDString(createD(r2a));
	}
	
	public Real2Array getD() {
		String d = getDString();
		return createReal2ArrayFromDString(d);
	}
	
	public void setDString(String d) {
		this.addAttribute(new Attribute("d", d));
	}
	
	public String getDString() {
		return this.getAttributeValue("d");
	}
	
	
//  <g style="stroke-width:0.2;">
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//</g>
	
	protected void drawElement(Graphics2D g2d) {
		GeneralPath path = createAndSetPath2D();
		g2d.draw(path);
	}
	
	public static Real2Array createReal2ArrayFromDString(String s) {
		Real2Array r2a = new Real2Array();
		while (s.length() > 0) {
			// crude
			if (s.startsWith("M") ||
				s.startsWith("m") ||
				s.startsWith("L") ||
				s.startsWith("l")) {
				Real2String r2s = new Real2String(s.substring(1));
				Real2 xy = new Real2((double)r2s.x, (double)r2s.y);
				r2a.add(xy);
				s = r2s.s.trim();
			} else if (s.startsWith("z") || s.startsWith("Z")) {
				Real2String r2s = new Real2String(s.substring(1));
				s = r2s.s.trim();
//				path2.closePath();
//				s = s.substring(1).trim();
			} else {
				Real2String r2s = new Real2String(s.substring(0));
				s = r2s.s.trim();
			}
		}
		return r2a;
	}
	
	public GeneralPath createAndSetPath2D() {
		String s = this.getDString().trim()+S_SPACE;
		path2 = new GeneralPath();
		while (s.length() > 0) {
			if (s.startsWith("M")) {
				Real2String r2s = new Real2String(s.substring(1));
				path2.moveTo((float)r2s.x, (float)r2s.y);
				s = r2s.s.trim();
			} else if (s.startsWith("L")) {
				Real2String r2s = new Real2String(s.substring(1));
				path2.lineTo((float)r2s.x, (float)r2s.y);
				s = r2s.s.trim();
			} else if (s.startsWith("z") || s.startsWith("Z")) {
				path2.closePath();
				s = s.substring(1).trim();
			}
		}
		return path2;
	}
	
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public GeneralPath getPath2() {
		return path2;
	}

	public void setPath2(GeneralPath path2) {
		this.path2 = path2;
	}
	
	public void applyTransform(Transform2 t2) {
		Real2Array xy = this.getD();
		xy.transformBy(t2);
		setD(xy);
	}
	
	public void format(int places) {
		setD(getD().format(places));
	}
}
class Real2String {
	String s;
	float x;
	float y;
	public Real2String(String s) {
		this.s = s;
		x = grabDouble();
		y = grabDouble();
	}
	
	private float grabDouble() {
		String ss = s;
		float x;
		int idx = s.indexOf(CMLConstants.S_SPACE);
		if (idx != -1) {
			ss = s.substring(0, idx);
			s = s.substring(idx+1);
		} else {
			s = CMLConstants.S_EMPTY;
		}
		try {
			x = (float)new Double(ss).floatValue();
		} catch (Exception e) {
			throw new RuntimeException("bad double:"+ss+" ... "+s);
		}
		return x;
	}
}

