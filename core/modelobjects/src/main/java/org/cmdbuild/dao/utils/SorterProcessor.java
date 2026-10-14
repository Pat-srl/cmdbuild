/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElementDirection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import static java.util.stream.Collectors.toList;

public class SorterProcessor {

    public static <T> Comparator<T> comparatorFromSorter(CmdbSorter sorter, BiFunction<String, T, Comparable> keyToValueFunction) {
        if (sorter.isNoop()) {
            return (a, b) -> 0;
        } else {
            return (a, b) -> {
                for (SorterElement element : sorter.getElements()) {
                    Comparable va  = keyToValueFunction.apply(element.getProperty(), a);
                    Comparable vb = keyToValueFunction.apply(element.getProperty(), b);
                    int dir = element.getDirection().equals(SorterElementDirection.ASC) ? 1 : -1;
                    if (va  != null || vb != null) {
                        int res;
                        if (va  == null) {
                            res = 1;
                        } else if (vb == null) {
                            res = -1;
                        } else {
                            res = va.compareTo(vb);
                        }
                        if (res != 0) {
                            return res * dir;
                        }
                    }
                }
                return 0;

            };
        }
    }

    public static <T> Comparator<T> comparatorFromSorter(CmdbSorter sorter) {
        return comparatorFromSorter(sorter, (BiFunction) DefaultBeanKeyToValueFunction.INSTANCE);
    }

    public static <T> List<T> sorted(List<T> source, CmdbSorter sorter, BiFunction<String, T, Comparable> keyToValueFunction) {
        if (sorter.isNoop()) {
            return source;
        } else {
            return source.stream().sorted((T a, T b) -> {
                for (SorterElement element : sorter.getElements()) {
                    Comparable va  = keyToValueFunction.apply(element.getProperty(), a);
                    Comparable vb = keyToValueFunction.apply(element.getProperty(), b);
                    int dir = element.getDirection().equals(SorterElementDirection.ASC) ? 1 : -1;
                    if (va  != null || vb != null) {
                        int res;
                        if (va  == null) {
                            res = 1;
                        } else if (vb == null) {
                            res = -1;
                        } else {
                            res = va.compareTo(vb);
                        }
                        if (res != 0) {
                            return res * dir;
                        }
                    }
                }
                return 0;

            }).collect(toList());
        }
    }

    public static <T> List<T> sorted(List<T> source, CmdbSorter sorter) {
        return sorted(source, sorter, (BiFunction) DefaultBeanKeyToValueFunction.INSTANCE);
    }

}
