package org.cmdbuild.cql.compiler;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.cmdbuild.cql.CQLBuilderListener;
import org.cmdbuild.cql.CQLCompilerBuilder;
import org.cmdbuild.cql.CQLLexer;
import org.cmdbuild.cql.CQLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CQLCompiler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void compile(String text, CQLBuilderListener listener) throws RecognitionException {
		logger.debug("compile cql code = '{}'", text);
		ANTLRStringStream input = new ANTLRStringStream(text);
		logger.debug("cql stream = {}", input);
		CQLLexer lexer = new CQLLexer(input);
		logger.debug("cql lexer = {}", lexer);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		logger.debug("cql tokens = {}", tokens);
		CQLParser parser = new CQLParser(tokens);
		logger.debug("cql parser = {}", parser);
		CQLParser.expr_return r = parser.expr();
		CommonTree commonTree = (CommonTree) r.getTree();
		logger.debug("cql tree = {}", commonTree);
		CQLCompilerBuilder builder = new CQLCompilerBuilder();
		builder.setCQLBuilderListener(listener);
		builder.compile(commonTree);
		logger.debug("compiled cql code = '{}'", text);
	}
}
