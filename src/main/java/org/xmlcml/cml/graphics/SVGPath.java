package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGPath extends SVGElement {

	private static Logger LOG = Logger.getLogger(SVGPath.class);
	
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

	// FIXME
	/** at present only does Mx,y Lx,y Lx,y ... z
	 * 
	 */
	public Real2Array getReal2Array() {
		String s = this.getD();
//		StringBuilder sb = new StringBuilder(s);
		if (s.endsWith("z") || s.endsWith("Z")) {
			s = s.substring(0, s.length()-1);
		}
		Real2Array real2Array = new Real2Array();
		if (s.length() == 0 || !s.substring(0, 1).equals("M")) {
			
		} else {
			int ii = s.indexOf("L");
			if (ii != -1) {
				Real2 move = getReal(s.substring(1, ii).trim());
				s = s.substring(ii);
				if (move != null) {
					real2Array.add(move);
					while (true) {
						System.out.println("IDX "+ii);
						ii = s.indexOf("L", 1); 
						if (ii == -1) {
							break;
						}
						//ii;
						System.out.println(""+s+"/"+ii);
						String ss = s.substring(1, ii).trim();
						System.out.println("L"+ss);
						Real2 draw = getReal(ss);
						s = s.substring(ii).trim();
						if (draw == null) {
							LOG.error("Null path element: "+s);
						} else {
							real2Array.add(draw);
						}
					}
				}
			}
		}
		return real2Array;
	}
	
	/** parse string or two reals separated by a comma or spaces.
	 * 
	 * @param s
	 * @return null if cannot parse
	 */
	private Real2 getReal(String s) {
		Real2 real2 = null;
		s = s.replace(S_COMMA, S_SPACE);
		String[] ss = s.split(S_WHITEREGEX);
		if (ss.length == 2) {
			try {
				double x = new Double(ss[0]).doubleValue();
				double y = new Double(ss[1]).doubleValue();
				real2 = new Real2(x, y);
			} catch (Exception e) {
				//
			}
		}
		return real2;
	}
	
	public GeneralPath createAndSetPath2D() {
		path2 = new GeneralPath();
		Real2Array dArray = this.getReal2Array();
		System.out.println("REALARRAY "+dArray);
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
