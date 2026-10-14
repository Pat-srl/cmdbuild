/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.test;

import static com.google.common.base.Objects.equal;
import static java.util.Arrays.asList;
import org.cmdbuild.dao.driver.repository.CardIdService;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.orm.services.CardMapperLoader;
import org.cmdbuild.dao.orm.services.CardMapperConfigRepositoryImpl;
import org.cmdbuild.dao.orm.test.beans.RequestData;
import org.cmdbuild.dao.orm.test.beans.SimpleRequestData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.cmdbuild.dao.orm.CardMapperConfigRepository;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.orm.services.CardMapperServiceImpl;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class CardMapperLoaderTest {

    @Test
    public void testCardMapperLoader() {
        CardMapperConfigRepository repository = new CardMapperConfigRepositoryImpl();
        CardMapperLoader loader = new CardMapperLoader(repository);

        loader.scanClassesForHandlers(asList(SimpleRequestData.class));

        assertFalse(repository.getConfigs().isEmpty());
        assertNotNull(repository.getConfigs().stream().anyMatch(c -> equal(c.getTargetClass(), RequestData.class)));

        CardMapperService service = new CardMapperServiceImpl(repository, mock(ClasseRepository.class), mock(CardIdService.class), mock(CardMapperLoader.class));

        assertNotNull(service.getMapperForModelOrBuilder(RequestData.class));
        assertNotNull(service.getMapperForModelOrBuilder(SimpleRequestData.class));
        assertEquals("Request", service.getMapperForModelOrBuilder(RequestData.class).getClassId());
        assertEquals(SimpleRequestData.class, service.getMapperForModelOrBuilder(RequestData.class).getTargetClass());
    }
}
