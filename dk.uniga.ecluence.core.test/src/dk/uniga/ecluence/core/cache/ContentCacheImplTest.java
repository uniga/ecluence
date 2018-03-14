package dk.uniga.ecluence.core.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.ContentUpdateListener;
import dk.uniga.ecluence.core.QueryException;

public class ContentCacheImplTest {

	@Mock ContentQuery contentQuery;
	@Mock FetchingContentStore contentStore;
	@Mock ContentBean page1;
	DummyListener listener;

	@Captor
	ArgumentCaptor<Collection<ContentBean>> captor;

	@Captor
	ArgumentCaptor<ContentUpdateListener> listenerCaptor;
	
	class DummyListener implements ContentUpdateListener {
		@Override
		public void contentUpdated(Collection<ContentBean> pages) {
		}
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		listener = Mockito.spy(new DummyListener());
	}
	
	private ContentCacheImpl getCache() throws QueryException, ContentStoreException {
		return new ContentCacheImpl(CacheContentType.Context, "name", contentQuery, contentStore);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructNullType() throws Exception {
		new ContentCacheImpl(null, "name", contentQuery, contentStore);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructNullName() throws Exception {
		new ContentCacheImpl(CacheContentType.Context, null, contentQuery, contentStore);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructNullQuery() throws Exception {
		new ContentCacheImpl(CacheContentType.Context, "name", null, contentStore);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructNullStore() throws Exception {
		new ContentCacheImpl(CacheContentType.Context, "name", contentQuery, null);
	}

	@Test
	public void testConstruct() throws Exception {
		new ContentCacheImpl(CacheContentType.Context, "name", contentQuery, contentStore);
		// cache always starts by fetching and getting all from store
		verify(contentStore).fetch();
		verify(contentStore).getAll();
		verify(contentStore).addListener(any(ContentUpdateListener.class));
	}

	@Test
	public void testGetNull() throws Exception {
		when(page1.getId()).thenReturn("1");
		when(contentStore.getAll()).thenReturn(Arrays.asList(page1));
		when(contentQuery.matches(page1)).thenReturn(true);
		ContentCacheImpl cache = getCache();
		try {
			cache.get(null);
			fail("Expected exception");
		} catch (NullPointerException e) {
		}
		verify(contentStore, times(1)).fetch();
	}

	@Test
	public void testGetExisting() throws Exception {
		when(page1.getId()).thenReturn("1");
		when(contentStore.getAll()).thenReturn(Arrays.asList(page1));
		when(contentQuery.matches(page1)).thenReturn(true);
		ContentCacheImpl cache = getCache();
		assertEquals(page1, cache.get("1"));
		verify(contentStore, times(2)).fetch();
	}

	@Test
	public void testGetNonExisting() throws Exception {
		when(page1.getId()).thenReturn("2");
		when(contentStore.getAll()).thenReturn(Arrays.asList(page1));
		when(contentQuery.matches(page1)).thenReturn(true);
		ContentCacheImpl cache = getCache();
		assertNull(cache.get("1"));
	}

	@Test
	public void testGetAll() throws Exception {
		when(page1.getId()).thenReturn("1");
		when(contentStore.getAll()).thenReturn(Arrays.asList(page1));
		when(contentQuery.matches(page1)).thenReturn(true);
		ContentCacheImpl cache = getCache();
		Collection<ContentBean> result = cache.getAll();
		assertEquals(1, result.size());
		assertEquals(page1, result.iterator().next());
		verify(contentStore, times(2)).fetch();
	}

	@Test
	public void testRefresh() throws Exception {
		when(contentStore.getAll()).thenReturn(Arrays.asList());
		when(contentStore.fetch(true)).thenReturn(0);
		when(contentQuery.matches(page1)).thenReturn(true);
		
		ContentCacheImpl cache = getCache();
		verify(contentStore).addListener(listenerCaptor.capture());
		verify(contentStore).fetch();
		
		// Starts out empty
		assertEquals(0, cache.getAll().size());
		
		when(contentStore.fetch(true)).thenReturn(1);
		assertEquals(1, cache.refresh());
		verify(contentStore).fetch(true);
		
		// Content store notifies of returned contents
		listenerCaptor.getValue().contentUpdated(Arrays.asList(page1));
		
		assertEquals(1, cache.getAll().size());
		assertEquals(page1, cache.getAll().iterator().next());
	}

	@Test(expected = NullPointerException.class)
	public void testAddListenerNull() throws Exception {
		getCache().addListener(null);
	}

	@Test
	public void testListener() throws Exception {
		ContentCache cache = getCache();
		verify(contentStore).addListener(listenerCaptor.capture());
		cache.addListener(listener);
		// second time is just ignored
		cache.addListener(listener);
		
		// force notification
		when(contentStore.fetch(true)).thenReturn(1);
		assertEquals(1, cache.refresh());
		
		// Content store notifies of return contents
		listenerCaptor.getValue().contentUpdated(Arrays.asList(page1));
		
		verify(listener).contentUpdated(captor.capture());
		assertEquals(1, captor.getValue().size());
		assertTrue(captor.getValue().contains(page1));
	}
	
	@Test(expected = NullPointerException.class)
	public void testRemoveListenerNull() throws Exception {
		getCache().removeListener(null);
	}

	@Test
	public void testRemoveListenerTwice() throws Exception {
		ContentCache cache = getCache();
		cache.removeListener(listener);
		// second time is just ignored
		cache.removeListener(listener);
	}

	@Test
	public void testGetType() throws Exception {
		assertEquals(CacheContentType.Context, getCache().getType());
	}

	@Test
	public void testGetName() throws Exception {
		assertEquals("name", getCache().getName());
	}

}
