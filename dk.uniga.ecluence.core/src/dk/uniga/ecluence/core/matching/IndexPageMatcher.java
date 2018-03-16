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
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelBean;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentProvider;

/**
 * Returns all index pages from a given content provider. If a page has a
 * rank-XX label, the rank of the match is set to 1000 + XX, otherwise a rank of
 * 1100 is used..
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
					.map((page) -> createMatch(page))
					.forEach((match -> results.add(match)));
		}
		log.debug("getMatches({}) from {}: {} pages", (o == null ? null : o.getClass().getSimpleName()),
				contentProvider, results.size());
		log.debug("results {}", results);
		return results;
	}

	private ContentMatch createMatch(ContentBean page) {
		Rank rank = getRank(page);
		return new ContentMatch(page, new IndexPageMatchExplanation(label), rank);
	}

	private Rank getRank(ContentBean page) {
		List<LabelBean> labels = page.getMetadata().getLabels().getResults();
		Optional<LabelBean> rankLabel = StreamSupport.stream(labels.spliterator(), false)
			.filter(p -> p.getName().startsWith("rank-"))
			.findFirst();
		if (rankLabel.isPresent()) 
			return new Rank(1000 + extractRankFromLabel(rankLabel.get().getName()));
		return new Rank(1000);
	}

	Pattern rankPattern = Pattern.compile("rank-([0-9])+");
	
	private int extractRankFromLabel(String label) {
		Matcher matcher = rankPattern.matcher(label);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group(1));
		}
		return 100;
	}

}
