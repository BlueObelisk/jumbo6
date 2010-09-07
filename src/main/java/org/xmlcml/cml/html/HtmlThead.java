package org.xmlcml.cml.html;

import org.apache.log4j.Logger;


/** 
 <thead>
    <tr>
      <th>Month</th>
      <th>Savings</th>
    </tr>
  </thead>

 * @author pm286
 *
 */
public class HtmlThead extends HtmlElement {
	private final static Logger LOG = Logger.getLogger(HtmlThead.class);
	public final static String TAG = "thead";

	/** constructor.
	 * 
	 */
	public HtmlThead() {
		super(TAG);
	}
}
