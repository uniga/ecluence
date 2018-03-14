package dk.uniga.ecluence.core.matching;

/**
 * Source of selections to which listeners can register to get notified when the
 * selection change.
 */
public interface SelectionSource {

	void setListener(SelectionListener listener);
}