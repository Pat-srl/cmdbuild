/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import static java.lang.Integer.parseInt;
import java.util.function.Consumer;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DxfStreamProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TagHandler tagHandler = new TagHandler();
    private final ValueHandler valueHandler = new ValueHandler();

    private final Consumer<DxfStreamEvent> consumer;

    private Consumer<String> lineHandler = tagHandler;
    private int lineNumber = 0, tagLineNumber, currentGroupCode;

    public DxfStreamProcessor(Consumer<DxfStreamEvent> consumer) {
        this.consumer = checkNotNull(consumer);
    }

//    public void processStream(InputStream stream) {
//        processStream(stream, StandardCharsets.UTF_8);//default charset ??
//    }
//    public void processStream(InputStream stream, Charset charset) {
    public void processStream(InputStreamReader reader) {
        logger.debug("start dxf stream processing");
//        new BufferedReader(new InputStreamReader(stream, charset)).lines().forEach(this::handleLine);
        new BufferedReader(reader).lines().forEach(this::handleLine);
        logger.debug("completed dxf stream processing, {} lines processed", lineNumber);
    }

    private void handleLine(String line) {
        try {
            lineHandler.accept(line);
        } catch (Exception ex) {
            throw new CadException(ex, "error processing dxf line %s", lineNumber);
        }
        lineNumber++;
    }

    private class TagHandler implements Consumer<String> {

        @Override
        public void accept(String groupCodeLine) {
            tagLineNumber = lineNumber;
            currentGroupCode = parseInt(trimAndCheckNotBlank(groupCodeLine));
            lineHandler = valueHandler;
        }
    }

    private class ValueHandler implements Consumer<String> {

        @Override
        public void accept(String valueLine) {
            DxfStreamEvent event = new DxfStreamEventImpl(tagLineNumber, currentGroupCode, valueLine);
            try {
                consumer.accept(event);
            } catch (Exception ex) {
                throw new CadException(ex, "dxf event consumer error");
            }
            currentGroupCode = -1;
            lineHandler = tagHandler;
        }

    }

    private static class DxfStreamEventImpl implements DxfStreamEvent {

        private final int lineNumber, groupCode;
        private final String value;

        public DxfStreamEventImpl(int lineNumber, int groupCode, String value) {
            this.lineNumber = lineNumber;
            this.groupCode = groupCode;
            this.value = nullToEmpty(value);
        }

        @Override
        public int getLineNumber() {
            return lineNumber;
        }

        @Override
        public int getGroupCode() {
            return groupCode;
        }

        @Override
        public String getValue() {
            return value;
        }

    }
}
