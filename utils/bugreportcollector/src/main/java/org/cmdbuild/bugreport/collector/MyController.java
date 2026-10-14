/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bugreport.collector;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MyController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final File targetDir;
    private final String notificationCommand;

    public MyController() {
        notificationCommand = trimToNull(System.getProperty("org.cmdbuild.bugreport.notificationCommand"));
        targetDir = new File(firstNonNull(trimToNull(System.getProperty("org.cmdbuild.bugreport.dir")), "/tmp/bugreport/"));
        targetDir.mkdirs();
        checkArgument(targetDir.exists() && targetDir.isDirectory(), "error: invalid target directory = '%s'", targetDir);
        logger.info("configured target dir = {}", targetDir.getAbsolutePath());
        logger.info("notification command = {}", firstNotNull(notificationCommand, "<disabled>"));
    }

    @GetMapping("/test")
    @ResponseBody
    public Object test() {
        return map("success", true, "status", "READY");
    }

    @PostMapping("/bugreport")
    @ResponseBody
    public Object uploadBugreport(@RequestParam("file") MultipartFile file) throws IOException {

        logger.info("receiving bug report file");

        String fileName = new File(file.getOriginalFilename()).getName();

        //TODO uploaded file validation
        File targetFile = new File(targetDir, fileName);
        file.transferTo(targetFile);

        logger.info("received bug report file = {}", targetFile.getAbsolutePath());

        String message = "";
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(targetFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equalsIgnoreCase("message.txt")) {
                    logger.info("bug report message = {}", message = readToString(zipInputStream));
                    break;
                }
            }
        }

        if (isNotBlank(notificationCommand)) {
            try {
                List<String> command = list(notificationCommand, targetFile.getAbsolutePath(), message);
                logger.info("execute notification command =< {} >", Joiner.on(" ").join(command));
                Runtime.getRuntime().exec(command.toArray(new String[]{}));
            } catch (Exception ex) {
                logger.warn("error executing notification command = {}", notificationCommand, ex);
            }
        }

        logger.info("processed bug report file = {}", targetFile.getAbsolutePath());

        return map("success", true, "message", "bug report file successfully received");
    }

}
