/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.handler;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import java.io.File;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.dms.sharepoint.SharepointDmsAuthProtocol;
import org.cmdbuild.dms.sharepoint.SharepointDmsUtils;
import static org.cmdbuild.dms.sharepoint.SharepointDmsUtils.SHAREPOINT_ENTRY_NAME;
import static org.cmdbuild.dms.sharepoint.SharepointDmsUtils.buildSharepointClient;
import org.cmdbuild.dms.sharepoint.SharepointGraphApiClient;
import org.cmdbuild.dms.sharepoint.config.SharepointConfiguration;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_CLIENT_ID;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_CLIENT_SECRET;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_PROTOCOL;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_RESOURCE_ID;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_SERVICE_URL;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_SERVICE_URL_DEFAULT;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_TENANT_ID;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_GRAPH_API_BASE_URL;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_GRAPH_API_BASE_URL_DEFAULT;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_PASSWORD;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_URL;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_USER;
import org.cmdbuild.etl.EtlException;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_DIRECTORYREADER;
import static org.cmdbuild.etl.handler.FileReaderEtlLoadHandler.FILEREADER_FILENAME_META;
import static org.cmdbuild.etl.handler.PostImportAction.PIA_DISABLE_FILES;
import static org.cmdbuild.etl.handler.PostImportAction.PIA_DO_NOTHING;
import static org.cmdbuild.etl.handler.PostImportAction.PIA_MOVE_FILES;
import org.cmdbuild.etl.job.EtlLoadHandler;
import org.cmdbuild.etl.job.EtlLoaderApi;
import static org.cmdbuild.etl.utils.EtlUtils.PROCESSED_FILE_REGEXP;
import static org.cmdbuild.etl.utils.EtlUtils.filenameForProcessedFile;
import static org.cmdbuild.etl.utils.EtlUtils.handlePostImportAction;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DirectoryReaderEtlLoadHandler implements EtlLoadHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final DmsConfiguration dmsConfiguration;

    public DirectoryReaderEtlLoadHandler(GlobalConfigService configService, DmsConfiguration dmsConfiguration) {
        this.configService = checkNotNull(configService);
        this.dmsConfiguration = checkNotNull(dmsConfiguration);
    }

    @Override
    public String getType() {
        return ETLHT_DIRECTORYREADER;
    }

    @Override
    public WaterwayMessageData load(EtlLoaderApi api) {
        return new DirectoryReaderHelper(api).load();
    }

    private class DirectoryReaderHelper {

        private final EtlLoaderApi api;
        private final DirectorySource source;

        private final String path, filePattern, excludePattern, targetDirectory;
        private final boolean recursive;
        private final PostImportAction postImportAction;

        private int index = 1;

        public DirectoryReaderHelper(EtlLoaderApi api) {
            this.api = checkNotNull(api);
            source = buildSource(api);
            path = api.getConfigNotBlank("path");
            filePattern = api.getConfig("filePattern");
            recursive = toBooleanOrDefault(api.getConfig("recursive"), true);
            postImportAction = parseEnumOrDefault(api.getConfig("postImportAction"), PIA_DO_NOTHING);
            excludePattern = equal(postImportAction, PIA_DISABLE_FILES) ? PROCESSED_FILE_REGEXP : null;
            targetDirectory = equal(postImportAction, PIA_MOVE_FILES) ? api.getConfigNotBlank("targetDirectory") : null;
        }

        public WaterwayMessageData load() {
            List<String> files = source.listSourceFiles();
            if (files.isEmpty()) {
                logger.debug("no file to process, skip");
                return WaterwayMessageDataImpl.builder().withMeta(api.getMeta()).build();
            } else {
                return WaterwayMessageDataImpl.builder().withMeta(api.getMeta()).accept(b -> files.forEach(f -> {
                    logger.debug("loading file =< {} >", f);
                    try {
                        DataSource data = source.loadFile(f);
                        logger.info("found file =< {} > ( {} {} )", f, byteCountToDisplaySize(countBytes(data)), data.getContentType());

                        b.withAttachments(WaterwayMessageAttachmentImpl.builder().withName(format("output_%s_%s", index++, FilenameUtils.getName(f))).fromObject(data).withMeta(FILEREADER_FILENAME_META, FilenameUtils.getName(f)).build());
//                        l.add(new EtlHandlerContextImpl(data, map(api.getMeta()).with(FILEREADER_FILENAME_META, FilenameUtils.getName(f))));
                        try {
                            logger.debug("execute post import action on file =< {} > action = {}", f, postImportAction);
                            source.applyPostImportAction(f);
                        } catch (Exception ex) {
                            logger.warn(marker(), "error executing post import action on file =< {} > action = {}", f, postImportAction, ex);
                        }
                    } catch (Exception ex) {
                        logger.warn(marker(), "error loading file =< {} >", f, ex);
                    }
                })).build();
            }
        }

        private DirectorySource buildSource(EtlLoaderApi api) {
            return switch (parseEnum(api.getConfigNotBlank("source"), DirectorySourceType.class)) {
                case DS_FILESYSTEM ->
                    new FilesystemDirectorySource();
                case DS_EXTERNALDMS ->
                    switch (api.getConfigNotBlank("externaldms_type")) {
                        case "sharepoint_online" ->
                            new SharepointOnlineDirectorySource(new SharepointConfigurationImpl(map(api.getConfig()).filterMapKeys("externaldms_sharepoint_").mapKeys(s -> s.replaceAll("_", "."))));
                        default ->
                            throw new UnsupportedOperationException(format("invalid external dms source =< %s >", api.getConfigNotBlank("externaldms_type")));
                    };
                case DS_INTERNALDMS -> {
                    checkArgument(dmsConfiguration.isEnabled(), "invalid internal dms source: dms service is not enabled");
                    yield switch (dmsConfiguration.getService()) {
                        case "sharepoint_online" ->
                            new SharepointOnlineDirectorySource(new SharepointConfigurationImpl(map(configService.getConfigOrDefaultsAsMap()).filterMapKeys("org.cmdbuild.dms.service.sharepoint.")));
                        default ->
                            throw new UnsupportedOperationException("invalid internal dms source: current dms service provider not supported");
                    };
                }
                default ->
                    throw new UnsupportedOperationException("unsupported directory reader source");
            };
        }

        private class FilesystemDirectorySource implements DirectorySource {

            @Override
            public List<String> listSourceFiles() {
                File baseDir = new File(path);
                if (!baseDir.exists()) {
                    logger.warn(marker(), "CM: source directory not found for path =< {} >", path);
                    return emptyList();
                } else {
                    checkArgument(baseDir.isDirectory(), "CM: invalid source directory =< %s >", path);
                    return list(FileUtils.listFiles(baseDir, new AbstractFileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.isFile() && (isBlank(excludePattern) || !file.getName().matches(excludePattern)) && (isBlank(filePattern) || file.getName().matches(filePattern));
                        }
                    }, recursive ? TrueFileFilter.INSTANCE : null)).map(File::getAbsolutePath).sorted();
                }
            }

            @Override
            public DataSource loadFile(String file) {
                return toDataSource(new File(file));
            }

            @Override
            public void applyPostImportAction(String filename) {
                handlePostImportAction(new File(filename), postImportAction, () -> new File(targetDirectory));
            }

            @Override
            public void close() throws Exception {
                //nothing to do
            }

        }

        private class SharepointOnlineDirectorySource implements DirectorySource {

            private final SharepointGraphApiClient client;

            public SharepointOnlineDirectorySource(SharepointConfiguration config) {
                client = buildSharepointClient(config);
            }

            @Override
            public List<String> listSourceFiles() {
                Queue<String> queue = new ConcurrentLinkedQueue<>(singleton(path));
                FluentList<String> files = list();
                while (!queue.isEmpty()) {
                    String folder = queue.poll();
                    logger.debug("list source files from folder =< {} >", folder);
                    List<Map<String, Object>> content = client.listFolderContent(folder);
                    content.stream().filter(SharepointDmsUtils::isFile)
                            .filter(f -> (isBlank(excludePattern) || !toStringNotBlank(f.get(SHAREPOINT_ENTRY_NAME)).matches(excludePattern)) && (isBlank(filePattern) || toStringNotBlank(f.get(SHAREPOINT_ENTRY_NAME)).matches(filePattern)))
                            .forEach(e -> files.add(format("%s/%s", folder, toStringNotBlank(e.get(SHAREPOINT_ENTRY_NAME)))));
                    if (recursive) {
                        content.stream().filter(SharepointDmsUtils::isFolder).forEach(e -> queue.add(format("%s/%s", folder, toStringNotBlank(e.get(SHAREPOINT_ENTRY_NAME)))));//TODO check this
                    }
                }
                return files.sorted().distinct();
            }

            @Override
            public DataSource loadFile(String file) {
                logger.debug("load content for file =< {} >", file);
                return toDataSource(client.getItemContentByPath(file));
            }

            @Override
            public void applyPostImportAction(String file) {
                switch (postImportAction) {
                    case PIA_DELETE_FILES -> {
                        logger.debug("delete processed file =< {} >", file);
                        client.deleteItemByPath(file);
                    }
                    case PIA_DISABLE_FILES ->
                        client.renameOrMoveItem(file, filenameForProcessedFile(file));
                    case PIA_MOVE_FILES -> {
                        logger.debug("move processed file to {}", targetDirectory);
                        client.renameOrMoveItem(file, format("%s/%s", targetDirectory, FilenameUtils.getName(file)));
                    }
                    case PIA_DO_NOTHING -> {
                    }
                    default ->
                        throw new EtlException("unsupported post import action = %s", postImportAction);
                }
            }

            @Override
            public void close() throws Exception {
                client.close();
            }

        }

        private class SharepointConfigurationImpl implements SharepointConfiguration {

            private final String url, user, password, authResourceId, authClientId, authTenantId, authServiceUrl, authClientSecret, graphApiBaseUrl;
            private final SharepointDmsAuthProtocol authProtocol;

            public SharepointConfigurationImpl(Map<String, String> config) {
                this.url = checkNotBlank(config.get(SHAREPOINT_URL), "missing sharepoint `url` param");
                this.user = checkNotBlank(config.get(SHAREPOINT_USER), "missing sharepoint `user` param");
                this.password = checkNotBlank(config.get(SHAREPOINT_PASSWORD), "missing sharepoint `password` param");
                this.authProtocol = parseEnum(config.get(SHAREPOINT_AUTH_PROTOCOL).toUpperCase().replaceFirst("MSAZUREOAUTH2_", ""), SharepointDmsAuthProtocol.class);
                this.authResourceId = checkNotBlank(config.get(SHAREPOINT_AUTH_RESOURCE_ID), "missing sharepoint `authResourceId` param");
                this.authClientId = checkNotBlank(config.get(SHAREPOINT_AUTH_CLIENT_ID), "missing sharepoint `authClientId` param");
                this.authTenantId = checkNotBlank(config.get(SHAREPOINT_AUTH_TENANT_ID), "missing sharepoint `authTenantId` param");
                this.authServiceUrl = firstNotBlank(config.get(SHAREPOINT_AUTH_SERVICE_URL), SHAREPOINT_AUTH_SERVICE_URL_DEFAULT);
                this.authClientSecret = checkNotBlank(config.get(SHAREPOINT_AUTH_CLIENT_SECRET), "missing sharepoint `authClientSecret` param");
                this.graphApiBaseUrl = firstNotBlank(config.get(SHAREPOINT_GRAPH_API_BASE_URL), SHAREPOINT_GRAPH_API_BASE_URL_DEFAULT);
            }

            @Override
            public String getSharepointUrl() {
                return url;
            }

            @Override
            public String getSharepointUser() {
                return user;
            }

            @Override
            public String getSharepointPassword() {
                return password;
            }

            @Override
            public SharepointDmsAuthProtocol getSharepointAuthProtocol() {
                return authProtocol;
            }

            @Override
            public String getSharepointAuthResourceId() {
                return authResourceId;
            }

            @Override
            public String getSharepointAuthClientId() {
                return authClientId;
            }

            @Override
            public String getSharepointAuthTenantId() {
                return authTenantId;
            }

            @Override
            public String getSharepointAuthServiceUrl() {
                return authServiceUrl;
            }

            @Override
            public String getSharepointAuthClientSecret() {
                return authClientSecret;
            }

            @Override
            public String getSharepointGraphApiBaseUrl() {
                return graphApiBaseUrl;
            }

            @Override
            public boolean autodeleteEmptyDirectories() {
                return false;
            }

        }
    }

    private enum DirectorySourceType {
        DS_FILESYSTEM, DS_INTERNALDMS, DS_EXTERNALDMS
    }

    private interface DirectorySource extends AutoCloseable {

        List<String> listSourceFiles();

        DataSource loadFile(String file);

        void applyPostImportAction(String file);

    }

}
