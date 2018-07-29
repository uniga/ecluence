package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EmptyExplanationTest {

	@Test
	public void testGetDefaultTextIsEmpty() throws Exception {
		assertEquals("", new EmptyExplanation().getDefaultText());
	}

}
