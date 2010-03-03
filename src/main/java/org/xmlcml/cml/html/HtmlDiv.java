package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlDiv extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlDiv.class);
	public final static String TAG = "div";

	/** constructor.
	 * 
	 */
	public HtmlDiv() {
		super(TAG);
	}
}
