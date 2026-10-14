/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.webapp.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    public static void main(String[] args) throws Throwable {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        if (argList.size() >= 2 && "CM_START_FROM_WEBAPP_DIR".equals(argList.get(0))) {
            startFromWebappDir(new File(argList.get(1)).getCanonicalFile(), argList.subList(2, argList.size()));
        } else {
            startFromWarFile(argList);
        }
    }

    private static void startFromWebappDir(File cliHome, List<String> argList) throws Throwable {
        List<File> libFiles = Files.find(cliHome.toPath(), Integer.MAX_VALUE, (p, a) -> p.toFile().getName().endsWith(".jar")).map(Path::toFile).sorted((a, b) -> a.toString().compareTo(b.toString())).collect(toList());
        runCli(cliHome, libFiles, argList);
    }

    private static void startFromWarFile(List<String> argList) throws Throwable {
        File cliHome;
        List<File> libFiles;
        URL main = Main.class.getResource(Main.class.getSimpleName() + ".class");
        String filename = main.toString().replaceFirst("jar:file:(.*[.][wj]ar)[!]/.*class", "$1");
        cliHome = new File(filename).getCanonicalFile();
        String info = cliHome.getName() + "|" + cliHome.length() + "|" + cliHome.lastModified();
        File libDir;
        for (int i = 0; true; i++) {
            libDir = new File(System.getProperty("java.io.tmpdir"), "cmdbuild_cli_" + UUID.nameUUIDFromBytes((info + i).getBytes()).toString().toLowerCase().replaceAll("[^a-z0-9]", ""));
            if (!libDir.exists() || new File(libDir, "ok").exists()) {
                break;
            } else {
                System.err.println("warning: broken temp dir " + libDir.getAbsolutePath() + ", using next temp dir");
            }
        }
        if (!libDir.exists()) {
            System.err.print("loading jars ...");
            libDir.mkdirs();
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(cliHome));
            ZipEntry nextEntry;
            byte[] buffer = new byte[1024 * 1024];
            int i = 0;
            while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                if (nextEntry.getName().endsWith(".jar")) {
                    File libFile = new File(libDir, new File(nextEntry.getName()).getName()); //TODO change this, make it work without having to copy all jars to temp dir every time
                    try (FileOutputStream out = new FileOutputStream(libFile)) {
                        int count;
                        while ((count = zipInputStream.read(buffer)) >= 0) {
                            out.write(buffer, 0, count);
                        }
                    }
                    if ((i++) % 5 == 0) {
                        System.err.print(".");
                    }
                }
            }
            new File(libDir, "ok").createNewFile();
            System.err.println(" done");
        }
        libFiles = new ArrayList<>(Arrays.asList(libDir.listFiles()));
        libFiles.removeIf(f -> !f.getName().endsWith(".jar"));
        libFiles.sort((a, b) -> a.toString().compareTo(b.toString()));
        runCli(cliHome, libFiles, argList);
    }

    private static void runCli(File cliHome, List<File> libFiles, List<String> argList) throws Throwable {

        boolean extraVerbose = argList.contains("-vv");

        if (extraVerbose) {
            System.err.printf("start cli from home = %s\n", cliHome.getAbsolutePath());
        }

        String libpathChecksum = "";

        List<URL> urls = new ArrayList<>();
        for (File libFile : libFiles) {
            if (extraVerbose) {
                String libFileChecksum = checksum(libFile);
                System.err.printf("load file = %s checksum = %s\n", libFile.getAbsolutePath(), libFileChecksum);
                libpathChecksum += libFileChecksum;
            }
            urls.add(libFile.toURI().toURL());
        }

        if (extraVerbose) {
            libpathChecksum = checksum(libFiles.stream().sorted((a, b) -> a.getName().compareTo(b.getName())).map(f -> checksum(f)).collect(joining("")));
            System.err.printf("lib path checksum = %s\n", libpathChecksum);
        }

        ClassLoader classLoader = new URLClassLoader(urls.toArray(URL[]::new), Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);

        ClassLoader.getSystemClassLoader();

        Class<?> mainClass = classLoader.loadClass("org.cmdbuild.utils.cli.Main");
        mainClass.getMethod("setCliHome", File.class).invoke(null, cliHome);
        try {
            mainClass.getMethod("main", String[].class).invoke(null, (Object) argList.toArray(String[]::new));
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    private static String checksum(File file) {
        try {
            return checksum(file.getName() + file.length());
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String checksum(String data) throws NoSuchAlgorithmException {
        return new BigInteger(MessageDigest.getInstance("MD5").digest(data.getBytes(StandardCharsets.UTF_8))).abs().toString(Character.MAX_RADIX);
    }

}
