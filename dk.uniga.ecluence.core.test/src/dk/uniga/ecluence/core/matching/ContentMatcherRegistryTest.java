package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ContentMatcherRegistryTest {

	@Mock
	private ContentMatcherProvider provider1;
	
	@Mock
	private ContentMatcherProvider provider2;
	
	@Mock
	private ContentMatcher matcher1;
	
	@Mock
	private ContentMatcher matcher2;
	
	@Mock
	private ContentMatcher matcher3;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = NullPointerException.class)
	public void testAddNullProviderFails() throws Exception {
		new ContentMatcherRegistry().addProvider(null);
	}

	@Test()
	public void testGetNoneFromEmpty() throws Exception {
		ContentMatcherRegistry registry = new ContentMatcherRegistry();
		assertEquals(0, registry.getContentMatchers().size());
	}
	
	@Test()
	public void testGetThreeFromTwoAddedProvider() throws Exception {
		ContentMatcherRegistry registry = new ContentMatcherRegistry();
		registry.addProvider(provider1);
		registry.addProvider(provider2);
		Mockito.when(provider1.getContentMatchers()).thenReturn(Arrays.asList(matcher1));
		Mockito.when(provider2.getContentMatchers()).thenReturn(Arrays.asList(matcher2, matcher3));
		assertEquals(3, registry.getContentMatchers().size());
		assertTrue(registry.getContentMatchers().contains(matcher1));
		assertTrue(registry.getContentMatchers().contains(matcher2));
		assertTrue(registry.getContentMatchers().contains(matcher3));
	}
	
	@Test(expected = NullPointerException.class)
	public void testRemoveNullProviderFails() throws Exception {
		new ContentMatcherRegistry().removeProvider(null);
	}

	@Test()
	public void testRemoveSingleAddedProvider() throws Exception {
		ContentMatcherRegistry registry = new ContentMatcherRegistry();
		registry.addProvider(provider1);
		registry.removeProvider(provider1);
		Mockito.when(provider1.getContentMatchers()).thenReturn(Arrays.asList(matcher1));
		assertEquals(0, registry.getContentMatchers().size());
	}
}
