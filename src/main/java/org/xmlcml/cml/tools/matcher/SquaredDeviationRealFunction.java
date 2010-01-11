package org.xmlcml.cml.tools.matcher;

import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLLink;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.LinkTool;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;

public class SquaredDeviationRealFunction implements MultivariateRealFunction {

	private double[] params;
	private double value;
	private int count;
	private Fit2DDirectSearchOptimizer fit2dDirectSearchOptimizer;
	private Transform2 transform;
	
	public SquaredDeviationRealFunction(
			Fit2DDirectSearchOptimizer fit2dDirectSearchOptimizer) {
		this.fit2dDirectSearchOptimizer = fit2dDirectSearchOptimizer;
		count = 0;
	}

	/** just to keep track
	 * 
	 * @return
	 */
	public double[] getParams() {
		return params;
	}
	
	public double getValue() {
		return value;
	}
	
	public double value(double[] params) {
		this.params = new double[params.length];
		System.arraycopy(params, 0, this.params, 0, params.length);
		value = computeValue();
		return value;
	}

	private double computeValue() {
		CMLMap map = fit2dDirectSearchOptimizer.getMap();
		double sqdev = 0;
		CMLMolecule mol0 = fit2dDirectSearchOptimizer.getMols()[0];
		CMLMolecule mol1 = (CMLMolecule) fit2dDirectSearchOptimizer.getMols()[1].copy();
		transform = new Transform2(new Angle(params[0]));
		transform.setTranslation(new Real2(params[1], params[2]));
		MoleculeTool moleculeTool1 = MoleculeTool.getOrCreateTool(mol1);
		moleculeTool1.transform(transform);
		for (CMLLink link : map.getLinkElements()) {
			LinkTool linkTool = LinkTool.getOrCreateTool(link);
			CMLAtom fromAtom = linkTool.getSingleFromAtom(mol0);
			CMLAtom toAtom = linkTool.getSingleToAtom(mol1);
			if (toAtom != null && fromAtom != null) {
				double dist = toAtom.getDistance2(fromAtom);
				sqdev += dist*dist;
			}
		}
//		System.out.println("DEV "+sqdev);
		++count;
		return sqdev;
	}

	public Transform2 getTransform() {
		return transform;
	}

	public int getCount() {
		return count;
	}
	
	
}