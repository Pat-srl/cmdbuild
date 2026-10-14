/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.sharepoint.utils;

import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dms.sharepoint.SharepointDmsProviderService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ataboga
 */
public class SharepointUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Map<String, String> getTranslations(String language) {
        try {
            if (isNotBlank(language)) {
                return fromJson(new String(SharepointDmsProviderService.class.getResourceAsStream(format("/org/cmdbuild/dms/sharepoint/locales/%s.json", language)).readAllBytes(), StandardCharsets.UTF_8), MAP_OF_STRINGS);
            }
        } catch (Exception ex) {
            LOGGER.warn("error retrieving config translations, using default", ex);
        }
        return emptyMap();
    }

    public static Map<String, String> generateAttributeConfig(String config, String description, Map<String, String> translations, AttributeTypeName type, Set<String> options) {//TODO options only for string type?
        return map(
                "_id", config,
                "name", config,
                "description", description,
                "_description_translation", firstNotNull(translations.get(config), description),
                "type", type,
                "options", options
        );
    }
}
