/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formtrigger;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.entrytype.Classe;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class FormTriggerServiceImpl implements FormTriggerService {

    private final FormTriggerRepository repository;
    private final CmCache<List<FormTrigger>> cache;

    public FormTriggerServiceImpl(FormTriggerRepository repository, CacheService cacheService) {
        this.repository = checkNotNull(repository);
        this.cache = cacheService.newCache("class_form_triggers_by_class", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public List<FormTrigger> getFormTriggersForClass(Classe classe) {
        return cache.get(classe.getName(), () -> repository.getFormTriggersForClass(classe.getName()).stream()
                .sorted(Ordering.natural().onResultOf(FormTriggerData::getIndex))
                .map(this::dataToTrigger)
                .collect(toList()));
    }

    @Override
    public void updateFormTriggersForClass(Classe classe, List<FormTrigger> data) {
        List<FormTriggerData> list = list();
        for (int index = 0; index < data.size(); index++) {
            FormTrigger trigger = data.get(index);
            list.add(FormTriggerDataImpl.builder()
                    .withClassId(classe.getName())
                    .withActive(trigger.isActive())
                    .withJsScript(trigger.getJsScript())
                    .withIndex(index)
                    .withBindings(trigger.getBindings().stream().map(FormTriggerBinding::name).collect(toList()))
                    .build());
        }
        repository.updateFormTriggersForClass(classe.getName(), list);
        cache.invalidate(classe.getName());
    }

    private FormTrigger dataToTrigger(FormTriggerData data) {
        return FormTriggerImpl.builder()
                .withActive(data.isActive())
                .withJsScript(data.getJsScript())
                .withBindings(data.getBindings().stream().map((b) -> FormTriggerBinding.valueOf(b)).collect(toSet()))
                .build();
    }

    @Override
    public void deleteForClass(Classe classe) {
        repository.deleteForClass(classe.getName());
        cache.invalidate(classe.getName());
    }

}
