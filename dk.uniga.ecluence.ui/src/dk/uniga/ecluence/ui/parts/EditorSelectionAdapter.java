package dk.uniga.ecluence.ui.parts;

import org.eclipse.ui.IEditorPart;

import dk.uniga.ecluence.core.matching.SelectionSource;

public interface EditorSelectionAdapter extends SelectionSource {

	void editorActivated(IEditorPart editor);

	void editorDeactivated(IEditorPart editor);
}
