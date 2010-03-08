package org.xmlcml.cml.html;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;

/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public abstract class HtmlElement extends Element implements CMLConstants {
	private final static Logger LOG = Logger.getLogger(HtmlElement.class);

	public enum Target {
		bottom,
		menu,
		separate;
	};
	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public HtmlElement(String name) {
		super(name, XHTML_NS);
	}
	
	public void setAttribute(String name, String value) {
		this.addAttribute(new Attribute(name, value));
	}

	public void setContent(String content) {
		this.appendChild(content);
	}
	
	public void setClass(String value) {
		this.setAttribute("class", value);
	}

	public void setId(String value) {
		this.setAttribute("id", value);
	}

	public void setName(String value) {
		this.setAttribute("name", value);
	}

	public void output(OutputStream os) throws IOException {
		CMLUtil.debug(this, os, 1);
	}

	public void debug(String msg) {
		CMLUtil.debug(this, msg);
	}

	public void setValue(String value) {
		this.removeChildren();
		this.appendChild(value);
	}

}
