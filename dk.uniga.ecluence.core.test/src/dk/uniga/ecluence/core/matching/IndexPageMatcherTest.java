package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentProvider;

public class IndexPageMatcherTest {

	@Mock
	private Supplier<ContentProvider> contentProviderSupplier;

	@Mock
	private ContentProvider contentProvider;

	@Mock
	private RankExtractor rankExtractor;

	@Mock
	private ContentBean page1 = new ContentBean("1");

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullPointerException.class)
	public void testNullRankExtractorFails() throws Exception {
		new IndexPageMatcher(null, contentProviderSupplier, "label");
	}

	@Test(expected = NullPointerException.class)
	public void testNullContentProviderSupplierFails() throws Exception {
		new IndexPageMatcher(rankExtractor, null, "label");
	}

	@Test(expected = NullPointerException.class)
	public void testNullLabelFails() throws Exception {
		new IndexPageMatcher(rankExtractor, contentProviderSupplier, null);
	}

	@Test
	public void testNullContentProviderReturnsEmptyList() throws Exception {
		IndexPageMatcher matcher = new IndexPageMatcher(contentProviderSupplier, "label");
		when(contentProviderSupplier.get()).thenReturn(null);
		Collection<ContentMatch> matches = matcher.getMatches(new Object[0]);
		assertEquals(0, matches.size());
	}

	@Test
	public void testGetMatchesWithPageWithoutRankLabel() throws Exception {
		IndexPageMatcher matcher = getMatcher();
		when(rankExtractor.getRank(page1)).thenReturn(Optional.empty());
		Collection<ContentMatch> matches = matcher.getMatches(new Object[0]);
		assertsWithRank(matches, Rank.BASE);
	}
	
	@Test
	public void testGetMatchesWithPageWithRankLabel() throws Exception {
		IndexPageMatcher matcher = getMatcher();
		when(rankExtractor.getRank(page1)).thenReturn(Optional.of(new Rank(1001)));
		Collection<ContentMatch> matches = matcher.getMatches(new Object[0]);
		assertsWithRank(matches, new Rank(1001));
	}

	private IndexPageMatcher getMatcher() throws QueryException {
		IndexPageMatcher matcher = new IndexPageMatcher(rankExtractor, contentProviderSupplier, "label");
		when(contentProviderSupplier.get()).thenReturn(contentProvider);
		when(contentProvider.getPages()).thenReturn(Arrays.asList(page1));
		return matcher;
	}

	private void assertsWithRank(Collection<ContentMatch> matches, Rank expectedRank) {
		assertEquals(1, matches.size());
		ContentMatch match = matches.iterator().next();
		assertEquals(page1, match.getContent());
		assertEquals(expectedRank, match.getRank());
		assertTrue(match.getExplanation() instanceof IndexPageMatchExplanation);
		assertTrue(((IndexPageMatchExplanation) match.getExplanation()).getDefaultText().contains("label"));
	}

}
