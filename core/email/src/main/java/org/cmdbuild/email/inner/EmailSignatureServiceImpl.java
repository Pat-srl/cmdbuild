/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static org.cmdbuild.cache.CacheConfig.SYSTEM_OBJECTS;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class EmailSignatureServiceImpl implements EmailSignatureService {

    private final DaoService dao;
    private final Holder<List<EmailSignature>> allEmailSignatures;
    private final CmCache<EmailSignature> emailSignatureByCode, emailSignatureById;
    private final ObjectTranslationService translationService;

    public EmailSignatureServiceImpl(DaoService daoService, CacheService cacheService, ObjectTranslationService translationService) {
        this.dao = checkNotNull(daoService);
        allEmailSignatures = cacheService.newHolder("email_signature_all", SYSTEM_OBJECTS);
        emailSignatureByCode = cacheService.newCache("email_signature_by_id", SYSTEM_OBJECTS);
        emailSignatureById = cacheService.newCache("email_signature_by_code", SYSTEM_OBJECTS);
        this.translationService = checkNotNull(translationService);
    }

    private void invalidateCache() {
        allEmailSignatures.invalidate();
        emailSignatureByCode.invalidateAll();
        emailSignatureById.invalidateAll();
    }

    @Override
    public String getSignatureHtmlForCurrentUser(long id) {
        EmailSignature signature = getOne(id);
        checkArgument(signature.isActive(), "error, signature %s is not active", signature);
        return translationService.translateEmailSignatureContenthtml(signature.getCode(), signature.getContentHtml());
    }

    @Override
    public List<EmailSignature> getAll() {
        return allEmailSignatures.get(() -> dao.selectAll().from(EmailSignature.class).asList());
    }

    @Override
    public EmailSignature getOneByCode(String code) {
        return emailSignatureById.get(checkNotBlank(code), () -> getAll().stream().filter(s -> equal(s.getCode(), code)).collect(onlyElement("email signature not found for code =< %s >", code)));
    }

    @Override
    public EmailSignature getOne(long id) {
        return emailSignatureById.get(id, () -> getAll().stream().filter(s -> equal(s.getId(), id)).collect(onlyElement("email signature not found for id =< %s >", id)));
    }

    @Override
    public EmailSignature create(EmailSignature emailSignature) {
        emailSignature = dao.create(emailSignature);
        invalidateCache();
        return getOne(emailSignature.getId());
    }

    @Override
    public EmailSignature update(EmailSignature emailSignature) {
        emailSignature = dao.update(emailSignature);
        invalidateCache();
        return getOne(emailSignature.getId());
    }

    @Override
    public void delete(long id) {
        dao.delete(EmailSignature.class, id);
        invalidateCache();
    }

}
