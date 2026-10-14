package org.cmdbuild.dao.beans;

import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecordValues;

public class CardValueSet implements DatabaseRecordValues {

	private final Card card;

	public CardValueSet(final Card card) {
		this.card = card;
	}

	@Override
	public Object get(final String key) {
		return card.get(key);
	}

	@Override
	public <T> T get(final String key, final Class<? extends T> requiredType) {
		return card.get(key, requiredType);
	}

	@Override
	public <T> T get(final String key, final Class<? extends T> requiredType, final T defaultValue) {
		return card.get(key, requiredType, defaultValue);
	}

	@Override
	public Iterable<Map.Entry<String, Object>> getAttributeValues() {
		return card.getAttributeValues();
	}

}
