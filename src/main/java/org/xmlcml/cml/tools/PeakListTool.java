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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLPeak;
import org.xmlcml.cml.element.CMLPeakGroup;
import org.xmlcml.cml.element.CMLPeakList;
import org.xmlcml.cml.element.CMLPeakList.Type;
import org.xmlcml.cml.interfacex.PeakOrGroup;

/**
 * tool for managing peakList
 *
 * @author pmr
 *
 */
public class PeakListTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(PeakListTool.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	CMLPeakList peakList = null;

	/** constructor.
	 * requires molecule to contain <crystal> and optionally <symmetry>
	 * @param molecule
	 * @throws RuntimeException must contain a crystal
	 */
	public PeakListTool(CMLPeakList peakList) throws RuntimeException {
		init();
		this.peakList = peakList;
	}


	void init() {
	}


	/**
	 * get angle.
	 *
	 * @return the angle or null
	 */
	public CMLPeakList getPeakList() {
		return this.peakList;
	}

    
	/** gets PeakListTool associated with peakList.
	 * if null creates one and sets it in peakList
	 * @param peakList
	 * @return tool
	 */
	public static PeakListTool getOrCreateTool(CMLPeakList peakList) {
		PeakListTool peakListTool = null;
		if (peakList != null) {
			peakListTool = (PeakListTool) peakList.getTool();
			if (peakListTool == null) {
				peakListTool = new PeakListTool(peakList);
				peakList.setTool(peakListTool);
			}
		}
		return peakListTool;
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
				LOG.debug(peakIds[i]+" /"+atomIds[i]);
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
      		throw new RuntimeException("Cannot group groups");
      	}
      	if (from != null && to != null) {
      		CMLPeak thisPeak = (CMLPeak) this.getPeakChildById(from);
      		if (thisPeak == null) {
      			throw new RuntimeException("No peak with id: "+from);
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
          			throw new RuntimeException("No peak with id: "+fromG);
          		}
          		CMLPeak newPeak = new CMLPeak(thisPeak);
          		peakGroup.appendChild(newPeak);
      		}
      		peakList.appendChild(peakGroup);
      	} else {
      		throw new RuntimeException("Cannot map link: (from|Set)"+from+"|"+fromSet+" (to) "+to);
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
  			throw new RuntimeException("missing @to on link");
  		}
  		String atomId = link.getFrom();
  		String[] atomIds = link.getFromSet();
      	CMLPeak peak = (CMLPeak) this.getPeakChildById(peakId);
      	if (peak == null) {
      		throw new RuntimeException("no peak for: "+peakId);
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
  			throw new RuntimeException("Must have @from or @fromSet");
  		}
  	}
  }
  
  private void addLink(String atomRef, CMLPeak peak) {
  	Attribute peakAtt = peak.getAtomRefsAttribute();
  	String atomRefs = (peakAtt == null || peakAtt.equals(S_EMPTY)) ? CMLConstants.S_EMPTY : peakAtt.getValue();
  	if (!atomRefs.equals(S_EMPTY)) {
  		atomRefs += CMLConstants.S_SPACE;
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
  	List<CMLPeak> peaks = peakList.getPeakChildren();
  	if (atoms.size() != peaks.size()) {
  		throw new RuntimeException("atoms and peaks do not match: "+
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
   * finds all peak/Groups with atomRefs. If any contain any ids
   * in atomSet, strips these from atomRefs attribute. If attribute
   * is then empty delete it or if delete=true delete the node and
   * if no siblings deletes its parent
   * @param atomSet of atoms with refs to delete
   * @param delete if true deletes peak/Groups with empty attributes
   */
  public void removeAtomRefsOnPeaksAndGroups(CMLAtomSet atomSet, boolean delete) {
  	for (String atomId : atomSet.getAtomIDs()) {
  		Nodes nodes = peakList.query(".//*[@atomRefs]");
  		for (int i = 0; i < nodes.size(); i++) {
  			Element elem = (Element) nodes.get(i);
  			if (elem instanceof CMLPeakGroup || elem instanceof CMLPeak) {
  				String atomRefs = CMLConstants.S_SPACE+elem.getAttributeValue("atomRefs")+S_SPACE;
  				String atomRefs1 = atomRefs.replace(S_SPACE+atomId+S_SPACE, CMLConstants.S_SPACE);
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
		CMLPeakList peakList1 = new CMLPeakList();
		Nodes nodes = peakList.cmlQuery("./cml:peak[@atomRefs]");
		for (int i = 0; i < nodes.size(); i++) {
			CMLPeak peak = (CMLPeak) nodes.get(i);
			String[] atomRefs = peak.getAtomRefs();
			if (check(atomRefs, atomId)) {
				peakList1.addPeak(peak);
			}
		}
		return peakList1;
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
  
    /**
     * @param id
     * @return peak or null
     */
    public CMLPeak getPeakChildById(String id) {
    	CMLPeak peak = null;
    	if (id != null) {
	    	Nodes nodes = peakList.query("./cml:peak[@id='"+id+"']", CMLConstants.CML_XPATH);
	    	if (nodes.size() > 1) {
	    		throw new RuntimeException("Duplicate peak: "+id);
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
	    	Nodes nodes = peakList.query(".//cml:peak[@id='"+id+"']", CMLConstants.CML_XPATH);
	    	if (nodes.size() > 1) {
	    		throw new RuntimeException("Dupicate peak: "+id);
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
	    	Nodes nodes = peakList.cmlQuery("./cml:peak[@id='"+id+"'] | ./cml:peakGroup[@id='"+id+"']");
	    	if (nodes.size() > 1) {
	    		throw new RuntimeException("Duplicate peak or group: "+id);
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
    	CMLPeak[] peakArray = peakList.getPeakChildren().toArray(new CMLPeak[0]);
    	Arrays.sort(peakArray, comparator);
    	List<CMLPeak> peaks = new ArrayList<CMLPeak>();
    	for (CMLPeak peak : peakArray) {
    		peaks.add(peak);
    	}
    	return peaks;
    }
    
}

class PeakComparator implements Comparator<PeakOrGroup> {

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
	public int compare(PeakOrGroup peak1, PeakOrGroup peak2) {
		int result = 0;
		if (peak1 != null && peak2 != null) {
			if (type.equals(CMLPeakList.Type.XVALUE)) {
				double x1 = peak1.getXValue(); 
				double x2 = peak2.getXValue(); 
				if (!Double.isNaN(x1) && !Double.isNaN(x2)) {
					result = (x1 < x2) ? -1 : 1;
				}
			} else if (type.equals(CMLPeakList.Type.YVALUE)) {
				double y1 = peak1.getYValue(); 
				double y2 = peak2.getYValue(); 
				if (!Double.isNaN(y1) && !Double.isNaN(y2)) {
					result = (y1 < y2) ? -1 : 1;
				}
			}
		}
		return result;
	}
	

};