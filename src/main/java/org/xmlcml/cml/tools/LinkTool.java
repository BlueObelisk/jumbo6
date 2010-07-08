package org.xmlcml.cml.tools;



import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMap.Direction;

/**
 * tool for managing length
 *
 * @author pmr
 *
 */
public class LinkTool extends AbstractTool {
	final static Logger logger = Logger.getLogger(LinkTool.class.getName());

	
	private CMLLink link;
	
	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public LinkTool(CMLLink link) throws RuntimeException {
		init();
		this.link = link;
	}


	void init() {
	}

	public static CMLLink makeLink(String title, CMLAtomSet fromSet, CMLAtomSet toSet) {
		CMLLink cmlLink = new CMLLink();
		cmlLink.setTitle(title);
		cmlLink.setFromSet(fromSet.getXMLContent());
		cmlLink.setToSet(toSet.getXMLContent());
		return cmlLink;
	}

	/**
	 * get link
	 *
	 * @return
	 */
	public CMLLink getLink() {
		return this.link;
	}

    
	static int getLinkSetLength(CMLLink link, Direction direction) {
		String[] set = (direction.equals(Direction.FROM)) ? link.getFromSet() : link.getToSet();
		return (set == null) ? 0 : set.length;
	}


	/** gets LengthTool associated with length.
	 * if null creates one and sets it in length
	 * @param length
	 * @return tool
	 */
	public static LinkTool getOrCreateTool(CMLLink link) {
		LinkTool linkTool = null;
		if (link != null) {
			linkTool = (LinkTool) link.getTool();
			if (linkTool == null) {
				linkTool = new LinkTool(link);
				link.setTool(linkTool);
			}
		}
		return linkTool;
	}

	/**
	 * 
	 * @param direction
	 * @param atomSet
	 * @return empty set if no attribute or no atoms found
	 */
	public CMLAtomSet getSet(Direction direction, CMLAtomSet atomSet) {
		CMLAtomSet atomSet1 = new CMLAtomSet();
		String[] ids = (direction.equals(Direction.FROM)) ?
				link.getFromSet() : link.getToSet();
		if (ids != null) {
			atomSet1 = atomSet.getAtomSetById(ids);
		}
		return atomSet1;
	}
	
	public void addSingleAtomsToSets(CMLAtom fromAtom, CMLAtom toAtom) {
		CMLAtomSet newFromAtomSet = new CMLAtomSet();
		newFromAtomSet.addAtom(fromAtom);
		CMLAtomSet newToAtomSet = new CMLAtomSet();
		newToAtomSet.addAtom(toAtom);
		link.setToSet(newToAtomSet.getAtomIDs());
		link.setFromSet(newFromAtomSet.getAtomIDs());
	}

	public String getSingleToAtomRef() {
		return getSingleAtomRef(link.getTo(), link.getToSet());
	}

	public String getSingleFromAtomRef() {
		return getSingleAtomRef(link.getFrom(), link.getFromSet());
	}
	
	public CMLAtom getSingleToAtom(CMLMolecule mol) {
		String ref = this.getSingleToAtomRef();
		return (ref == null) ? null : mol.getAtomById(ref);
	}

	public CMLAtom getSingleFromAtom(CMLMolecule mol) {
		String ref = this.getSingleFromAtomRef();
		return (ref == null) ? null : mol.getAtomById(ref);
	}

	private String getSingleAtomRef(String toFrom, String[] toFromSet) {
		String toFromRef = null;
		if (toFrom != null) {
			toFromRef = toFrom;
		} else if (toFromSet != null && toFromSet.length == 1) {
			toFromRef = toFromSet[0];
		}
		toFromRef = beheadMoleculeId(toFromRef);
		return toFromRef;
	}


	private String beheadMoleculeId(String toFromRef) {
		if (toFromRef != null && toFromRef.startsWith("m")) {
			toFromRef = toFromRef.substring("m1_".length());
		}
		return toFromRef;
	}


	public void addToAndFrom(CMLMap.Direction direction, CMLAtom atom, CMLAtom otherAtom) {
		Direction otherDirection = direction.opposite();
		this.set(direction, otherAtom.getId());
		this.set(otherDirection, atom.getId());
	}

	public void addToAndFrom(CMLMap.Direction direction, CMLBond bond, CMLBond otherBond) {
		Direction otherDirection = direction.opposite();
		this.set(direction, otherBond.getId());
		this.set(otherDirection, bond.getId());
	}


	public void set(Direction direction, String value) {
		if (direction.equals(Direction.TO)) {
			link.setTo(value);
		} else if (direction.equals(Direction.FROM)) {
			link.setFrom(value);
		}
	}	
};