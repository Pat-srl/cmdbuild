/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sql;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.StringReader;
import static java.lang.String.format;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import static org.apache.commons.io.IOUtils.readLines;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SqlScriptUtils {

    public static List<SqlScriptFunctionToken> parseSqlFunctionTokensFromScript(String script) {
        return new SqlScriptFunctionsProcessor(readLines(new StringReader(script))).readSqlFunctions();
    }

    private static class SqlScriptFunctionsProcessor {

        private final Stream<String> lines;
        private final List<String> unparsedLines = list(), activeLines = list();
        private final List<SqlScriptFunctionToken> list = list();
        private String functionSignature, functionName;

        public SqlScriptFunctionsProcessor(List<String> lines) {
            this.lines = checkNotNull(lines).stream();
        }

        private List<SqlScriptFunctionToken> readSqlFunctions() {
            lines.forEach(this::parseLine);
            return list;
        }

        private void parseLine(String line) {
            tryToParseFunctionSignature(line);
            if (isBlank(functionSignature)) {
                unparsedLines.add(line);
            } else {
                if (isNotBlank(line)) {
                    activeLines.add(line);
                    if (isFinalLine(line)) {
                        createFunctionToken();
                    }
                }
            }
        }

        private void tryToParseFunctionSignature(String line) {
            Matcher matcher = Pattern.compile("\\s*CREATE\\s+(OR\\s+REPLACE\\s+)?FUNCTION\\s+([^\\s(]+)\\s*[(]([^)]*)[)].*", Pattern.CASE_INSENSITIVE).matcher(line);
            if (matcher.find()) {
                functionName = checkNotBlank(matcher.group(2));
                String args = trim(matcher.group(3)).toLowerCase().replaceAll("\\s*,\\s*", ",").replaceAll("\\s+", " ").replaceAll("\\s*(out\\s+)?(variadic\\s+)?([^\\s,]+)\\s+([^\\s,]+)", "$1$4");
                functionSignature = format("%s(%s)", functionName, args);
            }
        }

        private boolean isFinalLine(String line) {
            return line.matches("\\s*(END)?\\s*[$].*[$]\\s*LANGUAGE.*");
        }

        private void createFunctionToken() {
            String content = Joiner.on("\n").join(activeLines);
            String prefix = Joiner.on("\n").join(unparsedLines);
            list.add(new SqlScriptFunctionTokenImpl(prefix, functionName, functionSignature, content));
            activeLines.clear();
            unparsedLines.clear();
            functionSignature = null;
        }

    }

    private final static class SqlScriptFunctionTokenImpl implements SqlScriptFunctionToken {

        private final String prefix, name, signature, content;

        public SqlScriptFunctionTokenImpl(String prefix, String name, String signature, String content) {
            this.prefix = nullToEmpty(prefix);
            this.name = checkNotBlank(name);
            this.signature = checkNotBlank(signature);
            this.content = checkNotBlank(content);
        }

        @Override
        public String getUnparsedTextBeforeFunctionToken() {
            return prefix;
        }

        @Override
        public String getFunctionName() {
            return name;
        }

        @Override
        public String getFunctionSignature() {
            return signature;
        }

        @Override
        public String getFunctionDefinition() {
            return content;
        }

        @Override
        public String toString() {
            return "SqlScriptFunctionToken{" + "signature=" + signature + '}';
        }

    }
}
