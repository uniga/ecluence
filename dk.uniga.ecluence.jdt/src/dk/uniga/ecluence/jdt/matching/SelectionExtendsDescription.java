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
package dk.uniga.ecluence.jdt.matching;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import dk.uniga.ecluence.core.matching.SelectionDescription;

public final class SelectionExtendsDescription implements SelectionDescription {
	
	private final String name;
	private final String superClassName;

	SelectionExtendsDescription(String name, String superClassName) {
		this.name = Objects.requireNonNull(name, "name");
		this.superClassName = Objects.requireNonNull(superClassName, "superClassName");
	}

	@Override
	public String getDefaultText() {
		return String.format("%s extends %s", name, superClassName);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SelectionExtendsDescription) {
			SelectionExtendsDescription other = (SelectionExtendsDescription) obj;
			return name.equals(other.name) && superClassName.equals(other.superClassName);
		}
		return false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name)
				.append("superClassName", superClassName).build();
	}
}
