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
import java.util.List;
import java.util.Objects;
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
import dk.uniga.ecluence.core.PageContentProcessingException;
import dk.uniga.ecluence.core.PageContentProcessor;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.ui.Activator;
import dk.uniga.ecluence.ui.template.TemplateProvider;

/**
 * Wrapper of a {@link Browser} component that can show pages using
 * {@link #showPage(ContentBean)}. Formats pages using a template from a given
 * TemplateProvider, processing pages with any registered content processors
 * ({@link #addContentProcessor(PageContentProcessor)}), and handles when links
 * are clicked, first checking with an internal handler that can load linked
 * pages and then any LinkHandler registered with
 * {@link #addLinkHandler(LinkHandler)}.
 */
public final class PageBrowser {

	private static final Logger log = LoggerFactory.getLogger(PageBrowser.class);

	private final Browser browser;

	private final Supplier<ConfluenceFacade> facadeSupplier;

	private final TemplateProvider templateProvider;

	private final List<PageContentProcessor> contentProcessors = new ArrayList<>();

	private final List<LinkHandler> linkHandlers = new ArrayList<>();

	private ContentBean currentPage;

	public PageBrowser(Composite parent, Supplier<ConfluenceFacade> facadeSupplier, TemplateProvider templateProvider) {
		this.facadeSupplier = facadeSupplier;
		this.templateProvider = templateProvider;
		this.browser = createBrowser(parent, SWT.NONE);
		addLinkHandler(createPageLinkHandler());
	}

	/**
	 * Adds a PageContentProcessor to process the content. Processors are called in
	 * the order they have been added.
	 * 
	 * @param processor PageContentProcessor instance to process the content
	 */
	public void addContentProcessor(PageContentProcessor processor) {
		contentProcessors.add(Objects.requireNonNull(processor));
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

	private LinkHandler createPageLinkHandler() {
		return (location) -> handlePageLink(location);
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
					ContentBean page = getConfluenceFacade().getPageById(contentId);
					log.debug("page loaded {}", page);
					if (page != null)
						showPage(page);
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
		if (page != null && !page.equals(currentPage)) {
			String content = new ContentFormatter(templateProvider).formatContent(page);
			if (content != null) {
				for (PageContentProcessor processor : contentProcessors) {
					try {
						content = processor.process(content);
					} catch (PageContentProcessingException e) {
						// log error and continue using the content as is
						Activator.handleError("Exception processing page content", e, false);
					}
				}
				updateBrowser(content, page);
			}
		}
	}

	private void updateBrowser(String content, ContentBean page) {
		if (browser.isDisposed())
			return;
		new UIJob("Loading page...") {
			@Override
			public IStatus runInUIThread(IProgressMonitor arg0) {
				if (!content.equals(browser.getText())) {
					log.debug("browser.setText");
					browser.setText(content);
				}
				currentPage = page;
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	public void setFocus() {
		browser.setFocus();
	}

}
