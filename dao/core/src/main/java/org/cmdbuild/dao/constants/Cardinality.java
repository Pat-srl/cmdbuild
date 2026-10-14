package org.cmdbuild.dao.constants;

/**
 * <code>cardinality.value()</code> in compatible with:
 * <ol>
 * <li>{@link #CARDINALITY_11} -> {@link org.cmdbuild.dao.entrytype.Domain#ONE_TO_ONE};
 * <li>{@link #CARDINALITY_N1} -> {@link org.cmdbuild.dao.entrytype.Domain#MANY_TO_ONE};
 * <li>{@link #CARDINALITY_1N} -> {@link org.cmdbuild.dao.entrytype.Domain#ONE_TO_MANY};
 * <li>{@link #CARDINALITY_NN} -> {@link org.cmdbuild.dao.entrytype.Domain#MANY_TO_MANY};
 * </ol>
 * 
 * through {@link org.cmdbuild.dao.utils.DomainUtils#parseDomainCardinality(java.lang.String)}
 * @author afelice
 */
public enum Cardinality {

	CARDINALITY_11("1:1"), //
	CARDINALITY_1N("1:N"), //
	CARDINALITY_N1("N:1"), //
	CARDINALITY_NN("N:N");

	private final String toString;

	private Cardinality(final String toString) {
		this.toString = toString;
	}

	public String value() {
		return toString;
	}

	public static Cardinality of(final String value) {
		for (final Cardinality element : values()) {
			if (element.toString.equals(value)) {
				return element;
			}
		}
		throw new IllegalArgumentException(value);
	}

}
