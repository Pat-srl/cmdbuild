/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import jakarta.annotation.Nullable;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.common.utils.PositionOfImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;

public class PositionOfUtils {

    public static PositionOf buildPositionOf(@Nullable Long rowNumber, DaoQueryOptions queryOptions) {
        long offset = queryOptions.getOffset();
        if (rowNumber == null) {
            return PositionOfImpl.builder()
                    .withFoundCard(false)
                    .withActualOffset(offset)
                    .build();
        } else {
            long positionInPage = rowNumber % queryOptions.getLimit();
            long pageOffset = rowNumber - positionInPage;
            if (queryOptions.getGoToPage()) {
                offset = pageOffset;
            }
            return PositionOfImpl.builder()
                    .withFoundCard(true)
                    .withPositionInPage(positionInPage)
                    .withPositionInTable(rowNumber)
                    .withPageOffset(pageOffset)
                    .withActualOffset(offset)
                    .build();
        }
    }
}
