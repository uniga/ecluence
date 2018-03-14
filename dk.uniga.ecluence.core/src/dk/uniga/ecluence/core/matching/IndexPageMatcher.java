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
package dk.uniga.ecluence.core.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentProvider;

/**
 * Returns all index pages regardless of selection.
 */
public final class IndexPageMatcher implements ContentMatcher {
	
	private static final Logger log = LoggerFactory.getLogger(IndexPageMatcher.class);
	
	private final Supplier<ContentProvider> contentProviderSupplier;

	private final String label;
	
	public IndexPageMatcher(Supplier<ContentProvider> contentProviderSupplier, String label) {
		this.contentProviderSupplier = contentProviderSupplier;
		this.label = label;
	}

	@Override
	public Collection<ContentMatch> getMatches(Object o) throws QueryException {
		ContentProvider contentProvider = contentProviderSupplier.get();
		ArrayList<ContentMatch> results = new ArrayList<ContentMatch>();
		if (contentProvider != null) {
			contentProvider.getPages().stream()
					.map((page) -> new ContentMatch(page, new IndexPageMatchExplanation(label)))
					.forEach((match -> results.add(match)));
		}
		log.debug("getMatches({}) from contentProvider {}: {} pages", (o == null ? null : o.getClass()),
				contentProvider, results.size());
		return results;
	}

}
