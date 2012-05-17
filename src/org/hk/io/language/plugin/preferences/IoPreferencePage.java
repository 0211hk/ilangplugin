package org.hk.io.language.plugin.preferences;

import java.io.File;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import ilangplugin.Activator;

public class IoPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public IoPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("");
	}
	
	public void createFieldEditors() {
		addField(new FileFieldEditor(PreferenceConstants.P_PATH, "&io path:",
				getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench) {
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		String newVal = (String)event.getNewValue();
		if (event.getSource() instanceof FileFieldEditor) {
			String name = ((FileFieldEditor)event.getSource()).getPreferenceName();
			if (name.equals(PreferenceConstants.P_PATH)){
				File f = new File(newVal);
				if (f.exists() && f.isFile()) {
					setValid(true);
					return;
				}
			}
		}
		setValid(false);
	}
}