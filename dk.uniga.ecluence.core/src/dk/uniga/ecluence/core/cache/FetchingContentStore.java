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

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.cql.SearchResultEntry;
import dk.uniga.ecluence.core.ContentUpdateListener;
import dk.uniga.ecluence.core.QueryException;

/**
 * Keeps local copies of {@link ContentBean ContentBeans} in a
 * {@link ContentStore}. Fetches content using {@link ContentFetcher} and
 * expands the content using {@link ContentExpander}.
 * 
 * Listeners are notified if new or updated content has been fetched.
 * 
 * Use the asynchronous {@link #fetch()} or the blocking
 * {@link #fetch(boolean)}, which returns the number of pages that were fetched.
 */
public class FetchingContentStore {

	private static final Logger log = LoggerFactory.getLogger(FetchingContentStore.class);

	private final ContentFetcher contentFetcher;
	
	private final ContentExpander contentExpander;

	private final ContentStore contentStore;

	private final Executor fetchExecutor;

	private final Collection<ContentUpdateListener> listeners = new HashSet<>();

	private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));
	
	/**
	 * 
	 * @param contentFetcher
	 * @param contentExpander
	 * @param contentStore
	 * @param apiExecutor
	 *            Executor to perform long-running fetches. The executor is
	 *            responsible for handling exceptions from the call wrapped in a
	 *            RuntimeException.
	 * @throws QueryException
	 * @throws ContentStoreException 
	 */
	public FetchingContentStore(ContentFetcher contentFetcher, ContentExpander contentExpander,
			ContentStore contentStore, Executor apiExecutor)
			throws QueryException, ContentStoreException {
		this.contentFetcher = requireNonNull(contentFetcher);
		this.contentExpander = requireNonNull(contentExpander);
		this.contentStore = requireNonNull(contentStore);
		this.fetchExecutor = requireNonNull(apiExecutor);
	}

	/**
	 * Returns all pages.
	 * 
	 * @return Collection of {@link ContentBean}s
	 * @throws ContentStoreException
	 */
	public Collection<ContentBean> getAll() throws ContentStoreException {
		return contentStore.getAll();
	}
	
	/**
	 * Non blocking fetching of new content. Listeners will be notified if new
	 * content is available.
	 */
	public void fetch() {
		fetchExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					fetch(false);
				} catch (QueryException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	/**
	 * Fetches new content. Callers are blocked until the fetching is finished.
	 * 
	 * @param force
	 *            <code>true</code> if fetcher should be forced to fetch
	 * @return number of pages fetched
	 * @throws QueryException
	 */
	public int fetch(boolean force) throws QueryException {
		return contentFetcher.fetch(force, (content) -> { storeFetchedContent(content); });
	}
	
	private void storeFetchedContent(List<SearchResultEntry> results) {
		log.debug("Fetched {} search results", results.size());
		List<ContentBean> expanded = results.stream()
				.filter((entry) -> !inStore(entry))
				.map((entry) -> expand((ContentBean) entry.getBean()))
				.filter(Optional::isPresent).map(Optional::get)
				.peek((page) -> store(page))
				.collect(Collectors.toList());
		notifyListeners(expanded);
	}

	private void store(ContentBean page) {
		logContent(page);
		try {
			contentStore.putContent(page);
		} catch (ContentStoreException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns <code>true</code> if the page corresponding to a search result entry
	 * is in the content store and that it has not been updated.
	 * 
	 * @param entry
	 * @return
	 */
	private boolean inStore(SearchResultEntry entry) {
		try {
			Optional<ContentBean> storedPage = contentStore.get(entry.getBean().getId());
			return (storedPage.isPresent() && !isNewer(entry.getLastModified(), storedPage.get()));
		} catch (ContentStoreException e) {
			// Take this to suggest that the page is not in the store
			return false;
		}
	}

	/**
	 * Returns whether the given date is later than the version date of
	 * the given page.
	 * 
	 * @param date
	 * @param page
	 * @return <code>true</code> if page1 is newer than page2.
	 */
	private boolean isNewer(LocalDateTime date, ContentBean page) {
		LocalDateTime pageDate = getVersionDate(page);
		boolean isNewer = date.isAfter(pageDate);
		log.debug("{} isNewer than {}: {}", date, pageDate, isNewer);
		return isNewer;
	}

	private LocalDateTime getVersionDate(ContentBean page) {
		// Format looks like ISO_INSTANT: "when":"2017-09-08T12:33:39.226Z"
		return LocalDateTime.parse(page.getVersion().getWhen(), INSTANT_FORMATTER);
	}

	/**
	 * Returns the given page with fully expanded fields.
	 * 
	 * @param page
	 *            the page to expand
	 * @return expanded page or {@link Optional#empty()} if it was not possible to
	 *         expand the page
	 */
	private Optional<ContentBean> expand(ContentBean page) {
		log.debug("expand: expand page {} {}", page.getId(), page.getTitle());
		try {
			return Optional.of(contentExpander.expand(page));
		} catch (ExpandingContentException e) {
			// Return no expanded content
			return Optional.empty();
		}
	}
	
	private void logContent(ContentBean c) {
		log.debug("Content cached: {}", c.getId());
	}

	private void notifyListeners(Collection<ContentBean> pages) {
		for (ContentUpdateListener listener : listeners) {
			listener.contentUpdated(pages);
		}
	}

	public void addListener(ContentUpdateListener listener) {
		listeners.add(requireNonNull(listener));
	}

	public void removeListener(ContentUpdateListener listener) {
		listeners.remove(requireNonNull(listener));
	}
}
