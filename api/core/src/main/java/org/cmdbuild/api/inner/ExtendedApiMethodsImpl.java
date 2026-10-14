/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.toIntExact;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.concurrent.Callable;
import jakarta.annotation.Nullable;
import org.cmdbuild.api.ApiImpersonateHelper;
import org.cmdbuild.api.ExtendedApi;
import org.cmdbuild.api.ExtendedApiMethods;
import org.cmdbuild.api.ImpersonateApi;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.CardDescriptor;
import org.cmdbuild.api.fluent.CardDescriptorImpl;
import org.cmdbuild.api.fluent.ExecutorBasedFluentApi;
import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.api.fluent.FluentApiExecutor;
import org.cmdbuild.api.fluent.WsType;
import org.cmdbuild.api.fluent.ws.AttrTypeVisitor;
import org.cmdbuild.api.fluent.ws.ClassAttribute;
import org.cmdbuild.api.fluent.ws.EntryTypeAttributeImpl;
import org.cmdbuild.api.fluent.ws.FunctionInput;
import org.cmdbuild.api.fluent.ws.FunctionOutput;
import org.cmdbuild.auth.session.ImpersonateRequestImpl;
import org.cmdbuild.common.Constants;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import org.cmdbuild.workflow.beans.EntryTypeAttribute;
import org.cmdbuild.workflow.inner.AttributeInfo;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ExtendedApiMethodsImpl implements ExtendedApiMethods {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LookupService lookupService;
    private final FluentApi fluentApi;
    private final DaoService dao;
    private final ApiImpersonateHelper impersonateHelper;

    public ExtendedApiMethodsImpl(FluentApiExecutor executor, LookupService lookupService, ApiImpersonateHelper impersonateHelper, DaoService dao) {
        fluentApi = new ExecutorBasedFluentApi(executor);
        this.lookupService = checkNotNull(lookupService);
        this.impersonateHelper = checkNotNull(impersonateHelper);
        this.dao = checkNotNull(dao);
    }

    @Override
    public AttributeInfo findAttributeFor(EntryTypeAttribute entryTypeAttribute) {//TODO check and improve this

        return new AttrTypeVisitor() {

            private String entryName;
            private AttributeInfo attributeInfo;

            public AttributeInfo attributeInfo() {
                ((EntryTypeAttributeImpl) entryTypeAttribute).accept(this);//TODO remove cast
                return (attributeInfo == null) ? unknownAttributeInfo(entryName) : attributeInfo;
            }

            private AttributeInfo unknownAttributeInfo(final String entryName) {
                return new AttributeInfo() {

                    @Override
                    public String getName() {
                        return entryName;
                    }

                    @Override
                    public WsType getWsType() {
                        return WsType.UNKNOWN;
                    }

                    @Override
                    public Optional<String> getTargetClassName() {
                        return Optional.empty();
                    }

                };
            }

            @Override
            public void visit(ClassAttribute classAttribute) {
                entryName = classAttribute.getClassName();
                Attribute attribute = dao.getClasse(entryName).getAttributeOrNull(classAttribute.getAttributeName());
                if (attribute != null) {
                    attributeInfo = new AttributeInfo() {

                        @Override
                        public String getName() {
                            return attribute.getName();
                        }

                        @Override
                        public WsType getWsType() {
                            return parseEnum(attribute.getType().getName().name(), WsType.class);
                        }

                        @Override
                        public Optional<String> getTargetClassName() {
                            return Optional.of(attribute.getOwner().getName());
                        }

                    };
                }
            }

            @Override
            public void visit(FunctionInput functionInput) {
                entryName = functionInput.getFunctionName();
                Attribute attribute = dao.getFunctionByName(entryName).getInputParameter(functionInput.getAttributeName());
                attributeInfo = new AttributeInfo() {

                    @Override
                    public String getName() {
                        return attribute.getName();
                    }

                    @Override
                    public WsType getWsType() {
                        return parseEnum(attribute.getType().getName().name(), WsType.class);
                    }

                    @Override
                    public Optional<String> getTargetClassName() {
                        return Optional.empty();
                    }

                };
            }

            @Override
            public void visit(FunctionOutput functionOutput) {
                entryName = functionOutput.getFunctionName();
                Attribute attribute = dao.getFunctionByName(entryName).getOutputParameter(functionOutput.getAttributeName());
                attributeInfo = new AttributeInfo() {

                    @Override
                    public String getName() {
                        return attribute.getName();
                    }

                    @Override
                    public WsType getWsType() {
                        return parseEnum(attribute.getType().getName().name(), WsType.class);
                    }

                    @Override
                    public Optional<String> getTargetClassName() {
                        return Optional.empty();
                    }

                };
            }

        }.attributeInfo();
    }

    @Override
    public ClassInfo findClass(String className) {
        return toClassInfo(dao.getClasse(className));
    }

    @Override
    public ClassInfo findClass(int oid) {
        return toClassInfo(dao.getClasse(oid));
    }

    @Override
    public LookupType selectLookupById(long id) {
        return convertLookup(lookupService.getLookup(id));
    }

    @Override
    public LookupType selectLookupByCode(String type, String code) {
        return convertLookup(lookupService.getLookupByTypeAndCode(type, code));
    }

    @Override
    public LookupType selectLookupByDescription(String type, String description) {
        return convertLookup(lookupService.getLookupByTypeAndDescription(type, description));
    }

    @Override
    public LookupType selectLookupByCodeCreateIfMissing(String type, String code) {
        LookupValue lookup = lookupService.getLookupByTypeAndCodeOrNull(type, code);
        if (lookup == null) {
            lookup = lookupService.createLookup(LookupValueImpl.builder().withType(lookupService.getLookupType(type)).withCode(code).build());
        }
        return convertLookup(lookup);
    }

    @Override
    public LookupType updateLookup(String type, String code, String description) {
        LookupValue existingLookup = lookupService.getLookupByTypeAndCode(type, code);
        return convertLookup(lookupService.createOrUpdateLookup(LookupValueImpl.copyOf(existingLookup).withDescription(description).build()));
    }

    @Override
    public CardDescriptor cardDescriptorFrom(ReferenceType referenceType) {
        checkNotNull(referenceType, "reference type param is null");
        ClassInfo classInfo;
        try {
            classInfo = checkNotNull(findClass(referenceType.getClassName()));
        } catch (Exception ex) {
            logger.warn("class not found for name = {} (trying fallback query on 'Class'): {}", referenceType.getClassName(), ex);
            ReferenceType fallbackReferenceType = referenceTypeFrom(referenceType.getId());
            classInfo = checkNotNull(findClass(fallbackReferenceType.getClassName()), "class not found for id = %s", referenceType.getClassName());
        }
        return new CardDescriptorImpl(classInfo.getName(), referenceType.getId());
    }

    @Override
    public Card cardFrom(ReferenceType referenceType) {
        return fluentApi.existingCard(cardDescriptorFrom(referenceType)).fetch();
    }

    @Override
    public ReferenceType referenceTypeFrom(Card card) {
        return new ReferenceType(card.getClassName(), card.getId(), ofNullable(card.getDescription()).orElseGet(() -> fluentApi.existingCard(card).limitAttributes(ATTR_DESCRIPTION).fetch().getDescription()), card.getCode());//TODO get code
    }

    @Override
    public ReferenceType referenceTypeFrom(Object idAsObject) {
        Long id = CmConvertUtils.convert(idAsObject, Long.class);
        if (isNotNullAndGtZero(id)) {
            return referenceTypeFrom(fluentApi.existingCard(Constants.BASE_CLASS_NAME, id).limitAttributes(ATTR_DESCRIPTION).fetch());
        } else {
            return new ReferenceType();
        }
    }

    @Override
    public ReferenceType referenceTypeFrom(CardDescriptor cardDescriptor) {
        return referenceTypeFrom(fluentApi.existingCard(cardDescriptor));
    }

    @Override
    public ImpersonateApi<ExtendedApi> impersonate() {
        return new ImpersonateApiHelper();
    }

    private ClassInfo toClassInfo(Classe classe) {
        return new ClassInfo(classe.getName(), classe.getOid());
    }

    private @Nullable
    LookupType convertLookup(@Nullable LookupValue in) {
        if (in == null) {
            return null;
        } else {
            LookupType out = new LookupType();
            out.setType(in.getType().getName());
            out.setId(toIntExact(in.getId()));
            out.setCode(in.getCode());
            out.setDescription(in.getDescription());
            return out;
        }
    }

    private class ImpersonateApiHelper implements ImpersonateApi<ExtendedApi> {

        private String username, group, sponsor;

        @Override
        public ImpersonateApi username(String username) {
            this.username = username;
            return this;
        }

        @Override
        public ImpersonateApi group(String group) {
            this.group = group;
            return this;
        }

        @Override
        public ImpersonateApi sponsor(String sponsor) {
            this.sponsor = sponsor;
            return this;
        }

        @Override
        public ExtendedApi impersonate() {
            return impersonateHelper.buildImpersonateApiWrapper(new ImpersonateRequestImpl(username, group, sponsor));
        }

        @Override
        public ExtendedApi transientImpersonate() {
            return impersonateHelper.buildImpersonateApiWrapper(new ImpersonateRequestImpl(username, group, sponsor, true));
        }

        @Override
        public <O> O call(Callable<O> callable) {
            return impersonateHelper.run(new ImpersonateRequestImpl(username, group, sponsor, false), callable);
        }
    }
}
