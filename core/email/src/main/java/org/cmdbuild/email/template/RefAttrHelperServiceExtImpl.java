/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.auth.AuthConst.SYSTEM_USER;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.TransientSessionHelper;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.postgres.q3.QueryBuilderConfiguration;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderConfiguration.SqlQueryReferenceProcessingStrategy.RPS_IGNORETENANT;
import org.cmdbuild.dao.postgres.q3.RefAttrHelperService;
import org.cmdbuild.dao.postgres.q3.RefAttrHelperServiceExt;
import org.springframework.stereotype.Component;

@Component
public class RefAttrHelperServiceExtImpl implements RefAttrHelperServiceExt {

    private final RefAttrHelperService service;
    private final QueryBuilderConfiguration configuration;
    private final DaoService dao;
    private final OperationUserSupplier operationUserSupplier;
    private final SessionService sessionService;

    public RefAttrHelperServiceExtImpl(RefAttrHelperService service, QueryBuilderConfiguration configuration, DaoService dao, OperationUserSupplier operationUserSupplier, SessionService sessionService) {
        this.service = checkNotNull(service);
        this.configuration = checkNotNull(configuration);
        this.dao = checkNotNull(dao);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public Classe getTargetClassForAttribute(Attribute a) {
        return service.getTargetClassForAttribute(a);
    }

    @Override
    public Attribute getAttrForMasterCardFilterOrNull(Classe source, Classe target) {
        return service.getAttrForMasterCardFilterOrNull(source, target);
    }

    @Override
    public Card getReferencedCard(Attribute attribute, @Nullable Object value) {
        if (value == null) {
            return null;
        } else if (configuration.hasReferenceProcessingStrategy(RPS_IGNORETENANT) && !operationUserSupplier.ignoreTenantPolicies()) {
            try (TransientSessionHelper helper = sessionService.createAndSetTransient(SYSTEM_USER)) {
                return getReferencedCard(attribute, value);
            }
        } else {
            return dao.getCard(getTargetClassForAttribute(attribute), ((IdAndDescription) value).getId());
        }
    }
}
