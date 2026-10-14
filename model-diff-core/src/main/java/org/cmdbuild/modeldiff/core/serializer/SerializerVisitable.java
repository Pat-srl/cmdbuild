/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core.serializer;

import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

/**
 * Visitable for visitor pattern.
 *
 * <p>
 * From visitable to visitor invocation.
 *
 * @author afelice
 */
public interface SerializerVisitable {

    void accept(SerializerVisitor visitor);

    boolean hasName();
} // end SerializerVisitable interface

abstract class AbstractVisitable implements SerializerVisitable {

    protected final String fieldName;

    AbstractVisitable(String name) {
        this.fieldName = name;
    }

    /**
     * Is <code>false</code> for array items: each item has no fieldName.
     */
    @Override
    public boolean hasName() {
        return isNotBlank(fieldName);
    }
} // end AbstractVisitable

class NullVisitable extends AbstractVisitable {

    NullVisitable(String fieldName) {
        super(fieldName);
    }

    @Override
    public void accept(SerializerVisitor visitor) {
        visitor.visitNull(fieldName);
    }
} // end NullVisitable class

class NumberVisitable extends AbstractVisitable {

    private final Number value;

    NumberVisitable(String fieldName, Number value) {
        super(fieldName);
        this.value = value;
    }

    @Override
    public void accept(SerializerVisitor visitor) {
        visitor.visit(fieldName, value);
    }
} // end LongVisitable class

class BooleanVisitable extends AbstractVisitable {

    private final Boolean value;

    BooleanVisitable(String fieldName, Boolean value) {
        super(fieldName);
        this.value = value;
    }

    @Override
    public void accept(SerializerVisitor visitor) {
        visitor.visit(fieldName, value);
    }
} // end BooleanVisitable class

class StringVisitable extends AbstractVisitable {

    private final String value;

    StringVisitable(String fieldName, String value) {
        super(fieldName);
        this.value = value;
    }

    @Override
    public void accept(SerializerVisitor visitor) {
        visitor.visit(fieldName, value);
    }
} // end StringVisitable class

class EnumVisitable extends AbstractVisitable {

    private final Enum<?> value;

    EnumVisitable(String fieldName, Enum<?> value) {
        super(fieldName);
        this.value = value;
    }

    @Override
    public void accept(SerializerVisitor visitor) {
        visitor.visit(fieldName, value);
    }
} // end EnumVisitable class

class MapVisitable extends AbstractVisitable {

    private final Map<String, Object> value;

    MapVisitable(String fieldName, Map<String, Object> value) {
        super(fieldName);
        this.value = value;
    }

    @Override
    public void accept(SerializerVisitor visitor) {
        visitor.visit(fieldName, value);
    }
} // end MapVisitable class

class ListVisitable extends AbstractVisitable {

    private final List value;

    ListVisitable(String fieldName, List<Object> value) {
        super(fieldName);
        this.value = value;
    }

    @Override
    public void accept(SerializerVisitor visitor) {
        visitor.visit(fieldName, value);
    }
} // end ListVisitable class
