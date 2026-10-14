/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import com.google.common.collect.Ordering;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.removeStart;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.executeAction;
import static org.cmdbuild.utils.cli.utils.CliUtils.buildProgressListener;
import org.cmdbuild.utils.console.CmCandidate;
import org.cmdbuild.utils.console.CmConsole;
import org.cmdbuild.utils.ifc.IfcEntry;
import org.cmdbuild.utils.ifc.IfcModel;
import org.cmdbuild.utils.ifc.IfcModelEntryReport;
import org.cmdbuild.utils.ifc.XpathQuery;
import org.cmdbuild.utils.ifc.utils.IfcUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.classNameOrVoid;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.jline.reader.Candidate;
import org.jline.reader.EndOfFileException;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class BimCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public BimCommandRunner() {
        super("bim", "bimserver utils");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        super.printAdditionalHelp();
        System.out.println("\navailable bim utils:");
        actions.values().stream().distinct().forEach((action -> {
            System.out.printf("\t%-32s\t%s\n", action.getHelpAliases(), action.getHelpParameters());
        }));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no method selected, doing nothing...");
        } else {
            executeAction(actions, iterator);
        }
    }

    @CliCommand("exportIfc")
    protected void mapIfc(String filename, String xpathSelector, String mappingfile) throws Exception {
        Map<String, String> mapping = loadProperties(new File(mappingfile));
        IfcModel ifc = loadIfc(filename);
        System.err.printf("export from ifc with xpath selector =< %s > and mapping = \n\n%s\n\nprocessing ...", xpathSelector, mapToLoggableString(mapping));
        List<Map<String, Object>> records = ifc.extractRecords(xpathSelector, mapping);
        System.err.printf(" extracted %s records\n\n", records.size());
        try (CsvListWriter writer = new CsvListWriter(new OutputStreamWriter(System.out), CsvPreference.EXCEL_PREFERENCE)) {
            writer.write(list(mapping.keySet()));
            records.forEach(rethrowConsumer(r -> writer.write(r.values().stream().map(v -> toStringOrEmpty(v)).collect(toList()))));
        }
    }

    @CliCommand("ifc")
    protected void queryIfc(String filename) throws Exception {
        IfcModel ifc = loadIfc(filename);

        AtomicReference<XpathQuery> context = new AtomicReference<>(ifc);

        try (CmConsole console = CmConsole.builder().withCompleter((ParsedLine line, List<Candidate> candidates) -> {
            logger.info("line =< {} >", line.word());
            try {
                String part = line.word();
                Matcher matcher = Pattern.compile("^(.+)/([^/]+)?$").matcher(part);
                if (matcher.matches()) {
                    String autocompleteCtx = matcher.group(1), prefix = defaultString(matcher.group(2));
                    context.get().queryEntries(autocompleteCtx).stream().findFirst().ifPresent(e -> {
                        e.getFeatures().keySet().stream().filter(c -> c.startsWith(prefix)).map(c -> part + removeStart(c, prefix)).map(CmCandidate::new).forEach(candidates::add);
                    });
                } else if (isNotBlank(part)) {
                    if (context.get() instanceof IfcEntry ifcEntry) {
                        ifcEntry.getFeatures().keySet().stream().filter(c -> c.startsWith(part)).map(CmCandidate::new).forEach(candidates::add);
                    } else {
                        ifc.getAvailableClasses().stream().filter(c -> c.startsWith(part)).map(CmCandidate::new).forEach(candidates::add);
                    }
                }
            } catch (Exception ex) {
                logger.warn("error processing autocomplete with line =< {} >", line.word(), ex);
                candidates.clear();
            }
        }).withHistory(".ifc_query_history").build()) {
            while (true) {
                String line = console.readLine("query: ");
                if (isNotBlank(line)) {
                    try {
                        Matcher matcher = Pattern.compile(" *set +context *(.+)", Pattern.CASE_INSENSITIVE).matcher(line);
                        if (matcher.find()) {
                            String query = matcher.group(1);
                            context.set(context.get().queryEntry(query));
                            console.writeLine("query <%s>: ", query);
                        } else if (line.toLowerCase().matches(" *reset +context *")) {
                            context.set(ifc);
                            console.writeLine("query: ");
                        } else if (line.toLowerCase().matches(" *content *")) {
                            ifc.getReport().getEntries().values().stream().sorted(Ordering.natural().onResultOf(IfcModelEntryReport::getCount).reversed()).forEach(e -> System.out.printf("\t%8s %s\n", e.getCount(), e.getName()));
                        } else if (line.toLowerCase().matches(" *explore *.*")) {
                            System.out.println();
                            String expr = Pattern.compile(" *explore *(.*)", Pattern.CASE_INSENSITIVE).matcher(line).replaceAll("$1").trim();
                            if (expr.isBlank()) {
                                new IfcExplorer().explore(0, ((IfcEntry) context.get()));
                            } else {
                                new IfcExplorer().explore(0, context.get().queryEntries(expr));
                            }
                            System.out.println();
                        } else {
                            System.out.println();
                            List list = context.get().query(line.trim());
                            list.forEach(rethrowConsumer(e -> {
                                if (e instanceof IfcEntry entry) {
                                    System.out.printf("\t%s#%s\n%s\n\n", entry.getType(), entry.getId(), mapToLoggableString(entry.asMap()));
                                } else {
                                    System.out.printf("\t%s  (%s)\n", e, getClassOfNullable(e).getName());
                                }
                            }));
                            System.out.printf("query returned %s records\n\n", list.size());
                        }
                    } catch (Exception ex) {
                        System.out.println("ERROR : " + ex.toString());
                        System.out.println();
                    }
                }
            }
        } catch (UserInterruptException | EndOfFileException e) {
            logger.info("user interrupt!");
        }
    }

    private static class IfcExplorer {

        private final int maxDepth = 4;
        private final Set<Long> visited = set();

        public void explore(int depth, List<IfcEntry> entries) {
            entries.forEach(e -> {
                explore(depth, e);
                System.out.println();
            });
        }

        public void explore(int depth, IfcEntry entry) {
            if (depth > maxDepth) {
                System.out.printf("%s%s#%s (max depth exceeded)", "\t".repeat(depth + 1), entry.getType(), entry.getId());
            } else if (visited.contains(entry.getId())) {
                System.out.printf("%s%s#%s (already visited)", "\t".repeat(depth + 1), entry.getType(), entry.getId());
            } else {
                visited.add(entry.getId());
                System.out.println();
                entry.asMap().forEach((k, v) -> {
                    print(depth, k, v);
                });
            }
        }

        private void print(int depth, String k, Object v) {
            if (v instanceof IfcEntry ifcEntry) {
                System.out.printf("\t%s%-40s %20s =\n", "\t".repeat(depth), k, "(" + classNameOrVoid(v) + ")");
                explore(depth + 1, ifcEntry);
                System.out.println();
            } else if (v instanceof Iterable iterable) {
                System.out.printf("\t%s%-40s %20s = ", "\t".repeat(depth), k, "(" + classNameOrVoid(v) + ")");
                if (isEmpty(iterable)) {
                    System.out.println("[]");
                } else {
                    System.out.println();
                    stream(iterable).forEach(e -> {
                        explore(depth + 1, (IfcEntry) e);
                        System.out.println();
                    });
                }
            } else {
                System.out.printf("\t%s%-40s %20s = %s\n", "\t".repeat(depth), k, "(" + classNameOrVoid(v) + ")", abbreviate(v));
            }
        }

    }

    private IfcModel loadIfc(String filename) {
        File file = new File(filename);
        checkArgument(file.isFile(), "invalid file =< %s >", filename);
        System.err.printf("loading ifc from file = %s (%s)\n", file.getAbsolutePath(), byteCountToDisplaySize(file.length()));
        IfcModel ifc = IfcUtils.loadIfc(newDataSource(file), buildProgressListener("ifc loading"));
        System.err.printf("\nready: %s records and %s classes loaded\n", ifc.getReport().getCount(), ifc.getReport().getEntries().size());
        return ifc;
    }
}
