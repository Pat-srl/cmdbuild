/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public class DocumentPathUtils {

    public static String buildDocumentPath(Classe classe, @Nullable Long cardId) {
        return buildDocumentPathList(classe, cardId).collect(joining("/"));
    }

    public static String buildClassFolderPath(Classe classe) {
        return buildClassFolderPathList(classe).collect(joining("/"));
    }

    public static FluentList<String> buildDocumentPathList(Classe classe, @Nullable Long cardId) {
        return buildClassFolderPathList(classe).accept(l -> {
            if (isNotNullAndGtZero(cardId)) {
                l.add(format("Id%s", cardId));
            }
        });
    }

    public static FluentList<String> buildClassFolderPathList(Classe classe) {
        return list(classe.getAncestorsAndSelf().stream().filter(not(equalTo(BASE_CLASS_NAME))).collect(toList()));
    }

    public static String nextVersion(@Nullable String currentVersion, boolean major) {
        if (isBlank(currentVersion)) {
            return "1.0";
        } else {
            Matcher matcher = Pattern.compile("^([0-9]+)[.]([0-9]+)$").matcher(currentVersion);
            checkArgument(matcher.find(), "invalid version syntax for value = '%s'", currentVersion);
            int first = parseInt(matcher.group(1)), last = parseInt(matcher.group(2));
            if (major) {
                first++;
                last = 0;
            } else {
                last++;
            }
            return format("%s.%s", first, last);
        }
    }

}
