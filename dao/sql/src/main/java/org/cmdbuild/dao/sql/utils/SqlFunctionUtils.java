/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.sql.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Ordering;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.cmdbuild.utils.sql.SqlScriptFunctionToken;
import static org.cmdbuild.utils.sql.SqlScriptUtils.parseSqlFunctionTokensFromScript;

public class SqlFunctionUtils {

    public static List<SqlFunction> readSqlFunctionsFromDir(File directory) {
        checkArgument(directory.isDirectory());
        List<SqlFunction> list = list();
        FileUtils.listFiles(directory, new String[]{"sql"}, true).stream().sorted(Ordering.natural().onResultOf(File::getAbsolutePath)).forEach((f) -> {
            try {
                String string = readToString(f);
                list.addAll(readSqlFunctions(string));
            } catch (Exception ex) {
                throw runtime(ex, "error loading function code from file = %s", f.getAbsolutePath());
            }
        });
        return list;
    }

    public static List<SqlFunction> readSqlFunctions(String script) {
        List<SqlScriptFunctionToken> tokens = parseSqlFunctionTokensFromScript(script);
        return new SqlFunctionProcessor(tokens).readSqlFunctions();
    }

    public static SqlFunction readSqlFunction(String script) {
        return getOnlyElement(readSqlFunctions(script));
    }

    public static Map<String, SqlFunction> readSqlFunctionsAsMap(String script) {
        return uniqueIndex(readSqlFunctions(script), SqlFunction::getSignature);
    }

    private static class SqlFunctionProcessor {

        private final Stream<SqlScriptFunctionToken> tokens;
        private String requiredPatchVersion;

        public SqlFunctionProcessor(List<SqlScriptFunctionToken> tokens) {
            this.tokens = checkNotNull(tokens).stream();
        }

        private List<SqlFunction> readSqlFunctions() {
            return tokens.map(this::parseToken).collect(toImmutableList());
        }

        private SqlFunction parseToken(SqlScriptFunctionToken token) {
            try {
                token.getUnparsedLinesBeforeFunctionToken().forEach(this::tryToParseRequiredPatchVersion);
                return createFunction(token);
            } catch (Exception ex) {
                throw runtime(ex, "error processing sql script token with signature =< %s >", abbreviate(token.getFunctionSignature()));
            }
        }

        private void tryToParseRequiredPatchVersion(String line) {
            Matcher matcher = Pattern.compile("--+\\s*REQUIRE\\s+PATCH\\s+([^\\s]+)").matcher(line);
            if (matcher.find()) {
                requiredPatchVersion = checkNotBlank(matcher.group(1));
            }
        }

        private SqlFunction createFunction(SqlScriptFunctionToken token) {
            Map<String, String> meta = list(token.getUnparsedLinesBeforeFunctionToken()).reverse().until(StringUtils::isBlank).reverse().stream().map(l -> {
                Matcher matcher = Pattern.compile("--+\\s*([^\\s]+?)(\\s*[:=])?\\s+([^\\s].*)").matcher(l);
                if (matcher.matches()) {
                    return Pair.of(checkNotBlank(matcher.group(1)), matcher.group(3));
                } else {
                    return null;
                }
            }).filter(notNull()).collect(toMap(Pair::getLeft, Pair::getRight));
            return SqlFunctionImpl.builder()
                    .withSignature(token.getFunctionSignature())
                    .withRequiredPatchVersion(requiredPatchVersion)
                    .withFunctionDefinition(token.getFunctionDefinition())
                    .withComment(meta.get("COMMENT"))
                    .build();
        }

    }
}
