package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTh extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlTh.class);
	public final static String TAG = "th";

	/** constructor.
	 * 
	 */
	public HtmlTh() {
		super(TAG);
	}
	/**
	 * create a Td with the included text
	 * @param content
	 * @return
	 */
	public static HtmlTh createAndWrapText(String content) {
		HtmlTh th = new HtmlTh();
		th.appendChild(content);
		return th;
	}


}
