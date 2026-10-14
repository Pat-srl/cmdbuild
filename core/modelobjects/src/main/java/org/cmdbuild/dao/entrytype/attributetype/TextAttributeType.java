package org.cmdbuild.dao.entrytype.attributetype;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.entrytype.attributetype.TextAttributeLanguage.TAL_OTHER;

public class TextAttributeType implements CardAttributeType<String> {

    private final TextAttributeLanguage language;

    public TextAttributeType(TextAttributeLanguage language) {
        this.language = checkNotNull(language);
    }

    public TextAttributeType() {
        this(TAL_OTHER);
    }

    public TextAttributeLanguage getLanguage() {
        return language;
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.TEXT;
    }

}
