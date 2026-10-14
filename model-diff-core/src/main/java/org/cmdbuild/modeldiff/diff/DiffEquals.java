/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import com.google.common.base.Equivalence;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.common.beans.IdAndDescription;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;

/**
 *
 * @author ataboga
 */
public class DiffEquals extends Equivalence<Object> implements Serializable {

    public static final DiffEquals INSTANCE = new DiffEquals();

    @Override
    public boolean doEquivalent(Object a, Object b) {
        a = convertValue(a);
        b = convertValue(b);
        if (a instanceof Number aa && b instanceof Number bb) {
            return areNumbersEqual(aa, bb);
        }

        return Objects.equals(a, b);
    }

    @Override
    public int doHash(Object o) {
        return o.hashCode();
    }

    private Object readResolve() {
        return INSTANCE;
    }

    private static final long serialVersionUID = 1;

    private static final double NUMERIC_DIFF_EPSILON = 1e-9;

    /**
     * Normalize data when diffing:
     * <ol>
     * <li>handle Ids, between {@link Integer}, {@link Long} and
     * {@link IdAndDescription};
     * <li>handle numeric values, between
     * {@link Float}, {@link Double}, {@link Integer}, {@link Long}, evaluating
     * as equals two numeric values that differ less than
     * {@link #NUMERIC_DIFF_EPSILON}.
     * </ol>
     *
     * @param differingAttribValues
     * @return
     */
    private static boolean areNumbersEqual(Number leftNumber, Number rightNumber) {
        double diff = Math.abs(leftNumber.doubleValue() - rightNumber.doubleValue());
        return diff < NUMERIC_DIFF_EPSILON;
    }

    /**
     * Ids loaded from JSON may be represented as Integer in the card
     * attributes, but Ids (and reference/lookup) are Long. Plus, Ids may be in
     * form of {@link IdAndDescription}.
     *
     * @param obj
     * @return differences long id that differ only by type (Integer, loaded
     * from JSON, and Long or {@link IdAndDescription}, as in CMDBuild
     * {@link Card} id).
     */
    private Object convertValue(Object obj) {
        if (obj instanceof Integer integer) {
            return integer.longValue();
        } else if (obj instanceof Long aLong) {
            return aLong;
        } else if (obj instanceof IdAndDescription idAndDescription) {
            return idAndDescription.getId();
        } else if (obj instanceof Number number) {
            return number;
        } else if (obj instanceof Collection collection && collection.stream().allMatch(l -> NumberUtils.isCreatable(l.toString()))) {
            return collection.stream().map(l -> Long.valueOf(l.toString())).collect(toList());
        } else if (obj.toString().matches("[0-9]{4}-[0-9]{2}-[0-9]{2}(T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]{3})?Z)?")) {
            return toDateTime(obj);
        }
        return obj;
    }
}
