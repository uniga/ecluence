/*******************************************************************************
 * Copyright (c) 2017, 2018 Uniga.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mikkel R. Jakobsen - initial API and implementation
 *******************************************************************************/
package dk.uniga.ecluence.ui.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.PageContent;
import dk.uniga.ecluence.core.PageContentProcessingException;
import dk.uniga.ecluence.core.PageContentProcessor;
import dk.uniga.ecluence.core.PageKey;
import dk.uniga.ecluence.ui.Activator;

public class PageContentRenderer {

	private final ContentFormatter formatter;
	private final List<PageContentProcessor> contentProcessors = new ArrayList<>();

	private final Map<PageKey, PageContent> pageContents = new HashMap<>();

	public PageContentRenderer(ContentFormatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * Adds a PageContentProcessor to process the content. Processors are called in
	 * the order they have been added.
	 * 
	 * @param processor
	 *            PageContentProcessor instance to process the content
	 */
	public void addContentProcessor(PageContentProcessor processor) {
		contentProcessors.add(Objects.requireNonNull(processor));
	}

	public PageContent render(ContentBean page) {
		PageContent rendered = getPageContent(page);
		for (PageContentProcessor processor : contentProcessors) {
			try {
				processor.process(rendered);
			} catch (PageContentProcessingException e) {
				// log error and continue using the content as is
				Activator.handleError("Exception processing page content", e, false);
			}
		}
		return rendered;
	}

	private PageContent getPageContent(ContentBean page) {
		PageContent pageContent = pageContents.get(PageKey.createKey(page));
		return (pageContent != null) ? pageContent : createPageContent(page);
	}

	private PageContent createPageContent(ContentBean page) {
		PageKey key = PageKey.createKey(page);
		PageContent rendered = new PageContent(page, key);
		rendered.setContent(formatter.formatContent(page));
		pageContents.put(key, rendered);
		return rendered;
	}
}
