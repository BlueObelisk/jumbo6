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

import static org.xmlcml.euclid.EuclidConstants.U_S;

/** superclass to manage resources etc.
 * 
 * @author pm286
 *
 */
public final class AbstractToolTestBase {
    /**
     * resource
     */
    public final static String TOOLS_RESOURCE = "org"+U_S+"xmlcml"+U_S+"cml"+U_S+"tools";
    /**
     * examples
     */
    public final static String TOOLS_EXAMPLES = TOOLS_RESOURCE+U_S+"examples";

    /**
     * crystal examples
     */
    public final static String CRYSTAL_EXAMPLES = TOOLS_EXAMPLES+U_S+"cryst";
}