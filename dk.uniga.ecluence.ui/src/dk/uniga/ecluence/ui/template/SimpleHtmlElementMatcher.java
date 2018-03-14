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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleHtmlElementMatcher {

	public List<SimpleElement> matchElements(String elementName, String content) {
		
		List<SimpleElement> elements = new ArrayList<>();
		
		String regex = "(<"+ elementName +"([\\w\\W]+?)/?>)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String group = matcher.group(0);
			HashMap<String, String> attributes = findAttributes(group);
			elements.add(new SimpleElement(elementName, group, attributes));
		}
		return elements;
	}
	
	final static Pattern ATTRIBUTE_PATTERN = Pattern.compile("([\\w-]+)\\s*=\\s*\\\"([^\"]*)\\\"");
	
	private HashMap<String, String> findAttributes(String element) {
		Matcher matcher = ATTRIBUTE_PATTERN.matcher(element);
		HashMap<String, String> attributes = new HashMap<>();
		while (matcher.find()) {
			attributes.put(matcher.group(1), matcher.group(2));
		}
		return attributes;
	}
}
