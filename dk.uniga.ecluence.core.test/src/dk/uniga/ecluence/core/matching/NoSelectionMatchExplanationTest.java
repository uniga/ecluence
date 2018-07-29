package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NoSelectionMatchExplanationTest {

	@Test
	public void testGetDefaultTextShowsNoMatch() throws Exception {
		assertEquals("No match", new NoSelectionMatchExplanation().getDefaultText());
	}

}
