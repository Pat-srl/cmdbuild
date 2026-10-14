/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.cmdbuild.utils.soap.SoapHelper;
import org.cmdbuild.workflow.inner.SchemaApiForWorkflowExt;

public interface ExtendedApiMethods extends SchemaApiForWorkflowExt, AnotherWfApi<ExtendedApi> {

    default SoapHelper soap() {
        return SoapHelper.newSoap();
    }

    default RequestSpecification rest() {
        return RestAssured.given();
    }

}
