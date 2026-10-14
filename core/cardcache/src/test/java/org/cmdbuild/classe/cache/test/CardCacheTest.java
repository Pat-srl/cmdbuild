/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.cache.test;

import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.classe.cache.CardCacheServiceImpl.buildComparator;
import static org.cmdbuild.classe.cache.CardCacheServiceImpl.getOrder;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.beans.CardImpl.buildCard;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.data.filter.beans.CmdbSorterImpl.sorter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CardCacheTest {

    @Test
    public void testSort() {
        Classe classe = ClasseImpl.builder().withId(1l).withName("MyClass").build();
        Attribute attr = AttributeImpl.builder().withName("MyAttr").withType(new StringAttributeType()).withOwner(classe).build();
        classe = ClasseImpl.copyOf(classe).withAttributes(list(attr)).build();

        assertEquals(1, getOrder(null, "one", attr));
        assertEquals(-1, getOrder("one", null, attr));
        assertEquals(0, getOrder(null, null, attr));

        List<Card> cards = list(
                buildCard(classe, ATTR_ID, 3, "MyAttr", "c"),
                buildCard(classe, ATTR_ID, 2, "MyAttr", "b"),
                buildCard(classe, ATTR_ID, 1, "MyAttr", "a"),
                buildCard(classe, ATTR_ID, 4, "MyAttr", null));

        Collections.sort(cards, buildComparator(sorter("MyAttr", ASC)));

        assertEquals("1,2,3,4", cards.stream().map(c -> c.getId().toString()).collect(joining(",")));

        Collections.sort(cards, buildComparator(sorter("MyAttr", DESC)));

        assertEquals("4,3,2,1", cards.stream().map(c -> c.getId().toString()).collect(joining(",")));
    }

}
