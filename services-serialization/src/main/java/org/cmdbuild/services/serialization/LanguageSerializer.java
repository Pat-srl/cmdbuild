/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;


import org.cmdbuild.common.localization.LanguageInfo;

import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class LanguageSerializer {

    public static Object languageInfoToResponse(LanguageInfo l) {
        return map("code", l.getCode(), "description", l.getDescription());
    }
}
