/*
 * CMDBuild has been developed and is managed by Pat srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.openapi.config;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import jakarta.ws.rs.core.Application;
import java.io.InputStream;
import java.util.*;

/**
 *
 * @author ataboga
 */
@OpenAPIDefinition(
        info = @Info(
                title = "CMDBuild rest webservices",
                description = """
                                The following documentation describes the WS REST APIs provided by CMDBuild web application.
                                CMDBuild is an open source web application for the configuration of custom IT and business service management solutions.
                                It is based on a powerful and flexible core that allows you to easily model and manage complex infrastructures and services.
                                The REST APIs allow developers to interact with CMDBuild programmatically, enabling integration with other systems and automation of tasks.

                                ---

                                Verticalization links:
                                - [Ready2Use](https://demo.cmdbuildready2use.org/cmdbuild/ui/)
                                - [OpenMaint](https://demo.openmaint.org/openmaint/ui/)

                                ---
                                """,
                termsOfService = "https://www.cmdbuild.org/en/project/license",
                license = @License(
                        name = "GNU Affero General Public License v3",
                        url = "https://www.gnu.org/licenses/agpl-3.0.en.html"
                ),
                contact = @Contact(
                        name = "Pat srl",
                        url = "https://www.pat.eu"),
                version = "4.3.0"
        ),
        tags = {
            @Tag(name = "Administration", description = "The following documentation aims to illustrate the usage of the REST APIs provided by CMDBuild in order to manage the administrative functionalities of the application.")},
        servers = {
            @Server(
                    url = "{protocolType}://{host}:{port}/{basePath}/services/rest/v4",
                    description = "CMDBuild rest v4 API base path",
                    variables = {
                        @ServerVariable(
                                name = "protocolType",
                                description = "Protocol type (http or https)",
                                allowableValues = {"http", "https"},
                                defaultValue = "https"
                        ),
                        @ServerVariable(
                                name = "host",
                                description = "CMDBuild server host",
                                defaultValue = "localhost"
                        ),
                        @ServerVariable(
                                name = "port",
                                description = "CMDBuild server port",
                                defaultValue = "8080"
                        ),
                        @ServerVariable(
                                name = "basePath",
                                description = "CMDBuild application base path",
                                allowableValues = {"cmdbuild", "cmdbuild-test", "openmaint", "openmaint-test"},
                                defaultValue = "cmdbuild"
                        )
                    }
            )},
        externalDocs = @ExternalDocumentation(
                description = "CMDBuild official documentation",
                url = "https://www.cmdbuild.org/en/documentation"
        ),
        extensions = {
            @Extension(
                    name = "x-logo",
                    properties = {
                        @ExtensionProperty(name = "url", value = "https://www.cmdbuild.org/wp-content/uploads/2020/09/cropped-cmdbuild-logo-1.png"),
                        @ExtensionProperty(name = "backgroundColor", value = "#FFFFFF"),
                        @ExtensionProperty(name = "altText", value = "CMDBuild Logo")
                    })
        }
)
@SecuritySchemes({
    @SecurityScheme(
            name = "BasicAuth",
            type = SecuritySchemeType.HTTP,
            scheme = "basic",
            description = "Please note that this authentication method doesn't work if the user belongs to multiple groups and doesn't have a default group"
    ),
    @SecurityScheme(
            name = "BearerAuth",
            type = SecuritySchemeType.APIKEY,
            paramName = "Cmdbuild-authorization",
            in = SecuritySchemeIn.HEADER,
            description = "In order to use the the following authentication please provide the access token given by the Post call"
    )
})
public class ApplicationConfig extends Application {

    public OpenAPI getCombinedOpenAPI() {
        // Load the static yaml file from resources
        InputStream is = getClass().getClassLoader().getResourceAsStream("openapi.yaml");
        if (is == null) {
            return new OpenAPI();
        }

        try (Scanner s = new Scanner(is).useDelimiter("\\A")) {
            String yamlContent = s.hasNext() ? s.next() : "";
            SwaggerParseResult result = new OpenAPIV3Parser().readContents(yamlContent);
            return result.getOpenAPI();
        }
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();

        // Create the configuration and attach static OpenAPI model
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(getCombinedOpenAPI())
                .prettyPrint(true)
                .resourcePackages(Set.of(
                        "org.cmdbuild.service.rest.v4.openapi.config",
                        "org.cmdbuild.service.rest.v4.endpoint"
                ));

        // Create the resource and inject the configuration
        OpenApiResource openApiResource = new OpenApiResource();
        openApiResource.setOpenApiConfiguration(oasConfig);

        singletons.add(openApiResource);
        return singletons;
    }
}
