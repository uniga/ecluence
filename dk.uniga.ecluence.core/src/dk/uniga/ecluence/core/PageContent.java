package dk.uniga.ecluence.core;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

public class PageContent {

	private final ContentBean page;
	private final PageKey key;
	private String renderedContent;

	private final Set<Listener> listeners = new HashSet<>();
	
	public PageContent(ContentBean page) {
		this.page = Objects.requireNonNull(page);
		this.key = new PageKey(page);
	}
	
	public PageKey getKey() {
		return key;
	}
	
	public String getContent() {
		return renderedContent;
	}
	
	public void setContent(String modified) {
		this.renderedContent = Objects.requireNonNull(modified);
		listeners.forEach((l) -> l.contentChanged(this));
	}

	public ContentBean getPage() {
		return page;
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void close() {
		listeners.forEach((l) -> l.closed(this));
	}
	
	public static PageKey createKey(ContentBean page) {
		return new PageKey(page);
	}
	
	public static class PageKey {
		
		private final String key;
		
		private PageKey(ContentBean page) {
			key = String.format("%s : %s", page.getId(), page.getHistory().getCreatedDate().toString());
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
	
	public interface Listener {

		void contentChanged(PageContent content);
		
		void closed(PageContent content);
	}

}