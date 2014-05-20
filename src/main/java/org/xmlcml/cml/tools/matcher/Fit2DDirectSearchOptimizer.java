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

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.apache.commons.math.optimization.direct.DirectSearchOptimizer;
import org.apache.commons.math.optimization.direct.NelderMead;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Transform2;

public class Fit2DDirectSearchOptimizer  {

	private static Logger LOG = Logger.getLogger(Fit2DDirectSearchOptimizer.class);
	
	private double[] startPoint = new double[3];
	private double[] steps = new double[3];
	private CMLMolecule[] mols;
	private CMLMap map;
//	private SimpleScalarValueChecker checker;
	private int maxIterations;
	private DirectSearchOptimizer directSearchOptimizer;
	private SquaredDeviationRealFunction sqdevFunc;
	private GoalType goalType;
	
	public Fit2DDirectSearchOptimizer(CMLMolecule mol1, CMLMolecule mol2, CMLMap map) {
//		directSearchOptimizer = new MultiDirectional();
		directSearchOptimizer = new NelderMead();
		setDefaults();
		sqdevFunc = new SquaredDeviationRealFunction(this);
		mols = new CMLMolecule[2];
		mols[0] = mol1;
		mols[1] = mol2;
		this.map = map;
	}
	
	private void setDefaults() {
		setConvergenceLimit(0.5, 0.1);
		steps[0] = 0.1;
		steps[1] = 50.0;
		steps[2] = 50.0;
		directSearchOptimizer.setStartConfiguration(steps);
		startPoint[0] = -1.0;
		startPoint[1] = -1.0;
		startPoint[2] = -1.0;
		maxIterations = 100;
		setMaxIterations(maxIterations);
		this.goalType = GoalType.MINIMIZE;
	}

	public CMLMolecule[] getMols() {
		return mols;
	}

	public CMLMap getMap() {
		return map;
	}

	public void setStartPoint(double[] start) {
		System.arraycopy(start, 0, startPoint, 0, start.length);
	}
	
	public void setSteps(double[] steps) {
		directSearchOptimizer.setStartConfiguration(steps);
	}
	
	public void setConvergenceLimit(double drel, double dabs) {
		directSearchOptimizer.setConvergenceChecker(new SimpleScalarValueChecker(drel, dabs));
	}
		
	public void setMaxIterations(int maxIterations) {
		directSearchOptimizer.setMaxIterations(maxIterations);
	}
	
	public void setGoalType(GoalType goalType) {
		this.goalType = goalType;
	}
	public void optimize() {
		LOG.trace(goalType);
		try {
			directSearchOptimizer.optimize(sqdevFunc, goalType, startPoint);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("optimize error "+e);
		}
	}
	
	public double getCurrentValue() {
		return sqdevFunc.getValue();
	}
	
	public double[] getCurrentParams() {
		return sqdevFunc.getParams();
	}
	
	public Transform2 getTransform2() {
		return sqdevFunc.getTransform();
	}
}
