/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.cml.graphics;

import nu.xom.Element;
import nu.xom.Node;

/** supports clipPath (dummy at present)
 * 
 * @author pm286
 *
 */
/**
 * @deprecated "use SVG-DEV package"
 */
@Deprecated

public class SVGClipPath extends SVGElement {

	public final static String TAG ="clipPath";
	/** constructor
	 */
	public SVGClipPath() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public SVGClipPath(SVGElement element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGClipPath(Element element) {
        super((SVGElement) element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGClipPath(this);
    }

	

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	
}
