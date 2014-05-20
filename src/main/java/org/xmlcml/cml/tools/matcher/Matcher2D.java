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

package org.xmlcml.cml.tools.matcher;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Transform2;

/**
 * matches 2D structures
 * @author pm286
 *
 */
public class Matcher2D {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(Matcher2D.class);

	public Matcher2D() {
		
	}
	
	public Transform2 fit2D(CMLMolecule mol1, CMLMolecule mol2, CMLMap map) {

		double[] steps = new double[]{0.1, 50., 50.}; // angle, xshift, yshift
		double[] startPoint = new double[]{0.0, 0.0, 0.0}; // angle, xshift, yshift

//		LOG.debug("Centroid1 "+mol1.calculateCentroid2D());
//		LOG.debug("Centroid2 "+mol2.calculateCentroid2D());
		Fit2DDirectSearchOptimizer optimizer = new Fit2DDirectSearchOptimizer(mol1, mol2, map);
		optimizer.setSteps(steps);
		optimizer.setStartPoint(startPoint);
		optimizer.setConvergenceLimit(1.0e-8, 1.0e-8);
		optimizer.setMaxIterations(200);
		optimizer.optimize();
//		LOG.debug("Deviation " + optimizer.getCurrentValue());
		Transform2 transform = optimizer.getTransform2();
//		System.out.println("ang deg: "+transform.getAngleOfRotation().getDegrees());
		return transform;
	}
	

}
