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

import java.util.Arrays;
import java.util.Optional;

public enum CacheContentType {
	
	Index("index"), 
	
	Context("context");
	
	private String id;

	CacheContentType(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public static Optional<CacheContentType> fromId(String id) {
		return Arrays.stream(values()).filter((type) -> type.id.equals(id)).findFirst();
	}
}
