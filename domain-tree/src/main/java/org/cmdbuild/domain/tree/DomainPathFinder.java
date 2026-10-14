/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.domain.tree;

import java.util.List;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.services.serialization.domain.tree.CardData;
import org.cmdbuild.services.serialization.domain.tree.DomainTreeData;

/**
 *
 * @author afelice
 */
public interface DomainPathFinder {

    List<Card> findPathToRoot(DomainTreeData domainTreeData, CardData leafCardData);
    
}
