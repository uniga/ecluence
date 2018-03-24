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
import java.util.Collections;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.QueryException;

public class ContentProviderImpl implements ContentProvider {

	private Supplier<ConfluenceFacade> facadeSupplier;
	private String cacheName;

	public ContentProviderImpl(Supplier<ConfluenceFacade> facadeSupplier, String cacheName) {
		this.facadeSupplier = facadeSupplier;
		this.cacheName = cacheName;
	}
	
	@Override
	public Collection<ContentBean> getPages() throws QueryException {
		ConfluenceFacade facade = facadeSupplier.get();
		if (facade != null)
			return facade.getPages(cacheName);
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append(cacheName)
				.build();
	}
}
