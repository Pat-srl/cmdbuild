/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class EasyuploadUtils {

    public final static String ROOT_DIR = "/";

    public static boolean isRoot(String path) {
        return equal(path, ROOT_DIR);
    }

    public static String[] pathToArray(String normalizedPath) {
        return Splitter.on("/").splitToList(normalizedPath).toArray(new String[]{});
    }

    public static String normalizePath(String path) {
        return normalizePath(new String[]{checkNotBlank(path)});
    }

    public static String normalizePath(String... parts) {
        String path = Joiner.on("/").join(parts).replaceFirst("^[.]/", "").replaceAll("/+", "/").replaceAll("^/|/$", "");
        List<String> list = Splitter.on("/").splitToList(path).stream().filter(not(equalTo("."))).collect(toList());
        while (list.contains("..")) {
            int index = list.indexOf("..");
            list.remove(index - 1);
            list.remove(index - 1);
        }
        return firstNotBlank(Joiner.on("/").join(list), ROOT_DIR);
    }

    public static String getFolder(String normalizedPath) {
        return normalizePath(normalizedPath.replaceFirst("[^/]+$", ""));
    }

    public static DataHandler toDataHandler(EasyuploadItem item) {
        return new DataHandler(new EasyuploadItemDataSourceAdapter(item));
    }

    private static class EasyuploadItemDataSourceAdapter implements DataSource {

        private final EasyuploadItem item;

        public EasyuploadItemDataSourceAdapter(EasyuploadItem item) {
            this.item = checkNotNull(item);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(item.getContent());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            return item.getMimeType();
        }

        @Override
        public String getName() {
            return item.getFileName();
        }
    }

}
