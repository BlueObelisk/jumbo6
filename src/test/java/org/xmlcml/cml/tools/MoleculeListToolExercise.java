package org.xmlcml.cml.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.html.HtmlMenuSystem;
import org.xmlcml.euclid.Util;

/**
 * tests moleculeTool.
 * 
 * @author pmr
 * 
 */
public class MoleculeListToolExercise {
	private static Logger LOG = Logger.getLogger(MoleculeListTool.class);

	/**
	 * test display.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void testSVG(String[] args) throws Exception {
		if (args.length < 2) {
			Util.println("-SVG infile/dir");
		} else {
			makeMenu(args[1], ".svg");
		}
	}

	private static void makeMenu(String dir, String suffix) throws IOException,
			ParsingException, ValidityException, FileNotFoundException {
		HtmlMenuSystem menu = new HtmlMenuSystem();
		File infile = new File(dir);
		if (infile.isDirectory()) {
			menu.setOutdir(dir);
			File[] files = infile.listFiles();
			for (File file : files) {
				if (file.toString().endsWith(suffix)) {
					process1File(file, menu);
				}
			}
		}
		menu.outputMenuAndBottomAndIndexFrame();
	}

	private static void process1File(File infile, HtmlMenuSystem menu)
			throws ParsingException, ValidityException, IOException {
		LOG.debug("INFILE " + infile);
		InputStream is = new FileInputStream(infile);
		CMLMoleculeList moleculeList = getMoleculeList(is);
		String outputFile = infile.toString();
		outputFile = outputFile.substring(0, outputFile.lastIndexOf("."))
				+ ".svg";
		SVGElement svg = MoleculeListTool.getOrCreateTool(moleculeList)
				.createGraphicsElement();
		CMLUtil.debug(svg, new FileOutputStream(outputFile), 1);
		menu.addHRef(outputFile);
	}

	private static CMLMoleculeList getMoleculeList(InputStream is)
			throws IOException {
		CMLMoleculeList moleculeList = null;
		Document doc = null;
		try {
			doc = new CMLBuilder().build(is);
			doc = CMLBuilder.ensureCML(doc);
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		Nodes nodes = doc.query("//cml:moleculeList", CMLConstants.CML_XPATH);
		if (nodes.size() > 0) {
			moleculeList = (CMLMoleculeList) nodes.get(0);
		} else {
			nodes = doc.query("//cml:molecule", CMLConstants.CML_XPATH);
			if (nodes.size() > 0) {
				moleculeList = new CMLMoleculeList();
				for (int i = 0; i < nodes.size(); i++) {
					moleculeList.addMolecule((CMLMolecule) nodes.get(i));
				}
			}
		}
		return moleculeList;
	}

	static void usage() {
		Util.println("java org.xmlcml.cml.tools.MoleculeListToolTest <options>");
		Util.println("... options ...");
		Util.println("-SVG inputfile/dir  <options>");
	}

	/**
	 * main
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			Util.println("Args is 0");
			usage();
		} else {
			if (args[0].equalsIgnoreCase("-SVG")) {
				testSVG(args);
			}
		}
	}

}
