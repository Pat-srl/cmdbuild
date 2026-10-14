/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.workflow.type.ReferenceType;

public interface AnotherWfApi<T> {

    ReferenceType referenceTypeFrom(Card card);

    ReferenceType referenceTypeFrom(CardDescriptor cardDescriptor);

    ReferenceType referenceTypeFrom(Object idAsObject);

    CardDescriptor cardDescriptorFrom(ReferenceType referenceType);

    Card cardFrom(ReferenceType referenceType);

    ImpersonateApi<T> impersonate();
}
