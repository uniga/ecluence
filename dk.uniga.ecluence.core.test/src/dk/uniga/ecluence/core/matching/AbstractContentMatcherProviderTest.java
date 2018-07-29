package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dk.uniga.ecluence.core.cache.ContentProvider;

public class AbstractContentMatcherProviderTest {
	
	@Mock
	private Supplier<ContentProvider> supplier;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	AbstractContentMatcherProvider build() {
		return new AbstractContentMatcherProvider() {
			@Override
			protected Collection<ContentMatcher> createContentMatchers(Supplier<ContentProvider> supplier) {
				supplier.get();
				return Arrays.asList();
			}
		};
	}

	@Test(expected = NullPointerException.class)
	public void testNullContentProviderSupplierFails() throws Exception {
		AbstractContentMatcherProvider p = build();
		p.setContentProviderSupplier(null);
	}
	
	@Test
	public void testGetContentMatchersCreatesContentMatchersOnce() throws Exception {
		AbstractContentMatcherProvider p = build();
		p.setContentProviderSupplier(supplier);
		Mockito.when(supplier.get()).thenReturn(null);
		assertEquals(0, p.getContentMatchers().size());
		assertEquals(0, p.getContentMatchers().size());
		Mockito.verify(supplier, Mockito.times(1)).get();
	}

	@Test(expected = IllegalStateException.class)
	public void testGetContentMatchersFromNullSupplierFails() throws Exception {
		build().getContentMatchers();
	}
}
