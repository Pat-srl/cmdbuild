/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Maps.transformEntries;
import static com.google.common.collect.Maps.transformValues;
import com.google.common.collect.Sets;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_ALL;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_LIST;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_MODIFY;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_NONE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_READ;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_UPDATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_WRITE;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_ALL;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_CUSTOM;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_DEFAULT;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_HIDDEN;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_IMMUTABLE;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_NONE;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_PROTECTED;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_READ;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_RESCORE;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_RESERVED;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_SYSHIDDEN;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_SYSREAD;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_WRITE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_ALL;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CLONE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_LIST;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_MODIFY;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_NONE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_PRINT;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_SEARCH;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_BASIC;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_READTOUCHED;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WRITE;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_ALL;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_DEFAULT;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_NONE;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_PROTECTED;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_READ;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_RESERVED;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_WRITE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.MapDuplicateKeyMode.ALLOW_DUPLICATES;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.cmdbuild.utils.privileges.PrivilegeProcessor;
import org.cmdbuild.utils.privileges.PrivilegeProcessorImpl;

public class DaoPermissionUtils {

    private final static PrivilegeProcessor<ClassPermission> CLASS_PERMISSION_PROCESSOR = PrivilegeProcessorImpl.<ClassPermission>builder()
            .withNullPrivilegeValues(CP_NONE)
            .withPrivilegeImplicationMap(map(CP_ALL, set(CP_WRITE, CP_MODIFY),
                    CP_WRITE, set(CP_CREATE, CP_UPDATE, CP_DELETE, CP_CLONE, CP_WF_READTOUCHED, CP_WF_BASIC),
                    CP_CREATE, set(CP_READ),
                    CP_UPDATE, set(CP_READ),
                    CP_DELETE, set(CP_READ),
                    CP_CLONE, set(CP_READ),
                    CP_READ, set(CP_LIST, CP_PRINT, CP_SEARCH),
                    CP_MODIFY, set(CP_LIST),
                    CP_WF_READTOUCHED, set(CP_WF_BASIC, CP_LIST),
                    CP_WF_BASIC, set(CP_LIST, CP_PRINT, CP_SEARCH)
            )).build();

    private final static PrivilegeProcessor<AttributePermission> ATTRIBUTE_PERMISSION_PROCESSOR = PrivilegeProcessorImpl.<AttributePermission>builder()
            .withNullPrivilegeValues(AP_NONE)
            .withPrivilegeImplicationMap(map(AP_ALL, set(AP_WRITE, AP_READ, AP_CREATE, AP_UPDATE, AP_MODIFY, AP_LIST),
                    AP_WRITE, set(AP_READ, AP_CREATE, AP_UPDATE, AP_LIST),
                    AP_CREATE, set(AP_LIST),
                    AP_UPDATE, set(AP_LIST),
                    AP_READ, set(AP_LIST),
                    AP_MODIFY, set(AP_LIST)
            )).build();

    private static final BiMap<AttributePermission, String> ATTRIBUTE_PERMISSION_KEYWORD_MAPPING = ImmutableBiMap.copyOf(map(
            AP_ALL, "A",
            AP_WRITE, "W",
            AP_CREATE, "C",
            AP_UPDATE, "U",
            AP_READ, "R",
            AP_LIST, "L",
            AP_MODIFY, "M",
            AP_NONE, "N"
    ));

    private final static Map<AttributePermissionMode, AttributePermissions> ATTRIBUTE_PERMISSION_DEFAULTS = unmodifiableMap(new EnumMap(transformValues(map(
            APM_ALL, "WM|A|A",
            APM_DEFAULT, "WM|A|A",
            APM_WRITE, "WM|A|A",
            APM_READ, "WM|A|RM",
            APM_HIDDEN, "WM|WM|M",
            APM_IMMUTABLE, "CRM|A|A",
            APM_PROTECTED, "WL|A|A",
            APM_SYSREAD, "WL|RL|A",
            APM_SYSHIDDEN, "WL|RL|N",
            APM_RESERVED, "RL|N|N",
            APM_RESCORE, "WL|N|N",
            APM_CUSTOM, "N|N|N",
            APM_NONE, "N|N|N"
    ), DaoPermissionUtils::parseAttributePermissions)));

    private static final BiMap<ClassPermission, String> CLASS_PERMISSION_KEYWORD_MAPPING = ImmutableBiMap.copyOf(map(
            CP_ALL, "A",
            CP_WRITE, "W",
            CP_READ, "R",
            CP_LIST, "L",
            CP_MODIFY, "M",
            CP_NONE, "N"
    ));

    private final static Map<ClassPermissionMode, ClassPermissions> CLASS_PERMISSION_DEFAULTS = unmodifiableMap(new EnumMap(transformValues(map(
            CPM_ALL, "WM|A|A",
            CPM_DEFAULT, "WM|A|A",
            CPM_WRITE, "WM|A|A",
            CPM_READ, "RM|A|A",
            CPM_PROTECTED, "WL|A|A",
            CPM_RESERVED, "WL|N|N",
            CPM_NONE, "N|N|N"
    ), DaoPermissionUtils::parseClassPermissions)));

    private static final Map<String, ClassPermissionMode> DOMAIN_PERMISSION_MAP = ImmutableMap.copyOf((Map) map(
            "write|false|write|false", CPM_WRITE,
            "write|true|write|false", CPM_WRITE,
            "write|true|write|true", CPM_WRITE,
            "write|false|read|false", CPM_WRITE,
            "write|true|read|false", CPM_WRITE,
            "write|false|read|true", CPM_READ,
            "write|true|read|true", CPM_READ,
            "write|false|none|false", CPM_NONE,
            "write|true|none|false", CPM_READ,
            "write|false|none|true", CPM_NONE,
            "write|true|none|true", CPM_READ,
            "read|false|none|false", CPM_NONE,
            "read|true|none|false", CPM_READ,
            "read|false|none|true", CPM_NONE,
            "read|true|none|true", CPM_READ,
            "none|false|none|false", CPM_NONE,
            "none|true|none|false", CPM_NONE,
            "none|true|none|true", CPM_NONE,
            "read|false|read|false", CPM_READ,
            "read|true|read|true", CPM_READ,
            "read|true|read|false", CPM_READ,
            "read|false|read|true", CPM_READ
    ).entrySet()
            .stream().flatMap(e -> list(Pair.of(e.getKey(), e.getValue()), Pair.of(e.getKey().toString().replaceFirst("([^|]+[|][^|]+)[|](.+)", "$2|$1"), e.getValue())).stream()).collect(toMap(Pair::getKey, Pair::getValue, ALLOW_DUPLICATES)));

    public static String serializeAttributePermissionMode(AttributePermissionMode mode) {
        return serializeEnum(mode);
    }

    public static AttributePermissionMode parseAttributePermissionMode(String value) {
        return parseEnum(value, AttributePermissionMode.class);
    }

    public static String serializeClassPermissionMode(ClassPermissionMode mode) {
        return mode.name().toLowerCase().replaceFirst("cpm_", "");
    }

    public static ClassPermissionMode parseClassPermissionMode(String value) {
        return parseEnum(value, ClassPermissionMode.class);
    }

    public static AttributePermissions getDefaultPermissions(AttributePermissionMode mode) {
        return checkNotNull(ATTRIBUTE_PERMISSION_DEFAULTS.get(mode), "unsupported permission mode = %s", mode);
    }

    public static ClassPermissions getDefaultPermissions(ClassPermissionMode mode) {
        return checkNotNull(CLASS_PERMISSION_DEFAULTS.get(mode), "unsupported permission mode = %s", mode);
    }

    public static AttributePermissions parseAttributePermissions(String string) {
        Matcher matcher = Pattern.compile("([^|]*)\\|([^|]*)\\|([^|]*)").matcher(string);
        checkArgument(matcher.find(), "string format error");
        return AttributePermissionsImpl.builder().withPermissions(expandAttributePermissions(map(PermissionScope.PS_CORE, parseAttributeKeywords(matcher.group(1)),
                PermissionScope.PS_SERVICE, parseAttributeKeywords(matcher.group(2)),
                PermissionScope.PS_UI, parseAttributeKeywords(matcher.group(3))
        ))).build();
    }

    public static ClassPermissions parseClassPermissions(String string) {
        Matcher matcher = Pattern.compile("([^|]*)\\|([^|]*)\\|([^|]*)").matcher(string);
        checkArgument(matcher.find(), "string format error");
        return ClassPermissionsImpl.builder().withPermissions(cascadeExpandClassPermissions(map(PermissionScope.PS_CORE, parseClassKeywords(matcher.group(1)),
                PermissionScope.PS_SERVICE, parseClassKeywords(matcher.group(2)),
                PermissionScope.PS_UI, parseClassKeywords(matcher.group(3))
        ))).build();
    }

    public static String serializePermissions(AttributePermissions permissions) {
        return list(PermissionScope.PS_CORE, PermissionScope.PS_SERVICE, PermissionScope.PS_UI).stream()
                .map((s) -> permissions.getPermissionsForScope(s).stream().map(ATTRIBUTE_PERMISSION_KEYWORD_MAPPING::get).collect(joining("")))//TODO normalize?
                .collect(joining("|"));
    }

    public static Map<PermissionScope, Set<AttributePermission>> mergeAttributePermissions(Map<PermissionScope, Set<AttributePermission>> source, Map<PermissionScope, Set<AttributePermission>> toAdd) {
        return expandAttributePermissions(transformEntries(source, (k, v) -> set(v).with(checkNotNull(toAdd.get(k)))));
    }

    public static Map<PermissionScope, Set<AttributePermission>> intersectAttributePermissions(Map<PermissionScope, Set<AttributePermission>> source, Set<AttributePermission> subset, Map<PermissionScope, Set<AttributePermission>> toIntersect) {
        Map<PermissionScope, Set<AttributePermission>> toRemove = map(transformEntries(toIntersect, (x, v) -> set(ATTRIBUTE_PERMISSION_PROCESSOR.expandPrivileges(subset)).without(ATTRIBUTE_PERMISSION_PROCESSOR.expandPrivileges(v))));
        return removeAttributePermissions(source, toRemove);
    }

    public static Map<PermissionScope, Set<AttributePermission>> removeAttributePermissions(Map<PermissionScope, Set<AttributePermission>> source, Map<PermissionScope, Set<AttributePermission>> toRemove) {
        return expandAttributePermissions(transformEntries(source, (k, v) -> set(v).without(ATTRIBUTE_PERMISSION_PROCESSOR.expandPrivilegesBackwards(nullToEmpty(toRemove.get(k))))));
    }

    public static Map<PermissionScope, Set<ClassPermission>> mergeClassPermissions(Map<PermissionScope, Set<ClassPermission>> source, Map<PermissionScope, Set<ClassPermission>> toAdd) {
        return cascadeExpandClassPermissions(transformEntries(source, (k, v) -> set(v).with(checkNotNull(toAdd.get(k)))));
    }

    public static Map<PermissionScope, Set<ClassPermission>> removeClassPermissions(Map<PermissionScope, Set<ClassPermission>> source, Map<PermissionScope, Set<ClassPermission>> toRemove) {
        return cascadeExpandClassPermissions(transformEntries(source, (k, v) -> set(v).without(CLASS_PERMISSION_PROCESSOR.expandPrivilegesBackwards(nullToEmpty(toRemove.get(k))))));
    }

    public static Map<PermissionScope, Set<ClassPermission>> intersectClassPermissions(Map<PermissionScope, Set<ClassPermission>> source, Map<PermissionScope, Set<ClassPermission>> toIntersect) {
        return cascadeExpandClassPermissions(transformEntries(source, (k, v) -> set(v).withOnly(CLASS_PERMISSION_PROCESSOR.expandPrivileges(firstNonNull(toIntersect.get(k), EnumSet.allOf(ClassPermission.class))))));
    }

    public static Map<PermissionScope, Set<ClassPermission>> intersectClassPermissionsNoExpand(Map<PermissionScope, Set<ClassPermission>> source, Map<PermissionScope, Set<ClassPermission>> toIntersect) {
        return cascadeClassPermissions(transformEntries(source, (k, v) -> set(v).withOnly(firstNonNull(toIntersect.get(k), EnumSet.allOf(ClassPermission.class)))));
    }

    public static ClassPermissions buildDomainPermissions(ClassPermissions sourceClassAccess, boolean hasSourceReference, ClassPermissions targetClassAccess, boolean hasTargetReference) {
        ClassPermissionMode sourceMode = getClassPermissionModeForDomainFromClass(sourceClassAccess), targetMode = getClassPermissionModeForDomainFromClass(targetClassAccess);
        String key = Joiner.on("|").join(serializeEnum(sourceMode), hasSourceReference, serializeEnum(targetMode), hasTargetReference);
        return getDefaultPermissions(checkNotNull(DOMAIN_PERMISSION_MAP.get(key), "missing domain permission mapping for key =< %s >", key));
    }

    private static ClassPermissionMode getClassPermissionModeForDomainFromClass(ClassPermissions classAccess) {
//        if (classAccess.hasServiceWritePermission()) {
//            return CPM_WRITE;
//        } else if (classAccess.hasServiceReadPermission()) {
//            return CPM_READ;
//        } else {
//            return CPM_NONE;
//        }
        if (classAccess.hasServicePermission(CP_WF_BASIC)//note: this is not technically correct, we should filter for task access (?)
                || classAccess.hasServiceWritePermission()) {
            return CPM_WRITE;
        } else if (classAccess.hasServiceReadPermission()) {
            return CPM_READ;
        } else {
            return CPM_NONE;
        }
    }

    public static Set<ClassPermission> expandClassPermissions(Iterable<ClassPermission> privileges) {
        return CLASS_PERMISSION_PROCESSOR.expandPrivileges(privileges);
    }

    public static Set<ClassPermission> expandClassPermissions(ClassPermission... privileges) {
        return CLASS_PERMISSION_PROCESSOR.expandPrivileges(privileges);
    }

    private static Set<AttributePermission> parseAttributeKeywords(String string) {
        return Splitter.fixedLength(1).splitToList(string).stream()
                .map(String::toUpperCase)
                .map((k) -> checkNotNull(ATTRIBUTE_PERMISSION_KEYWORD_MAPPING.inverse().get(k), "unknown permission keyword for char = %s", k))
                .filter(not(equalTo(AttributePermission.AP_NONE)))
                .collect(toImmutableSet());
    }

    private static Set<ClassPermission> parseClassKeywords(String string) {
        return Splitter.fixedLength(1).splitToList(string).stream()
                .map(String::toUpperCase)
                .map((k) -> checkNotNull(CLASS_PERMISSION_KEYWORD_MAPPING.inverse().get(k), "unknown permission keyword for char = %s", k))
                .filter(not(equalTo(ClassPermission.CP_NONE)))
                .collect(toImmutableSet());
    }

    private static Map<PermissionScope, Set<AttributePermission>> expandAttributePermissions(Map<PermissionScope, Set<AttributePermission>> map) {
        map = map(transformValues(map, ATTRIBUTE_PERMISSION_PROCESSOR::expandPrivileges));

        map.put(PermissionScope.PS_SERVICE, Sets.intersection(map.get(PermissionScope.PS_SERVICE), map.get(PermissionScope.PS_CORE)));
        map.put(PermissionScope.PS_UI, Sets.intersection(map.get(PermissionScope.PS_SERVICE), map.get(PermissionScope.PS_UI)));

        return unmodifiableMap(new EnumMap(transformValues(map, (s) -> s.isEmpty() ? emptySet() : unmodifiableSet(EnumSet.copyOf(s)))));
    }

    private static Map<PermissionScope, Set<ClassPermission>> cascadeExpandClassPermissions(Map<PermissionScope, Set<ClassPermission>> map) {
        return cascadeClassPermissions(map, true);
    }

    private static Map<PermissionScope, Set<ClassPermission>> cascadeClassPermissions(Map<PermissionScope, Set<ClassPermission>> map) {
        return cascadeClassPermissions(map, false);
    }

    private static Map<PermissionScope, Set<ClassPermission>> cascadeClassPermissions(Map<PermissionScope, Set<ClassPermission>> map, boolean expand) {
        if (expand) {
            map = map(transformValues(map, CLASS_PERMISSION_PROCESSOR::expandPrivileges));
        } else {
            map = map(map);
        }

        map.put(PermissionScope.PS_SERVICE, Sets.intersection(map.get(PermissionScope.PS_SERVICE), map.get(PermissionScope.PS_CORE)));
        map.put(PermissionScope.PS_UI, Sets.intersection(map.get(PermissionScope.PS_SERVICE), map.get(PermissionScope.PS_UI)));

        return unmodifiableMap(new EnumMap(transformValues(map, (s) -> s.isEmpty() ? emptySet() : unmodifiableSet(EnumSet.copyOf(s)))));
    }

}
