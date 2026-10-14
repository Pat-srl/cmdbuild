/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.console;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 *
 * @author ataboga
 */
public class CmConsole implements Closeable {

    private final Terminal terminal;
    private final LineReader lineReader;

    public CmConsole(CmConsoleBuilder builder) throws IOException {
        this.terminal = TerminalBuilder.builder().system(true).build();
        this.lineReader = builder.builder.terminal(terminal).build();
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public LineReader getLineReader() {
        return lineReader;
    }

    public String readLine() {
        return lineReader.readLine();
    }

    public String readLine(String prompt) {
        return lineReader.readLine(prompt);
    }

    public void writeLine(String format, Object... args) {
        terminal.writer().printf(format, args);
        terminal.writer().println();
        terminal.flush();
    }

    @Override
    public void close() throws IOException {
        terminal.close();
    }

    public static CmConsoleBuilder builder() {
        return new CmConsoleBuilder();
    }

    public static CmConsole defaultConsole() {
        return builder().build();
    }

    public static class CmConsoleBuilder implements Builder<CmConsole, CmConsoleBuilder> {

        private final LineReaderBuilder builder = LineReaderBuilder.builder();

        public CmConsoleBuilder withCompleter(BiConsumer<ParsedLine, List<Candidate>> consumer) {
            builder.completer(new Completer() {
                @Override
                public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
                    consumer.accept(line, candidates);
                }
            });
            return this;
        }

        public CmConsoleBuilder withHistory(String fileName) {
            builder.variable(LineReader.HISTORY_FILE, Paths.get(System.getProperty("user.home"), fileName));
            return this;
        }

        @Override
        public CmConsole build() {
            try {
                return new CmConsole(this);
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }
    }
}
