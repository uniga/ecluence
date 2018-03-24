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

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.AttachmentDownloader;
import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.ImageLinkReplacer;
import dk.uniga.ecluence.core.ImageStore;
import dk.uniga.ecluence.ui.Activator;
import dk.uniga.ecluence.ui.template.BuiltinTemplateProvider;
import dk.uniga.ecluence.ui.template.ConfigurableTemplateProvider;
import dk.uniga.ecluence.ui.template.TemplateProvider;

public final class PageBrowserBuilder {

	private static final Logger log = LoggerFactory.getLogger(PageBrowserBuilder.class);
	
	private final Supplier<ConfluenceFacade> facadeSupplier;

	private final Consumer<String> openWikiLinkExternalCommand;

	public PageBrowserBuilder(Supplier<ConfluenceFacade> facadeSupplier, Consumer<String> openWikiLinkExternalCommand) {
		this.facadeSupplier = facadeSupplier;
		this.openWikiLinkExternalCommand = openWikiLinkExternalCommand;
	}

	public PageBrowser build(final Composite parent) {
		PageContentRenderer renderer = createPageContentRenderer();
		PageBrowser browser = new PageBrowser(parent, facadeSupplier, renderer);
		addLinkifier(browser, renderer);
		addWikiLinkHandler(browser);
		return browser;
	}

	private PageContentRenderer createPageContentRenderer() {
		ContentFormatter formatter = new ContentFormatter(createTemplateProvider());
		PageContentRenderer renderer = new PageContentRenderer(formatter);
		addImageReplacer(renderer);
		return renderer;
	}

	private TemplateProvider createTemplateProvider() {
		return new ConfigurableTemplateProvider(getPreferenceStore(), new BuiltinTemplateProvider());
	}
	
	private IPreferenceStore getPreferenceStore() {
		return dk.uniga.ecluence.ui.Activator.getDefault().getPreferenceStore();
	}

	private void addImageReplacer(PageContentRenderer renderer) {
		IPath stateLocation = Activator.getDefault().getStateLocation().addTrailingSeparator().append("imagecache");
		try {
			ImageStore imageStore = new ImageStore(stateLocation.toFile());
			AttachmentDownloader downloader = new AttachmentDownloaderImpl(facadeSupplier, imageStore);
			ImageLinkReplacer replacer = new ImageLinkReplacer(downloader, imageStore);
			renderer.addContentProcessor(replacer);
		} catch (IOException e) {
			Activator.handleError("Cannot store images locally", e, true);
		}
	}
	
	private void addLinkifier(PageBrowser browser, PageContentRenderer renderer) {
		NameLinkifier linkifier = new NameLinkifier(browser.getComponent().getShell());
		renderer.addContentProcessor(linkifier);
		browser.addLinkHandler(linkifier);
	}
	
	private void addWikiLinkHandler(PageBrowser browser) {
		browser.addLinkHandler(location -> {
			String link = LinkMatcher.matchWikiLink(location);
			if (link != null) {
				log.debug("handleWikiLink({}) link = {}", location, link);
				openWikiLinkExternalCommand.accept(link);
				return true;
			}
			return false;
		});
	}

}
