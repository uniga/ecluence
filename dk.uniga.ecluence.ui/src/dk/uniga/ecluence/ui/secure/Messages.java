package dk.uniga.ecluence.ui.secure;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "dk.uniga.ecluence.ui.secure.messages"; //$NON-NLS-1$
	public static String CustomPromptDialog_informationAbout;
	public static String CustomPromptDialog_provideInformationFor;
	public static String LoginDialog_changeStoredCredentials;
	public static String LoginDialog_login;
	public static String LoginDialog_password;
	public static String LoginDialog_repository;
	public static String LoginDialog_storeInSecureStore;
	public static String LoginDialog_user;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
