package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.BiPredicate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class IdentifierComparisonPredicateTest {

	@Mock
	private BiPredicate<String, String> delegate;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullDelegateFails() throws Exception {
		new IdentifierComparisonPredicate(null, "");
	}

	@Test(expected = NullPointerException.class)
	public void testNullPrefixFails() throws Exception {
		new IdentifierComparisonPredicate(delegate, null);
	}
	
	@Test
	public void testFalseIfIdentifierNotSet() throws Exception {
		assertFalse(new IdentifierComparisonPredicate(delegate, "a").test("a"));
	}
	
	@Test
	public void testFalseIfIdentifierSetNull() throws Exception {
		assertFalse(new IdentifierComparisonPredicate(delegate, "a").setIdentifier(null).test("a"));
	}
	
	@Test
	public void testFalseIfNotMatchingPrefix() throws Exception {
		assertFalse(new IdentifierComparisonPredicate(delegate, "a").setIdentifier("1").test("b1"));
	}
	
	@Test
	public void testCallDelegateOnEmptyPrefix() throws Exception {
		when(delegate.test(anyString(), anyString())).thenReturn(false);
		assertFalse(new IdentifierComparisonPredicate(delegate, "").setIdentifier("1").test("1"));
		verify(delegate).test(eq("1"), eq("1"));
	}
	
	@Test
	public void testCallDelegateOnMatchingPrefix() throws Exception {
		when(delegate.test(anyString(), anyString())).thenReturn(false);
		assertFalse(new IdentifierComparisonPredicate(delegate, "a").setIdentifier("1").test("a1"));
		verify(delegate).test(eq("1"), eq("1"));
	}
	
	@Test
	public void testCallDelegateWithEmptyIdentifier() throws Exception {
		when(delegate.test(anyString(), anyString())).thenReturn(false);
		assertFalse(new IdentifierComparisonPredicate(delegate, "a").setIdentifier("").test("a1"));
		verify(delegate).test(eq(""), eq("1"));
	}
	
	@Test
	public void testCallDelegateWithReplacedNonWordCharacters() throws Exception {
		when(delegate.test(anyString(), anyString())).thenReturn(false);
		assertFalse(new IdentifierComparisonPredicate(delegate, "a").setIdentifier("1./2").test("a1"));
		verify(delegate).test(eq("1-2"), eq("1"));
	}
	
	@Test
	public void testNonWordCharactersInPrefixAreNotReplaced() throws Exception {
		when(delegate.test(anyString(), anyString())).thenReturn(false);
		assertFalse(new IdentifierComparisonPredicate(delegate, "a./b").setIdentifier("1").test("a./b1"));
		verify(delegate).test(eq("1"), eq("1"));
	}

	@Test
	public void testToStringContainsClassnameAndAllArguments() throws Exception {
		IdentifierPredicate p = new IdentifierComparisonPredicate(delegate, "a").setIdentifier("b");
		assertTrue(p.toString().contains(IdentifierComparisonPredicate.class.getSimpleName()));
		assertTrue(p.toString().contains(delegate.toString()));
		assertTrue(p.toString().contains("a"));
		assertTrue(p.toString().contains("b"));
	}
}
