/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

public class ClassMultitenantModeUtils {

    public static ClassMultitenantMode parseClassMultitenantMode(String value) {
        return parseEnum(value, ClassMultitenantMode.class);
    }

    public static String serializeClassMultitenantMode(ClassMultitenantMode mode) {
        return mode.name().toLowerCase().replaceFirst("cmm_", "");
    }

}
