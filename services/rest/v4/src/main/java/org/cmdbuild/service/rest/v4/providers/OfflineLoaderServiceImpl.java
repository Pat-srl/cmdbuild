/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.providers;

import jakarta.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import static java.util.stream.Collectors.toUnmodifiableList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.lock.LockType.ILT_CARD;
import static org.cmdbuild.lock.LockTypeUtils.itemIdWithLockType;
import org.cmdbuild.modeldiff.core.JsonSerializationMode;
import org.cmdbuild.modeldiff.core.SerializationHandle;
import org.cmdbuild.modeldiff.core.SerializationHandle_FileSystem_Zipped;
import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.data.CmCardAttributesData;
import org.cmdbuild.modeldiff.data.DataMerger;
import org.cmdbuild.modeldiff.data.ModelConfiguration;
import org.cmdbuild.modeldiff.data.deserializer.CardDataDeserializerImpl_OnFileSystem;
import org.cmdbuild.modeldiff.dataset.data.ClasseDataset;
import org.cmdbuild.modeldiff.dataset.data.DataCollector;
import org.cmdbuild.modeldiff.dataset.data.DataDataset;
import org.cmdbuild.modeldiff.dataset.data.GeneratedData;
import org.cmdbuild.modeldiff.dataset.data.GeneratedData_Process;
import org.cmdbuild.modeldiff.dataset.data.ModelCollector;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;
import org.cmdbuild.notification.NotificationService;
import org.cmdbuild.offline.Offline;
import org.cmdbuild.offline.OfflineService;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import org.cmdbuild.requestcontext.RequestContext;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.temp.TempInfoSource.TS_SECURE;
import org.cmdbuild.temp.TempService;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class OfflineLoaderServiceImpl implements OfflineLoaderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean isOfflineAvailable;

    private final RequestContextService requestContextService;
    private final ExecutorService executor;
    private final LockService lockService;
    private final TempService tempService;
    private final OfflineService offlineService;
    private final ModelCollector modelCollector;
    private final DataCollector dataCollector;
    private final DataMerger dataMerger;
    private final NotificationService notificationService;
    private final EmailTemplateService emailTemplateService;
    private final OperationUserSupplier operationUser;
    private final WaterwayService waterwayService;

    /**
     * Used when {@link ModelCollector} service is not available.
     *
     * <p>
     * throws {@link UnsupportedOperationException} for each method invoked.
     */
    public static final ModelCollector DUMMY_MODEL_COLLECTOR = new ModelCollector() {
        private static final String UNSUPPORTED_MESSAGE = "service ModelCollector not available";

        @Override
        public ModelConfiguration collectModel(DataDataset dataset, boolean detailed) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }
    };

    /**
     * Used when {@link DataCollector} service is not available.
     *
     * <p>
     * throws {@link UnsupportedOperationException} for each method invoked.
     */
    public static final DataCollector DUMMY_DATA_COLLECTOR = new DataCollector() {
        private static final String UNSUPPORTED_MESSAGE = "service DataCollector not available";

        @Override
        public SerializationHandle collectData(DataDataset dataset, Map<String, String> bindedFilters, boolean cardInfoOnly, JsonSerializationMode jsonSerializationMode) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }

        @Override
        public SerializationHandle_FileSystem_Zipped collectData(DataDataset dataset, Map<String, String> bindedFilters) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }

        @Override
        public SerializationHandle compareData(DataDataset dataset, Map<String, String> bindedFilters, SerializationHandle serializedData) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }

        @Override
        public DataHandler extractDmsDocument(DataDataset dataset, SerializationHandle_FileSystem_Zipped zippedFileSerializationHandle, String classeName, String cardId, String docZippedName) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }
    };

    /**
     * Used when {@link DataMerger} service is not available.
     *
     * <p>
     * throws {@link UnsupportedOperationException} for each method invoked.
     */
    public static final DataMerger DUMMY_DATA_MERGER = new DataMerger() {
        private static final String UNSUPPORTED_MESSAGE = "service DataMerger not available";

        @Override
        public List<CmCardAttributesData> mergeData(SerializationHandle serializedData, SerializationHandle_FileSystem_Zipped modifiedDataWithDms) {
            throw unsupported(UNSUPPORTED_MESSAGE);
        }
    };

    public OfflineLoaderServiceImpl(RequestContextService requestContextService, LockService lockService, TempService tempService, OfflineService offlineService, List<ModelCollector> modelCollectors, List<DataCollector> dataCollectors, List<DataMerger> dataMergers, NotificationService notificationService, EmailTemplateService emailTemplateService, OperationUserSupplier operationUser, WaterwayService waterwayService) {
        this.requestContextService = checkNotNull(requestContextService);
        this.lockService = checkNotNull(lockService);
        this.tempService = checkNotNull(tempService);
        this.offlineService = checkNotNull(offlineService);
        this.waterwayService = checkNotNull(waterwayService);
        this.isOfflineAvailable = true;

        // Optional model-diff stuff
        if (modelCollectors.isEmpty()) {
            logger.info("loading **dummy** ModelCollector");
            this.modelCollector = DUMMY_MODEL_COLLECTOR;
            this.isOfflineAvailable = false;
        } else {
            // (there is only one service implementation, indeed...)
            logger.info("loading real ModelCollector");
            this.modelCollector = getOnlyElement(modelCollectors, "found many model collector implementation");
        }
        if (dataCollectors.isEmpty()) {
            logger.info("loading **dummy** DataCollector");
            this.dataCollector = DUMMY_DATA_COLLECTOR;
            this.isOfflineAvailable = false;
        } else {
            // (there is only one service implementation, indeed...)
            logger.info("loading real DataCollector");
            this.dataCollector = getOnlyElement(dataCollectors, "found many data collector implementation");
        }
        if (dataMergers.isEmpty()) {
            logger.info("loading **dummy** DataMerger");
            this.dataMerger = DUMMY_DATA_MERGER;
            this.isOfflineAvailable = false;
        } else {
            // (there is only one service implementation, indeed...)
            logger.info("loading real DataMerger");
            this.dataMerger = getOnlyElement(dataMergers, "found many data merger implementation");
        }

        this.notificationService = checkNotNull(notificationService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.operationUser = checkNotNull(operationUser);
        executor = executorService(getClass().getName(), () -> {
            MDC.put("cm_type", "sys");
            MDC.put("cm_id", format("offline:%s", randomId(6)));
        });
    }

    @Override
    public boolean isOfflineAvailable() {
        return isOfflineAvailable;
    }

    @Override
    public ModelConfiguration getDataModel(String offlineCode) {
        String datasetJson = offlineToDatasetJson(offlineService.getActiveForCurrentUserByCode(offlineCode));
        DataDataset dataDataset = CmJsonUtils.fromJson(datasetJson, DataDataset.class);
        return modelCollector.collectModel(dataDataset, true);
    }

    @Override
    public void executeDataFromDataset(String offlineCode, Map<String, String> filters) {
        String datasetJson = offlineToDatasetJson(offlineService.getActiveForCurrentUserByCode(offlineCode));
        DataDataset dataDataset = CmJsonUtils.fromJson(datasetJson, DataDataset.class);
        RequestContext requestContext = requestContextService.getRequestContext();
        String username = operationUser.getUsername();
        executor.submit(() -> {
            requestContextService.initCurrentRequestContext("offline processing job", requestContext);
            try {
                SerializationHandle_FileSystem_Zipped result = dataCollector.collectData(dataDataset, filters);
                lockCards(dataDataset, result);
                sendLoadNotificationFromResult(username, offlineCode, result);
            } catch (Exception ex) {
                logger.error("error processing load offline =< {} >", offlineCode, ex);
            }
            requestContextService.destroyCurrentRequestContext();
            MDC.clear();
        });
    }

    @Override
    public String executeDiffFromData(String offlineCode, Map<String, String> filters, String tempId) {
        String datasetJson = offlineToDatasetJson(offlineService.getActiveForCurrentUserByCode(offlineCode));
        DataDataset dataDataset = CmJsonUtils.fromJson(datasetJson, DataDataset.class);
        DataHandler tempData = tempService.getTempData(tempId);
        File tempFile = CmIoUtils.tempFile(null, "zip");
        copy(tempData, tempFile);
        return dataCollector.compareData(dataDataset, filters, new SerializationHandle_FileSystem_Zipped(tempFile)).getSerializationInfo();
    }

    @Override
    public List<Map<String, Object>> executeMergeFromDiff(String offlineCode, GeneratedDiffData diffData, String tempId) {
        String datasetJson = offlineToDatasetJson(offlineService.getActiveForCurrentUserByCode(offlineCode));
        DataDataset dataDataset = CmJsonUtils.fromJson(datasetJson, DataDataset.class);
        DataHandler tempData = tempService.getTempData(tempId);
        File tempFile = CmIoUtils.tempFile(null, "zip");
        copy(tempData, tempFile);
        SerializationHandle_FileSystem_Zipped result = new SerializationHandle_FileSystem_Zipped(tempFile);
        unlockCards(dataDataset, result);
        if (diffData.isEmpty()) {
            handleProcessesToGate(dataDataset, result); //TODO remove duplicate code
            tempService.deleteTempData(tempId); //TODO remove duplicate code
            return emptyList();
        }
        SerializationHandle_String diffSerialization = new SerializationHandle_String(toJson(diffData));
        List<CmCardAttributesData> mergeData = dataMerger.mergeData(diffSerialization, result);
        handleProcessesToGate(dataDataset, result);
        tempService.deleteTempData(tempId);
        return mergeData.stream().map(CmCardAttributesData::getAttributesSerialization).collect(toUnmodifiableList());
    }

    @Override
    public String uploadToTempService(DataHandler dataHandler) {
        return tempService.helper().withData(dataHandler).withTimeToLive(2592000L).withSource(TS_SECURE).put();
    }

    @Override
    public void sendNotificationForDiff(String offlineCode, String dataTemp) {
        offlineService.getActiveForCurrentUserByCode(offlineCode);
        String username = operationUser.getUsername();
        sendDiffNotificationWithTempId(username, offlineCode, dataTemp);
    }

    private void handleProcessesToGate(DataDataset dataDataset, SerializationHandle_FileSystem_Zipped zippedResult) {
        List<GeneratedData_Process> processes = getGeneratedDataFromZippedResult(zippedResult).data.processes;
        if (!processes.isEmpty()) {
            String gate = format("Offline%sGate", dataDataset.getName());
            logger.debug("found {} modified processes, executing =< {} >", processes.size(), gate);
            waterwayService.submitRequest(gate, toJson(processes));
        }
    }

    private void lockCards(DataDataset dataDataset, SerializationHandle_FileSystem_Zipped zippedResult) {
        getCardIdsWritable(dataDataset, zippedResult).forEach(cardId -> {
            LockResponse lockResponse = lockService.aquireLockTimeToLiveSeconds(itemIdWithLockType(ILT_CARD, cardId), 2592000);
            if (lockResponse.isAquired()) {
                logger.info("offline lock acquired for card =< {} >", cardId);
            } else {
                throw runtime("offline lock not acquired for card =< %s >", cardId);
            }
        });
    }

    private void unlockCards(DataDataset dataDataset, SerializationHandle_FileSystem_Zipped zippedDiffResult) {
        File serializationZipFile = new File(zippedDiffResult.getSerializationInfo());
        try (ZipFile zipFile = new ZipFile(serializationZipFile)) {
            SerializationHandle_FileSystem_Zipped zippedResult;
            ZipEntry fileEntry = zipFile.getEntry("originalDataset/tempId");
            if (fileEntry == null) {
                logger.warn("could not find file entry =< {} > in zip file =< {} >, using diff data to unlock cards", serializationZipFile.getName(), serializationZipFile.getCanonicalPath());
                zippedResult = zippedDiffResult;
            } else {
                String originalTempId = readToString(zipFile.getInputStream(fileEntry));
                File tempFile = CmIoUtils.tempFile(null, "zip");
                try {
                    DataHandler originalTempData = tempService.getTempData(originalTempId);
                    tempService.deleteTempData(originalTempId);
                    copy(originalTempData, tempFile);
                } catch (NullPointerException ex) {
                    logger.warn("original data not found, can't unlock cards or they are already unlocked");
                    return;
                }
                zippedResult = new SerializationHandle_FileSystem_Zipped(tempFile);
            }

            getCardIdsWritable(dataDataset, zippedResult).forEach(cardId -> {
                ItemLock itemLock = lockService.getLockOrNull(itemIdWithLockType(ILT_CARD, cardId));
                if (itemLock != null && !itemLock.isExpired()) {
                    lockService.deleteLock(itemLock.getItemId());
                    logger.info("offline lock for card =< {} > released", cardId);
                } else {
                    logger.error("offline lock not released for card  =< %s >", cardId);
                }
            });
        } catch (IOException ex) {
            throw runtime(" error unzipping content =< %s > from zip file =< %s > - %s", serializationZipFile.getName(), serializationZipFile.toString(), ex);
        }
    }

    private List<Object> getCardIdsWritable(DataDataset dataDataset, SerializationHandle_FileSystem_Zipped zippedResult) {
        DataDataset writableOnlyDataDataset = dataDataset.clone();
        writableOnlyDataDataset.reduceToWritableOnly();
        GeneratedData deserialize = getGeneratedDataFromZippedResult(zippedResult);
        return deserialize.data.classes.stream()
                .filter(c -> list(writableOnlyDataDataset.classes).map(ClasseDataset::getName).contains(c.getName()))
                .flatMap(c -> c.getValues().stream().map(card -> card.get("_id")))
                .collect(toUnmodifiableList());
    }

    private GeneratedData getGeneratedDataFromZippedResult(SerializationHandle_FileSystem_Zipped zippedResult) {
        return new CardDataDeserializerImpl_OnFileSystem().deserialize(zippedResult);
    }

    private void sendLoadNotificationFromResult(String username, String offlineCode, SerializationHandle result) {
        File fileResult = new File(result.getSerializationInfo());
        String tempId = uploadToTempService(CmIoUtils.newDataHandler(fileResult));
        notificationService.sendNotificationFromTemplate(getTemplateOrDefault("SystemOfflineCollectNotification"), getDataTemplate(username, offlineCode, tempId, fileResult.getName()));
    }

    private void sendDiffNotificationWithTempId(String username, String offlineCode, String tempId) {
        notificationService.sendNotificationFromTemplate(getTemplateOrDefault("SystemOfflineDiffNotification"), getDataTemplate(username, offlineCode, tempId, tempService.getTempInfo(tempId).getFileName()));
    }

    private EmailTemplate getTemplateOrDefault(String templateName) {
        EmailTemplate template = emailTemplateService.getByNameOrNull(templateName);
        if (template != null && template.isActive()) {
            return template;
        }
        return emailTemplateService.getByName(format("%sDefault", templateName));
    }

    private Map<String, Object> getDataTemplate(String username, String offlineCode, String tempId, String fileName) {
        return map("username", username, "offlineCode", offlineCode, "offlineDescr", offlineService.getByCode(offlineCode).getDescription(), "tempId", tempId, "fileName", fileName);
    }

    private String offlineToDatasetJson(Offline offline) {
        return toJson(map(
                "_id", offline.getCode(),
                "name", offline.getCode(),
                "description", offline.getDescription(),
                "active", offline.isActive(),
                "classes", getJsonList(offline, "classes"),
                "processes", getJsonList(offline, "processes"),
                "views", getJsonList(offline, "views")
        ));
    }

    private List<Object> getJsonList(Offline offline, String obj) {
        return nullToEmpty((List) fromJson(offline.getMetadata(), MAP_OF_OBJECTS).get(obj));
    }
}
