/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.providers;

import static java.lang.String.format;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_CHAT;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.lock.LockType.ILT_SCHEMA;
import static org.cmdbuild.lock.LockTypeUtils.itemIdWithLockType;
import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.diff.schema.GeneratedDiffSchema;
import org.cmdbuild.modeldiff.loader.SchemaLoaderService;
import org.cmdbuild.modeldiff.schema.SchemaCollector;
import org.cmdbuild.modeldiff.schema.SchemaConfiguration;
import org.cmdbuild.notification.NotificationService;
import org.cmdbuild.requestcontext.RequestContext;
import org.cmdbuild.requestcontext.RequestContextService;

import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElementOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Service implementation for schema management operations including schema
 * availability checks, schema model retrieval, diff processing, and schema
 * merge execution with notifications.
 *
 * <p>
 * This component handles the entire lifecycle of schema operations with proper
 * locking mechanisms, request context management, and notification services for
 * merge completion/errors.</p>
 *
 * @author ataboga
 */
@Component
public class SchemaLoaderServiceImpl implements SchemaLoaderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String SCHEMA_LOCK_ID = "LOCK_ID";

    private final RequestContextService requestContextService;
    private final ExecutorService executor;
    private final LockService lockService;
    private final SchemaCollector schemaCollector;
    private final NotificationService notificationService;
    private final EmailTemplateService emailTemplateService;
    private final OperationUserSupplier operationUser;

    public SchemaLoaderServiceImpl(RequestContextService requestContextService, LockService lockService, List<SchemaCollector> schemaCollector, NotificationService notificationService, EmailTemplateService emailTemplateService, OperationUserSupplier operationUser) {
        this.requestContextService = checkNotNull(requestContextService);
        this.lockService = checkNotNull(lockService);
        this.schemaCollector = getOnlyElementOrNull(schemaCollector);
        this.notificationService = checkNotNull(notificationService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.operationUser = checkNotNull(operationUser);
        executor = executorService(getClass().getName(), () -> {
            MDC.put("cm_type", "sys");
            MDC.put("cm_id", format("schema:%s", randomId(6)));
        });
    }

    private SchemaCollector getSchemaService() {
        return checkNotNull(schemaCollector, "schema collector plugin not installed!");
    }

    /**
     * Checks if the schema collector implementation is properly configured.
     *
     * @return true if schema collector is available, false otherwise
     */
    @Override
    public boolean isSchemaAvailable() {
        return schemaCollector != null;
    }

    /**
     * Retrieves the current schema model configuration by schema code.
     *
     * @param schemaCode Unique identifier for the schema
     * @return Schema configuration object for the specified schema
     */
    @Override
    public SchemaConfiguration getSchemaModel(String schemaCode) {
        logger.info("getSchemaModel(\"{}\") -- invoking...", schemaCode);
        SchemaConfiguration schemaConfiguration = getCurrentSchema(schemaCode);

        final String resultPrettyJson = toPrettyJson(schemaConfiguration);
        logger.info("getSchemaModel(\"{}\") -- result json: size [{}]. ", schemaCode, getReadableSize(resultPrettyJson));

        File tmpFile = writeJsonToTemp(resultPrettyJson, "%s_%s".formatted("getSchemaModel", schemaCode));
        logger.info("getSchemaModel(\"{}\") -- result json: stored to temp file =< {} >).", schemaCode, tmpFile.getAbsolutePath());

        return schemaConfiguration;
    }

    /**
     * Executes schema diff calculation between current schema and provided
     * schema.
     *
     * @param schema Source schema configuration for diff calculation
     * @return Generated diff schema object representing differences
     */
    @Override
    public GeneratedDiffSchema executeDiffFromSchema(SchemaConfiguration schema) {
        logger.info("executeDiffFromSchema(\"{}\") -- invoking...",  schema.name);

        final String resultPrettyJson = toPrettyJson(schema);
        logger.info("executeDiffFromSchema(\"{}\") -- provided schema json: size [{}]. ", schema.name, getReadableSize(resultPrettyJson));

        File tmpFile = writeJsonToTemp(resultPrettyJson, "%s_%s".formatted("executeDiffFromSchema_provided", schema.name));
        logger.info("executeDiffFromSchema(\"{}\") -- provided schema json: stored to temp file =< {} >).", schema.name, tmpFile.getAbsolutePath());

        SerializationHandle_String result = getSchemaService().compareSchema(schema, "current");

        tmpFile = writeJsonToTemp(result.getSerializationInfo(), "%s_%s_%s_VS_%s".formatted("executeDiffFromSchema_result", schema.name, schema.id, "current"));
        logger.info("executeDiffFromSchema(\"{}\") -- result json: stored to temp file =< {} >).", schema.name, tmpFile.getAbsolutePath());

        return fromJson(result.getSerializationInfo(), GeneratedDiffSchema.class);
    }

    /**
     * Executes schema merge operation from diff schema with notification
     * handling.
     *
     * <p>
     * This method performs the following steps:
     * <ul>
     * <li>1. Initializes request context</li>
     * <li>2. Acquires schema lock</li>
     * <li>3. Processes schema merge</li>
     * <li>4. Sends completion/error notifications</li>
     * <li>5. Releases schema lock</li>
     * <li>6. Cleans up request context</li>
     * </ul>
     *
     * @param diff Schema diff object containing changes to apply
     * @throws Exception If schema merge fails (handled via notifications)
     */
    @Override
    public void executeMergeFromDiff(GeneratedDiffSchema diff) {
        logger.info("executeMergeFromDiff(\"{}\") -- invoking...", diff.name);

        RequestContext requestContext = requestContextService.getRequestContext();
        String username = operationUser.getUsername();
        executor.submit(() -> {
            requestContextService.initCurrentRequestContext("schema processing job", requestContext);
            try {
                lockSchema();
                String diffPrettyJson = toPrettyJson(diff);
                File tmpFile = writeJsonToTemp(diffPrettyJson, "%s_%s".formatted("executeMergeFromDiff_provided", diff.name));
                logger.info("executeMergeFromDiff(\"{}\") -- provided json: stored to temp file =< {} >).",diff.name, tmpFile.getAbsolutePath());
                SerializationHandle_String mergeSchema = new SerializationHandle_String(toJson(diff));

                SchemaConfiguration schemaConfiguration = getSchemaService().applySchemaDiff(mergeSchema);

                final String resultPrettyJson = toPrettyJson(schemaConfiguration);
                tmpFile = writeJsonToTemp(resultPrettyJson, "%s_%s_%s".formatted("executeMergeFromDiff_result", schemaConfiguration.name, schemaConfiguration.id));

                logger.info("executeMergeFromDiff(\"{}\") -- result json: stored to temp file =< {} >).",diff.name, tmpFile.getAbsolutePath());
                sendMergeCompletedNotification(username, diff.description);
            } catch (Exception ex) {
                logger.error("error processing load schema =< {} >", diff.description, ex);
                sendMergeErrorNotification(username, diff.description);
            } finally {
                unlockSchema();
            }
            requestContextService.destroyCurrentRequestContext();
            MDC.clear();
        });
    }

    /**
     * Retrieves the current schema configuration by name.
     *
     * @param name Schema identifier
     * @return Current schema configuration
     */
    private SchemaConfiguration getCurrentSchema(String name) {
        return getSchemaService().collectSchema("0", name);
    }

    /**
     * Sends notification for successful schema merge operation.
     *
     * @param username Current user's username
     * @param diffDescription Description of the schema diff
     */
    private void sendMergeCompletedNotification(String username, String diffDescription) {
        notificationService.sendNotification(EmailImpl.builder().withNotificationProvider(NOTIFICATION_PROVIDER_CHAT).withFrom(username).withTo(username).withSubject("Schema merge completed!").withContent(format("Schema merge =< %s > completed!", diffDescription)).build());
//        notificationService.sendNotificationFromTemplate(getTemplateOrDefault(templateName), getDataTemplate(username, offlineCode, tempId, tempService.getTempInfo(tempId).getFileName()));
    }

    /**
     * Sends notification for failed schema merge operation.
     *
     * @param username Current user's username
     * @param diffDescription Description of the schema diff
     */
    private void sendMergeErrorNotification(String username, String diffDescription) {
        notificationService.sendNotification(EmailImpl.builder().withNotificationProvider(NOTIFICATION_PROVIDER_CHAT).withFrom(username).withTo(username).withSubject("Schema merge error!").withContent(format("Schema merge =< %s > error!", diffDescription)).build());
//        notificationService.sendNotificationFromTemplate(getTemplateOrDefault(templateName), getDataTemplate(username, offlineCode, tempId, tempService.getTempInfo(tempId).getFileName()));
    }

    /**
     * Acquires a lock for schema operations with 3600-second timeout.
     *
     * <p>
     * Lock is used to ensure schema consistency during merge operations. Throws
     * runtime exception if lock acquisition fails.</p>
     *
     * @throws RuntimeException If schema lock acquisition fails
     */
    private void lockSchema() {
        LockResponse lockResponse = lockService.aquireLockTimeToLiveSeconds(itemIdWithLockType(ILT_SCHEMA, SCHEMA_LOCK_ID), 3600);
        if (lockResponse.isAquired()) {
            logger.info("schema lock acquired");
        } else {
            throw runtime("schema lock not acquired");
        }
    }

    /**
     * Releases the schema lock acquired by lockSchema().
     *
     * <p>
     * Ensures proper resource cleanup after schema operations.</p>
     */
    private void unlockSchema() {
        ItemLock itemLock = lockService.getLockOrNull(itemIdWithLockType(ILT_SCHEMA, SCHEMA_LOCK_ID));
        try {
            if (itemLock != null && !itemLock.isExpired()) {
                lockService.releaseLock(itemLock);
                logger.info("schema lock released");
            } else {
                logger.error("schema lock not released");
            }
        } catch (Exception ex) {
            logger.error("schema lock not released", ex);
        }
    }

    private static String getReadableSize(String content) {
        if (content == null) {
            return "0 B";
        }

        byte[] utf8Bytes = content.getBytes(StandardCharsets.UTF_8);
        long sizeInBytes = utf8Bytes.length; // Dimensione effettiva in byte

        if (sizeInBytes >= 1024 * 1024) {
            double sizeInMB = sizeInBytes / (1024.0 * 1024);
            return String.format("%.1f MB", sizeInMB);
        } else if (sizeInBytes >= 1024) {
            double sizeInKB = sizeInBytes / 1024.0;
            return String.format("%.1f KB", sizeInKB);
        } else {
            return sizeInBytes + " B";
        }
    }

    private static File writeJsonToTemp(String content, String filename) {
        File tmpFile = tempFile(filename, "json", false);
        writeToFile(content, tmpFile);

        if (!tmpFile.exists()) {
            throw runtime("Unable to create file %s".formatted(tmpFile.getAbsolutePath()));
        }

        return tmpFile;
    }
}
