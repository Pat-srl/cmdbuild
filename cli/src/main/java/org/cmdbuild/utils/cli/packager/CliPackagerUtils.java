/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.packager;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.padEnd;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.readLines;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.fetchFileWithCache;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.io.CmTarUtils.createTarArchive;

public class CliPackagerUtils {

    public static void main(String[] args) {
        File warFile = new File(checkNotBlank(args[0]));
        File targetFile = new File(checkNotBlank(args[1]));
        buildExecutableBashFileFromWarFile(warFile, targetFile);
    }

    public static void buildExecutableBashFileFromWarFile(File warFile, File targetFile) {
        try {

            String headerCode = readToString(CliPackagerUtils.class.getResourceAsStream("/org/cmdbuild/utils/cli/packager/cmdbuild_sh_header.sh")),
                    jvmArgs = readToString(new File(".mvn/jvm.config").getAbsoluteFile());//TODO improve this

            headerCode = headerCode.replace("JVM_ARGS_PLACEHOLDER", jvmArgs);

            Map<String, String> params = readLines(new StringReader(headerCode)).stream().filter(l -> l.matches("[a-z_]+=.*")).map(l -> {
                Matcher matcher = Pattern.compile("([^=]+)=(.*)").matcher(l);
                checkArgument(matcher.find());
                return Pair.of(matcher.group(1), matcher.group(2));
            }).collect(toMap(Pair::getKey, Pair::getValue));

            String javaUrl = checkNotBlank(params.get("java_archive_url")),
                    javaChecksum = checkNotBlank(params.get("java_archive_checksum")),
                    javaFilename = checkNotBlank(params.get("java_archive_filename"));

            File javaFile = fetchFileWithCache(javaChecksum, javaUrl);

            BigByteArray tar = createTarArchive(list(Pair.of(toBigByteArray(javaFile), javaFilename)));

            int headerSize = headerCode.getBytes(StandardCharsets.US_ASCII).length;

            headerCode = headerCode.replaceAll("header_size=[0-9]+", "header_size=" + padEnd(Integer.toString(headerSize), 12, ' '));
            headerCode = headerCode.replaceAll("resources_archive_size=[0-9]+", "resources_archive_size=" + padEnd(Long.toString(tar.length()), 12, ' '));

            try (FileOutputStream out = new FileOutputStream(targetFile); FileInputStream warIn = new FileInputStream(warFile)) {
                out.write(headerCode.getBytes(StandardCharsets.US_ASCII));
                tar.writeTo(out);
                IOUtils.copy(warIn, out);
            }
        } catch (Exception ex) {
            throw runtime(ex, "error building executable cmdbuild from war = %s with target = %s", warFile, targetFile);
        }
    }

}
