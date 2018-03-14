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
package dk.uniga.ecluence.ui.preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.ui.template.SimpleElement;
import dk.uniga.ecluence.ui.template.SimpleHtmlElementMatcher;
import dk.uniga.ecluence.ui.template.TemplateStore;

public class TemplateImporter {

	private static Logger log = LoggerFactory.getLogger(TemplateImporter.class);
	
	private TemplateStore templateStore;

	public TemplateImporter(TemplateStore templateStore) throws MalformedURLException {
		this.templateStore = templateStore;
	}

	public TemplateFileset execute(String templateUrl) throws IOException, URISyntaxException {
		log.debug("execute({})", templateUrl);
		URL url = new URL(templateUrl);
		return downloadTemplateFileset(url);
	}

	private TemplateFileset downloadTemplateFileset(URL templateUrl) throws IOException, URISyntaxException {
		TemplateFileset fileset = new TemplateFileset();
		String templateContent = downloadToString(templateUrl);
		String modifiedContent = downloadLinked(templateUrl, templateContent, fileset, "link", "href");
		modifiedContent = downloadLinked(templateUrl, modifiedContent, fileset, "img", "src");
		String templateFile = fileset.getTemplateFilename(new File(templateUrl.getPath()).getName().replace(" ", "%20"));
		fileset.add(templateStore.putFile(templateFile, modifiedContent));
		return fileset;
	}
	
	private String downloadLinked(URL templateUrl, String content, TemplateFileset fileset, String elementName, String srcAttr) throws MalformedURLException, URISyntaxException, IOException {
		String modified = content;
		List<SimpleElement> elements = new SimpleHtmlElementMatcher().matchElements(elementName, content);
		for (SimpleElement element : elements) {
			String href = element.getAttribute(srcAttr);
			log.debug("downloadLinked({}) {}", templateUrl, href);
			if (!isPlaceholder(href)) {
				File localFile = downloadLinkedFile(templateUrl, fileset, href);
				element.setAttribute(srcAttr, localFile.getAbsolutePath());
				modified = modified.replace(element.getOriginalElement(), element.getElement());
			}
		}
		return modified;
	}

	private boolean isPlaceholder(String href) {
		return href.startsWith("##");
	}

	private File downloadLinkedFile(URL url, TemplateFileset fileset, String href)
			throws MalformedURLException, URISyntaxException, IOException {
		URL linkedUrl = getRelativeUrl(url, href);
		log.debug("downloadLinkedFile({}) {}", url, linkedUrl);
		String filename = fileset.getLinkedFilename(new File(linkedUrl.getPath()).getName().replace(" ", "%20"));
		return templateStore.downloadFile(linkedUrl, filename);
	}

	private URL getRelativeUrl(URL url, String href) throws MalformedURLException, URISyntaxException {
		return url.toURI().resolve(new URI(href)).toURL();
	}

	private String downloadToString(URL url) throws MalformedURLException, IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }	
    }
}
