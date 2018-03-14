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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.ContentUpdateListener;
import dk.uniga.ecluence.core.NotConnectedException;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentCacheProvider;
import dk.uniga.ecluence.core.matching.ContentMatch;
import dk.uniga.ecluence.core.matching.ContentMatcherRegistry;
import dk.uniga.ecluence.core.matching.SelectionSource;
import dk.uniga.ecluence.ui.Activator;
import dk.uniga.ecluence.ui.handlers.EventConstants;
import dk.uniga.ecluence.ui.parts.formatted.FormattedMatchFactoryImpl;

/**
 * Eclipse view part that shows matching Confluence pages and shows the content
 * of the selected page (defaults to the first matching page).
 * 
 * Automatically connects to a new {@link ConfluenceFacade} if user connects to
 * a new Confluence location.
 */
public final class ConfluenceView {

	static final Logger log = LoggerFactory.getLogger(ConfluenceView.class);
	
	@Inject
	private UISynchronize synchronize;

	@Inject
	@Translation
	private Messages messages;

	private ConfluenceFacade confluenceFacade;

	private PageBrowser pageBrowser;

	private PageListViewer listViewer;

	private EclipseSelectionContentMatcher selectionContentMatcher;

	/** Last object matched by {@link #selectionContentMatcher} */
	private Object currentSelection;
	
	private SelectionSource currentSelectionSource;

	Consumer<ContentMatch> openCommand = (page) -> showPageAsync(page);
	
	Consumer<ContentMatch> openExternalCommand = (page) -> openPageInExternalBrowser(page);
	
	Consumer<String> openWikiLinkExternalCommand = (string) -> openWikiLinkInExternalBrowser(string);
	
	ContentUpdateListener contentCacheListener = pages -> refreshCurrentSelection();

	private StatusLabel statusLabel;

	public ConfluenceView() {
	}
	
	@PostConstruct
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		final SashForm sash = new SashForm(parent, SWT.VERTICAL);
		sash.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		createComponents(sash);
		sash.setWeights(new int[] { 20, 80 });
		
		ContentMatcherRegistry contentMatcherRegistry = dk.uniga.ecluence.core.Activator.getDefault().getContentMatcherRegistry();
		selectionContentMatcher = new EclipseSelectionContentMatcher(contentMatcherRegistry, 
				(source, o, matches) -> matched(source, o, matches), () -> getPreferenceStore());
		
		this.confluenceFacade = dk.uniga.ecluence.core.Activator.getDefault().getConfluenceFacade();
		
		showIndexPages();
	}

	private void createComponents(final SashForm parent) {
		listViewer = new PageListViewer(parent, openCommand, openExternalCommand, new FormattedMatchFactoryImpl());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().create());
		pageBrowser = new PageBrowserBuilder(() -> getConfluenceFacade(), openWikiLinkExternalCommand).build(composite);
		pageBrowser.getComponent().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		statusLabel = new StatusLabel(composite);
	}

	private IPreferenceStore getPreferenceStore() {
		return dk.uniga.ecluence.ui.Activator.getDefault().getPreferenceStore();
	}

	private ConfluenceFacade getConfluenceFacade() {
		return confluenceFacade;
	}

	@PreDestroy
	public void close() {
		selectionContentMatcher.close();
	}

	@Inject
	@Optional
	private void refresh(@UIEventTopic(EventConstants.REFRESH_DONE) Integer updates) {
		log.debug("refresh with {} updates", updates);
		refreshCurrentSelection();
	}

	@Inject
	@Optional
	private void connectedFacade(@UIEventTopic(EventConstants.CONNECTED_FACADE) ConfluenceFacade facade) {
		log.debug("connected to {}", facade);
		updateConfluenceFacade(facade);
		update();
	}

	/**
	 * This will load the view when content caches have been added.
	 * 
	 * @param provider
	 */
	@Inject
	@Optional
	private void cacheAdded(@UIEventTopic(dk.uniga.ecluence.core.EventConstants.CONTENT_CACHE_ADDED) ContentCacheProvider provider) {
		update();
	}
	
	/**
	 * This will load the given page in the page browser.
	 * 
	 * @param page
	 */
	@Inject
	@Optional
	private void cacheAdded(@UIEventTopic(EventConstants.PAGE_SELECTED) ContentBean page) {
		pageBrowser.showPage(page);
	}
	
	private void update() {
		if (currentSelection != null)
			refreshCurrentSelection();
		else
			showIndexPages();
	}
	
	@Inject
	@Optional
	private void exception(@UIEventTopic(dk.uniga.ecluence.core.EventConstants.EXCEPTION) Exception exception) {
		log.error("Notified of exception", exception);
		String message = exception.getMessage();
		Throwable cause = exception.getCause();
		String causeMessage = (cause != null) ? String.format("(%s: %s)", cause.getClass().getTypeName(), cause.getMessage()) : "";
		String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		statusLabel.setVisible(true);
		statusLabel.setText(String.format("%s %s [%s]", message, causeMessage, time));
	}
	
	private void updateConfluenceFacade(ConfluenceFacade facade) {
		Objects.requireNonNull(facade);
		log.debug("updateConfluenceFacade({}) existing: ", facade, this.confluenceFacade);
		try {
			if (this.confluenceFacade != null) {
				this.confluenceFacade.removeContentListener(contentCacheListener);
			}
		} catch (NotConnectedException e) {
			Activator.handleError("Cannot register listener to ConfluenceFacade", e, false);
		}
		this.confluenceFacade = facade;
		try {
			facade.addContentListener(contentCacheListener);
		} catch (NotConnectedException e) {
			Activator.handleError("Cannot register listener to ConfluenceFacade", e, false);
		}
	}
	
	protected void refreshCurrentSelection() {
		if (currentSelection != null)
			selectionContentMatcher.selectionChanged(currentSelectionSource, currentSelection);
	}

	protected void matched(final SelectionSource source, final Object o, final Collection<ContentMatch> pages) {
		log.debug("matched {} pages to object {} from source {}", pages.size(), o.getClass().getSimpleName(), source);
		currentSelectionSource = source;
		currentSelection = o;
		updateMatches(pages);
	}

	private void showIndexPages() {
		new Job("Retrieve index pages") {
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				try {
					if (selectionContentMatcher != null)
						updateMatches(selectionContentMatcher.getIndexPages());
					return Status.OK_STATUS;
				} catch (QueryException e) {
					return Status.OK_STATUS;
				}
			}
		}.schedule();
	}

	private void updateMatches(Collection<ContentMatch> pages) {
		updateList(pages);
		showPage(pages);
	}

	private void updateList(final Collection<ContentMatch> matches) {
		synchronize.asyncExec(new Runnable() {
			@Override
			public void run() {
				listViewer.setInput(new WritableList<ContentMatch>(matches, ContentMatch.class));
			}
		});
	}

	protected void showPage(final Collection<ContentMatch> matches) {
		if (!matches.isEmpty()) {
			showPageAsync(matches.iterator().next());
		}
	}

	private void showPageAsync(ContentMatch match) {
		synchronize.asyncExec(new Runnable() {
			@Override
			public void run() {
				selectPageInList(match);
				pageBrowser.showPage(match.getContent());
			}
		});
	}

	private void selectPageInList(ContentMatch match) {
		TableViewer viewer = listViewer.getViewer();
		if (viewer.getStructuredSelection().isEmpty()) {
			log.debug("selectPageInList: {}", match);
			viewer.setSelection(new StructuredSelection(match), true);
		}
	}

	private void openPageInExternalBrowser(ContentMatch page) {
		openWikiLinkInExternalBrowser(page.getContent().getLinks().getWebui());
	}

	private void openWikiLinkInExternalBrowser(String link) {
		try {
			if (getConfluenceFacade() != null) {
				String url = getConfluenceFacade().getLinkUrl(link);
				log.debug("openWikiLinkInExternalBrowser({}): {}", link, url);
				org.eclipse.swt.program.Program.launch(url);
			}
		} catch (NotConnectedException e) {
		}
	}

	@Focus
	public void setFocus() {
		pageBrowser.setFocus();
	}

	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) ISelection s) {
		if (selectionContentMatcher != null)
			selectionContentMatcher.setSelection(s);
	}

	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {
		if (selectionContentMatcher != null)
			selectionContentMatcher.setSelection(o);
	}

	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object[] selectedObjects) {
		// Ignore for now
//		StructuredSelection s = new StructuredSelection(selectedObjects);
//		if (selectionContentMatcher != null)
//			selectionContentMatcher.setSelection(s);
	}

	private class StatusLabel {

		private final Label label;
		private final GridData data;

		public StatusLabel(Composite parent) {
			label = new Label(parent, SWT.NONE);
			data = GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create();
			label.setLayoutData(data);
			setVisible(false);
			addClickToClose();
		}

		private void addClickToClose() {
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					setVisible(false);
				}
			});
		}

		protected void setVisible(boolean b) {
			data.exclude = !b;
			label.setVisible(b);
			label.getParent().layout(true, true);
		}

		public void setText(String text) {
			label.setText(text);
		}
		
	}
	
}
