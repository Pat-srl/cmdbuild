/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.calendar.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import org.cmdbuild.calendar.CalendarTriggersService;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.calendar.data.CalendarTriggerRepository;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class CalendarTriggersServiceImpl implements CalendarTriggersService {

    private final DaoService dao;
    private final CalendarTriggerRepository triggerRepository;

    public CalendarTriggersServiceImpl(DaoService dao, CalendarTriggerRepository triggerRepository) {
        this.dao = checkNotNull(dao);
        this.triggerRepository = checkNotNull(triggerRepository);
    }

    @Override
    public List<CalendarTrigger> getAllTriggers() {
        return triggerRepository.getAllTriggers();
    }

    @Override
    public List<CalendarTrigger> getTriggersByOwnerClassIncludeInherited(String ownerClass) {
        Classe classe = dao.getClasse(ownerClass);
        return classe.getAncestorsAndSelf().stream().flatMap(c -> getTriggersByOwnerClass(c).stream().filter(t -> classe.hasAttributeActive(t.getOwnerAttr()))).collect(toImmutableList());
    }

    @Override
    public List<CalendarTrigger> getTriggersByOwnerClassOwnerAttrIncludeInherited(String ownerClass, String ownerAttr) {
        return dao.getClasse(ownerClass).getAncestorsAndSelf().stream().flatMap(c -> getTriggersByOwnerClassOwnerAttr(c, ownerAttr).stream()).collect(toImmutableList());
    }

    @Override
    public List<CalendarTrigger> getTriggersByOwnerClass(String ownerClass) {
        return triggerRepository.getTriggersByOwnerClass(ownerClass);
    }

    @Override
    public List<CalendarTrigger> getTriggersByOwnerClassOwnerAttr(String ownerClass, String ownerAttr) {
        return triggerRepository.getTriggersByOwnerClassOwnerAttr(ownerClass, ownerAttr);
    }

    @Override
    public CalendarTrigger createTrigger(CalendarTrigger trigger) {
        return triggerRepository.createTrigger(trigger);
    }

    @Override
    public CalendarTrigger updateTrigger(CalendarTrigger trigger) {
        return triggerRepository.updateTrigger(trigger);
    }

    @Override
    public void deleteTrigger(long id) {
        triggerRepository.deleteTrigger(id);
    }

    @Override
    public CalendarTrigger getTriggerById(long id) {
        return triggerRepository.getTriggerById(id);
    }

    @Override
    public CalendarTrigger getTriggerByCode(String triggerCode) {
        return triggerRepository.getTriggerByCode(triggerCode);
    }

}
