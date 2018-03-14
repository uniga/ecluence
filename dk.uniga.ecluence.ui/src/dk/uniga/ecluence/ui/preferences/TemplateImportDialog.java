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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TemplateImportDialog extends Dialog {
	
	private Browser browser;
	private Text location;
	private String templateUrl;

	public TemplateImportDialog(Shell shell) {
		super(shell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        Composite header = new Composite(container, SWT.NONE);
        header.setLayout(new GridLayout(3, false));
        header.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        
		Label locationLabel = new Label(header, SWT.NONE);
		locationLabel
				.setText("Location:");

        location = new Text(header, SWT.BORDER | SWT.SINGLE);
        location.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER)
		.grab(true, false).applyTo(location);
       
        Button loadButton = new Button(header, SWT.PUSH);
        loadButton.setLayoutData(GridDataFactory.swtDefaults().hint(100, SWT.DEFAULT).create());
        loadButton.setText("Load");	
        loadButton.addSelectionListener(widgetSelectedAdapter(e -> { loadBrowser(); }));
        location.addModifyListener((e) -> { enableButton(loadButton); });
        
        browser = new Browser(container, SWT.BORDER);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
        return container;
    }
	
	private void enableButton(Button button) {
		button.setEnabled(URLValidator.validate(location.getText()));
	}

	private void loadBrowser() {
		browser.setUrl(location.getText());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Import", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
	}

	@Override
	protected void okPressed() {
		templateUrl = location.getText();
		super.okPressed();
	}
	
	public String getTemplateUrl() {
		return templateUrl;
	}
	
	@Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Import template");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 500);
    }

}
