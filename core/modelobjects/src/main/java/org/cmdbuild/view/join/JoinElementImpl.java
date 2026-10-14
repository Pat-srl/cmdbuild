/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class JoinElementImpl implements JoinElement {

    private final String source, domain, targetType, domainAlias, targetAlias;
    private final RelationDirection direction;
    private final JoinType joinType;

    private JoinElementImpl(JoinElementImplBuilder builder) {
        this(builder.toMap());
    }

    public JoinElementImpl(Map<String, ?> config) {
        source = toStringNotBlank(config.get("source"), "missing join element source");
        domain = toStringNotBlank(config.get("domain"), "missing join element domain");
        targetType = toStringOrNull(config.get("targetType"));
        domainAlias = toStringNotBlank(config.get("domainAlias"), "missing join element domain alias");
        targetAlias = toStringNotBlank(config.get("targetAlias"), "missing join element target alias");
        direction = parseEnum(toStringNotBlank(config.get("direction"), "missing join element direction"), RelationDirection.class);
        joinType = parseEnum(toStringNotBlank(config.get("joinType"), "missing join element join type"), JoinType.class);
    }

    @JsonAnyGetter
    public Map<String, String> toMap() {
        return copyOf(this).toMap();
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    @Nullable
    public String getTargetType() {
        return targetType;
    }

    @Override
    public String getDomainAlias() {
        return domainAlias;
    }

    @Override
    public String getTargetAlias() {
        return targetAlias;
    }

    @Override
    public RelationDirection getDirection() {
        return direction;
    }

    @Override
    public JoinType getJoinType() {
        return joinType;
    }

    public static JoinElementImplBuilder builder() {
        return new JoinElementImplBuilder();
    }

    public static JoinElementImplBuilder copyOf(JoinElementImpl source) {
        return new JoinElementImplBuilder()
                .withSource(source.getSource())
                .withDomain(source.getDomain())
                .withTargetType(source.getTargetType())
                .withDomainAlias(source.getDomainAlias())
                .withTargetAlias(source.getTargetAlias())
                .withDirection(source.getDirection())
                .withJoinType(source.getJoinType());
    }

    public static class JoinElementImplBuilder implements Builder<JoinElementImpl, JoinElementImplBuilder> {

        private String source;
        private String domain;
        private String targetType;
        private String domainAlias;
        private String targetAlias;
        private RelationDirection direction;
        private JoinType joinType;

        public Map<String, String> toMap() {
            return map(
                    "source", source,
                    "domain", domain,
                    "targetType", targetType,
                    "domainAlias", domainAlias,
                    "targetAlias", targetAlias,
                    "direction", serializeEnum(direction),
                    "joinType", serializeEnum(joinType));
        }

        public JoinElementImplBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public JoinElementImplBuilder withDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public JoinElementImplBuilder withTargetType(String targetType) {
            this.targetType = targetType;
            return this;
        }

        public JoinElementImplBuilder withDomainAlias(String domainAlias) {
            this.domainAlias = domainAlias;
            return this;
        }

        public JoinElementImplBuilder withTargetAlias(String targetAlias) {
            this.targetAlias = targetAlias;
            return this;
        }

        public JoinElementImplBuilder withDirection(RelationDirection direction) {
            this.direction = direction;
            return this;
        }

        public JoinElementImplBuilder withJoinType(JoinType joinType) {
            this.joinType = joinType;
            return this;
        }

        @Override
        public JoinElementImpl build() {
            return new JoinElementImpl(this);
        }

    }
}
