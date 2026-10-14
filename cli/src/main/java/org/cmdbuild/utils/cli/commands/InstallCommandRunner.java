/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import static java.lang.String.format;
import java.util.UUID;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.createDatabase;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import org.cmdbuild.utils.postgres.PostgresUtils;
import org.cmdbuild.utils.console.CmConsole;
import static org.cmdbuild.utils.console.CmConsole.defaultConsole;
import org.cmdbuild.utils.tomcatmanager.TomcatBuilder;
import org.cmdbuild.utils.tomcatmanager.TomcatConfig;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_DEBUG_PORT;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_DEFAULT_DEBUG_PORT;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_DEFAULT_HTTP_PORT;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_DEFAULT_SHUTDOWN_PORT;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_HTTP_PORT;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_PORT_OFFSET;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_SHUTDOWN_PORT;

public class InstallCommandRunner extends AbstractCommandRunner {

    public InstallCommandRunner() {
        super("install", "provision a new cmdbuild+tomcat instance");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        //TODO add install options to skip wizard
        return options;
    }

    public static File getDefaultTomcatLocation() {
        File baseLocation = new File(System.getProperty("user.home"), "cmdbuild_30"), tomcatLocation = baseLocation;
        if (tomcatLocation.exists()) {
            for (int i = 2; tomcatLocation.exists(); i++) {
                tomcatLocation = new File(baseLocation.getParentFile(), format("%s_%s", baseLocation.getName(), i));
            }
        }
        return tomcatLocation;
    }

    public static String getDefaultDatabaseName() {
        return "cmdbuild_" + UUID.randomUUID().toString().substring(0, 4).toLowerCase();
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {

        CmConsole console = defaultConsole();

        System.out.println("\nCMDBuild interactive install wizard - welcome!\n\nthis wizard will guide you in the process of installing and configuring a new instance of CMDBuild!\n");//TODO add version
        File tomcatLocation = getDefaultTomcatLocation();
        boolean hasValidTomcatLocation = false;
        System.out.println();
        while (!hasValidTomcatLocation) {
            try {
                String tomcatLocationValue = trimToNull(console.readLine("tomcat install location <" + tomcatLocation.getAbsolutePath() + "> : "));
                if (tomcatLocationValue != null) {
                    tomcatLocation = new File(tomcatLocationValue).getAbsoluteFile();
                }
                checkArgument(!tomcatLocation.exists(), "tomcat location %s is already in use!", tomcatLocation.getAbsolutePath());
                hasValidTomcatLocation = true;
            } catch (IllegalArgumentException ex) {
                System.err.println("error: " + ex.toString());
            }
        }
        System.out.println();
        int tomcatHttpPort = Integer.parseInt(firstNotNull(trimToNull(console.readLine("tomcat http port <" + TOMCAT_DEFAULT_HTTP_PORT + ">: ")), Integer.toString(TOMCAT_DEFAULT_HTTP_PORT)));
        int tomcatShutdownPort = tomcatHttpPort - TOMCAT_DEFAULT_HTTP_PORT + TOMCAT_DEFAULT_SHUTDOWN_PORT;
        tomcatShutdownPort = Integer.parseInt(firstNotNull(trimToNull(console.readLine("tomcat shutdown port (offset already applied) <" + tomcatShutdownPort + ">: ")), Integer.toString(tomcatShutdownPort)));
        int tomcatDebugPort = tomcatHttpPort - TOMCAT_DEFAULT_HTTP_PORT + TOMCAT_DEFAULT_DEBUG_PORT;
        tomcatDebugPort = Integer.parseInt(firstNotNull(trimToNull(console.readLine("tomcat debug port (offset already applied) <" + tomcatDebugPort + ">: ")), Integer.toString(tomcatDebugPort)));
        boolean hasValidPostgresDatabase = false;
        String hostname = "localhost", port = "5432", adminUser = "postgres", adminPassword = adminUser, databaseName = getDefaultDatabaseName(), limitedUser = databaseName, limitedPassword = limitedUser;
        System.out.println();
        while (!hasValidPostgresDatabase) {
            try {
                {
                    String line = trimToNull(console.readLine("postrgres db <" + hostname + ":" + port + "> : "));
                    if (line != null) {
                        checkArgument(line.matches("[^:]+[:][0-9]+"), "input format error");
                        hostname = line.split(":")[0];
                        port = Integer.valueOf(line.split(":")[1]).toString();
                    }
                }
                {
                    String line = trimToNull(console.readLine("postrgres admin account <" + adminUser + "/" + adminPassword + "> : "));
                    if (line != null) {
                        adminUser = trimToNull(line.split("/")[0]);
                        adminPassword = trimToNull(line.split("/")[1]);
                        checkArgument(adminUser != null && adminPassword != null);
                    }
                }
                String serverVersion = PostgresUtils.newHelper(hostname, Integer.parseInt(port), adminUser, adminPassword).buildHelper().getServerVersion();
                if (!serverVersion.matches("^17[.]*.")) {
                    System.err.printf("WARNING: server version %s is not supported, you may encounter problems\n", serverVersion);
                }
                {
                    String line = trimToNull(console.readLine("cmdbuild posrgres database name <" + databaseName + "> : "));
                    if (line != null) {
                        checkArgument(line.matches("[a-z0-9_]+"), "database name syntax error; must match [a-z0-9_]+");
                        databaseName = limitedUser = limitedPassword = line;
                    }
                }
                hasValidPostgresDatabase = true;
            } catch (IllegalArgumentException ex) {
                System.err.println("error: " + ex.toString());
            }
        }

        String databaseDump = "demo";
        databaseDump = firstNotNull(trimToNull(console.readLine("database dump to load <" + databaseDump + "> : ")), databaseDump);
        if (databaseDump.toLowerCase().equals("demo")) {
            databaseDump = "demo.dump.xz";
        } else if (databaseDump.toLowerCase().equals("empty")) {
            databaseDump = "empty.dump.xz";
        }

        System.out.println("\nwe're ready to begin, this is your configuration: \n");
        System.out.println("\t tomcat: " + tomcatLocation.getAbsolutePath() + "\n\t http port: " + tomcatHttpPort + "\n\t shutdown port: " + tomcatShutdownPort + "\n\t debug port: " + tomcatDebugPort);
        System.out.println("\t postgres database: " + hostname + ":" + port + "/" + databaseName);
        System.out.println("\t database dump: " + databaseDump);
        System.out.println("\nif everything is ok, press ENTER to begin installation");
        console.readLine("<press enter to begin>");
        System.out.println("BEGIN installation");

        DatabaseCreatorConfig databaseCreatorConfig = DatabaseCreatorConfigImpl.builder()
                .withDatabaseUrl(hostname, Integer.valueOf(port), databaseName)
                .withAdminUser(adminUser, adminPassword)
                .withLimitedUser(limitedUser, limitedPassword)
                .withSource(databaseDump)
                //                .withSkipConfigTable(false)//TODO skip config only for empty,demo etc
                .build();

        TomcatConfig tomcatConfig = TomcatConfig.builder()
                .withProperties(getClass().getResourceAsStream("/tomcat-manager-cmdbuild-config-for-wizard.properties"))
                .withProperty(TOMCAT_PORT_OFFSET, Integer.toString(0))
                .withProperty(TOMCAT_HTTP_PORT, Integer.toString(tomcatHttpPort))
                .withProperty(TOMCAT_SHUTDOWN_PORT, Integer.toString(tomcatShutdownPort))
                .withProperty(TOMCAT_DEBUG_PORT, Integer.toString(tomcatDebugPort))
                .withTomcatInstallDir(tomcatLocation.getAbsolutePath())
                .withProperty("tomcat_deploy_artifacts", getCliHome().getAbsolutePath() + " AS cmdbuild") //TODO choose version; install shark
                .withOverlay("database", null, ImmutableMap.of(
                        "db.url", databaseCreatorConfig.getDatabaseUrl(),
                        "db.username", databaseCreatorConfig.getCmdbuildUser(),
                        "db.password", databaseCreatorConfig.getCmdbuildPassword()))
                .build();

        System.out.println("install tomcat... ");
        new TomcatBuilder(tomcatConfig).buildTomcat();
        System.out.println("OK");

        System.out.println("create database... ");
        createDatabase(databaseCreatorConfig);
        System.out.println("OK");

        System.out.println("\n\ncmdbuild successfully installed! you can find startup/shutdown scripts in dir " + new File(tomcatConfig.getInstallDir(), "bin").getAbsolutePath());
    }
}
