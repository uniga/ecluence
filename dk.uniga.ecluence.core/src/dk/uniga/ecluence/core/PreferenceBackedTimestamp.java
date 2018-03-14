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
package dk.uniga.ecluence.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferenceBackedTimestamp implements StoredTimestamp {

	private static final Logger log = LoggerFactory.getLogger(PreferenceBackedTimestamp.class);

	private static final String LAST_FETCH = "lastFetch";

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private final IEclipsePreferences rootNode;
	private final String nodeName;

	public PreferenceBackedTimestamp(IEclipsePreferences rootNode, String nodeName) {
		this.rootNode = rootNode;
		this.nodeName = nodeName;
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.StoredTimestamp#get()
	 */
	@Override
	public Optional<LocalDateTime> get() {
		Preferences node = rootNode.node(nodeName);
		String last = node.get(LAST_FETCH, null);
		if (last == null)
			return Optional.empty();
		return Optional.of(LocalDateTime.from(timeFormatter.parse(last)));
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.StoredTimestamp#set(java.time.LocalDateTime)
	 */
	@Override
	public void set(LocalDateTime time) {
		log.debug("Set to {}", time);
		Preferences node = rootNode.node(nodeName);
		node.put(LAST_FETCH, time == null ? null : timeFormatter.format(time));
		try {
			node.flush();
		} catch (BackingStoreException e) {
			log.error("Problem storing last time of fetch in plug-in store.", e);
		}
	}

	@Override
	public void clear() {
		set(null);
	}

}
