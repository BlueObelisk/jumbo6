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

	/** constructor.
	 * 
	 */
	public HtmlHead() {
		super(TAG);
	}
}
