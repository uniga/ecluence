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
package dk.uniga.ecluence.core.cache;

import dk.uniga.ecluence.core.QueryException;

public class ExpandingContentException extends QueryException {

	private static final long serialVersionUID = -5673116283490091922L;
	
	private String contentBeanId;

	public ExpandingContentException(String message, Throwable cause, String contentBeanId) {
		super(message, cause);
		this.contentBeanId = contentBeanId;
	}

	public String getContentBeanId() {
		return contentBeanId;
	}
}
