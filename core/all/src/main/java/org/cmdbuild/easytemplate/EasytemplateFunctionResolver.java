package org.cmdbuild.easytemplate;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import java.time.format.DateTimeFormatter;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.utils.date.CmDateUtils.isDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import org.springframework.stereotype.Component;

@Component
public class EasytemplateFunctionResolver implements Function<String, Object> {

    private final DaoService dao;

    public EasytemplateFunctionResolver(DaoService dao) {
        this.dao = dao;
    }

    @Override
    public Object apply(String expression) {
        Matcher matcher = Pattern.compile("^([^()]+)\\((.*)\\)$").matcher(expression);
        checkArgument(matcher.matches(), "invalid expression '%s'", expression);
        String name = matcher.group(1);
        String paramsExpr = matcher.group(2);
        List<String> params = isBlank(paramsExpr) ? emptyList() : Splitter.on(",").trimResults().splitToList(paramsExpr);
        StoredFunction function = dao.getFunctionByName(name);
        Attribute outputParameter = function.getOnlyOutputParameter();
        return convertOutput(dao.selectFunction(function, params).getSingleRow().asMap().get(outputParameter.getName()));
    }

    private @Nullable
    Object convertOutput(@Nullable Object output) {
        if (isDateTime(output)) {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(toDateTime(output));
        } else {
            return output;
        }
    }
}
