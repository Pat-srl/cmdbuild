/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.lock;

import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.lock.LockType.ILT_UNKNOWN;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmStringUtils;

/**
 *
 * @author ataboga
 */
public class LockTypeUtils {

    public static LockType parseLockTypeFromItemId(String itemId) {
        try {
            Matcher matcher = Pattern.compile("^([A-Za-z]+?)_").matcher(itemId);
            String lockType = matcher.group(1);
            return parseEnumOrDefault(lockType, ILT_UNKNOWN);
        } catch (IllegalStateException ex) {
            return ILT_UNKNOWN;
        }
    }

    public static String itemIdWithLockType(LockType lockType, Object... items) {
        return format("%s_%s", serializeEnum(lockType), list(items).map(CmStringUtils::toStringNotBlank).collect(joining("_")));
    }
}
