package dk.uniga.ecluence.ui.parts;

public interface LinkHandler {

	/**
	 * Handles location if possible, returning <code>true</code> if successful and
	 * no other should handle this location.
	 * 
	 * @param location
	 * @return
	 */
	boolean handle(String location);

}
