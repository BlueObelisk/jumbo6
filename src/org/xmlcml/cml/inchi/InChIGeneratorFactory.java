package org.xmlcml.cml.inchi;

import java.util.List;

import net.sf.jniinchi.JniInchiWrapper;
import net.sf.jniinchi.LoadNativeLibraryException;

import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * <p>Factory providing access to InChIGenerator and InChIToStructure. See those
 * classes for examples of use. These methods make use of the JNI-InChI library.
 * 
 * <p>InChI/Structure interconversion is implemented in this way so that we can
 * check whether or not the native code required is available. If the native
 * code cannot be loaded a CDKException will be thrown when the constructor
 * is called. The most common problem is that the native code is not in the
 * the correct location. Java searches the locations in the PATH environmental
 * variable, under Windows, and LD_LIBRARY_PATH under Linux, so the JNI-InChI
 * native libraries must be in one of these locations. If the JNI-InChI jar file
 * is being used and either the current working directory, or '.' are contained
 * in PATH of LD_LIBRARY_PATH then the native code should be placed
 * automatically. If the native files are in the correct location but fail to
 * load, then they may need to be recompiled for your system. See:
 * <ul>
 * <li>http://sourceforge.net/projects/jni-inchi
 * <li>http://www.iupac.org/inchi/
 * </ul>
 * 
 * @author Sam Adams
 */
public class InChIGeneratorFactory {
    
    /**
     * <p>Constructor for InChIGeneratorFactory. Ensures that native code
     * required for InChI/Structure interconversion is available, otherwise
     * throws CMLException.
     * 
     * @throws CMLException
     */
    public InChIGeneratorFactory() throws CMLException {
        try {
            JniInchiWrapper.loadLibrary();
        } catch (LoadNativeLibraryException lnle) {
            throw new CMLException("Unable to load native code; " + lnle.getMessage());
        }
    }
    
    /**
     * <p>Gets InChI generator for CMLMolecule.
     * 
     * @param molecule     CMLMolecule to generate InChI for.
     * @throws CMLException
      * @return InChIGenrator
      * 
     */
    public InChIGenerator getInChIGenerator(CMLMolecule molecule) throws CMLException {
        return(new InChIGenerator(molecule));
    }
    
    /**
     * <p>Gets InChI generator for CMLMolecule.
     * 
     * @param molecule     CMLMolecule to generate InChI for.
     * @param options       String of options for InChI generation.
     * @return inchi generator
     * @throws CMLException
     */
    public InChIGenerator getInChIGenerator(CMLMolecule molecule, String options) throws CMLException {
        return(new InChIGenerator(molecule, options));
    }
    
    /**
     * <p>Gets InChI generator for CMLMolecule.
     * 
     * @param molecule     CMLMolecule to generate InChI for.
     * @param options      List of options (net.sf.jniinchi.INCHI_OPTION) for InChI generation.
     * @return Inchi generator
     * @throws CMLException
     */
    public InChIGenerator getInChIGenerator(CMLMolecule molecule, List options) throws CMLException {
        return(new InChIGenerator(molecule, options));
    }
    
    /**
     * <p>Gets structure generator for an InChI string.
     * 
     * @param inchi         InChI to generate structure from.
     * @return InChIToStructure
     * @throws CMLException
     */
    public InChIToStructure getInChIToStructure(String inchi) throws CMLException {
        return(new InChIToStructure(inchi));
    }
    
    /**
     * <p>Gets structure generator for an InChI string.
     * 
     * @param inchi         InChI to generate structure from.
     * @param options       String of options for structure generation.
     * @return InChIToStructure
     * @throws CMLException
     */
    public InChIToStructure getInChIToStructure(String inchi, String options) throws CMLException {
        return(new InChIToStructure(inchi, options));
    }
    
    /**
     * <p>Gets structure generator for an InChI string.
     * 
     * @param inchi         InChI to generate structure from.
     * @param options       List of options (net.sf.jniinchi.INCHI_OPTION) for structure generation.
     * @return InChIToStructure
     * @throws CMLException
     */
    public InChIToStructure getInChIToStructure(String inchi, List options) throws CMLException {
        return(new InChIToStructure(inchi, options));
    }
}
