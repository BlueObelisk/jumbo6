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
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;

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

	public final static String D = "d";
	public final static String TAG ="path";
	private GeneralPath path2;
	private boolean isClosed = false;
	private Real2Array coords = null; // for diagnostics

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
	
	public Boolean isClosed() {
		return isClosed;
	}
	public static void setDefaultStyle(SVGPath path) {
		path.setStroke("black");
		path.setStrokeWidth(0.5);
		path.setFill("none");
	}
	
	/** creates a list of primitives
	 * at present Move, Line, Curve, Z
	 * @param d
	 * @return
	 */
	public List<SVGPathPrimitive> parseD() {
		String d = getDString();
		return d == null ? null : SVGPathPrimitive.parseD(d);
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
		this.addAttribute(new Attribute(D, d));
	}
	
	public String getDString() {
		return this.getAttributeValue(D);
	}
	
	
//  <g style="stroke-width:0.2;">
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//</g>
	
	protected void drawElement(Graphics2D g2d) {
		GeneralPath path = createAndSetPath2D();
		g2d.draw(path);
	}

	/** analyses MmLmZz 
	 * 
	 * @param s
	 * @return
	 */
	public static Real2Array createReal2ArrayFromDString(String s) {
		Real2Array r2a = new Real2Array();
		while (s.length() > 0) {
			// crude
			if (s.startsWith("M") ||
				s.startsWith("m") ||
				s.startsWith("L") ||
				s.startsWith("l")) {
				Real2String r2s = new Real2String(s.substring(1));
				r2a.add(r2s.getReal2());
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

	/** extract polyline if path is M followed by L's
	 * @return
	 */
	public SVGPolyline createPolyline() {
		SVGPolyline polyline = null;
		String s = this.getDString().trim()+S_SPACE;
		Real2Array real2Array = new Real2Array();
		boolean started = false;
		int subgraphCount = 0;
		isClosed = false;
		while (s.length() > 0) {
			if (s.startsWith("M")) {
				// disjoint parts?
				if (subgraphCount > 0) {
					return null;
				}
				Real2String r2s = new Real2String(s.substring(1));
				real2Array.add(r2s.getReal2());
				s = r2s.s.trim();
				subgraphCount++;
			} else if (subgraphCount > 0 && s.startsWith("L")) {
				Real2String r2s = new Real2String(s.substring(1));
				real2Array.add(r2s.getReal2());
				s = r2s.s.trim();
			} else if (subgraphCount > 0 && (s.startsWith("z") || s.startsWith("Z"))) {
				// close ??
				String ss = s.substring(1).trim();
				if (ss.trim().length() != 0) {
					return null;
				}
				isClosed = true;
				break;
			} else if (s.charAt(0) == 'c' || s.charAt(0) == 'C') {
				// not a polyline
				return null;
			}
		}
		if (real2Array.size() > 1) {
			polyline = new SVGPolyline(real2Array);
			polyline.setClosed(isClosed);
		}
		return polyline;
	}
	
	public Real2Array getCoords() {
		if (coords == null) {
			coords = new Real2Array();
			String ss = this.getDString().trim()+S_SPACE;
			List<SVGPathPrimitive> primitives = this.createPathPrimitives();
			for (SVGPathPrimitive primitive : primitives) {
				Real2 coord = primitive.getCoords();
				Real2Array coordArray = primitive.getCoordArray();
				if (coord != null) {
					coords.add(coord);
				} else if (coordArray != null) {
					coords.add(coordArray);
				}
			}
		}
		return coords;
	}

	private List<SVGPathPrimitive> createPathPrimitives() {
		return SVGPathPrimitive.parseD(this.getDString());
	}

	/** get bounding box
	 * use coordinates given and ignore effect of curves
	 */
	@Override
	public Real2Range getBoundingBox() {
//		if (boundingBox == null) {
			getCoords();
			boundingBox = coords.getRange2();
//		}
		return boundingBox;
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
			} else {
				throw new RuntimeException("Cannot create path: "+s.charAt(0));
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

	public String getSignature() {
		String sig = null;
		if (getDString() != null) {
			List<SVGPathPrimitive> primitiveList = SVGPathPrimitive.parseD(getDString());
			sig = SVGPathPrimitive.createSignature(primitiveList);
		}
		return sig;
	}

	public void normalizeOrigin() {
		Real2Range boundingBox = this.getBoundingBox();
		if (boundingBox == null) {
			throw new RuntimeException("NULL BoundingBox");
		}
		RealRange xr = boundingBox.getXRange();
		RealRange yr = boundingBox.getYRange();
		Real2 xymin = new Real2(xr.getMin(), yr.getMin());
		xymin = xymin.multiplyBy(-1.0);
		Transform2 t2 = new Transform2(new Vector2(xymin));
		List<SVGPathPrimitive> primitives = this.parseD();
		for (SVGPathPrimitive primitive : primitives) {
			primitive.transformBy(t2);
		}
		this.setD(primitives);
	}

	private void setD(List<SVGPathPrimitive> primitives) {
		String d = constructDString(primitives);
		this.addAttribute(new Attribute(D, d));
	}

	public static String constructDString(List<SVGPathPrimitive> primitives) {
		StringBuilder dd = new StringBuilder();
		for (SVGPathPrimitive primitive : primitives) {
			dd.append(primitive.toString());
		}
		return dd.toString();
	}
}
class Real2String {
	String s;
	String grabbed;
	float x;
	float y;
	int idx = 0;
	
	public Real2String(String s) {
		this.s = s;
		x = grabDouble();
		y = grabDouble();
	}
	
	public Real2 getReal2() {
		return new Real2(x, y);
	}
	
	public int getCharactersGrabbed() {
		return idx;
	}
	
	private float grabDouble() {
		String ss = s.substring(idx)+" ";
		String sss = null;
		float x;
		int idx0 = ss.indexOf(CMLConstants.S_SPACE);
		if (idx0 != -1) {
			sss = ss.substring(0, idx0);
			idx += idx0+1;
		} else {
			sss = CMLConstants.S_EMPTY;
		}
		try {
			x = (float)new Double(sss.substring(0, idx0)).floatValue();
		} catch (Exception e) {
			throw new RuntimeException("bad double ["+sss+"] ... "+s);
		}
		return x;
	}
}

