/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.utils.csv;

import com.google.common.base.Splitter;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.activation.DataSource;
import static java.lang.Math.min;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.io.CmIoUtils.readLines;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class CsvUtils {

    @Nullable
    public static String detectCsvSeparatorOrNull(DataSource data) {
        List<String> lines = list(readLines(readToString(data))).without(StringUtils::isBlank);
        if (lines.size() < 2) {
            return null;
        } else {
            Collection<String> candidates = lines.get(0).chars().mapToObj(i -> Character.valueOf((char) i).toString()).distinct().sorted().collect(toSet());
            candidates.retainAll(set(";", ",", "\t"));
            Set<String> accepted = set();
            candidates.forEach(candidateSeparator -> {
                FluentMap<Integer, Integer> columnCountForRows = map();
                int lineCount = min(100, lines.size()), filterMargin = lineCount / 10, minValidCount = lineCount * 8 / 10;
                lines.stream().limit(lineCount).forEach(l -> {
                    int columnCount = Splitter.on(candidateSeparator).splitToList(l).size();
                    columnCountForRows.put(columnCount, columnCountForRows.getOrDefault(columnCount, 0) + 1);
                });
                columnCountForRows.filterValues(i -> i >= filterMargin);
                if (columnCountForRows.size() == 1 && getOnlyElement(columnCountForRows.values()) >= minValidCount) {
                    accepted.add(candidateSeparator);
                }
            });
            if (accepted.size() == 1) {
                return getOnlyElement(accepted);
            } else {
                return null;
            }
        }
    }
}
