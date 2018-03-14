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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicate;
import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicateFactory;
import dk.uniga.ecluence.core.cache.ContentProvider;

public class IdentifierLabelContentMatcherTest {

	@Mock
	ContentProvider contentProvider;

	@Mock
	IdentifierPredicate predicate;

	@Mock
	MatchLabelsContentBeanPredicateFactory predicateFactory;

	@Mock
	MatchLabelsContentBeanPredicate matchPredicate;

	@Mock
	IdentifierProvider identifierProvider;

	@Mock
	SelectionDescription selectionDescription;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullFacadeSupplier() throws Exception {
		new IdentifierLabelContentMatcher(null, predicate, predicateFactory, identifierProvider);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullPredicate() throws Exception {
		new IdentifierLabelContentMatcher(() -> contentProvider, null, predicateFactory, identifierProvider);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullPredicateFactory() throws Exception {
		new IdentifierLabelContentMatcher(() -> contentProvider, predicate, null, identifierProvider);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullProvider() throws Exception {
		new IdentifierLabelContentMatcher(() -> contentProvider, predicate, predicateFactory, null);
	}

	@Test
		public void testGetMatchesNull() throws Exception {
			IdentifierLabelContentMatcher handler = new IdentifierLabelContentMatcher(() -> contentProvider, predicate,
					predicateFactory, identifierProvider);
			// Handling null simply returns empty collection
			assertTrue(handler.getMatches(null).isEmpty());
		}

	@Test
		public void testGetMatchesUnknownType() throws Exception {
			IdentifierLabelContentMatcher handler = new IdentifierLabelContentMatcher(() -> contentProvider, predicate,
					predicateFactory, identifierProvider);
			Collection<ContentMatch> result = handler.getMatches("");
			assertTrue(result.isEmpty());
		}

	@Test
	public void testHandleNullFacade() throws Exception {
		when(identifierProvider.getIdentifier(any())).thenReturn(Optional.of("name"));
		when(identifierProvider.getSelectionDescription(any())).thenReturn(Optional.of(selectionDescription));
		IdentifierLabelContentMatcher handler = new IdentifierLabelContentMatcher(() -> null, predicate,
				predicateFactory, identifierProvider);
		Collection<ContentMatch> result = handler.getMatches("o");
		assertEquals(0, result.size());
	}

	@Test
		public void testGetMatchesObject() throws Exception {
			when(identifierProvider.getIdentifier(any())).thenReturn(Optional.of("name"));
			when(identifierProvider.getSelectionDescription(any())).thenReturn(Optional.of(selectionDescription));
			when(contentProvider.getPages()).thenReturn(getMockContentCollection());
			when(predicateFactory.getPredicate(eq(predicate))).thenReturn(matchPredicate);
			when(matchPredicate.test(any(ContentBean.class))).thenReturn(true);
	
			IdentifierLabelContentMatcher handler = new IdentifierLabelContentMatcher(() -> contentProvider, predicate,
					predicateFactory, identifierProvider);
			Collection<ContentMatch> result = handler.getMatches("o");
			assertEquals(2, result.size());
	
			verify(identifierProvider).getIdentifier(eq("o"));
			verify(identifierProvider).getSelectionDescription(eq("o"));
			verify(predicateFactory).getPredicate(predicate);
			verify(matchPredicate, times(2)).test(any(ContentBean.class));
		}

	private Collection<ContentBean> getMockContentCollection() {
		return Arrays.asList(new ContentBean("a"), new ContentBean("b"));
	}
}
