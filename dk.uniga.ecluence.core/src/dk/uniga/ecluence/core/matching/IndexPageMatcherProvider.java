package dk.uniga.ecluence.core.matching;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import dk.uniga.ecluence.core.cache.ContentProvider;

public final class IndexPageMatcherProvider extends AbstractContentMatcherProvider {

	private final String label;

	public IndexPageMatcherProvider(String label) {
		this.label = label;
	}

	@Override
	protected Collection<ContentMatcher> createContentMatchers(Supplier<ContentProvider> contentProviderSupplier) {
		return Collections.singleton(new IndexPageMatcher(contentProviderSupplier, label));
	}

}
