package org.xmlcml.cml.html;

import nu.xom.Attribute;

import org.apache.log4j.Logger;


/** 
 * @author pm286
 *
 */
public class HtmlImg extends HtmlElement {
	private static final String SRC = "src";
	private final static Logger LOG = Logger.getLogger(HtmlImg.class);
	public final static String TAG = "img";

	/** constructor.
	 * 
	 */
	public HtmlImg() {
		super(TAG);
	}

	public void setSrc(String src) {
		this.addAttribute(new Attribute(SRC, src));
	}

	public String getSrc() {
		return this.getAttributeValue(SRC);
	}
}
