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

package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class SpanningTreeElement extends AbstractTool {
	final static Logger logger = Logger.getLogger(SpanningTreeElement.class);

	private SpanningTreeElement parent;
	private CMLAtom atom;
	private CMLBond bond;
	private int indent = 0;
	List<SpanningTreeElement> childList;
	
	/**
	 * @param parent
	 * @param atom
	 * @param bond
	 */
	public SpanningTreeElement(SpanningTreeElement parent, CMLAtom atom, CMLBond bond) {
		init();
		this.parent = parent;
		this.atom = atom;
		this.bond = bond;
		if (parent != null) {
			parent.addChild(this);
		}
	}
	
	private void init() {
	}
	
	private void addChild(SpanningTreeElement spe) {
		if (childList == null) {
			childList = new ArrayList<SpanningTreeElement>();
		}
		if (!childList.contains(spe)) {
			childList.add(spe);
		}
		
	}
	/**
	 * @return the indent
	 */
	public int getIndent() {
		return indent;
	}

	/**
	 * @param indent the indent to set
	 */
	public void setIndent(int indent) {
		this.indent = indent;
	}
	

	/**
	 * @return the bond
	 */
	public CMLBond getBond() {
		return bond;
	}

	/**
	 * @param bond the bond to set
	 */
	public void setBond(CMLBond bond) {
		this.bond = bond;
	}

	/**
	 * @return the atom
	 */
	public CMLAtom getAtom() {
		return atom;
	}

	/**
	 * @param atom the atom to set
	 */
	public void setAtom(CMLAtom atom) {
		this.atom = atom;
	}

	/**
	 * @return the parent
	 */
	public SpanningTreeElement getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SpanningTreeElement parent) {
		this.parent = parent;
	}
	
	/**
	 * @return string
	 */
	public String toString() {
		String s = "";
		String indentS = "";
		for (int i = 0; i < indent; i++) {
			indentS += ".. ";
		}
		s += indentS + this.getAtom().getId()+" -> \n";
		if (childList != null) {
			for (SpanningTreeElement child : childList) {
				child.setIndent(indent+1);
				s += child;
			}
		}
		return s;
		
	}

}

