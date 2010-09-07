package org.xmlcml.cml.html;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;


/** 
 * @author pm286
 *
 <table border="1">
  <thead>
    <tr>
      <th>Month</th>
      <th>Savings</th>
    </tr>
  </thead>
  <tfoot>
    <tr>
      <td>Sum</td>
      <td>$180</td>
    </tr>
  </tfoot>
  <tbody>
    <tr>
      <td>January</td>
      <td>$100</td>
    </tr>
    <tr>
      <td>February</td>
      <td>$80</td>
    </tr>
  </tbody>
</table>
 */
public class HtmlTable extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlTable.class);
	public final static String TAG = "table";

	/** constructor.
	 * 
	 */
	public HtmlTable() {
		super(TAG);
	}
	
	public List<HtmlElement> getRows() {
		HtmlTbody tbody = this.getTbody();
		if (tbody != null) {
			return tbody.getRows();
		} else {	
			return getChildElements(this, HtmlTr.TAG);
		}
	}

	public HtmlTbody getTbody() {
		return (HtmlTbody) getSingleChildElement(this, HtmlTbody.TAG); 
	}

	public HtmlTbody getTfoot() {
		return (HtmlTbody) getSingleChildElement(this, HtmlTfoot.TAG); 
	}

	public HtmlTbody getThead() {
		return (HtmlTbody) getSingleChildElement(this, HtmlThead.TAG); 
	}

	public HtmlTr getSingleLeadingTrThChild() {
		List<HtmlElement> rows = getRows();
		HtmlTr tr = null;
		if (rows.size() > 0) {
			Nodes trthNodes = this.query("./*[local-name()='tr' and *[local-name()='th']]");
			if (trthNodes.size() == 1) {
				Element elem = (Element) this.getChildElements().get(0);
				if (elem.equals(trthNodes.get(0))) {
					tr = (HtmlTr) trthNodes.get(0);
				}
			}	
		}
		return tr;
	}
	
	public List<HtmlTr> getTrTdRows() {
		List<HtmlTr> rows = new ArrayList<HtmlTr>();
		Nodes trthNodes = this.query("./*[local-name()='tr' and *[local-name()='td']]");
		for (int i = 0; i < trthNodes.size(); i++) {
			rows.add((HtmlTr)trthNodes.get(i));
		}
		return rows;
	}

	public static HtmlTable getFirstTable(HtmlElement root) {
		Nodes tableNodes = root.query(".//*[local-name()='table']");
		return (tableNodes.size() == 0) ? null : (HtmlTable) tableNodes.get(0);
	}

	public void setBorder(int i) {
		this.addAttribute(new Attribute("border", ""+i));
	}

	public void addRow(HtmlTr row) {
		this.appendChild(row);
	}


}
