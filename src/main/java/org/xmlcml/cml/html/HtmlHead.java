package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlHead extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlHead.class);
	public final static String TAG = "head";

	private HtmlStyle style;
	
	/** constructor.
	 * 
	 */
	public HtmlHead() {
		super(TAG);
	}

	public HtmlStyle getHtmlStyle() {
		ensureHtmlStyle();
		return style;
	}

	private void ensureHtmlStyle() {
		if (style == null) {
			style = new HtmlStyle();
			this.appendChild(style);
		}
	}
}
