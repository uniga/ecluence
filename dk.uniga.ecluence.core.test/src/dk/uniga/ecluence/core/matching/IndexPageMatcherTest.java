package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dk.uniga.ecluence.core.cache.ContentProvider;

public class IndexPageMatcherTest {

	@Mock
	private Supplier<ContentProvider> contentProviderSupplier;
	
	@Mock
	private ContentProvider contentProvider;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullContentProviderSupplierFails() throws Exception {
		new IndexPageMatcher(null, "label");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullLabelFails() throws Exception {
		new IndexPageMatcher(contentProviderSupplier, null);
	}
	
	@Test
	public void testGetMatches() throws Exception {
		IndexPageMatcher matcher = new IndexPageMatcher(contentProviderSupplier, "label");
		when(contentProviderSupplier.get()).thenReturn(contentProvider);
		when(contentProvider.getPages()).thenReturn(Arrays.asList());
		Collection<ContentMatch> matches = matcher.getMatches(new Object[0]);
		assertEquals(0, matches.size());
	}

}
