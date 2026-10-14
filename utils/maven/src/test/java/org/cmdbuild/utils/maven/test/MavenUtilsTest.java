/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.maven.test;

import java.io.File;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.cmdbuild.utils.maven.MavenUtils.getFileByGav;

public class MavenUtilsTest {

    @Test
    public void testArtifactDownload() {
        File file = getFileByGav("org.slf4j:slf4j-api:1.7.15:jar");
        assertTrue(file.exists());
        assertEquals("slf4j-api-1.7.15.jar", file.getName());
        assertEquals("46c0kvjgbhg7382fl4dmw3qz", hash(toByteArray(file)));
    }

}
