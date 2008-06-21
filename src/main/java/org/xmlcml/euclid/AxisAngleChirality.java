package org.xmlcml.euclid;

/** holds results of deconstructing rotation matrix
 * 
 * @author ned24
 *
 */
public class AxisAngleChirality {

	private Vector3 axis;
	private double angle;
	private int chirality;
	
	/** constructor with deep copy
	 * 
	 * @param axis
	 * @param angle
	 * @param chirality
	 */
	public AxisAngleChirality(Vector3 axis, double angle, int chirality) {
		this.axis = new Vector3(axis);
		this.angle = angle;
		this.chirality = chirality;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public Vector3 getAxis() {
		return axis;
	}

	public void setAxis(Vector3 axis) {
		this.axis = axis;
	}

	public int getChirality() {
		return chirality;
	}

	public void setChirality(int chirality) {
		this.chirality = chirality;
	}
}
