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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.itboehmer.confluence.rest.core.domain.cql.SearchResultEntry;
import dk.uniga.ecluence.core.StoredTimestamp;

public class ContentFetcherImplTest {

	private static final int THRESHOLD = 5;

	// Do not fetch more than once per 5 minutes
	private final static Duration MINUTES_BETWEEN_FETCHING = Duration.ofMinutes(5);

	@Mock
	ContentSearcher contentSearcher;

	@Mock
	StoredTimestamp timestamp;
	
	@Mock 
	Consumer<List<SearchResultEntry>> consumer;

	@Mock SearchResultEntry page1;

	@Captor
	ArgumentCaptor<List<SearchResultEntry>> captor;
	
	@Captor
	ArgumentCaptor<LocalDateTime> timeCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = NullPointerException.class)
	public void testConstructorNullSearcher() throws Exception {
		new ContentFetcherImpl(null, timestamp, MINUTES_BETWEEN_FETCHING);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullTimestamp() throws Exception {
		new ContentFetcherImpl(contentSearcher, null, MINUTES_BETWEEN_FETCHING);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullStateStore() throws Exception {
		new ContentFetcherImpl(contentSearcher, timestamp, null);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullDuration() throws Exception {
		new ContentFetcherImpl(contentSearcher, timestamp, null);
	}

	private ContentFetcher getFetcher() {
		return new ContentFetcherImpl(contentSearcher, timestamp, MINUTES_BETWEEN_FETCHING);
	}
	
	@Test
	public void testFetchFirstTime() throws Exception {
		when(timestamp.get()).thenReturn(Optional.empty());
		getFetcher().fetch(false, consumer);
		verify(contentSearcher, times(1)).search();
	}
	
	@Test
	public void testRequireFullFetch() throws Exception {
		getFetcher().requireFullFetch();
		verify(timestamp).clear();
	}

	@Test
	public void testFetchForced() throws Exception {
		LocalDateTime time = LocalDateTime.now().minusMinutes(THRESHOLD-1);
		when(timestamp.get()).thenReturn(Optional.of(time));
		when(contentSearcher.searchSince(any())).thenReturn(Arrays.asList(page1));
		
		getFetcher().fetch(true, consumer);
		
		verify(timestamp, times(1)).set(any());
		verify(contentSearcher, times(1)).searchSince(time);
		verify(consumer).accept(captor.capture());
		assertEquals(1, captor.getValue().size());
		assertEquals(page1, captor.getValue().iterator().next());
	}

	@Test
	public void testFetchIsDue() throws Exception {
		LocalDateTime time = LocalDateTime.now().minusMinutes(THRESHOLD+1);
		when(timestamp.get()).thenReturn(Optional.of(time));
		when(contentSearcher.searchSince(any())).thenReturn(Arrays.asList(page1));
		
		LocalDateTime beforeCall = LocalDateTime.now().minusNanos(1);
		getFetcher().fetch(false, consumer);
		LocalDateTime afterCall = LocalDateTime.now().plusNanos(1);
		
		// check that time is set correctly
		verify(timestamp, times(1)).set(timeCaptor.capture());
		assertTrue(timeCaptor.getValue().isBefore(afterCall));
		assertTrue(timeCaptor.getValue().isAfter(beforeCall));
		
		// verify search since right time
		verify(contentSearcher, times(1)).searchSince(time);
		
		// right result?
		verify(consumer).accept(captor.capture());
		assertEquals(1, captor.getValue().size());
		assertEquals(page1, captor.getValue().iterator().next());
	}

	@Test
	public void testFetchIsNotDue() throws Exception {
		when(timestamp.get()).thenReturn(Optional.of(LocalDateTime.now().minusMinutes(THRESHOLD-1)));
		
		getFetcher().fetch(false, consumer);
		
		verify(contentSearcher, times(0)).searchSince(any());
		verify(timestamp, times(0)).set(any());
		verify(consumer, times(0)).accept(any());
	}

}
