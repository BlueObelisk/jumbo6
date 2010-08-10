    package org.xmlcml.cml.html;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.w3c.dom.html.HTMLElement;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;

/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public abstract class HtmlElement extends Element implements CMLConstants {
	private static final String ID = "id";
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
	
	public static HtmlElement create(Element element) {
		HtmlElement htmlElement = null;
		String tag = element.getLocalName();
		if(HtmlA.TAG.equals(tag)) {
			htmlElement = new HtmlA();
		} else if(HtmlB.TAG.equals(tag)) {
			htmlElement = new HtmlB();
		} else if(HtmlBody.TAG.equals(tag)) {
			htmlElement = new HtmlBody();
		} else if(HtmlBr.TAG.equals(tag)) {
			htmlElement = new HtmlBr();
		} else if(HtmlCaption.TAG.equals(tag)) {
			htmlElement = new HtmlCaption();
		} else if(HtmlDiv.TAG.equals(tag)) {
			htmlElement = new HtmlDiv();
		} else if(HtmlFrame.TAG.equals(tag)) {
			htmlElement = new HtmlFrame();
		} else if(HtmlFrameset.TAG.equals(tag)) {
			htmlElement = new HtmlFrameset();
		} else if(HtmlH1.TAG.equals(tag)) {
			htmlElement = new HtmlH1();
		} else if(HtmlH2.TAG.equals(tag)) {
			htmlElement = new HtmlH2();
		} else if(HtmlH3.TAG.equals(tag)) {
			htmlElement = new HtmlH3();
		} else if(HtmlHead.TAG.equals(tag)) {
			htmlElement = new HtmlHead();
		} else if(HtmlHtml.TAG.equals(tag)) {
			htmlElement = new HtmlHtml();
		} else if(HtmlI.TAG.equals(tag)) {
			htmlElement = new HtmlI();
		} else if(HtmlImg.TAG.equals(tag)) {
			htmlElement = new HtmlImg();
		} else if(HtmlLi.TAG.equals(tag)) {
			htmlElement = new HtmlLi();
		} else if(HtmlOl.TAG.equals(tag)) {
			htmlElement = new HtmlH1();
		} else if(HtmlP.TAG.equals(tag)) {
			htmlElement = new HtmlP();
		} else if(HtmlSpan.TAG.equals(tag)) {
			htmlElement = new HtmlSpan();
		} else if(HtmlStyle.TAG.equals(tag)) {
			htmlElement = new HtmlStyle();
		} else if(HtmlSub.TAG.equals(tag)) {
			htmlElement = new HtmlSub();
		} else if(HtmlSup.TAG.equals(tag)) {
			htmlElement = new HtmlSup();
		} else if(HtmlTable.TAG.equals(tag)) {
			htmlElement = new HtmlTable();
		} else if(HtmlTd.TAG.equals(tag)) {
			htmlElement = new HtmlTd();
		} else if(HtmlTh.TAG.equals(tag)) {
			htmlElement = new HtmlTh();
		} else if(HtmlTr.TAG.equals(tag)) {
			htmlElement = new HtmlTr();
		} else if(HtmlUl.TAG.equals(tag)) {
			htmlElement = new HtmlUl();
		} else {
			throw new RuntimeException("Unknown html tag "+tag);
		}
		return htmlElement;
		
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
		this.setAttribute(ID, value);
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

	public String getId() {
		return this.getAttributeValue(ID);
	}

}
