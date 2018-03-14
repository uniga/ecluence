package dk.uniga.ecluence.ui;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.ui.parts.EditorSelectionAdapter;

public final class ExtensionEditorSelectionAdapterProvider {

	private static final Logger log = LoggerFactory.getLogger(ExtensionEditorSelectionAdapterProvider.class);
	
	private final Collection<EditorSelectionAdapter> editorSelectionAdapters = new HashSet<>();
	
	public void execute(IExtensionRegistry registry) {
        IConfigurationElement[] elements = registry.getConfigurationElementsFor("dk.uniga.ecluence.ui.editorSelectionAdapter");
        for (IConfigurationElement element : elements) {
			log.debug("{}: {}", element.getContributor(), element.getName());
			if ("editorSelectionAdapter".equals(element.getName())) {
				try {
					editorSelectionAdapters.add(readEditorSelectionAdapter(element));
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Collection<EditorSelectionAdapter> getEditorSelectionAdapters() {
		return editorSelectionAdapters;
	}

	private EditorSelectionAdapter readEditorSelectionAdapter(IConfigurationElement elem) throws CoreException {
		String className = elem.getAttribute("className");
		log.debug("read editor selection adapter {}", className);
		EditorSelectionAdapter adapter = (EditorSelectionAdapter) elem.createExecutableExtension("className");
		return adapter;
	}
}
