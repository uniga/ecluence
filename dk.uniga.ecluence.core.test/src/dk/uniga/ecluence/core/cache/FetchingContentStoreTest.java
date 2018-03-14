package dk.uniga.ecluence.core.cache;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.ContentUpdateListener;
import dk.uniga.ecluence.core.QueryException;

public class FetchingContentStoreTest {

	@Mock
	ContentFetcher contentFetcher;

	@Mock
	ContentExpander contentExpander;

	@Mock
	ContentStore contentStore;

	@Mock
	Executor apiExecutor;

	@Mock
	ContentBean page1;

	@Mock
	ContentBean expandedPage1;

	@Mock
	Consumer<List<ContentBean>> consumer;

	@Captor
	ArgumentCaptor<List<ContentBean>> captor;

	@Mock
	ContentUpdateListener listener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullFetcher() throws Exception {
		new FetchingContentStore(null, contentExpander, contentStore, apiExecutor);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullExpander() throws Exception {
		new FetchingContentStore(contentFetcher, null, contentStore, apiExecutor);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullStore() throws Exception {
		new FetchingContentStore(contentFetcher, contentExpander, null, apiExecutor);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullExecutor() throws Exception {
		new FetchingContentStore(contentFetcher, contentExpander, contentStore, null);
	}

	private FetchingContentStore getFetchingStore() throws QueryException, ContentStoreException {
		return new FetchingContentStore(contentFetcher, contentExpander, contentStore, apiExecutor);
	}

	@Test
	public void testGetAll() throws Exception {
		when(contentStore.getAll()).thenReturn(Arrays.asList(page1));
		Collection<ContentBean> results = getFetchingStore().getAll();
		assertEquals(1, results.size());
		assertEquals(page1, results.iterator().next());
	}

	@Test
	public void testFetch() throws Exception {
		getFetchingStore().fetch();
		verify(apiExecutor, times(1)).execute(any(Runnable.class));
	}

	@Test
	public void testFetchForce() throws Exception {
		getFetchingStore().fetch(true);
		verify(contentFetcher).fetch(eq(true), any());
	}

	@Test
	public void testFetchNotForce() throws Exception {
		getFetchingStore().fetch(false);
		verify(contentFetcher).fetch(eq(false), any());
	}

	@Test
	public void testFetchWithArgument() throws Exception {
		int fetched = fetchOneExpandedPage(getFetchingStore(), true);
		assertEquals(1, fetched);
	}

	private int fetchOneExpandedPage(FetchingContentStore store, boolean arg) throws QueryException, ContentStoreException {
		doAnswer(fetchAnswer(Arrays.asList(page1))).when(contentFetcher).fetch(eq(arg), any());
		when(contentExpander.expand(page1)).thenReturn(expandedPage1);
		when(page1.getId()).thenReturn("1");
		return store.fetch(arg);
	}

	private Answer<Integer> fetchAnswer(List<ContentBean> result) {
		return new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				Consumer<List<ContentBean>> consumer = invocation.getArgument(1);
				consumer.accept(result);
				return result.size();
			}
		};
	}

	@Test(expected = NullPointerException.class)
	public void testAddNullListener() throws Exception {
		getFetchingStore().addListener(null);
	}

	@Test(expected = NullPointerException.class)
	public void testRemoveNullListener() throws Exception {
		getFetchingStore().removeListener(null);
	}

	@Test
	public void testAddListener() throws Exception {
		FetchingContentStore store = getFetchingStore();
		store.addListener(listener);
		// second call is silently ignored
		store.addListener(listener);
		
		fetchOneExpandedPage(store, false);
		
		// Expect one call to listener
		verify(listener, times(1)).contentUpdated(captor.capture());
		assertEquals(1, captor.getValue().size());
		assertEquals(expandedPage1, captor.getValue().iterator().next());
	}

	@Test
	public void testRemoveListener() throws Exception {
		FetchingContentStore store = getFetchingStore();
		store.addListener(listener);
		store.removeListener(listener);
		// second call is silently ignored
		store.removeListener(listener);
		
		fetchOneExpandedPage(store, false);
		
		// listener should not be called
		verify(listener, never()).contentUpdated(any());
	}

}
