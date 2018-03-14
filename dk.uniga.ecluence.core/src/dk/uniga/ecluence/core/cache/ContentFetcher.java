package dk.uniga.ecluence.core.cache;

import java.util.List;
import java.util.function.Consumer;

import de.itboehmer.confluence.rest.core.domain.cql.SearchResultEntry;
import dk.uniga.ecluence.core.QueryException;

/**
 * Fetches content using a ContentSearcher and provides the results to a
 * consumer. 
 * 
 * Keeps track of the last time a fetch was made and calls to
 * {@link #fetch(boolean, Consumer)} only allows so many fetching operations
 * within a given time interval.
 */
public interface ContentFetcher {

	/**
	 * Notifies this fetcher that all content should be fetched in the next call to
	 * {@link #fetch(boolean, Consumer)}. Also forces a fetch regardless of the
	 * force parameter to {@link #fetch(boolean, Consumer)}.
	 */
	void requireFullFetch();

	/**
	 * Fetches content pages resulting from a search. Callers are blocked until
	 * fetching has completed.
	 * 
	 * @param force
	 *            <code>true</code> forces fetching even though a fetch operation
	 *            was made within the allowed threshold.
	 * @param consumer
	 *            consumer to get accept the fetched content.
	 * @return number of pages fetched
	 * @throws QueryException
	 */
	int fetch(boolean force, Consumer<List<SearchResultEntry>> consumer) throws QueryException;

}