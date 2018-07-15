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
package dk.uniga.ecluence.core;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

public class PageContent {

	private final ContentBean page;
	private final PageKey key;
	private String renderedContent;

	private final Set<Listener> listeners = new HashSet<>();
	
	public PageContent(ContentBean page, PageKey key) {
		this.page = Objects.requireNonNull(page);
		this.key = key;
	}
	
	public PageKey getKey() {
		return key;
	}
	
	public String getContent() {
		return renderedContent;
	}
	
	public void setContent(String modified) {
		this.renderedContent = Objects.requireNonNull(modified);
		listeners.forEach((l) -> l.contentChanged(this));
	}

	public ContentBean getPage() {
		return page;
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void close() {
		listeners.forEach((l) -> l.closed(this));
	}
	
	public interface Listener {

		void contentChanged(PageContent content);
		
		void closed(PageContent content);
	}

}