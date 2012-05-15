package org.hk.io.language.plugin.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class IoWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
