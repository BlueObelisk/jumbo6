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

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.element.CMLAmount;
import org.xmlcml.cml.element.CMLProduct;

/**
 * tool for managing products
 *
 * @author pmr
 *
 */
public class ProductTool extends AbstractTool {
	final static Logger LOG = Logger.getLogger(ProductTool.class);
	public static final String ID_PREFIX = "product";

	CMLProduct product = null;

	/** constructor.
	 */
	public ProductTool(CMLProduct product) throws RuntimeException {
		init();
		this.product = product;
	}


	void init() {
	}


	/**
	 * get product.
	 *
	 * @return the product or null
	 */
	public CMLProduct getProduct() {
		return this.product;
	}

    
	/** gets ProductTool associated with product.
	 * if null creates one and sets it in product
	 * @param product
	 * @return tool
	 */
	public static ProductTool getOrCreateTool(CMLProduct product) {
		ProductTool productTool = (product == null) ? null : (ProductTool) product.getTool();
		if (productTool == null) {
			productTool = new ProductTool(product);
			product.setTool(productTool);
		}
		return productTool;
	}

	public void ensureId(String defaultId) {
		ReactantTool.ensureId(product, defaultId);
	}

	public CMLAmount getMolarAmount() {
		return ReactantTool.getMolarAmount(product);
	}

	public double getCountNoDefault() {
		return ReactantTool.getCount(product);
	}

	public double getCountDefaultToUnity() {
		return ReactantTool.getCountDefaultToUnity(product);
	}
	

};