package org.xmlcml.cml.tools.matcher;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.apache.commons.math.optimization.direct.DirectSearchOptimizer;
import org.apache.commons.math.optimization.direct.NelderMead;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Transform2;

public class Fit2DDirectSearchOptimizer  {

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
		System.out.println(goalType);
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
