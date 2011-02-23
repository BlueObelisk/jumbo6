package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTfoot extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlTfoot.class);
	public final static String TAG = "tfoot";

	/** constructor.
	 * 
	 */
	public HtmlTfoot() {
		super(TAG);
	}
	
	
}
