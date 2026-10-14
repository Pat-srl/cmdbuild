/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.webapp.utils;

import org.cmdbuild.utils.io.CmIoUtils;

/**
 *
 * @author ataboga
 */
public class LandingPageUtils {

    public static byte[] generateCustomLandingPage(String title, String details) {
        String landingPage = CmIoUtils.readToString(LandingPageUtils.class.getResourceAsStream("/org/cmdbuild/webapp/cmdbuild_system_page.html"))
                .replace("###title###", title)
                .replace("###details###", details);
        return landingPage.getBytes();
    }
}
