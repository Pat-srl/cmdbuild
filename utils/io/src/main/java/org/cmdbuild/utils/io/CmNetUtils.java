/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.any;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import static java.util.Arrays.asList;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.io.IOUtils;
import org.apache.http.ssl.SSLContextBuilder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmNetUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static TrustManager[] getInsecureTrustManagers() {
        return new TrustManager[]{getInsecureTrustManager()};
    }

    public static TrustManager getInsecureTrustManager() {
        return new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };
    }

    public static SSLContext getInsecureSslContext() {
        try {
            return new SSLContextBuilder().loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
            throw runtime(ex);
        }
    }

    public static String getHostname() {
        String myHostname = "localhost";
        try {
            myHostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
            LOGGER.warn("error retrieving my hostname", ex);
        }
        return myHostname;
    }

    public static String getIpAddr() {
        String myIpAddr = "127.0.0.1";
        try {
            myIpAddr = InetAddress.getByName(getHostname()).getHostAddress();
        } catch (Exception ex) {
            LOGGER.warn("error retrieving my ip addr", ex);
        }
        return myIpAddr;
    }

    public static int scanPortOffset(int intitialOffset, Integer... defaultPorts) {
        return scanPortOffset(intitialOffset, asList(checkNotNull(defaultPorts)));
    }

    public static int getNextAvailablePort(int intitialPort) {
        return getAvailablePort(intitialPort + 1);
    }

    public static int getAvailablePort(int intitialPort) {
        return intitialPort + scanPortOffset(0, intitialPort);
    }

    public static int scanPortOffset(int intitialOffset, Iterable<Integer> defaultPorts) {
        checkNotNull(defaultPorts);
        checkArgument(!any(defaultPorts, isNull()));
        for (int i = intitialOffset; i < Integer.MAX_VALUE; i++) {
            LOGGER.trace("scan port offset = {}", i);
            final int offset = i;
            if (all(defaultPorts, (Integer port) -> isPortAvailable(port + offset))) {
                return offset;
            }
        }
        throw new RuntimeException("unable to find port offset for available ports!");
    }

    public static void checkPortIsAvailable(int port) {
        checkArgument(isPortAvailable(port), "port %s is not available (already used by some other application)", port);
    }

    public static boolean isPortAvailable(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            return true;
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(serverSocket);
        }
        return false;
    }
}
