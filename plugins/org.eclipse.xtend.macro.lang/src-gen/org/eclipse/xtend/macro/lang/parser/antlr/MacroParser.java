/*
* generated by Xtext
*/
package org.eclipse.xtend.macro.lang.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtend.macro.lang.services.MacroGrammarAccess;

public class MacroParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private MacroGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	
	@Override
	protected org.eclipse.xtend.macro.lang.parser.antlr.internal.InternalMacroParser createParser(XtextTokenStream stream) {
		return new org.eclipse.xtend.macro.lang.parser.antlr.internal.InternalMacroParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "MacroFile";
	}
	
	public MacroGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(MacroGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}