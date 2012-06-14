package org.hk.io.language.plugin.launcher;

import ilangplugin.Activator;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.jface.preference.IPreferenceStore;
import org.hk.io.language.plugin.preferences.PreferenceConstants;

public class IoLauncher implements ILaunchConfigurationDelegate2 {

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        IPreferenceStore s = Activator.getDefault().getPreferenceStore();
        String path = s.getString(PreferenceConstants.P_PATH);
        String targetIoFile = configuration.getAttribute(IoLauncherTab.ATTR_SELECT_IO_FILE, "");
        if (mode.equals(ILaunchManager.RUN_MODE)) {
            String cmd = path + " " + targetIoFile;
            Process process;
            try {
                process = Runtime.getRuntime().exec(cmd);
                DebugPlugin.newProcess(launch, process, "");
            } catch (IOException e) {
                Activator.logException(e);
            }
        }
    }

    @Override
    public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
        return new Launch(configuration, mode, null);
    }

    @Override
    public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        return false;
    }

    @Override
    public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        return true;
    }

    @Override
    public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        return true;
    }

}
