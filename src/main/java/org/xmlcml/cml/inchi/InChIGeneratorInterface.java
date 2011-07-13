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

package org.xmlcml.cml.inchi;

public interface InChIGeneratorInterface {

	/**
	 * did generation succeed?
	 * @return true if INCHI_RET.OKAY returned in JNIInchi
	 */
	boolean isOK();
	
	/** relays any message fromJNInchi
	 * 
	 * @return message (not controlled in JUMBO)
	 */
	String getMessage();
	
	/** the satisfactorily generated InChI.
	 * 
	 * @return InChI
	 */
	String getInchi();
	
}
