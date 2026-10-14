/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import java.io.IOException;
import static java.lang.Math.toIntExact;

public interface BigInputStream {

    long availableLong() throws IOException;

    default int available() throws IOException {
        long available = availableLong();
        return available > Integer.MAX_VALUE ? Integer.MAX_VALUE : toIntExact(available);
    }
}
