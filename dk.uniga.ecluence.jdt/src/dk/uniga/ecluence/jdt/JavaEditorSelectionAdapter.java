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
package dk.uniga.ecluence.jdt;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.folding.IJavaFoldingStructureProvider;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.ui.parts.BaseSelectionSource;
import dk.uniga.ecluence.ui.parts.EditorSelectionAdapter;

/**
 * Dynamically listens to caret movement events from the active Java editor and
 * adapts the caret position to a selection that can be matched to content.
 */
public class JavaEditorSelectionAdapter extends BaseSelectionSource implements EditorSelectionAdapter {

	private static final Logger log = LoggerFactory.getLogger(JavaEditorSelectionAdapter.class);

	private IEditorPart editor;

	private CaretListener caretListener = new CaretListener() {
		@Override
		public void caretMoved(CaretEvent arg0) {
			updatedPosition(widgetToModel(arg0.caretOffset));
		}
	};

	public void editorActivated(IEditorPart editor) {
		log.debug("editorActivated {} {}", editor, editor.getEditorInput());
		if (isJavaEditor(editor) && !editor.equals(this.editor)) {
			registerCaretListener(editor);
			this.editor = editor;
		}
	}

	public void editorDeactivated(IEditorPart editor) {
		log.debug("editorDeactivated {} {}", editor, editor.getEditorInput());
		if (editor.equals(this.editor)) {
			unregisterCaretListener(this.editor);
			this.editor = null;
		}
	}

	private void registerCaretListener(IEditorPart editor) {
		Control adapter = editor.getAdapter(Control.class);
		if (adapter instanceof StyledText) {
			log.debug("registerCaretListener");
			((StyledText) adapter).addCaretListener(caretListener);
		}
	}

	private void unregisterCaretListener(IEditorPart editor) {
		Control adapter = editor.getAdapter(Control.class);
		if (adapter instanceof StyledText) {
			log.debug("unregisterCaretListener");
			((StyledText) adapter).removeCaretListener(caretListener);
		}
	}

	/**
	 * Returns the offset in the edited file that corresponds to the caret position,
	 * correcting for example for possible folding in the editors source code
	 * viewer.
	 * 
	 * @param position
	 * @return
	 */
	protected int widgetToModel(int position) {
		ITextOperationTarget target = (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
		if (target != null && target instanceof TextViewer) {
			int modelOffset = ((TextViewer) target).widgetOffset2ModelOffset(position);
			log.debug("widgetToModel({})*: {}", position, modelOffset);
			return modelOffset;
		}
		log.debug("widgetToModel({}) : {}", position, position);
		return position;
	}

	private boolean isJavaEditor(IEditorPart editor) {
		return editor.getAdapter(IJavaFoldingStructureProvider.class) != null;
	}

	protected void updatedPosition(int position) {
		Object input = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
		if (input instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit) input;
			try {
				IJavaElement element = cu.getElementAt(position);
				if (element != null)
					notifySelectionChanged(this, element);
				log.debug("updatedPosition({}) found element: {} {}", element.getPath(), element.getElementName());
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

}
