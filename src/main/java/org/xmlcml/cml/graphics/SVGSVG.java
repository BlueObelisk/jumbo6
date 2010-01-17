package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;

/** container for SVG
 * "svg"
 * @author pm286
 *
 */
public class SVGSVG extends SVGElement {

	public final static Logger LOG = Logger.getLogger(SVGSVG.class);
	public final static String TAG = "svg";
	
	/** constructor.
	 * 
	 */
	public SVGSVG() {
		super(TAG);
	}
	
	/** constructor
	 */
	public SVGSVG(SVGSVG element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGSVG(this);
    }

	/**
	 * @return tag
	 */

	public String getTag() {
		return TAG;
	}

	/** just draw first g element
	 * 
	 */
	protected void drawElement(Graphics2D g2d) {
		if (this.getChildElements().size() > 0) {
			SVGElement g = (SVGElement) this.getChildElements().get(0);
			g.drawElement(g2d);
		}
	}
	
	public void setId(String id) {
		this.addAttribute(new Attribute("id", id));
	}
	
	public String getId() {
		return this.getAttributeValue("id");
	}

	public static void wrapAndWriteAsSVG(SVGG svgg, String svgfile) {
		SVGSVG svgsvg = new SVGSVG();
		svgsvg.appendChild(svgg);
		try {
		CMLUtil.debug(svgsvg, new FileOutputStream(svgfile), 1);
		} catch (Exception e) {
			throw new RuntimeException("cannot write svg ", e);
		}
	}

}
