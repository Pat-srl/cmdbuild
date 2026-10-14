package org.cmdbuild.dao.entrytype.attributetype;

import static com.google.common.base.Preconditions.checkArgument;
import java.math.BigDecimal;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltZeroToNull;

public class DecimalAttributeType implements CardAttributeType<BigDecimal> {

    private final Integer precision;
    private final Integer scale;

    public DecimalAttributeType() {
        this.precision = null;
        this.scale = null;
    }

    public DecimalAttributeType(@Nullable Integer precision, @Nullable Integer scale) {
        if (precision == null && scale == null) {
            this.precision = this.scale = null;
        } else {
            this.precision = ltZeroToNull(precision);
            this.scale = ltZeroToNull(scale);
            checkArgument(precision == null || scale == null || precision >= scale, "invalid decimal attr params: precision = %s scale = %s (precision must be >= scale)", precision, scale);
        }
    }

    @Override
    public void accept(final CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.DECIMAL;
    }

    @Nullable
    public Integer getPrecision() {
        return precision;
    }

    @Nullable
    public Integer getScale() {
        return scale;
    }

    public boolean hasPrecisionAndScale() {
        return precision != null && scale != null;
    }

    public boolean hasPrecisionOrScale() {
        return precision != null || scale != null;
    }

}
