/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cmdbuild.translation.dao.Translation;
import org.cmdbuild.translation.dao.TranslationImpl;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static com.google.common.base.Strings.nullToEmpty;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslationFromFileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static List<Translation> loadTranslations(String fileContent) {//TODO improve this  (it is kinda weak)
//        Matcher matcher = Pattern.compile("^.*Ext.define[(]'CMDBuildUI.locales.(([^.]+)[.])?(Locales|LocalesAdministration)', *[{].*?administration.*?,(.*[}])[)];.*", Pattern.DOTALL).matcher(checkNotBlank(fileContent));
        Matcher matcher = Pattern.compile("^.*Ext.define[(]'CMDBuildUI.locales.(([^.]+)[.])?(Locales)', *[{].*?administration.*?,(.*[}])[)];.*", Pattern.DOTALL).matcher(checkNotBlank(fileContent));
        checkArgument(matcher.find(), "invalid file format: expected pattern not found");
        String lang = firstNotBlank(matcher.group(2), "default");
        JsonNode node = fromJson("{" + checkNotBlank(matcher.group(4)), JsonNode.class);
        for (String element : list("system", "data")) {
            if (node.has(element)) {
                node = node.get(element);
            } else {
                return emptyList();
            }
        }
        Helper helper = new Helper();
        helper.loadTranslations(lang, emptyList(), node);
        return helper.list;
    }

    private static class Helper {

        private final List<Translation> list = list();

        private void loadTranslations(String lang, List<String> path, JsonNode node) {
            if (node.isObject()) {
                node.fields().forEachRemaining(e -> loadTranslations(lang, list(path).with(e.getKey()), e.getValue()));
            } else {
                String value = nullToEmpty(node.asText());
                Translation translation = TranslationImpl.builder().withLang(lang).withValue(value).withCode(Joiner.on(".").join(path)).build();
                LOGGER.debug("load translation code =< {} > lang =< {} > value =< {} >", translation.getCode(), translation.getLang(), translation.getValue());
                list.add(translation);
            }
        }
    }

}
