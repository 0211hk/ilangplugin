package org.hk.io.language.plugin.launcher;

import ilangplugin.Activator;

import java.io.File;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class IoLauncherTab extends AbstractLaunchConfigurationTab {

    private Text textFile;
    static final String ATTR_SELECT_IO_FILE = "select_io_file";
    private boolean isValid = true;

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        createFileGroup(composite);
        setControl(composite);
    }

    private void createFileGroup(final Composite composite) {
        Group projectGroup = new Group(composite, SWT.NULL);
        projectGroup.setText("Io File");
        projectGroup.setLayout(new GridLayout(2, false));
        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        textFile = new Text(projectGroup, SWT.BORDER);
        textFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textFile.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
            }
        });
        createBrowseFileButton(textFile, composite);
    }

    private Button createBrowseFileButton(final Text text, Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                FileDialog dialog = new FileDialog(text.getShell(), SWT.SINGLE);
                File file = new File(text.getText());
                dialog.setFilterPath(file.getParent());
                dialog.setFileName(file.getName());
                String result = dialog.open();
                if (result != null) {
                    text.setText(result);
                }
            }
        });
        return button;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            String project = configuration.getAttribute(ATTR_SELECT_IO_FILE, "");
            textFile.setText(project);
        } catch (Exception ex) {
            Activator.logException(ex);
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        String ioFile = textFile.getText();
        if (ioFile.length() == 0) {
            setErrorMessage("io file not found.");
            isValid = false;
            return;
        }

        if (!ioFile.endsWith(".io")) {
            setErrorMessage("not io file");
            isValid = false;
            return;
        }

        // TODO: valid preference io path
        // TODO: valid file path is io file
        configuration.setAttribute(ATTR_SELECT_IO_FILE, ioFile);
        setErrorMessage(null);
        isValid = true;
    }

    @Override
    public String getName() {
        return "Main";
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        return isValid;
    }

}
