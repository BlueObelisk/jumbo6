package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlHtml extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlHtml.class);
	public final static String TAG = "html";

	private HtmlBody body;
	private HtmlStyle htmlStyle;
	private HtmlHead head;
	
	/** constructor.
	 * 
	 */
	public HtmlHtml() {
		super(TAG);
	}

	public HtmlHead ensureHead() {
		if (head == null) {
			head = new HtmlHead();
			this.insertChild(head, 0);
		}
		return head;
	}

	public HtmlBody ensureBody() {
		if (body == null) {
			body = new HtmlBody();
			this.appendChild(body);
		}
		return body;
	}
	
	public void addCSS(String cssStyle) {
		ensureHead();
		htmlStyle = head.getHtmlStyle(); 
		htmlStyle.addCss(cssStyle);
	}

}
