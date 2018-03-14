package dk.uniga.ecluence.core.matching;

/**
 * Listener to selection changes.
 */
public interface SelectionListener {
	
	/**
	 * Notifies this listener that the selection from the given source has changed.
	 * 
	 * @param source
	 * @param selection
	 */
	public void selectionChanged(SelectionSource source, Object selection);
}
