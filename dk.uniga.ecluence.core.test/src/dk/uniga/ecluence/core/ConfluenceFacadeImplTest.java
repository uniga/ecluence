package dk.uniga.ecluence.core;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import dk.uniga.ecluence.core.cache.ContentCacheProvider;

public class ConfluenceFacadeImplTest {

	private Executor executor;
	private IPath stateLocation = new Path("");
	private Consumer<ContentCacheProvider> cacheAddedCallback;
	
	@Test(expected = NullPointerException.class)
	public void testNullExecutorFails() throws Exception {
		new ConfluenceFacadeImpl(null, stateLocation, cacheAddedCallback);
	}

	@Test(expected = NullPointerException.class)
	public void testNullStateLocationFails() throws Exception {
		new ConfluenceFacadeImpl(executor, null, cacheAddedCallback);
	}

	@Test(expected = NullPointerException.class)
	public void testNullCallbackFails() throws Exception {
		new ConfluenceFacadeImpl(executor, stateLocation, null);
	}

}
