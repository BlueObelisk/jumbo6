package org.xmlcml.cml.html;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlFrameset extends HtmlElement {

	/** constructor.
	 * 
	 */
	public HtmlFrameset() {
		super("frameset");
	}
	
	public void setCols(String cols) {
		this.setAttribute("cols", cols);
	}
	
	public void setRows(String rows) {
		this.setAttribute("rows", rows);
	}
}
