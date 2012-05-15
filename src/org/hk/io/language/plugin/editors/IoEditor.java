package org.hk.io.language.plugin.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class IoEditor extends TextEditor {

	private ColorManager colorManager;

	public IoEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new IoConfiguration(colorManager));
		setDocumentProvider(new IoDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
