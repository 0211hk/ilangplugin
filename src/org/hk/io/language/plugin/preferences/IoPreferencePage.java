package org.hk.io.language.plugin.preferences;

import ilangplugin.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.hk.io.language.plugin.util.FileUtil;
import org.hk.io.language.plugin.util.SystemExecUtil;

public class IoPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public IoPreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("");
    }

    @Override
    public void createFieldEditors() {
        addField(new FileFieldEditor(PreferenceConstants.P_PATH, "&io path:", getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        String newVal = (String) event.getNewValue();
        if (event.getSource() instanceof FileFieldEditor) {
            String name = ((FileFieldEditor) event.getSource()).getPreferenceName();
            if (name.equals(PreferenceConstants.P_PATH)) {
                if (FileUtil.fileExists(newVal) && validPath(newVal)) {
                    setValid(true);
                    return;
                }
            }
        }
        setValid(false);
    }

    private boolean validPath(final String path) {
        String command = "\"%s\" print";
        String test = "test";
        try {
            final String[] returns = SystemExecUtil.execCommand(new String[] { path, "-e", String.format(command, test) });

            if (returns.length != 0 && returns[0].trim().equals(test)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}