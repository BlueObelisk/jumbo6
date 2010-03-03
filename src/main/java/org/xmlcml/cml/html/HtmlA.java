package org.xmlcml.cml.html;

import org.apache.log4j.Logger;



/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlA extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlA.class);
	public final static String TAG = "a";
	
	/** constructor.
	 * 
	 */
	public HtmlA() {
		super(TAG);
	}
	
	public void setHref(String href) {
		this.setAttribute("href", href);
	}
	
	public void setTarget(Target target) {
		this.setAttribute("target", target.toString());
	}
	
}
