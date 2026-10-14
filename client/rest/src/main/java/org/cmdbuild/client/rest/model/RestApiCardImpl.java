/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableMap;
import java.util.Map;
import jakarta.annotation.Nullable;

public class RestApiCardImpl implements Card {

    private final Long cardId;
    private final String classId;
    private final Map<String, Object> attributes;

    public RestApiCardImpl(String classId, Long cardId, Map<String, ? extends Object> attributes) {
        this.cardId = cardId;
        this.classId = classId;
        this.attributes = unmodifiableMap(checkNotNull(attributes));
    }

    public RestApiCardImpl(Map<String, ? extends Object> attributes) {
        this(null, null, attributes);
    }

    @Override
    @Nullable
    public Long getId() {
        return cardId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "Card{" + "id=" + cardId + ", type=" + classId + ", attributes=" + attributes + '}';
    }

    @Override
    public String getClassName() {
        return classId;
    }

}
