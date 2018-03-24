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
package dk.uniga.ecluence.ui.preferences;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a template and its associated files with filenames that ensures a
 * relation between them using a common UUID (e.g.,
 * 0db6ee43-f98f-4f8c-b533-faed287a8666).
 * 
 */
public class TemplateFileset {

	private static final String UUID_PATTERN = "[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}";

	private static final String FILE_PATTERN = UUID_PATTERN + "(-template)?_(.+)";

	private static final String TEMPLATE_PATTERN = UUID_PATTERN + "-template_.+";

	private static final String LINKED_PATTERN = UUID_PATTERN + "_.+";

	public static FileFilter getFileFilter() {
		return (FileFilter) f -> f.getName().matches(FILE_PATTERN);
	}

	public static FileFilter getTemplateFileFilter() {
		return (FileFilter) f -> f.getName().matches(TEMPLATE_PATTERN);
	}

	public static FileFilter getLinkedFileFilter() {
		return (FileFilter) f -> f.getName().matches(LINKED_PATTERN);
	}

	public static String getOriginalName(File file) {
		Matcher matcher = Pattern.compile(FILE_PATTERN).matcher(file.getName());
		matcher.find();
		return matcher.group(2);
	}
	
	private String uuid;

	private File templateFile;
	private Set<File> linkedFiles = new HashSet<>();

	public TemplateFileset() {
		uuid = UUID.randomUUID().toString();
	}

	public TemplateFileset(File templateFile, Collection<File> files) {
		if (getTemplateFileFilter().accept(templateFile)) {
			this.templateFile = templateFile;
			this.uuid = templateFile.getName().substring(0, 36);
			linkedFiles = files.stream().filter(f -> matches(f)).collect(Collectors.toSet());
		}
		else
			throw new IllegalArgumentException("Filename patten not allowed.");
	}

	public boolean matches(File f) {
		if (getFileFilter().accept(f))
			return f.getName().startsWith(uuid);
		return false;
	}

	public String getTemplateFilename(String name) {
		return String.format("%s-template_%s", uuid, name);
	}

	public String getLinkedFilename(String name) {
		return String.format("%s_%s", uuid, name);
	}

	public void add(File file) {
		if (getTemplateFileFilter().accept(file))
			templateFile = file;
		else if (getLinkedFileFilter().accept(file))
			linkedFiles.add(file);
		else
			throw new IllegalArgumentException("Filename patten not allowed.");
	}

	public File getTemplateFile() {
		return templateFile;
	}

	public Collection<File> getLinkedFiles() {
		return linkedFiles;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + uuid + "]";
	}
}
