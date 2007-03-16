package org.xmlcml.cml.interfacex;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.xmlcml.cml.attribute.GenericDictionaryMap;

/**
 * user-modifiable class supporting dictionary. *
 */
public interface GenericDictionary {

	/**
	 * get title of dictionary.
	 * 
	 * @return title
	 */
	String getTitle();

	/**
	 * get namespace of dictionary.
	 * 
	 * @return namespace URI
	 */
	String getNamespace();

	/**
	 * create a dictionary from file.
	 * 
	 * @param file
	 * @return the dictionary
	 * @throws IOException
	 */
	GenericDictionary createDictionary(File file) throws IOException;

	/**
	 * create a dictionary from inputStreamContainer.
	 * 
	 * @param url
	 * @return the dictionary
	 * @throws IOException
	 */
	GenericDictionary createDictionary(URL url) throws IOException;

	/**
	 * create dictionaryMap from dicrectory.
	 * 
	 * @param dir
	 *            directory to start from
	 * @param useSubdirectories
	 *            if true recurse downwards
	 * @return dictionaryMap
	 */
	GenericDictionaryMap createDictionaryMap(File dir, boolean useSubdirectories);

	/**
	 * get entry by id.
	 * 
	 * @param id
	 * @return the entry or null
	 */
	GenericEntry getGenericEntry(String id);

	/**
	 * index Entries.
	 */
	void indexEntries();

	/**
	 * debug. probably to sysout.
	 */
	void debug();

}
