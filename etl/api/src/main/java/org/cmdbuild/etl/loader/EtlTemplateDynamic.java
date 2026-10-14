package org.cmdbuild.etl.loader;

public interface EtlTemplateDynamic extends EtlTemplateReference {

    Object getDynamicTemplate();

    @Override
    default boolean isDynamic() {
        return true;
    }
}
