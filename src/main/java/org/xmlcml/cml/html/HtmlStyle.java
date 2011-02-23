package org.xmlcml.cml.html;

import org.apache.log4j.Logger;

public class HtmlStyle  extends HtmlElement {

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlStyle.class);
	public final static String TAG = "style";

	/** constructor.
	 * 
	 */
	public HtmlStyle() {
		super(TAG);
	}
	
	public void addCss(String cssStyle) {
		String value = this.getValue();
		value += "\n"+cssStyle;
		this.removeChildren();
		this.appendChild(value);
	}
}
