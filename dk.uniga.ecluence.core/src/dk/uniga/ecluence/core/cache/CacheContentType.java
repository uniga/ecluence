package dk.uniga.ecluence.core.cache;

import java.util.Arrays;
import java.util.Optional;

public enum CacheContentType {
	
	Index("index"), 
	
	Context("context");
	
	private String id;

	CacheContentType(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public static Optional<CacheContentType> fromId(String id) {
		return Arrays.stream(values()).filter((type) -> type.id.equals(id)).findFirst();
	}
}
