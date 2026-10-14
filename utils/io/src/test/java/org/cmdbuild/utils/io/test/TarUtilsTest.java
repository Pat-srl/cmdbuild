/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import java.io.File;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmTarUtils.untarToDir;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.cmdbuild.utils.io.CmTarUtils.createTarArchive;

public class TarUtilsTest {

    @Test
    public void testTarData() {
        createTarArchive(list(Pair.of(new BigByteArray("ciao!".getBytes()), "file.txt")));
    }

    @Test
    public void testUntarData() {
        File tempDir = tempDir();
        try {
            untarToDir(new File("src/test/resources/org/cmdbuild/utils/io/test/file.tar"), tempDir);
            assertEquals("ciao\n", readToString(new File(tempDir, "file.txt")));
        } finally {
            deleteQuietly(tempDir);
        }
    }

    @Test
    public void testUntargzData() {
        File tempDir = tempDir();
        try {
            untarToDir(new File("src/test/resources/org/cmdbuild/utils/io/test/file.tar.gz"), tempDir);
            assertEquals("ciao\n", readToString(new File(tempDir, "file.txt")));
        } finally {
            deleteQuietly(tempDir);
        }
    }
}
