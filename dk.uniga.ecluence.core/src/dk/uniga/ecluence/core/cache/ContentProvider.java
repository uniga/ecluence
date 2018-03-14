package dk.uniga.ecluence.core.cache;

import java.util.Collection;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.QueryException;

public interface ContentProvider {

	Collection<ContentBean> getPages() throws QueryException;
}
