/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmInlineUtils.InlineOptions.KEEP_ORIGINAL;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CmInlineUtils {

    public static <T> Map<String, T> recursiveUnflattenMaps(Map<String, T> source) {
        return (Map) map().accept(m -> {
            unflattenMaps(source).forEach((k, v) -> {
                m.put(k, v instanceof Map innerMap ? recursiveUnflattenMaps(innerMap) : v);
            });
        });
    }

    public static <T> Map<String, T> unflattenMaps(Map<String, T> source) {
        Pattern pattern = Pattern.compile("^(.+?)___(.+)$");
        return (Map) map(source).accept(m -> {
            source.forEach((k, v) -> {
                Matcher matcher = pattern.matcher(k);
                if (matcher.matches()) {
                    m.remove(k);
                    String k1 = checkNotBlank(matcher.group(1)), k2 = checkNotBlank(matcher.group(2));
                    Map<String, Object> inner;
                    if (m.containsKey(k1)) {
                        inner = checkNotNull((Map) m.get(k1));
                    } else {
                        m.put(k1, inner = map());
                    }
                    inner.put(k2, v);
                }
            });
        });
    }

    public static <T> Map<String, T> flattenMaps(Map<String, T> source) {
        return flattenMaps(source, new InlineOptions[]{});
    }

    public static <T> Map<String, T> flattenMapsKeepOriginal(Map<String, T> source) {
        return flattenMaps(source, KEEP_ORIGINAL);
    }

    public static <T> Map<String, T> flattenMaps(Map<String, T> source, InlineOptions... options) {
        boolean leaveOriginal = set(options).contains(KEEP_ORIGINAL);
        return (Map) map(source).accept(m -> {
            source.forEach((k, v) -> {
                if (v instanceof Map map) {
                    if (!leaveOriginal) {
                        m.remove(k);
                    }
                    map = flattenMaps(map, options);
                    map.forEach((ik, iv) -> {
                        m.put(format("%s___%s", k, ik), iv);
                    });
                }
                if (v instanceof List list) {
                    if (!leaveOriginal) {
                        m.remove(k);
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Object item = list.get(i);
                        String prefix = format("%s___%s", k, i);
                        if (item instanceof Map map) {
                            map = flattenMaps(map, options);
                            map.forEach((ik, iv) -> {
                                m.put(format("%s___%s", prefix, ik), iv);
                            });
                        } else {
                            m.put(prefix, item);
                        }
                    }
                }
            });
        });
    }

    public static <T> Map<String, T> unflattenMap(Map<String, T> source, String key) {
        Pattern pattern = Pattern.compile(format("^%s(___|_)(.+)$", Pattern.quote(checkNotBlank(key))));
        return (Map) map().accept(m -> {
            source.forEach((k, v) -> {
                Matcher matcher = pattern.matcher(k);
                if (matcher.matches()) {
                    m.put(checkNotBlank(matcher.group(2)), v);
                }
            });
        });
    }

    public static <T> List<Map<String, T>> unflattenListOfMaps(Map<String, T> source, String key) {
        return (List) list().accept(l -> {
            for (int i = 0; i < MAX_VALUE; i++) {
                Pattern pattern = Pattern.compile(format("^%s(___|_)%s(___|_)(.+)$", Pattern.quote(checkNotBlank(key)), i));
                Map<String, T> item = map();
                source.forEach((k, v) -> {
                    Matcher matcher = pattern.matcher(k);
                    if (matcher.matches()) {
                        item.put(checkNotBlank(matcher.group(3)), v);
                    }
                });
                if (item.isEmpty() && !source.keySet().stream().anyMatch(Pattern.compile(format("^%s(___|_)%s$", Pattern.quote(checkNotBlank(key)), i)).asMatchPredicate())) {
                    break;
                } else {
                    l.add(item);
                }
            }
        });
    }

    public enum InlineOptions {
        KEEP_ORIGINAL
    }
}
