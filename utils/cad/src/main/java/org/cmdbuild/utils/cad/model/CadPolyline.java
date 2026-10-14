/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.model;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.utils.cad.CadGeometryUtils;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CadPolyline {

    private final List<CadPoint> vertexes;

    public CadPolyline(CadPoint... vertexes) {
        this(ImmutableList.copyOf(vertexes));
    }

    public CadPolyline(List<CadPoint> vertexes) {
        this.vertexes = ImmutableList.copyOf(vertexes);
        checkArgument(!vertexes.isEmpty(), "invalid polyline: vertex list is empty");
    }

    public List<CadPoint> getVertexes() {
        return vertexes;
    }

    @Override
    public String toString() {
        return "CadPolyline{" + Joiner.on(", ").join(vertexes) + '}';
    }

    public boolean contains(CadPoint point) {
        return CadGeometryUtils.contains(this, point);
    }

    public boolean contains(CadPolyline other) {
        return CadGeometryUtils.contains(this, other);
    }

    public boolean intersect(CadPolyline other) {
        return CadGeometryUtils.intersects(this, other);
    }

    public boolean isPoint() {
        return vertexes.size() == 1;
    }

    public CadPoint getCenter() {
        return isPoint() ? getOnlyElement(vertexes) : CadGeometryUtils.getCenter(vertexes);
    }

    public List<Pair<CadPoint, CadPoint>> getLines() {
        List<Pair<CadPoint, CadPoint>> lines = list();
        for (int i = 0; i < vertexes.size() - 1; i++) {
            lines.add(Pair.of(vertexes.get(i), vertexes.get(i + 1)));
        }
        return lines;
    }

    public static CadPolyline fromText(String asText) {
        Matcher matcher = Pattern.compile("(POLYGON)[(][(]([^)]+)[)][)]").matcher(checkNotBlank(asText));
        checkArgument(matcher.matches(), "unsupported gis/text format");
        return new CadPolyline(Splitter.on(",").trimResults().splitToList(checkNotBlank(matcher.group(2))).stream().map(e -> {
            Matcher m = Pattern.compile("([0-9.]+)\\s+([0-9.]+)").matcher(e);
            checkArgument(m.matches(), "unsupported gis/text format");
            return point(toDouble(m.group(1)), toDouble(m.group(2)));
        }).collect(toImmutableList()));
    }

}
