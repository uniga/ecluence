package dk.uniga.ecluence.ui.parts;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.ImageLinkReplacer;
import dk.uniga.ecluence.core.ImageStore;
import dk.uniga.ecluence.core.NotConnectedException;
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
		PageBrowser browser = new PageBrowser(parent, facadeSupplier, createTemplateProvider());
		addImageReplacer(browser);
		addLinkifier(browser);
		addWikiLinkHandler(browser);
		return browser;
	}

	private TemplateProvider createTemplateProvider() {
		return new ConfigurableTemplateProvider(getPreferenceStore(), new BuiltinTemplateProvider());
	}
	
	private IPreferenceStore getPreferenceStore() {
		return dk.uniga.ecluence.ui.Activator.getDefault().getPreferenceStore();
	}

	private void addImageReplacer(PageBrowser browser) {
		IPath stateLocation = Activator.getDefault().getStateLocation().addTrailingSeparator().append("imagecache");
		try {
			ImageStore imageStore = new ImageStore(stateLocation.toFile());
			ImageLinkReplacer replacer = new ImageLinkReplacer((String name) -> getAttachment(name), imageStore);
			browser.addContentProcessor(replacer);
		} catch (IOException e) {
			Activator.handleError("Cannot store images locally", e, true);
		}
	}
	
	private void addLinkifier(PageBrowser browser) {
		NameLinkifier linkifier = new NameLinkifier(browser.getComponent().getShell());
		browser.addContentProcessor(linkifier);
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

	private InputStream getAttachment(String name) {
		try {
			return getConfluenceFacade().getAttachment(name);
		} catch (NotConnectedException | InterruptedException e) {
			// Ignore because connection may have been closed while we read page
			Activator.handleError("Cannot get attachment", e, false);
		} catch (ExecutionException e) {
			Activator.handleError("Cannot get attachment", e, true);
		}
		return null;
	}

	private ConfluenceFacade getConfluenceFacade() {
		return facadeSupplier.get();
	}

}
