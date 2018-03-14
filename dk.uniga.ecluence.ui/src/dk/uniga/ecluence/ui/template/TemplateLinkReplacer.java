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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;

public class TemplateLinkReplacer {

	public String replace(String content) {
		List<SimpleElement> elements = new SimpleHtmlElementMatcher().matchElements("link", content);
		for (SimpleElement element : elements) {
			String href = element.getAttribute("href");
			String localFile = "";
			element.setAttribute("href", localFile);
		}
		
		return content;
	}

	/**
	 * Replace any platform-based URL in the href attribute to a file path.
	 *
	 * <link rel="stylesheet" 
	 *  type="text/css" 
	 *  href="platform:/plugin/dk.uniga.ecluence.ui/files/css/combined.css"/>
	 *
	 * @param page
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String replaceLocalLinks(String page) throws MalformedURLException, IOException {
		List<SimpleElement> elements = new SimpleHtmlElementMatcher().matchElements("link", page);
		for (SimpleElement element : elements) {
			String href = element.getAttribute("href");
			if (href.startsWith("platform:")) {
				URL localFile = FileLocator.toFileURL(new URL(href));
				element.setAttribute("href", localFile.toString());
				page = page.replace(href, localFile.toString());
			}
		}
		return page;
	}

	
}
