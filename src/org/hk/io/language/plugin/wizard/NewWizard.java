package org.hk.io.language.plugin.wizard;

import ilangplugin.builder.IoBuilder;
import ilangplugin.builder.IoNature;

import java.net.URI;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.hk.io.language.plugin.util.Arrays;

public class NewWizard extends Wizard implements INewWizard {

    private WizardNewProjectCreationPage projectPage;

    public NewWizard() {

    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public boolean performFinish() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject project = root.getProject(projectPage.getProjectName());
        try {
            createNature(workspace, project);
            createBuilder(project);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void createNature(final IWorkspace workspace, final IProject project) throws CoreException {
        URI location = null;
        if (!projectPage.useDefaults()) {
            location = projectPage.getLocationURI();
        }
        final IProjectDescription description = workspace.newProjectDescription(projectPage.getProjectName());
        String[] natureIds = new String[] { IoNature.NATURE_ID };
        description.setNatureIds(natureIds);
        description.setLocationURI(location);
        project.create(description, null);
        project.open(null);
    }

    private void createBuilder(final IProject project) throws CoreException {
        IProjectDescription description = project.getDescription();
        ICommand command = description.newCommand();
        command.setBuilderName(IoBuilder.BUILDER_ID);
        description.setBuildSpec(Arrays.push(description.getBuildSpec(), command, ICommand.class));
        project.setDescription(description, null);
    }

    @Override
    public void addPages() {
        projectPage = new WizardNewProjectCreationPage("");
        projectPage.setDescription("Create a new io Project.");
        projectPage.setTitle("New io Project");
        addPage(projectPage);
    }

}