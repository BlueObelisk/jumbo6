package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlFrame extends HtmlElement {

	private final static Logger LOG = Logger.getLogger(HtmlFrame.class);
	public final static String TAG = "frame";
	/** constructor.
	 * 
	 */
	public HtmlFrame() {
		super(TAG);
	}
		/** constructor.
		 * 
		 */
	public HtmlFrame(Target name, String src) {
		this();
		this.setName(name.toString());
		this.setSrc(src);
	}
	
	public void setSrc(String src) {
		this.setAttribute("src", src);
	}
	
}
