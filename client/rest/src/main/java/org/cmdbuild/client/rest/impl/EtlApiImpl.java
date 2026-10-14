/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.api.EtlApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.serializeSorter;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoExt;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoImpl;
import org.cmdbuild.etl.config.WaterwayItemInfo;
import org.cmdbuild.etl.config.WaterwayItemInfoImpl;
import org.cmdbuild.etl.config.WaterwayItemType;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecordImpl;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.gate.inner.EtlGateHandler;
import org.cmdbuild.etl.gate.inner.EtlGateHandlerImpl;
import org.cmdbuild.etl.gate.inner.EtlGateImpl;
import org.cmdbuild.etl.gate.inner.EtlProcessingMode;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.inner.EtlProcessingResultErrorImpl;
import org.cmdbuild.etl.loader.inner.EtlProcessingResultImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage.WMAS_REFERENCE;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType;
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EtlApiImpl extends AbstractServiceClientImpl implements EtlApi {

    public EtlApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    private Boolean requireSession;

    @Override
    protected boolean isSessionTokenRequired() {
        return firstNonNull(requireSession, true);
    }

    @Override
    public List<WaterwayDescriptorRecord> getAllDescriptors() {
        return list(get("administration/etl/configs?detailed=true").asJackson().get("data").elements()).map(e -> parseWaterwayConfigFileRecord((ObjectNode) e));
    }

    @Override
    public List<WaterwayItemInfo> getAllItems() {
        return list(get("administration/etl/configs/_ALL/items").asJackson().get("data").elements()).map(e -> WaterwayItemInfoImpl.builder()
                .withCode(e.get("_id").textValue())
                .withDescription(e.get("description").textValue())
                .withNotes(e.get("notes").textValue())
                .withDescriptorKey(e.get("descriptor").textValue())
                .withEnabled(e.get("enabled").booleanValue())
                .withType(parseEnum(e.get("type").textValue(), WaterwayItemType.class))
                .withSubtype(e.hasNonNull("subtype") ? e.get("subtype").textValue() : null)
                .build());
    }

    @Override
    @Nullable
    public WaterwayDescriptorRecord getDescriptorOrNull(String code) {
        ObjectNode data = (ObjectNode) get(format("administration/etl/configs/%s?if_exists=true", encodeUrlPath(code))).asJackson().get("data");
        return toBooleanOrDefault(fromJson(data, MAP_OF_OBJECTS).get("exists"), true) ? parseWaterwayConfigFileRecord(data) : null;
    }

    @Override
    public void enableDescriptorOrItem(String code) {
        checkNotBlank(code);
        ObjectNode e = (ObjectNode) get(format("administration/etl/configs/%s", encodeUrlPath(code))).asJackson().get("data");
//        list(get("etl/configs").asJackson().get("data").elements()).forEach(e -> {
//            if (equal(e.get("_id"), code) && e.get("enabled").booleanValue() == false) {
        if (e.get("enabled").booleanValue() == false) {
            ((ObjectNode) e).put("enabled", true);
            put(format("etl/configs/%s", encodeUrlPath(code)), MultipartEntityBuilder.create().addTextBody("meta", toJson(e), ContentType.APPLICATION_JSON).build());
        }
//            Set<String> disabled = set(toListOfStrings(e.get("disabled").textValue()));
//            if (disabled.contains(code)) {
//                disabled.remove(code);
//                ((ObjectNode) e).set("disabled", fromJson(toJson(disabled), ArrayNode.class));
//                put(format("etl/configs/%s", encodeUrlPath(code)), MultipartEntityBuilder.create().addTextBody("meta", toJson(e), ContentType.APPLICATION_JSON).build());
//            }
//        });
    }

    @Override
    public void disableDescriptorOrItem(String code) {
        ObjectNode e = (ObjectNode) get(format("administration/etl/configs/%s", encodeUrlPath(checkNotBlank(code)))).asJackson().get("data");
//        list(get("etl/configs").asJackson().get("data").elements()).forEach(e -> {
//            if (equal(e.get("_id"), code) && e.get("enabled").booleanValue() == false) {
        if (e.get("enabled").booleanValue() == true) {
            ((ObjectNode) e).put("enabled", false);
            put(format("administration/etl/configs/%s", encodeUrlPath(code)), MultipartEntityBuilder.create().addTextBody("meta", toJson(e), ContentType.APPLICATION_JSON).build());
        }
//        list(get("etl/configs").asJackson().get("data").elements()).forEach(e -> {
//            if (equal(e.get("_id"), code) && e.get("enabled").booleanValue() == true) {
//                ((ObjectNode) e).put("enabled", false);
//                put(format("etl/configs/%s", encodeUrlPath(code)), MultipartEntityBuilder.create().addTextBody("meta", toJson(e), ContentType.APPLICATION_JSON).build());
//            }
//            //TODO disable single item (?)



    ////            Set<String> disabled = set(fromJson(e.get("disabled"), LIST_OF_STRINGS));
////            if (disabled.contains(code)) {
////                disabled.remove(code);
////                ((ObjectNode) e).set("disabled", fromJson(toJson(disabled), ArrayNode.class));
////                put(format("etl/configs/%s", encodeUrlPath(code)), MultipartEntityBuilder.create().addTextBody("meta", toJson(e), ContentType.APPLICATION_JSON).build());
////            }
//        });
    }

    @Override
    public void deleteDescriptor(String code) {
        delete(format("administration/etl/configs/%s", encodeUrlPath(checkNotBlank(code))));
    }

    @Override
    public WaterwayDescriptorInfoExt getDescriptorInfo(String code) {
        return parseWaterwayConfigFileInfo((ObjectNode) get(format("administration/etl/configs/%s", encodeUrlPath(checkNotBlank(code)))).asJackson().get("data"));
    }

    @Override
    public WaterwayDescriptorInfoExt createUpdateDescriptor(DataSource content) {
        JsonNode response = post("administration/etl/configs?overwriteIfExists=true", MultipartEntityBuilder.create().addBinaryBody("file", readToString(content).getBytes(UTF_8), ContentType.create("application/x-yaml", UTF_8), content.getName()).build()).asJackson().get("data");
        return parseWaterwayConfigFileInfo((ObjectNode) response);
    }

    @Override
    public PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query) {
        JsonNode response = get(format("administration/etl/messages?limit=%s&offset=%s&filter=%s&sort=%s&detailed=true", firstNotNull(query.getLimit(), ""), query.getOffset(), encodeUrlQuery(serializeFilter(query.getFilter())), encodeUrlQuery(serializeSorter(query.getSorter()))))
                .asJackson();
        long total = response.get("meta").get("total").asLong();
        List<WaterwayMessage> messages = list(response.get("data").elements()).map(ObjectNode.class::cast).map(this::parseMessage);
        return paged(messages, total);
    }

    @Override
    public WaterwayMessage getMessage(String messageReference) {
        return parseMessage((ObjectNode) get(format("administration/etl/messages/%s", encodeUrlPath(checkNotBlank(messageReference)))).asJackson().get("data"));
    }

    @Override
    public DataSource getMessageAttachment(String messageReference, String attachmentName) {
        return getDataSource(format("administration/etl/messages/%s/attachments/%s", encodeUrlPath(checkNotBlank(messageReference)), encodeUrlPath(checkNotBlank(attachmentName))));
    }

    private WaterwayMessage parseMessage(ObjectNode e) {
        return WaterwayMessageImpl.builder()
                .withMessageId(e.get("messageId").asText())
                .withQueue(e.get("queue").asText())
                .withNodeId(e.get("nodeId").asText())
                .withStatus(parseEnum(e.get("status").asText(), WaterwayMessageStatus.class))
                .withStorage(e.get("storage").asText())
                .withTimestamp(CmDateUtils.toDateTime(e.get("timestamp").asText()))
                .withTransactionId(CmConvertUtils.toInt(e.get("transactionId").asText()))
                .withMeta(fromJson(e.get("meta"), MAP_OF_STRINGS))
                .withHistory(fromJson(e.get("history"), LIST_OF_STRINGS))
                .withAttachments(list(e.get("attachments").elements()).map(a -> WaterwayMessageAttachmentImpl.builder()
                .withName(a.get("name").asText())
                .withType(parseEnum(a.get("type").asText(), WaterwayMessageAttachmentType.class))
                .withMeta(fromJson(a.get("meta"), MAP_OF_STRINGS))
                .accept(b -> {
                    WaterwayMessageAttachmentStorage storage = parseEnum(a.get("storage").asText(), WaterwayMessageAttachmentStorage.class);
                    switch (storage) {
                        case WMAS_REFERENCE -> {
                            b.withStorage(storage).withObject(a.get("value").asText());
                        }
                        default -> {
                            b.withStorage(WMAS_REFERENCE).withObject("n/a").withMeta("_restclient_storage", serializeEnum(storage));//TODO improve this
                        }
                    }
                })
                .build()))
                .build();
    }

    @Override
    public EtlGate getGate(String gateId) {
        return parseEtlGate(get(format("administration/etl/gates/%s", encodeUrlPath(gateId))).asJackson().get("data"));
    }

    @Override
    public EtlGate updateGate(EtlGate gate) {
        return parseEtlGate(put(format("administration/etl/gates/%s", encodeUrlPath(gate.getCode())), map(
                //                "_id", gate.getCode(),
                "code", gate.getCode(),
                "allowPublicAccess", gate.getAllowPublicAccess(),
                "processingMode", serializeEnum(gate.getProcessingMode()),
                "handlers", gate.getHandlers().stream().map(EtlGateHandler::getConfig).collect(toUnmodifiableList()),
                "enabled", gate.isEnabled(),
                "config", map(gate.getConfig())
        )).asJackson().get("data"));
    }

    @Override
    @Nullable
    public EtlProcessingResult postToGate(String gateId, DataSource newDataSource) {
        Response response = post(format("/services/etl/gate/private/%s", encodeUrlPath(gateId)), newDataSource);
        if (response.hasContent()) {
            if (isContentType(response.getContentType(), "application/json")) {
                JsonNode data = response.asJackson().get("data");
                if (data != null && data.isObject() && data.has("created")) {
                    return new EtlProcessingResultImpl(data.get("created").asLong(), data.get("modified").asLong(), data.get("unmodified").asLong(), data.get("deleted").asLong(), data.get("processed").asLong(),
                            stream((ArrayNode) data.get("errors")).map(e -> new EtlProcessingResultErrorImpl(
                            e.get("recordNumber").asLong(),
                            e.get("lineNumber").asLong(),
                            emptyMap(), //TODO record data
                            e.get("message").asText(),
                            e.get("techMessage").asText())).collect(toUnmodifiableList())
                    );//TODO retailed report
                } else {
                    return null;
                }
            } else {
                logger.info("received content {}=\n\n{}\n", response.getContentType(), response.asString());
                return null;
            }
        } else {
            return null;
        }
    }

    protected EtlGate parseEtlGate(JsonNode data) {
        return EtlGateImpl.builder()
                //                .withId(data.get("_id").asLong())
                .withCode(data.get("code").asText())
                .withAllowPublicAccess(data.get("allowPublicAccess").asBoolean())
                .withProcessingMode(parseEnum(data.get("processingMode").asText(), EtlProcessingMode.class))
                .withHandlers(stream(data.get("handlers").elements()).map(e -> new EtlGateHandlerImpl((Map) fromJson(e, MAP_OF_STRINGS))).collect(toUnmodifiableList()))
                .withEnabled(data.get("enabled").asBoolean())
                .withConfig(fromJson(data.get("config"), MAP_OF_STRINGS))
                .build();
    }

    protected static WaterwayDescriptorInfoExt parseWaterwayConfigFileInfo(ObjectNode e) {
        return WaterwayDescriptorInfoImpl.builder()
                .withCode(e.get("_id").textValue())
                .withDescription(e.get("description").textValue())
                .withNotes(e.get("notes").textValue())
                .withVersion(e.get("version").intValue())
                .withTag(e.get("tag").textValue())
                .withEnabled(e.get("enabled").booleanValue())
                .withValid(e.get("valid").booleanValue())
                .withDisabledItems(toListOfStrings(e.get("disabled").textValue()))
                .withParams(fromJson(e.get("params"), MAP_OF_STRINGS))
                .build();
    }

    protected static WaterwayDescriptorRecord parseWaterwayConfigFileRecord(ObjectNode e) {
        return WaterwayDescriptorRecordImpl.builder()
                .withCode(e.get("_id").textValue())
                .withDescription(e.get("description").textValue())
                .withNotes(e.get("notes").textValue())
                .withVersion(e.get("version").intValue())
                .withEnabled(e.get("enabled").booleanValue())
                .withValid(e.get("valid").booleanValue())
                .withDisabledItems(toListOfStrings(e.get("disabled").textValue()))
                .withParams(fromJson(e.get("params"), MAP_OF_STRINGS))
                .withData(e.get("data").textValue())
                .build();
    }

}
