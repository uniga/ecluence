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
package dk.uniga.ecluence.ui.preferences;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.preferences.PreferenceConstants;
import dk.uniga.ecluence.ui.Activator;
import dk.uniga.ecluence.ui.parts.ContentFormatter;
import dk.uniga.ecluence.ui.template.TemplateStore;

public final class TemplatePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private static final Logger log = LoggerFactory.getLogger(TemplatePreferencePage.class);

	private TreeViewer fileTreeViewer;

	private ListViewer placeholderViewer;

	private Button deleteButton;

	private Button selectButton;

	private Font boldFont;
	
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Label importLabel = new Label(composite, SWT.NONE | SWT.WRAP);
		importLabel.setText("Select a template to use for presenting Confluence pages\n"
				+ "instead of the built-in template.");
		importLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		
		fileTreeViewer = createFileTreeViewer(composite);
		fileTreeViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		
		Composite buttonComposite = createButtonComposite(composite);
		buttonComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Label placeholderLabel = new Label(composite, SWT.NONE);
		placeholderLabel.setText("Templates can contain the following placeholders.");
		placeholderLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		
		placeholderViewer = createPlaceholderViewer(composite);;
		placeholderViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		placeholderViewer.setInput(ContentFormatter.PLACEHOLDERS);
		
		LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), composite);
		boldFont = resourceManager.createFont(FontDescriptor.createFrom(composite.getFont()).setStyle(SWT.BOLD));
		
		updateList();
		return composite;
	}

	@Override
	public void dispose() {
		boldFont.dispose();
		super.dispose();
	}
	
	private Composite createButtonComposite(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);

		Button importButton = new Button(composite, SWT.PUSH);
		GridDataFactory.fillDefaults().applyTo(importButton);
		importButton.setText("Import...");
		importButton.addSelectionListener(widgetSelectedAdapter(e -> openImportDialog()));

		deleteButton = new Button(composite, SWT.PUSH);
		GridDataFactory.fillDefaults().applyTo(deleteButton);
		deleteButton.setText("Delete");
		deleteButton.addSelectionListener(widgetSelectedAdapter(e -> deleteSelected()));

		selectButton = new Button(composite, SWT.PUSH);
		GridDataFactory.fillDefaults().applyTo(selectButton);
		selectButton.setText("Select");
		selectButton.addSelectionListener(widgetSelectedAdapter(e -> useSelected()));
		
		return composite;
	}

	private void openImportDialog() {
		TemplateImportDialog dialog = new TemplateImportDialog(getShell());
		if (dialog.open() == Window.OK) {
			importTemplate(dialog.getTemplateUrl());
		}
	}

	private void deleteSelected() {
		ITreeSelection sel = fileTreeViewer.getStructuredSelection();
		if (!sel.isEmpty()) {
			for (Object n : (List<?>) sel.toList()) {
				Object o = ((TreeNode) n).getValue();
				log.debug("Deleting selected " + o);
				if (o instanceof TemplateFileset)
					delete((TemplateFileset) o);
			}
			updateList();
		}
	}

	private void delete(TemplateFileset fileset) {
		try {
			getTemplateStore().deleteFile(fileset.getTemplateFile());
			for (File file : fileset.getLinkedFiles()) {
				getTemplateStore().deleteFile(file);
			}
		} catch (IOException e) {
			Activator.handleError("Could not delete template file", e, true);
		}
		
	}
	
	private void useSelected() {
		ITreeSelection sel = fileTreeViewer.getStructuredSelection();
		if (!sel.isEmpty()) {
			for (Object n : (List<?>) sel.toList()) {
				Object o = ((TreeNode) n).getValue();
				log.debug("Use selected " + o);
				if (o instanceof TemplateFileset)
					use(Optional.of((TemplateFileset) o));
				else if (o instanceof BuiltinTemplate)
					use(Optional.empty());
			}
			updateList();
		}
	}

	private void use(Optional<TemplateFileset> o) {
		if (o.isPresent())
			getPreferenceStore().setValue(PreferenceConstants.PREFERENCE_SELECTED_TEMPLATE, o.get().getTemplateFile().getAbsolutePath());
		else
			getPreferenceStore().setToDefault(PreferenceConstants.PREFERENCE_SELECTED_TEMPLATE);
	}

	private boolean isSelectedTemplate(Object o) {
		if (o instanceof BuiltinTemplate)
			return getPreferenceStore().isDefault(PreferenceConstants.PREFERENCE_SELECTED_TEMPLATE);
		else if (o instanceof TemplateFileset) {
			return ((TemplateFileset) o).getTemplateFile().getAbsolutePath().equals(getPreferenceStore().getString(PreferenceConstants.PREFERENCE_SELECTED_TEMPLATE));
		}
		return false;
	}
	
	private TreeViewer createFileTreeViewer(Composite parent) {
		TreeViewer viewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new TreeNodeContentProvider());
		viewer.addSelectionChangedListener(e -> selectionChanged(e));
		
		TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
		viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(
				new FilenameLabelProvider()));
		viewerColumn.getColumn().setText("Filename");
		viewerColumn.getColumn().setWidth(150);
		
		TreeViewerColumn viewerColumn2 = new TreeViewerColumn(viewer, SWT.NONE);
		viewerColumn2.setLabelProvider(new DelegatingStyledCellLabelProvider(
				new FileModifiedLabelProvider(DateFormat.getDateTimeInstance())));
		viewerColumn2.getColumn().setText("Date");
		viewerColumn2.getColumn().setWidth(150);
		
		return viewer;
	}

	private void selectionChanged(SelectionChangedEvent e) {
		deleteButton.setEnabled(!e.getStructuredSelection().isEmpty());
	}

	private ListViewer createPlaceholderViewer(Composite parent) {
		ListViewer viewer = new ListViewer(parent);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});
		return viewer;
	}

	private void importTemplate(String templateUrl) {
		try {
			TemplateImporter importer = new TemplateImporter(getTemplateStore());
			importer.execute(templateUrl);
			updateList();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private TemplateStore getTemplateStore() {
		return Activator.getDefault().getTemplateStore();
	}

	private void updateList() {
		TemplateStore templateStore = getTemplateStore();
		Collection<File> templateFiles = templateStore.getFiles(TemplateFileset.getTemplateFileFilter());
		Collection<File> linkedFiles = templateStore.getFiles(TemplateFileset.getLinkedFileFilter());

		List<TreeNode> nodes = templateFiles.stream()
				.map(f -> { return new TemplateFileset(f, linkedFiles); })
				.map(this::buildTreeNode).collect(Collectors.toList());
		
		nodes.add(0, new TreeNode(new BuiltinTemplate()));
		
		fileTreeViewer.setInput(nodes.toArray(new TreeNode[0]));
	}

	private TreeNode buildTreeNode(TemplateFileset tf) {
		TreeNode node = new TreeNode(tf);
		node.setChildren((TreeNode[]) tf.getLinkedFiles().stream().map(f -> buildChildTreeNode(f, node))
				.toArray(TreeNode[]::new));
		return node;
	}

	private TreeNode buildChildTreeNode(File f, TreeNode parent) {
		TreeNode node = new TreeNode(f);
		node.setParent(parent);
		return node;
	}

	public class URLValidator implements IInputValidator {

		@Override
		public String isValid(String arg0) {
			try {
				new URL(arg0);
			} catch (MalformedURLException e) {
				return "The entered location is not a valid URL";
			}
			return null;
		}

	}

	private final class FilenameLabelProvider extends LabelProvider implements IStyledLabelProvider {
		@Override
		public StyledString getStyledText(Object element) {
			if (element instanceof TreeNode) {
				TreeNode node = (TreeNode) element;
				element = node.getValue();
			}
			if (element instanceof TemplateFileset) {
				TemplateFileset fileset = (TemplateFileset) element;
				getPreferenceStore().getString(PreferenceConstants.PREFERENCE_SELECTED_TEMPLATE);
				return style(TemplateFileset.getOriginalName(fileset.getTemplateFile()), isSelectedTemplate(fileset));
			}
			if (element instanceof BuiltinTemplate) {
				return style("Built-in template", isSelectedTemplate(element));
			}
			if (element instanceof File) {
				return style(TemplateFileset.getOriginalName((File) element), false);
			}
			return style(element.toString(), false);
		}

		private StyledString style(String text, boolean selected) {
			return new StyledString(text, new Styler() {
				@Override
				public void applyStyles(TextStyle style) {
					if (selected)
						style.font = boldFont;
				}
			});
		}
	}
	
    private final class FileModifiedLabelProvider extends LabelProvider implements IStyledLabelProvider {

        private DateFormat dateLabelFormat;

        public FileModifiedLabelProvider(DateFormat dateFormat) {
            dateLabelFormat = dateFormat;
        }

		@Override
		public StyledString getStyledText(Object element) {
			if (element instanceof TreeNode) {
				TreeNode node = (TreeNode) element;
				element = node.getValue();
			}
			if (element instanceof TemplateFileset) {
				TemplateFileset fileset = (TemplateFileset) element;
				return styledDate(fileset.getTemplateFile());
			}
			if (element instanceof File) {
				return styledDate((File) element);
            }
            return new StyledString();
		}

		private StyledString styledDate(File file) {
            long lastModified = file.lastModified();
            return new StyledString(dateLabelFormat.format(new Date(lastModified)));
		}
    }
    
    private final class BuiltinTemplate {}
}
