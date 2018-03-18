package dk.uniga.ecluence.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
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
import dk.uniga.ecluence.core.PageContent.PageKey;

public final class PageAttachmentProvider {

	private static final Logger log = LoggerFactory.getLogger(PageAttachmentProvider.class);
	
	private static final Pattern MODIFICATION_DATE_PATTERN = Pattern.compile("modificationDate=([0-9]+)[^0-9]+");
	
	private final AttachmentDownloader attachmentDownloader;
	
	private final PageContent content;
	
	private final HashMap<String, File> downloads = new HashMap<>();
	
	private boolean closed;

	private Listener listener = new Listener() {
		@Override
		public void contentChanged(PageContent content) {
		}
		@Override
		public void closed(PageContent content) {
			cancel();
		}
	};
	
	private static Map<PageKey, PageAttachmentProvider> downloaders = new HashMap<>();
	
	public static PageAttachmentProvider get(AttachmentDownloader attachmentDownloader, PageContent content) {
		synchronized (downloaders) {
			PageAttachmentProvider downloader = downloaders.get(content.getKey());
			if (downloader == null) {
				downloader = new PageAttachmentProvider(attachmentDownloader, content);
				downloaders.put(content.getKey(), downloader);
			}
			return downloader;
		}
	}
	
	private static void remove(PageContent content) {
		downloaders.remove(content.getKey());
	}
	
	private PageAttachmentProvider(AttachmentDownloader attachmentDownloader, PageContent content) {
		this.attachmentDownloader = attachmentDownloader;
		this.content = content;
		this.content.addListener(listener);
		log.debug("instance({})", content.getKey());
		attachmentDownloader.addListener(this::downloadsDone);
	}

	private void downloadsDone(Collection<AttachmentJob> jobs) {
		log.debug("downloadsDone({}) for {}", jobs, content.getKey());
		replaceLinks(jobs);
		attachmentDownloader.removeListener(this::downloadsDone);
		remove(content);
	}
	
	private void cancel() {
		log.debug("cancel {}", content.getKey());
		for (Entry<String, File> entry : downloads.entrySet()) {
			attachmentDownloader.cancel(entry.getKey());
		}
		attachmentDownloader.removeListener(this::downloadsDone);
		remove(content);
	}
	
	private void replaceLinks(Collection<AttachmentJob> jobs) {
		String modified = content.getContent();
		for (AttachmentJob job : jobs) {
			modified = StringUtils.replace(modified, nameToPlaceholder(job.getName()), toFileLink(job.getFile()));
			downloads.remove(job.getName());
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
		log.debug("fetchFile({}, {})", name, file);
		attachmentDownloader.download(name, file);
		downloads.put(name, file);
	}

	/**
	 * Start waiting for updates
	 */
	public void close() {
		this.closed = true;
	}

}
