/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import static com.google.common.base.Preconditions.checkArgument;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import static java.time.LocalDate.now;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import org.apache.maven.artifact.versioning.ComparableVersion;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgVersionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Map<String, PostgresInfo> PG_VERSION = getPostgresInfoFromWebSite();

    public static ComparableVersion getPostgresServerVersionNum(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("show server_version_num"); ResultSet resultSet = preparedStatement.executeQuery()) {
            checkArgument(resultSet.next(), "unable to get postgres server version");
            int pgServerVersion = resultSet.getInt(1);
            checkArgument(pgServerVersion > 0, "unable to get postgres server version");
            String postgresServerVersionFromNumber = getPostgresServerVersionFromNumber(pgServerVersion);
            return new ComparableVersion(postgresServerVersionFromNumber);
        } catch (SQLException ex) {
            throw runtime(ex);
        }
    }

    public static String getPostgresServerVersionFromNumber(int versionNumber) {
        return Integer.toString(versionNumber)
                .replaceFirst("^([0-9]{1,2})([0-9]{2})([0-9]{2})$", "$1.$2.$3")
                .replaceAll("[.]0([0-9])", ".$1")
                .replaceAll("^([0-9]{2}.)0.", "$1");
    }

    public static PostgresInfo getPostgresInfo(String version) {
        return PG_VERSION.get(getMajorPostgresVersion(version));
    }

    public static String getMajorPostgresVersion(String version) {
        return version.replaceAll("^([0-9]{2})\\.?([0-9]+)?", "$1");
    }

    private static Map<String, PostgresInfo> getPostgresInfoFromWebSite() {
        try {
            String url = "https://www.postgresql.org/support/versioning/";
            Document document = Jsoup.connect(url).get();

            Map<String, PostgresInfo> pgVersions = map();
            document.select("table").select("tbody").forEach(table -> {
                table.select("tr").forEach(row -> {
                    Elements cells = row.select("td");
                    if (toDouble(cells.get(0).text()) >= 10) {
                        pgVersions.put(cells.get(0).text(), new PostgresInfo(cells.get(0).text(), cells.get(1).text(), cells.get(4).text()));
                    }
                });
            });
            return pgVersions;
        } catch (Exception ex) {
            LOGGER.warn("error getting postgres version information from https://www.postgresql.org/support/versioning/");
            return map();
        }
    }

    public static class PostgresInfo {

        private final int major;
        private final ComparableVersion currentMinor;
        private final boolean supported;
        private final LocalDate eol;

        private PostgresInfo(String major, String currentMinor, String endOfLife) {
            this.major = toInt(major);
            this.currentMinor = new ComparableVersion(currentMinor);
            this.eol = applyOrNull(endOfLife, eol -> LocalDate.parse(eol, DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)));
            this.supported = applyOrDefault(eol, e -> e.isAfter(now()), true); // default true, skip warning
        }

        public int getMajor() {
            return major;
        }

        public ComparableVersion getCurrentMinor() {
            return currentMinor;
        }

        public boolean isSupported() {
            return supported;
        }

        public LocalDate getEol() {
            return eol;
        }

        @Override
        public String toString() {
            return "PostgresVersion{" + "major=" + major + ", currentMinor=" + currentMinor + ", supported=" + supported + ", eol=" + eol + '}';
        }
    }
}
