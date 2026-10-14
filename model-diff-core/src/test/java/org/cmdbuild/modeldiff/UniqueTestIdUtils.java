/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Duplicated here from module cmdbuild-test-framework to not import all that
 * module only to use this class.
 *
 * @author afelice
 */
public class UniqueTestIdUtils {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static int i = 0;

    public static void prepareTuid() {
        i++;
    }

    /**
     * @return test unique id (to prefix names and stuff)
     */
    public static String tuid() {
        return Integer.toString(i, 32);
    }

    /**
     * @param id
     * @return param + test unique id
     */
    public static String tuid(String id) {
        return id + tuid();//StringUtils.capitalizeFirstLetter(tuid());
    }

} // end UniqueTestIdUtils class
