package org.cmdbuild.api.fluent;

public interface NewCard extends Card {

    NewCard withCode(String value);

    NewCard withDescription(String value);

    NewCard with(String name, Object value);

    NewCard withAttribute(String name, Object value);

    CardDescriptor create();

}
