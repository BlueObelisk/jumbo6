package org.xmlcml.cml.legacy.molecule;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLScalar;

/**
 * convert to and from SDF.
 * 
 * @author pmr
 * 
 */
public class SDFConverter implements CMLConstants {

    final static Logger logger = Logger.getLogger(SDFConverter.class.getName());

    private Document cmlDoc;

    protected boolean joinBonds = false;

    protected boolean addOrderBonds = false;

    protected boolean vvv = false;

    protected final static String S_NEWLINE = "\r\n";

    /**
     * constructor.
     * 
     * 
     */
    public SDFConverter() {
        ;
    }

    /**
     * Current version.
     * 
     * @return version
     */
    public String getVersion() {
        return "V2000";
    }

    /**
     * Reads an input stream containing multiple MOL molecules.
     * 
     * @param reader
     * @throws IOException
     * @throws CMLException
     * @return the Document (rootElement is CMLMolecule)
     */
    public Document readSDF(Reader reader) throws IOException, CMLException {

        LineNumberReader lineReader = new LineNumberReader(reader);

        CMLList moleculeList = new CMLList();
        cmlDoc = new Document(moleculeList);
        MDLConverter mdlConverter = new MDLConverter();

        while (true) {
            CMLMolecule currentMolecule;

            try {
                currentMolecule = mdlConverter.readMOL(lineReader);
                moleculeList.appendChild(currentMolecule);
            } catch (CMLException cmle) {
                if (cmle.getMessage().substring(0, 28).trim().equals(
                        "MDLConverter: Unexpected EOF")) {
                    break;
                } else {
                    throw cmle;
                }
            }

            CMLList dataList = readData(lineReader);
            if (dataList != null) {
                currentMolecule.appendChild(dataList);
            }
        }

        /*
         * TODO check last molecule isnt blank (shouldnt happen) Node lastChild =
         * moleculeList.getLastChild(); if
         * (lastChild.getNodeName().equals("molecule")) { if
         * (lastChild.getChildNodes().getLength() == 0) {
         * list.removeChild(lastChild); } }
         */

        return cmlDoc;
    }

    /**
     * Reads an input stream into existing document.
     * 
     * @param doc
     *            the document to add molecule(s) to
     * @param reader
     * @throws IOException
     * @throws CMLException
     * @return the Document (rootElement is CMLMolecule)
     */
    public Document read(Document doc, Reader reader) throws IOException,
            CMLException {
        // NYI
        return doc;
    }

    /**
     * outputs CMLMolecule as an SDFMofile if possible. This is NOT //a faithful
     * representation as I haven't read the spec completely //@param Writer
     * writer to output it to
     */
    /*--
     public void write(Writer writer, CMLDocument doc)
     throws CMLException, IOException {
     this.doc = doc;
     mol = (CMLMolecule) doc.getFirstElement("molecule");
     atomVector = mol.getAtomVector();
     natoms = atomVector.size();
     bondVector = mol.getBondVector();
     nbonds = bondVector.size();

     if (natoms > 999) {
     throw new CMLException
     ("Too many atoms for SDFMolfile: " + natoms);
     }

     writeHeader(writer);
     writeAtoms(writer);
     writeBonds(writer);
     writeFooter(writer);

     }
     > 1 <CAS.NUMBER>
     74-82-8

     > 1 <FORMULA>
     CH4

     > 1 <SYNONYMS>
     Methane
     Marsh gas
     Methyl hydride
     CH4
     Fire damp
     UN 1971
     UN 1972
     R 50
     Biogas

     --*/

    /**
     * set document.
     * 
     * @param doc
     */
    public void setDocument(Document doc) {
        cmlDoc = doc;
    }

    /**
     * get document.
     * 
     * @return document
     */
    public Document getDocument() {
        return cmlDoc;
    }

    /**
     * set join.
     * 
     * @param join
     */
    public void setJoin(boolean join) {
        joinBonds = join;
    }

    /**
     * set version.
     * 
     * @param vvv
     */
    public void setVvv(boolean vvv) {
        this.vvv = vvv;
    }

    /**
     * get join.
     * 
     * @return join
     */
    public boolean getJoin() {
        return joinBonds;
    }

    /**
     * set addOrder.
     * 
     * @param addOrder
     */
    public void setAddOrder(boolean addOrder) {
        addOrderBonds = addOrder;
    }

    /**
     * get addOrder.
     * 
     * @return addOrder
     */
    public boolean getAddOrder() {
        return addOrderBonds;
    }

    private void process() throws CMLException {
        /*
         * FIXME List<CMLElement> moleculeList =
         * multiDoc.getDescendants("molecule"); for (int i = 0; i <
         * moleculeList.size(); i++) { CMLMolecule molecule = (CMLMolecule)
         * moleculeList.get(i); //MoleculeTool MoleculeTool =
         * MoleculeToolImpl.getMoleculeTool(molecule); MoleculeTool moleculeTool =
         * MoleculeToolImpl.getTool(molecule); boolean joined = true; if
         * (joinBonds) { try { moleculeTool.calculateBondedAtoms(); } catch
         * (CMLException e) { //logger.info("problem in molecule " + i + S_SPACE +
         * molecule.getTitle() + S_SPACE + molecule.getId()); //e.printStackTrace();
         * joined = false; } // must set to reasonable number or algorithm fails
         * if (joined) { moleculeTool.setBondOrders("1"); } } if (joined &&
         * addOrderBonds) { moleculeTool.adjustBondOrdersToValency(); } }
         */
    }

    /**
     * read XML.
     * 
     * @param r
     *            reader
     * @return document
     * @throws IOException
     * @throws CMLException
     */
    public Document readXML(Reader r) throws IOException, CMLException {
        cmlDoc = null;
        Builder builder = new CMLBuilder();
        try {
            cmlDoc = builder.build(r);
        } catch (ValidityException ve) {
            throw new CMLException(S_EMPTY + ve);
        } catch (ParsingException pe) {
            throw new CMLException(S_EMPTY + pe);
        }

        return cmlDoc;
    }

    /**
     * Reads data fields from the SDF file Note: currently only extracts the
     * title of the field, any other registry numbers are lost.
     * 
     * @param br
     *            LineNumberReader pointing to the SDF file
     * @throws CMLException
     * @throws IOException
     */
    private CMLList readData(LineNumberReader br) throws CMLException,
            IOException {
        CMLList mainList = null;
        while (true) {
            String line = br.readLine(); // .trim();
            if (line.equals("$$$$")) {
                break;
            } else if (line.charAt(0) == '>') {
                line = line.substring(1).trim();

                if (mainList == null) {
                    mainList = new CMLList();
                    mainList.setTitle("SDF Data Items");
                }
                CMLList list = new CMLList();

                // n (n) <title> DTn
                // <title> - field name
                // DTn - MACCS-II number
                // n - internal registry number
                // (n) - external registry number
                String dataTitle = line.substring(line.indexOf("<") + 1, line
                        .indexOf(">"));
                line = line.replaceFirst("<" + dataTitle + ">", S_EMPTY);
                // TODO Implement reading of diffrent data headers in SDF files

                list.setTitle(dataTitle);
                mainList.appendChild(list);
                while (true) {
                    line = br.readLine().trim();
                    if (line.equals(S_EMPTY)) {
                        break;
                    }
                    CMLScalar scalar = new CMLScalar();
                    scalar.setXMLContent(line);
                    list.appendChild(scalar);
                }
            } else if (line.equals(S_EMPTY)) {
                // ignore blank lines
            } else {
                logger.info("SDF data misread: " + line);
            }
        }
        return mainList;
    }

    /**
     * write SDF.
     * 
     * @param writer
     * @param mol
     * @throws IOException
     */
    public void writeData(Writer writer, CMLMolecule mol) throws IOException {
        // Assume first list in molecule is a list of data
        CMLElements<CMLList> mainListElements = mol.getListElements();

        if (mainListElements.size() > 0) {
            CMLList mainList = (CMLList) mainListElements.get(0);
            Elements dataListElements = mainList.getChildElements("list",
                    CMLElement.CML_NS);

            int numOfDatas = dataListElements.size();
            for (int k = 0; k < numOfDatas; k++) {
                CMLList dataList = (CMLList) dataListElements.get(k);
                Elements scalarElements = dataList.getChildElements("scalar",
                        CMLElement.CML_NS);

                if (k != 0) { // if we've written before, add a blank line
                    writer.write(S_NEWLINE);
                }

                String dataName = dataList.getTitle();
                writer.write(">  <" + dataName + ">" + S_NEWLINE);

                int numOfScalars = scalarElements.size();
                for (int j = 0; j < numOfScalars; j++) {
                    CMLScalar thisScalar = (CMLScalar) scalarElements.get(j);
                    String scalarData = thisScalar.getXMLContent();
                    writer.write(scalarData + S_NEWLINE);
                }
            }
        }
    }

    /**
     * writes the content of the SDF
     * 
     * @param writer
     * @throws CMLException
     * @throws IOException
     */

    public void write(Writer writer) throws IOException, CMLException {
        MDLConverter mdl = new MDLConverter();

        if (cmlDoc.getRootElement() instanceof CMLList) {
            CMLList moleculeList = (CMLList) cmlDoc.getRootElement();
            Elements moleculeListElements = moleculeList.getChildElements(
                    "molecule", CMLElement.CML_NS);

            int nmols = moleculeListElements.size();
            for (int i = 0; i < nmols; i++) {
                if (moleculeListElements.get(i) instanceof CMLMolecule) {
                    CMLMolecule mol = (CMLMolecule) moleculeListElements.get(i);
                    if (mol.getAtomCount() != 0) {
                        mdl.writeMOL(writer, mol);
                        writeData(writer, mol);
                    }
                }
                writer.write(S_NEWLINE + "$$$$" + S_NEWLINE);
            }
        }
    }

    /**
     * usage.
     * 
     * @param out
     */
    public static void usage(java.io.PrintStream out) {
        out.println("Usage: org.xmlcml.legacy.molecule.SDFConverter [options]");
        out.println("        -IN inputFile (SDF or CML)");
        out.println("        -OUT outputFile (CML or SDF)");
        out.println("        -JOIN (joins bonded atoms)");
        out.println("        -VVV (write bonds sum into vvv field)");
        out
                .println("        -ADDORDER (calculates bond order from connectivity)");
        out.println("        determines file types from suffix (sdf or cml)");
    }

    /**
     * main.
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage(System.out);
            System.exit(0);
        }
        SDFConverter sdf = new SDFConverter();
        int i = 0;
        String infile = S_EMPTY;
        String outfile = S_EMPTY;
        boolean join = false;
        boolean addOrder = false;
        boolean vvv = false;
        while (i < args.length) {
            if (1 == 2) {
                ;
            } else if (args[i].equalsIgnoreCase("-IN")) {
                infile = args[++i];
                i++;
            } else if (args[i].equalsIgnoreCase("-OUT")) {
                outfile = args[++i];
                i++;
            } else if (args[i].equalsIgnoreCase("-JOIN")) {
                join = true;
                i++;
            } else if (args[i].equalsIgnoreCase("-ADDORDER")) {
                addOrder = true;
                i++;
            } else if (args[i].equalsIgnoreCase("-VVV")) {
                vvv = true;
                i++;
            } else {
                logger.severe("Unknown arg: " + args[i]);
                i++;
            }
        }
        Document doc = null;
        try {
            if (!infile.equals(S_EMPTY)) {
                if (infile.endsWith(".cml")) {
                    doc = sdf.readXML(new FileReader(infile));
                    sdf.setDocument(doc);
                    logger.info("read CML document from: " + infile);
                } else if (infile.endsWith(".sdf")) {
                    doc = sdf.readSDF(new FileReader(infile));
                } else {
                    throw new CMLException("Cannot determine fileType: "
                            + infile);
                }
            }
            if (join) {
                sdf.setJoin(join);
            }
            if (addOrder) {
                sdf.setAddOrder(addOrder);
            }
            if (vvv) {
                sdf.setVvv(vvv);
            }
            sdf.process();
            if (doc != null && !outfile.equals(S_EMPTY)) {
                if (outfile.endsWith(".cml")) {
                    Serializer serializer = new Serializer(
                            new FileOutputStream(outfile));
                    serializer.setIndent(2);
                    serializer.write(doc);
                } else if (outfile.endsWith(".sdf")) {
                    FileWriter fw = new FileWriter(outfile);
                    sdf.setDocument(doc);
                    sdf.write(fw);
                    fw.close();
                    logger.info("wrote SDF document to: " + outfile);
                } else {
                    throw new CMLException("Cannot determine fileType: "
                            + outfile);
                }
            }
        } catch (IOException ioe) {
            logger.info("IOException: " + ioe);
        } catch (CMLException cmle) {
            logger.info("CMLException: " + cmle);
            cmle.printStackTrace();
        }
    }
}
