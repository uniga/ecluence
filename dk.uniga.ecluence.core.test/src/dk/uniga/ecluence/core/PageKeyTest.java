package dk.uniga.ecluence.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.HistoryBean;

public class PageKeyTest {

	@Test(expected = NullPointerException.class)
	public void testCreateKeyNullPageFails() throws Exception {
		PageKey.createKey(null);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateKeyNullPageIdFails() throws Exception {
		ContentBean page = Mockito.mock(ContentBean.class);
		Mockito.when(page.getId()).thenReturn(null);
		PageKey.createKey(page);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateKeyNullPageHistoryFails() throws Exception {
		ContentBean page = Mockito.mock(ContentBean.class);
		Mockito.when(page.getId()).thenReturn("");
		Mockito.when(page.getHistory()).thenReturn(null);
		PageKey.createKey(page);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateKeyNullPageHistoryDateFails() throws Exception {
		ContentBean page = Mockito.mock(ContentBean.class);
		Mockito.when(page.getId()).thenReturn("");
		HistoryBean history = Mockito.mock(HistoryBean.class);
		Mockito.when(page.getHistory()).thenReturn(history);
		Mockito.when(history.getCreatedDate()).thenReturn(null);
		PageKey.createKey(page);
	}

	@Test
	public void testEquals() throws Exception {
		Date date = new Date();
		PageKey key1 = PageKey.createKey(createPage("1", date));
		PageKey key2 = PageKey.createKey(createPage("1", date));
		assertTrue(key1.equals(key2));
		int hashCode = key1.hashCode();
		assertEquals(hashCode, key2.hashCode());
		assertEquals(hashCode, key1.hashCode()); // must remain unchanged
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testNotEqualsDifferentType() throws Exception {
		PageKey key1 = PageKey.createKey(createPage("1", new Date()));
		assertFalse(key1.equals(new String()));
	}
	
	@Test
	public void testNotEqualsDifferentKeys() throws Exception {
		Date date = new Date();
		PageKey key1 = PageKey.createKey(createPage("1", date));
		PageKey key2 = PageKey.createKey(createPage("2", date));
		assertFalse(key1.equals(key2));
		assertNotEquals(key1.hashCode(), key2.hashCode());
	}
	
	@Test
	public void testNotEqualsDifferentDates() throws Exception {
		PageKey key1 = PageKey.createKey(createPage("1", new Date(1)));
		PageKey key2 = PageKey.createKey(createPage("1", new Date()));
		assertFalse(key1.equals(key2));
		assertNotEquals(key1.hashCode(), key2.hashCode());
	}

	@Test
	public void testToStringContainsKeyAndDate() throws Exception {
		Date date = new Date();
		PageKey key = PageKey.createKey(createPage("1234", date));
		assertTrue(key.toString().contains("1234"));
		assertTrue(key.toString().contains(date.toString()));
	}
	
	private ContentBean createPage(String id, Date date) {
		ContentBean page = Mockito.mock(ContentBean.class);
		Mockito.when(page.getId()).thenReturn(id);
		HistoryBean history = Mockito.mock(HistoryBean.class);
		Mockito.when(page.getHistory()).thenReturn(history);
		Mockito.when(history.getCreatedDate()).thenReturn(date);
		return page;
	}

}
