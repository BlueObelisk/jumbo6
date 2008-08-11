package org.xmlcml.cml.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.html.HtmlElement.Target;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlMenuSystem {

	private HtmlHtml menu;
	private String menuName = null;
	private HtmlHead head;
	private HtmlBody body;
	private HtmlUl ul;
	
	private HtmlHtml indexFrame;
	private String indexFrameName = null;
	private HtmlFrameset menuFrameset;
	private HtmlFrame menuFrame;
	private HtmlFrame bottomFrame;
	
	private HtmlHtml bottom;
	private String bottomName = null;
	
	@SuppressWarnings("unused")
	private String outdir;
	
	/** constructor.
	 */
	public HtmlMenuSystem() {
		makeBottom();
		makeIndexFrame();
		makeMenu();
	}

	private void makeBottom() {
		bottom = new HtmlHtml();
		head = new HtmlHead();
		bottom.appendChild(head);
		body = new HtmlBody();
		bottom.appendChild(body);
		body.appendChild("Images will appear here");
	}
	
	private void makeMenu() {
		menu = new HtmlHtml();
		head = new HtmlHead();
		menu.appendChild(head);
		body = new HtmlBody();
		menu.appendChild(body);
		ul = new HtmlUl();
		body.appendChild(ul);
	}
	
	private void makeIndexFrame() {
		indexFrame = new HtmlHtml();
		menuFrameset = new HtmlFrameset();
		menuFrameset.setCols("100, *");
		indexFrame.appendChild(menuFrameset);
		menuFrame = new HtmlFrame(Target.menu, "menu.html");
		menuFrameset.appendChild(menuFrame);
		bottomFrame = new HtmlFrame(Target.bottom, "bottom.html");
		menuFrameset.appendChild(bottomFrame);
	}
	
	public HtmlUl getUl() {
		return ul;
	}

	public HtmlA addA(String href, Target target, String content) {
		HtmlLi li = new HtmlLi();
		ul.appendChild(li);
		HtmlA a = new HtmlA();
		a.setHref(href);
		a.setTarget(target);
		a.appendChild(content);
		li.appendChild(a);
		return a;
	}
	
	public void setOutdir(String outdir) {
		menuName = outdir+File.separator+"menu.html";		
		indexFrameName = outdir+File.separator+"indexFrame.html";		
		bottomName = outdir+File.separator+"bottom.html";		
		System.out.println("HTML OUTPUT DIR "+outdir);
		this.outdir = outdir;
	}

	public void outputMenuAndBottomAndIndexFrame() throws IOException {
		FileOutputStream fos = new FileOutputStream(menuName);
		CMLUtil.debug(menu, fos, 1);
		fos.close();
		fos = new FileOutputStream(indexFrameName);
		CMLUtil.debug(indexFrame, fos, 1);
		fos.close();
		fos = new FileOutputStream(bottomName);
		CMLUtil.debug(bottom, fos, 1);
		fos.close();
	}

	public void addHRef(String outputFile) {
		String svgFile = outputFile.substring(outputFile.lastIndexOf(File.separator)+1);
		this.addA(svgFile, Target.bottom, svgFile+" ");
	}


	public HtmlHtml getMenu() {
		return menu;
	}

	public HtmlHtml getIndexFrame() {
		return indexFrame;
	}
}
