/*******************************************************************************
 * Copyright (c) 2017 Uniga.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mikkel R. Jakobsen - initial API and implementation
 *******************************************************************************/
package dk.uniga.ecluence.ui.parts;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.matching.SelectionListener;
import dk.uniga.ecluence.core.matching.SelectionSource;
import dk.uniga.ecluence.ui.Activator;
import dk.uniga.ecluence.ui.EditorSelectionAdapterRegistry;
import dk.uniga.ecluence.ui.EditorSelectionAdapterRegistryListener;

/**
 * Source of selections adapted from editor selections that can either originate
 * from this class itself when editor input changes or the editor get activated, or
 * it can originate from a editor selection adapter, which can be contributed via 
 * {@link EditorSelectionAdapterRegistry}.
 * 
 * Contains code extracted from JDT's PackageExplorerPart for getting the editor
 * selection.
 */
public final class LinkedEditorSelectionAdapter extends BaseSelectionSource {

	private static final Logger log = LoggerFactory.getLogger(LinkedEditorSelectionAdapter.class);

	private final SelectionListener parentAdapter;

	private final IPartListener2 fLinkWithEditorListener = createPartListener();

	private final Collection<EditorSelectionAdapter> adapters = new HashSet<>();
	
	private final SelectionListener selectionListener = new SelectionListener() {
		@Override
		public void selectionChanged(SelectionSource source, Object selection) {
			LinkedEditorSelectionAdapter.this.selectionChanged(source, selection);
		}
	};

	private EditorSelectionAdapterRegistryListener registryListener = new EditorSelectionAdapterRegistryListener() {
		@Override
		public void editorSelectionAdapterAdded(EditorSelectionAdapter adapter) {
			addAdapter(adapter);
		}
	};
	
	/**
	 * Create a new EditorSelectionAdapter that passes selections to the given
	 * SelectionListener.
	 * 
	 * @param selectionAdapter
	 */
	public LinkedEditorSelectionAdapter(SelectionListener selectionAdapter) {
		this.parentAdapter = selectionAdapter;
		EditorSelectionAdapterRegistry adapterRegistry = Activator.getDefault().getEditorSelectionAdapterRegistry();
		adapterRegistry.addListener(registryListener);
		adapterRegistry.getAdapters().forEach(adapter -> addAdapter(adapter));
		setLinkingEnabled(true);
	}

	private void addAdapter(EditorSelectionAdapter adapter) {
		adapter.setListener(selectionListener);
		adapters.add(adapter);
	}

	public void disable() {
		setLinkingEnabled(false);
	}

	private void setLinkingEnabled(boolean enabled) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (enabled) {
			page.addPartListener(fLinkWithEditorListener);

			IEditorPart editor = page.getActiveEditor();
			if (editor != null)
				editorActivated(editor);
		} else {
			page.removePartListener(fLinkWithEditorListener);
		}
	}

	private IPartListener2 createPartListener() {
		return new IPartListener2() {
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
				log.debug("partDeactivated {}", partRef);
				if (partRef instanceof IEditorReference) {
					editorDeactivated(((IEditorReference) partRef).getEditor(true));
				}
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
				log.debug("partInputChanged {}", partRef);
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (partRef instanceof IEditorReference && activePage != null
						&& activePage.getActivePartReference() == partRef) {
					editorActivated(((IEditorReference) partRef).getEditor(true));
				}
			}

			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				log.debug("partActivated {}", partRef);
				if (partRef instanceof IEditorReference) {
					editorActivated(((IEditorReference) partRef).getEditor(true));
				}
			}

		};
	}

	void editorActivated(IEditorPart editor) {
		log.debug("editorActivated {}", editor);
		adapters.forEach(adapter -> adapter.editorActivated(editor));

		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput == null)
			return;
		Object input = getInputFromEditor(editorInput);
		if (input == null)
			return;
		activateOnInput(input);
	}

	void editorDeactivated(IEditorPart editor) {
		log.debug("editorDeactivated {}", editor);
		adapters.forEach(adapter -> adapter.editorDeactivated(editor));
	}

	private void activateOnInput(Object input) {
		log.debug("activateOnInput {}", input.getClass());
		selectionChanged(this, input);
	}

	private void selectionChanged(SelectionSource source, Object input) {
		parentAdapter.selectionChanged(source, input);
	}

	private Object getInputFromEditor(IEditorInput editorInput) {
		Object input = JavaUI.getEditorInputJavaElement(editorInput);
		if (input instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit) input;
			if (!cu.getJavaProject().isOnClasspath(cu)) { // test needed for Java files in non-source folders (bug
															// 207839)
				input = cu.getResource();
			}
		}
		if (input == null) {
			input = editorInput.getAdapter(IFile.class);
		}
		return input;
	}
}
