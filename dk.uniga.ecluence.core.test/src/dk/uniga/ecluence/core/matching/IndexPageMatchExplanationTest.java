package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IndexPageMatchExplanationTest {

	@Test(expected = NullPointerException.class)
	public void testNullLabelFails() throws Exception {
		new IndexPageMatchExplanation(null);
	}

	@Test
	public void testGetDefaultTextContainsLabel() throws Exception {
		IndexPageMatchExplanation expl = new IndexPageMatchExplanation("label");
		assertTrue(expl.getDefaultText().contains("label"));
		assertTrue(expl.toString().contains(IndexPageMatchExplanation.class.getSimpleName()));
		assertTrue(expl.toString().contains("label"));
	}

}
