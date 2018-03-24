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

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.itboehmer.confluence.rest.client.ContentClient;
import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Implementation of a ContentExpander that uses a ContentClient from the
 * Confluence REST library and is configurable with a list of fields to expand.
 */
public class ContentExpanderImpl implements ContentExpander {
	
	private final ContentClient contentClient;
	private final List<String> expandFields;
	
	public ContentExpanderImpl(ContentClient contentClient, List<String> expand) {
		this.contentClient = requireNonNull(contentClient);
		this.expandFields = requireNonNull(expand);
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.ContentExpander#expand(de.itboehmer.confluence.rest.core.domain.content.ContentBean)
	 */
	@Override
	public ContentBean expand(ContentBean bean) throws ExpandingContentException {
		try {
			return contentClient.getContentById(bean.getId(), 0, expandFields).get();
		} catch (InterruptedException e) {
			throw new ExpandingContentException("Exception expanding content bean", e.getCause(), bean.getId());
		} catch (ExecutionException e) {
			throw new ExpandingContentException("Exception expanding content bean", e.getCause(), bean.getId());
		}
	}

}
