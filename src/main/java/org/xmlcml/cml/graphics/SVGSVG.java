package org.xmlcml.cml.graphics;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;

import nu.xom.Attribute;
import nu.xom.Node;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;

/** container for SVG
 * "svg"
 * @author pm286
 *
 */
public class SVGSVG extends SVGElement {

	public final static Logger LOG = Logger.getLogger(SVGSVG.class);
	public final static String TAG = "svg";
	private static String svgSuffix = "svg";
	
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

	public static void wrapAndWriteAsSVG(SVGG svgg, File file) {
		SVGSVG svgsvg = new SVGSVG();
		svgsvg.appendChild(svgg);
		try {
			LOG.trace("Writing SVG "+file.getAbsolutePath());
			CMLUtil.debug(svgsvg, new FileOutputStream(file), 1);
		} catch (Exception e) {
			throw new RuntimeException("cannot write svg to "+file, e);
		}
	}

	public static String createFileName(String id) {
		return id + CMLConstants.S_PERIOD+svgSuffix ;
	}

}
