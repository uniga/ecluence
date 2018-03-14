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
package dk.uniga.ecluence.ui.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.NotConnectedException;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.matching.ContentMatch;
import dk.uniga.ecluence.core.matching.ContentMatchComparator;
import dk.uniga.ecluence.core.matching.ContentMatcher;
import dk.uniga.ecluence.core.matching.SelectionListener;
import dk.uniga.ecluence.core.matching.SelectionSource;
import dk.uniga.ecluence.ui.Activator;

/**
 * Delegates selection changes to content matchers, collects all
 * {@link ContentMatch}es and notifiers listeners when done.
 */
public class SelectionMatcher implements SelectionListener {

	private static final Logger log = LoggerFactory.getLogger(SelectionMatcher.class);

	private final List<ContentMatcher> selectionHandlers = new ArrayList<>();

	private final Collection<ContentMatchListener> listeners = new HashSet<>();

	public SelectionMatcher() {
	}

	/**
	 * Adds {@link ContentMatcher}s that should be checked for matching content when
	 * the selection changes.
	 * 
	 * @param handlers
	 *            One or more {@link ContentMatcher}s
	 */
	public void addHandlers(ContentMatcher... handlers) {
		synchronized (selectionHandlers) {
			selectionHandlers.addAll(Arrays.asList(handlers));
		}
	}

	/**
	 * Adds a listener to receive notification when new content has been matched.
	 * 
	 * @param listener
	 */
	public void addMatchListener(ContentMatchListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeMatchListener(ContentMatchListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public void selectionChanged(SelectionSource source, Object selection) {
		match(source, selection);
	}

	private void match(SelectionSource source, Object o) {
		new Job("Searching for matching Confluence content") {
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				try {
					Collection<ContentMatch> matches = getMatches(o);
					notifyMatched(source, o, matches);
				} catch (NotConnectedException e) {
					log.debug("Not connected.");
				} catch (QueryException e) {
					return new Status(Status.ERROR, Activator.PLUGIN_ID, "Could not query Confluence.", e);
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	private Collection<ContentMatch> getMatches(Object o) throws QueryException {
		Collection<ContentMatch> pages = new TreeSet<ContentMatch>(new ContentMatchComparator());
		for (ContentMatcher handler : selectionHandlers) {
			log.debug("handle selection in {}", handler);
			pages.addAll(handler.getMatches(o));
		}
		return pages;
	}

	private void notifyMatched(SelectionSource source, Object o, Collection<ContentMatch> matches) {
		for (ContentMatchListener listener : listeners) {
			listener.matched(source, o, matches);
		}
	}

	/**
	 * Listener that is notified of content matching a given selection.
	 */
	@FunctionalInterface
	public interface ContentMatchListener {
		/**
		 * Notifies this listener of matches to a selection object o that originates
		 * from the given SelectionSource source.
		 * 
		 * @param source the source of the selection object
		 * @param o the selection object
		 * @param matches the content matches
		 */
		void matched(SelectionSource source, Object o, Collection<ContentMatch> matches);
	}

	/**
	 * Returns index pages that will always be returned regardless of selection.
	 * 
	 * @return
	 * @throws QueryException
	 */
	public Collection<ContentMatch> getIndexPages() throws QueryException {
		return getMatches(null);
	}

}
