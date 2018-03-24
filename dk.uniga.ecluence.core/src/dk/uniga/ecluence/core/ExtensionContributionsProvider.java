/*******************************************************************************
 * Copyright (c) 2017, 2018 Uniga.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mikkel R. Jakobsen - initial API and implementation
 *******************************************************************************/
package dk.uniga.ecluence.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.cache.CacheContentType;
import dk.uniga.ecluence.core.cache.ContentCacheProvider;
import dk.uniga.ecluence.core.cache.ContentProvider;
import dk.uniga.ecluence.core.cache.ContentProviderImpl;
import dk.uniga.ecluence.core.cache.ContentQueryImpl;
import dk.uniga.ecluence.core.cache.LabelledContentCacheProvider;
import dk.uniga.ecluence.core.matching.AbstractContentMatcherProvider;
import dk.uniga.ecluence.core.matching.ContentMatcherProvider;

public final class ExtensionContributionsProvider {

	private static final Logger log = LoggerFactory.getLogger(ExtensionContributionsProvider.class);
	
	private final Supplier<ConfluenceFacade> confluenceFacadeSupplier;
	private Map<String, ContentCacheProvider> contentCacheProviders = new HashMap<>();
	private Collection<ContentMatcherProvider> contentMatcherProviders = new HashSet<>();
	
	public ExtensionContributionsProvider(Supplier<ConfluenceFacade> confluenceFacade) {
		this.confluenceFacadeSupplier = confluenceFacade;
	}

	public void execute(IExtensionRegistry registry) {
        IConfigurationElement[] contributions = registry.getConfigurationElementsFor("dk.uniga.ecluence.core.contentContribution");
        for (IConfigurationElement contribution : contributions) {
			log.debug("contribution {}", contribution.getName());
			IConfigurationElement[] children = contribution.getChildren();
			buildContentCacheProviders(children);
			buildContentMatcherProviders(children);
		}
	}

	public Collection<ContentCacheProvider> getContentCacheProviders() {
		return contentCacheProviders.values();
	}
	
	public Collection<ContentMatcherProvider> getContentMatcherProviders() {
		return contentMatcherProviders;
	}
	
	private void buildContentCacheProviders(IConfigurationElement[] children) {
		for (IConfigurationElement elem : children) {
			log.debug("{}: {}", elem.getContributor(), elem.getName());
			if ("labelledContentCacheProvider".equals(elem.getName())) {
				ContentCacheProvider provider = readLabelledContentCacheProvider(elem);
				contentCacheProviders.put(provider.getName(), provider);
			}
		}
	}

	private void buildContentMatcherProviders(IConfigurationElement[] children) {
		for (IConfigurationElement elem : children) {
			log.debug("{}: {}", elem.getContributor(), elem.getName());
			if ("contentMatcherProvider".equals(elem.getName()))
				try {
					contentMatcherProviders.add(readContentMatcherProvider(elem));
				} catch (CoreException e) {
					e.printStackTrace();
				}
		}
	}
	
	private ContentCacheProvider readLabelledContentCacheProvider(IConfigurationElement elem) {
		String id = elem.getAttribute("id");
		String type = elem.getAttribute("type");
		String labels = elem.getAttribute("labels");
		log.debug("  labelled provider {} {} {}", id, type, labels);
		List<String> labelsList = Arrays.asList(StringUtils.split(labels, ","));
		CacheContentType cacheType = CacheContentType.fromId(type).get(); // throws exception if invalid type
		return new LabelledContentCacheProvider(id, new ContentQueryImpl(labelsList), cacheType);
	}
	
	private ContentMatcherProvider readContentMatcherProvider(IConfigurationElement elem) throws CoreException {
		String className = elem.getAttribute("className");
		String cacheProviderId = elem.getAttribute("contentCacheProviderId");
		log.debug("  content matcher provider {} {}", 
				className, 
				cacheProviderId);
		ContentMatcherProvider provider = (ContentMatcherProvider) elem.createExecutableExtension("className");
		if (provider instanceof AbstractContentMatcherProvider) {
			ContentProvider contentProvider = new ContentProviderImpl(confluenceFacadeSupplier, cacheProviderId);
			((AbstractContentMatcherProvider) provider).setContentProviderSupplier(() -> contentProvider);
		}
		return provider;
	}
}
