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

import dk.uniga.ecluence.core.matching.SelectionListener;
import dk.uniga.ecluence.core.matching.SelectionSource;

public class BaseSelectionSource implements SelectionSource {

	private SelectionListener listener;

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.ui.parts.SelectionSource#setListener(dk.uniga.ecluence.ui.parts.SelectionListener)
	 */
	@Override
	public void setListener(SelectionListener listener) {
		this.listener = listener;
	}
	
	protected void notifySelectionChanged(SelectionSource source, Object selection) {
		if (selection != null)
			listener.selectionChanged(source, selection);
	}

}
