package dk.uniga.ecluence.core;

import java.util.Objects;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

public final class PageKey {
	
	private final String key;

	public static PageKey createKey(ContentBean page) {
		Objects.requireNonNull(page);
		Objects.requireNonNull(page.getId(), "page must have a non null id");
		Objects.requireNonNull(page.getHistory(), "page must have a non null history");
		Objects.requireNonNull(page.getHistory().getCreatedDate(), "page must have a non null history with a non null creation date");
		return new PageKey(String.format("%s : %s", page.getId(), page.getHistory().getCreatedDate().toString()));
	}
	
	PageKey(String key) {
		this.key = key;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PageKey) {
			return ((PageKey) obj).key.equals(key);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(key);
	}
	
	@Override
	public String toString() {
		return "PageKey[" + key + "]";
	}
}