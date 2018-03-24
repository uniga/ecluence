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
package dk.uniga.ecluence.ui.parts;

import java.util.Collection;
import java.util.function.Supplier;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.RestException;
import dk.uniga.ecluence.core.Activator;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.matching.ContentMatch;
import dk.uniga.ecluence.core.matching.ContentMatcher;
import dk.uniga.ecluence.core.matching.ContentMatcherProvider;
import dk.uniga.ecluence.core.matching.SelectionSource;
import dk.uniga.ecluence.core.preferences.PreferenceConstants;
import dk.uniga.ecluence.ui.parts.SelectionMatcher.ContentMatchListener;

/**
 * Links selections in Eclipse workspace views and editors that are provided by
 * {@link SelectionAdapter}s, to content through {@link ContentMatcher}s.
 */
public final class EclipseSelectionContentMatcher {

	static final Logger log = LoggerFactory.getLogger(EclipseSelectionContentMatcher.class);

	private final ContentMatcherProvider contentMatcherProvider;

	private final ContentMatchListener matchListener;

	private final Supplier<IPreferenceStore> preferenceStoreSupplier;

	private IPropertyChangeListener preferenceListener;

	private SelectionMatcher selectionMatcher;

	private WorkspaceSelectionAdapter workspaceSelectionAdapter;

	private LinkedEditorSelectionAdapter editorSelectionAdapter;

	private boolean linkSelection;

	public EclipseSelectionContentMatcher(ContentMatcherProvider contentMatcherProvider,
			ContentMatchListener matchListener, Supplier<IPreferenceStore> preferenceStoreSupplier) {
		this.contentMatcherProvider = contentMatcherProvider;
		this.matchListener = matchListener;
		this.preferenceStoreSupplier = preferenceStoreSupplier;
		initialize();
		registerLinkSelectionListener();
	}

	public void close() {
		editorSelectionAdapter.disable();
		workspaceSelectionAdapter = null;
		preferenceStoreSupplier.get().removePropertyChangeListener(preferenceListener);
	}

	private void registerLinkSelectionListener() {
		preferenceListener = new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (PreferenceConstants.PREFERENCE_LINK_SELECTION.equals(event.getProperty())) {
					log.debug("Link selection changed to {}", event.getNewValue());
					linkSelection = (boolean) event.getNewValue();
				}
			}
		};
		preferenceStoreSupplier.get().addPropertyChangeListener(preferenceListener);
		linkSelection = preferenceStoreSupplier.get().getBoolean(PreferenceConstants.PREFERENCE_LINK_SELECTION);
		log.debug("Link selection: " + linkSelection);
	}

	private void initialize() {
		selectionMatcher = createSelectionMatcher();

		workspaceSelectionAdapter = new WorkspaceSelectionAdapter() {
			@Override
			void handleException(QueryException e) {
				if (e.getCause() instanceof RestException)
					Activator.getDefault().restException(e.getCause().getMessage(), (RestException) e.getCause());
			}
		};
		workspaceSelectionAdapter.setListener(selectionMatcher);

		editorSelectionAdapter = new LinkedEditorSelectionAdapter(selectionMatcher);
	}

	private SelectionMatcher createSelectionMatcher() {
		SelectionMatcher matcher = new SelectionMatcher() {
			@Override
			public void selectionChanged(SelectionSource source, Object selection) {
				if (linkSelection)
					super.selectionChanged(source, selection);
			}
		};
		matcher.addHandlers(contentMatcherProvider.getContentMatchers().toArray(new ContentMatcher[0]));
		matcher.addMatchListener(matchListener);
		return matcher;
	}

	public void setSelection(ISelection s) {
		workspaceSelectionAdapter.setSelection(s);
	}

	public void setSelection(Object o) {
		workspaceSelectionAdapter.setSelection(o);
	}

	public void selectionChanged(SelectionSource source, Object o) {
		selectionMatcher.selectionChanged(source, o);
	}

	/**
	 * Returns index pages that will always be returned regardless of selection.
	 * 
	 * @return
	 * @throws QueryException
	 */
	public Collection<ContentMatch> getIndexPages() throws QueryException {
		return selectionMatcher.getIndexPages();
	}
}
