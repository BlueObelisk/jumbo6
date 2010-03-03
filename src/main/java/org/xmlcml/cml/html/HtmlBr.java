package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlBr extends HtmlElement {

	private final static Logger LOG = Logger.getLogger(HtmlBr.class);
	public final static String TAG = "br";
	/** constructor.
	 * 
	 */
	public HtmlBr() {
		super(TAG);
	}
}
