package org.xmlcml.cml.html;

import java.util.List;

import nu.xom.Nodes;

import org.apache.log4j.Logger;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTr extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlTr.class);
	public final static String TAG = "tr";

	/** constructor.
	 * 
	 */
	public HtmlTr() {
		super(TAG);
	}

	public List<HtmlElement> getThChildren() {
		return HtmlElement.getChildElements(this, HtmlTh.TAG);
	}
	
	public List<HtmlElement> getTdChildren() {
		return HtmlElement.getChildElements(this, HtmlTd.TAG);
	}
	
	public HtmlTd getTd(int col) {
		List<HtmlElement> cells = getTdChildren();
		return (col < 0 || col >= cells.size()) ? null : (HtmlTd) cells.get(col);
	}
	
	public HtmlTh getTh(int col) {
		List<HtmlElement> cells = getThChildren();
		return (col < 0 || col >= cells.size()) ? null : (HtmlTh) cells.get(col);
	}

}
