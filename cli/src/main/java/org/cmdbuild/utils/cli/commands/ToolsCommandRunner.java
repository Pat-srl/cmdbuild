/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Splitter;
import com.google.common.net.UrlEscapers;
import groovy.lang.GroovyShell;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.String.format;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.getRecordsFromXlsFile;
import static org.cmdbuild.gis.etl.CadEtlLoadHandler.CMDBUILD_DEFAULT_EPSG;
import static org.cmdbuild.utils.cad.CadUtils.getDwgVersion;
import static org.cmdbuild.utils.cad.CadUtils.parseCadFile;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.inner.Dwg2DxfHelperImpl;
import org.cmdbuild.utils.cad.model.CadEntity;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandParser.printActionHelp;
import org.cmdbuild.utils.cli.utils.CliCommandUtils;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.prepareAction;
import static org.cmdbuild.utils.cli.utils.CliUtils.hasInteractiveConsole;
import org.cmdbuild.utils.console.CmConsole;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import org.cmdbuild.utils.encode.CmPackUtils;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import static org.cmdbuild.utils.gui.GuiFileEditor.editFile;
import org.cmdbuild.utils.hash.CmHashUtils;
import org.cmdbuild.utils.io.CmImageUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.fromYaml;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toYaml;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNullSafe;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowSupplier;
import org.cmdbuild.utils.proxy.CmProxyUtils;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

public class ToolsCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public ToolsCommandRunner() {
        super(list("tools", "t"), "mixed tools");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable methods:");
        printActionHelp(actions);
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no rest call requested, doing nothing...");
        } else {
            CliCommandUtils.ExecutableAction action = prepareAction(actions, iterator);
            action.execute();
        }
    }

    @CliCommand("urlescape")
    protected void urlEncode(String val) {
        System.out.println(UrlEscapers.urlFormParameterEscaper().escape(val));
    }

    @CliCommand
    protected void urlDecode(String val) throws UnsupportedEncodingException {
        System.out.println(URLDecoder.decode(val, StandardCharsets.UTF_8.name()));
    }

    @CliCommand
    protected void analyzeXls(String xlsFile) {
        File file = new File(xlsFile);
        AtomicInteger index = new AtomicInteger(0);
        getRecordsFromXlsFile(newDataSource(file)).forEach(l -> {
            System.out.printf("\n\n=== row %4s ===\n", index.get());
            AtomicInteger columnIndex = new AtomicInteger(0);
            l.forEach(c -> {
                String val;
                if (c instanceof Date) {
                    val = toIsoDateTimeUtc(c);
                } else {
                    val = toStringOrNull(c);
                }
                System.out.printf("%4s.%s: %20s  %s\n", index.get(), Integer.toString(columnIndex.getAndIncrement() + 10, Character.MAX_RADIX).toUpperCase(), format("(%s)", getClassOfNullable(c).getName()), val);
            });
            index.incrementAndGet();
        });
    }

    @CliCommand
    protected void analyzeCad(String cadFile) throws FileNotFoundException {
        System.out.printf("analyzing cad file = %s\n", cadFile);
        if (FilenameUtils.isExtension(cadFile, "dwg")) {
            System.out.printf("dwg version =< %s >\n", firstNotBlank(getDwgVersion(rethrowSupplier(() -> new FileInputStream(cadFile))), "unknown"));
        }
        DxfDocument document = parseCadFile(toDataSource(new File(cadFile)));
        System.out.printf("document version =< %s >\n", document.getAcadVersion());
        List<CadEntity> entities = list(document.getCadEntities(CMDBUILD_DEFAULT_EPSG, true));
        System.out.printf("found %s cad entities\n", entities.size());
        entities.forEach(e -> System.out.printf("\t%s\n", e));
        System.out.printf("%s cad entities OK\n", entities.size());
    }

    @CliCommand
    protected void cadToDxf(String cadFile) {
        cadToDxf(cadFile, new File(new File(cadFile).getParentFile(), FilenameUtils.getBaseName(cadFile) + ".dxf").getPath());
    }

    @CliCommand
    protected void cadToDxf(String sourceFile, String targetFile) {
        File source = new File(sourceFile), target = new File(targetFile);
        System.out.printf("converting cad file = %s to dxf file = %s ...\n", source.getAbsolutePath(), target.getAbsolutePath());
        checkArgument(Objects.equals(FilenameUtils.getExtension(target.getName()), "dxf"));
        writeToFile(new Dwg2DxfHelperImpl().dwg2Dxf(toDataSource(source)), target);
        System.out.println("done");
    }

    @CliCommand
    protected void resizeImage(String sourceFile, String res, String targetFile) {
        File source = new File(sourceFile), target = new File(targetFile);
        System.out.printf("resize image file = %s with res = %s write to file = %s ...\n", source.getAbsolutePath(), res, target.getAbsolutePath());
        byte[] data = toByteArray(source);
        if (res.matches("[0-9]+x[0-9]+")) {
            data = CmImageUtils.resizeImage(data, toInt(Splitter.on("x").splitToList(res).get(0)), toInt(Splitter.on("x").splitToList(res).get(1)));
        } else {
            data = CmImageUtils.resizeImage(data, toInt(res));
        }
        writeToFile(data, target);
        System.out.println("done");
    }

    @CliCommand
    protected void base64encode(String payload) {
        String value = Base64.encodeBase64String(payload.getBytes());
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.print(value);
        }
    }

    @CliCommand
    protected void base64encode() {
        byte[] data = toByteArray(System.in);
        String value = Base64.encodeBase64String(data);
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.print(value);
        }
    }

    @CliCommand
    protected void pack(String source) {
        String value;
        if (new File(source).isFile()) {
            value = CmPackUtils.pack(toByteArray(new File(source)));
        } else {
            value = CmPackUtils.pack(source);
        }
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.println(value);
        }
    }

    @CliCommand
    protected void pax(String source) {
        String value;
        if (new File(source).isFile()) {
            value = CmPackUtils.pax(toByteArray(new File(source)));
        } else {
            value = CmPackUtils.pax(source);
        }
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.println(value);
        }
    }

    @CliCommand
    protected void lpack(String source) {
        String value;
        if (new File(source).isFile()) {
            value = CmPackUtils.lpack(toByteArray(new File(source)));
        } else {
            value = CmPackUtils.lpack(source);
        }
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.println(value);
        }
    }

    @CliCommand
    protected void pack() {
        byte[] data = toByteArray(System.in);
        String value = CmPackUtils.pack(data);
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.println(value);
        }
    }

    @CliCommand
    protected void unpack(String value) throws IOException {
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", CmPackUtils.unpack(value));
        } else {
            System.out.write(unpackBytes(value));
        }
    }

    @CliCommand
    protected void unpack() throws IOException {
        unpack(readToString(System.in));
    }

    @CliCommand
    protected void editpack(String source) throws IOException {
        String value;
        boolean isFile = new File(source).isFile();
        if (isFile) {
            value = CmPackUtils.unpack(readToString(new File(source)));
        } else {
            value = CmPackUtils.unpack(source);
        }
        editFile(value, isFile ? source : "packed value", (output) -> {
            output = CmPackUtils.pack(output);
            if (isFile) {
                writeToFile(new File(source), output);
            } else {
                if (hasInteractiveConsole()) {
                    System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", output);
                } else {
                    System.out.println(output);
                }
            }
        });

    }

    @CliCommand
    protected void hash() {
        byte[] data = toByteArray(System.in);
        String value = CmHashUtils.hash(data);
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.println(value);
        }
    }

    @CliCommand
    protected void hash(String source) throws FileNotFoundException {
        String value;
        if (new File(source).isFile()) {
            value = CmHashUtils.hash(new FileInputStream(source));
        } else {
            value = CmHashUtils.hash(source);
        }
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.println(value);
        }
    }

    @CliCommand
    protected void startProxy(int sourcePort, int destinationPort, String customHeaderKey, String customHeaderValue) throws Exception {
        System.out.printf("start reverse proxy from source url = http://localhost:%s to destination url = http://localhost:%s\nset custom header < %s > = < %s >", sourcePort, destinationPort, customHeaderKey, customHeaderValue);
        CmProxyUtils.newHttpProxy(sourcePort, destinationPort).withCustomHeader(checkNotBlank(customHeaderKey), checkNotBlank(customHeaderValue)).start();
        System.out.println("proxy server is running - press crtl+C to stop");
    }

    @CliCommand
    protected void now() {
        System.out.println(toIsoDateTime(CmDateUtils.now()));
    }

    @CliCommand
    protected void groovy() throws Exception {
        GroovyShell groovy = new GroovyShell();

        try (CmConsole console = CmConsole.builder().withHistory(".cm_groovy_history").build()) {
            while (true) {
                String line = console.readLine("groovy : ");
                if (StringUtils.isNotBlank(line)) {
                    try {
                        Object res = groovy.evaluate(line);
                        if (res != null) {
                            console.writeLine("groovy > %s (%s)", toStringOrNullSafe(res), getClassOfNullable(res).getName());
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

    @CliCommand
    protected void yaml2json(String filename) {
        System.out.println(toPrettyJson(fromYaml(readToString(new File(checkNotBlank(filename))), JsonNode.class)));
    }

    @CliCommand
    protected void json2yaml(String filename) {
        System.out.println(toYaml(fromJson(readToString(new File(checkNotBlank(filename))), JsonNode.class)));
    }

//    private File getLibFolder(@Nullable String dir) {
//        dir = firstNotBlank(dir, ".");
//        return checkNotNull(getFirst(list(new File(dir, "webapps/cmdbuild/WEB-INF/lib"), new File(dir, "WEB-INF/lib"), new File(dir)).filter(File::isDirectory), null), "invalid lib folder =< %s >", dir).getAbsoluteFile();
//    }
}
