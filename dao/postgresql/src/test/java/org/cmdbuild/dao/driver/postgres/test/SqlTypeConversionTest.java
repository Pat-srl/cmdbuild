package org.cmdbuild.dao.driver.postgres.test;

import static java.util.Collections.emptyMap;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.postgres.utils.SqlTypeName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.attributeTypeToSqlType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.attributeTypeToSqlTypeName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.createAttributeType;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Ignore;
import org.junit.Test;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FloatAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.JsonAttributeType;

public class SqlTypeConversionTest {

    private static final AttributeMetadata NO_META = new AttributeMetadataImpl(emptyMap());

    @Test
    public void supportsBooleanAttributes() {
        final CardAttributeType<?> type = createAttributeType("bool", NO_META);
        assertThat(type, instanceOf(BooleanAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.bool));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("bool")));
    }

    @Test
    public void supportsCharAttributes() {
        final CardAttributeType<?> type = createAttributeType("bpchar", NO_META);
        assertThat(type, instanceOf(CharAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.bpchar));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("bpchar(1)")));
    }

    @Test
    public void supportsCharAttributesWithLimit() {
        final CardAttributeType<?> type = createAttributeType("bpchar(1)", NO_META);
        assertThat(type, instanceOf(CharAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.bpchar));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("bpchar(1)")));
    }

    @Test
    public void supportsJsonbAttributes() {
        final CardAttributeType<?> type = createAttributeType("jsonb", NO_META);
        assertThat(type, instanceOf(JsonAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.jsonb));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("jsonb")));
    }

//	@Test
//	public void supportsCharStringAttributes() {
//		final CMAttributeType<?> type = createAttributeType("bpchar(32)", NO_META);
//		assertThat(type, instanceOf(StringAttributeType.class));
//		assertThat(((StringAttributeType) type).length, is(32));
////		assertThat(SqlType.getSqlType(type), is(SqlType.bpchar));//TODO: warning: this will be deserialized as StringAttribute, but then serialized back as varchar (so it is not currently fully supported)
////		assertThat(SqlType.getSqlTypeString(type), is(equalTo("bpchar(32)"))); 
//	}
    @Test
    public void supportsDateAttributes() {
        final CardAttributeType<?> type = createAttributeType("date", NO_META);
        assertThat(type, instanceOf(DateAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.date));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("date")));
    }

    @Test
    public void supportsDoubleAttributes() {
        final CardAttributeType<?> type = createAttributeType("float8", NO_META);
        assertThat(type, instanceOf(DoubleAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.float8));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("float8")));
    }

    @Test
    public void supportsFloatAttributes() {
        final CardAttributeType<?> type = createAttributeType("float4", NO_META);
        assertThat(type, instanceOf(FloatAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.float4));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("float4")));
    }

    @Test
    public void supportsIPAddressAttributes() {
        final CardAttributeType<?> type = createAttributeType("inet", NO_META);
        assertThat(type, instanceOf(IpAddressAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.inet));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("inet")));
    }

    @Test
    public void supportsIntegerAttributes() {
        final CardAttributeType<?> type = createAttributeType("int4", NO_META);
        assertThat(type, instanceOf(IntegerAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.int4));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("int4")));
    }

    @Test
    public void supportsLookupAttributes() {
        final CardAttributeType<?> type = createAttributeType("int8", new AttributeMetadataImpl(map(AttributeMetadata.LOOKUP_TYPE, "^_^")));
        assertThat(type, instanceOf(LookupAttributeType.class));
        assertThat(((LookupAttributeType) type).getLookupTypeName(), is("^_^"));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.int8));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("int8")));
    }

    @Ignore
    @Test
    public void supportsReferenceAttributes() {
        final CardAttributeType<?> type = createAttributeType("int8", new AttributeMetadataImpl(emptyMap()));
        assertThat(type, instanceOf(ReferenceAttributeType.class));
        // TODO check correct domain
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.int8));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("int8")));
    }

    @Ignore
    @Test
    public void supportsForeignKeyAttributes() {
        final CardAttributeType<?> type = createAttributeType("int8", new AttributeMetadataImpl(emptyMap()));
        assertThat(type, instanceOf(ForeignKeyAttributeType.class));
        // TODO check correct target class
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.int8));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("int8")));
    }

    @Test
    public void supportsDecimalAttributes() {
        final CardAttributeType<?> type = createAttributeType("numeric(43,21)", NO_META);
        assertThat(type, instanceOf(DecimalAttributeType.class));
        assertThat(((DecimalAttributeType) type).getPrecision(), is(43));
        assertThat(((DecimalAttributeType) type).getScale(), is(21));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.numeric));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("numeric(43,21)")));
    }

    @Test
    public void supportsEntryTypeAttributes() {
        final CardAttributeType<?> type = createAttributeType("regclass", NO_META);
        assertThat(type, instanceOf(RegclassAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.regclass));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("regclass")));
    }

    @Test
    public void supportsTextAttributes() {
        final CardAttributeType<?> type = createAttributeType("text", NO_META);
        assertThat(type, instanceOf(TextAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.text));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("text")));
    }

    @Test
    public void supportsTimeAttributes() {
        final CardAttributeType<?> type = createAttributeType("time", NO_META);
        assertThat(type, instanceOf(TimeAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.time));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("time")));
    }

    @Test
    public void supportsDateTimeAttributes() {
        final CardAttributeType<?> type = createAttributeType("timestamp", NO_META);
        assertThat(type, instanceOf(DateTimeAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.timestamptz));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("timestamptz")));
    }

    @Test
    public void supportsStringAttributes() {
        final CardAttributeType<?> type = createAttributeType("varchar(20)", NO_META);
        assertThat(type, instanceOf(StringAttributeType.class));
        assertThat(((StringAttributeType) type).getLength(), is(20));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.varchar));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("varchar(20)")));
    }

    @Test
    public void supportsStringAttributes2() {
        final CardAttributeType<?> type = createAttributeType("varchar", NO_META);
        assertThat(type, instanceOf(StringAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.varchar));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("varchar")));
    }

    @Test
    public void supportsSqlStringArrayAttributes() {
        final CardAttributeType<?> type = createAttributeType("_varchar", NO_META);
        assertThat(type, instanceOf(StringArrayAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName._varchar));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("_varchar")));
    }

    @Test
    public void supportssupportSqlBinaryAttributes() {
        final CardAttributeType<?> type = createAttributeType("bytea", NO_META);
        assertThat(type, instanceOf(ByteArrayAttributeType.class));
        assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.bytea));
        assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("bytea")));
    }

//    @Test(expected = Exception.class)
//    public void doesNotsupportSqlIntegerArrayAttributes() {
//        final CardAttributeType<?> type = createAttributeType("_int4", NO_META);
//        assertThat(type, instanceOf(UndefinedAttributeType.class));
////		assertThat(attributeTypeToSqlTypeName(type), is(SqlTypeName.unknown));
////		assertThat(attributeTypeToSqlType(type).toSqlTypeString(), is(equalTo("unknown")));
//    }
}
