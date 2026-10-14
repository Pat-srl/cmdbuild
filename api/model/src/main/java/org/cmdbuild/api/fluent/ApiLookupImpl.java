package org.cmdbuild.api.fluent;

import static org.cmdbuild.common.Constants.LOOKUP_CLASS_NAME;

public class ApiLookupImpl extends CardDescriptorImpl implements Lookup {

    private String type;
    private String code;
    private String description;

    public ApiLookupImpl(Long id) {
        super(LOOKUP_CLASS_NAME, id);
    }

    @Override
    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    @Override
    public String getCode() {
        return code;
    }

    void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

}
