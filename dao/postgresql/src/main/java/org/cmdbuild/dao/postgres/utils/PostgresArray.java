package org.cmdbuild.dao.postgres.utils;

import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import static java.sql.Types.VARCHAR;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.BIGINT;
import static java.sql.Types.BINARY;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.Nullable;
import org.apache.commons.codec.binary.Hex;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class PostgresArray implements Array {

	private static final String NULL = "NULL";
	private static final String EMPTY = "{}";

	private final String baseTypeName;
	private final int baseType;
	private final List values;
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
			return format("{%s}", list.stream().map(l->Long.toString(l)).collect(joining(",")));
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

}
