/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.job;

import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.utils.lang.CmPreconditions;

public class MapperConfigImpl implements MapperConfig {

    private final String keyBegin;
    private final String keyEnd;
    private final String valueBegin;
    private final String valueEnd;

    public MapperConfigImpl() {
        this(emptyMap());
    }

    public MapperConfigImpl(Map<String, String> config) {
        keyBegin = CmPreconditions.firstNotBlank(config.get("mapper_key_init"), "<key>");
        keyEnd = CmPreconditions.firstNotBlank(config.get("mapper_key_end"), "</key>");
        valueBegin = CmPreconditions.firstNotBlank(config.get("mapper_value_init"), "<value>");
        valueEnd = CmPreconditions.firstNotBlank(config.get("mapper_value_end"), "</value>");
    }

    @Override
    public String getKeyBegin() {
        return keyBegin;
    }

    @Override
    public String getKeyEnd() {
        return keyEnd;
    }

    @Override
    public String getValueBegin() {
        return valueBegin;
    }

    @Override
    public String getValueEnd() {
        return valueEnd;
    }

}
