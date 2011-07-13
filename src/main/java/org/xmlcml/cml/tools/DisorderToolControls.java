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

/**
 * 
 * @author pm286
 *
 */
public class DisorderToolControls {
	
	private RemoveControl removeControl;
	private ProcessControl processControl;

	/** constructor
	 */
	public DisorderToolControls() {
		this(ProcessControl.STRICT, RemoveControl.REMOVE_MINOR_DISORDER);
	}
	
	/** constructor.
	 * 
	 * @param processControl
	 */
	public DisorderToolControls(ProcessControl processControl) {
		this(processControl, RemoveControl.REMOVE_MINOR_DISORDER);
	}
	
	/** constructor.
	 * 
	 * @param removeControl
	 */
	public DisorderToolControls(RemoveControl removeControl) {
		this(ProcessControl.STRICT, removeControl);
	}
	
	/** constructor.
	 * 
	 * @param processControl
	 * @param removeControl
	 */
	public DisorderToolControls(ProcessControl processControl, RemoveControl removeControl) {
		this.processControl = processControl;
		this.removeControl = removeControl;
	}
	
	/**
	 * control operations for removing disorder.
	 */
	public enum RemoveControl {
		/** used with processDisorder to remove minor components. */
		REMOVE_MINOR_DISORDER;
		private RemoveControl() {
		}
	}

	/**
	 * control operations for removing disorder.
	 */
	public enum ProcessControl {
		/**
		 * used with processDisorder to handle disorder in a molecule. STRICT -
		 * if the disorder does not comply with the CIF specification, then an
		 * error is thrown. LOOSE - attempts to process disorder which deviates
		 * from the CIF specification
		 */
		STRICT,
	    /** dewisott */
		LOOSE;
		private ProcessControl() {
		}
	}
	
	/**
     * sets disorder remove control.
     * 
     * @param removeControl
     */
	public void setRemoveControl(RemoveControl removeControl) {
		this.removeControl = removeControl;
	}
	
	/**
     * sets disorder process control.
     * 
     * @param processControl
     */
	public void setProcessControl(ProcessControl processControl) {
		this.processControl = processControl;
	}
	
	/**
     * gets disorder remove control.
	 * @return control
     */
	public RemoveControl getRemoveControl() {
		return this.removeControl;
	}
	
	/**
     * gets disorder process control.
	 * @return control
     */
	public ProcessControl getProcessControl() {
		return this.processControl;
	}
}
