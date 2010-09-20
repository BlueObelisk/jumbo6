package org.xmlcml.cml.html;

import java.util.ArrayList;
import java.util.List;

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

	public List<HtmlTh> getThChildren() {
		List<HtmlElement> ths = HtmlElement.getChildElements(this, HtmlTh.TAG);
		List<HtmlTh> thList = new ArrayList<HtmlTh>();
		for (HtmlElement th : ths) {
			thList.add((HtmlTh) th);
		}
		return thList;
	}
	
	public List<HtmlTd> getTdChildren() {
		List<HtmlElement> tds = HtmlElement.getChildElements(this, HtmlTd.TAG);
		List<HtmlTd> tdList = new ArrayList<HtmlTd>();
		for (HtmlElement td : tds) {
			tdList.add((HtmlTd) td);
		}
		return tdList;
	}
	
	public HtmlTd getTd(int col) {
		List<HtmlTd> cells = getTdChildren();
		return (col < 0 || col >= cells.size()) ? null : (HtmlTd) cells.get(col);
	}
	
	public HtmlTh getTh(int col) {
		List<HtmlTh> cells = getThChildren();
		return (col < 0 || col >= cells.size()) ? null : (HtmlTh) cells.get(col);
	}

}
