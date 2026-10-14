/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.repository.CardIdService;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.JsonAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.orm.CardMapperConfigRepository;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.orm.services.CardMapperConfigRepositoryImpl;
import org.cmdbuild.dao.orm.services.CardMapperLoader;
import org.cmdbuild.dao.orm.services.CardMapperServiceImpl;
import org.cmdbuild.dao.orm.test.beans.JsonBeanImpl;
import org.cmdbuild.dao.orm.test.beans.MyEnum;
import org.cmdbuild.dao.orm.test.beans.RequestData;
import org.cmdbuild.dao.orm.test.beans.SimpleRequestData;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardMapperTest {

    private final ClasseRepository classeRepository = mock(ClasseRepository.class);
    private final Classe classe = mock(Classe.class);

    private final CardMapperConfigRepository mapperRepository = new CardMapperConfigRepositoryImpl();
    private final CardMapperLoader loader = new CardMapperLoader(mapperRepository);
    private CardMapperService service;

    @Before
    public void init() {
        loader.scanClassesForHandlers(asList(SimpleRequestData.class));

        Attribute actionId = attr("ActionId", new StringAttributeType(), classe);
        Attribute payload = attr("Payla", new StringAttributeType(), classe);
        Attribute payloadSize = attr("PayloadSize", new IntegerAttributeType(), classe);
        Attribute response = attr("Response", new StringAttributeType(), classe);
        Attribute bean = attr("Bean", JsonAttributeType.INSTANCE, classe);
        Attribute method = attr("Method", new StringAttributeType(), classe);

        when(classe.getAttributeOrNull("ActionId")).thenReturn(actionId);
        when(classe.getAttributeOrNull("Payla")).thenReturn(payload);
        when(classe.getAttributeOrNull("PayloadSize")).thenReturn(payloadSize);
        when(classe.getAttributeOrNull("Response")).thenReturn(response);
        when(classe.getAttributeOrNull("Bean")).thenReturn(bean);
        when(classe.getAttributeOrNull("Method")).thenReturn(method);

        when(classe.getAttribute("ActionId")).thenReturn(actionId);
        when(classe.getAttribute("Payla")).thenReturn(payload);
        when(classe.getAttribute("PayloadSize")).thenReturn(payloadSize);
        when(classe.getAttribute("Response")).thenReturn(response);
        when(classe.getAttribute("Bean")).thenReturn(bean);
        when(classe.getAttribute("Method")).thenReturn(method);

        when(classe.hasAttribute("ActionId")).thenReturn(true);
        when(classe.hasAttribute("Payla")).thenReturn(true);
        when(classe.hasAttribute("PayloadSize")).thenReturn(true);
        when(classe.hasAttribute("Response")).thenReturn(true);
        when(classe.hasAttribute("Bean")).thenReturn(true);
        when(classe.hasAttribute("Method")).thenReturn(true);

        when(classe.getServiceAttributes()).thenReturn(asList(actionId, payload, payloadSize, response, bean, method));

        when(classe.getName()).thenReturn("Request");
        when(classe.getClassType()).thenReturn(ClassType.CT_SIMPLE);

        when(classeRepository.getClasse("Request")).thenReturn(classe);

        service = new CardMapperServiceImpl(mapperRepository, classeRepository, mock(CardIdService.class), mock(CardMapperLoader.class));
    }

    @Test
    public void testObjectToCard() {

        RequestData object = SimpleRequestData.builder()
                .withActionId("myActionId")
                .withPayload("myPayload")
                .withPayloadSize(123)
                .withResponse(null)
                .withErrorOrWarningEvents(new JsonBeanImpl("hello"))
                .withMethod(MyEnum.ME_TWO)
                .build();

        Card card = service.objectToCard(object);

        assertEquals("myActionId", card.get("ActionId", String.class));
        assertEquals("myPayload", card.get("Payla", String.class));
        assertEquals(Integer.valueOf(123), card.get("PayloadSize", Integer.class));
        assertEquals(null, card.get("Response", String.class));
        assertEquals("{\"value\":\"hello\"}", card.getString("Bean"));
        assertEquals("two", card.getString("Method"));
    }

    private static Attribute attr(String name, CardAttributeType type, Classe classe) {
        return AttributeImpl.builder().withName(name).withType(type).withMeta(new AttributeMetadataImpl(emptyMap())).withOwner(classe).build();
    }
}
