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
package dk.uniga.ecluence.core.cache;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.domain.cql.SearchResultEntry;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.StoredTimestamp;

public final class ContentFetcherImpl implements ContentFetcher {

	private static final Logger log = LoggerFactory.getLogger(ContentFetcherImpl.class);

	private final ContentSearcher contentSearcher;

	private final StoredTimestamp lastFetchingTime;

	private final Duration durationBetweenFetching;

	public ContentFetcherImpl(ContentSearcher contentSearcher, StoredTimestamp lastFetchingTime, Duration durationBetweenFetching) {
		this.contentSearcher = Objects.requireNonNull(contentSearcher);
		this.lastFetchingTime = Objects.requireNonNull(lastFetchingTime);
		this.durationBetweenFetching = Objects.requireNonNull(durationBetweenFetching);
	}
	
	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.ContentFetcher#requireFullFetch()
	 */
	@Override
	public void requireFullFetch() {
		log.debug("requireFullFetch");
		lastFetchingTime.clear();
	}
	
	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.ContentFetcher#fetch(boolean, java.util.function.Consumer)
	 */
	@Override
	public int fetch(boolean force, Consumer<List<SearchResultEntry>> consumer) throws QueryException {
		synchronized (lastFetchingTime) {
			Optional<LocalDateTime> lastFetch = lastFetchingTime.get();
			if (force || canFetch(lastFetch)) {
				return fetchSince(consumer, lastFetch);
			}
			return 0;
		}
	}

	private int fetchSince(Consumer<List<SearchResultEntry>> consumer, Optional<LocalDateTime> lastFetch)
			throws QueryException {
		List<SearchResultEntry> results = searchSince(lastFetch);
		log.debug("fetchSince({}): {} results returned", lastFetch, results.size());
		consumer.accept(results);
		lastFetchingTime.set(LocalDateTime.now());
		return results.size();
	}

	private boolean canFetch(Optional<LocalDateTime> lastFetch) {
		// Always fetch if first time
		if (!lastFetch.isPresent()) {
			log.debug("canFetch lastFetch not present");
			return true;
		}
		Duration durationSinceFetch = Duration.between(lastFetch.get(), LocalDateTime.now());
		boolean canFetch = durationSinceFetch.compareTo(durationBetweenFetching) >= 0;
		log.debug("canFetch {} {}: {}", lastFetch, durationSinceFetch.toString(), canFetch);
		return canFetch;
	}

	private List<SearchResultEntry> searchSince(Optional<LocalDateTime> lastFetch) throws QueryException {
		if (lastFetch.isPresent()) {
			// Search for content changed since last fetch
			log.debug("Searching content created or updated since {} using {}", lastFetch.get(), contentSearcher);
			return contentSearcher.searchSince(lastFetch.get());
		} else {
			// Search all if first time
			log.debug("Never fetched from API, searching all content using {}", contentSearcher);
			return contentSearcher.search();
		}
	}

}
