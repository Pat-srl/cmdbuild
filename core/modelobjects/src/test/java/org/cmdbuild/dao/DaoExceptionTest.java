/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dao;

import org.cmdbuild.dao.DaoException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.dao.DataIntegrityViolationException;

public class DaoExceptionTest {

    @Test
    public void testMissingMandatoryAttribute() {

        //act:
        DaoException ex = assertThrows(
                DaoException.class,
                () -> {
                    throw new DaoException(new DataIntegrityViolationException("""
                            ERROR: new row for relation "PreventiveMaint" violates check constraint "_cm3_PrevMaintConfig_notnull"
                            Detail: Failing row contains (445999, "PreventiveMaint", null, PM.000.000.154 - test, A, system / admin, 2025-11-04 12:29:29.466596+01, null, null, 445999, null, 1, {}, zcf9qoobae8s6szv4qhjho9s, {}, {}, null, river#0#g121u3u4xzbuwa62b6ng34f8, {"ProcessId": 0, "regDetails": "", "stepAction": "", "ProcessCla..., PM.000.000.154, 2025-11-04 12:29:23+01, null, test, 278763, null, null, null, 277465, null, null, null, PM.000.000.154, null, null, 379010, null, null, null, null, null, 2025-11-04 12:29:23+01, 0.00, null, null, null, null, null, null, 379522, null, null, null, null, f, f, null, null, null, null, null, f, f, 383322).
                            """
                    ));
                }
        );

        //assert:
        assertEquals("CMO 208: attribute \"PrevMaintConfig\" of \"PreventiveMaint\" is mandatory but found null", ex.getMessage());
    }

    @Test
    public void testDuplicateUniqueValue() {

        //act:
        DaoException ex = assertThrows(
                DaoException.class,
                () -> {
                    throw new DaoException(new DataIntegrityViolationException("""
                            ERROR: duplicate key value violates unique constraint "_cm3_Budget_Code"
                            Detail: Key ("Code")=(BG2019) already exists.
                            """
                    ));
                }
        );

        //assert:
        assertEquals("CMO 201: duplicate value: a card with \"Code\" \"BG2019\" does already exist", ex.getMessage());
    }
}
