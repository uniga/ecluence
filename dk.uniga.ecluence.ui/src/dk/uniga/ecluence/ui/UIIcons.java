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
package dk.uniga.ecluence.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;

public class UIIcons {
	
	/** Refresh icon */
	public final static ImageDescriptor ELCL16_REFRESH;
	
	/** base URL */
	public final static URL base;

	static {
		base = init();
		ELCL16_REFRESH = map("elcl16/refresh.png"); //$NON-NLS-1$

	}
	
	private static ImageDescriptor map(final String icon) {
		if (base != null)
			try {
				return ImageDescriptor.createFromURL(new URL(base, icon));
			} catch (MalformedURLException mux) {
				Activator.logError("Error loading plugin image", mux);
			}
		return ImageDescriptor.getMissingImageDescriptor();
	}

	private static URL init() {
		try {
			return new URL(Activator.getDefault().getBundle().getEntry("/"), //$NON-NLS-1$
					"icons/"); //$NON-NLS-1$
		} catch (MalformedURLException mux) {
			Activator.logError("Error determining icon base url", mux);
			return null;
		}
	}

	/**
	 * Get the image for the given descriptor from the resource manager which
	 * handles disposal of the image when the resource manager itself is
	 * disposed.
	 *
	 * @param resourceManager
	 *            {code ResourceManager} managing the image resources
	 * @param descriptor
	 *            object describing an image
	 * @return the image for the given descriptor
	 */
	public static Image getImage(ResourceManager resourceManager,
			ImageDescriptor descriptor) {
		return (Image) resourceManager.get(descriptor);
	}

}
