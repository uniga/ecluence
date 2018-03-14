package dk.uniga.ecluence.ui.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import dk.uniga.ecluence.core.cache.ContentCacheProvider;
import dk.uniga.ecluence.ui.Activator;

public class MatcherPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private TableViewer matcherViewer;
	private TableViewer cachesViewer;

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Label importLabel = new Label(composite, SWT.NONE | SWT.WRAP);
		importLabel.setText("The following content matchers have been registered.");
		importLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		
		matcherViewer = createMatcherViewer(composite);
		matcherViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		matcherViewer.setInput(dk.uniga.ecluence.core.Activator.getDefault().getContentMatcherRegistry().getContentMatchers());
		
		Label cachesLabel = new Label(composite, SWT.NONE | SWT.WRAP);
		cachesLabel.setText("The following content caches have been registered.");
		cachesLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		
		cachesViewer = createCachesViewer(composite);
		cachesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		cachesViewer.setInput(dk.uniga.ecluence.core.Activator.getDefault().getContentCacheRegistry().getProviders());
		
		return composite;
	}

	private TableViewer createCachesViewer(Composite parent) {
		TableViewer viewer = new TableViewer(parent);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				ContentCacheProvider provider = (ContentCacheProvider) element;
				return String.format("%s (%s)", provider.getName(), provider.getCacheContentType());
			}
		});
		return viewer;
	}

	private TableViewer createMatcherViewer(Composite parent) {
		TableViewer viewer = new TableViewer(parent);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}
		});
		return viewer;
	}


}
