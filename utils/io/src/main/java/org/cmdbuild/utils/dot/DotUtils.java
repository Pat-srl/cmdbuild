/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.dot;

import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import jakarta.activation.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DotUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static DataSource dotToImage(String dot) {
        return dotToImage(dot, "neato", "png");
    }

    public static DataSource dotToImage(String dot, String engine, String format) {
        return dotToImage(dot, parseEnum(engine, Engine.class), parseEnum(format, Format.class));
    }

    public static DataSource dotToImage(String dot, Engine engine, Format format) {
        LOGGER.trace("rendering dot graph = \n\n{}\n", dot);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Graphviz.fromString(dot).engine(engine).render(format).toOutputStream(outputStream);
            String mimeType = format("image/%s", format.name().toLowerCase()),
                    fileName = format("diagram.%s", format.name().toLowerCase());
            return newDataSource(outputStream.toByteArray(), mimeType, fileName);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }
}
