package org.xmlcml.cml.html;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlHead extends HtmlElement {

	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public HtmlHead() {
		super("head");
	}
}
