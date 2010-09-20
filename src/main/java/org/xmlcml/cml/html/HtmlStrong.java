package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** HTML s element 
 *  @author pm286
 *
 */
public class HtmlStrong extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlStrong.class);
	public final static String TAG = "strong";

	/** constructor.
	 * 
	 */
	public HtmlStrong() {
		super(TAG);
	}
}
