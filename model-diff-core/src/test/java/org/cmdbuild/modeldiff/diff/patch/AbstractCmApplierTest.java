/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.patch;

import java.util.Map;
import java.util.Objects;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.constants.SystemAttributes;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.modeldiff.TestHelper_Model;
import org.cmdbuild.modeldiff.diff.stub.KnownModelRootNode;
import org.cmdbuild.modeldiff.stub.KnownModelRoot;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.cmdbuild.sync.CardSync;
import org.cmdbuild.sync.AttributeSync;
import static org.cmdbuild.modeldiff.TestHelper_Model.mockBuildClasse;

/**
 *
 * @author afelice
 */
public class AbstractCmApplierTest {

    protected static final int A_KNOWN_CARD_VALUE = 43;

    private final CardSync cardSynch = mock(CardSync.class);
    private final AttributeSync attributeSynch = mock(AttributeSync.class);

    /**
     * Test of apply method, changed delta, of class AbstractCmApplier.
     */
    @Test
    public void testApply_Equal() {
        System.out.println("apply_Equal");

        //arrange:
        StubCmCardDataApplierImpl cardApplier = new StubCmCardDataApplierImpl(cardSynch, attributeSynch); // applier of synch

        KnownModelRoot root_left = new KnownModelRoot("A"); // (name in model stub will be interpreted as) Class A
        root_left.setDescription("aCardValue"); // (description in model stub will be interpreted as) source Card
        KnownModelRootNode root_left_node = new KnownModelRootNode(root_left); // source aggregating node

        KnownModelRoot root_right = new KnownModelRoot("A"); // (name in model stub will be interpreted as) Class A
        root_right.setDescription("aCardValue"); // (description in model stub will be interpreted as) target (not modified) Card
        KnownModelRootNode root_right_node = new KnownModelRootNode(root_right); // target aggregating node

        CmEqualDelta delta = new CmEqualDelta(KnownModelRootNode.class, "pippo", root_left_node);

        //act:
        KnownModelRoot result = cardApplier.apply(delta);

        //assert:
        verifyZeroInteractions(cardSynch);
        assertEquals(root_left, result);
    }

    /**
     * Test of apply method, changed delta, of class AbstractCmApplier.
     */
    @Test
    public void testApply_Change() {
        System.out.println("apply_Change");

        //arrange:
        StubCmCardDataApplierImpl cardApplier = new StubCmCardDataApplierImpl(cardSynch, attributeSynch); // applier of synch

        KnownModelRoot root_left = new KnownModelRoot("A"); // (name in model stub will be interpreted as) Class A
        root_left.setDescription("origCardValue"); // (description in model stub will be interpreted as) source Card
        KnownModelRootNode root_left_node = new KnownModelRootNode(root_left); // source aggregating node

        KnownModelRoot root_right = new KnownModelRoot("A"); // (name in model stub will be interpreted as) Class A
        root_right.setDescription("newCardValue"); // (description in model stub will be interpreted as) target (changed) Card
        KnownModelRootNode root_right_node = new KnownModelRootNode(root_right); // target aggregating node

        CmChangeDelta delta = new CmChangeDelta(KnownModelRootNode.class, "pippo", root_left_node, root_right_node);

        Classe expClasse = mockBuildClasse(root_right.getName());
        Map<String, Object> expAttrMap = map(
                SystemAttributes.ATTR_DESCRIPTION, root_right.getDescription()
        );
        Card expCard = CardImpl.buildCard(expClasse, expAttrMap);
        //act:
        KnownModelRoot result = cardApplier.apply(delta);

        //assert:
        verify(cardSynch, times(1)).update(matchClasse(expClasse), matchCard(expCard));
        assertEquals(root_right, result);
    }

    /**
     * Test of apply method, changed delta, of class AbstractCmApplier.
     */
    @Test
    public void testApply_Insert() {
        System.out.println("apply_Insert");

        //arrange:
        StubCmCardDataApplierImpl cardApplier = new StubCmCardDataApplierImpl(cardSynch, attributeSynch); // applier of synch

        KnownModelRoot root_right = new KnownModelRoot("A"); // (name in model stub will be interpreted as) Class A
        root_right.setDescription("aCardValue"); // (description in model stub will be interpreted as) target (inserted) card
        KnownModelRootNode root_right_node = new KnownModelRootNode(root_right);

        CmInsertDelta delta = new CmInsertDelta(KnownModelRootNode.class, "pippo", root_right_node);

        Classe expClasse = mockBuildClasse(root_right.getName());
        Card expCard = CardImpl.buildCard(expClasse, map(SystemAttributes.ATTR_CODE, A_KNOWN_CARD_VALUE));
        Map<String, Object> expAttrMap = map(
                SystemAttributes.ATTR_DESCRIPTION, root_right.getDescription()
        );
        //act:
        KnownModelRoot result = cardApplier.apply(delta);

        //assert:
        verify(cardSynch, times(1)).insert(matchClasse(expClasse), matchCardMap(expAttrMap));
        assertEquals(root_right, result);
    }

    /**
     * Test of apply method, changed delta, of class AbstractCmApplier.
     */
    @Test
    public void testApply_Remove() {
        System.out.println("apply_Remove");

        //arrange:
        StubCmCardDataApplierImpl cardApplier = new StubCmCardDataApplierImpl(cardSynch, attributeSynch); // applier of synch

        KnownModelRoot root_left = new KnownModelRoot("A"); // (name in model stub will be interpreted as) Class A
        root_left.setDescription("origCardValue"); // (description in model stub will be interpreted as) source (removed) Card
        KnownModelRootNode root_left_node = new KnownModelRootNode(root_left); // source aggregating node

        CmRemoveDelta delta = new CmRemoveDelta(KnownModelRootNode.class, "pippo", root_left_node);

        Classe expClasse = mockBuildClasse(root_left.getName());
        Card expCard = CardImpl.buildCard(expClasse, map(SystemAttributes.ATTR_CODE, A_KNOWN_CARD_VALUE));
        Map<String, Object> expAttrMap = map(
                SystemAttributes.ATTR_DESCRIPTION, root_left.getDescription()
        );
        //act:
        KnownModelRoot result = cardApplier.apply(delta);

        //assert:
        verify(cardSynch, times(1)).remove(matchClasse(expClasse), matchCard(expCard));
        assertEquals(root_left, result);
    }

    private static Classe matchClasse(Classe expClasse) {
        return argThat(new ClasseMatcher(expClasse));
    }

    private static Card matchCard(Card expCard) {
        return argThat(new CardMatcher(expCard));
    }

    private static Map<String, Object> matchCardMap(Map<String, Object> expCardMap) {
        return argThat(new CardMapMatcher(expCardMap));
    }

} // end test class

/**
 *
 * @author afelice
 */
class StubCmCardDataApplierImpl extends AbstractCmApplier<KnownModelRootNode, KnownModelRoot> {

    // can contain more synch objects, if nneded, for example to update model schema
    private final CardSync cardSynch;
    private final AttributeSync attributeSynch;

    public StubCmCardDataApplierImpl(CardSync cardSynch, AttributeSync attributeSynch) {
        this.cardSynch = cardSynch;
        this.attributeSynch = attributeSynch;
    }

    /**
     *
     * @param delta
     * @return target (changed) model object
     */
    @Override
    protected KnownModelRoot applyChange(CmChangeDelta<KnownModelRootNode, KnownModelRoot> delta) {
        KnownModelRoot changedObj = (KnownModelRoot) delta.getTargetModelNode().getModelObj();

        Classe aClasse = mockBuildClasse(changedObj.getName());
        Map<String, Object> expAttribs = map(
                SystemAttributes.ATTR_DESCRIPTION, changedObj.getDescription()
        );
        Card aCard = CardImpl.buildCard(aClasse, expAttribs);
        // A simulation of use of synch object, to test invocation of update()
        Card updatedCard = cardSynch.update(aClasse, aCard);

        // (use of attributeSynch, if needed)
        //
        return changedObj;
    }

    /**
     *
     * @param delta
     * @return target (inserted) model object
     */
    @Override
    protected KnownModelRoot applyInsert(CmInsertDelta<KnownModelRootNode, KnownModelRoot> delta) {
        KnownModelRoot insertedObj = (KnownModelRoot) delta.getTargetModelNode().getModelObj();

        Classe aClasse = mockBuildClasse(insertedObj.getName());
        Card aCard = CardImpl.buildCard(aClasse, map(SystemAttributes.ATTR_CODE, AbstractCmApplierTest.A_KNOWN_CARD_VALUE));
        // A simulation of use of synch object, to test invocation of update()
        Card insertedCard = cardSynch.insert(aClasse, map(
                SystemAttributes.ATTR_DESCRIPTION, insertedObj.getDescription()
        ));

        // (use of attributeSynch, if needed)
        //
        return insertedObj;
    }

    /**
     *
     * @param delta
     * @return source (removed) model object
     */
    @Override
    protected KnownModelRoot applyRemove(CmRemoveDelta<KnownModelRootNode, KnownModelRoot> delta) {
        KnownModelRoot removedObj = (KnownModelRoot) delta.getSourceModelNode().getModelObj();

        Classe aClasse = mockBuildClasse(removedObj.getName());
        Card aCard = CardImpl.buildCard(aClasse, map(SystemAttributes.ATTR_CODE, AbstractCmApplierTest.A_KNOWN_CARD_VALUE));
        // A simulation of use of synch object, to test invocation of update()
        cardSynch.remove(aClasse, aCard);

        // (use of attributeSynch, if needed)
        //
        return removedObj;
    }

} // end StubCmCardDataApplierImpl class

class ClasseMatcher extends ArgumentMatcher<Classe> {

    private final Classe expLeft;

    ClasseMatcher(Classe expLeft) {
        this.expLeft = expLeft;
    }

    @Override
    public boolean matches(Object obj) {
        Classe actualRight = (Classe) obj;
        return Objects.equals(expLeft.getName(), actualRight.getName());
    }
} // end ClasseMatcher class

class CardMatcher extends ArgumentMatcher<Card> {

    private final Card expLeft;

    CardMatcher(Card expLeft) {
        this.expLeft = expLeft;
    }

    @Override
    public boolean matches(Object obj) {
        Card actualRight = (Card) obj;
        return Objects.equals(expLeft.getCode(), actualRight.getCode())
                && Objects.equals(expLeft.getType(), actualRight.getType());
    }
} // end CardMatcher class

class CardMapMatcher extends ArgumentMatcher<Map> {

    private final Map<String, Object> expLeft;

    CardMapMatcher(Map<String, Object> expLeft) {
        this.expLeft = expLeft;
    }

    @Override
    public boolean matches(Object obj) {
        Map<String, Object> actualRight = (Map<String, Object>) obj;
        TestHelper_Model.checkEquals_Map(expLeft, actualRight);
        return true;
    }
} // end CardMatcher class
