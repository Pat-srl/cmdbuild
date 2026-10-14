package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import com.google.common.net.UrlEscapers;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.api.TranslationApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.translation.dao.Translation;
import org.cmdbuild.translation.dao.TranslationImpl;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class TranslationApiImpl extends AbstractServiceClientImpl implements TranslationApi {

    public TranslationApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public TranslationApi put(String code, String lang, String value) {
        put(format("administration/translations/%s", encodeUrlPath(checkNotBlank(code))), map(checkNotBlank(lang), nullToEmpty(value)));
        return this;
    }

    @Override
    public TranslationApi delete(String code, String lang) {
        delete(format("administration/translations/%s?lang=%s", encodeUrlPath(checkNotBlank(code)), encodeUrlQuery(checkNotBlank(lang))));
        return this;
    }

    @Override
    public List<Translation> getAll() {
        JsonNode list = get("administration/translations").asJackson().get("data");
        return stream(list.elements()).map(e -> TranslationImpl.builder().withCode(e.get("code").asText()).withLang(e.get("lang").asText()).withValue(e.get("value").asText()).build()).collect(toImmutableList());
    }

    @Override
    public List<Translation> getMany(String query) {
        JsonNode list = get(format("administration/translations?filter=%s", UrlEscapers.urlFragmentEscaper().escape(checkNotBlank(query)))).asJackson().get("data");
        return stream(list.elements()).map(e -> TranslationImpl.builder().withCode(e.get("code").asText()).withLang(e.get("lang").asText()).withValue(e.get("value").asText()).build()).collect(toImmutableList());
    }

    @Override
    public void importFromFile(DataSource payload, @Nullable String separator) {
        checkNotNull(payload);
        post(format("administration/translations/import?separator=%s",UrlEscapers.urlFragmentEscaper().escape(nullToEmpty(separator))), MultipartEntityBuilder.create().addBinaryBody("file", toByteArray(payload), ContentType.create("text/csv", UTF_8), "file.csv").build());
    }
}
