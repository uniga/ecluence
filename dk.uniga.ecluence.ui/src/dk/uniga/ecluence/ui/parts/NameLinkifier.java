package dk.uniga.ecluence.ui.parts;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Shell;

import dk.uniga.ecluence.core.PageContent;
import dk.uniga.ecluence.core.PageContentProcessor;

public final class NameLinkifier implements PageContentProcessor, LinkHandler {

	private final Shell shell;

	public NameLinkifier(Shell shell) {
		this.shell = Objects.requireNonNull(shell);
	}

	@Override
	public void process(PageContent content) {
		// This is a demonstration
		// TODO implement a meaningful way of identifying resources
		String modified = StringUtils.replace(content.getContent(), "ServiceEndpointProvider", linkify("ServiceEndpointProvider"));
		content.setContent(modified);
	}
	
	private String linkify(String text) {
		return String.format("<a href=\"resource:%s\">%s</a>", text, text);
	}

	@Override
	public boolean handle(String location) {
		return handleResourceLink(location);
	}
	
	private boolean handleResourceLink(String location) {
		String resource = LinkMatcher.matchResource(location);
		if (resource != null) {
			WorkspaceTools.openResource(resource, shell);
			return true;
		}
		return false;
	}
	

}
