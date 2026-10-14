/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.CardWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CardWs;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkId;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Mockito.*;

/**
 *
 * @author afelice
 */
public class CardWsTest extends WsTestBase {

    private static final String A_KNOWN_CLASS_NAME = "aClass";
    private static final String A_KNOWN_ATTRIBUTE_NAME = "aAttribute";

    private final CardWs instance;

    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mockBuildCardWsSerializationHelperv3();

    private final AttributeWithoutOwner attribute1;
    private final AttributeWithoutOwner attribute2;
    private final Classe aSuperClasse;
    private final Card aSuperCard_1;

    public CardWsTest() {
        CardWsCommand command = new CardWsCommand(userCardService);
        instance = new CardWs(userClassService, userCardService, daoService, cardWsSerializationHelperv3, dmsService, cardsForDomainFetcher, command);

        attribute1 = mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME1);
        attribute2 = mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME2);
        aSuperClasse = mockBuildClasse(A_KNOWN_CLASS_NAME1, list(attribute1, attribute2));
        aSuperCard_1 = mockBuildCard(A_KNOWN_CARD_ID1, aSuperClasse,
                map(
                        attribute1.getName(), "superCard_1",
                        attribute2.getName(), "super card 1"
                ));
    }

    /**
     * Test of readOne method invoked services, with info only flag, of class
     * CardWs.
     */
    @Test
    public void testReadOne_infoOnly() {
        System.out.println("readOne_infoOnly");

        //arrange:
        String classeId = aSuperClasse.getId().toString();
        when(userCardService.getUserCardInfo(classeId, A_KNOWN_CARD_ID1)).thenReturn(aSuperCard_1);

        //act:
        Object resultObject = instance.readOne(aSuperClasse.getId().toString(), aSuperCard_1.getId(),
                false, false, false, true); // infoOnly is true

        //assert:
        verify(userCardService).getUserCardInfo(eq(classeId), eq(A_KNOWN_CARD_ID1));
        checkId(A_KNOWN_CARD_ID1, resultObject);
    }

    /**
     * Test of readOne method invoked services, default, of class CardWs.
     */
    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        String classeId = aSuperClasse.getId().toString();
        when(userCardService.getUserCard(classeId, A_KNOWN_CARD_ID1)).thenReturn(aSuperCard_1);

        //act:
        Object resultObject = instance.readOne(aSuperClasse.getId().toString(), aSuperCard_1.getId(),
                false, false, false, false); // no flags set

        //assert:
        verify(userCardService).getUserCard(eq(classeId), eq(A_KNOWN_CARD_ID1));
        checkId(A_KNOWN_CARD_ID1, resultObject);
    }

    /**
     * Test of readOne method invoked services, with include model flag, of
     * class CardWs.0
     */
    @Test
    public void testReadOne_includeModel() {
        System.out.println("readOne_includeModel");

        //arrange:
        String classeId = aSuperClasse.getId().toString();
        when(userCardService.getUserCard(classeId, A_KNOWN_CARD_ID1)).thenReturn(aSuperCard_1);

        //act:
        Object resultObject = instance.readOne(aSuperClasse.getId().toString(), aSuperCard_1.getId(),
                true, false, false, false); // include model flag set

        //assert:
        verify(userCardService).getUserCard(eq(classeId), eq(A_KNOWN_CARD_ID1));
        checkId(A_KNOWN_CARD_ID1, resultObject);
    }

    /**
     * Test of readOne method invoked services, with include widgets flag, of
     * class CardWs.
     */
    @Test
    public void testReadOne_includeWidgets() {
        System.out.println("readOne_includeWidgets");

        //arrange:
        String classeId = aSuperClasse.getId().toString();
        when(userCardService.getUserCard(classeId, A_KNOWN_CARD_ID1)).thenReturn(aSuperCard_1);

        //act:
        Object resultObject = instance.readOne(aSuperClasse.getId().toString(), aSuperCard_1.getId(),
                false, true, false, false); // include widgets flags set

        //assert:
        verify(userCardService).getUserCard(eq(classeId), eq(A_KNOWN_CARD_ID1));
        checkId(A_KNOWN_CARD_ID1, resultObject);
    }

    /**
     * Test of readOne method invoked services, with include stats flag, of
     * class CardWs.
     */
    @Test
    public void testReadOne_includeStats() {
        System.out.println("readOne_includeStats");

        //arrange:
        int expStats_AttachmentCount = 2;
        int expStats_EmailCount = 3;
        String classeId = aSuperClasse.getId().toString();
        when(userCardService.getUserCard(classeId, A_KNOWN_CARD_ID1)).thenReturn(aSuperCard_1);
        when(dmsService.getCardAttachmentCountSafe(aSuperCard_1)).thenReturn(expStats_AttachmentCount);
        mockSelectCount(daoService, EMAIL_CLASS_NAME, expStats_EmailCount);

        //act:
        Object resultObject = instance.readOne(aSuperClasse.getId().toString(), aSuperCard_1.getId(),
                false, false, true, false); // infoStats is true

        //assert:
        verify(userCardService).getUserCard(eq(classeId), eq(A_KNOWN_CARD_ID1));
        checkId(A_KNOWN_CARD_ID1, resultObject);
    }

    /**
     * Test of readMany method invoked services, with distinct attribute flag,
     * of class CardWs.
     *
     * <p>
     * Returns the distinct values for an attribute/couples of attributes. Used
     * to create dynamic multipart keys.
     */
    @Test
    public void testReadMany_distinctAttribute() {
        System.out.println("readMany_distinctAttribute");

        //arrange:
        WsQueryOptions wsQueryOptions = TestHelper_WSData.mockBuildWsQueryOptions(true);
        String classeId = A_KNOWN_CLASS_NAME;
        String distinctAttribute = A_KNOWN_ATTRIBUTE_NAME;
        final FluentMap<String, Object> firstAttribValue = map("key", 1);
        final FluentMap<String, Object> secondAttribValue = map("key", 2);
        List<Map<String, Object>> expMapList = list(firstAttribValue, secondAttribValue);
        mockSelectDistinctAttribute(daoService, classeId, distinctAttribute, expMapList);
        List<AttributeWithoutOwner> attributeList = list(mockBuildAttributeWithoutOwner(A_KNOWN_ATTRIBUTE_NAME));
        Classe classe = mockBuildClasse(A_KNOWN_CLASS_NAME, attributeList);
        when(userClassService.getUserClass(classeId)).thenReturn(classe);
        when(daoService.getClasse(classeId)).thenReturn(classe);

        //act:
        Object result = instance.readMany(classeId, wsQueryOptions, null,
                "", false, A_KNOWN_ATTRIBUTE_NAME, "");

        //assert:
        verify(userClassService).getUserClass(classeId);
        verify(daoService).getClasse(classeId);
    }

    private void mockSelectCount(DaoService dao, String className, long expCount) {
        QueryBuilder selectCountBuilder = mock(QueryBuilder.class);
        when(dao.selectCount()).thenReturn(selectCountBuilder);
        QueryBuilder fromBuilder = mock(QueryBuilder.class);
        when(selectCountBuilder.from(className)).thenReturn(fromBuilder);
        QueryBuilder whereBuilder = mock(QueryBuilder.class);
        when(fromBuilder.where(anyString(), any(), any(Object[].class))).thenReturn(whereBuilder);
        when(whereBuilder.getCount()).thenReturn(expCount);
    }

    private void mockSelectDistinctAttribute(DaoService dao, String className, String attributeName, List<Map<String, Object>> expMapList) {
        QueryBuilder selectDistinctBuilder = mock(QueryBuilder.class);
        when(dao.selectDistinct(attributeName)).thenReturn(selectDistinctBuilder);
        when(selectDistinctBuilder.accept(any())).thenReturn(selectDistinctBuilder);
        QueryBuilder fromBuilder = mock(QueryBuilder.class);
        when(selectDistinctBuilder.from(className)).thenReturn(fromBuilder);
        QueryBuilder whereBuilder = mock(QueryBuilder.class);
        when(fromBuilder.where(any(CmdbFilter.class))).thenReturn(whereBuilder);
        List<ResultRow> resultRows = expMapList.stream().map(expMap -> {
                    ResultRow resRow = mock(ResultRow.class);
                    when(resRow.asMap()).thenReturn(expMap);
                    return resRow;
                })
                .collect(toList());
        when(whereBuilder.run()).thenReturn(resultRows);
    }
}
