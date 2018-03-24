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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceBackedStateStore {

	/**
	 * Update the value for the given key, returning <code>true</code> if the value changed.
	 * 
	 * @param key key of state variable to update
	 * @param value the new state value
	 * @return <code>true</code> if the value has changed, <code>false</code> if it was unchanged.
	 */
	public synchronized boolean update(String key, String value) {
		IEclipsePreferences node = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_PREFERENCE_SCOPE);
		if (!node.get(key, "").equals(value)) {
			node.put(key, value);
			return true;
		}
		return false;
	}
}
