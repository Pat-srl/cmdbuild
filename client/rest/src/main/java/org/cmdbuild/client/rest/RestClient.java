/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest;

import java.io.Closeable;
import org.cmdbuild.api.SchemaCollectorApi;
import org.cmdbuild.client.rest.api.AttachmentApi;
import org.cmdbuild.client.rest.api.AuditApi;
import org.cmdbuild.client.rest.api.CalendarApi;
import org.cmdbuild.client.rest.api.CardApi;
import org.cmdbuild.client.rest.api.ClassApi;
import org.cmdbuild.client.rest.api.CustomComponentApi;
import org.cmdbuild.client.rest.api.DomainApi;
import org.cmdbuild.client.rest.api.EmailApi;
import org.cmdbuild.client.rest.api.EtlApi;
import org.cmdbuild.client.rest.api.GeoserverLayerApi;
import org.cmdbuild.client.rest.api.LoginApi;
import org.cmdbuild.client.rest.api.LookupApi;
import org.cmdbuild.client.rest.api.MenuApi;
import org.cmdbuild.client.rest.api.RelationApi;
import org.cmdbuild.client.rest.api.ReportApi;
import org.cmdbuild.client.rest.api.SessionApi;
import org.cmdbuild.client.rest.api.SystemApi;
import org.cmdbuild.client.rest.api.TranslationApi;
import org.cmdbuild.client.rest.api.UploadApi;
import org.cmdbuild.client.rest.api.UserApi;
import org.cmdbuild.client.rest.api.WokflowApi;
import org.cmdbuild.client.rest.core.InnerRestClient;
import org.cmdbuild.utils.io.StreamProgressListener;

public interface RestClient extends Closeable {

    LoginApi login();

    WokflowApi workflow();

    SessionApi session();

    CardApi card();

    ClassApi classe();

    DomainApi domain();

    UserApi user();

    RelationApi relation();

    AttachmentApi attachment();

    SystemApi system();

    EtlApi etl();

    AuditApi audit();

    InnerRestClient inner();

    MenuApi menu();

    LookupApi lookup();

    UploadApi uploads();

    ReportApi report();

    CustomComponentApi customComponent();

    EmailApi email();

    CalendarApi calendar();

    GeoserverLayerApi geoserverLayer();

    TranslationApi translation();

    SchemaCollectorApi schemaCollector();

    String getBaseUrl();

    default RestClient doLogin(String username, String password) {
        return login().doLogin(username, password);
    }

    default RestClient doLoginWithAnyGroup(String username, String password) {
        return login().doLoginWithAnyGroup(username, password);
    }

    default RestClient withSessionToken(String sessionToken) {
        inner().setSessionToken(sessionToken);
        return this;
    }

    default RestClient withActionId(String actionId) {
        inner().setActionId(actionId);
        return this;
    }

    default RestClient withHeader(String key, String value) {
        inner().setHeader(key, value);
        return this;
    }

    default RestClient withUploadProgressListener(StreamProgressListener listener) {
        inner().addUploadProgressListener(listener);
        return this;
    }

    default RestClient withInsecureSsl() {
        inner().allowInsecureSsl(true);
        return this;
    }

    /**
     *
     * @return session token (never null)
     * @throws RuntimeException if session token is not available
     */
    default String getSessionToken() {
        return inner().getSessionToken();
    }

}
