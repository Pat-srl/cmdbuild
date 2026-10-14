/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.date.inner;

import java.time.ZonedDateTime;

public class CmDateTestUtils {

    public static CmTicker getTicker() {
        return CmTicker.getTicker();
    }

    public static void setTicker(ZonedDateTime setTime) {
        getTicker().set(setTime);
    }

    public static void restartTicker() {
        getTicker().resume();
    }

}
