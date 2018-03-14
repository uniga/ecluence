/*******************************************************************************
 * Copyright (c) 2017 Uniga.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mikkel R. Jakobsen - initial API and implementation
 *******************************************************************************/
package dk.uniga.ecluence.jdt.matching;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import dk.uniga.ecluence.core.matching.SelectionDescription;

public final class SelectionOfTypeDescription implements SelectionDescription {
	
	private final String name;
	private final String type;

	SelectionOfTypeDescription(String name, String type) {
		this.name = Objects.requireNonNull(name, "name");
		this.type = Objects.requireNonNull(type, "type");
	}
	
	@Override
	public String getDefaultText() {
		return String.format("[%s] %s", type, name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SelectionOfTypeDescription) {
			SelectionOfTypeDescription other = (SelectionOfTypeDescription) obj;
			return name.equals(other.name) && type.equals(other.type);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name).append("type", type)
				.build();
	}
}