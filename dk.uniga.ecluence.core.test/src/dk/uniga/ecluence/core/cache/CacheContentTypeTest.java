package dk.uniga.ecluence.core.cache;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

public class CacheContentTypeTest {

	@Test
	public void testGetId() throws Exception {
		assertEquals("context", CacheContentType.Context.getId());
		assertEquals("index", CacheContentType.Index.getId());
	}

	@Test
	public void testFromId() throws Exception {
		assertEquals(Optional.of(CacheContentType.Context), CacheContentType.fromId("context"));
	}

	@Test
	public void testFromUnknownId() throws Exception {
		assertEquals(Optional.empty(), CacheContentType.fromId("1"));
	}

}
