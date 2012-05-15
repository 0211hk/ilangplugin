package org.hk.io.language.plugin.editors;

import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.hk.io.language.plugin.util.Arrays;

public class IoPartitionScanner extends RuleBasedPartitionScanner {
	public final static String COMMENT = "comment";
	public final static String STRING = "string";
	public final static String OPERATOR = "operator";

	public IoPartitionScanner() {
		List<IPredicateRule> l = getCommentRule();
		l.addAll(getStringRule());
		l.addAll(getOperatorRule());
		setPredicateRules(l.toArray(new IPredicateRule[l.size()]));
	}

	private final List<IPredicateRule> getCommentRule() {
		IToken string = new Token(COMMENT);
		List<IPredicateRule> l = Arrays.newArrayList();
		l.add(new EndOfLineRule("//", string));
		l.add(new EndOfLineRule("#", string));
		l.add(new MultiLineRule("/*", "*/", string, (char) 0, true));
		return l;
	}

	private final List<IPredicateRule> getStringRule() {
		IToken string = new Token(STRING);
		List<IPredicateRule> l = Arrays.newArrayList();
		l.add(new SingleLineRule("\"", "\"", string));
		l.add(new MultiLineRule("\"\"\"", "\"\"\"", string));
		return l;
	}
	
	private final List<IPredicateRule> getOperatorRule(){
		IToken string = new Token(OPERATOR);
		List<IPredicateRule> l = Arrays.newArrayList();
		l.add(new SingleLineRule(":", "=", string));
		l.add(new SingleLineRule("!", "=", string));
		l.add(new SingleLineRule(".", ".", string));
		return l;
	}
}
