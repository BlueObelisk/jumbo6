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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLAtom;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class AtomPath implements Comparable<AtomPath> {
	final static Logger LOG = Logger.getLogger(RingNucleus.class);

	private List<CMLAtom> atomList;

	/**
	 */
	public AtomPath() {
		init();
		atomList = new ArrayList<CMLAtom>();
	}

	protected void init() {

	}

	/**
	 * @param atom
	 * @return success
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(CMLAtom atom) {
		return atomList.add(atom);
	}

	/**
	 * @param index
	 * @param atom
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, CMLAtom atom) {
		atomList.add(index, atom);
	}

	/**
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() {
		atomList.clear();
	}

	/**
	 * @param atom
	 * @return success
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object atom) {
		return atomList.contains(atom);
	}

	/**
	 * @param o
	 * @return true if equals
	 * @see java.util.List#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return atomList.equals(o);
	}

	/**
	 * @param index
	 * @return atom
	 * @see java.util.List#get(int)
	 */
	public CMLAtom get(int index) {
		return atomList.get(index);
	}

	/**
	 * @return hash
	 * @see java.util.List#hashCode()
	 */
	public int hashCode() {
		return atomList.hashCode();
	}

	/**
	 * @param atom
	 * @return index or -1
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object atom) {
		return atomList.indexOf(atom);
	}

	/**
	 * @return true if empty
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return atomList.isEmpty();
	}

	/**
	 * @return iterator
	 * @see java.util.List#iterator()
	 */
	public Iterator<CMLAtom> iterator() {
		return atomList.iterator();
	}

	/**
	 * @param atom
	 * @return last index or -1
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object atom) {
		return atomList.lastIndexOf(atom);
	}

	/**
	 * @return iterator
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<CMLAtom> listIterator() {
		return atomList.listIterator();
	}

	/**
	 * @param index
	 * @return iterator
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<CMLAtom> listIterator(int index) {
		return atomList.listIterator(index);
	}

	/**
	 * @param index
	 * @return atom removed
	 * @see java.util.List#remove(int)
	 */
	public CMLAtom remove(int index) {
		return atomList.remove(index);
	}

	/**
	 * @param atom
	 * @return success
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object atom) {
		return atomList.remove(atom);
	}

	/**
	 * @param index
	 * @param element
	 * @return atom set
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public CMLAtom set(int index, CMLAtom element) {
		return atomList.set(index, element);
	}

	/**
	 * @return size
	 * @see java.util.List#size()
	 */
	public int size() {
		return atomList.size();
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return subList
	 * @see java.util.List#subList(int, int)
	 */
	public List<CMLAtom> subList(int fromIndex, int toIndex) {
		return atomList.subList(fromIndex, toIndex);
	}

	/**
	 * @return CMLAtom[]
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return atomList.toArray();
	}

	/**
	 * @param <T>
	 * @param a
	 * @return array of CMLAtom[]
	 * @see java.util.List#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return atomList.toArray(a);
	}

	/**
	 * @return the atomList
	 */
	public List<CMLAtom> getAtomList() {
		return atomList;
	}

	/**
	 * @param atomList
	 *            the atomList to set
	 */
	public void setAtomList(List<CMLAtom> atomList) {
		this.atomList = atomList;
	}

	/**
	 * returns AtomPath with atoms in reverse order
	 * 
	 * @return reversed path
	 */
	public AtomPath getReversePath() {
		AtomPath reverse = new AtomPath();
		int size = this.size();
		for (int i = size - 1; i >= 0; i--) {
			reverse.add(this.get(i));
		}
		return reverse;
	}

	/**
	 * compares on length of path
	 * 
	 * @param atomPath
	 * @return -1 0 1
	 */
	public int compareTo(AtomPath atomPath) {
		int result = 0;
		if (this.size() < atomPath.size()) {
			result = -1;
		} else if (this.size() > atomPath.size()) {
			result = 1;
		}
		return result;
	}

	/**
	 * get list of atomIds
	 * 
	 * @return string
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (CMLAtom atom : atomList) {
			sb.append(".." + atom.getId());
		}
		return sb.toString();
	}

}
