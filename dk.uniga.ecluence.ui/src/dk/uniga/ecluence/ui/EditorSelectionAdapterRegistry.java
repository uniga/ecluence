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
package dk.uniga.ecluence.ui;

import java.util.HashSet;
import java.util.Set;

import dk.uniga.ecluence.ui.parts.EditorSelectionAdapter;

public final class EditorSelectionAdapterRegistry {

	private final Set<EditorSelectionAdapter> adapters = new HashSet<>();
	private final Set<EditorSelectionAdapterRegistryListener> listeners = new HashSet<>();
	
	public void addAdapter(EditorSelectionAdapter adapter) {
		if (adapters.add(adapter))
			notifyAdded(adapter);
	}
	
	public void removeAdapter(EditorSelectionAdapter adapter) {
		adapters.remove(adapter);
	}
	
	public Set<EditorSelectionAdapter> getAdapters() {
		return adapters;
	}
	
	public void addListener(EditorSelectionAdapterRegistryListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(EditorSelectionAdapterRegistryListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyAdded(EditorSelectionAdapter adapter) {
		listeners.forEach(listener -> listener.editorSelectionAdapterAdded(adapter));
	}
}
