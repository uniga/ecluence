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
