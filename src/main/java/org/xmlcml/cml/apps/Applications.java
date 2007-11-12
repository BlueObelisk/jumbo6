package org.xmlcml.cml.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLName;

/**
 * 
 * <p>
 * Contains typical applications of CML
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class Applications {

    /** possible tests
     * 
     */
    public static String[] tests = {
            "readCML",
            "makeCML",
    };

    /** read a CML file and report.
     * if the file has a top element in the current CML namespace this
     * will be created as a CML class, else simply a nu.xom.Element
     * args: of form "readCML" args1, arg2...
     * if args[1] = -HELP, issue help, else args[1] is filename to read
     * exceptions are repoerted in situ
     * @param args
     */
    public static void readCML(String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("-HELP")) {
            System.out.println("readCML filename");
        } else {
            Document doc = null;
            try {
                System.out.println("Reading: "+args[1]);
                doc = new CMLBuilder().build(new File(args[1]));
            } catch (ValidityException e) {
                System.out.println("Not a valid CML file: "+args[1]+e);
                e.printStackTrace();
            } catch (ParsingException e) {
                System.out.println("Not a valid CML file: "+args[1]+e);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Cannot read CML file: "+args[1]);
                e.printStackTrace();
            }
            Element rootElement = doc.getRootElement();
            System.out.println("Parse file, root element is "+rootElement.getLocalName());
            if (rootElement instanceof CMLElement) {
                System.out.println("root element is in CML namespace: "+
                        rootElement.getNamespaceURI());
                System.out.println("Document parsed to:");
                CMLElement cmlElement = (CMLElement) rootElement;
            }
        }
    }
    
    /** make a CML file and write to file.
     * args: of form "makeCML" args1, arg2...
     * if args[1] = -HELP, issue help, else args[1] is filename to write
     * exceptions are reported in situ
     * @param args
     */
    public static void makeCML(String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("-HELP")) {
            System.out.println("readCML [filename]");
            System.out.println("... if filename is present will write to this file");
        } else {
            CMLMolecule molecule = new CMLMolecule();
            molecule.setId("m1");
            CMLName name = new CMLName();
            name.setXMLContent("benzene");
            molecule.appendChild(name);
            System.out.println("The following molecule has been craeted");
            molecule.debug("MOL");
            if (args[1] != null) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(args[1]);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found: "+args[1]);
                    e.printStackTrace();
                }
                Document doc = new Document(molecule);
                Serializer serializer = new Serializer(fos);
                try {
                    serializer.write(doc);
                } catch (IOException e) {
                    System.err.println("Cannout output file: "+e);
                    e.printStackTrace();
                }
            }
        }
    }
    
    /** usage.
     * 
     * @return usage String
     */
    static String usage() {
        String s = "";
        s += "The following applications are available: \n";
        for (String t : tests) {
            s += t+"\n";
        }
        s += "\nto run a tests\n";
        s += "  java org.xmlcml.cml.apps.Applications [testname [args]]\n";
        s += "\nto get help on a tests\n";
        s += "  java org.xmlcml.cml.apps.Applications [testname -HELP]\n";
        return s;
    }
    
    /** main will run simple apps.
     * 
     * @param args if none outputs help; else runs test of name args[0]
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println(usage());
        } else {
            String test = args[0];
            if (test.equalsIgnoreCase("readCML")) {
                readCML(args);
            } else if (test.equalsIgnoreCase("makeCML")) {
                makeCML(args);
            } else {
                System.err.println("Unknown test: "+test);
            }
        }
    }
}
