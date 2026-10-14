/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.api.CustomComponentApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.model.CustomComponentInfo;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CustomPageApiImpl extends AbstractServiceClientImpl implements CustomComponentApi {

    public CustomPageApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public CustomComponentApiResponse uploadCustomPage(InputStream data, String description, @Nullable String targetDevice) {
        checkNotNull(data, "data param cannot be null");
        HttpEntity multipart;
        if (description.isBlank()) {
            multipart = MultipartEntityBuilder.create()
                    .addBinaryBody("file", listenUpload("custom page upload", data), ContentType.APPLICATION_OCTET_STREAM, "file.zip")
                    .build();
        } else {
            String jsonbody = "{\"description\":\"" + description + "\",\"device\":\"" + parseEnumOrDefault(targetDevice, TD_DEFAULT) + "\",\"active\":true}";
            multipart = MultipartEntityBuilder.create()
                    .addBinaryBody("file", listenUpload("custom page upload", data), ContentType.APPLICATION_OCTET_STREAM, "file.zip")
                    .addBinaryBody("data", jsonbody.getBytes(), ContentType.APPLICATION_JSON, "data")
                    .build();
        }
        JsonNode jsonNode = post("administration/custompages?merge=true", multipart).asJackson().get("data");
        CustomComponentInfo customPageInfo = fromJson(jsonNode, CustomComponentInfoImpl.class);
        return new CustomComponentApiResponse() {
            @Override
            public CustomComponentInfo getCustomComponentInfo() {
                return customPageInfo;
            }

            @Override
            public CustomComponentApi then() {
                return CustomPageApiImpl.this;
            }
        };
    }

    @Override
    public CustomComponentApiResponse uploadCustomWidget(InputStream data, String description, @Nullable String targetDevice) {
        checkNotNull(data, "data param cannot be null");
        String jsonbody = "{\"device\":\"" + parseEnumOrDefault(targetDevice, TD_DEFAULT) + "\",\"active\":true,\"description\":\"" + description + "\"}";
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", listenUpload("custom widget upload", data), ContentType.APPLICATION_OCTET_STREAM, "file.zip")
                .addBinaryBody("data", jsonbody.getBytes(), ContentType.APPLICATION_JSON, "data")
                .build();
        JsonNode jsonNode = post("administration/components/widget?merge=true", multipart).asJackson().get("data");
        CustomComponentInfo customWidgetInfo = fromJson(jsonNode, CustomComponentInfoImpl.class);
        return new CustomComponentApiResponse() {
            @Override
            public CustomComponentInfo getCustomComponentInfo() {
                return customWidgetInfo;
            }

            @Override
            public CustomComponentApi then() {
                return CustomPageApiImpl.this;
            }
        };
    }

    @Override
    public CustomComponentApiResponse uploadCustomContextMenu(InputStream data, String description, @Nullable String targetDevice) {
        checkNotNull(data, "data param cannot be null");
        String jsonbody = "{\"device\":\"" + parseEnumOrDefault(targetDevice, TD_DEFAULT) + "\",\"active\":true,\"description\":\"" + description + "\"}";
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", listenUpload("context menu upload", data), ContentType.APPLICATION_OCTET_STREAM, "file.zip")
                .addBinaryBody("data", jsonbody.getBytes(), ContentType.APPLICATION_JSON, "data")
                .build();
        JsonNode jsonNode = post("administration/components/contextmenu?merge=true", multipart).asJackson().get("data");
        CustomComponentInfo contextMenu = fromJson(jsonNode, CustomComponentInfoImpl.class);
        return new CustomComponentApiResponse() {
            @Override
            public CustomComponentInfo getCustomComponentInfo() {
                return contextMenu;
            }

            @Override
            public CustomComponentApi then() {
                return CustomPageApiImpl.this;
            }
        };
    }

    @Override
    public CustomComponentApiResponse createCustomScript(String code, String description, String data, boolean active) {
        JsonNode jsonNode = post("administration/components/core/script", map(
                "name", checkNotBlank(code),
                "description", description,
                "data", data,
                "active", active
        )).asJackson().get("data");
        CustomComponentInfo customScript = fromJson(jsonNode, CustomComponentInfoImpl.class);
        return new CustomComponentApiResponse() {
            @Override
            public CustomComponentInfo getCustomComponentInfo() {
                return customScript;
            }

            @Override
            public CustomComponentApi then() {
                return CustomPageApiImpl.this;
            }
        };
    }

    @Override
    public CustomComponentApiResponse getCustomScript(String code) {
        JsonNode jsonNode = get("administration/components/core/script/" + code).asJackson().get("data");
        CustomComponentInfo customScript = fromJson(jsonNode, CustomComponentInfoImpl.class);
        return new CustomComponentApiResponse() {
            @Override
            public CustomComponentInfo getCustomComponentInfo() {
                return customScript;
            }

            @Override
            public CustomComponentApi then() {
                return CustomPageApiImpl.this;
            }
        };
    }

    @Override
    public List<CustomComponentApiResponse> getCustomScripts() {
        String jsonNode = get("administration/components/core/script/").asJackson().toString();
        List<CustomComponentApiResponse> customScripts = list();
        JsonNode arrNode = null;
        try {
            arrNode = new ObjectMapper().readTree(jsonNode).get("data");
        } catch (IOException ex) {
        }
        if (arrNode.isArray()) {
            for (JsonNode objNode : arrNode) {
                CustomComponentApiResponse customComponentApiResponse = new CustomComponentApiResponse() {
                    @Override
                    public CustomComponentInfo getCustomComponentInfo() {
                        return fromJson(objNode, CustomComponentInfoImpl.class);
                    }

                    @Override
                    public CustomComponentApi then() {
                        return CustomPageApiImpl.this;
                    }
                };
                customScripts.add(customComponentApiResponse);
            }
        }
        return customScripts;
    }

    @Override
    public CustomComponentApiResponse updateCustomScript(String code, String newDescription, String newData, boolean active) {
        JsonNode jsonNode = put("administration/components/core/script/" + code, map(
                "name", checkNotBlank(code),
                "description", newDescription,
                "data", newData,
                "active", active
        )).asJackson().get("data");
        CustomComponentInfo customScript = fromJson(jsonNode, CustomComponentInfoImpl.class);
        return new CustomComponentApiResponse() {
            @Override
            public CustomComponentInfo getCustomComponentInfo() {
                return customScript;
            }

            @Override
            public CustomComponentApi then() {
                return CustomPageApiImpl.this;
            }

        };
    }

    @Override
    public boolean deleteCustomScript(String scriptCode) {
        return delete("administration/components/core/script/" + scriptCode).asJackson().get("success").asBoolean();
    }

    @JsonDeserialize(builder = CustomComponentInfoImplBuilder.class)
    public static class CustomComponentInfoImpl implements CustomComponentInfo {

        private final String name, description, data;
        private final boolean active;

        private CustomComponentInfoImpl(CustomComponentInfoImplBuilder builder) {
            this.name = checkNotBlank(builder.name);
            this.description = nullToEmpty(builder.description);
            this.data = nullToEmpty(builder.data);
            this.active = firstNotNull(builder.active, true);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getData() {
            return data;
        }

        @Override
        public boolean getActive() {
            return active;
        }

        public static CustomComponentInfoImplBuilder builder() {
            return new CustomComponentInfoImplBuilder();
        }
    }

    public static class CustomComponentInfoImplBuilder implements Builder<CustomComponentInfoImpl, CustomComponentInfoImplBuilder> {

        private String name;
        private String description;
        private String data;
        private boolean active;

        public CustomComponentInfoImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CustomComponentInfoImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CustomComponentInfoImplBuilder withData(String data) {
            this.data = data;
            return this;
        }

        public CustomComponentInfoImplBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        @Override
        public CustomComponentInfoImpl build() {
            return new CustomComponentInfoImpl(this);
        }

    }

}
