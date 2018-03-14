/*******************************************************************************
 * Copyright (c) 2017 Uniga.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mikkel R. Jakobsen - initial API and implementation
 *******************************************************************************/
package dk.uniga.ecluence.ui.template;

import java.util.HashMap;

public class SimpleElement {

	private String elementName;
	private String element;
	private HashMap<String, String> attributes;

	public SimpleElement(String elementName, String element, HashMap<String, String> attributes) {
		this.elementName = elementName;
		this.element = element;
		this.attributes = attributes;
	}
	
	public String getOriginalElement() {
		return element;
	}
	
	public String getElement() {
		StringBuffer sb = new StringBuffer("<");
		sb.append(elementName).append(" ");
		for (String key : attributes.keySet()) {
			sb.append(' ').append(key).append("=\"");
			sb.append(attributes.get(key)).append('\"');
		}
		return sb.append("/>").toString();
	}

	public String getAttribute(String name) {
		return attributes.get(name);
	}
	
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}
}
