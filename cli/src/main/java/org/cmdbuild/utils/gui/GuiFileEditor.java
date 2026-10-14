/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import static org.cmdbuild.utils.gui.GuiUtils.getCmdbuildIcon;
import static org.cmdbuild.utils.json.CmJsonUtils.isJson;
import static org.cmdbuild.utils.json.CmJsonUtils.isYaml;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToMessage;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JSON;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
import static org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_YAML;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiFileEditor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void editFile(String fileContent, String fileInfo, Consumer<String> callback) {
        doEditFile(fileContent, fileInfo, callback);
    }

    public static String editFile(String fileContent, String fileInfo) {
        return doEditFile(fileContent, fileInfo, null);
    }

    private static String doEditFile(String fileContent, String fileInfo, @Nullable Consumer<String> callback) {
        try {
            boolean enableCallbackMode = callback != null;

            CompletableFuture<String> future = new CompletableFuture<>();
            SwingUtilities.invokeLater(() -> {
                JFrame guiFrame = new JFrame();
                guiFrame.setIconImage(getCmdbuildIcon());
                guiFrame.setTitle(format("Edit %s", fileInfo));

                JPanel content = new JPanel(new BorderLayout());

                RSyntaxTextArea textArea = new RSyntaxTextArea(50, 120);
                textArea.setSyntaxEditingStyle(isJson(fileContent) ? SYNTAX_STYLE_JSON : isYaml(fileContent) ? SYNTAX_STYLE_YAML : SYNTAX_STYLE_UNIX_SHELL);//TODO improve this
                textArea.setText(fileContent);
                RTextScrollPane scrollPane = new RTextScrollPane(textArea);
                scrollPane.setLineNumbersEnabled(true);
                content.add(scrollPane, BorderLayout.CENTER);

                JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JTextField searchField = new JTextField();
                searchField.setToolTipText("search");
                searchField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        String text = searchField.getText();
                        if (text.length() > 1) {
                            SearchContext context = new SearchContext(text);
                            context.setMarkAll(true);
                            context.setMatchCase(false);
                            SearchResult result = SearchEngine.find(textArea, context);
                            if (!result.wasFound()) {
                                int caretPosition = textArea.getCaretPosition();
                                textArea.setCaretPosition(0);
                                result = SearchEngine.find(textArea, context);
                                if (!result.wasFound()) {
                                    textArea.setCaretPosition(caretPosition);
                                }
                            }
                        }
                    }

                });
                searchField.setPreferredSize(new Dimension(250, 30));
                searchField.setMargin(new Insets(5, 5, 5, 5));
                toolBar.add(searchField);
                content.add(toolBar, BorderLayout.NORTH);

                guiFrame.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == 'f' && e.isControlDown()) {//TODO not working, fix this
                            searchField.requestFocusInWindow();
                            searchField.selectAll();
                        }
                    }

                });

                JPanel buttons = new JPanel();
                buttons.setLayout(new GridLayout());

                JButton submitButton = new JButton(enableCallbackMode ? "SAVE" : "SUBMIT");
                submitButton.addActionListener(a -> {
                    String value = textArea.getText();
                    if (enableCallbackMode) {
                        try {
                            callback.accept(value);
                        } catch (Exception ex) {
                            LOGGER.error("error saving content", ex);
                            JOptionPane.showMessageDialog(guiFrame, CmStringUtils.multiline("error: " + exceptionToMessage(ex), 80), "error saving content", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        guiFrame.dispose();
                        future.complete(value);
                    }
                });
                submitButton.setPreferredSize(new Dimension(150, 40));
                GridBagConstraints submitButtonPosition = new GridBagConstraints();
                submitButtonPosition.gridx = 0;
                submitButtonPosition.gridy = 0;
                submitButtonPosition.insets = new Insets(20, 10, 5, 10);
                buttons.add(submitButton, submitButtonPosition);

                JButton cancelButton = new JButton(enableCallbackMode ? "CLOSE" : "CANCEL");
                cancelButton.addActionListener(a -> {
                    guiFrame.dispose();
                    future.complete(fileContent);
                });
                cancelButton.setPreferredSize(new Dimension(150, 40));
                GridBagConstraints cancelButtonPosition = new GridBagConstraints();
                cancelButtonPosition.gridx = 1;
                cancelButtonPosition.gridy = 0;
                cancelButtonPosition.insets = new Insets(20, 10, 5, 10);
                buttons.add(cancelButton, cancelButtonPosition);

                guiFrame.add(content, BorderLayout.CENTER);
                guiFrame.add(buttons, BorderLayout.SOUTH);

                guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                guiFrame.pack();
                guiFrame.setLocationByPlatform(true);
                guiFrame.setVisible(true);
                guiFrame.addWindowStateListener(new WindowAdapter() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                        scrollPane.getVerticalScrollBar().setValue(0); //TODO not working, fix this						
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        if (!future.isDone()) {
                            future.complete(fileContent);
                        }
                    }

                });
            });
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw runtime(ex);
        }
    }

}
