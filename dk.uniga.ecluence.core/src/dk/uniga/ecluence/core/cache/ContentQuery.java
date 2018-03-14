/*******************************************************************************
 * Copyright (c) 2017 Uniga.
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

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Matches ContentBeans that are of type page and has a label among a given set
 * of labels.
 */
public interface ContentQuery {

	/**
	 * Returns a CQL expression for matching pages.
	 * 
	 * @return String with CQL expression
	 */
	String getCQL();

	/**
	 * Returns the labels that a page should have to match this query.
	 * 
	 * @return
	 */
	Collection<String> getLabels();

	/**
	 * Returns whether the given {@link ContentBean} matches this query.
	 * 
	 * @param content
	 * @return <code>true</code> if the given page matches this query
	 */
	boolean matches(ContentBean pages);

}