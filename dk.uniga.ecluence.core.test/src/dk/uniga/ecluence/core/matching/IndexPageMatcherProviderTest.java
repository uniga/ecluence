package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collection;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import dk.uniga.ecluence.core.cache.ContentProvider;

public class IndexPageMatcherProviderTest {

	@Mock
	private Supplier<ContentProvider> supplier;

	@Before
	public void setup() {
		initMocks(this);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullLabelFails() throws Exception {
		new IndexPageMatcherProvider(null);
	}

	@Test
	public void testCreateContentMatchersReturnsSingleton() throws Exception {
		Collection<ContentMatcher> matchers = new IndexPageMatcherProvider("label").createContentMatchers(supplier);
		assertEquals(1, matchers.size());
		assertTrue(matchers.iterator().next() instanceof IndexPageMatcher);
	}

}
