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
import dk.uniga.ecluence.core.cache.ContentCacheProvider;
import dk.uniga.ecluence.core.matching.ContentMatch;
import dk.uniga.ecluence.core.matching.ContentMatcherRegistry;
import dk.uniga.ecluence.core.matching.SelectionSource;
import dk.uniga.ecluence.ui.Activator;
import dk.uniga.ecluence.ui.handlers.EventConstants;
import dk.uniga.ecluence.ui.parts.SelectionMatcher.ContentMatchListener;
import dk.uniga.ecluence.ui.parts.formatted.FormattedMatchFactoryImpl;

/**
 * Eclipse view part that shows matching Confluence pages and shows the content
 * of the selected page.
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

	private PageBrowser pageBrowser;
	
	private PageListViewer listViewer;
	
	private StatusLabel statusLabel;
	
	private ConfluenceFacade confluenceFacade;

	private ViewUpdater viewUpdater;
	
	private EclipseSelectionContentMatcher selectionContentMatcher;

	private final Consumer<ContentMatch> openCommand = (match) -> openPageInBrowser(match);
	
	private final Consumer<ContentMatch> openExternalCommand = (match) -> openPageInExternalBrowser(match);
	
	private final Consumer<String> openWikiLinkExternalCommand = (string) -> openWikiLinkInExternalBrowser(string);
	
	private final ContentMatchListener contentMatchListener = (source, o, matches) -> matched(source, o, matches);

	private final ContentUpdateListener contentCacheListener = (pages) -> viewUpdater.refreshSelection();

	private final Consumer<ContentBean> linkedPageListener = (page) -> viewUpdater.userSelectedPage(page);
	
	public ConfluenceView() {
	}
	
	@PostConstruct
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		final SashForm sash = new SashForm(parent, SWT.VERTICAL);
		sash.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		createComponents(sash);
		sash.setWeights(new int[] { 20, 80 });
		
		this.selectionContentMatcher = createSelectionMatcher();
		this.viewUpdater = createViewUpdater();
		this.confluenceFacade = dk.uniga.ecluence.core.Activator.getDefault().getConfluenceFacade();
		
		viewUpdater.refreshSelection();
	}

	private ViewUpdater createViewUpdater() {
		return new ViewUpdater(selectionContentMatcher,
				new ViewUpdateStrategy(pages -> updateListAsync(pages), pages -> selectMatchAsync(pages)));
	}

	private EclipseSelectionContentMatcher createSelectionMatcher() {
		ContentMatcherRegistry contentMatcherRegistry = dk.uniga.ecluence.core.Activator.getDefault()
				.getContentMatcherRegistry();
		return new EclipseSelectionContentMatcher(contentMatcherRegistry, contentMatchListener,
				() -> getPreferenceStore());
	}

	private void createComponents(final SashForm parent) {
		listViewer = new PageListViewer(parent, openCommand, openExternalCommand, new FormattedMatchFactoryImpl());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().create());
		pageBrowser = new PageBrowserBuilder(() -> getConfluenceFacade(), openWikiLinkExternalCommand).build(composite);
		pageBrowser.getComponent().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		pageBrowser.addLinkedPageListener(linkedPageListener);
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

	private void openPageInBrowser(ContentMatch match) { 
		selectMatchAsync(match); 
		viewUpdater.userSelectedPage(match.getContent()); 
	}
	
	@Inject
	@Optional
	private void refresh(@UIEventTopic(EventConstants.REFRESH_DONE) Integer updates) {
		log.debug("refresh with {} updates", updates);
		viewUpdater.refreshSelection();
	}

	@Inject
	@Optional
	private void connectedFacade(@UIEventTopic(EventConstants.CONNECTED_FACADE) ConfluenceFacade facade) {
		log.debug("connected to {}", facade);
		updateConfluenceFacade(facade);
		viewUpdater.refreshSelection();
	}

	/**
	 * This will load the view when content caches have been added.
	 * 
	 * @param provider
	 */
	@Inject
	@Optional
	private void cacheAdded(@UIEventTopic(dk.uniga.ecluence.core.EventConstants.CONTENT_CACHE_ADDED) ContentCacheProvider provider) {
		viewUpdater.refreshSelection();
	}
	
	/**
	 * This will load the given page in the page browser.
	 * 
	 * @param page
	 */
	@Inject
	@Optional
	private void userSelectedPage(@UIEventTopic(EventConstants.USER_SELECTED_PAGE) ContentBean page) {
		viewUpdater.userSelectedPage(page);
		pageBrowser.showPage(page);
	}
	
	@Inject
	@Optional
	private void exception(@UIEventTopic(dk.uniga.ecluence.core.EventConstants.EXCEPTION) Exception exception) {
		log.error("Notified of exception", exception);
		statusLabel.handleException(exception);
	}
	
	private void updateConfluenceFacade(ConfluenceFacade facade) {
		Objects.requireNonNull(facade);
		log.debug("updateConfluenceFacade({}) existing: {}", facade, this.confluenceFacade);
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

	protected void matched(final SelectionSource source, final Object o, final Collection<ContentMatch> pages) {
		log.debug("matched {} pages to object {} from source {}", pages.size(), o.getClass().getSimpleName(), source);
		viewUpdater.updateMatches(source, o, pages);
	}

	private void updateListAsync(final Collection<ContentMatch> matches) {
		synchronize.asyncExec(new Runnable() {
			@Override
			public void run() {
				listViewer.setInput(new WritableList<ContentMatch>(matches, ContentMatch.class));
			}
		});
	}

	private void selectMatchAsync(ContentMatch match) {
		log.debug("selectMatchAsync {}", match);
		synchronize.asyncExec(new Runnable() {
			@Override
			public void run() {
				listViewer.selectPage(match);
				pageBrowser.showPage(match.getContent());
			}
		});
	}

	private void openPageInExternalBrowser(ContentMatch match) {
		openWikiLinkInExternalBrowser(match.getContent().getLinks().getWebui());
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

	private final class StatusLabel {

		private final Label label;
		private final GridData data;

		public StatusLabel(Composite parent) {
			label = new Label(parent, SWT.NONE);
			data = GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create();
			label.setLayoutData(data);
			setVisible(false);
			addClickToClose();
		}

		public void handleException(Exception exception) {
			String message = exception.getMessage();
			Throwable cause = exception.getCause();
			String causeMessage = (cause != null) ? String.format("(%s: %s)", cause.getClass().getTypeName(), cause.getMessage()) : "";
			String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			setVisible(true);
			setText(String.format("%s %s [%s]", message, causeMessage, time));
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

		protected void setText(String text) {
			label.setText(text);
		}
		
	}
	
}
