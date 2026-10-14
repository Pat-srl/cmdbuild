/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join.inner;

import java.util.Collection;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.view.View;

public interface JoinViewQueryService {

    PagedElements<Card> getCards(View view, DaoQueryOptions queryOptions);

    Card getCard(View view, String cardId);

    Collection<Attribute> getAttributesForView(View view);

    void validateViewConfig(View view);

    EntryType getEntryTypeForView(View view);
}
