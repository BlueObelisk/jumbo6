package org.xmlcml.cml.html;

import java.util.List;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTbody extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlTbody.class);
	public final static String TAG = "tbody";

	/** constructor.
	 * 
	 */
	public HtmlTbody() {
		super(TAG);
	}
	
	public List<HtmlElement> getRows() {
		return getChildElements(this, HtmlTr.TAG);
	}
}
