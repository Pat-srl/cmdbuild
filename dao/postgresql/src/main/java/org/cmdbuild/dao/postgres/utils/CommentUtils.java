/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.dao.DaoConst.COMMENT_ACTIVE;
import static org.cmdbuild.dao.DaoConst.COMMENT_DESCR;
import static org.cmdbuild.dao.DaoConst.COMMENT_FLOW_SAVE_BUTTON_ENABLED;
import static org.cmdbuild.dao.DaoConst.COMMENT_FLOW_STATUS_ATTR;
import static org.cmdbuild.dao.DaoConst.COMMENT_MODE;
import static org.cmdbuild.dao.DaoConst.COMMENT_MULTITENANT_MODE;
import static org.cmdbuild.dao.DaoConst.COMMENT_SUPERCLASS;
import static org.cmdbuild.dao.DaoConst.COMMENT_TYPE;
import static org.cmdbuild.dao.DaoConst.COMMENT_USERSTOPPABLE;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import org.cmdbuild.dao.entrytype.DomainMetadata;
import org.cmdbuild.dao.entrytype.EntryTypeMetadata;
import org.cmdbuild.dao.function.FunctionMetadata;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static String COMMENT_IGNORED = "org.cmdbuild.dao.postgres.utils.COMMENT_IGNORED";

    public final static BiMap<String, String> ENTRY_TYPE_COMMENT_TO_METADATA_MAPPING = ImmutableBiMap.copyOf(map(
            COMMENT_ACTIVE, EntryTypeMetadata.ACTIVE,
            COMMENT_MODE, EntryTypeMetadata.ENTRY_TYPE_MODE));

    public final static BiMap<String, String> CLASS_COMMENT_TO_METADATA_MAPPING = ImmutableBiMap.copyOf(map(ENTRY_TYPE_COMMENT_TO_METADATA_MAPPING).with(COMMENT_DESCR, EntryTypeMetadata.DESCRIPTION,
            COMMENT_SUPERCLASS, ClassMetadata.SUPERCLASS,
            COMMENT_TYPE, ClassMetadata.CLASS_TYPE,
            COMMENT_MULTITENANT_MODE, ClassMetadata.MULTITENANT_MODE,
            COMMENT_USERSTOPPABLE, ClassMetadata.USER_STOPPABLE,
            COMMENT_FLOW_STATUS_ATTR, ClassMetadata.WORKFLOW_STATUS_ATTR,
            COMMENT_FLOW_SAVE_BUTTON_ENABLED, ClassMetadata.WORKFLOW_ENABLE_SAVE_BUTTON
    ));

    public final static BiMap<String, String> DOMAIN_COMMENT_TO_METADATA_MAPPING = ImmutableBiMap.copyOf(map(ENTRY_TYPE_COMMENT_TO_METADATA_MAPPING).with(
            "LABEL", EntryTypeMetadata.DESCRIPTION,
            COMMENT_TYPE, DomainMetadata.DOMAIN_TYPE,
            "CLASS1", DomainMetadata.CLASS_1,
            "CLASS2", DomainMetadata.CLASS_2,
            "DESCRDIR", DomainMetadata.DESCRIPTION_1,
            "DESCRINV", DomainMetadata.DESCRIPTION_2,
            "CARDIN", DomainMetadata.CARDINALITY,
            "MASTERDETAIL", DomainMetadata.MASTERDETAIL,
            "CASCADEDIRECT", DomainMetadata.CASCADE_ACTION_DIRECT,
            "CASCADEINVERSE", DomainMetadata.CASCADE_ACTION_INVERSE,
            "MDLABEL", DomainMetadata.MASTERDETAIL_DESCRIPTION,
            "MDFILTER", DomainMetadata.MASTERDETAIL_FILTER,
            "DISABLED1", DomainMetadata.DISABLED_1,
            "DISABLED2", DomainMetadata.DISABLED_2,
            "INDEX1", DomainMetadata.INDEX_1,
            "INDEX2", DomainMetadata.INDEX_2
    ));

    public final static BiMap<String, String> FUNCTION_COMMENT_TO_METADATA_MAPPING = ImmutableBiMap.copyOf(map(ENTRY_TYPE_COMMENT_TO_METADATA_MAPPING).with(
            COMMENT_TYPE, COMMENT_IGNORED,
            "CATEGORIES", FunctionMetadata.CATEGORIES,
            "MASTERTABLE", FunctionMetadata.MASTERTABLE,
            "TAGS", FunctionMetadata.TAGS,
            "SOURCE", FunctionMetadata.SOURCE
    ));

    public final static BiMap<String, String> ATTRIBUTE_COMMENT_TO_METADATA_MAPPING = ImmutableBiMap.copyOf(map(ENTRY_TYPE_COMMENT_TO_METADATA_MAPPING).with(COMMENT_DESCR, EntryTypeMetadata.DESCRIPTION,
            "BASEDSP", AttributeMetadata.BASEDSP,
            "CLASSORDER", AttributeMetadata.CLASSORDER,
            "EDITORTYPE", AttributeMetadata.EDITOR_TYPE,
            "GROUP", AttributeMetadata.GROUP,
            "INDEX", AttributeMetadata.INDEX,
            "LOOKUP", AttributeMetadata.LOOKUP_TYPE,
            "REFERENCEDIR", AttributeMetadata.REFERENCE_DIRECTION,
            "REFERENCEDOM", AttributeMetadata.REFERENCE_DOMAIN,
            "FKTARGETCLASS", AttributeMetadata.FK_TARGET_CLASS,
            "CASCADE", AttributeMetadata.CASCADE,
            "FILTER", AttributeMetadata.FILTER,
            "IP_TYPE", AttributeMetadata.IP_TYPE,
            "DEFAULT", AttributeMetadata.DEFAULT,
            "NOTNULL", AttributeMetadata.MANDATORY,
            "UNIQUE", AttributeMetadata.UNIQUE,
            "DOMAINKEY", AttributeMetadata.DOMAINKEY,
            "ITEMS", AttributeMetadata.ITEMS,
            "GISATTR", AttributeMetadata.GISATTR,
            "LENGTH", AttributeMetadata.LENGTH,
            "LINK", AttributeMetadata.LINK
    ));

    public static Map<String, String> parseCommentFromFeatures(Map<String, String> features, Map<String, String> mapping) {
        return features.entrySet().stream().map((e) -> {
            if (equal(mapping.get(e.getKey()), COMMENT_IGNORED)) {
                return null;
            } else {
                return Pair.of(mapping.getOrDefault(e.getKey(), e.getKey()), e.getValue());
            }
        }).filter(not(isNull())).collect(toMap(Pair::getKey, Pair::getValue));
    }

    public static Map<String, String> parseComment(String comment, Map<String, String> mapping) {
        return CmJsonUtils.<Map<String, String>>fromJson(comment, MAP_OF_STRINGS).entrySet().stream().map((e) -> {
            if (!mapping.containsKey(e.getKey())) {
                LOGGER.warn(marker(), "found unsupported entry type comment = {} (will be ignored)", e.getKey());
                return null;
            } else if (equal(mapping.get(e.getKey()), COMMENT_IGNORED)) {
                return null;
            } else {
                return Pair.of(mapping.get(e.getKey()), e.getValue());
            }
        }).filter(not(isNull())).collect(toMap(Pair::getKey, Pair::getValue));
    }

}
