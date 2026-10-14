package org.cmdbuild.common.beans;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class TypeAndCodeImpl implements TypeAndCode {

    private final String type, code;

    public TypeAndCodeImpl(String type, String code) {
        this.type = checkNotBlank(type);
        this.code = checkNotBlank(code);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "TypeAndCode{" + "type=" + type + ", code=" + code + '}';
    }

    public static TypeAndCode typeAndCode(String type, String code) {
        return new TypeAndCodeImpl(type, code);
    }

}
