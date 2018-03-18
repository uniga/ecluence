package dk.uniga.ecluence.core;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

/**
 * Downloads attachments to files asynchronously. Listeners are notified as jobs
 * are completed.
 */
public interface AttachmentDownloader {

	void download(String name, File file);
	
	void cancel(String name);

	void addListener(Listener listener);
	
	void removeListener(Listener listener);

	@FunctionalInterface
	interface Listener {
		
		/**
		 * Notifies this listener that the given jobs have been completed.
		 */
		void notifyDownloadsCompleted(Collection<AttachmentJob> jobs);

	}
	
	interface AttachmentJob {

		String getName();
		
		File getFile();
		
		Optional<Exception> getException();
		
		boolean isFailed();
		
		boolean isDone();
		
		/**
		 * Tries completing the job, return <code>true</code> if it completed (possibly
		 * with an exception, see {@link #isFailed()})
		 * 
		 * @return <code>true</code> if job completed
		 */
		boolean complete();
		
		void stop();
		
	}

}
