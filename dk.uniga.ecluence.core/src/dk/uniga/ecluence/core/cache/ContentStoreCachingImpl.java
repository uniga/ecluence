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
package dk.uniga.ecluence.core.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Wrapper of a ContentStore that fetches all content from the given store once
 * and keeps it in memory. Assumes that the given ContentStore is only accessed
 * through this caching wrapper.
 */
public class ContentStoreCachingImpl implements ContentStore {

	private final ContentStore store;
	private final Map<String, ContentBean> map = new HashMap<>();
	private Boolean fetched = false;
	
	public ContentStoreCachingImpl(ContentStore store) {
		this.store = store;
	}
	
	private Collection<ContentBean> fetchAll() throws ContentStoreException {
		synchronized (fetched) {
			if (!fetched) {
				Collection<ContentBean> all = store.getAll();
				for (ContentBean page : all) {
					map.put(page.getId(), page);
				}
				fetched = true;
			}
			return map.values();
		}
	}
	
	@Override
	public void putContent(ContentBean content) throws ContentStoreException {
		fetchAll();
		map.put(content.getId(), content);
		store.putContent(content);
	}

	@Override
	public Collection<ContentBean> getAll() throws ContentStoreException {
		return fetchAll();
	}

	@Override
	public Optional<ContentBean> get(String id) throws ContentStoreException {
		fetchAll();
		return Optional.ofNullable(map.get(id));
	}

}
