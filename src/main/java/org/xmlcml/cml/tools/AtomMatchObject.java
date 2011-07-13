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

import org.xmlcml.cml.tools.AtomMatcher.Strategy;

/**
 * a DTO for matching atome
 * @author pm286
 *
 */
public class AtomMatchObject {

	private String[] excludeElementTypes;
	private String[] includeElementTypes;
	private String[] excludeLigandElementTypes;
	private String[] includeLigandElementTypes;
	private boolean useCharge;
	private boolean useLabel;
	private boolean useImplicitHydrogens = false;
	private int atomTreeLevel = -1;
	private int maximumAtomTreeLevel;
	private Strategy atomMatchStrategy;
	private Strategy atomSetExpansionStrategy;
	private boolean explicitHydrogens;
	private boolean useExplicitHydrogens;
	
	
	public boolean isUseImplicitHydrogens() {
		return useImplicitHydrogens;
	}

	public void setUseImplicitHydrogens(boolean useImplicitHydrogens) {
		this.useImplicitHydrogens = useImplicitHydrogens;
	}

	public int getAtomTreeLevel() {
		return atomTreeLevel;
	}

	public void setAtomTreeLevel(int atomTreeLevel) {
		this.atomTreeLevel = atomTreeLevel;
	}

	public Strategy getAtomMatchStrategy() {
		return atomMatchStrategy;
	}

	public void setAtomMatchStrategy(Strategy atomMatchStrategy) {
		this.atomMatchStrategy = atomMatchStrategy;
	}

	public Strategy getAtomSetExpansionStrategy() {
		return atomSetExpansionStrategy;
	}

	public void setAtomSetExpansionStrategy(Strategy atomSetExpansionStrategy) {
		this.atomSetExpansionStrategy = atomSetExpansionStrategy;
	}

	public int getMaximumAtomTreeLevel() {
		return maximumAtomTreeLevel;
	}

	void setMaximumAtomTreeLevel(int maximumAtomTreeLevel) {
		this.maximumAtomTreeLevel = maximumAtomTreeLevel;
	}

	public String[] getExcludeElementTypes() {
		return excludeElementTypes;
	}

	public void setExcludeElementTypes(String[] excludeElementTypes) {
		this.excludeElementTypes = excludeElementTypes;
	}

	public String[] getIncludeElementTypes() {
		return includeElementTypes;
	}

	public void setIncludeElementTypes(String[] includeElementTypes) {
		this.includeElementTypes = includeElementTypes;
	}

	public String[] getExcludeLigandElementTypes() {
		return excludeLigandElementTypes;
	}

	public void setExcludeLigandElementTypes(String[] excludeLigandElementTypes) {
		this.excludeLigandElementTypes = excludeLigandElementTypes;
	}

	public String[] getIncludeLigandElementTypes() {
		return includeLigandElementTypes;
	}

	public void setIncludeLigandElementTypes(String[] includeLigandElementTypes) {
		this.includeLigandElementTypes = includeLigandElementTypes;
	}

	public boolean isUseCharge() {
		return useCharge;
	}

	public void setUseCharge(boolean useCharge) {
		this.useCharge = useCharge;
	}

	public boolean isUseLabel() {
		return useLabel;
	}

	public void setUseLabel(boolean useLabel) {
		this.useLabel = useLabel;
	}

	public AtomMatchObject() {
/*
	private String[] excludeElementTypes;
	private String[] includeElementTypes;
	private String[] excludeLigandElementTypes;
	private String[] includeLigandElementTypes;
	private boolean useCharge;
	private boolean useLabel;
	private boolean useImplicitHydrogens = false;
	private int atomTreeLevel = -1;
	private int maximumAtomTreeLevel;
	private Strategy atomMatchStrategy;
	private Strategy atomSetExpansionStrategy;
	private boolean useExplicitHydrogens;
 */
		excludeElementTypes = new String[] {};
		includeElementTypes = new String[] {};
		excludeLigandElementTypes = new String[] {};
		includeLigandElementTypes = new String[] {};
		useCharge = false;
		useLabel = false;
		useImplicitHydrogens = false;
		useExplicitHydrogens = true;
		atomTreeLevel = -1;
		maximumAtomTreeLevel = 3;
		atomMatchStrategy = null;
		atomSetExpansionStrategy = null;
	}
	
	public AtomMatchObject(AtomMatchObject atomMatchObject) {
		this.excludeElementTypes = atomMatchObject.excludeElementTypes;
		this.includeElementTypes = atomMatchObject.includeElementTypes;
		this.excludeLigandElementTypes = atomMatchObject.excludeLigandElementTypes;
		this.includeLigandElementTypes = atomMatchObject.includeLigandElementTypes;
		this.useCharge = atomMatchObject.useCharge;
		this.useLabel = atomMatchObject.useLabel;
		this.useImplicitHydrogens = atomMatchObject.useImplicitHydrogens;
		this.useExplicitHydrogens = atomMatchObject.useExplicitHydrogens;
		this.atomTreeLevel = atomMatchObject.atomTreeLevel;
		this.maximumAtomTreeLevel = atomMatchObject.maximumAtomTreeLevel;
		this.atomMatchStrategy = atomMatchObject.atomMatchStrategy;
		this.atomSetExpansionStrategy = atomMatchObject.atomSetExpansionStrategy;
	}
	
	public void setExplicitHydrogens(boolean explicitHydrogens) {
		this.explicitHydrogens = explicitHydrogens;
	}

	public boolean isExplicitHydrogens() {
		return explicitHydrogens;
	}

	public void setUseExplicitHydrogens(boolean useExplicitHydrogens) {
		this.useExplicitHydrogens = useExplicitHydrogens;
	}

	public boolean isUseExplicitHydrogens() {
		return useExplicitHydrogens;
	}

}
