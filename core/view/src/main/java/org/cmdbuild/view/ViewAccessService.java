package org.cmdbuild.view;

import java.util.Collection;
import java.util.Map;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;

public interface ViewAccessService {

    Card getCardById(View view, String cardId);

    PagedElements<Card> getCards(View view, DaoQueryOptions queryOptions);

    Collection<Attribute> getAttributesForView(View view);

    EntryType getEntryTypeForView(View view);

    Card getCardForCurrentUser(View view, String cardId);

    Card createUserCard(View view, Map<String, Object> values);

    Card updateUserCard(View view, long cardId, Map<String, Object> values);

    void deleteUserCard(View view, long cardId);

}
