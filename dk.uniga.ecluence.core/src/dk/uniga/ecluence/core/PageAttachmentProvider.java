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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.AttachmentDownloader.AttachmentJob;
import dk.uniga.ecluence.core.ImageAttachmentExtractor.ImageLink;
import dk.uniga.ecluence.core.PageContent.Listener;

public final class PageAttachmentProvider {

	private static final Logger log = LoggerFactory.getLogger(PageAttachmentProvider.class);
	
	private static final Pattern MODIFICATION_DATE_PATTERN = Pattern.compile("modificationDate=([0-9]+)[^0-9]+");
	
	private final AttachmentDownloader attachmentDownloader;
	
	private final PageContent content;
	
	private final HashMap<String, File> downloads = new HashMap<>();
	
	private boolean closed;

	private PageContent.Listener pageListener = new Listener() {
		@Override
		public void contentChanged(PageContent content) {
		}
		@Override
		public void closed(PageContent content) {
			cancel();
		}
	};
	
	private AttachmentDownloader.Listener downloaderListener = jobs -> downloadsDone(jobs);
	
	private static Map<PageKey, PageAttachmentProvider> downloaders = new HashMap<>();
	
	public static PageAttachmentProvider get(AttachmentDownloader attachmentDownloader, PageContent content) {
		synchronized (downloaders) {
			PageAttachmentProvider downloader = downloaders.get(content.getKey());
			if (downloader == null) {
				downloader = new PageAttachmentProvider(attachmentDownloader, content);
				downloaders.put(content.getKey(), downloader);
				log.debug("get({}), downloaders: {}", content.getKey(), downloaders.keySet());
			}
			return downloader;
		}
	}
	
	private static void remove(PageContent content) {
		downloaders.remove(content.getKey());
		log.debug("remove({}), downloaders: {}", content.getKey(), downloaders.keySet());
	}
	
	private PageAttachmentProvider(AttachmentDownloader attachmentDownloader, PageContent content) {
		log.debug("constructor({})", content.getKey());
		this.attachmentDownloader = attachmentDownloader;
		this.content = content;
		this.content.addListener(pageListener);
		attachmentDownloader.addListener(downloaderListener);
	}

	private void downloadsDone(Collection<AttachmentJob> jobs) {
		log.debug("downloadsDone({}) for {}", jobs, content.getKey());
		if (!jobs.isEmpty())
			replaceLinks(jobs);
		for (AttachmentJob job : jobs) {
			downloads.remove(job.getName());
		}
		removeIfDone();
	}
	
	private void removeIfDone() {
		if (downloads.isEmpty()) {
			this.content.removeListener(pageListener);
			attachmentDownloader.removeListener(downloaderListener);
			remove(content);
		}
	}

	private void cancel() {
		log.debug("cancel {}", content.getKey());
		for (Entry<String, File> entry : downloads.entrySet()) {
			attachmentDownloader.cancel(entry.getKey());
		}
		downloads.clear();
		removeIfDone();
	}
	
	private void replaceLinks(Collection<AttachmentJob> jobs) {
		String modified = content.getContent();
		for (AttachmentJob job : jobs) {
			modified = StringUtils.replace(modified, nameToPlaceholder(job.getName()), toFileLink(job.getFile()));
		}
		content.setContent(modified);
	}

	private String nameToPlaceholder(String name) {
		return "attachment" + name;
	}

	/**
	 * @param file
	 * @param link
	 * @return replacement link, either the filename if it exists or a placeholder
	 *         if the file awaits downloading
	 * @throws IOException
	 */
	public String replaceLink(File file, ImageLink link) throws IOException {
		String name = link.getLinkedResourceId();
		if (!closed) {
			if (isFetchNeeded(file, link))
				fetchFile(name, file);
		}
		return file.exists() ? toFileLink(file) : nameToPlaceholder(name);
	}

	private String toFileLink(File file) {
		return "file://" + file.getPath();
	}
	
	private boolean isFetchNeeded(File file, ImageLink link) {
		return !file.exists() || FileUtils.isFileOlder(file, getModificationDate(link));
	}

	private long getModificationDate(ImageLink link) {
		// Get modification date from src url
		Matcher matcher = MODIFICATION_DATE_PATTERN.matcher(link.getSrc());
		if (matcher.find()) {
			return Long.parseLong(matcher.group(1));
		}
		return 0;
	}
	
	private void fetchFile(String name, File file) throws IOException {
		log.debug("fetchFile({}, {})", name, file.getName());
		attachmentDownloader.download(name, file);
		downloads.put(name, file);
	}

	/**
	 * Start waiting for updates
	 */
	public void close() {
		this.closed = true;
		// If no downloads were started, we remove immediately
		removeIfDone();
	}

}
