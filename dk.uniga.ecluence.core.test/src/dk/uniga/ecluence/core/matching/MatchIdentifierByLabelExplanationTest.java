package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class MatchIdentifierByLabelExplanationTest {

	private SelectionDescription selection = new SelectionDescription() {
		@Override
		public String getDefaultText() {
			return "SELECTION";
		}
		@Override
		public String toString() {
			return "SELECTION";
		}
	};
	
	private List<String> matchingLabels = Arrays.asList("A", "B");
	
	@Test
	public void testGetSetFields() throws Exception {
		MatchIdentifierByLabelExplanation expl = new MatchIdentifierByLabelExplanation(selection, matchingLabels);
		assertEquals(selection, expl.getSelection());
		assertEquals(matchingLabels, expl.getMatchingLabels());
	}

	@Test
	public void testGetDefaultTextExplainsFirstLabelMatch() throws Exception {
		MatchIdentifierByLabelExplanation expl = new MatchIdentifierByLabelExplanation(selection, matchingLabels);
		assertEquals("Matches SELECTION by label 'A'", expl.getDefaultText());
	}

	@Test
	public void testToStringContainsClassnameAndFields() throws Exception {
		MatchIdentifierByLabelExplanation expl = new MatchIdentifierByLabelExplanation(selection, matchingLabels);
		String string = expl.toString();
		assertTrue(string.contains(MatchIdentifierByLabelExplanation.class.getSimpleName()));
		assertTrue(string.contains("SELECTION"));
		assertTrue(string.contains("A"));
		assertTrue(string.contains("B"));
	}

}
