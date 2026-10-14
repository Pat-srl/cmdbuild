/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import java.util.Arrays;
import static org.apache.commons.codec.digest.DigestUtils.sha256;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.utils.crypto.MagicUtils;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xdecodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xencodeBytes;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class TempServiceUtils {

    private static final MagicUtils.MagicUtilsHelper TEMPID_MAGIC = MagicUtils.helper(Arrays.copyOfRange(sha256("CMDBUILD_TEMPID"), 0, 4), 7, 11, 13, 21);

    public static String tempRecordId() {
        return xencodeBytes(TEMPID_MAGIC.embedMagic(randomId().getBytes()));
    }

    public static boolean isTempId(String str) {
        return !NumberUtils.isCreatable(str) && TEMPID_MAGIC.hasMagic(xdecodeBytes(str));
    }

}
