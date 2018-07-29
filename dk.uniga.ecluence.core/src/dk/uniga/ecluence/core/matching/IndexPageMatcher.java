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
package dk.uniga.ecluence.core.matching;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentProvider;

/**
 * Returns all index pages from a given content provider. If a page has a
 * rank-XX label, the rank of the match is set to 1000 + XX, otherwise a rank of
 * 1000 is used.
 */
public final class IndexPageMatcher implements ContentMatcher {
	
	private static final Logger log = LoggerFactory.getLogger(IndexPageMatcher.class);
	
	private final RankExtractor rankExtractor;
	
	private final Supplier<ContentProvider> contentProviderSupplier;
	
	private final String label;
	
	public IndexPageMatcher(Supplier<ContentProvider> contentProviderSupplier, String label) {
		this(new RankExtractor(), contentProviderSupplier, label);
	}
	
	public IndexPageMatcher(RankExtractor rankExtractor, Supplier<ContentProvider> contentProviderSupplier, String label) {
		this.rankExtractor = requireNonNull(rankExtractor);
		this.contentProviderSupplier = requireNonNull(contentProviderSupplier);
		this.label = requireNonNull(label);
	}

	@Override
	public Collection<ContentMatch> getMatches(Object o) throws QueryException {
		ContentProvider contentProvider = contentProviderSupplier.get();
		ArrayList<ContentMatch> results = new ArrayList<ContentMatch>();
		if (contentProvider != null) {
			contentProvider.getPages().stream()
					.map(page -> createMatch(page))
					.forEach(match -> results.add(match));
		}
		log.debug("getMatches(_) from {}: {} pages", contentProvider, results.size());
		log.debug("results {}", results);
		return results;
	}

	private ContentMatch createMatch(ContentBean page) {
		Rank rank = rankExtractor.getRank(page).orElse(new Rank(1000));
		return new ContentMatch(page, new IndexPageMatchExplanation(label), rank);
	}

}
