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
package dk.uniga.ecluence.ui.parts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkMatcher {

	private static final Logger log = LoggerFactory.getLogger(PageBrowser.class);

	private static final Pattern pageLinkPattern = Pattern.compile("file:\\/+wiki\\/.+\\/pages\\/([0-9]+)\\/.+");

	private static final Pattern wikiLinkPattern = Pattern.compile("file:\\/+wiki(\\/.+)");
	
	private static final Pattern resourcePattern = Pattern.compile("resource:(.+)");

	/**
	 * Matches the given location string to a page, returning the id of the page or
	 * <code>null</code> if the location didn't match the format of a page.
	 * 
	 * @param location
	 *            Location URL
	 * @return if of page or <code>null</code>
	 */
	public static String matchPage(String location) {
		Matcher matcher = pageLinkPattern.matcher(location);
		if (matcher.matches()) {
			String pageId = matcher.group(1);
			log.debug("Matches content page with id '{}'", pageId);
			return pageId;
		}
		return null;
	}

	public static String matchWikiLink(String location) {
		Matcher matcher = wikiLinkPattern.matcher(location);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String matchResource(String location) {
		Matcher matcher = resourcePattern.matcher(location);
		if (matcher.matches()) {
			String text = matcher.group(1);
			log.debug("Open linkified resource text: '{}'", text);
			return text;
		}
		return null;
	}

}
