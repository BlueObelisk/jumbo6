package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** 
 *  @author pm286
 */
public class HtmlHr extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlSpan.class);
	public final static String TAG = "hr";

	/** constructor.
	 * 
	 */
	public HtmlHr() {
		super(TAG);
	}
}