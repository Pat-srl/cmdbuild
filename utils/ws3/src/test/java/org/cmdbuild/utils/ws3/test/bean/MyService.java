/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.test.bean;

import jakarta.ws.rs.QueryParam;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class MyService {

    public Object myMethodOne(@QueryParam("arg1") String arg1, @QueryParam("arg2") String arg2) {
        return map("response", arg1 + "+" + arg2);
    }

    public Object myMethodTwo(@QueryParam("arg1") int arg1, @QueryParam("arg2") int arg2) {
        return map("response", arg1 + arg2);
    }

}
