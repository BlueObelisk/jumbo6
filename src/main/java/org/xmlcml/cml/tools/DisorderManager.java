package org.xmlcml.cml.tools;

public class DisorderManager {
	
	private RemoveControl removeControl;
	private ProcessControl processControl;
	
	public DisorderManager() {
		this(ProcessControl.STRICT, RemoveControl.REMOVE_MINOR_DISORDER);
	}
	
	public DisorderManager(ProcessControl processControl) {
		this(processControl, RemoveControl.REMOVE_MINOR_DISORDER);
	}
	
	public DisorderManager(RemoveControl removeControl) {
		this(ProcessControl.STRICT, removeControl);
	}
	
	
	public DisorderManager(ProcessControl processControl, RemoveControl removeControl) {
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
		/** */
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
     * 
     * @param removeControl
     */
	public RemoveControl getRemoveControl() {
		return this.removeControl;
	}
	
	/**
     * gets disorder process control.
     * 
     * @param removeControl
     */
	public ProcessControl getProcessControl() {
		return this.processControl;
	}
}
