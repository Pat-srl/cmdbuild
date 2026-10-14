/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto.test;

import static org.cmdbuild.utils.random.CmRandomUtils.DEFAULT_RANDOM_ID_SIZE;
import static org.cmdbuild.utils.random.CmRandomUtils.isRandomId;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomIdTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void randomIdMagic() {
        for (int n = 0; n < 100; n++) {
            String value = randomId();
            logger.debug("test random id =< {} >", value);
            assertEquals(DEFAULT_RANDOM_ID_SIZE, value.length());
            assertTrue(isRandomId(value));
        }
    }
}
