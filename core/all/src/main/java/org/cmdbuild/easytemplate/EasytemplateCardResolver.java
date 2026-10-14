package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForwardingAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;

public class EasytemplateCardResolver implements Function<String, Object> {

    private final Card card;

    public EasytemplateCardResolver(Card card) {
        this.card = checkNotNull(card);
    }

    public static EasytemplateCardResolver forCard(Card card) {
        return new EasytemplateCardResolver(card);
    }

    @Override
    public Object apply(String expression) {
        if (ATTR_ID.equalsIgnoreCase(expression)) {
            return card.getId();
        }
        return new ForwardingAttributeTypeVisitor() {

            private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

            private Object adapted;

            @Override
            protected CMAttributeTypeVisitor delegate() {
                return DELEGATE;
            }

            public Object adapt(final Object value) {
                adapted = value;
                card.getType().getAttributeOrNull(expression).getType().accept(this);
                return adapted;
            }

            @Override
            public void visit(final ForeignKeyAttributeType attributeType) {
                adapted = IdAndDescriptionImpl.class.cast(adapted).getId();
            }

            @Override
            public void visit(final LookupAttributeType attributeType) {
                adapted = IdAndDescriptionImpl.class.cast(adapted).getId();
            }

            @Override
            public void visit(final ReferenceAttributeType attributeType) {
                adapted = IdAndDescriptionImpl.class.cast(adapted).getId();
            }

        }.adapt(card.get(expression));
    }

}
