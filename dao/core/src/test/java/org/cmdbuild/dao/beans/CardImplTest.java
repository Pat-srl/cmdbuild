/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.dao.beans;

import com.google.common.annotations.VisibleForTesting;
import java.io.InputStream;
import java.io.Reader;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import static java.sql.Types.BIGINT;
import static java.sql.Types.BINARY;
import static java.sql.Types.VARCHAR;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Calendar;
import static java.util.Collections.emptyList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.Nullable;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.ClassType.CT_SIMPLE;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupArrayAttributeType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class CardImplTest {

    /**
     * Test of builder method, attribute of LOOKUPARRAY type, loaded an empty
     * PgArray, of class CardImpl.
     */
    @Test
    public void testBuilder_LookupArray_EmptyPgArray() {
        System.out.println("builder_LookupArray_EmptyPgArray");

        //arrange:
        String lookupArrayAttributeName = "AppType";
        Object value = new MyPostresArray(new Long[]{});
        Classe aClasse = mockClasse("Test", lookupArrayAttributeName);
        Attribute lookupArrayAttr = mockBuildAttr_LookupArray(lookupArrayAttributeName, aClasse);
        when(aClasse.getAttributeOrNull(eq(lookupArrayAttributeName))).thenReturn(lookupArrayAttr);
        List<LookupValue> expValue = emptyList();

        //act:
        CardImpl result = CardImpl.builder()
                .withType(aClasse)
                .withAttribute(lookupArrayAttributeName, value)
                .build();

        //assert:
        checkEquals(expValue, (List<LookupValue>) result.get(lookupArrayAttributeName));
    }

    /**
     * Test of builder method, attribute of LOOKUPARRAY type, loaded a PgArray,
     * of class CardImpl.
     */
    @Test
    public void testBuilder_LookupArray_PgArray() {
        System.out.println("builder_LookupArray_PgArray");

        //arrange:
        String lookupArrayAttributeName = "AppType";
        Object value = new MyPostresArray(new Long[]{1L, 2L, 3L});
        Classe aClasse = mockClasse("Test", lookupArrayAttributeName);
        Attribute lookupArrayAttr = mockBuildAttr_LookupArray(lookupArrayAttributeName, aClasse);
        when(aClasse.getAttributeOrNull(eq(lookupArrayAttributeName))).thenReturn(lookupArrayAttr);
        List<LookupValue> expValue = Arrays.asList(
                builLookupValue(1L, lookupArrayAttributeName),
                builLookupValue(2L, lookupArrayAttributeName),
                builLookupValue(3L, lookupArrayAttributeName)
        );

        //act:
        CardImpl result = CardImpl.builder()
                .withType(aClasse)
                .withAttribute(lookupArrayAttributeName, value)
                .build();

        //assert:
        checkEquals(expValue, (List<LookupValue>) result.get(lookupArrayAttributeName));
    }

    /**
     * Test of builder method, attribute of LOOKUPARRAY type, loaded a array of
     * {@link Long}, of class CardImpl.
     */
    @Test
    public void testBuilder_LookupArray_LongArray() {
        System.out.println("builder_LookupArray_LongArray");

        //arrange:
        String lookupArrayAttributeName = "AppType";
        Object value = new Long[]{1L, 2L, 3L};
        Classe aClasse = mockClasse("Test", lookupArrayAttributeName);
        Attribute lookupArrayAttr = mockBuildAttr_LookupArray(lookupArrayAttributeName, aClasse);
        when(aClasse.getAttributeOrNull(eq(lookupArrayAttributeName))).thenReturn(lookupArrayAttr);
        List<LookupValue> expValue = Arrays.asList(
                builLookupValue(1L, lookupArrayAttributeName),
                builLookupValue(2L, lookupArrayAttributeName),
                builLookupValue(3L, lookupArrayAttributeName)
        );

        //act:
        CardImpl result = CardImpl.builder()
                .withType(aClasse)
                .withAttribute(lookupArrayAttributeName, value)
                .build();

        //assert:
        checkEquals(expValue, (List<LookupValue>) result.get(lookupArrayAttributeName));
    }

    /**
     * Test of builder method, attribute of LOOKUPARRAY type, loaded a ArrayList
     * of {@link Long}, of class CardImpl.
     */
    @Test
    public void testBuilder_LookupArray_LongArrayList() {
        System.out.println("builder_LookupArray_LongArrayList");

        //arrange:
        String lookupArrayAttributeName = "AppType";
        Object value = asList(1L, 2L, 3L);
        Classe aClasse = mockClasse("Test", lookupArrayAttributeName);
        Attribute lookupArrayAttr = mockBuildAttr_LookupArray(lookupArrayAttributeName, aClasse);
        when(aClasse.getAttributeOrNull(eq(lookupArrayAttributeName))).thenReturn(lookupArrayAttr);
        List<LookupValue> expValue = Arrays.asList(
                builLookupValue(1L, lookupArrayAttributeName),
                builLookupValue(2L, lookupArrayAttributeName),
                builLookupValue(3L, lookupArrayAttributeName)
        );

        //act:
        CardImpl result = CardImpl.builder()
                .withType(aClasse)
                .withAttribute(lookupArrayAttributeName, value)
                .build();

        //assert:
        checkEquals(expValue, (List<LookupValue>) result.get(lookupArrayAttributeName));
    }

    /**
     * Test of builder method, attribute of LOOKUPARRAY type, loaded a ArrayList
     * of {@link Integer}, of class CardImpl.
     */
    @Test
    public void testBuilder_LookupArray_IntegerArrayList() {
        System.out.println("builder_LookupArray_IntegerArrayList");

        //arrange:
        String lookupArrayAttributeName = "AppType";
        Object value = asList(1, 2, 3);
        Classe aClasse = mockClasse("Test", lookupArrayAttributeName);
        Attribute lookupArrayAttr = mockBuildAttr_LookupArray(lookupArrayAttributeName, aClasse);
        when(aClasse.getAttributeOrNull(eq(lookupArrayAttributeName))).thenReturn(lookupArrayAttr);
        List<LookupValue> expValue = Arrays.asList(
                builLookupValue(1L, lookupArrayAttributeName),
                builLookupValue(2L, lookupArrayAttributeName),
                builLookupValue(3L, lookupArrayAttributeName)
        );

        //act:
        CardImpl result = CardImpl.builder()
                .withType(aClasse)
                .withAttribute(lookupArrayAttributeName, value)
                .build();

        //assert:
        checkEquals(expValue, (List<LookupValue>) result.get(lookupArrayAttributeName));
    }

    /**
     * Test of builder method, attribute of LOOKUPARRAY type, loaded a ArrayList
     * of {@link LookupValue}, of class CardImpl.
     */
    @Test
    public void testBuilder_LookupArray_LookupValueArrayList() {
        System.out.println("builder_LookupArray_LookupValueArrayList");

        //arrange:
        String lookupArrayAttributeName = "AppType";
        Object value = asList(
                builLookupValue(1L, lookupArrayAttributeName),
                builLookupValue(2L, lookupArrayAttributeName),
                builLookupValue(3L, lookupArrayAttributeName)
        );
        Classe aClasse = mockClasse("Test", lookupArrayAttributeName);
        Attribute lookupArrayAttr = mockBuildAttr_LookupArray(lookupArrayAttributeName, aClasse);
        when(aClasse.getAttributeOrNull(eq(lookupArrayAttributeName))).thenReturn(lookupArrayAttr);
        List<LookupValue> expValue = Arrays.asList(
                builLookupValue(1L, lookupArrayAttributeName),
                builLookupValue(2L, lookupArrayAttributeName),
                builLookupValue(3L, lookupArrayAttributeName)
        );

        //act:
        CardImpl result = CardImpl.builder()
                .withType(aClasse)
                .withAttribute(lookupArrayAttributeName, value)
                .build();

        //assert:
        checkEquals(expValue, (List<LookupValue>) result.get(lookupArrayAttributeName));
    }

    /**
     * Test of builder method, attribute of LOOKUPARRAY type, loaded a empty
     * ArrayList, of class CardImpl.
     */
    @Test
    public void testBuilder_LookupArray_EmptyArrayList() {
        System.out.println("builder_LookupArray_EmptyArrayList");

        //arrange:
        String lookupArrayAttributeName = "AppType";
        Object value = new ArrayList();
        Classe aClasse = mockClasse("Test", lookupArrayAttributeName);
        Attribute lookupArrayAttr = mockBuildAttr_LookupArray(lookupArrayAttributeName, aClasse);
        when(aClasse.getAttributeOrNull(eq(lookupArrayAttributeName))).thenReturn(lookupArrayAttr);
        List<LookupValue> expValue = emptyList();

        //act:
        CardImpl result = CardImpl.builder()
                .withType(aClasse)
                .withAttribute(lookupArrayAttributeName, value)
                .build();

        //assert:
        checkEquals(expValue, (List<LookupValue>) result.get(lookupArrayAttributeName));
    }

    /**
     * Test of builder method, attribute of LOOKUPARRAY type, loaded a
     * {@link String} with comma separated {@link Long}, of class CardImpl.
     */
    @Test
    public void testBuilder_LookupArray_StringOfLongs() {
        System.out.println("builder_LookupArray_StringOfLongs");

        //arrange:
        String lookupArrayAttributeName = "AppType";
        Object value = "1,2,3";
        Classe aClasse = mockClasse("Test", lookupArrayAttributeName);
        Attribute lookupArrayAttr = mockBuildAttr_LookupArray(lookupArrayAttributeName, aClasse);
        when(aClasse.getAttributeOrNull(eq(lookupArrayAttributeName))).thenReturn(lookupArrayAttr);
        List<LookupValue> expValue = Arrays.asList(
                builLookupValue(1L, lookupArrayAttributeName),
                builLookupValue(2L, lookupArrayAttributeName),
                builLookupValue(3L, lookupArrayAttributeName)
        );

        //act:
        CardImpl result = CardImpl.builder()
                .withType(aClasse)
                .withAttribute(lookupArrayAttributeName, value)
                .build();

        //assert:
        checkEquals(expValue, (List<LookupValue>) result.get(lookupArrayAttributeName));
    }

    private static Attribute mockBuildAttr(String name, CardAttributeType type, Classe classe, String[] metaValues) {
        return AttributeImpl.builder().withName(name).withDescription(name).withType(type).withMeta(metaValues).withOwner(classe).build();
    }

    private static Attribute mockBuildAttr_LookupArray(String lookupArrayAttributeName, Classe aClasse) {
        return mockBuildAttr(lookupArrayAttributeName, (new LookupArrayAttributeType(lookupArrayAttributeName)), aClasse, new String[]{});
    }

    private static Classe mockClasse(String classeName,
            String lookupArrayAttributeName) {
        Classe aClasseMock = mock(Classe.class);
        when(aClasseMock.getName()).thenReturn(classeName);
        when(aClasseMock.getClassType()).thenReturn(CT_SIMPLE);

        when(aClasseMock.getAliasToAttributeMap()).thenReturn(map(lookupArrayAttributeName, lookupArrayAttributeName));

        return aClasseMock;
    }

    private static LookupValue builLookupValue(Long id, String lookupTypeName) {
        return new LookupValueImpl(id, StringUtils.EMPTY, lookupTypeName);
    }

    private void checkEquals(List<LookupValue> expected, List<LookupValue> actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < actual.size(); i++) {
            LookupValueMatcher matcher = new LookupValueMatcher(expected.get(i));
            if (!matcher.matches(actual.get(i))) {
                fail(String.format("Mismatching expected item %s in expected <{ %s }> and actual <{ %s }>.", expected.get(i), expected, actual));
            }
        }
    }

} // end CardImplTest class

class LookupValueMatcher extends ArgumentMatcher<LookupValue> {

    private final LookupValue left;

    LookupValueMatcher(LookupValue left) {
        this.left = left;
    }

    @Override
    public boolean matches(Object obj) {
        LookupValue right = (LookupValue) obj;
        return Objects.equals(left.getId(), right.getId())
                && Objects.equals(left.getLookupType(), right.getLookupType());
    }
} // end LookupValueMatcher class

class MyPostresArray extends PostgresArray {

    MyPostresArray(Long[] values) {
        super(values);
    }

    @Override
    public ResultSet getResultSet() {
        return new AbstractResultSet() {
            Iterator valuesIt = values.stream().filter(Objects::nonNull).iterator();

            @Override
            public boolean next() throws SQLException {
                return valuesIt.hasNext();
            }

            @Override
            public Object getObject(int columnIndex) throws SQLException {
                return valuesIt.next();
            }

            @Override
            public void close() throws SQLException {
                // Nothing to do
            }
        };
    }
} // end MsPostgresArray class

/**
 * As in <code>org.cmdbuild.dao.postgres.utils.PostgresArray</code>, but copied
 * here to avoid cyclic reference
 *
 * @author afelice
 */
class PostgresArray implements Array {

    private static final String NULL = "NULL";
    private static final String EMPTY = "{}";

    private final String baseTypeName;
    private final int baseType;
    @VisibleForTesting
    protected final List values;
    private final String sqlStringValue;
    private final Object[] arrayPrototype;

    public PostgresArray(Long[] values) {
        this.values = values == null ? null : list(values);
        this.baseType = BIGINT;
        this.baseTypeName = "bigint";
        arrayPrototype = new Long[]{};
        this.sqlStringValue = longArrayToPostgreSQLArray(this.values);
    }

    public PostgresArray(String[] values) {
        this.values = values == null ? null : list(values);
        this.baseType = VARCHAR;
        this.baseTypeName = "varchar";
        arrayPrototype = new String[]{};
        this.sqlStringValue = stringArrayToPostgreSQLTextArray(this.values);//+"::varchar[]";
    }

    public PostgresArray(byte[][] values) {
        this.values = values == null ? null : list(values);
        this.baseType = BINARY;
        this.baseTypeName = "bytea";
        arrayPrototype = new byte[][]{};
        this.sqlStringValue = byteaArrayToPostgreSQLArray(this.values);
    }

    @Override
    public String toString() {
        return sqlStringValue;
    }

    @Override
    public Object getArray() throws SQLException {
        return values == null ? null : values.toArray(arrayPrototype);
    }

    @Override
    public Object getArray(Map<String, Class<?>> map) throws SQLException {
        return getArray();
    }

    @Override
    public Object getArray(long index, int count) throws SQLException {
        return values == null ? null : values.subList(toIntExact(index), toIntExact(index + count)).toArray(arrayPrototype);
    }

    @Override
    public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return getArray(index, count);
    }

    @Override
    public int getBaseType() throws SQLException {
        return baseType;
    }

    @Override
    public String getBaseTypeName() throws SQLException {
        return baseTypeName;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet getResultSet(long index, int count) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void free() throws SQLException {
        // nothing to do
    }

    private static String longArrayToPostgreSQLArray(@Nullable List<Long> list) {
        if (list == null) {
            return null;
        } else if (list.isEmpty()) {
            return EMPTY;
        } else {
            return format("{%s}", list.stream().map(l -> Long.toString(l)).collect(joining(",")));
        }
    }

    private static String byteaArrayToPostgreSQLArray(@Nullable List<byte[]> list) {
        if (list == null) {
            return null;
        } else if (list.isEmpty()) {
            return EMPTY;
        } else {
            return format("{%s}", list.stream().map((bytes) -> format("\"\\\\x%s\"", Hex.encodeHexString(bytes))).collect(joining(",")));
        }
    }

    private static String stringArrayToPostgreSQLTextArray(@Nullable List<String> stringArray) {
        int arrayLength;
        if (stringArray == null) {
            return NULL;
        } else if ((arrayLength = stringArray.size()) == 0) {
            return EMPTY;
        }
        // count the string length and if need to quote
        int neededBufferLentgh = 2; // count the beginning '{' and the
        // ending '}' brackets
        final boolean[] shouldQuoteArray = new boolean[stringArray.size()];
        for (int si = 0; si < arrayLength; si++) {
            // count the comma after the first element
            if (si > 0) {
                neededBufferLentgh++;
            }

            boolean shouldQuote;
            final String s = stringArray.get(si);
            if (s == null) {
                neededBufferLentgh += 4;
                shouldQuote = false;
            } else {
                final int l = s.length();
                neededBufferLentgh += l;
                if (l == 0 || s.equalsIgnoreCase(NULL)) {
                    shouldQuote = true;
                } else {
                    shouldQuote = false;
                    // scan for commas and quotes
                    for (int i = 0; i < l; i++) {
                        final char ch = s.charAt(i);
                        switch (ch) {
                            case '"':
                            case '\\':
                                shouldQuote = true;
                                // we will escape these characters
                                neededBufferLentgh++;
                                break;
                            case ',':
                            case '\'':
                            case '{':
                            case '}':
                                shouldQuote = true;
                                break;
                            default:
                                if (Character.isWhitespace(ch)) {
                                    shouldQuote = true;
                                }
                                break;
                        }
                    }
                }
                // count the quotes
                if (shouldQuote) {
                    neededBufferLentgh += 2;
                }
            }
            shouldQuoteArray[si] = shouldQuote;
        }

        final StringBuilder sb = new StringBuilder(neededBufferLentgh);
        sb.append('{');
        for (int si = 0; si < arrayLength; si++) {
            final String s = stringArray.get(si);
            if (si > 0) {
                sb.append(',');
            }
            if (s == null) {
                sb.append(NULL);
            } else {
                final boolean shouldQuote = shouldQuoteArray[si];
                if (shouldQuote) {
                    sb.append('"');
                }
                for (int i = 0, l = s.length(); i < l; i++) {
                    final char ch = s.charAt(i);
                    if (ch == '"' || ch == '\\') {
                        sb.append('\\');
                    }
                    sb.append(ch);
                }
                if (shouldQuote) {
                    sb.append('"');
                }
            }
        }
        sb.append('}');
        assert sb.length() == neededBufferLentgh;
        return sb.toString();
    }

} // end PostgresArray class

abstract class AbstractResultSet implements ResultSet {

    @Override
    public boolean next() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void close() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean first() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean last() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getType() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
} // end AbstractResultSet class
