/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.sync;

import java.io.File;
import static org.cmdbuild.utils.io.CmIoUtils.cmTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

/**
 *
 * @author afelice
 */
public class TempFileHadler {

    public static File writeToTemp(String content, String fileExt, String filename) {
        File tmpFile = tempFile(filename, fileExt, false);
        writeToFile(content, tmpFile);

        if (!tmpFile.exists()) {
            throw runtime("Unable to create file %s".formatted(tmpFile.getAbsolutePath()));
        }

        return tmpFile;
    }

    public static String getTmpPath() {
        return cmTmpDir().getAbsolutePath();
    }

    public static String readFromTmp(String tempFilename) {
        File tmpFile = new File(cmTmpDir(), tempFilename);
        if (!tmpFile.exists()) {
            throw runtime("couldn't find temp file =< %s >=".formatted(tmpFile.getAbsolutePath()));
        }
        return readToString(tmpFile);
    }

}
