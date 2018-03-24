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
import java.util.Optional;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Container in which to store and retrieve {@link ContentBean}s.
 */
public interface ContentStore {

	/**
	 * Stores the given {@link ContentBean}.
	 * 
	 * @param content bean to put in this store
	 * @throws ContentStoreException
	 */
	void putContent(ContentBean content) throws ContentStoreException;

	/**
	 * Returns all {@link ContentBean} found in this store.
	 * 
	 * @return
	 * @throws ContentStoreException
	 */
	Collection<ContentBean> getAll() throws ContentStoreException;

	/**
	 * Returns the ContentBean with the given id, if it exists.
	 * 
	 * @param id the id of the page to retrieve
	 * @return Page if found, otherwise {@link Optional#empty()}
	 * @throws ContentStoreException
	 */
	Optional<ContentBean> get(String id) throws ContentStoreException;

}
