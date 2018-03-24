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

import javax.inject.Named;

import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.matching.SelectionListener;
import dk.uniga.ecluence.core.matching.SelectionSource;

public abstract class WorkspaceSelectionAdapter extends BaseSelectionSource implements SelectionListener {

	private static final Logger log = LoggerFactory.getLogger(WorkspaceSelectionAdapter.class);

	public WorkspaceSelectionAdapter() {
	}
	
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) ISelection s) {
		if (s==null || s.isEmpty())
			return;

		log.debug("setSelection {}", s.getClass());
		
		if (s instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) s;
			if (iss.size() == 1)
				setSelection(iss.getFirstElement());
			else
				setSelection(iss.toArray());
		}
		if (s instanceof TextSelection) {
			TextSelection ts = (TextSelection) s;
			handleTextSelection(ts);
		}
	}

	private void handleTextSelection(TextSelection ts) {
//		selectionChanged(ts);
	}

	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {
		if (o instanceof ISelection) // Already captured
			return;
		notifySelectionChanged(this, o);
	}
	
	public void selectionChanged(SelectionSource source, Object o) {
		if (o == null)
			return;
		log.debug("selectionChanged {}", o.getClass());
		notifySelectionChanged(source, o);
	}

	abstract void handleException(QueryException e);
	
}
