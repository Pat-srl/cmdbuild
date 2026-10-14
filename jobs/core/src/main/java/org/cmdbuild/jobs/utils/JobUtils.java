/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class JobUtils {

    public static String jobDataToJobKey(JobData jobData) {
        String normalizedCode = abbreviate(nullToEmpty(jobData.getCode()), 60).replaceAll("[^a-zA-Z0-9]", "");
        return format("%s_%s_%s", jobData.getType(), normalizedCode, firstNotNull(jobData.getId(), "noid"));
    }
//
//    public static long jobKeyToCardId(String key) {
//        Matcher matcher = Pattern.compile(".*_([0-9]+)").matcher(checkNotBlank(key));
//        checkArgument(matcher.matches(), "invalid syntax for key = %s", key);
//        return toLong(matcher.group(1));
//    }

}
