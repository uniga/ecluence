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

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Expands fields of a {@link ContentBean}.
 */
public interface ContentExpander {

	/**
	 * Expands fields of the given {@link ContentBean}.
	 * 
	 * @param bean
	 *            the bean to expand
	 * @return the bean with fields expanded
	 * @throws ExpandContentException
	 *             if expansion query could not be completed
	 */
	ContentBean expand(ContentBean bean) throws ExpandingContentException;

}