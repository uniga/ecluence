package dk.uniga.ecluence.core.matching;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

import dk.uniga.ecluence.core.cache.ContentProvider;

/**
 * Base class for ContentMatcherProviders that require a
 * Supplier<ContentProvider> set with
 * {@link #setContentProviderSupplier(Supplier)}.
 */
public abstract class AbstractContentMatcherProvider implements ContentMatcherProvider {

	private Supplier<ContentProvider> contentProviderSupplier;

	private Collection<ContentMatcher> contentMatchers;

	public void setContentProviderSupplier(Supplier<ContentProvider> contentProviderSupplier) {
		this.contentProviderSupplier = contentProviderSupplier;
	}

	@Override
	public Collection<ContentMatcher> getContentMatchers() {
		Objects.requireNonNull(contentProviderSupplier);
		if (contentMatchers == null)
			contentMatchers = createContentMatchers(contentProviderSupplier);
		return contentMatchers;
	}

	protected abstract Collection<ContentMatcher> createContentMatchers(
			Supplier<ContentProvider> contentProviderSupplier);

}
