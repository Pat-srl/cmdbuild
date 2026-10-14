/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.gui;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.transformEntries;
import java.awt.BorderLayout;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import static java.lang.String.format;
import java.net.URI;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.DEMO_DUMP;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EMPTY_DUMP;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import org.cmdbuild.utils.cli.CliCommandRunner;
import static org.cmdbuild.utils.cli.commands.InstallCommandRunner.getDefaultDatabaseName;
import static org.cmdbuild.utils.cli.commands.InstallCommandRunner.getDefaultTomcatLocation;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.createDatabase;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.utils.tomcatmanager.TomcatBuilder;
import org.cmdbuild.utils.tomcatmanager.TomcatConfig;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_DEFAULT_HTTP_PORT;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_PORT_OFFSET;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.getValidPortOffsetForDefaultTomcatPorts;
import org.cmdbuild.utils.tomcatmanager.TomcatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.gui.GuiUtils.getCmdbuildIcon;
import static org.cmdbuild.utils.gui.GuiUtils.getCmdbuildLogo;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class GuiCommandRunner implements CliCommandRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JFrame guiFrame;

    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public List<String> getNames() {
        return singletonList(getName());
    }

    @Override
    public String getDescription() {
        return "Graphical user interface (allows wizard cmdbuild install and other features)";
    }

    @Override
    public void exec(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch (Exception ex) {
            }

            guiFrame = new JFrame();
            guiFrame.setIconImage(getCmdbuildIcon());

            JLabel label = new JLabel(new ImageIcon(getCmdbuildLogo()));
            label.setBorder(new EmptyBorder(30, 10, 40, 10));
            guiFrame.add(label, BorderLayout.NORTH);

            JPanel content = new JPanel();

            JButton installButton = new JButton("install CMDBuild V3");
            installButton.setPreferredSize(new Dimension(250, 40));
//        installButton.setVerticalTextPosition(AbstractButton.CENTER);
//        installButton.setHorizontalTextPosition(AbstractButton.LEADING);
            content.add(installButton);
            installButton.addActionListener((a) -> {
                content.removeAll();
                JPanel installUi = buildInstallUi();
                content.add(installUi);
                content.validate();
            });

            guiFrame.add(content, BorderLayout.CENTER);
            guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            guiFrame.setTitle("Cmdbuild GUI");
            guiFrame.setSize(800, 600);
            guiFrame.setLocationByPlatform(true);
            guiFrame.setVisible(true);
        });
    }

    public static boolean canRunGui() {
        return Desktop.isDesktopSupported(); //TODO check this
    }

    private JPanel buildInstallUi() {
        JPanel installUi = new JPanel();
        installUi.setLayout(new GridBagLayout());

        AtomicInteger row = new AtomicInteger(0);

        Map<String, JComponent> configFields = map();
        map("Tomcat dir", "tomcat.dir",
                "Tomcat port", "tomcat.port",
                "Database type", "db.dump",
                "Database host", "db.host",
                "Database port", "db.port",
                "Database name", "db.name",
                "Database user", "db.username",
                "Database password", "db.password",
                "Database admin user", "db.admin.username",
                "Database admin password", "db.admin.password").forEach((k, v) -> {
                    {
                        JLabel label = new JLabel(k + " :", SwingConstants.LEFT);
                        GridBagConstraints labelPosition = new GridBagConstraints();
                        labelPosition.gridx = 0;
                        labelPosition.gridy = row.get();
                        labelPosition.weightx = 1;
                        labelPosition.weighty = 1;
                        labelPosition.insets = new Insets(5, 10, 5, 10);
                        installUi.add(label, labelPosition);
                    }
                    {
                        JComponent field;
                        GridBagConstraints fieldPosition = new GridBagConstraints();
                        fieldPosition.gridx = 1;
                        fieldPosition.gridy = row.getAndIncrement();
                        fieldPosition.weightx = 1;
                        fieldPosition.weighty = 1;
                        switch ((String) v) {
                            case "db.dump":
                                field = new JComboBox(new Object[]{"demo", "empty"});
                                ((JComboBox) field).setEditable(true);
                                break;
                            default:
                                field = new JTextField();
                                field.setBorder(new EmptyBorder(5, 5, 5, 5));
                        }
                        field.setPreferredSize(new Dimension(350, 26));
                        fieldPosition.insets = new Insets(5, 10, 5, 10);
                        installUi.add(field, fieldPosition);
                        configFields.put((String) v, field);
                    }
                });

        map("tomcat.port", TOMCAT_DEFAULT_HTTP_PORT + getValidPortOffsetForDefaultTomcatPorts(),
                "tomcat.dir", getDefaultTomcatLocation().getAbsolutePath(),
                "db.host", "localhost",
                "db.port", "5432",
                "db.username", "cmdbuild",
                "db.password", "cmdbuild",
                "db.admin.username", "postgres",
                "db.admin.password", "postgres",
                "db.name", getDefaultDatabaseName()).forEach((k, v) -> {
                    ((JTextField) configFields.get((String) k)).setText(toStringOrNull(v));//TODO improve this
                });

        Supplier<Map<String, String>> configSupplier = () -> map(transformEntries(configFields, (k, f) -> {
            switch (k) {
                case "db.dump":
                    String source = ((JComboBox) f).getSelectedItem().toString();
                    source = firstNotBlank(source, "empty");
                    return (String) map("empty", EMPTY_DUMP, "demo", DEMO_DUMP).getOrDefault(source, source);
                default:
                    return ((JTextField) f).getText();
            }
        }));

        {
            JButton testButton = new JButton("TEST CONFIG");
            testButton.addActionListener(a -> new InstallHelper(configSupplier.get()).testInstallConfig());
            testButton.setPreferredSize(new Dimension(150, 40));
            GridBagConstraints testButtonPosition = new GridBagConstraints();
            testButtonPosition.gridx = 0;
            testButtonPosition.gridy = row.get();
            testButtonPosition.insets = new Insets(20, 10, 5, 10);
            installUi.add(testButton, testButtonPosition);
        }
        {
            JButton installButton = new JButton("INSTALL");
            installButton.addActionListener(a -> new InstallHelper(configSupplier.get()).install());
            installButton.setPreferredSize(new Dimension(150, 40));
            GridBagConstraints installButtonPosition = new GridBagConstraints();
            installButtonPosition.gridx = 1;
            installButtonPosition.gridy = row.get();
            installButtonPosition.insets = new Insets(20, 10, 5, 10);
            installUi.add(installButton, installButtonPosition);
        }
        return installUi;
    }

    private class InstallHelper {

        private final Map<String, String> config;
        private DatabaseCreatorConfig databaseCreatorConfig;
        private TomcatConfig tomcatConfig;

        public InstallHelper(Map<String, String> config) {
            this.config = checkNotNull(config);

        }

        private void prepareConfig() throws IOException {
            logger.info("prepare db config");
            String source = firstNotBlank(config.get("db.dump"), "empty");
            source = (String) map("empty", EMPTY_DUMP, "demo", DEMO_DUMP).getOrDefault(source, source);
            databaseCreatorConfig = DatabaseCreatorConfigImpl.builder()
                    .withDatabaseUrl(checkNotBlank(config.get("db.host"), "must set database host"),
                            toInt(checkNotBlank(config.get("db.port"), "must set database port")),
                            checkNotBlank(config.get("db.name"), "must set database name"))
                    .withAdminUser(config.get("db.admin.username"), config.get("db.admin.password"))
                    .withLimitedUser(config.get("db.username"), config.get("db.password"))
                    .withSource(source)
                    .build();
            logger.info("prepare tomcat config");
            int tomcatPort = toInt(checkNotBlank(config.get("tomcat.port"), "must set tomcat port")),
                    portOffset = tomcatPort - TOMCAT_DEFAULT_HTTP_PORT;
            tomcatConfig = TomcatConfig.builder()
                    .withProperties(getClass().getResourceAsStream("/tomcat-manager-cmdbuild-config-for-wizard.properties"))
                    .withProperty(TOMCAT_PORT_OFFSET, portOffset)
                    .withTomcatInstallDir(checkNotBlank(config.get("tomcat.dir"), "must set tomcat dir"))
                    .withProperty("tomcat_deploy_artifacts", getCliHome().getAbsolutePath() + " AS cmdbuild") //TODO choose version; install shark
                    .withOverlay("database", null, map(
                            "db.url", databaseCreatorConfig.getDatabaseUrl(),
                            "db.username", databaseCreatorConfig.getCmdbuildUser(),
                            "db.password", databaseCreatorConfig.getCmdbuildPassword()).accept((m) -> {
                        if (databaseCreatorConfig.hasAdminUser()) {
                            m.put(
                                    "db.admin.username", databaseCreatorConfig.getAdminUser(),
                                    "db.admin.password", databaseCreatorConfig.getAdminPassword()
                            );
                        }
                    }))
                    .build();
            logger.info("config is ready");
        }

        public void testInstallConfig() {
            logger.info("test install config");
            try {
                prepareConfig();
                JOptionPane.showMessageDialog(guiFrame, "config is OK", "config OK", INFORMATION_MESSAGE);
            } catch (Exception ex) {
                logger.warn("invalid config", ex);
                JOptionPane.showMessageDialog(guiFrame, "warning: " + ex.toString(), "invalid configuration", WARNING_MESSAGE);
            }
        }

        public void install() {
            try {
                prepareConfig();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(guiFrame, "error: " + ex.toString(), "error installing cmdbuild", ERROR_MESSAGE);
                return;
            }
            JDialog installProgressDialog = new JDialog(guiFrame, "Installation Progress", true);
            JProgressBar progressBar = new JProgressBar();
            progressBar.setPreferredSize(new Dimension(500, 20));
            installProgressDialog.add(progressBar, CENTER);
            JLabel progressMessage = new JLabel("loading");
            progressMessage.setHorizontalAlignment(SwingConstants.CENTER);
            progressMessage.setPreferredSize(new Dimension(500, 40));
            installProgressDialog.add(progressMessage, SOUTH);
            installProgressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            installProgressDialog.pack();
            installProgressDialog.setLocationRelativeTo(guiFrame);
            logger.info("start installation thread");
            new Thread(() -> {
                logger.info("installation thread is running");
                try {
                    try {
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(10);
                            progressMessage.setText(format("installing apache tomcat to %s", tomcatConfig.getInstallDir()));
                        });
                        new TomcatBuilder(tomcatConfig).buildTomcat();
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(60);
                            progressMessage.setText(format("loading database %s", databaseCreatorConfig.getSource()));
                        });
                        createDatabase(databaseCreatorConfig);
                    } finally {
                        SwingUtilities.invokeLater(() -> installProgressDialog.setVisible(false));
                    }
                    SwingUtilities.invokeLater(() -> {
                        int start = JOptionPane.showConfirmDialog(guiFrame, format("cmdbuild succesfully installed on %s\ndo you want to start it now?", tomcatConfig.getInstallDir()), "Installation completed", YES_NO_OPTION);
                        if (start == JOptionPane.NO_OPTION) {
                            System.exit(0);
                        } else {
                            logger.info("starting cmdbuild");
                            JDialog startupProgressDialog = new JDialog(guiFrame, "Installation Progress", true);
                            JProgressBar startupProgressBar = new JProgressBar();
                            startupProgressBar.setPreferredSize(new Dimension(500, 20));
                            startupProgressBar.setIndeterminate(true);
                            startupProgressDialog.add(startupProgressBar, CENTER);
                            JLabel startupProgressMessage = new JLabel(format("starting cmdbuild %s", tomcatConfig.getInstallDir()));
                            startupProgressMessage.setHorizontalAlignment(SwingConstants.CENTER);
                            startupProgressMessage.setPreferredSize(new Dimension(500, 40));
                            startupProgressDialog.add(startupProgressMessage, SOUTH);
                            startupProgressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                            startupProgressDialog.pack();
                            startupProgressDialog.setLocationRelativeTo(guiFrame);
                            new Thread(() -> {
                                logger.info("startup thread is running");
                                try {
                                    try {
                                        new TomcatManager(tomcatConfig).startTomcat().waitForTomcatAndCmdbuildStartup();
                                    } finally {
                                        SwingUtilities.invokeLater(() -> startupProgressDialog.setVisible(false));
                                    }
                                    SwingUtilities.invokeLater(() -> {
                                        //TODO show actual admin account
                                        JOptionPane.showMessageDialog(guiFrame, "cmdbuild succesfully started\nadmin account is admin/admin\nafter closing this dialog you'll be redirected to login page.", "Startup completed", INFORMATION_MESSAGE);
                                        try {
                                            if (Desktop.isDesktopSupported()) {
                                                Desktop.getDesktop().browse(new URI(format("http://localhost:%s/cmdbuild", tomcatConfig.getHttpPort())));
                                            }
                                        } catch (Exception ex) {
                                            logger.error("error opening cmdbuild url", ex);
                                        }
                                        System.exit(0);
                                    });
                                } catch (Exception ex) {
                                    logger.error("error starting tomcat", ex);
                                    SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(guiFrame, "startup error: " + ex.toString(), "error starting cmdbuild", ERROR_MESSAGE);
                                        System.exit(0);
                                    });
                                }
                            }).start();
                            startupProgressDialog.setVisible(true);
                        }
                    });
                } catch (Exception ex) {
                    logger.error("error executing install", ex);
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(guiFrame, "installation error: " + ex.toString(), "error installing cmdbuild", ERROR_MESSAGE));
                }
            }).start();
            installProgressDialog.setVisible(true);
        }

    }

}
