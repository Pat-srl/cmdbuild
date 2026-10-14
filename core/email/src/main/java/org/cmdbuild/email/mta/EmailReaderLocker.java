/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.lock.LockType.ILT_EMAILACCOUNT;
import static org.cmdbuild.lock.LockTypeUtils.itemIdWithLockType;

/**
 *
 * @author afelice
 */
public class EmailReaderLocker implements AutoCloseable {

    private final EmailAccount emailAccount;
    private final LockService lockService;

    public EmailReaderLocker(EmailAccount emailAccount, LockService lockService) {
        this.emailAccount = checkNotNull(emailAccount);
        this.lockService = lockService;
        acquireLock();
    }

    protected final void acquireLock() {
        lockService.aquireLockOrWait(getEmailAccountLockId()).getLock();
    }

    protected final void releaseLock() {
        lockService.releaseLock(getEmailAccountLockId());
    }

    private String getEmailAccountLockId() {
        return itemIdWithLockType(ILT_EMAILACCOUNT, emailAccount.getAddress());
    }

    @Override
    public void close() {
        releaseLock();
    }

}
