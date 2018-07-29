package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

public class ContentMatchTest {

	private ContentBean content = new ContentBean("1");
	private MatchExplanation explanation = new EmptyExplanation();
	private Rank rank = new Rank(0);

	@Before
	public void setup() {
		content.setTitle("title");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullContentBeanFails() throws Exception {
		new ContentMatch(null, explanation, rank);
	}

	@Test(expected = NullPointerException.class)
	public void testTwoArgNullContentBeanFails() throws Exception {
		new ContentMatch(null, explanation);
	}

	@Test(expected = NullPointerException.class)
	public void testNullMatchExplanationFails() throws Exception {
		new ContentMatch(content, null, rank);
	}

	@Test(expected = NullPointerException.class)
	public void testTwoArgNullMatchExplanationFails() throws Exception {
		new ContentMatch(content, null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullRankFails() throws Exception {
		new ContentMatch(content, explanation, null);
	}

	@Test
	public void testGetContent() throws Exception {
		assertEquals(new ContentMatch(content, explanation).getContent(), content);
	}

	@Test
	public void testGetExplanation() throws Exception {
		assertEquals(new ContentMatch(content, explanation).getExplanation(), explanation);
	}

	@Test
	public void testGetRank() throws Exception {
		assertEquals(new ContentMatch(content, explanation, rank).getRank(), rank);
	}

	@Test
	public void testEquals() throws Exception {
		ContentMatch m1 = new ContentMatch(content, explanation, rank);
		ContentMatch m2 = new ContentMatch(content, explanation, rank);
		assertTrue(m1.equals(m2) && m2.equals(m1));
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testNotEquals() throws Exception {
		ContentMatch m1 = new ContentMatch(content, explanation, rank);
		ContentMatch m2 = new ContentMatch(new ContentBean("2"), explanation, rank);
		ContentMatch m3 = new ContentMatch(content, new NoSelectionMatchExplanation(), rank);
		ContentMatch m4 = new ContentMatch(content, explanation, new Rank(1));
		assertFalse(m1.equals(""));
		assertFalse(m1.equals(m2) || m2.equals(m1));
		assertFalse(m1.equals(m3) || m3.equals(m1));
		assertFalse(m1.equals(m4) || m4.equals(m1));
	}
	
	@Test
	public void testHashcode() throws Exception {
		ContentMatch m1 = new ContentMatch(content, explanation, rank);
		ContentMatch m2 = new ContentMatch(content, explanation, rank);
		assertEquals(m1.hashCode(), m2.hashCode());
	}

	@Test
	public void testToStringIncludesNameAndAllFields() throws Exception {
		ContentMatch m = new ContentMatch(content, explanation, rank);
		assertTrue(m.toString().contains(ContentMatch.class.getSimpleName()));
		assertTrue(m.toString().contains(content.getTitle()));
		assertTrue(m.toString().contains(explanation.toString()));
		assertTrue(m.toString().contains(rank.toString()));
	}
}
