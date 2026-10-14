package org.cmdbuild.client.rest.impl;

import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.client.rest.api.MenuApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestClientException;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.model.MenuEntry;
import org.cmdbuild.client.rest.model.SimpleMenuEntry;

public class MenuApiImpl extends AbstractServiceClientImpl implements MenuApi {

	public MenuApiImpl(RestWsClient restClient) {
		super(restClient);
	}

	@Override
	public MenuEntry getMenu() {
		JsonObject root = get("sessions/current/menu").asJson().getAsJsonObject().getAsJsonObject("data");
		return toMenuEntry(root);
	}

	private MenuEntry toMenuEntry(JsonObject jsonObject) {
		try {
			List<MenuEntry> children = Streams.stream(jsonObject.getAsJsonArray("children").iterator())
					.map(JsonElement::getAsJsonObject)
					.map(this::toMenuEntry)
					.collect(toList());
			return SimpleMenuEntry.builder()
					.withMenuType(toString(jsonObject.get("menuType")))
					.withObjectDescription(toString(jsonObject.get("objectDescription")))
					.withObjectId(toLong(jsonObject.get("objectId")))
					.withObjectType(toString(jsonObject.get("objectType")))
					.withChildren(children)
					.build();
		} catch (Exception ex) {
			throw new RestClientException(ex, "error deserializing menu = %s", jsonObject);
		}
	}

}
