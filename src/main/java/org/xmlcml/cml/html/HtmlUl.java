package org.xmlcml.cml.html;

import nu.xom.Elements;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlUl extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlUl.class);
	public final static String TAG = "ul";

	/** constructor.
	 * 
	 */
	public HtmlUl() {
		super(TAG);
	}
	
	public Elements getLiElements() {
		return this.getChildElements(HtmlLi.TAG);
	}
}
