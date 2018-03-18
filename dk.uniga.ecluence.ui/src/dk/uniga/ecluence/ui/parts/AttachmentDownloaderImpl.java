package dk.uniga.ecluence.ui.parts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.AttachmentDownloader;
import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.ImageStore;
import dk.uniga.ecluence.core.NotConnectedException;
import dk.uniga.ecluence.ui.Activator;

public final class AttachmentDownloaderImpl implements AttachmentDownloader {
	
	private static final Logger log = LoggerFactory.getLogger(AttachmentDownloaderImpl.class);

	private final Supplier<ConfluenceFacade> facadeSupplier;
	private final ImageStore imageStore;
	private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
	private final Collection<AttachmentJob> jobs = new ArrayList<>();
	private final Set<Listener> listeners = new HashSet<>();

	private ScheduledFuture<?> future;
	
	public AttachmentDownloaderImpl(Supplier<ConfluenceFacade> facadeSupplier, ImageStore imageStore) {
		this.facadeSupplier = facadeSupplier;
		this.imageStore = imageStore;
	}
	
	private void completeJobs() {
		synchronized (jobs) {
			List<AttachmentJob> completed = filterBy(jobs, AttachmentJob::complete).collect(Collectors.toList());
			if (completed.size() == jobs.size()) {
				log.debug("jobs done with {} exceptions", countFailedJobs());
				jobs.removeAll(completed);
				listeners.forEach((l) -> l.notifyDownloadsCompleted(completed));
				future.cancel(false);
				future = null;
			}
		}
	}

	private Stream<AttachmentJob> filterBy(Collection<AttachmentJob> jobs, Predicate<? super AttachmentJob> filter) {
		return StreamSupport.stream(jobs.spliterator(), false).filter(filter);
	}

	private long countFailedJobs() {
		return filterBy(jobs, AttachmentJob::isFailed).count();
	}

	@Override
	public void cancel(String name) {
		synchronized (jobs) {
			Optional<AttachmentJob> job = filterBy(jobs, (j) -> j.getName().equals(name)).findFirst();
			if (job.isPresent()) {
				job.get().stop();
			}
		}
	}
	
	@Override
	public void download(String name, File file) {
		log.debug("download({}, {})", name, file.getName());
		synchronized (jobs) {
			jobs.add(new AttachmentJobImpl(name, file));
			if (future == null) {
				log.debug("schedule completeJobs");
				future = this.executor.scheduleAtFixedRate(this::completeJobs, 1000, 1000, TimeUnit.MILLISECONDS);
			}
		}
	}
	
	@Override
	public void addListener(Listener listener) {
		listeners.add(listener);
		log.debug("addListener({}), listeners: {}", listener, listeners);
	}
	
	@Override
	public void removeListener(Listener listener) {
		listeners.remove(listener);
		log.debug("removeListener({}), remaining listeners: {}", listener, listeners);
	}
	
	enum Status { Done, Failed, Waiting };
	
	/**
	 * Not thread safe.
	 */
	class AttachmentJobImpl implements AttachmentJob {
		
		private final String name;
		private final File file;
		private Future<InputStream> attachment;
		private Status status;
		private Exception exception;
		
		public AttachmentJobImpl(String name, File file) {
			this.name = name;
			this.file = file;
			this.status = Status.Waiting;
			start();
		}
		
		public String getName() {
			return name;
		}
		
		public File getFile() {
			return file;
		}
		
		public Optional<Exception> getException() {
			return Optional.ofNullable(exception);
		}
		
		public boolean isFailed() {
			return status == Status.Failed;
		}
		
		public boolean isDone() {
			return status == Status.Done;
		}
		
		public boolean complete() {
			// check if already complete
			if (status == Status.Done || status == Status.Failed)
				return true;
			log.debug("checking if download complete: {} {}", name, file.getName());
			if (attachment.isDone()) {
				try {
					imageStore.storeFile(file, attachment.get());
					status = Status.Done;
				} catch (InterruptedException | ExecutionException e) {
					Activator.handleError("Could not download attachment " + file.getName(), e, false);
					status = Status.Failed;
					exception = e;
				} catch (IOException e) {
					Activator.handleError("Could not store attachment " + file, e, false);
					status = Status.Failed;
					exception = e;
				}
				return true;
			}
			return false;
		}

		public void stop() {
			attachment.cancel(false);
		}

		public void start() {
			try {
				attachment = facadeSupplier.get().getAttachment(name);
			} catch (NotConnectedException e) {
				// Ignore because connection may have been closed while we read page
				Activator.handleError("Cannot get attachment", e, false);
			}
		}
		
		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append(name).append(file).append(status)
					.build();
		}
	}
}