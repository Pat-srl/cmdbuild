/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static java.lang.String.format;
import static org.cmdbuild.utils.hash.CmHashUtils.compact;

public class UserCardAccessUtils {

    public static String buildFilterMarkName(String key) {
        return format("cm_filter_mark_%s", compact(key, 40));
    }
}
