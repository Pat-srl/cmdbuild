/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static java.lang.Math.round;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmObjectSizeTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    @Ignore
    public void testObjectMemoryUsage() {
        memUsage("10 chars string", (i) -> StringUtils.leftPad(Integer.toString(i), 10, '_'));
        memUsage("100 chars string", (i) -> StringUtils.leftPad(Integer.toString(i), 100, '_'));
        //memUsage("1000 chars string", (i) -> StringUtils.leftPad(Integer.toString(i), 1000, '_')); Causes Java heap space out of memory TODO Check
    }

    private void memUsage(String label, Function<Integer, Object> supplier) {
        List<Long> sizes = list();
        int sampleSize = 10000000;

        for (int j = 0; j < 10; j++) {

            Object[] array = new Object[sampleSize];
            long beforeLoad = getUsedMemory();

            for (int i = 0; i < sampleSize; i++) {
                array[i] = supplier.apply(i);
            }

            long afterLoad = getUsedMemory();

            array = new Object[sampleSize];

            long afterUnload = getUsedMemory();

//        logger.debug("size before = {}, after load = {}, after unload = {}", byteCountToDisplaySize(before), byteCountToDisplaySize(after), byteCountToDisplaySize(after2));
            long size = afterLoad - (beforeLoad + afterUnload) / 2;
//            double error = abs(afterUnload - beforeLoad) / ((double) size);
            sizes.add(size);
        }

//        logger.info("avg size of `{}` is {} bytes (err {}%)", label, ((int) (size * 10 / ((double) sampleSize))) / ((double) 10), ((int) error * 1000) / ((double) 10));
        logger.info("avg size of `{}` is {} bytes (err {}%)", label, round(sizes.stream().mapToLong(Long.class::cast).average().getAsDouble() / sampleSize), 1);
    }

    private long getUsedMemory() {
        System.gc();
        sleepSafe(100);
        System.gc();
        sleepSafe(100);
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//        logger.debug("total memory = {}MB, freee memory = {}MB, used memory = {}MB", totalMemory / 1000000, freeMemory / 1000000, usedMemory / 1000000);
//        return usedMemory;
    }
}
