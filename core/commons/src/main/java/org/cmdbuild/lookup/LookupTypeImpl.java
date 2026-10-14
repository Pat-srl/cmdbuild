package org.cmdbuild.lookup;

import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.lookup.LookupAccessType.LT_DEFAULT;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DEFAULT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_LookupType")
public class LookupTypeImpl implements LookupType {

    private final String name;
    private final Long parent, id;
    private final LookupAccessType accessType;
    private final LookupSpeciality speciality;

    private LookupTypeImpl(LookupTypeImplBuilder builder) {
        this.id = ltEqZeroToNull(builder.id);
        this.name = checkNotBlank(builder.name);
        this.parent = ltEqZeroToNull(builder.parent);
        this.accessType = firstNotNull(builder.accessType, LT_DEFAULT);
        this.speciality = firstNotNull(builder.speciality, LS_DEFAULT);
    }

    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Override
    @Nullable
    @CardAttr("ParentType")
    public Long getParent() {
        return parent;
    }

    @Override
    @CardAttr("Access")
    public LookupAccessType getAccessType() {
        return accessType;
    }

    @Override
    @CardAttr
    public LookupSpeciality getSpeciality() {
        return speciality;
    }

    public static LookupTypeImplBuilder builder() {
        return new LookupTypeImplBuilder();
    }

    public static LookupTypeImplBuilder copyOf(LookupTypeImpl source) {
        return new LookupTypeImplBuilder()
                .withName(source.getName())
                .withAccessType(source.getAccessType())
                .withParent(source.getParent())
                .withSpeciality(source.getSpeciality());
    }

    public static class LookupTypeImplBuilder implements Builder<LookupTypeImpl, LookupTypeImplBuilder> {

        private String name;
        private Long parent, id;
        private LookupAccessType accessType;
        private LookupSpeciality speciality;

        public LookupTypeImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public LookupTypeImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public LookupTypeImplBuilder withParent(Long parent) {
            this.parent = parent;
            return this;
        }

        public LookupTypeImplBuilder withAccessType(LookupAccessType accessType) {
            this.accessType = accessType;
            return this;
        }

        public LookupTypeImplBuilder withSpeciality(LookupSpeciality speciality) {
            this.speciality = speciality;
            return this;
        }

        @Override
        public LookupTypeImpl build() {
            return new LookupTypeImpl(this);
        }

    }
}
