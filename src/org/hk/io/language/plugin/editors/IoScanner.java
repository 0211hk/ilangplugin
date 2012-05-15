package org.hk.io.language.plugin.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class IoScanner extends RuleBasedScanner {

	private final static String KEY_WORDS[] = new String[] { "activate",
			"activeCoroCount", "and", "asString", "block", "break", "catch",
			"clone", "collectGarbage", "compileString", "continue", "do",
			"doFile", "doMessage", "doString", "else", "elseif", "exit", "for",
			"foreach", "forward", "getSlot", "getenv", "hasSlot", "if",
			"ifFalse", "ifNil", "ifNilEval", "ifTrue", "isActive", "isNil",
			"isResumable", "list", "message", "method", "or", "parent", "pass",
			"pause", "perform", "performWithArgList", "print", "println",
			"proto", "raise", "raiseResumable", "removeSlot", "resend",
			"resume", "return", "schedulerSleepSeconds", "self", "sender",
			"setSchedulerSleepSeconds", "setSlot", "shallowCopy", "slotNames",
			"super", "system", "then", "thisBlock", "thisContext", "call",
			"try", "type", "uniqueId", "updateSlot", "wait", "while", "write",
			"yield" };
	private final static String TYPES[] = new String[] { "Array", "AudioDevice",
		"AudioMixer", "Block", "Box", "Buffer", "CFunction", "CGI",
		"Color", "Curses", "DBM", "DNSResolver", "DOConnection", "DOProxy",
		"DOServer", "Date", "Directory", "Duration", "DynLib", "Error",
		"Exception", "FFT", "File", "Fnmatch", "Font", "Future", "GL",
		"GLE", "GLScissor", "GLU", "GLUCylinder", "GLUQuadric",
		"GLUSphere", "GLUT", "Host", "Image", "Importer", "LinkList",
		"List", "Lobby", "Locals", "MD5", "MP3Decoder", "MP3Encoder",
		"Map", "Message", "Movie", "Notification", "Number", "Object",
		"OpenGL", "Point", "Protos", "Regex", "SGML", "SGMLElement",
		"SGMLParser", "SQLite", "Server", "Sequence", "ShowMessage",
		"SleepyCat", "SleepyCatCursor", "Socket", "SocketManager", "Sound",
		"Soup", "Store", "String", "Tree", "UDPSender", "UPDReceiver",
		"URL", "User", "Warning", "WeakLink", "true", "false", "nil",
		"Random", "BigNum", "Sequence" };
	
	static class WordDetector implements IWordDetector {

		public final boolean isWordPart(char c) {
			return Character.isJavaIdentifierPart(c);
		}

		public final boolean isWordStart(char c) {
			return Character.isJavaIdentifierStart(c);
		}
	}
	
	public IoScanner(ColorManager manager) {
		IToken string = new Token(new TextAttribute(
				manager.getColor(IIoColorConstants.KEYWORD)));

		final WordRule w = new WordRule(new WordDetector(), string);
		for (final String k : KEY_WORDS) {
			w.addWord(k, string);
			w.addWord(k.toUpperCase(), string);
		}
		
		for (final String k : TYPES) {
			w.addWord(k, string);
			w.addWord(k.toUpperCase(), string);
		}
		
		IRule[] rules = new IRule[1];
		rules[0] = w;
		setRules(rules);
	}
}
