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

import java.util.Collection;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Listener to be notified of updated content.
 */
@FunctionalInterface
public interface ContentUpdateListener {

	/**
	 * Notifies this listener that a given collection of pages have been updated.
	 * 
	 * @param pages collection of updated pages 
	 */
	void contentUpdated(Collection<ContentBean> pages);
}
