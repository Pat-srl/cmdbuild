package org.cmdbuild.client.rest.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import java.io.IOException;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.client.rest.impl.LookupApiImpl.SimpleLookupValue;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class LookupApiBeansTest {

	private final ObjectMapper mapper = new ObjectMapper() {
		{
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
	};

	@Test
	public void testDeserialization() throws IOException {

		String content = "{\"data\":[{\"_id\":109,\"_type\":\"Employee - Type\",\"code\":\"Manager\",\"description\":\"Manager\",\"number\":1,\"active\":true,\"parent_id\":null,\"parent_type\":null,\"default\":false},{\"_id\":13098,\"_type\":\"Employee - Type\",\"code\":\"Regular\",\"description\":\"Regular\",\"number\":2,\"active\":true,\"parent_id\":null,\"parent_type\":null,\"default\":false},{\"_id\":13099,\"_type\":\"Employee - Type\",\"code\":\"Trainee\",\"description\":\"Trainee\",\"number\":3,\"active\":true,\"parent_id\":null,\"parent_type\":null,\"default\":false}],\"meta\":{\"total\":3,\"positions\":{},\"references\":{}},\"success\":true,\"message\":\"\"}";

		JsonNode response = mapper.readTree(content);

		List<SimpleLookupValue> list = Streams.stream(response.get("data")).map((record) -> jacksonToObject(record, SimpleLookupValue.class)).collect(toList());

		assertEquals(3, list.size());
	}

	private <T> T jacksonToObject(JsonNode jsonNode, Class<T> classe) {
		try {
			return mapper.readValue(jsonNode.traverse(), classe);
		} catch (IOException ex) {
			throw runtime(ex);
		}
	}

}
