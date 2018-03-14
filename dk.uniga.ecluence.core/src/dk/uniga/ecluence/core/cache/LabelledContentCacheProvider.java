package dk.uniga.ecluence.core.cache;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import dk.uniga.ecluence.core.QueryException;

public final class LabelledContentCacheProvider implements ContentCacheProvider {

	private final CacheContentType cacheType;
	private final String cacheName;
	private final ContentQuery contentQuery;

	public LabelledContentCacheProvider(String cacheName, ContentQuery contentQuery, CacheContentType cacheType) {
		this.cacheName = cacheName;
		this.contentQuery = contentQuery;
		this.cacheType = cacheType;
	}
	
	@Override
	public CacheContentType getCacheContentType() {
		return cacheType;
	}

	@Override
	public String getName() {
		return cacheName;
	}
	
	@Override
	public ContentCache getCache(ContentCacheFactory cacheFactory) throws QueryException, ContentStoreException {
		return cacheFactory.createCache(cacheType, cacheName, contentQuery);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("cacheName", cacheName)
				.append("cacheType", cacheType).build();
	}
}
