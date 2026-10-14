/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.virtual;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import org.cmdbuild.api.ApiConverterService;
import org.cmdbuild.corecomponents.CoreComponentService;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.FormulaType.FT_SCRIPT;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.FormulaType.FT_SQL;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_CLASS;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_DOMAIN;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FORMULA;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.utils.DatabaseRecordUtils;
import org.cmdbuild.script.ScriptService;
import static org.cmdbuild.script.ScriptType.ST_GROOVY;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VirtualAttributeServiceImpl implements VirtualAttributeService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreComponentService componentService;
    private final DaoService dao;
    private final ScriptService scriptService;
    private final ApiConverterService converterService;

    public VirtualAttributeServiceImpl(CoreComponentService componentService, DaoService dao, ScriptService scriptService, ApiConverterService converterService) {
        this.componentService = checkNotNull(componentService);
        this.dao = checkNotNull(dao);
        this.scriptService = checkNotNull(scriptService);
        this.converterService = checkNotNull(converterService);
    }

    @Override
    public <T extends DatabaseRecord> T loadVirtualAttributes(T databaseRecord) {
        return loadVirtualAttributes(databaseRecord, databaseRecord.getType().getActiveServiceAttributes());
    }

    @Override
    public <T extends DatabaseRecord> T loadVirtualAttributes(T databaseRecord, Collection<Attribute> attribute) {
        return DatabaseRecordUtils.copyOf(databaseRecord).accept(c -> {
            attribute.stream().filter(a -> a.isOfType(FORMULA)).forEach(a -> {
                try {
                    c.withAttribute(a.getName(), loadVirtualAttributeValue(databaseRecord, a));
                } catch (Exception ex) {
                    throw new DaoException(ex, "error processing virtual attribute = %s for record = %s", a, databaseRecord);
                }
            });
        }).build();
    }

    private <T extends DatabaseRecord> Object loadVirtualAttributeValue(T databaseRecord, Attribute attribute) {
        return switch (attribute.getMetadata().getFormulaType()) {
            case FT_SCRIPT -> {
                logger.debug("load virtual attr = {} with script code =< {} > for card = {}", attribute, attribute.getMetadata().getFormulaCode(), databaseRecord);
                String script = componentService.getComponent(attribute.getMetadata().getFormulaCode()).getData();
                try {
                    yield switch (databaseRecord.getType().getEtType()) {
                        case ET_CLASS ->
                            scriptService.helper(getClass(), script, ST_GROOVY).withData(map("card", converterService.daoCardToApiCard((Card) databaseRecord))).executeForOutput();
                        case ET_DOMAIN ->
                            scriptService.helper(getClass(), script, ST_GROOVY).withData(map("card", converterService.daoRelationToApiRelation((CMRelation) databaseRecord))).executeForOutput();
                        default ->
                            throw unsupported("unsupported card type = %s", databaseRecord.getType().getEtType());
                    };
                } catch (Exception e) {
                    logger.error("There was an error executing the script: {}", e.getCause().getMessage());
                    yield "Script error";
                }
            }
            case FT_SQL -> {
                logger.debug("load virtual attr = {} with sql function =< {} > for card = {}", attribute, attribute.getMetadata().getFormulaCode(), databaseRecord);
                StoredFunction function = dao.getFunctionByName(attribute.getMetadata().getFormulaCode());
                yield getOnlyElement(dao.callFunction(function, map(getOnlyElement(function.getInputParameterNames(), "invalid function for virtual attr"), databaseRecord.getId())).values(), "invalid function for virtual attr");//TODO improve function mapping
            }
            default ->
                throw unsupported("unsupported formula type = %s", attribute.getMetadata().getFormulaType());
        };
    }
}
