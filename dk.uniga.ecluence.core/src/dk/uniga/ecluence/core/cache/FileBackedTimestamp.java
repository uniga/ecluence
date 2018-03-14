package dk.uniga.ecluence.core.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import dk.uniga.ecluence.core.StoredTimestamp;

/**
 * A date and time persisted using last modification time of a file.
 */
public class FileBackedTimestamp implements StoredTimestamp {

	private final File directory;
	private final String contentName;

	public FileBackedTimestamp(File directory, String contentName) {
		this.directory = directory;
		this.contentName = contentName;
		directory.mkdirs();
	}

	@Override
	public Optional<LocalDateTime> get() {
		File file = getFile();
		if (file.exists()) {
			return Optional.of(LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault()));
		}
		return Optional.empty();
	}

	@Override
	public void set(LocalDateTime time) {
		File file = getFile();
		if (!file.exists()) {
			try {
				new FileOutputStream(file).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		file.setLastModified(time.atZone(ZoneId.systemDefault()).toEpochSecond()*1000);
	}

	@Override
	public void clear() {
		File file = getFile();
		if (file.exists()) {
			file.delete();
		}
	}

	private File getFile() {
		return new File(directory, contentName);
	}

}
