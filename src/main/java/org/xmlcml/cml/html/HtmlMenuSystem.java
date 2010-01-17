package org.xmlcml.cml.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.html.HtmlElement.Target;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlMenuSystem {
	private static Logger LOG = Logger.getLogger(HtmlMenuSystem.class);

	private final static String DEFAULT_HTML_SUFFIX = "html";
	private final static String DEFAULT_MENU_ROOT = "menu";
	private final static int DEFAULT_MENU_WIDTH = 150;
	private final static String DEFAULT_INDEXFRAME_ROOT = "indexFrame";
	private final static String DEFAULT_BOTTOM_ROOT = "bottom";
	private final static String DEFAULT_BOTTOM_WELCOME = "Images will appear here";
	
	private HtmlHtml menu;
	private String menuRootName = DEFAULT_MENU_ROOT;
	private String htmlSuffix = DEFAULT_HTML_SUFFIX;
	private String menuFilename = createMenuFilename();
	private int menuWidth = DEFAULT_MENU_WIDTH;
	
	private HtmlHead head;
	private HtmlBody body;
	private HtmlUl ul;
	
	private HtmlHtml indexFrame;
	private String indexFrameRootName = DEFAULT_INDEXFRAME_ROOT;
	private String indexFrameFilename = createIndexFrameFilename();
	
	private HtmlFrameset menuFrameset;
	private HtmlFrame menuFrame;
	private HtmlFrame bottomFrame;
	
	private HtmlHtml bottom;
	private String bottomRootName = DEFAULT_BOTTOM_ROOT;
	private String bottomFilename = createBottomFilename();
	private String bottomWelcome = DEFAULT_BOTTOM_WELCOME;
	
	@SuppressWarnings("unused")
	private String outdir;
	
	/** constructor.
	 */
	public HtmlMenuSystem() {
		makeBottom();
		makeIndexFrame();
		makeMenu();
	}

	private String createBottomFilename() {
		return bottomRootName+CMLConstants.S_PERIOD+htmlSuffix;
	}

	private String createIndexFrameFilename() {
		return indexFrameRootName+CMLConstants.S_PERIOD+htmlSuffix;
	}

	private String createMenuFilename() {
		return menuRootName+CMLConstants.S_PERIOD+htmlSuffix;
	}

	private void makeBottom() {
		bottom = new HtmlHtml();
		head = new HtmlHead();
		bottom.appendChild(head);
		body = new HtmlBody();
		bottom.appendChild(body);
		body.appendChild(bottomWelcome);
	}
	
	private void makeMenu() {
		menu = new HtmlHtml();
		head = new HtmlHead();
		menu.appendChild(head);
		body = new HtmlBody();
		menu.appendChild(body);
		ensureUl();
		body.appendChild(ul);
	}
	
	private void makeIndexFrame() {
		indexFrame = new HtmlHtml();
		menuFrameset = new HtmlFrameset();
		menuFrameset.setCols(""+menuWidth+", *");
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
		ensureUl();
		HtmlLi li = new HtmlLi();
		ul.appendChild(li);
		HtmlA a = new HtmlA();
		a.setHref(href);
		a.setTarget(target);
		a.appendChild(content);
		li.appendChild(a);
		return a;
	}
	
	private HtmlUl ensureUl() {
		if (ul == null) {
			ul = new HtmlUl();
		}
		return ul;
	}

	public void setOutdir(String outdir) {
		menuFilename = outdir+File.separator + createMenuFilename();		
		indexFrameFilename = outdir+File.separator+ createIndexFrameFilename();		
		bottomFilename = outdir+File.separator+createBottomFilename();		
		this.outdir = outdir;
	}

	public void outputMenuAndBottomAndIndexFrame() throws IOException {
		FileOutputStream fos = new FileOutputStream(menuFilename);
		CMLUtil.debug(menu, fos, 1);
		fos.close();
		fos = new FileOutputStream(indexFrameFilename);
		CMLUtil.debug(indexFrame, fos, 1);
		fos.close();
		fos = new FileOutputStream(bottomFilename);
		CMLUtil.debug(bottom, fos, 1);
		fos.close();
	}

	public void setMenuRootName(String menuRootName) {
		this.menuRootName = menuRootName;
	}

	public void setMenuWidth(int menuWidth) {
		this.menuWidth = menuWidth;
	}

	public void setIndexFrameRootName(String indexFrameRootName) {
		this.indexFrameRootName = indexFrameRootName;
	}

	public void setBottomRootName(String bottomRootName) {
		this.bottomRootName = bottomRootName;
	}

	public void setBottomWelcome(String bottomWelcome) {
		this.bottomWelcome = bottomWelcome;
	}

	public void addHRef(String outputFile) {
		String name = new File(outputFile).getName();
		String content = name+" ";
		this.addA(name, Target.bottom, content);
	}


	public HtmlHtml getMenu() {
		return menu;
	}

	public HtmlHtml getIndexFrame() {
		return indexFrame;
	}
}
