package org.xmlcml.cml.graphics;

import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;

/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public /*abstract*/ class GraphicsElement extends Element implements SVGConstants {

	protected Transform2 cumulativeTransform = new Transform2();
//	protected StyleBundle styleBundle;
		
	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public GraphicsElement(String name, String namespace) {
		super(name, namespace);
		init();
	}
	
    /**
     * main constructor.
     * 
     * @param name tagname
     */
    public GraphicsElement(String name) {
        this(name, SVG_NAMESPACE);
        init();
    }
    
    protected void init() {
    	setDefaultStyle();
    }
    
    public void setDefaultStyle() {
//		setOpacity(1.0);
    }
    
    /**
     * copy constructor. copies attributes, children and properties using the
     * copyFoo() routines (q.v.)
     * 
     * @param element
     */
    public GraphicsElement(GraphicsElement element) {
        this(element.getLocalName());
        copyAttributesFrom(element);
        copyChildrenFrom(element);
        copyNamespaces(element);
    }

    /**
     * copies namespaces.
     * @param element to copy from
     */
    public void copyNamespaces(GraphicsElement element) {
        int n = element.getNamespaceDeclarationCount();
        for (int i = 0; i < n; i++) {
            String namespacePrefix = element.getNamespacePrefix(i);
            String namespaceURI = element.getNamespaceURIForPrefix(namespacePrefix);
            this.addNamespaceDeclaration(namespacePrefix, namespaceURI);
        }
    }

    /**
     * copies attributes. makes subclass if necessary.
     * 
     * @param element to copy from
     */
    public void copyAttributesFrom(Element element) {
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute att = element.getAttribute(i);
            Attribute newAtt = (Attribute) att.copy();
            this.addAttribute(newAtt);
        }
    }

    
    /** copies children of element make subclasses when required
     * 
     * @param element to copy from
     */
    public void copyChildrenFrom(Element element) {
        for (int i = 0; i < element.getChildCount(); i++) {
            Node childNode = element.getChild(i);
            Node newNode = childNode.copy();
            this.appendChild(newNode);
        }
    }
    
    
    /**
     * copy node.
     * 
     * @return node
     */
    public Node copy() {
        return new GraphicsElement(this);
    }

    /**
     * get namespace.
     * 
     * @param prefix
     * @return namespace
     */
    public String getNamespaceURIForPrefix(String prefix) {
        String namespace = null;
        Element current = this;
        while (true) {
            namespace = current.getNamespaceURI(prefix);
            if (namespace != null) {
                break;
            }
            Node parent = current.getParent();
            if (parent == null || parent instanceof Document) {
                break;
            }
            current = (Element) parent;
        }
        return namespace;
    }

    public void applyStyles(StyleBundle styleBundle) {
    	this.addAttribute(new Attribute("style", styleBundle.toString()));
    }
    
    public void setSvgClass(String svgClass) {
    	this.addAttribute(new Attribute("class", svgClass));
    }
    
    public String getSvgClass() {
    	return this.getAttributeValue("class");
    }
    
	/**
	 * @return the fill
	 */
	public String getFill() {
		return (String) getSubStyle("fill");
	}

	/**
	 * @param fill the fill to set
	 */
	public void setFill(String fill) {
		setSubStyle("fill", fill);
	}

	/**
	 * @return the fill
	 */
	public String getStroke() {
		return (String) getSubStyle("stroke");
	}

	/**
	 * @param fill the fill to set
	 */
	public void setStroke(String stroke) {
		setSubStyle("stroke", stroke);
	}

	/**
	 * @return the font
	 */
	public String getFontFamily() {
		return (String) getSubStyle("font-family");
	}

	/**
	 * @param fill the fill to set
	 */
	public void setFontFamily(String fontFamily) {
		setSubStyle("font-family", fontFamily);
	}

	/**
	 * @return the font
	 */
	public String getFontStyle() {
		return (String) getSubStyle("font-style");
	}

	/**
	 * @param fill the fill to set
	 */
	public void setFontStyle(String fontStyle) {
		setSubStyle("font-style", fontStyle);
	}

	/**
	 * @return the font
	 */
	public String getFontWeight() {
		return (String) getSubStyle("font-weight");
	}

	/**
	 * @param fill the font weight to set
	 */
	public void setFontWeight(String fontWeight) {
		setSubStyle("font-weight", fontWeight);
	}

	/**
	 * @return the opacity (1.0 if not present or error
	 */
	public double getOpacity() {
		Double opacity = (Double) getSubStyle("opacity");
		return (opacity == null) ? Double.NaN : opacity.doubleValue();
	}

	/**
	 * @param opacity the opacity to set
	 */
	public void setOpacity(double opacity) {
		setSubStyle("opacity", new Double(opacity));
	}

	/**
	 * @return the stroke-width (default if not present or error)
	 */
	public double getStrokeWidth() {
		Double strokeWidth = (Double) getSubStyle("stroke-width");
		return (strokeWidth == null) ? Double.NaN : strokeWidth.doubleValue();
	}

	/**
	 * 
	 * @param strokeWidth
	 */
	public void setStrokeWidth(double strokeWidth) {
		setSubStyle("stroke-width", new Double(strokeWidth));
	}

	/**
	 * @return the font-size 
	 */
	public double getFontSize() {
		Double fontSize = (Double) getSubStyle("font-size");
		return (fontSize == null) ? Double.NaN : fontSize.doubleValue();
	}

	/**
	 * 
	 * @param fontSize
	 */
	public void setFontSize(double fontSize) {
		setSubStyle("font-size", new Double(fontSize));
	}

	protected String getTag() {
		return "DUMMY";
	}
	
	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public static void test(String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		SVGSVG svg = new SVGSVG();
		SVGElement g = new SVGG();
		g.setFill("yellow");
		svg.appendChild(g);
		SVGLine line = new SVGLine(new Real2(100, 200), new Real2(300, 50));
		line.setFill("red");
		line.setStrokeWidth(3);
		line.setStroke("blue");
		g.appendChild(line);
		SVGCircle circle = new SVGCircle(new Real2(300, 150), 20);
		circle.setStroke("red");
		circle.setFill("yellow");
		circle.setStrokeWidth(3.);
		g.appendChild(circle);
		SVGElement text = new SVGText(new Real2(50, 100), "Foo");
		text.setFontFamily("TimesRoman");
		text.setStroke("green");
		text.setFill("red");
		text.setStrokeWidth(1.5);
		text.setFontSize(20);
		text.setFontWeight("bold");
		g.appendChild(text);
		CMLUtil.debug(svg, fos, 2);
		fos.close();		
	}
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			test(args[0]);
		}
	}

	/**
	 * @return the cumulativeTransform
	 */
	public Transform2 getCumulativeTransform() {
		return cumulativeTransform;
	}

	/**
	 * @param cumulativeTransform the cumulativeTransform to set
	 */
	public void setCumulativeTransform(Transform2 cumulativeTransform) {
		this.cumulativeTransform = cumulativeTransform;
	}

	private Object getSubStyle(String s) {
		StyleBundle styleBundle = getStyleBundle();
		return (styleBundle == null) ? null : styleBundle.getSubStyle(s);
	}

	public StyleBundle getStyleBundle() {
		StyleBundle styleBundle = null;
		String style = this.getAttributeValue("style");
		if (style != null) {
			styleBundle = new StyleBundle(style);
		}
		return styleBundle;
	}
	
	private void setSubStyle(String ss, Object object) {
		StyleBundle styleBundle = getStyleBundle();
		if (styleBundle == null) {
			styleBundle = new StyleBundle();
		}
		styleBundle.setSubStyle(ss, object);
		applyStyles(styleBundle);
	}

	public void debug(String msg) {
		CMLUtil.debug(this, msg);
	}
}

