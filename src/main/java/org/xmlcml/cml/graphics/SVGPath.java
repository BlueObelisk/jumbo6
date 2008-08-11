package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Array;

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
	
	final static String TAG ="path";
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
		this(getD(xy));
	}
	
	public SVGPath(String d) {
		this();
		setD(d);
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
	
	public static String getD(Real2Array xy) {
		String s = S_EMPTY;
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
	
	
	public void setD(String d) {
		this.addAttribute(new Attribute("d", d));
	}
	
	public String getD() {
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
	
	private class Real2String {
		String s;
		double x;
		double y;
		public Real2String(String s) {
			this.s = s;
			x = grabDouble();
			y = grabDouble();
		}
		private double grabDouble() {
			String ss = s;
			double x;
			int idx = s.indexOf(S_SPACE);
			if (idx != -1) {
				ss = s.substring(0, idx);
				s = s.substring(idx+1);
			} else {
				s = S_EMPTY;
			}
			try {
				x = new Double(ss).doubleValue();
			} catch (Exception e) {
				throw new RuntimeException("bad double:"+ss+" ... "+s);
			}
			return x;
		}
	}
	
	public GeneralPath createAndSetPath2D() {
		String s = this.getD().trim()+S_SPACE;
		path2 = new GeneralPath();
		while (s.length() > 0) {
			if (s.startsWith("M")) {
				Real2String r2s = new Real2String(s.substring(1));
				path2.moveTo(r2s.x, r2s.y);
				s = r2s.s.trim();
			} else if (s.startsWith("L")) {
				Real2String r2s = new Real2String(s.substring(1));
				path2.lineTo(r2s.x, r2s.y);
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
}
