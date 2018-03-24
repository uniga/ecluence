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
package dk.uniga.ecluence.ui.parts;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;
import org.eclipse.ui.progress.IProgressService;

@SuppressWarnings("restriction")
public class WorkspaceTools {

	/*
	 * Links
	 * 
	 * http://help.eclipse.org/oxygen/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fsearch%2Fui%2Fclass-use%2FISearchQuery.html&anchor=org.eclipse.search.ui
	 * http://help.eclipse.org/oxygen/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Findex.html&org/eclipse/ui/actions/OpenResourceAction.html
	 * 
	 * https://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fui%2FJavaUI.html&anchor=createTypeDialog-org.eclipse.swt.widgets.Shell-org.eclipse.jface.operation.IRunnableContext-org.eclipse.jdt.core.search.IJavaSearchScope-int-boolean-java.lang.String-org.eclipse.jdt.ui.dialogs.TypeSelectionExtension-
	 * https://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fui%2Fdialogs%2Fpackage-summary.html
	 * org.eclipse.jdt.ui.OpenAction
	 */
	static void search(String searchText) {
		IProgressService context = PlatformUI.getWorkbench()
                .getProgressService();
		IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getAllWorkingSets();
		String[] fileNamePatterns = new String[] { "*" };
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(workingSets, fileNamePatterns, false);
		FileSearchQuery query = new FileSearchQuery(searchText, false, true, scope);
		NewSearchUI.runQueryInForeground(context, query);
	}
	
	static void openResource(String searchText, Shell shell) {
		IContainer rootElement = ResourcesPlugin.getWorkspace().getRoot();
		OpenResourceDialog dialog = new OpenResourceDialog(shell, rootElement, IResource.FILE);
		dialog.setInitialPattern(searchText);
		dialog.open();
	}
	
	public static void openEditor(File file, IWorkbenchPage page) {
//				return IDE.openEditor(page, ifile,
//						OpenStrategy.activateOnOpen());
	}

}
