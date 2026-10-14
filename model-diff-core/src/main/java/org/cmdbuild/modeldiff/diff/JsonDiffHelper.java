/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import com.google.common.collect.MapDifference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author afelice
 */
public class JsonDiffHelper {
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
    public static Map<String, MapDifference.ValueDifference<Object>> normalizeDiff(Map<String, MapDifference.ValueDifference<Object>> differingAttribValues) {
        return handleNumericDiff(handleIdDiff(differingAttribValues));
    }

    /**
     * Normalize data when diffing:
     * <ol>
     * <li>handle Ids, between {@link Integer}, {@link Long} and
     * {@link IdAndDescription}; even in a compound structure;
     * <li>handle numeric values, between
     * {@link Float}, {@link Double}, {@link Integer}, {@link Long}, evaluating
     * as equals two numeric values that differ less than
     * {@link #NUMERIC_DIFF_EPSILON}.
     * </ol>
     *
     * @param differingAttribValues
     * @return
     */
    public static Map<String, MapDifference.ValueDifference<Object>> normalizeDiff_Schema(Map<String, MapDifference.ValueDifference<Object>> differingAttribValues) {
        return handleCompoundDiff(handleNumericDiff(handleIdDiff(differingAttribValues)));
    }

    /**
     * Ids loaded from JSON may be represented as Integer in the card
     * attributes, but Ids (and reference/lookup) are Long. Plus, Ids may be in
     * form of {@link IdAndDescription}.
     *
     * <p>
     * Compare the related left/right values, and remove entry if equals.
     *
     * @param differingAttribValues
     * @return differences without ids that differ only by type (@link Integer},
     * loaded     * from JSON, and Long or {@link IdAndDescription}, as in CMDBuild
     * {@link Card} id).
     */
    public static Map<String, MapDifference.ValueDifference<Object>> handleIdDiff(Map<String, MapDifference.ValueDifference<Object>> differingAttribValues) {
        Map<String, MapDifference.ValueDifference<Object>> result = map();
        differingAttribValues.entrySet().forEach(e -> {
            MapDifference.ValueDifference<Object> toCheckDiff = e.getValue();

            final Object rightValue = toCheckDiff.rightValue();
            final Object leftValue = toCheckDiff.leftValue();
            if (!areIdValuesEqual(rightValue, leftValue)) {
                // Not Ids or different Ids
                result.put(e.getKey(), toCheckDiff);
            }
        });

        return result;
    } // end handleIdDiff method

    /**
     * Numbers loaded from JSON may be represented as Integer even if Long in
     * the card attributes, or {@link Double} representation may be subject to
     * rounding.
     *
     * <p>
     * Compare the related left/right values, and remove entry if equals.
     *
     * @param differingAttribValues
     * @return differences without ids that differ only by type (Integer, loaded
     * from JSON, and Long or {@link IdAndDescription}, as in CMDBuild
     * {@link Card} id).
     */
    public static Map<String, MapDifference.ValueDifference<Object>> handleNumericDiff(Map<String, MapDifference.ValueDifference<Object>> differingAttribValues) {
        Map<String, MapDifference.ValueDifference<Object>> result = map();
        differingAttribValues.entrySet().forEach(e -> {
            MapDifference.ValueDifference<Object> toCheckDiff = e.getValue();

            final Object rightValue = toCheckDiff.rightValue();
            final Object leftValue = toCheckDiff.leftValue();
            if (areNumericValuesEqual(leftValue, rightValue)) {
                return; // Go to next element
            }

            // Different (even numeric) value
            result.put(e.getKey(), toCheckDiff);
        });

        return result;
    } // end handleNumericDiff method    

    private static boolean areIdValuesEqual(Object leftValue, Object rightValue) {
        long rawValue;

        if (rightValue instanceof Integer intVal) {
            rawValue = intVal.longValue();
        } else if (rightValue instanceof Long longVal) {
            rawValue = longVal;
        } else if (rightValue instanceof IdAndDescription idAndDescrVal) {
            rawValue = idAndDescrVal.getId();
        } else {
            // Not an id value
            return false; // Go to next element
        }

        if (leftValue instanceof Integer intVal) {
            if (rawValue == intVal.longValue()) {
                // Same value
                return true;
            }
        } else if (leftValue instanceof Long longVal) {
            if (rawValue == longVal) {
                // Same value
                return true;
            }
        } else if (leftValue instanceof IdAndDescription idAndDescrVal) {
            if (rawValue == idAndDescrVal.getId()) {
                // Same value
                return true;
            }
        }

        return false;
    }

    private static boolean areNumericValuesEqual(Object leftValue, Object rightValue) {
        if (leftValue == null && rightValue == null) {
            // both null
            return true;
        }

        if (leftValue == null || rightValue == null) {
            // one null, the other not null
            return false;
        }

        // both not null
        if (leftValue instanceof Number leftNumber && rightValue instanceof Number rightNumber) {
            return areNumbersEqual(leftNumber, rightNumber);
        }

        return leftValue.equals(rightValue);
    } // end areNumericValuesEqual method

    private static final double NUMERIC_DIFF_EPSILON = 1e-9;

    /**
     * Handle numeric unwanted rounding when deserializing a JSON value with a
     * {@link Card} serialization value.
     *
     * @param leftNumber
     * @param rightNumber
     * @return
     */
    private static boolean areNumbersEqual(Number leftNumber, Number rightNumber) {
        double diff = Math.abs(leftNumber.doubleValue() - rightNumber.doubleValue());
        return diff < NUMERIC_DIFF_EPSILON;
    }

    /**
     * Maps (for {@link LookupValue)s loaded from JSON are represented as
     * <code>int</code> while in CMDBuild are </code>long</i>. But are grafted
     * in a list of maps of lists, so invoking {@link #handleIdDiff()} is not enough.
     *
     * <p>
     * Compare the related left/right values, and remove entry if equals.
     *
     * @param differingAttribValues
     * @return differences without <i>id</i> values in compound structures that
     * differ only by type (<code>int</code>), loaded from JSON, and
     * <code>long</code> or {@link IdAndDescription}, as in CMDBuild.
     */
    public static Map<String, MapDifference.ValueDifference<Object>> handleCompoundDiff(Map<String, MapDifference.ValueDifference<Object>> differingAttribValues) {
        Map<String, MapDifference.ValueDifference<Object>> result = map();
        differingAttribValues.entrySet().forEach(e -> {
            MapDifference.ValueDifference<Object> toCheckDiff = e.getValue();

            final Object rightValue = toCheckDiff.rightValue();
            final Object leftValue = toCheckDiff.leftValue();
            if (isCompoundEqual(rightValue, leftValue)) {
                return; // Go to next element
            }

            // Different (even numeric) value
            result.put(e.getKey(), toCheckDiff);
        });

        return result;
    } // end handleCompoundDiff method        

    /**
     * Recursive method to handle equality of list of maps of values (ids or
     * numeric values) used in {@link LookupValue}
     * serialization/deserialization.
     *
     * @param rightValue
     * @param leftValue
     * @return
     */
    private static boolean isCompoundEqual(Object rightValue, Object leftValue) {
        if (rightValue == null && leftValue == null) {
            // Both null
            return true;
        }

        if (rightValue == null ^ leftValue == null) {
            // Only one null
            return false;
        }

        if (rightValue instanceof List rightValueList && leftValue instanceof List leftValueList) {
            // Liit of values
            if (rightValueList.isEmpty() && leftValueList.isEmpty()) {
                // Empty lists
                return true;
            }

            if (rightValueList.size() != leftValueList.size()) {
                // Different sized lists
                return false;
            }

            // Compare items (exit at first found difference)
            boolean bSameList = true;
            int curPos = 0;
            while (bSameList && curPos < rightValueList.size()) {
                for (int i = 0; i < rightValueList.size(); i++) {
                    Object rItem = rightValueList.get(i);
                    Object lItem = leftValueList.get(i);

                    bSameList = isCompoundEqual(rItem, lItem);

                    // Prepare for next item
                    curPos++;
                }
            }

            return bSameList;
        } else if (rightValue instanceof Map rightValueMap && leftValue instanceof Map leftValueMap) {
            // Map of values
            if (rightValueMap.isEmpty() && leftValueMap.isEmpty()) {
                // Empty Map
                return true;
            }

            if (rightValueMap.size() != leftValueMap.size()) {
                // Different sized maps
                return false;
            }

            if (!rightValueMap.keySet().equals(leftValueMap.keySet())) {
                // Different keys
                return false;
            }

            // Compare items, by key (exit at first difference)
            boolean bSameMap = true;
            Iterator it = rightValueMap.keySet().iterator();
            while (bSameMap && it.hasNext()) {
                Object curKey = it.next();

                Object rItem = rightValueMap.get(curKey);
                Object lItem = leftValueMap.get(curKey);

                bSameMap = isCompoundEqual(rItem, lItem);
            }

            return bSameMap;
        } else {
            // Scalar object
            return leftValue.equals(rightValue)
                    || areIdValuesEqual(leftValue, rightValue)
                    || areNumericValuesEqual(leftValue, rightValue);
        }
    }

    private static boolean isToNormalizeMap(Object rightValue, Object leftValue) {
        return (rightValue instanceof FluentMap && isStandardMap(leftValue))
                || (isStandardMap(rightValue) && leftValue instanceof FluentMap);
    }

    private static boolean isStandardMap(Object aValue) {
        return aValue instanceof Map && !(aValue instanceof FluentMap);
    }

    private static FluentMap toConsistentMap(Object aValue) {
        if (isStandardMap(aValue)) {
            return map(aValue);
        }

        return (FluentMap) aValue;
    }

}
