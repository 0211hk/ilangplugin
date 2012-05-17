package org.hk.io.language.plugin.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class IoConfiguration extends SourceViewerConfiguration {
	private IoDoubleClickStrategy doubleClickStrategy;
	private ColorManager colorManager;

	private IoScanner ioScanner;

	public IoConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				IoPartitionScanner.COMMENT, IoPartitionScanner.STRING,
				IoPartitionScanner.OPERATOR };
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new IoDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected IoScanner getIoScanner() {
		if (ioScanner == null) {
			ioScanner = new IoScanner(colorManager);
			ioScanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(IIoColorConstants.KEYWORD))));
		}
		return ioScanner;
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getIoScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(
						colorManager.getColor(IIoColorConstants.COMMENT)));
		reconciler.setDamager(ndr, IoPartitionScanner.COMMENT);
		reconciler.setRepairer(ndr, IoPartitionScanner.COMMENT);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(
				colorManager.getColor(IIoColorConstants.OPERATOR)));
		reconciler.setDamager(ndr, IoPartitionScanner.OPERATOR);
		reconciler.setRepairer(ndr, IoPartitionScanner.OPERATOR);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(
				colorManager.getColor(IIoColorConstants.STRING)));
		reconciler.setDamager(ndr, IoPartitionScanner.STRING);
		reconciler.setRepairer(ndr, IoPartitionScanner.STRING);

		return reconciler;
	}

}