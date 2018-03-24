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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.PageContent;
import dk.uniga.ecluence.core.PageContent.Listener;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.ui.Activator;

/**
 * Wrapper of a {@link Browser} component that can show pages using
 * {@link #showPage(ContentBean)}. Uses a PageContentRenderer to format and
 * process content, and handles when links are clicked, first checking with an
 * internal handler that can load linked pages and then any LinkHandler
 * registered with {@link #addLinkHandler(LinkHandler)}.
 */
public final class PageBrowser {

	private static final Logger log = LoggerFactory.getLogger(PageBrowser.class);

	private final Browser browser;

	private final Supplier<ConfluenceFacade> facadeSupplier;

	private final PageContentRenderer pageRenderer;

	private final List<LinkHandler> linkHandlers = new ArrayList<>();

	private PageContent currentPage;
	
	private Object[] pageLock = new Object[0];

	private Listener pageListener = new Listener() {
		@Override
		public void contentChanged(PageContent content) {
			if (content.equals(currentPage)) {
				log.debug("contentChanged({}), updating browser", content.getKey());
				updateBrowser(content);
			}
		}
		@Override
		public void closed(PageContent content) {
		}
	};

	private Consumer<ContentBean> linkedPageListener;

	public PageBrowser(Composite parent, Supplier<ConfluenceFacade> facadeSupplier, PageContentRenderer renderer) {
		this.facadeSupplier = facadeSupplier;
		this.pageRenderer = renderer;
		this.browser = createBrowser(parent, SWT.NONE);
		addLinkHandler(this::handlePageLink);
	}

	/**
	 * Adds a listener to be notified when a page link has been clicked.
	 * 
	 * @param linkedPageListener
	 *            consumer to accept a ContentBean when it gets shown in the browser
	 */
	public void addLinkedPageListener(Consumer<ContentBean> linkedPageListener) {
		this.linkedPageListener = linkedPageListener;
	}
	
	/**
	 * Adds a LinkHandler to handle links in the page that are clicked. Handlers are
	 * called in the order they have been added, until the
	 * {@link LinkHandler#handle(String)} method of a handler returns
	 * <code>true</code>.
	 * 
	 * @param handler
	 *            LinkHandler
	 */
	public void addLinkHandler(LinkHandler handler) {
		linkHandlers.add(Objects.requireNonNull(handler));
	}

	private Browser createBrowser(Composite parent, int style) {
		Browser browser = new Browser(parent, style);
		browser.addLocationListener(createLocationListener());
		return browser;
	}

	private LocationListener createLocationListener() {
		return new LocationListener() {
			@Override
			public void changing(LocationEvent arg0) {
				arg0.doit = !handleLocation(arg0.location);
			}

			@Override
			public void changed(LocationEvent arg0) {
			}
		};
	}

	private boolean handleLocation(String location) {
		for (LinkHandler handler : linkHandlers) {
			if (handler.handle(location))
				return true;
		}
		return false;
	}

	private boolean handlePageLink(String location) {
		String pageId = LinkMatcher.matchPage(location);
		log.debug("handlePageLink({}) pageId = {}", location, pageId);
		if (pageId != null) {
			loadPage(pageId);
			return true;
		}
		return false;
	}

	/**
	 * Returns the underlying Browser component.
	 * 
	 * @return
	 */
	public Browser getComponent() {
		return browser;
	}

	private void loadPage(String contentId) {
		log.debug("load page {}", contentId);
		new Job("Loading page...") {
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				try {
					log.debug("getPageById({})", contentId);
					ContentBean page = getConfluenceFacade().getPageById(contentId);
					log.debug("page loaded {}", page.getId());
					if (page != null) {
						showPage(page);
						linkedPageListener.accept(page);
					}
				} catch (QueryException e) {
					Activator.handleError("Exception retrieving page " + contentId, e, false);
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	private ConfluenceFacade getConfluenceFacade() {
		return facadeSupplier.get();
	}

	/**
	 * Updates the wrapped Browser component with the given page, formatted using a
	 * template and with pre-processing of the content.
	 * 
	 * @param page
	 */
	void showPage(ContentBean page) {
		synchronized (pageLock) {
			log.debug("showPage({}) {}", page.getId(), page.getTitle());
			if (page != null && !isSamePage(page)) {
				PageContent rendered = pageRenderer.render(page);
				setCurrentPage(rendered);
				updateBrowser(rendered);
			}
		}
	}

	private boolean isSamePage(ContentBean page) {
		return currentPage != null && page.equals(currentPage.getPage());
	}

	private void setCurrentPage(PageContent rendered) {
		log.debug("setCurrentPage({})", rendered.getKey());
		if (currentPage != null) {
			currentPage.removeListener(pageListener);
			currentPage.close();
		}
		currentPage = rendered;
		currentPage.addListener(pageListener);
	}
	
	private void updateBrowser(PageContent rendered) {
		new UIJob("Showing page...") {
			@Override
			public IStatus runInUIThread(IProgressMonitor arg0) {
				if (browser.isDisposed())
					return Status.OK_STATUS;
				browser.setText(rendered.getContent());
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	public void setFocus() {
		browser.setFocus();
	}
}
