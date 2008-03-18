package org.xmlcml.cml.html;



/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlA extends HtmlElement {

	/** constructor.
	 * 
	 */
	public HtmlA() {
		super("a");
	}
	
	public void setHref(String href) {
		this.setAttribute("href", href);
	}
	
	public void setTarget(Target target) {
		this.setAttribute("target", target.toString());
	}
	
}
