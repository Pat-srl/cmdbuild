/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.collect.Ordering;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.v4.model.WsFunctionOutputModel;
import org.cmdbuild.service.rest.v4.model.WsFunctionOutputParameter;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.NAME;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * @author ldare
 */
@Component
public class FunctionsWsCommand {

    private final DaoService daoService;

    public FunctionsWsCommand(DaoService daoService) {
        this.daoService = checkNotNull(daoService);
    }

    public PagedElements<StoredFunction> doReadAll(Integer limit, Integer offset, String filterStr) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);

        List<StoredFunction> list = daoService.getAllFunctions().stream().sorted(Ordering.natural().onResultOf(StoredFunction::getId)).collect(toList());
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {

            list = AttributeFilterProcessor.<StoredFunction>builder()
                    .withKeyToValueFunction((key, fun) -> switch (key) {
                        case NAME -> fun.getName();
                        case "tags" -> fun.getTags();
                        case "source" ->
                                fun.hasSourceClassName() ? daoService.getClasse(fun.getSourceClassName()) : null;
                        default -> throw unsupported("unsupported filter key = %s", key);
                    })
                    .withConditionEvaluatorFunction((AttributeFilterCondition condition, Object value) -> switch (condition.getOperator()) {
                        case EQUAL -> equal(toStringOrNull(value), condition.getSingleValue());
                        case IN -> condition.getValues().contains(toStringOrNull(value));
                        case CONTAIN -> {
                            if (value instanceof Collection collection) {
                                yield collection.contains(condition.getSingleValue());//TODO improve this
                            } else {
                                yield value != null && ((Classe) value).equalToOrAncestorOf(daoService.getClasse(condition.getSingleValue())); //TODO filter also
                            }
                        }
                        default ->
                                throw new IllegalArgumentException("unsupported operator = " + condition.getOperator());
                    })
                    .withFilter(filter.getAttributeFilter())
                    .filter(list);
        }

        return PagedElements.paged(list, offset, limit);
    }

    public StoredFunction doRead(String idOrName) {
        checkNotBlank(idOrName);
        if (NumberUtils.isCreatable(idOrName)) {
            return daoService.getFunctionById(Long.parseLong(idOrName));
        } else {
            return daoService.getFunctionByName(idOrName);
        }
    }

    public List<Object> doGetInputParams(String inputs, StoredFunction function) {
        Map<String, Object> params = isBlank(inputs) ? emptyMap() : fromJson(inputs, MAP_OF_OBJECTS);
        return function.getInputParameters().stream().map(Attribute::getName).map(params::get).collect(toList());
    }

    public List<Attribute> doGetOutputParams(String model, StoredFunction function) {
        return isBlank(model)
                ? function.getOutputParameters()
                : parseOutputParameters(function, model);
    }

    public List<ResultRow> doGetResults(StoredFunction function, List<Object> inputParams, List<Attribute> outputParams) {
        return daoService.selectFunction(function, inputParams, outputParams).run();
    }


    private List<Attribute> parseOutputParameters(StoredFunction function, String modelStr) {
        WsFunctionOutputModel model = CmJsonUtils.fromJson(modelStr, WsFunctionOutputModel.class);
        Map<String, WsFunctionOutputParameter> customParams = uniqueIndex(model.getOutput(), (p) -> p.getName());
        return function.getOutputParameters().stream().map((param) -> {
            WsFunctionOutputParameter customParam = customParams.get(param.getName());
            if (customParam != null) {
                param = AttributeImpl.builder().withOwner(function).withName(param.getName()).withType(customParam.getType()).withMeta(param.getMetadata()).build();
            }
            return param;

        }).collect(toList());
    }
}
