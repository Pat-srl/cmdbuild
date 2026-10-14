/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import static java.lang.String.format;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import jakarta.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.AuthConst.SYSTEM_USER;
import org.cmdbuild.auth.login.file.FileAuthUtils.AuthFile;
import static org.cmdbuild.auth.login.file.FileAuthUtils.buildAuthFile;
import static org.cmdbuild.auth.login.file.FileAuthUtils.isAuthFilePassword;
import org.cmdbuild.client.rest.RestClient;
import static org.cmdbuild.client.rest.RestClientImpl.build;
import static org.cmdbuild.client.rest.api.LoginApi.RSA_KEY_PASSWORD_PREFIX;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_MAINTENANCE_MODE_PASSTOKEN_HEADER_OR_COOKIE;
import static org.cmdbuild.common.http.HttpConst.MAINTENANCE_MODE_PASSTOKEN_DEFAULT;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.cli.Main.isRunningFromWebappDir;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandParser.printActionHelp;
import org.cmdbuild.utils.cli.utils.CliCommandUtils;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.prepareAction;
import static org.cmdbuild.utils.crypto.CmRsaUtils.parsePrivateKey;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.w3c.dom.Document;

public abstract class AbstractRestCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;
    protected String username, password, session, baseUrl;
    protected RestClient client;

    public AbstractRestCommandRunner(Collection<String> names, String description) {
        super(names, description);
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("url", true, "set cmdbuild root url for rest ws (default is 'http://localhost:8080/cmdbuild/')");
        options.addOption("username", true, "set ws username (default is 'admin')");
        options.addOption("password", true, "set ws password (default is 'admin')");
        options.addOption("session", true, "set ws session token");
        options.addOption("promptpassword", false, "read password from stdin");
        options.addOption(Option.builder("mmpasstoken").optionalArg(true).desc("attach maintenance mode passtoken to each request (use default passtoken unless specified)").build());
        options.addOption("insecure", false, "skip ssl security check (hostname, certificate, etc)");
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable rest methods:");
        printActionHelp(actions);
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        baseUrl = getBaseUrl(cmd.getOptionValue("url"));
        logger.debug("selected base url = {}", baseUrl);

        session = cmd.getOptionValue("session");
        if (isBlank(session)) {
            username = firstNonNull(cmd.getOptionValue("username"), SYSTEM_USER);
            password = cmd.getOptionValue("password");
            if (isBlank(password) && cmd.hasOption("promptpassword")) {
                System.err.print("password: ");
                password = new String(System.console().readPassword());
            }
            if (isBlank(password) && canUseAuthFilePassword()) {
                regenAuthFilePassword();
            }
            if (isBlank(password)) {
                File rsaKeyFile = new File(System.getProperty("user.home"), ".ssh/id_rsa_cmdbuild");
                if (rsaKeyFile.exists()) {
                    try {
                        String rsaKeyData = readToString(rsaKeyFile);
                        parsePrivateKey(rsaKeyData);
                        password = RSA_KEY_PASSWORD_PREFIX + rsaKeyData;
                        logger.debug("using rsa key from file = {}", rsaKeyFile);
                    } catch (Exception ex) {
                        logger.debug("unable to read private key data from file = {}", rsaKeyFile, ex);
                        logger.warn("unable to read private key data from file = {} : {}", rsaKeyFile, ex.toString());
                    }
                }
            }
            checkNotBlank(password, "missing 'password' param for user = %s", username);
        }

        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no rest call requested, doing nothing...");
        } else {
            CliCommandUtils.ExecutableAction action = prepareAction(actions, iterator);
            client = build(baseUrl).withActionId("cli_restws_" + action.getAction().getName());
            if (cmd.hasOption("insecure")) {
                client.withInsecureSsl();
                logger.debug("using insecure ssl");
            }
            if (cmd.hasOption("mmpasstoken")) {
                String mmPasstoken = firstNotNull(trimToNull(cmd.getOptionValue("mmpasstoken")), MAINTENANCE_MODE_PASSTOKEN_DEFAULT);
                client.withHeader(CMDBUILD_MAINTENANCE_MODE_PASSTOKEN_HEADER_OR_COOKIE, mmPasstoken);
            }
            try {
                action.execute();
            } finally {
                client.close();
            }
        }
    }

    @Nullable
    protected String regenPassword() {
        if (isAuthFilePassword(password)) {
            regenAuthFilePassword();
        }
        return password;
    }

    protected boolean canUseAuthFilePassword() {
        return baseUrl.matches(".*://localhost:.*") && isRunningFromWebappDir();
    }

    protected void regenAuthFilePassword() {
        AuthFile authFile = tryToBuildFilePassword();
        if (authFile != null) {
            logger.debug("authenticating with file password = {}", authFile.getFile().getAbsolutePath());
            authFile.getFile().deleteOnExit();
            password = authFile.getPassword();
        }
    }

    @Nullable
    private AuthFile tryToBuildFilePassword() {
        try {
            checkArgument(isRunningFromWebappDir(), "cannot use file auth: not running from webapp dir");
            File authDir = new File(getCliHome(), "../../temp/");
            return buildAuthFile(authDir);
        } catch (Exception ex) {
            logger.error("error building file password", ex);
            return null;
        }
    }

    protected String getBaseUrl(@Nullable String urlParam) {
        if (isNotBlank(urlParam)) {
            return urlParam;
        } else {
            int port = 8080;
            String webapp = "cmdbuild";
            if (isRunningFromWebappDir()) {
                try {
                    webapp = getCliHome().getCanonicalFile().getName();
                    File tomcatConf = new File(getCliHome(), "../../conf/server.xml");
                    if (tomcatConf.exists()) {
                        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tomcatConf);
                        port = Integer.parseInt(XPathFactory.newInstance().newXPath().compile("string(//*[local-name()='Connector'][@protocol='HTTP/1.1']/@port)").evaluate(document));
                        logger.debug("selected tomcat port = {}", port);
                    }
                } catch (Exception ex) {
                    logger.error("error processing tomcat config file", ex);
                }
            }
            return switch (webapp) {
                case "ROOT" ->
                    format("http://localhost:%s/", port);
                default ->
                    format("http://localhost:%s/%s/", port, webapp);
            };
        }
    }

    protected RestClient login() {
        return isNotBlank(session) ? client.withSessionToken(session) : client.doLoginWithAnyGroup(username, password);
    }

    protected String buildCurlCli(String authToken, String service) {
        return buildCurlCli(authToken, service, "get", null, null, true, true);
    }

    protected String buildCurlCli(String authToken, String service, String method, @Nullable String payload, @Nullable String contentType, boolean isJson, boolean comment) {
        String methodParam, otherParams = "";
        switch (method.toUpperCase()) {
            case "GET" ->
                methodParam = "";
            default -> {
                methodParam = format("-X %s", method.toUpperCase());
                if (payload != null) {
                    otherParams += format(" --data-binary '%s'", payload);
                }
            }
        }
        if (isNotBlank(contentType)) {
            otherParams += format(" -H'Content-Type:%s'", contentType);
        }

        return format("cmdbuild_auth_token='%s'\n%scurl %s -vv -H\"Cmdbuild-authorization:${cmdbuild_auth_token}\" \"%s\" %s%s\n",
                authToken, comment ? "# " : "", methodParam, baseUrl + service.replaceFirst("^/", ""), otherParams,
                isJson ? " | jshon" : " | xmlstarlet fo"); //TODO improve this
    }
}
