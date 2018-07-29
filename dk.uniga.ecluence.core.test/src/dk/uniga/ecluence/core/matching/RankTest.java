package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RankTest {

	@Test
	public void testGetLevel() throws Exception {
		assertEquals(1, new Rank(1).getLevel());
	}

	@Test
	public void testEquals() throws Exception {
		Rank r1 = new Rank(1);
		Rank r2 = new Rank(1);
		assertTrue(r1.equals(r2) && r2.equals(r1));
		assertTrue(r1.hashCode() == r2.hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testNotEquals() throws Exception {
		Rank r1 = new Rank(1);
		Rank r2 = new Rank(2);
		assertFalse(r1.equals(r2) || r2.equals(r1));
		assertTrue(r1.hashCode() != r2.hashCode());
		assertFalse(r1.equals(new Object[0]));
	}

	@Test
	public void testCompare() throws Exception {
		assertEquals(-1, new Rank(1).compareTo(new Rank(2)));
		assertEquals(0, new Rank(1).compareTo(new Rank(1)));
		assertEquals(1, new Rank(2).compareTo(new Rank(1)));
		// compareTo null ought to throw NPE
		assertEquals(1, new Rank(2).compareTo(null));
	}

	@Test
	public void testToStringContainsRankAndLevel() throws Exception {
		assertTrue(new Rank(1).toString().toLowerCase().contains("rank"));
		assertTrue(new Rank(1).toString().contains("1"));
	}

}
