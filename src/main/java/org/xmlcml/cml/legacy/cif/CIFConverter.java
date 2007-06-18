package org.xmlcml.cml.legacy.cif;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLFormula.Type;
import org.xmlcml.cml.element.CMLTable.TableType;
import org.xmlcml.cml.legacy.LegacyConverterFactoryOld;
import org.xmlcml.cml.legacy.LegacyConverterOld;
import org.xmlcml.molutil.ChemicalElement;

import uk.co.demon.ursus.cif.CIF;
import uk.co.demon.ursus.cif.CIFDataBlock;
import uk.co.demon.ursus.cif.CIFException;
import uk.co.demon.ursus.cif.CIFItem;
import uk.co.demon.ursus.cif.CIFLoop;
import uk.co.demon.ursus.cif.CIFParser;
import uk.co.demon.ursus.cif.CIFRuntimeException;
import uk.co.demon.ursus.cif.CIFTableCell;
import uk.co.demon.ursus.cif.CIFUtil;

/** converts CML to from CIF
 * @author pm286
 *
 */
public class CIFConverter implements LegacyConverterOld, CMLConstants {

    /** values for control of converter and parser.
     * the enum names can be used as String values,
     * e.g. "NO_GLOBAL"
     * @author pm286
     *
     */
    public enum Control {
        /** do not output global block.*/
        NO_GLOBAL,
        /** merges global block into all blocks; omits global.*/
        MERGE_GLOBAL,
        /** debug output*/
        DEBUG,
        /** echo input lines*/
        ECHO_INPUT,
        /** check for duplicate CIFItem and CIFLoop*/
        CHECK_DUPLICATES,
        /** skip header to first data_*/
        SKIP_HEADER,
        /** skip errors and try to recover*/
        SKIP_ERRORS;
        private Control() {
        }
    }
   	final static Logger logger = Logger.getLogger(CIFConverter.class.getName());
	CMLMolecule molecule;
	/** delimiter for CIF arrays/loops.
	 * best guess for a character that doesn't clash with CIF usage.
	 */
    /** output delimiter*/
	final static String DELIM = "|";
    /** */
	final static boolean NUMERIC = true;
    /** */
	final static boolean NON_NUMERIC = false;

    // final document holding all blocks as CMLCml
    Document cmlDoc;
    // list of blocks as CML
    List<CMLCml> cmlList;
//	an IUCr CIF in CIFDOM format
	CIF cif = null;
    Reader reader = null;
    CIFParser parser = null;
    
    // control fields
    boolean echoInput = false;
    boolean debug = false;
    boolean noGlobal = false;
    boolean mergeGlobal = false;
    boolean checkDuplicates = false;
    boolean skipHeader = false;
    boolean skipErrors = false;

	CIFDataBlock globalBlock;
//	CIFDataBlock nonGlobalBlock;
    List<CIFDataBlock> blockList;
    
	CMLCrystal crystal = null;
	List<CMLScalar> cellParams = null;
	/** omit "." or S_QUERY in CIFs */
	boolean omitIndeterminate = false;
    
	CIFCategory[] CML_CATEGORIES = new CIFCategory[] {
		// order matters because category not parsable
		CIFCategory.ATOM_SITE_ANISO,
		CIFCategory.ATOM_SITE,
		CIFCategory.ATOM_SITES_SOLUTION,
		CIFCategory.ATOM_TYPE,
		CIFCategory.CELL,
		CIFCategory.CHEMICAL_FORMULA,
		CIFCategory.CHEMICAL_NAME,
		CIFCategory.GEOM_ANGLE,
		CIFCategory.GEOM_BOND,
		CIFCategory.SYMMETRY_EQUIV
	};
	/*-
	 static String CAT_SYMM = "symmetry_equiv")) {
	 static String CAT_ANISO = "atom_site_aniso")) {
	 static String CAT_ATOM = "atom_site")) {
	 static String CAT_ATTYPE = "atom_type")) {
	 static String CAT_BOND = "geom_bond")) {
	 static String CAT_ANGLE = "geom_angle")) {
	 -*/
    /** constructor.
	 *
	 */
	public CIFConverter() {
		init();
	}
    private void init() {
		cif = null;
		globalBlock = null;
		crystal = null;
		cellParams = null;
        echoInput = false;
        debug = false;
        noGlobal = false;
        mergeGlobal = false;
        checkDuplicates = false;
        skipHeader = false;
        skipErrors = false;
        
		logger.setLevel(Level.INFO);
	}

    /** control parser via a String.
     * the values and actions are described in the Control enum.
     * @param controlStrings must be value of an enum.
     * @throws CMLRuntimeException incorrect string
     */
    public void setControls(String... controlStrings) throws CMLRuntimeException {
        for (String controlString : controlStrings) {
            Control control = null;
            for (Control c : Control.values()) {
                if (c.name().equals(controlString)) {
                    control = c;
                    break;
                }
            }
            if (control == null) {
                throw new CMLRuntimeException("Bad control string: "+controlString);
            } else if (control.equals(Control.NO_GLOBAL)) {
                noGlobal = true;
            } else if (control.equals(Control.MERGE_GLOBAL)) {
                mergeGlobal = true;
            } else if (control.equals(Control.DEBUG)) {
                debug = true;
//                if (parser != null) {
//                    parser.setDebug(debug);
//                }
            } else if (control.equals(Control.ECHO_INPUT)) {
                echoInput = true;
//                if (parser != null) {
//                    parser.setEchoInput(echoInput);
//                }
            } else if (control.equals(Control.CHECK_DUPLICATES)) {
                checkDuplicates = true;
//                if (parser != null) {
//                    parser.setCheckDuplicates(checkDuplicates);
//                }
            } else if (control.equals(Control.SKIP_HEADER)) {
                skipHeader = true;
//                if (parser != null) {
//                    parser.setSkipHeader(skipHeader);
//                }
            } else if (control.equals(Control.SKIP_ERRORS)) {
                skipErrors = true;
//                if (parser != null) {
//                    parser.setSkipErrors(skipErrors);
//                }
            }
        }
    }
    /** set cif.
     * 
     * @param cif
     */
    public void setCIF(CIF cif) {
        this.cif = cif;
    }
    /** set CML element.
     * at present must be CMLCml
     * @param element
     */
    public void setCML(CMLElement element) {
        if (!(element instanceof CMLCml)) {
            throw new CMLRuntimeException("Element must be of type CML");
        }
        setCML((CMLCml) element);
    }
    
    /** gets Document after parseLegacy(). 
     * @return the document (null if no parse)
     */
    public Document getDocument() {
    	if (cmlDoc == null) {
            CMLCml cmlTop = new CMLCml();
            for (CMLCml cml : cmlList) {
                cmlTop.appendChild(cml);
            }
    		cmlDoc = new Document(cmlTop);
    	}
        return cmlDoc;
    }
    
    /** gets the CIF. 
     * @return the CIF or null;
     */
    public Object getLegacyObject() {
        return cif;
    }
    
    private void setCML(CMLCml cml) {
		Elements molecules = cml.getChildCMLElements(CMLMolecule.TAG);
		if (molecules.size() == 0) {
			throw new CMLRuntimeException("Cannot create CIF2CML; no molecule");
		}
		this.molecule = (CMLMolecule) molecules.get(0);
		Elements crystals = cml.getChildCMLElements(CMLCrystal.TAG);
		if (crystals.size() == 0) {
			throw new CMLRuntimeException("Cannot create CIF2CML; no crystal");
		}
		this.crystal = (CMLCrystal) crystals.get(0);
	}
	
    /** write CIF.
     * NYI
     * @param w
     */
    public void writeLegacy(Writer w) {
        throw new CMLRuntimeException("NOT YET IMPLEMENTED");
    }
    
    /** write CIF.
     * NYI
     * @param os
     */
    public void writeLegacy(OutputStream os) {
        throw new CMLRuntimeException("NOT YET IMPLEMENTED");
    }

    /** read CIF.
     * NYI
     * @param reader
     * @throws IOException
     * @return document (CMLCml root with CMLCml children from each CIF
     */
    public Document parseLegacy(Reader reader) throws IOException {
        if (parser == null) {
            parser = new CIFParser();
        }
        parser.setEchoInput(echoInput);
        parser.setDebug(debug);
        parser.setCheckDuplicates(checkDuplicates);
        parser.setSkipHeader(skipHeader);
        parser.setSkipErrors(skipErrors);
        try {
            BufferedReader br = new BufferedReader(reader);
            Document document = parser.parse(br);
            // there is a memory leak here; minimise by removing the CIF
//            parser = null;
            cif = (CIF) document.getRootElement();
            document.replaceChild(cif, new Element("dummy"));
            document = null;
            br.close();
        } catch (CIFException e) {
            throw new CMLRuntimeException("Cannot parse CIF: "+e);
        }
        try {
            processCIF();
        } catch (CMLException e) {
            throw new CMLRuntimeException("Exception "+e);
        }
        cmlDoc = null;
        return cmlDoc;
    }
    
    /** read CIF.
     * @param inputStream
     * @return document
     * @throws IOException
     */
    public Document parseLegacy(InputStream inputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(inputStream);
        return parseLegacy(isr);
    }
    
    /** get specific CMLElements
     * @param elementName can be "molecule" or "crystal"
     * @return element or name
     */
    public CMLElement getCMLElement(String elementName) {
        CMLElement element = null;
        if (elementName.equals(CMLMolecule.TAG)) {
            element = this.getCMLMolecule();
        } else if (elementName.equals(CMLCrystal.TAG)) {
                element = this.getCMLCrystal();
        }
        return element;
    }

    private CMLCrystal getCMLCrystal() {
		return crystal;
	}
    private CMLMolecule getCMLMolecule() {
		return molecule;
	}

	/*--
	 data_I
	 _chemical_name_systematic
	 ;
	 lithium triflate acetonitrile adduct
	 ;
	 _chemical_name_common     Li(CF3SO3)(CH3CN)
	 _chemical_formula_moiety     'C F3 Li O3 S, C2 H3 N'
	 _chemical_formula_sum     'C3 H3 F3 Li N O3 S'
	 _chemical_formula_iupac     '[Li(C F3 O3 S) (C2 H3 N)]'
	 _chemical_formula_weight     197.06
	 _symmetry_cell_setting     Monoclinic
	 _symmetry_space_group_name_H-M     'P 21/c'
	 loop_
	 _symmetry_equiv_pos_as_xyz
	 'x, y, z'
	 '-x, y+1/2, -z+1/2'
	 '-x, -y, -z'
	 'x, -y-1/2, z-1/2'
	 _cell_length_a     5.4814(11)
	 _cell_length_b     14.790(3)
	 _cell_length_c     9.409(2)
	 _cell_angle_alpha     90.00
	 _cell_angle_beta     90.064(3)
	 _cell_angle_gamma     90.00
	 _cell_volume     762.8(3)
	 _cell_formula_units_Z     4
	 _diffrn_ambient_temperature     173(2)
	 _refine_ls_R_factor_all     0.0224
	 _refine_ls_R_factor_gt     0.0219
	 _refine_ls_wR_factor_gt     0.0604
	 _refine_ls_wR_factor_ref     0.0606
	 _refine_ls_goodness_of_fit_ref     1.111
	 _refine_ls_restrained_S_all     1.111
	 _refine_ls_number_reflns     1731
	 _refine_ls_hydrogen_treatment     constr
	 _atom_sites_solution_hydrogens     'placed geometrically'
	 loop_
	 _atom_type_symbol
	 _atom_type_description
	 _atom_type_scat_dispersion_real
	 _atom_type_scat_dispersion_imag
	 _atom_type_scat_source
	 'C' 'C' 0.0033 0.0016
	 'International Tables Vol C Tables 4.2.6.8 and 6.1.1.4'
	 'H' 'H' 0.0000 0.0000
	 'International Tables Vol C Tables 4.2.6.8 and 6.1.1.4'
	 'Li' 'Li' -0.0003 0.0001
	 'International Tables Vol C Tables 4.2.6.8 and 6.1.1.4'
	 'O' 'O' 0.0106 0.0060
	 'International Tables Vol C Tables 4.2.6.8 and 6.1.1.4'
	 'F' 'F' 0.0171 0.0103
	 'International Tables Vol C Tables 4.2.6.8 and 6.1.1.4'
	 'S' 'S' 0.1246 0.1234
	 'International Tables Vol C Tables 4.2.6.8 and 6.1.1.4'
	 'N' 'N' 0.0061 0.0033
	 'International Tables Vol C Tables 4.2.6.8 and 6.1.1.4'
	 loop_
	 _atom_site_label
	 _atom_site_fract_x
	 _atom_site_fract_y
	 _atom_site_fract_z
	 _atom_site_U_iso_or_equiv
	 _atom_site_adp_type
	 _atom_site_calc_flag
	 _atom_site_refinement_flags
	 _atom_site_occupancy
	 _atom_site_disorder_assembly
	 _atom_site_disorder_group
	 _atom_site_type_symbol
	 Li1 0.2780(5) 0.56145(15) -0.1656(2) 0.0235(4) Uani d . 1 . . Li
	 H3A 0.0835 0.5725 -0.6931 0.071 Uiso calc R 1 . . H
	 loop_
	 _atom_site_aniso_label
	 _atom_site_aniso_U_11
	 _atom_site_aniso_U_22
	 _atom_site_aniso_U_33
	 _atom_site_aniso_U_12
	 _atom_site_aniso_U_13
	 _atom_site_aniso_U_23
	 Li1 0.0239(11) 0.0253(10) 0.0214(10) -0.0004(9) -0.0019(9) 0.0017(8)
	 _geom_special_details
	 ;
	 All esds (except the esd in the dihedral angle between two l.s. planes)
	 are estimated using the full covariance matrix.  The cell esds are taken
	 into account individually in the estimation of esds in distances, angles
	 and torsion angles; correlations between esds in cell parameters are only
	 used when they are defined by crystal symmetry.  An approximate (isotropic)
	 treatment of cell esds is used for estimating esds involving l.s. planes.
	 ;
	 loop_
	 _geom_bond_atom_site_label_1
	 _geom_bond_atom_site_label_2
	 _geom_bond_site_symmetry_2
	 _geom_bond_distance
	 _geom_bond_publ_flag
	 Li1 O1 . 1.940(2) y
	 Li1 O2 3_565 1.926(3) y
	 S1 C1 . 1.8266(14) ?
	 O2 Li1 3_565 1.926(3) ?
	 loop_
	 _geom_angle_atom_site_label_1
	 _geom_angle_atom_site_label_2
	 _geom_angle_atom_site_label_3
	 _geom_angle_site_symmetry_1
	 _geom_angle_site_symmetry_3
	 _geom_angle
	 _geom_angle_publ_flag
	 N1 Li1 O1 . . 105.52(11) y
	 N1 Li1 O2 . 3_565 114.53(13) y
	 O3 S1 O2 . . 114.25(8) ?
	 S1 O2 Li1 . 3_565 143.40(10) ?
	 data_global
	 _journal_name_full     'Acta Crystallographica, Section E'
	 _journal_year     2002
	 _journal_volume     58
	 _journal_issue      5
	 _journal_page_first     m176
	 _journal_page_last     m177
	 _publ_section_title
	 ;
	 Lithium triflate acetonitrile aduct
	 ;
	 loop_
	 _publ_author_name
	 _publ_author_address
	 'Brooks, Neil R.'
	 ; Department of Chemistry
	 University of Minnesota
	 207 Pleasant St. SE
	 Minneapolis
	 MN 55455
	 USA
	 ;
	 --*/
	/** processes CIF using CML semantics.
	 * if there is a global block this adds it to the CML.
	 * Selects block by ID. if this is null finds first block that is not
	 * global (we may change this strategy)
	 * @return CML or null
	 */
    private CMLCml processNonGlobalBlock(CIFDataBlock nonGlobalBlock) throws CMLException {
		CMLCml cml0 = null;
		if (cif != null) {
			cml0 = new CMLCml();
			String dataBlockId = nonGlobalBlock.getId();
			if (nonGlobalBlock != null) {
				String id = makeId(dataBlockId);
				cml0.setId(id);
				cml0.setTitle(dataBlockId);
			}
			if (globalBlock != null) {
				processNonCMLItems(globalBlock, cml0);
				processNonCMLLoops(globalBlock, cml0);
				processCMLItems(globalBlock, cml0);
				processCMLLoops(globalBlock, cml0);
			}
			processNonCMLItems(nonGlobalBlock, cml0);
			processNonCMLLoops(nonGlobalBlock, cml0);
			processCMLItems(nonGlobalBlock, cml0);
			processCell();
			processCMLLoops(nonGlobalBlock, cml0);
			molecule.appendChild(crystal);
			try {
				checkCML(cml0);
			} catch (CMLRuntimeException e) {
				severeError("Cannot processBlock "+e);
			}
		}
		return cml0;
	}
	/** substitute all non alpahNumeric by '_'.
	 *
	 * @param s string to substitute
	 * @return munged string
	 */
    private String makeId(String s) {
		String ss = (s == null) ? "unknown" : s;
		while (ss.startsWith(S_UNDER)) {
			ss = ss.substring(1);
		}
		ss = (ss.length() == 0) ? "unknown" : ss;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length(); i++) {
			char c = ss.charAt(i);
			if (Character.isDigit(c) || Character.isLetter(c)) {
				sb.append(c);
			} else {
				sb.append('_');
			}
		}
		ss = sb.toString();
		return (!Character.isLetter(ss.charAt(0))) ? "c"+ss : ss;
	}
    private void checkCML(CMLCml cml) {
		if (molecule.getChildCMLElements(CMLCrystal.TAG).size() == 0) {
			throw new CMLRuntimeException("nonGlobalBlock "+cml.getId()+" has no cell");
		}
		if (cml.getChildCMLElements(CMLMolecule.TAG).size() == 0) {
			throw new CMLRuntimeException("nonGlobalBlock "+cml.getId()+" has no molecule");
		}
	}
	/** processes CIF using CML semantics.
	 * if there is a block labelled global, uses that as global
	 * data and adds it to the CML.
	 * Selects blocks in sequence.
     * @exception CMLException
	 */
    public void processCIF() throws CMLException {
        cmlList = new ArrayList<CMLCml>();
        blockList = new ArrayList<CIFDataBlock>();
		globalBlock = null;
		if (cif != null) {
			List<CIFDataBlock> bList = cif.getDataBlockList();
			for (CIFDataBlock block : bList) {
				String id = block.getId();
                if (isGlobalBlock(block)) {
                	if (globalBlock != null) {
                		System.err.println("WARNING more than one global block - taking first");
                	} else {
                		globalBlock = block;
                        if (!noGlobal || mergeGlobal) {
                            blockList.add(globalBlock);
                        }
                	}
                } else if (isNonGlobalBlock(block)) {
                	CMLCml cml0 = null;
					try{
						CIFDataBlock nonGlobalBlock = block;
						cml0 = processNonGlobalBlock(nonGlobalBlock);
					} catch (CMLRuntimeException e) {
						throw new CMLRuntimeException("processBlock ("+id+"), maybe data error: "+e);
					}
                    if (mergeGlobal && globalBlock != null) {
                        //mergeGlobal();
                    }
					if (cml0 != null) {
						cmlList.add(cml0);
					}
				} else {
					System.err.println("Cannot determine type of block: "+id);
				}
			}
		}
	}

    private static boolean isGlobalBlock(CIFDataBlock block) {
    	Nodes crystalNodes = block.query(".//item[@name='_cell_length_a']");
    	Nodes moleculeNodes = block.query(".//loop[contains(@names,'_atom_site_label')]");
    	Nodes symmetryNodes = block.query(".//loop[contains(@names,'_symmetry_equiv_pos_as_xyz')]");
    	return crystalNodes.size() == 0 && 
    	    moleculeNodes.size() == 0 &&
	        symmetryNodes.size() == 0 ;
    }
    
    private static boolean isNonGlobalBlock(CIFDataBlock block) {
    	Nodes crystalNodes = block.query(".//item[@name='_cell_length_a']");
    	Nodes moleculeNodes = block.query(".//loop[contains(@names,'_atom_site_label')]");
    	Nodes symmetryNodes = block.query(".//loop[contains(@names,'_symmetry_equiv_pos_as_xyz')]");
    	return crystalNodes.size() == 1 && 
    	    moleculeNodes.size() >= 1 &&
	        symmetryNodes.size() == 1 ;
    }
    
	private CIFCategory getCMLCategory(String name) {
		CIFCategory category = null;
		for (CIFCategory cat : CML_CATEGORIES) {
			if (cat.contains(name)) {
				category = cat;
			}
		}
		return category;
	}
	private CIFCategory getCMLCategory(List<String> nameList) {
		CIFCategory category = null;
		for (CIFCategory cat : CML_CATEGORIES) {
			if (cat.matches(nameList)) {
				category = cat;
			}
		}
		return category;
	}
	private void addItem(CMLElement cmlElement, CIFItem item) {
		CMLScalar scalar = makeScalar(item, NON_NUMERIC);
		String value = item.getValue();
		if (!omitIndeterminate || !isIndeterminateValue(value)) {
			cmlElement.appendChild(scalar);
		}
	}
    private String makeDictRef(String name) {
		String name0 = (name.startsWith(S_UNDER)) ? name.substring(0) : name;
		name0 = name0.replaceAll(S_SLASH, "_slash_");
		name0 = name0.replaceAll(S_PERCENT, "_percent_");
		return "iucr:"+name0;
	}
    /**
    @deprecated
    */
	private CMLTable createTable(CIFLoop loop, String categoryName) {
		CMLTable table = new CMLTable();
		table.setTableType(TableType.COLUMN_BASED);
		table.setDictRef(makeDictRef(categoryName));
		List<String> nameList = loop.getNameList();
		for (int i = 0; i < nameList.size(); i++) {
			List<CIFTableCell> cellList = loop.getColumnCells(i);
			CMLArray column = new CMLArray();
			column.setDictRef(makeDictRef(nameList.get(i)));
			column.setDelimiter(DELIM);
			for (CIFTableCell cell : cellList) {
				column.append(cell.getValue());
			}
			// this is a bug! the count output is larger by 1
			// FIXME
			Attribute sz = column.getSizeAttribute();
			if (sz != null) {
				column.removeAttribute(sz);
			}
			table.addArray(column);
		}
		return table;
	}
    private void processNonCMLItems(CIFDataBlock block, CMLCml cml0) {
		if (block != null) {
			for (CIFItem item : block.getItemList()) {
				// omit CML
				CIFCategory category = getCMLCategory(item.getName());
				if (category != null) {
					continue;
				}
				addItem((CMLElement)cml0, item);
			}
		}
	}
    
    private void processNonCMLLoops(CIFDataBlock block, CMLCml cml0) {
		if (block != null) {
			for (CIFLoop loop : block.getLoopList()) {
				// omit CML
				CIFCategory category = getCMLCategory(loop.getNameList());
				if (category != null) {
					continue;
				}
				cml0.appendChild(createTable(loop, loop.getNameList().get(0)));
			}
		}
	}
    private void processCMLItems(CIFDataBlock block, CMLCml cml0) {
		if (block != null) {
			cellParams = null;
			crystal = null;
			for (CIFItem item : block.getItemList()) {
				String name = item.getName();
				CIFCategory category = getCMLCategory(name);
				if (category != null) {
					String categoryName = category.getName();
					if (categoryName.equals("cell")) {
						if (crystal == null) {
							crystal = new CMLCrystal();
						}
						if (cellParams == null) {
							cellParams = new ArrayList<CMLScalar>();
							for (int i = 0; i < 6; i++) {
								cellParams.add((CMLScalar) null);
							}
						}
						addCell(item);
					} else if (categoryName.equals("chemical_name")) {
						addChemicalName(item, cml0);
					} else if (categoryName.equals("chemical_formula")) {
						try {
							addChemicalFormula(item, cml0);
						} catch (CMLException e) {
							severeError("unparsable formula: "+e);
						}
					} else if (categoryName.equals("atom_sites_solution")) {
						addAtomSitesSolution(item, cml0);
					} else {
						System.out.println("Unprocessed CML item: "+name);
						//throw new CMLRuntime("Unprocessed CML item: "+name);
					}
				}
			}
		}
	}
    private void addCell(CIFItem item) {
		String[] names = {
				"_cell_length_a",
				"_cell_length_b",
				"_cell_length_c",
				"_cell_angle_alpha",
				"_cell_angle_beta",
				"_cell_angle_gamma"
		};
		String[] names1 = {
				"_cell_formula_units_z",
				"_cell_volume",
		};
		String name = item.getName();
		int idx = CIFUtil.indexOf(name, names, true);
		// sets params in correct order
		if (idx >= 0) {
			CMLScalar scalar = makeScalar(item, NUMERIC);
			cellParams.set(idx, scalar);
		} else {
			idx = CIFUtil.indexOf(name, names1, true);
			// formula units
			if (idx == 0) {
				item.processSu(true);
				Double z = item.getNumericValue();
				if (z == null) {
					throw new CMLRuntimeException("Bad Z value "+item.getValue());
				}
				crystal.setZ((int) z.doubleValue());
				// cell volume
			} else if (idx == 1) {
				// omit volume till we redesign cell
//				item.processSu(true);
//				addItem((CMLElement)crystal, item);
			} else {
				logger.warning("Unknown cell name: "+name);
				addItem((CMLElement)crystal, item);
			}
		}
	}
    private CMLScalar makeScalar(CIFItem item, boolean numeric) {
		CMLScalar scalar = new CMLScalar();
		scalar.setDictRef(makeDictRef(item.getName()));
		if (numeric) {
			item.processSu(true);
			try {
				Double d = item.getNumericValue();
				if (d != null) {
					scalar.setValue(d.doubleValue());
					scalar.setErrorValue(item.getSu());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String v = item.getValue();
			if (v != null) {
				scalar.setValue(item.getValue());
			}
		}
		return scalar;
	}
    private CMLScalar makeScalar(String dictRef, String value) {
		CMLScalar scalar = new CMLScalar();
		scalar.setDictRef(makeDictRef(dictRef));
		scalar.setValue(value);
		return scalar;
	}
    private CMLScalar makeScalar(String dictRef, double value) {
		CMLScalar scalar = new CMLScalar();
		scalar.setDictRef(makeDictRef(dictRef));
		scalar.setValue(value);
		return scalar;
	}
    private void processCell() {
		if (cellParams != null) {
			for (CMLScalar cellParam : cellParams) {
				if (cellParam == null) {
					throw new CMLRuntimeException("mus have 6 cell parameters");
				}
			}
			for (CMLScalar cellParam : cellParams) {
				crystal.appendChild(cellParam);
			}
		}
	}
    private void addChemicalName(CIFItem item, CMLCml cml0) {
		String[] names = {
				"_chemical_name_common",
				"_chemical_name_systematic",
		};
		String name = item.getName();
		int idx = CIFUtil.indexOf(name, names, true);
		// sets params in correct order
		if (idx == -1) {
			logger.warning("unknown name type "+name);
		}
		CMLScalar scalar = makeScalar(item, NON_NUMERIC);
		cml0.appendChild(scalar);
	}
    private void addChemicalFormula(CIFItem item, CMLCml cml0) throws CMLException {
		String[] names = {
				"_chemical_formula_moiety",
				"_chemical_formula_sum",
				"_chemical_formula_iupac",
				"_chemical_formula_structural",
				//
				"_chemical_formula_weight",
		};
		CMLFormula formula = null;
		String name = item.getName();
		String value = item.getValue();
		if (!isIndeterminateValue(value)) {
			int idx = CIFUtil.indexOf(name, names, true);
			// sets params in correct order
			if (idx == -1) {
				// not a formula
			} else if (idx == 0) {
				// moiety
				// if this cannot be parsed we throw an error and do not
				// create the object
				formula = CMLFormula.createFormula(value, Type.MOIETY);
				formula.setDictRef(makeDictRef(name));
				formula.setInline(value);
//				System.out.println("MOIETY   "+value);
				cml0.appendChild(formula);
			} else if (idx == 1) {
				// sum
				// if this cannot be parsed we throw an error and do not
				// create the object
				formula = CMLFormula.createFormula(value);
				formula.setDictRef(makeDictRef(name));
				formula.setInline(value);
				cml0.appendChild(formula);
			} else if (idx == 2) {
				// iupac
				// if we cannot parse it, create formula without contents
				try {
					formula = CMLFormula.createFormula(value);
				} catch (CMLRuntimeException e) {
					formula = new CMLFormula();
				}
				formula.setInline(value);
				formula.setDictRef(makeDictRef(name));
				cml0.appendChild(formula);
			} else if (idx == 3) {
				// structural, probably same as IUPAC
				formula = CMLFormula.createFormula(value);
				formula.setInline(value);
				formula.setDictRef(makeDictRef(name));
				cml0.appendChild(formula);
			} else if (idx == 4) {
				CMLScalar scalar = makeScalar(item, NUMERIC);
				cml0.appendChild(scalar);
			}
		}
	}
	/** checks for special CIF values.
	 *
	 * @param s
	 * @return true if s is S_QUERY or "."
	 */
    private static boolean isIndeterminateValue(String s) {
		String ss = s.trim();
		return ss.equals(S_QUERY) || ss.equals(S_PERIOD);
	}
    private void addAtomSitesSolution(CIFItem item, CMLCml cml0) {
		String[] names = {
				"_atom_sites_solution_hydrogens",
		};
		String name = item.getName();
		int idx = CIFUtil.indexOf(name, names, true);
		// sets params in correct order
		if (idx == -1) {
			logger.warning("Unknown atom_sites_solution "+name);
		}
		CMLScalar scalar = makeScalar(item, NON_NUMERIC);
		cml0.appendChild(scalar);
	}
    private void processCMLLoops(CIFDataBlock block, CMLCml cml0) {
		if (block != null) {
			for (CIFLoop loop : block.getLoopList()) {
				CIFCategory category = getCMLCategory(loop.getNameList());
				if (category != null) {
//					String categoryName = category.getName();
					if (category.equals(CIFCategory.SYMMETRY_EQUIV)) {
						addSymmetry(loop);
					} else if (category.equals(CIFCategory.ATOM_SITE_ANISO)) {
						addAtomSiteAniso(loop, cml0);
					} else if (category.equals(CIFCategory.ATOM_SITE)) {
						addAtomSite(loop, cml0);
					} else if (category.equals(CIFCategory.ATOM_TYPE)) {
						addAtomType(loop, cml0);
					} else if (category.equals(CIFCategory.GEOM_BOND)) {
						addGeomBond(loop, cml0);
					} else if (category.equals(CIFCategory.GEOM_ANGLE)) {
						addGeomAngle(loop, cml0);
					} else if (category.equals(CIFCategory.GEOM_TORSION)) {
						addGeomTorsion(loop, cml0);
					} else {
						throw new CMLRuntimeException("Unprocessed CML loop: "+category);
					}
				}
			}
		}
	}
    private void addSymmetry(CIFLoop loop) {
		String[] names =  {
				"_symmetry_equiv_pos_as_xyz",
				"_symmetry_equiv_pos_site_id",
		};
		checkLoop(loop, names, 0, "symmetry_equiv");
		List<String> operators = loop.getColumnValues(names[0]);
		if (operators == null) {
			throw new CMLRuntimeException("no "+names[0]);
		}
		
		CMLSymmetry symmetry = CMLSymmetry.createFromXYZStrings(operators);
		crystal.addSymmetry(symmetry);
	}
	private void addAtomSiteAniso(CIFLoop loop, CMLCml cml0) {
		String[] names = {
				"_atom_site_aniso_label",
				"_atom_site_aniso_u_11",
				"_atom_site_aniso_u_12",
				"_atom_site_aniso_u_13",
				"_atom_site_aniso_u_22",
				"_atom_site_aniso_u_23",
				"_atom_site_aniso_u_33",
                "_atom_site_aniso_type_symbol",                
		};
		String category = "atom_site_aniso";
		checkLoop(loop, names, 0, category);
		CMLTable table = createTable(loop, category);
		cml0.appendChild(table);
	}
	private void addAtomSite(CIFLoop loop, CMLCml cml0) {
		String AS = "_atom_site";
		String AS_LABEL = "_atom_site_label";
		String AS_FX = "_atom_site_fract_x";
		String AS_FY = "_atom_site_fract_y";
		String AS_FZ = "_atom_site_fract_z";
		String AS_OCC = "_atom_site_occupancy";
		String AS_SYM = "_atom_site_type_symbol";
		String AS_UISO = "_atom_site_U_iso_or_equiv";
		String AS_ADP = "_atom_site_adp_type";
		String AS_CALC = "_atom_site_calc_flag";
//		obsolete according to CIF dictionary
		String AS_REF = "_atom_site_refinement_flags";
		String AS_DISASS = "_atom_site_disorder_assembly";
		String AS_DISGRP = "_atom_site_disorder_group";
		String AS_MULT = "_atom_site_symmetry_multiplicity";
		String AS_ATT = "_atom_site_calc_attached_atom";
		String AS_THERM = "_atom_site_thermal_displace_type";
		String[] names = {
				AS_LABEL,
				AS_FX,
				AS_FY,
				AS_FZ,
				AS_OCC,
				AS_SYM,
				AS_UISO,
				AS_ADP,
				AS_CALC,
				AS_REF,
				AS_DISASS,
				AS_DISGRP,
				AS_MULT,
				AS_ATT,
				AS_THERM
		};
		checkLoop(loop, names, 0, AS);
		try {
			boolean failOnError = false;
			loop.processSu(failOnError);
		} catch (CIFRuntimeException e) {
			throw new CIFRuntimeException(AS+": "+e.getMessage());
		}
		// check names
		List<String> nameList = loop.getNameList();
		for (String name : nameList) {
			if (CIFUtil.indexOf(name, names, true) == -1) {
				logger.info("unknown atom_site name "+name);
			}
		}
		molecule = new CMLMolecule();
		List<String> symbols = loop.getColumnValues(AS_SYM);
		List<String> labels = loop.getColumnValues(AS_LABEL);
		// atom labels
		if (symbols == null) {
			symbols = new ArrayList<String>();
			for (String label : labels) {
				symbols.add(getSymbol(label));
			}
		}
		// chemical symbols
		int i = 0;
		for (String symbol : symbols) {
			if (ChemicalElement.getChemicalElement(symbol) == null) {
				throw new CMLRuntimeException("Bad element: "+symbol);
			}
			CMLAtom atom = null;
            atom = new CMLAtom("a"+(++i));
            molecule.addAtom(atom);
			atom.setElementType(symbol);
		}
		double[] x = loop.getNumericColumnValues(AS_FX);
		double[] y = loop.getNumericColumnValues(AS_FY);
		double[] z = loop.getNumericColumnValues(AS_FZ);
		if (x == null || y == null || z == null) {
			throw new CMLRuntimeException("Missing fractional coordinates");
		}
		double[] occ = loop.getNumericColumnValues(AS_OCC);
		double[] u = loop.getNumericColumnValues(AS_UISO);
		List<String> adp = loop.getColumnValues(AS_ADP);
		List<String> calc = loop.getColumnValues(AS_CALC);
		List<String> disass = loop.getColumnValues(AS_DISASS);
		List<String> disgrp = loop.getColumnValues(AS_DISGRP);
		List<String> label = loop.getColumnValues(AS_LABEL);
		List<String> mult = loop.getColumnValues(AS_MULT);
		List<String> ref = loop.getColumnValues(AS_REF);
		List<CMLAtom> atoms = molecule.getAtoms();
		i = 0;
		for (CMLAtom atom : atoms) {
			// coordinates are meaningless is calc flag = "dum"
			if (calc == null || !calc.get(i).equals("dum")) {
				atom.setXFract(x[i]);
				atom.setYFract(y[i]);
				atom.setZFract(z[i]);
			}
			// occupancy - (only add if not 1.0)
			// ned24 changed this line.  If an atom is disordered
			// and isn't given a numerical occupancy in the cif then there is
			// a problem.  If we don't attach the occupancies when
			// they are == 1 then we cannot distinguish between those
			// atoms which haven't been given an occupancy and those
			// which have occupancy == 1.  We need to keep track 
			// of occupancies for all atoms until disorder is resolved.
			if (occ != null /*&& occ[i] < 1.0*/) {
				atom.setOccupancy(occ[i]);
			}
			if (u != null) {
				atom.appendChild(makeScalar(AS_UISO, u[i]));
			}
			if (adp != null && !isIndeterminateValue(adp.get(i))) {
				atom.appendChild(makeScalar(AS_ADP, adp.get(i)));
			}
			// calc flag. omit default "d" (calculated from diffraction data)
			// only add if "c" or "calc" and expand to "calc"
			if (calc != null) {
				if (calc.get(i).equals("") ||
						calc.get(i).equals("calc")) {
					atom.appendChild(makeScalar(AS_CALC, "calc"));
				}
			}
			if (disass != null && !isIndeterminateValue(disass.get(i))) {
//                System.out.println("DISASS: "+disass.get(i));
				atom.appendChild(makeScalar(AS_DISASS, disass.get(i)));
			}
			if (disgrp != null && !isIndeterminateValue(disgrp.get(i))) {
//                System.out.println("DISGRP: "+disgrp.get(i));
				atom.appendChild(makeScalar(AS_DISGRP, disgrp.get(i)));
			}
			if (label != null && !isIndeterminateValue(label.get(i))) {
				atom.appendChild(makeScalar(AS_LABEL, label.get(i)));
			}
			// add multiplicity if not unity
			if (mult != null) {
				int m = 0;
				try {
					m = Integer.parseInt(mult.get(i));
				} catch (NumberFormatException e) {
					throw new CMLRuntimeException("bad atom spaceGroup multiplicity: "+e);
				}
				if (m > 1) {
					atom.setSpaceGroupMultiplicity(m);
				}
			}
			if (ref != null && !isIndeterminateValue(ref.get(i))) {
				atom.appendChild(makeScalar(AS_REF, ref.get(i)));
			}
			i++;
		}
		cml0.appendChild(molecule);
	}
	private String getSymbol(String ss) {
		String s = ss;
		if (s.length() > 2) {
			s = s.substring(0, 2);
		}
		// chop anything that is not a trailing lowercase
		if (s.length() == 2) {
			if (!Character.isLowerCase(s.charAt(1))) {
				s = s.substring(0, 1);
			}
		}
		return s;
	}

    private void addAtomType(CIFLoop loop, CMLCml cml0) {
		String[] names = {
				"_atom_type_symbol",
				"_atom_type_description",
				"_atom_type_scat_dispersion_real",
				"_atom_type_scat_dispersion_imag",
				"_atom_type_scat_source",
				"_atom_type_oxidation_number",
				"_atom_type_number_in_cell",
				"_atom_type_scat_Cromer_Mann_a1",
				"_atom_type_scat_Cromer_Mann_b1",
				"_atom_type_scat_Cromer_Mann_a2",
				"_atom_type_scat_Cromer_Mann_b2",
				"_atom_type_scat_Cromer_Mann_a3",
				"_atom_type_scat_Cromer_Mann_b3",
				"_atom_type_scat_Cromer_Mann_a4",
				"_atom_type_scat_Cromer_Mann_b4",
				"_atom_type_scat_Cromer_Mann_a3",
				"_atom_type_scat_Cromer_Mann_c",
		};
		String category = "atom_type";
		checkLoop(loop, names, 0, category);
		CMLTable table = createTable(loop, category);
		cml0.appendChild(table);
	}
    private void checkLoop(CIFLoop loop, String[] names, int keyPos, String categoryName) {
		boolean key = false;
		for (String name : loop.getNameList()) {
			int idx = CIFUtil.indexOf(name, names, true);
			if (idx == -1) {
				logger.warning("Unknown "+categoryName+": "+name);
			}
			key = (key) ? key : name.equalsIgnoreCase(names[keyPos]);
		}
		if (!key) {
			throw new CMLRuntimeException("Must give "+names[keyPos]);
		}
	}
    private void addGeomBond(CIFLoop loop, CMLCml cml0) {
		String[] names = {
				"_geom_bond_atom_site_label_1",
				"_geom_bond_atom_site_label_2",
				"_geom_bond_site_symmetry_2",
				"_geom_bond_distance",
				"_geom_bond_publ_flag",
				"_geom_bond_site_symmetry_1",
		};
		String category = "geom_bond";
		checkLoop(loop, names, 0, category);
		CMLTable table = createTable(loop, category);
		cml0.appendChild(table);
	}
    private void addGeomAngle(CIFLoop loop, CMLCml cml0) {
		String[] names = {
				"_geom_angle_atom_site_label_1",
				"_geom_angle_atom_site_label_2",
				"_geom_angle_atom_site_label_3",
				"_geom_angle_site_symmetry_1",
				"_geom_angle_site_symmetry_3",
				"_geom_angle",
				"_geom_angle_publ_flag",
				"_geom_angle_site_symmetry_2",
		};
		String category = "geom_angle";
		checkLoop(loop, names, 0, category);
		CMLTable table = createTable(loop, category);
		cml0.appendChild(table);
	}
    private void addGeomTorsion(CIFLoop loop, CMLCml cml0) {
		String[] names = {
				"_geom_torsion_atom_site_label_1",
				"_geom_torsion_atom_site_label_2",
				"_geom_torsion_atom_site_label_3",
				"_geom_torsion_atom_site_label_4",
				"_geom_torsion_site_symmetry_1",
				"_geom_torsion_site_symmetry_3",
				"_geom_torsion",
				"_geom_torsion_publ_flag",
				"_geom_torsion_site_symmetry_2",
		};
		String category = "geom_torsion";
		checkLoop(loop, names, 0, category);
		CMLTable table = createTable(loop, category);
		cml0.appendChild(table);
	}
    private static void severeError(String s) {
		System.out.println("***** SEVERE ERROR: "+s);
	}

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0 || args.length == 1 && args[0].equals("test")) {
            String filename="C:\\pmr\\cifdom1\\examples\\camb\\NABBAT.cif";
            LegacyConverterOld cifConverter = null;
            try {
                cifConverter = LegacyConverterFactoryOld.createLegacyConverter(
                    "org.xmlcml.cml.legacy.cif.CIFConverter");
            } catch (Throwable t) {
                throw new CMLRuntimeException("Cannot create legacyConverter "+t);
            }
            cifConverter.setControls("NO_GLOBAL", "SKIP_ERRORS", "SKIP_HEADER");
            CIFConverter converter = new CIFConverter();
            for (int i = 0; i < 10; i++) {
                System.err.println("------------"+i+"---->>>>>>>>>");
                converter.test(cifConverter, filename);
                System.err.println("<<<<<<<<----"+i+"-------------");
            }
            
            try {
                System.out.println("Waiting...");
                System.in.read();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            File cifDir = new File("G:"+File.separator+"01-00+"+File.separator);
            String[] files = cifDir.list();
            /*--
            String cifnames[] = {
    //                  "at6058",
                    "at6059",
                    "at6060",
                    "at6061",
                    "at6062",
                    "at6065",
                    "at6066",
                        };
                        --*/
            LegacyConverterOld cifConverter = LegacyConverterFactoryOld.createLegacyConverter(
                 "org.xmlcml.cml.legacy.cif.CIFConverter");
            for (String cifname : files) {
                if (cifname.startsWith(S_PERIOD) || 
                        cifname.equals("cifs") ||
                cifname.equals("files")) continue;
                System.out.println(cifname+": ");
                try {
                    FileReader fr = new FileReader(cifDir+File.separator+cifname+File.separator+cifname+".cif");
                    cifConverter.parseLegacy(fr);
                    fr.close();
                } catch (CMLRuntimeException e) {
                    throw new CMLRuntimeException("Cannot parse CIF: "+e);
                } catch (IOException e) {
                    throw new CMLRuntimeException("Cannot read CIF: "+e);
                }
            }
            System.out.println("FINISHED");
        }
    }
    
    
    /** is there another object to read.
     * NYI
     * @return true if still has more to read. 
     */
    public boolean hasNext() {
        if (1 == 1) throw new CMLRuntimeException("NOT YET IMPLEMENTED");
        return false;
    }

    
    /** removes iterator.
     * NYI
     */
    public void remove() {
        if (1 == 1) throw new CMLRuntimeException("NOT YET IMPLEMENTED");
    }

    /** reads next CIFBlock.
     * this may be useful for a large list of CIFs but will ignore global.
     * NYI
     * @return CIFBlock (including any globals)
     */
    public Object next() {
        if (1 == 1) throw new CMLRuntimeException("NOT YET IMPLEMENTED");
        return null;
    }

    /** get list of CIFBLocks.
     * returns all blocks in order of file.
     * if controlString = "NO_GLOBAL" skips global blocks
     * if controlString = "MERGE_GLOBAL" merges global blocks into each block.
     * @return list of blocks
     */
    public List getLegacyObjectList() {
        return blockList;
    }

    /** sets Reader.
     * required for iterator.
     * @param reader
     */
    public void setReader(Reader reader) {
        this.reader = reader;
    }
    
    /** returns blocks as list of CMLCml.
     * 
     * @return list of blocks
     */
    public List<CMLCml> getCMLCmlList() {
        return cmlList;
    }
    
    /** returns single object as CMLCml.
     * not implemented for CIF
     * @return block
     */
    public CMLCml getCMLCml() {
        throw new CMLRuntimeException("NOT IMPLEMENTED: use getCMLCmlList");
    }
    
    /** sets single CMLCml for conversion to legacy.
     * not yet implemented
     * @param cml
     */
    public void setCMLCml(CMLCml cml) {
        throw new CMLRuntimeException("NOT YET IMPLEMENTED ");
    }

    void test(LegacyConverterOld cifConverter, String filename) {
        System.err.println(">>>test");
        try {
            FileReader fr = new FileReader(filename);
            cifConverter.parseLegacy(fr);
            fr.close();
            System.err.println("PARSED: "+filename);
        } catch (Throwable e) {
            System.err.println("ERROR "+e);
        }
        cifConverter = null;
//        System.gc();
    }
    
}

