package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** HTML s element 
 *  @author pm286
 *
 */
public class HtmlS extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlS.class);
	public final static String TAG = "s";

	/** constructor.
	 * 
	 */
	public HtmlS() {
		super(TAG);
	}
}
