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
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Real;
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
	private static final double EPS1 = 0.000001;
	private GeneralPath path2;
	private boolean isClosed = false;
	private Real2Array coords = null; // for diagnostics
	private SVGPolyline polyline;
	private Real2Array allCoords;
	private List<SVGPathPrimitive> primitiveList;
	private Boolean isPolyline;
	private Real2Array firstCoords;

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
	
	public boolean isClosed() {
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

	/** extract polyline if path is M followed by L's
	 * @return
	 */
	public void createCoordArray() {
		polyline = null;
		allCoords = new Real2Array();
		firstCoords = new Real2Array();
		ensurePrimitives();
		isPolyline = true;
		for (SVGPathPrimitive primitive : primitiveList) {
			if (primitive instanceof CurvePrimitive) {
				isPolyline = false;
				Real2Array curveCoords = primitive.getCoordArray();
				allCoords.add(curveCoords);
				firstCoords.add(primitive.getFirstCoord());
//				break;
			} else if (primitive instanceof ClosePrimitive) {
				isClosed = true;
			} else {
				Real2 r2 = primitive.getCoords();
				allCoords.add(r2);
				firstCoords.add(primitive.getFirstCoord());
			}
		}
	}
	
	public SVGPolyline createPolyline() {
		createCoordArray();
		if (isPolyline && allCoords.size() > 1) {
			polyline = new SVGPolyline(allCoords);
			polyline.setClosed(isClosed);
		}
		return polyline;
	}
	
	public SVGRect createRectangle(double epsilon) {
		createPolyline();
		SVGRect rect = null;
		if (polyline != null && polyline.isBox(epsilon)) {
			Real2Range r2r = this.getBoundingBox();
			rect = new SVGRect(this.getOrigin(), this.getUpperRight());
			rect.setFill("none");
		}
		return rect;
	}

	public SVGSymbol createSymbol(double maxWidth) {
		createCoordArray();
		SVGSymbol symbol = null;
		Real2Range r2r = this.getBoundingBox();
		if (Math.abs(r2r.getXRange().getRange()) < maxWidth && Math.abs(r2r.getYRange().getRange()) < maxWidth) {
			symbol = new SVGSymbol();
			SVGPath path = (SVGPath)this.copy();
			Real2 orig = path.getOrigin();
			path.normalizeOrigin();
			SVGLine line = path.createHorizontalOrVerticalLine(EPS);
			symbol.appendChild(path);
			symbol.setId(path.getId()+".s");
			List<SVGElement> defsNodes = SVGUtil.getQuerySVGElements(this,"/svg:svg/svg:defs");
			defsNodes.get(0).appendChild(symbol);
		}
		return symbol;
	}

	private SVGLine createHorizontalOrVerticalLine(double eps) {
		SVGLine  line = null;
		Real2Array coords = this.getCoords();
		if (coords.size() == 2) {
			line = new SVGLine(coords.get(0), coords.get(1));
			if (!line.isHorizontal(eps) && !line.isVertical(eps)) {
				line = null;
			}
		}
		return  line;
	}

	/** sometimes polylines represent circles
	 * crude algorithm - assume points are roughly equally spaced
	 * @param maxSize
	 * @param epsilon
	 * @return
	 */
	public SVGCircle createCircle(double epsilon) {
		createCoordArray();
		SVGCircle circle = null;
		if (isClosed && allCoords.size() >= 8) {
			Real2Range r2r = this.getBoundingBox();
			if (Real.isEqual(r2r.getXRange().getRange(),  r2r.getYRange().getRange(), 2*epsilon)) {
				Real2 centre = r2r.getCentroid();
				Double sum = 0.0;
				double[] spokeLengths = new double[firstCoords.size()];
				for (int i = 0; i < firstCoords.size(); i++) {
					Double spokeLength = centre.getDistance(firstCoords.get(i));
					spokeLengths[i] = spokeLength;
					sum += spokeLength;
				}
				Double rad = sum / firstCoords.size();
				for (int i = 0; i < spokeLengths.length; i++) {
					if (Math.abs(spokeLengths[i] - rad) > epsilon) {
						return null;
					}
				}
				circle = new SVGCircle(centre, rad);
			}
		}
		return circle;
	}

	public List<SVGPathPrimitive> ensurePrimitives() {
		isClosed = false;
		if (primitiveList == null) {
			primitiveList = this.createPathPrimitives();
		}
		if (primitiveList != null && primitiveList.size() > 1) {
			SVGPathPrimitive primitive0 = primitiveList.get(0);
			SVGPathPrimitive primitiveEnd = primitiveList.get(primitiveList.size() - 1);
			Real2 coord0 = primitive0.getFirstCoord();
			Real2 coordEnd = primitiveEnd.getLastCoord();
			isClosed = coord0.getDistance(coordEnd) < EPS1;
		}
		return primitiveList;
	}

	/**
	 * do two paths have identical coordinates?
	 * @param svgPath 
	 * @param path2
	 * @param epsilon tolerance allowed
	 * @return
	 */
	public boolean hasEqualCoordinates(SVGPath path2, double epsilon) {
		Real2Array r2a = this.getCoords();
		Real2Array r2a2 = path2.getCoords();
		return r2a.isEqualTo(r2a2, epsilon);
	}
	
	public Real2Array getCoords() {
//		if (coords == null) {
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
//		}
		return coords;
	}
	
	/**
	 * scale of bounding boxes
	 * scale = Math.sqrt(xrange2/this.xrange * yrange2/this.yrange) 
	 * (Can be used to scale vector fonts or other scalable objects)
	 * @param path2
	 * @return null if problem (e.g. zero ranges). result may be zero
	 */
	public Double getBoundingBoxScalefactor(SVGPath path2) {
		Double s = null;
		if (path2 != null) {
			Real2Range bb = this.getBoundingBox();
			Real2Range bb2 = path2.getBoundingBox();
			double xr = bb.getXRange().getRange();
			double yr = bb.getYRange().getRange();
			if (xr > EPS && yr > EPS) {
				s = Math.sqrt(bb2.getXRange().getRange()/xr * bb2.getYRange().getRange()/yr);
			}
		}
		return s;
	}
	
	/**
	 * compares paths scaled by bounding boxes and then compares coordinates
	 * @param path2
	 * @return null if paths cannot be scaled else bounding box ratio
	 */
	public Double getScalefactor(SVGPath path2, double epsilon) {
		Double s = this.getBoundingBoxScalefactor(path2);
		if (s != null) {
			SVGPath path = (SVGPath) this.copy();
			path.normalizeOrigin();
			Transform2 t2 = new Transform2(new double[]{s,0.,0.,0.,s,0.,0.,0.,1.});
			path.applyTransform(t2);
			SVGPath path22 = (SVGPath) path2.copy();
			path22.normalizeOrigin();
			if (!path.hasEqualCoordinates(path22, epsilon)) {
				s = null;
			}
		}
		return s;
	}

	private List<SVGPathPrimitive> createPathPrimitives() {
		return SVGPathPrimitive.parseD(this.getDString());
	}

	/** get bounding box
	 * use coordinates given and ignore effect of curves
	 */
	@Override
	public Real2Range getBoundingBox() {
		if (boundingBox == null) {
			getCoords();
			boundingBox = coords.getRange2();
		}
		return boundingBox;
	}
	
	/** property of graphic bounding box
	 * can be overridden
	 * @return default none
	 */
	protected String getBBFill() {
		return "none";
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default blue
	 */
	protected String getBBStroke() {
		return "blue";
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default 0.1
	 */
	protected double getBBStrokeWidth() {
		return 0.1;
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
		List<SVGPathPrimitive> pathPrimitives = this.createPathPrimitives();
		for (SVGPathPrimitive primitive : pathPrimitives) {
			primitive.transformBy(t2);
		}
		setD(pathPrimitives);
	}
	
//	public void format(int places) {
//		setD(getD().format(places));
//	}

	public String getSignature() {
		String sig = null;
		if (getDString() != null) {
			List<SVGPathPrimitive> primitiveList = SVGPathPrimitive.parseD(getDString());
			sig = SVGPathPrimitive.createSignature(primitiveList);
		}
		return sig;
	}

	public void normalizeOrigin() {
		boundingBox = null; // fprce recalculate
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

	public Real2 getOrigin() {
		Real2Range r2r = this.getBoundingBox();
		return new Real2(r2r.getXRange().getMin(), r2r.getYRange().getMin());
	}

	/** opposite corner to origin
	 * 
	 * @return
	 */
	public Real2 getUpperRight() {
		Real2Range r2r = this.getBoundingBox();
		return new Real2(r2r.getXRange().getMax(), r2r.getYRange().getMax());
	}

	// there are some polylines which contain a small number of curves and may be transformable
	public SVGPolyline createHeuristicPolyline(int minL, int maxC, int minPrimitives) {
		SVGPolyline polyline = null;
		String signature = this.getSignature();
		if (signature.length() < 3) {
			return null;
		}
		// must start with M
		if (signature.charAt(0) != 'M') {
			return null;
		}
		// can only have one M
		if (signature.substring(1).indexOf("M") != -1) {
			return null;
		}
		signature.replaceAll("[^C]", "").length();
		StringBuilder sb = new StringBuilder();
		if (signature.length() >= minPrimitives) {
			int cCount = signature.replaceAll("[^C]", "").length();
			int lCount = signature.replaceAll("[^L]", "").length();
			if (lCount >= minL && maxC >= cCount) {
				for (SVGPathPrimitive primitive : primitiveList) {
					if (primitive instanceof CurvePrimitive) {
						sb.append("L"+primitive.getLastCoord().toString());
					} else {
						sb.append(primitive.toString());
					}
				}
			}
			SVGPath path = new SVGPath(sb.toString());
			polyline = new SVGPolyline(path);
		}
		return polyline;
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

