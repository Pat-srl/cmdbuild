/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.utils.lang.Builder;

public class PositionOfImpl implements PositionOf {

    private final boolean foundCard;
    private final Long positionInPage, positionInTable, pageOffset;
    private final long actualOffset;

    private PositionOfImpl(PositionOfImplBuilder builder) {
        this.foundCard = builder.foundCard;
        if (foundCard) {
            this.positionInPage = checkNotNull(builder.positionInPage);
            this.positionInTable = checkNotNull(builder.positionInTable);
            this.pageOffset = checkNotNull(builder.pageOffset);
        } else {
            this.positionInPage = null;
            this.positionInTable = null;
            this.pageOffset = null;
        }
        this.actualOffset = checkNotNull(builder.actualOffset, "actual offset not set in positionOf metadata bean");
    }

    @Override
    public boolean foundCard() {
        return foundCard;
    }

    @Override
    public long getPositionInPage() {
        checkHasCard();
        return positionInPage;
    }

    @Override
    public long getPositionInTable() {
        checkHasCard();
        return positionInTable;
    }

    @Override
    public long getPageOffset() {
        checkHasCard();
        return pageOffset;
    }

    @Override
    public long getActualOffset() {
        return actualOffset;
    }

    private void checkHasCard() {
        checkArgument(foundCard, "info not available: positionOf card not found");
    }

    public static PositionOfImplBuilder builder() {
        return new PositionOfImplBuilder();
    }

    public static class PositionOfImplBuilder implements Builder<PositionOfImpl, PositionOfImplBuilder> {

        private Boolean foundCard;
        private Long positionInPage;
        private Long positionInTable;
        private Long pageOffset;
        private Long actualOffset;

        public PositionOfImplBuilder withFoundCard(Boolean foundCard) {
            this.foundCard = foundCard;
            return this;
        }

        public PositionOfImplBuilder withPositionInPage(Long positionInPage) {
            this.positionInPage = positionInPage;
            return this;
        }

        public PositionOfImplBuilder withPositionInTable(Long positionInTable) {
            this.positionInTable = positionInTable;
            return this;
        }

        public PositionOfImplBuilder withPageOffset(Long pageOffset) {
            this.pageOffset = pageOffset;
            return this;
        }

        public PositionOfImplBuilder withActualOffset(Long actualOffset) {
            this.actualOffset = actualOffset;
            return this;
        }

        @Override
        public PositionOfImpl build() {
            return new PositionOfImpl(this);
        }

    }
}
