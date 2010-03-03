package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** HTML p element 
 *  @author pm286
 *
 */
public class HtmlP extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlP.class);
	public final static String TAG = "p";

	/** constructor.
	 * 
	 */
	public HtmlP() {
		super(TAG);
	}
}
