package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

public class ContentMatchComparatorTest {

	@Test
	public void testCompareDifferentRank() throws Exception {
		ContentMatch m1 = new ContentMatch(new ContentBean("1"), new EmptyExplanation(), new Rank(0));
		ContentMatch m2 = new ContentMatch(new ContentBean("2"), new EmptyExplanation(), new Rank(1));
		assertEquals(-1, new ContentMatchComparator().compare(m1, m2));
	}

	@Test
	public void testCompareEqualRankDifferentContent() throws Exception {
		ContentMatch m1 = new ContentMatch(new ContentBean("1"), new EmptyExplanation(), new Rank(0));
		ContentMatch m2 = new ContentMatch(new ContentBean("2"), new EmptyExplanation(), new Rank(0));
		assertEquals(-1, new ContentMatchComparator().compare(m1, m2));
	}

	@Test
	public void testCompareEqualRankSameContent() throws Exception {
		ContentBean c1 = new ContentBean("1");
		c1.setTitle("t");
		ContentBean c2 = new ContentBean("1");
		c2.setTitle("t");
		ContentMatch m1 = new ContentMatch(c1, new EmptyExplanation(), new Rank(0));
		ContentMatch m2 = new ContentMatch(c2, new EmptyExplanation(), new Rank(0));
		assertEquals(0, new ContentMatchComparator().compare(m1, m2));
	}
}
