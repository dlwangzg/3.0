package com.leadingsoft.bizfuse.quartz.convertor;

import javax.annotation.Resource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.quartz.dto.TriggerDTO;

@Component
public class TriggerConvertor extends AbstractConvertor<Trigger, TriggerDTO> {

    @Resource
    private Scheduler scheduler;

    public TriggerDTO convertorToTriggerDTO(final Trigger trigger) throws SchedulerException {
        final TriggerDTO triggerDTO = new TriggerDTO();
        triggerDTO.setTriggerKey(trigger.getKey().toString());
        triggerDTO.setTriggerGroup(trigger.getKey().getGroup());
        triggerDTO.setTriggerName(trigger.getKey().getName());
        triggerDTO.setJobKey(trigger.getJobKey().toString());
        triggerDTO.setDescription(trigger.getDescription());
        triggerDTO.setPriority(trigger.getPriority());
        triggerDTO.setMayFireAgain(trigger.mayFireAgain());
        triggerDTO.setStartTime(trigger.getStartTime());
        triggerDTO.setEndTime(trigger.getEndTime());
        triggerDTO.setNextFireTime(trigger.getNextFireTime());
        triggerDTO.setPreviousFireTime(trigger.getPreviousFireTime());
        triggerDTO.setFinalFireTime(trigger.getFinalFireTime());
        triggerDTO.setTriggerState(this.scheduler.getTriggerState(trigger.getKey()).toString());
        return triggerDTO;
    }

    @Override
    public TriggerDTO toDTO(final Trigger trigger) {
        final TriggerDTO triggerDTO = new TriggerDTO();
        triggerDTO.setTriggerKey(trigger.getKey().toString());
        triggerDTO.setTriggerGroup(trigger.getKey().getGroup());
        triggerDTO.setTriggerName(trigger.getKey().getName());
        triggerDTO.setJobKey(trigger.getJobKey().toString());
        triggerDTO.setDescription(trigger.getDescription());
        triggerDTO.setPriority(trigger.getPriority());
        triggerDTO.setMayFireAgain(trigger.mayFireAgain());
        triggerDTO.setStartTime(trigger.getStartTime());
        triggerDTO.setEndTime(trigger.getEndTime());
        triggerDTO.setNextFireTime(trigger.getNextFireTime());
        triggerDTO.setPreviousFireTime(trigger.getPreviousFireTime());
        triggerDTO.setFinalFireTime(trigger.getFinalFireTime());
        try {
            triggerDTO.setTriggerState(this.scheduler.getTriggerState(trigger.getKey()).toString());
        } catch (final SchedulerException e) {
            e.printStackTrace();
        }
        if (trigger instanceof CronTriggerImpl) {
            triggerDTO.setTriggerType("CronTrigger");
            final CronTriggerImpl newCronTrigger = (CronTriggerImpl) trigger;
            triggerDTO.setCronExpression(newCronTrigger.getCronExpression());
        }
        if (trigger instanceof SimpleTriggerImpl) {
            triggerDTO.setTriggerType("SimpleTrigger");
            final SimpleTriggerImpl newCronTrigger = (SimpleTriggerImpl) trigger;
            triggerDTO.setRepeatInterval(newCronTrigger.getRepeatInterval());
        }
        return triggerDTO;
    }

    @Override
    public Trigger toModel(final TriggerDTO dto) {
        // TODO Auto-generated method stub
        return null;
    }
}
