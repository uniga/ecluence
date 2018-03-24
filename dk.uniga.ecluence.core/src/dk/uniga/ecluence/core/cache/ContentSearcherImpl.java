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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.client.SearchClient;
import de.itboehmer.confluence.rest.core.cql.CqlSearchBean;
import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.cql.CqlSearchResult;
import de.itboehmer.confluence.rest.core.domain.cql.SearchResultEntry;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.SearchContentException;

public final class ContentSearcherImpl implements ContentSearcher {

	private static final Logger log = LoggerFactory.getLogger(ContentSearcherImpl.class);

	private final ContentQuery query;

	private final SearchClient searchClient;

	private final int limit = 100;
	
	public ContentSearcherImpl(SearchClient searchClient, ContentQuery query) {
		this.searchClient = searchClient;
		this.query = query;
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.ContentSearcher#searchSince(java.time.LocalDateTime)
	 */
	@Override
	public List<SearchResultEntry> searchSince(LocalDateTime time) throws SearchContentException {
		String timeString = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		String cond = " and (lastModified > \"" + timeString + "\" or created > \"" + timeString + "\")";
		return search(Optional.of(cond));
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.ContentSearcher#search()
	 */
	@Override
	public List<SearchResultEntry> search() throws QueryException {
		return search(Optional.empty());
	}

	private List<SearchResultEntry> search(Optional<String> cqlConditions) throws SearchContentException {
		String q = query.getCQL();
		if (cqlConditions.isPresent())
			q += cqlConditions.get();
		log.debug("search {}", q);
		CqlSearchBean cql = buildCql(q);
		return search(cql);
	}
	
	private List<SearchResultEntry> search(CqlSearchBean cql) throws SearchContentException {
		try {
			CqlSearchResult results = searchClient.searchContent(cql).get();
			int mark = results.getStart() + results.getSize();
			List<SearchResultEntry> entries = new ArrayList<>(extractEntriesList(results));
			// recursively call to get the next batch of entries
			if (mark < results.getTotalSize()) {
				cql.setStart(mark);
				entries.addAll(search(cql));
			}
			return entries;
		} catch (InterruptedException | ExecutionException e) {
			throw new SearchContentException("Exception performing query", e.getCause(), cql.getCql());
		}
	}

	private List<SearchResultEntry> extractEntriesList(CqlSearchResult results) {
		return results.getResults().stream().filter(b -> {
			return b.getBean() instanceof ContentBean;
		}).collect(Collectors.toList());
	}

	private CqlSearchBean buildCql(String q) {
		CqlSearchBean cql = new CqlSearchBean();
		cql.setCql(q);
		cql.setLimit(limit);
		return cql;
	}

	@Override
	public String getHash() {
		return String.valueOf(query.getCQL().hashCode());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("query", query).build();
	}
}