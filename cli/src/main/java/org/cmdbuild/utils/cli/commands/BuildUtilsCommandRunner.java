/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.collect.Iterables.getFirst;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.CheckBoxList;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import jakarta.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.function.Predicate.not;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.systemplugin.SystemPlugin;
import static org.cmdbuild.systemplugin.SystemPluginUtils.addPluginFilesToWarFileInplace;
import static org.cmdbuild.systemplugin.SystemPluginUtils.removePluginsFromWarFileInplace;
import static org.cmdbuild.systemplugin.SystemPluginUtils.scanFileForPlugin;
import static org.cmdbuild.systemplugin.SystemPluginUtils.scanFolderForPlugins;
import static org.cmdbuild.systemplugin.SystemPluginUtils.scanWarFileForPlugins;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.cli.Main.isRunningFromWarFile;
import static org.cmdbuild.utils.cli.Main.isRunningFromWebappDir;
import static org.cmdbuild.utils.cli.commands.RestCommandRunner.printSystemPlugins;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandParser.printActionHelp;
import org.cmdbuild.utils.cli.utils.CliCommandUtils;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.prepareAction;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmExecutorUtils.unsafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.and;

public class BuildUtilsCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    private File libsFolder;

    public BuildUtilsCommandRunner() {
        super(list("build", "b"), "build utils");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("libs", true, "folder with extra libs/plugins");
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable build methods:");
        printActionHelp(actions);
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no action requested, doing nothing...");
        } else {
            CliCommandUtils.ExecutableAction action = prepareAction(actions, iterator);
            if (cmd.hasOption("libs")) {
                libsFolder = new File(cmd.getOptionValue("libs"));
                checkArgument(libsFolder.isDirectory(), "invalid libs folder");
            }
            action.execute();
        }
    }

    @CliCommand
    protected void listPluginLibs(String pluginFile) {
        SystemPlugin plugin = scanFileForPlugin(new File(pluginFile));
        System.err.printf("processed plugin = %s\nfound %s required lib[s]:\n", plugin.getNameVersion(), plugin.getRequiredLibs().size());
        plugin.getRequiredLibFiles().forEach(l -> System.out.println(l));
    }

    @CliCommand("pluginfiles")
    protected void listPluginFiles(@Nullable String warFileOrWebappDir) {
        list(scanFolderForPlugins(getLibFolder(warFileOrWebappDir))).flatMap(p -> list(p.getFilename()).with(p.getRequiredLibFiles())).sorted().forEach(System.out::println);
    }

    @CliCommand(alias = {"listsystemplugins", "listplugins", "listplugin"})
    protected void listSystemPlugin(@Nullable String warFileOrWebappDir) {
        File file = getWarFile(warFileOrWebappDir);
        System.out.printf("scan file/folder: %s\n", file.getAbsolutePath());
        printSystemPlugins(scanWarFileForPlugins(file));
    }

    @CliCommand(alias = {"addsystemplugins", "addplugins", "addplugin"})
    protected void addSystemPlugin(String plugins) {
        checkArgument(isRunningFromWarFile() || isRunningFromWebappDir(), "cannot add system plugin, missing war file or webapp dir");
        addSystemPlugin(getCliHome().getAbsolutePath(), plugins);
    }

    @CliCommand(alias = {"addsystemplugins", "addplugins", "addplugin"})
    protected void addSystemPlugin(String warFile, String plugins) {
        List<File> files = list(plugins.contains(",") ? Splitter.on(",").trimResults().omitEmptyStrings().splitToList(plugins) : singletonList(plugins)).flatMap(name -> new File(name).isDirectory() ? list(new File(name).listFiles()) : singletonList(new File(name)));
        System.out.printf("add system plugin[s]: \n\t%s\nto war file or webapp folder = %s\nprocessing... ", Joiner.on("\n\t").join(files), warFile);
        addPluginFilesToWarFileInplace(new File(warFile), files);
        System.out.println("OK");
    }

    @CliCommand(alias = {"removesystemplugins", "removeplugins", "removeplugin"})
    protected void removeSystemPlugin(String plugins) {
        checkArgument(isRunningFromWarFile() || isRunningFromWebappDir(), "cannot remove system plugin, missing war file or webapp dir");
        removeSystemPlugin(getCliHome().getAbsolutePath(), plugins);
    }

    @CliCommand(alias = {"removesystemplugins", "removeplugins", "removeplugin"})
    protected void removeSystemPlugin(String warFile, String plugins) {
        List<String> list = checkNotEmpty(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(plugins));
        System.out.printf("remove system plugin[s]: \n\t%s\nfrom war file or webapp folder = %s\nprocessing... ", Joiner.on("\n\t").join(list), warFile);
        removePluginsFromWarFileInplace(new File(warFile), list);
        System.out.println("OK");
    }

    @CliCommand(alias = {"configureSystemPlugin", "configureplugins", "configureplugin", "setplugins", "setplugin"})
    protected void configureSystemPlugins(@Nullable String warFileOrWebappDir) throws IOException {
        File file = getWarFile(warFileOrWebappDir);
        System.out.printf("scan file/folder: %s\n", file.getAbsolutePath());
        Map<String, SystemPlugin> actualPlugins = map(scanWarFileForPlugins(file), SystemPlugin::getName);
        List<String> toremove = list(), toadd = list();

        Map<String, SystemPlugin> availablePlugins = map(libsFolder != null ? scanFolderForPlugins(libsFolder) : emptyList(), SystemPlugin::getName).withoutKeys(actualPlugins.keySet());

        UnixTerminal terminal = new UnixTerminal();

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        BasicWindow window = new BasicWindow("Select System Plugins");

        Panel panel = new Panel();
        window.setComponent(panel);

        CheckBoxList<String> checkBoxList = new CheckBoxList<>();
        panel.addComponent(checkBoxList);
        list(actualPlugins.keySet()).with(availablePlugins.keySet()).sorted().forEach(p -> checkBoxList.addItem(p, actualPlugins.containsKey(p)));

        Panel buttonPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        panel.addComponent(buttonPanel);

        Button submitButton = new Button("Apply", unsafe(() -> {
            list(checkBoxList.getItems()).filter(and(actualPlugins::containsKey, not(checkBoxList::isChecked))).forEach(toremove::add);
            list(checkBoxList.getItems()).filter(and(not(actualPlugins::containsKey), checkBoxList::isChecked)).forEach(toadd::add);
            window.close();
        }));
        buttonPanel.addComponent(submitButton);

        Button cancelButton = new Button("Cancel", unsafe(() -> {
            window.close();
        }));
        buttonPanel.addComponent(cancelButton);

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        gui.addWindowAndWait(window);

        screen.stopScreen();

        if (!toremove.isEmpty() || !toadd.isEmpty()) {
            if (!toadd.isEmpty()) {
                System.out.printf("add system plugin[s]: %s ...", Joiner.on(", ").join(toadd));
                addPluginFilesToWarFileInplace(file, list(toadd).map(availablePlugins::get).flatMap(p -> list(p.getFilename()).with(p.getRequiredLibFiles()).distinct().map(f -> findFileByName(libsFolder, f))));
                System.out.println("done");
            }
            if (!toremove.isEmpty()) {
                System.out.printf("remove system plugin[s]: %s ...", Joiner.on(", ").join(toremove));
                removePluginsFromWarFileInplace(file, toremove);
                System.out.println("done");
            }
            System.out.println("\nactual system plugin[s]:");
            printSystemPlugins(scanWarFileForPlugins(file));
        }
    }

    private File getWarFile(@Nullable String warFile) {
        if (isNotBlank(warFile)) {
            return new File(warFile);
        }
        checkArgument(isRunningFromWarFile() || isRunningFromWebappDir(), "cannot configure system plugins, missing war file or webapp dir");
        return getCliHome();
    }

    private File getLibFolder(@Nullable String dir) {
        dir = firstNotBlank(dir, ".");
        return checkNotNull(getFirst(list(new File(dir, "webapps/cmdbuild/WEB-INF/lib"), new File(dir, "WEB-INF/lib"), new File(dir)).accept(l -> {
            if (isRunningFromWarFile() || isRunningFromWebappDir()) {
                l.add(getCliHome());
            }
        }).filter(File::exists), null), "invalid lib folder =< %s >", dir).getAbsoluteFile();
    }

    private static File findFileByName(File dir, String name) {
        checkNotBlank(name);
        return FileUtils.listFiles(dir, null, true).stream().filter(f -> equal(f.getName(), name)).collect(onlyElement("file not found for name =< %s >", name));
    }
}
