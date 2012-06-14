package org.hk.io.language.plugin.launcher;

import ilangplugin.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class IoLauncherShortcut implements ILaunchShortcut {

    @Override
    public void launch(ISelection selection, String mode) {
        if (selection != null && selection instanceof IStructuredSelection && !selection.isEmpty()) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            if (obj instanceof IResource) {
                String path = ((IResource) obj).getLocation().toOSString();
                IProject targetProject = ((IResource) obj).getProject();
                launchApplication(targetProject, mode, path);
            }
        }
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        if (mode.equals(ILaunchManager.RUN_MODE)) {
            IEditorInput input = editor.getEditorInput();
            if (input instanceof IFileEditorInput) {
                String path = ((IFileEditorInput) input).getFile().getLocation().toOSString();
                IProject targetProject = ((IFileEditorInput) input).getFile().getProject();
                launchApplication(targetProject, mode, path);
            }
        }
    }

    private void launchApplication(IProject targetProject, String mode, String path) {
        try {
            ILaunchConfiguration config = getLaunchConfiguration(path, targetProject);
            DebugUITools.launch(config, mode);
        } catch (Exception ex) {
            Activator.logException(ex);
        }
    }

    private ILaunchConfiguration getLaunchConfiguration(String path, IProject project) throws CoreException {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfiguration[] configs = manager.getLaunchConfigurations();

        for (int i = 0; i < configs.length; i++) {
            String value = configs[i].getAttribute(IoLauncherTab.ATTR_SELECT_IO_FILE, "");
            if (value.equals(path)) {
                return configs[i];
            }
        }

        ILaunchConfigurationType type = manager.getLaunchConfigurationType("ilangplugin.launchConfigurationType");
        ILaunchConfigurationWorkingCopy wc = type.newInstance(null, manager.generateLaunchConfigurationName(project.getName()));

        wc.setAttribute(IoLauncherTab.ATTR_SELECT_IO_FILE, path);
        wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
        return wc.doSave();
    }

}
