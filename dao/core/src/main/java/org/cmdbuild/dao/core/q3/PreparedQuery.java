/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import static org.cmdbuild.dao.core.q3.DaoService.ROW_NUMBER;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface PreparedQuery {

    List<ResultRow> run();

    default boolean hasFalseFilter() {
        return false;
    }

    default long getCount() {
        return checkNotNull(convert(getOnlyElement(run()).asMap().get(COUNT), Long.class), "count not found: not a count query?");
    }

    @Nullable
    default <T> T getOneOrNull(Class<T> type) {
        ResultRow row = getOnlyElement(run(), null);
        return row == null ? null : row.toModel(type);
    }

    @Nullable
    default <T> T getOneOrNull() {
        ResultRow row = getOnlyElement(run(), null);
        return row == null ? null : row.toModel();
    }

    default <T> T getOne() {
        return checkNotNull(getOneOrNull(), "record not found");
    }

    default ResultRow getSingleRow() {
        return checkNotNull(getSingleRowOrNull(), "record not found");
    }

    @Nullable
    default ResultRow getSingleRowOrNull() {
        return getOnlyElement(run(), null);
    }

    @Nullable
    default Long getRowNumberOrNull() {
        return run().stream().collect(toOptional()).map((r) -> toLong(r.asMap().get(ROW_NUMBER)) - 1).orElse(null);
    }

    @Nullable
    default Long getFirstRowNumberOrNull() {
        return run().stream().map((r) -> toLong(r.asMap().get(ROW_NUMBER)) - 1).sorted().findFirst().orElse(null);
    }

    default <T> T getOne(Class<T> type) {
        return checkNotNull(getOneOrNull(type), "record not found");
    }

    @Nullable
    default Card getCardOrNull() {
        ResultRow row = getOnlyElement(run(), null);
        return row == null ? null : row.toCard();
    }

    default Card getCard() {
        return checkNotNull(getCardOrNull(), "card not found");
    }

    @Nullable
    default Long getCardIdOrNull() {
        return Optional.ofNullable(getCardOrNull()).map(Card::getId).orElse(null);
    }

    @Nullable
    default CMRelation getRelationOrNull() {
        ResultRow row = getOnlyElement(run(), null);
        return row == null ? null : row.toRelation();
    }

    default CMRelation getRelation() {
        return checkNotNull(getRelationOrNull(), "relation not found");
    }

    default List<Card> getCards() {
        return run().stream().map(ResultRow::toCard).collect(toList());
    }

    default List<CMRelation> getRelations() {
        return run().stream().map(ResultRow::toRelation).collect(toList());
    }

    default <T> List<T> asList(Class<T> type) {
        return run().stream().map((r) -> r.toModel(type)).collect(toList());
    }

    default <T> List<T> asList() {
        return run().stream().map((r) -> r.<T>toModel()).collect(toList());
    }

    @Nullable
    default <T> T asModelOrNull() {
        ResultRow row = getOnlyElement(run(), null);
        return row == null ? null : row.toModel();
    }

    @Nullable
    default boolean getSingleFunctionOutput(StoredFunction function) {
        checkArgument(function.hasOnlyOneOutputParameter(), "cannot get single function output: this function does not have a single output parameter");
        return getSingleRow().getFunctionOutput(function);
    }

}
