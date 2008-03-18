package org.xmlcml.cml.html;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlFrame extends HtmlElement {

	/** constructor.
	 * 
	 */
	public HtmlFrame(Target name, String src) {
		super("frame");
		this.setName(name.toString());
		this.setSrc(src);
	}
	
	public void setSrc(String src) {
		this.setAttribute("src", src);
	}
	
}
