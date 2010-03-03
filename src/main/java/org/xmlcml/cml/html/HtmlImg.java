package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** 
 * @author pm286
 *
 */
public class HtmlImg extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlImg.class);
	public final static String TAG = "img";

	/** constructor.
	 * 
	 */
	public HtmlImg() {
		super(TAG);
	}
}
