package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicate;
import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicateFactory;
import dk.uniga.ecluence.core.cache.ContentProvider;

public class MultipleIdentifierBasedContentMatcherTest {

	@Mock
	ContentProvider contentProvider;

	@Mock
	IdentifierPredicate predicate;

	@Mock
	MatchLabelsContentBeanPredicateFactory predicateFactory;

	@Mock
	MatchLabelsContentBeanPredicate matchPredicate;

	@Mock
	MultipleIdentifierProvider identifierProvider;

	ContentBean page1 = new ContentBean("1");
	ContentBean page2 = new ContentBean("2");
	List<ContentBean> pages = Arrays.asList(page1, page2);

	@Mock
	SelectionDescription selectionDescription;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSupplierFails() throws Exception {
		new MultipleIdentifierBasedContentMatcher(null, predicate, predicateFactory, identifierProvider);
	}

	@Test(expected = NullPointerException.class)
	public void testNullPredicateFails() throws Exception {
		new MultipleIdentifierBasedContentMatcher(() -> contentProvider, null, predicateFactory, identifierProvider);
	}

	@Test(expected = NullPointerException.class)
	public void testNullPredicateFactoryFails() throws Exception {
		new MultipleIdentifierBasedContentMatcher(() -> contentProvider, predicate, null, identifierProvider);
	}

	@Test(expected = NullPointerException.class)
	public void testNullIdentifierProviderFails() throws Exception {
		new MultipleIdentifierBasedContentMatcher(() -> contentProvider, predicate, predicateFactory, null);
	}
	
	@Test
	public void testGetNothingFromNullContentProvider() throws Exception {
		MultipleIdentifierBasedContentMatcher matcher = new MultipleIdentifierBasedContentMatcher(() -> null,
				predicate, predicateFactory, identifierProvider);
		Collection<ContentMatch> matches = matcher.getMatches(new Object[0]);
		assertEquals(0, matches.size());
	}
	
	@Test
	public void testProviderReturningEmptyIdentifierGivesNoMatches() throws Exception {
		MultipleIdentifierBasedContentMatcher matcher = new MultipleIdentifierBasedContentMatcher(() -> contentProvider,
				predicate, predicateFactory, identifierProvider);
		when(contentProvider.getPages()).thenReturn(pages);
		IdentifierProvider provider1 = mock(IdentifierProvider.class);
		Object[] o = new Object[0];
		when(provider1.getIdentifier(o)).thenReturn(Optional.empty());
		when(identifierProvider.getIdentifierProviders(o)).thenReturn(Arrays.asList(provider1));
		Collection<ContentMatch> matches = matcher.getMatches(o);
		assertEquals(0, matches.size());
	}

	@Test
	public void testProviderReturningOneIdentifierGivesMatchForOnePage() throws Exception {
		MultipleIdentifierBasedContentMatcher matcher = new MultipleIdentifierBasedContentMatcher(() -> contentProvider,
				predicate, predicateFactory, identifierProvider);
		when(contentProvider.getPages()).thenReturn(pages);
		Object[] o = new Object[0];
		IdentifierProvider provider1 = mockIdentifierProvider(o);
		when(identifierProvider.getIdentifierProviders(o)).thenReturn(Arrays.asList(provider1));
		configureMultiPredicate();
		Collection<ContentMatch> matches = matcher.getMatches(o);
		assertEquals(1, matches.size());
		ContentMatch match = matches.iterator().next();
		assertEquals(page1, match.getContent());
		assertEquals(Rank.BASE, match.getRank());
		assertEquals(new EmptyExplanation(), match.getExplanation());
	}

	@Test
	public void testTwoProvidersGiveOnlyOneMatchForPage() throws Exception {
		MultipleIdentifierBasedContentMatcher matcher = new MultipleIdentifierBasedContentMatcher(() -> contentProvider,
				predicate, predicateFactory, identifierProvider);
		when(contentProvider.getPages()).thenReturn(pages);
		Object[] o = new Object[0];
		IdentifierProvider provider1 = mockIdentifierProvider(o);
		IdentifierProvider provider2 = mockIdentifierProvider(o);
		when(identifierProvider.getIdentifierProviders(o)).thenReturn(Arrays.asList(provider1, provider2));
		configureMultiPredicate();
		Collection<ContentMatch> matches = matcher.getMatches(o);
		assertEquals(1, matches.size());
		ContentMatch match = matches.iterator().next();
		assertEquals(page1, match.getContent());
		assertEquals(Rank.BASE, match.getRank());
		assertEquals(new EmptyExplanation(), match.getExplanation());
	}

	private IdentifierProvider mockIdentifierProvider(Object[] o) {
		IdentifierProvider provider = mock(IdentifierProvider.class);
		when(provider.getIdentifier(o)).thenReturn(Optional.of("id"));
		when(provider.getSelectionDescription(o)).thenReturn(Optional.of(selectionDescription));
		return provider;
	}

	private void configureMultiPredicate() {
		MatchLabelsContentBeanPredicate multiPredicate = mock(MatchLabelsContentBeanPredicate.class);
		when(predicateFactory.getPredicate(eq(predicate))).thenReturn(multiPredicate);
		when(multiPredicate.test(eq(page1))).thenReturn(true);
		when(multiPredicate.test(eq(page2))).thenReturn(false);
		when(multiPredicate.getMatchExplanation(any(SelectionDescription.class), eq(page1))).thenReturn(new EmptyExplanation());
	}

	@Test
	public void testToString() throws Exception {
		MultipleIdentifierBasedContentMatcher matcher = new MultipleIdentifierBasedContentMatcher(() -> contentProvider,
				predicate, predicateFactory, identifierProvider);
		String string = matcher.toString();
		assertTrue(string.contains(MultipleIdentifierBasedContentMatcher.class.getSimpleName()));
	}

}
