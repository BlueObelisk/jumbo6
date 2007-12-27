package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.interfacex.PeakOrGroup;
import org.xmlcml.cml.tools.Morgan;

/**
 * user-modifiable class supporting peakList. 
 * NO INTERNAL index of peaks (uses XPATH)
 */
public class CMLPeakList extends AbstractPeakList {

	/** type of comparsion */
	public enum Type {
	    /** dewisott */
		XVALUE,
	    /** dewisott */
		YVALUE;
	}
	

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /** argument name to identify id.
     */
    public final static String IDX = "idx";
    
    /**
     * constructor.
     */
    public CMLPeakList() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLPeakList(CMLPeakList old) {
        super((AbstractPeakList) old);

    }
    
    /**
     * COPY constructor from list of peaks
     * @param peakList
     */
    public CMLPeakList(List<CMLPeak> peakList) {
    	for (CMLPeak peak : peakList) {
    		this.addPeak(peak);
    	}
    }
    
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLPeakList(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLPeakList
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLPeakList();

    }
    
    /**
     * update Index
     * @param parent element
     */
    public void finishMakingElement(Element parent) {
    }

    /**
     * @return list of peak children (not peakGroups or descendants)
     */
    public List<CMLPeak> getPeakChildren() {
    	Nodes nodes = this.query("./cml:peak", X_CML);
    	List<CMLPeak> peaks = new ArrayList<CMLPeak>();
    	for (int i = 0; i < nodes.size(); i++) {
    		peaks.add((CMLPeak) nodes.get(i));
    	}
    	return peaks;
    }
    
    /**
     * @return list of peak descendants (not peakGroups)
     */
    public List<CMLPeak> getPeakDescendants() {
    	Nodes nodes = this.query(".//cml:peak", X_CML);
    	List<CMLPeak> peaks = new ArrayList<CMLPeak>();
    	for (int i = 0; i < nodes.size(); i++) {
    		peaks.add((CMLPeak) nodes.get(i));
    	}
    	return peaks;
    }
    
    /**
     * @return list of peak or peakGroup children (not descendants)
     */
    public List<PeakOrGroup> getPeakOrGroupChildren() {
    	Nodes nodes = this.query("./cml:peak | ./cml:peakGroup", X_CML);
    	List<PeakOrGroup> peaks = new ArrayList<PeakOrGroup>();
    	for (int i = 0; i < nodes.size(); i++) {
    		peaks.add((PeakOrGroup) nodes.get(i));
    	}
    	return peaks;
    }

    /**
     * @param id
     * @return peak or null
     */
    public CMLPeak getPeakChildById(String id) {
    	CMLPeak peak = null;
    	if (id != null) {
	    	Nodes nodes = this.query("./cml:peak[@id='"+id+"']", X_CML);
	    	if (nodes.size() > 1) {
	    		throw new CMLRuntimeException("Duplicate peak: "+id);
	    	} else if (nodes.size() == 1) {
	    		peak = (CMLPeak) nodes.get(0);
	    	}
    	}
    	return peak;
    }
    
    /**
     * @param id
     * @return peak or null
     */
    public CMLPeak getPeakDescendantById(String id) {
    	CMLPeak peak = null;
    	if (id != null) {
	    	Nodes nodes = this.query(".//cml:peak[@id='"+id+"']", X_CML);
	    	if (nodes.size() > 1) {
	    		throw new CMLRuntimeException("Dupicate peak: "+id);
	    	} else if (nodes.size() == 1) {
	    		peak = (CMLPeak) nodes.get(0);
	    	}
    	}
    	return peak;
    }
    
    /**
     * @param id
     * @return peak or null
     */
    public PeakOrGroup getPeakOrGroupChildById(String id) {
    	PeakOrGroup peak = null;
    	if (id != null) {
	    	Nodes nodes = this.cmlQuery("./cml:peak[@id='"+id+"'] | ./cml:peakGroup[@id='"+id+"']");
	    	if (nodes.size() > 1) {
	    		throw new CMLRuntimeException("Duplicate peak or group: "+id);
	    	} else if (nodes.size() == 1) {
	    		peak = (PeakOrGroup) nodes.get(0);
	    	}
    	}
    	return peak;
    }
    
    /**
     * get list of peaks after sorting
     * DOES NOT ALTER THIS
     * DOES NOT sort on peakGroups
     * @param type (xValue or yValue)
     * @return list of peaks as array
     */
    public List<CMLPeak> getSortedPeakChildList(Type type) {
    	PeakComparator comparator = new PeakComparator(type);
    	CMLPeak[] peakArray = this.getPeakChildren().toArray(new CMLPeak[0]);
    	Arrays.sort(peakArray, comparator);
    	List<CMLPeak> peaks = new ArrayList<CMLPeak>();
    	for (CMLPeak peak : peakArray) {
    		peaks.add(peak);
    	}
    	return peaks;
    }
    
    /**
     * creates a new peakList with peakGroups formed by mapping child peaks.
     * 
     * the map should consist of links from peakRefs to a proposed
     * set of new peak groups.
     * the map may have two types of link:
     * - "fromSet" links in the map correspond to groups of 
     * single peaks in "this".  "to" links are new peakGroups 
     * in the new peakList 
     * - "from" links map single peaks to "to" peaks in new peakList.
     * normally this is just a copy operation but it could be used
     * to sort a peakList
     * If a peak is not referenced in a from or fromSet it will be deleted
     * If a peak is referenced in more than one from or fromSet it will appear
     * twice in the new peakList. This could cause problems
     * NOTE: The normal method of use will probably be to
     * - copy some peaks retaining heir ids
     * - group some other peaks into new new groups, while retaining their
     * indivdidual ids
     * - delete others
     * There is no check at present that this is done, so the user is 
     * responsible for the id-space of the result.
     * 
     * @param peaks2group map of peaks to groups
     * @return new peakList
     * 
     */
    public CMLPeakList createPeakGroups(CMLMap peaks2group) {
    	CMLPeakList peakList = new CMLPeakList();
    	CMLElements<CMLLink> links = peaks2group.getLinkElements();
        for (int i = 0; i < links.size(); i++) {
        	CMLLink link = (CMLLink) links.get(i);
        	String from = link.getFrom();
        	String[] fromSet = link.getFromSet();
        	String to = link.getTo();
        	String[] toSet = link.getToSet();
        	if (toSet != null) {
        		throw new CMLRuntimeException("Cannot group groups");
        	}
        	if (from != null && to != null) {
        		CMLPeak thisPeak = (CMLPeak) this.getPeakChildById(from);
        		if (thisPeak == null) {
        			throw new CMLRuntimeException("No peak with id: "+from);
        		}
        		CMLPeak newPeak = new CMLPeak(thisPeak);
        		newPeak.setId(to);
        		peakList.addPeak(newPeak);
        	} else if (fromSet != null && to != null) {
        		CMLPeakGroup peakGroup = new CMLPeakGroup();
        		peakGroup.setId(to);
        		for (String fromG : fromSet) {
        			CMLPeak thisPeak = (CMLPeak) this.getPeakChildById(fromG);
            		if (thisPeak == null) {
            			throw new CMLRuntimeException("No peak with id: "+fromG);
            		}
            		CMLPeak newPeak = new CMLPeak(thisPeak);
            		peakGroup.appendChild(newPeak);
        		}
        		peakList.appendChild(peakGroup);
        	} else {
        		throw new CMLRuntimeException("Cannot map link: (from|Set)"+from+"|"+fromSet+" (to) "+to);
        	}
        }
    	return peakList;
    }
    
    /** adds atomRefs to peaks or peakGroups
     * based on ids of immediate children
     * the map is of the form:
     * link@from = atomId or link@fromSet = atomIds (as ws-separated string)
     * link@to = peakId or peakGroup Id (toSet forbidden as groups should be used)
     * not all atoms or peaks need to be linked
     * existing atomRefs may be overwritten or retained
     * @param atoms2peaks map of links
     * @param overwrite existing atomRefs (at present a peak can only have 
     * 1 atomRefs attribute. This might change later)
     */
    public void addAtomRefs(CMLMap atoms2peaks, boolean overwrite) {
    	for (CMLLink link : atoms2peaks.getLinkElements()) {
    		String peakId = link.getTo();
    		if (peakId == null) {
    			throw new CMLRuntimeException("missing @to on link");
    		}
    		String atomId = link.getFrom();
    		String[] atomIds = link.getFromSet();
        	CMLPeak peak = (CMLPeak) this.getPeakChildById(peakId);
        	if (peak == null) {
        		throw new CMLRuntimeException("no peak for: "+peakId);
        	}
        	if (overwrite) {
        		peak.setAtomRefs(S_EMPTY);
        	}
    		if (atomId != null) {
    			addLink(atomId, peak);
    		} else if (atomIds != null) {
    			for (String atomIdd : atomIds) {
        			addLink(atomIdd, peak);
    			}
    		} else {
    			throw new CMLRuntimeException("Must have @from or @fromSet");
    		}
    	}
    }
    
    private void addLink(String atomRef, CMLPeak peak) {
    	Attribute peakAtt = peak.getAtomRefsAttribute();
    	String atomRefs = (peakAtt == null || peakAtt.equals(S_EMPTY)) ? S_EMPTY : peakAtt.getValue();
    	if (!atomRefs.equals(S_EMPTY)) {
    		atomRefs += S_SPACE;
    	}
    	atomRefs += atomRef;
    	peak.setAtomRefs(atomRefs);
    }
    
    /**
     * atoms are in 1-1 relationship with peaks
     * @param molecule
     * @return the map generated to link atoms
     */
    public CMLMap createAtom2PeakMap(CMLMolecule molecule) {
    	CMLMap atoms2peaks = new CMLMap();
    	List<CMLAtom> atoms = molecule.getAtoms();
    	List<CMLPeak> peaks = this.getPeakChildren();
    	if (atoms.size() != peaks.size()) {
    		throw new CMLRuntimeException("atoms and peaks do not match: "+
				atoms.size()+ "/"+ peaks.size());
    	}
    	for (int i = 0; i < peaks.size(); i++) {
    		CMLLink link = new CMLLink();
    		link.setTo(peaks.get(i).getId());
    		link.setFrom(atoms.get(i).getId());
    		atoms2peaks.addLink(link);
    	}
    	return atoms2peaks;
    }

    /**
     * adds atomRefs from molecule assuming
     * atoms are in 1-1 relationship with peaks
     * @param molecule
     * @return the map generated to link atoms
     */
    public CMLMap addAtomRefs(CMLMolecule molecule) {
    	CMLMap atoms2peaks = this.createAtom2PeakMap(molecule);
    	this.addAtomRefs(atoms2peaks, true);
    	return atoms2peaks;
    }
    

    /**
     * creates map from molecule to peakgroups
     * based on Morgan equivalence
     * assumes initial moleucle and peakList is aligned
     * @param molecule
     * @return map
     */
    public CMLMap getPeakGroupsFromMorgan(CMLMolecule molecule) {
    	CMLMap atoms2peaks = this.createAtom2PeakMap(molecule);
    	CMLMap peaks2peakGroups = new CMLMap();
    	Morgan morgan = new Morgan(molecule);
		List<CMLAtomSet> atomSetList = morgan.getAtomSetList();
		for (CMLAtomSet atomSet : atomSetList) {
			String[] atomIds = atomSet.getAtomIDs();
			String[] peakIds = new String[atomIds.length];
			String id = "";
			for (int i = 0; i < atomIds.length; i++) {
				peakIds[i] = atoms2peaks.getToRef(atomIds[i]);
				id += peakIds[i];
			}
			CMLLink link = new CMLLink();
			link.setTo(id);
			link.setFromSet(peakIds);
			peaks2peakGroups.addLink(link);
		}
		return peaks2peakGroups;
    }

    /**
     * dreate new PeakList with groups according to morgan equivalences
     * 
     * @param molecule
     * @return peakList
     */
    public CMLPeakList createPeakListGroupedByMorgan(CMLMolecule molecule) {
    	CMLMap peakGroupMap = this.getPeakGroupsFromMorgan(molecule);
    	CMLPeakList groupedPeakList = this.createPeakGroups(peakGroupMap);
    	return groupedPeakList;
    }

    /**
     * finds all peak/Groups with atomRefs. If any contain any ids
     * in atomSet, strips these from atomRefs attribute. If attribute
     * is then empty delete it or if delete=true delete the node and
     * if no siblings deletes its parent
     * @param atomSet of atoms with refs to delete
     * @param delete if true deletes peak/Groups with empty attributes
     */
    public void removeAtomRefsOnPeaksAndGroups(CMLAtomSet atomSet, boolean delete) {
    	for (String atomId : atomSet.getAtomIDs()) {
    		Nodes nodes = this.query(".//*[@atomRefs]");
    		for (int i = 0; i < nodes.size(); i++) {
    			Element elem = (Element) nodes.get(i);
    			if (elem instanceof CMLPeakGroup || elem instanceof CMLPeak) {
    				String atomRefs = S_SPACE+elem.getAttributeValue("atomRefs")+S_SPACE;
    				String atomRefs1 = atomRefs.replace(S_SPACE+atomId+S_SPACE, S_SPACE);
    				if (!atomRefs1.equals(atomRefs)) {
    					atomRefs1 = atomRefs1.trim();
						elem.removeAttribute(elem.getAttribute("atomRefs"));
    					if (atomRefs1.length() == 0) {
    						// delete node
    						if (delete) {
    							delete(elem);
    						}
    					} else {
    						// replace with modified attribute
	    					Attribute attribute = new Attribute("atomRefs", atomRefs1);
	    					elem.addAttribute(attribute);
    					}
    				}
    			} else {
    				
    			}
    		}
    	}
    }

    // detach node. if parent has then no child elements, detach it recursively
    private void delete(Element elem) {
    	ParentNode parent = elem.getParent();
    	elem.detach();
    	if (parent instanceof CMLPeakGroup &&
    			((Element) parent).getChildElements().size() == 0) {
    		delete((Element)parent);
    	}
    }
    
    /**
     * @param atomId
     * @return new peakList
     */
	public CMLPeakList createPeakListFromPeakChildrenByAtomId(String[] atomId) {
		CMLPeakList peakList = new CMLPeakList();
		Nodes nodes = this.cmlQuery("./cml:peak[@atomRefs]");
		for (int i = 0; i < nodes.size(); i++) {
			CMLPeak peak = (CMLPeak) nodes.get(i);
			String[] atomRefs = peak.getAtomRefs();
			if (check(atomRefs, atomId)) {
				peakList.addPeak(peak);
			}
		}
		return peakList;
	}

	// returns true if any of atomRefs and atomIds match
	private boolean check(String[] atomRefs, String[] atomIds) {
		boolean check = false;
		for (String atomRef : atomRefs) {
			for (String atomId : atomIds) {
				if (atomRef.equals(atomId)) {
					check = true;
					break;
				}
			}
		}
		return check;
	}
    
}

class PeakComparator implements Comparator {

	private CMLPeakList.Type type;
	/**
	 * @param type
	 */
	public PeakComparator(CMLPeakList.Type type) {
		this.type = type;
	}
	
	/**
	 * @param peak1
	 * @param peak2
	 * @return -1, 0, 1 (0 if objects are null, no xvalues, etc.)
	 */
	public int compare(Object peak1, Object peak2) {
		int result = 0;
		if (peak1 != null && peak2 != null &&
			peak1 instanceof PeakOrGroup && peak2 instanceof PeakOrGroup) {
			PeakOrGroup p1 = (PeakOrGroup) peak1;
			PeakOrGroup p2 = (PeakOrGroup) peak2;
			if (type.equals(CMLPeakList.Type.XVALUE)) {
				double x1 = p1.getXValue(); 
				double x2 = p2.getXValue(); 
				if (!Double.isNaN(x1) && !Double.isNaN(x2)) {
					result = (x1 < x2) ? -1 : 1;
				}
			} else if (type.equals(CMLPeakList.Type.YVALUE)) {
				double y1 = p1.getYValue(); 
				double y2 = p2.getYValue(); 
				if (!Double.isNaN(y1) && !Double.isNaN(y2)) {
					result = (y1 < y2) ? -1 : 1;
				}
			}
		}
		return result;
	}
}
