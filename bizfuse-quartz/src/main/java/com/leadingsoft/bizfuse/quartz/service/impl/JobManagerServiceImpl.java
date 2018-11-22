package com.leadingsoft.bizfuse.quartz.service.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.quartz.CronExpression;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.utils.Key;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.leadingsoft.bizfuse.quartz.bean.TriggerBean;
import com.leadingsoft.bizfuse.quartz.bean.TriggerBean.TriggerType;
import com.leadingsoft.bizfuse.quartz.bean.TriggerSearchBean;
import com.leadingsoft.bizfuse.quartz.core.scheduler.CommonJobsExecutor;
import com.leadingsoft.bizfuse.quartz.service.JobManagerService;

/**
 * job任务管理
 *
 * @author wangwj
 * @version 1.0
 */
@Service
public class JobManagerServiceImpl implements JobManagerService {

    @Resource
    private Scheduler scheduler;

    @Resource
    private CommonJobsExecutor commonJobsExecutor;

    @Override
    @Transactional
    public Page<JobKey> getJobs(final String search, final Pageable pageable) {

        //查询所有的JobKey
        Set<JobKey> jobKeySet;
        try {
            jobKeySet = this.scheduler.getJobKeys(null);
        } catch (final SchedulerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        final List<JobKey> jobKeys = jobKeySet.stream().filter(jobKey -> {
            if (StringUtils.hasText(search)) {
                return this.matches(search, jobKey);
            } else {
                return true;
            }
        }).sorted((key1, key2) -> key1.compareTo(key2)).collect(Collectors.toList());

        // 分页
        final List<JobKey> pageContent = jobKeys.stream()
                .skip(pageable.getOffset()).limit(pageable.getPageSize()).collect(Collectors.toList());
        return new PageImpl<JobKey>(pageContent, pageable, jobKeys.size());
    }

    @Override
    public boolean pauseJob(final String jobName) {
        //构建JobKey
        final JobKey jobKey = JobKey.jobKey(jobName);
        //调用暂停方法
        try {
            this.scheduler.pauseJob(jobKey);
        } catch (final SchedulerException e) {
            throw new RuntimeException(String.format("暂停JOB失败，jobName[%s]", jobName), e);
        }
        return true;
    }

    @Override
    public boolean resumeJob(final String jobName) {
        //构建JobKey
        final JobKey jobKey = JobKey.jobKey(jobName);
        //调用恢复方法
        try {
            this.scheduler.resumeJob(jobKey);
        } catch (final SchedulerException e) {
            throw new RuntimeException(String.format("恢复JOB失败，jobName[%s]", jobName), e);
        }
        return true;
    }

    @Override
    public boolean deleteJob(final String jobName) {
        //构建JobKey
        final JobKey jobKey = JobKey.jobKey(jobName);
        //调用删除方法
        try {
            this.scheduler.deleteJob(jobKey);
        } catch (final SchedulerException e) {
            throw new RuntimeException(String.format("删除JOB失败，jobName[%s]", jobName), e);
        }
        return true;
    }

    @Override
    public Page<Trigger> getTriggers(final TriggerSearchBean search, final Pageable pageable) {
        final JobKey jobKey = new JobKey(search.getJobName());
        List<? extends Trigger> list;
        try {
            list = this.scheduler.getTriggersOfJob(jobKey);
        } catch (final SchedulerException e) {
            throw new RuntimeException(String.format("暂停JOB失败，jobName[%s]", search.getJobName()), e);
        }
        final List<Trigger> triggers =
                list.stream()
                        .filter(trigger -> {
                            if ((search.getTriggerName() != null)
                                    && !search.getTriggerName().equalsIgnoreCase(trigger.getKey().getName())) {
                                return false;
                            }
                            if (search.getTriggerState() == null) {
                                return true;
                            }
                            try {
                                final String state = this.scheduler.getTriggerState(trigger.getKey()).toString();
                                return state.equalsIgnoreCase(search.getTriggerState());
                            } catch (final Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }).sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey())).collect(Collectors.toList());
        final List<Trigger> pageContent = triggers.stream().skip(pageable.getOffset())
                .limit(pageable.getPageSize()).collect(Collectors.toList());
        return new PageImpl<Trigger>(pageContent, pageable, triggers.size());
    }

    @Override
    public boolean pauseTrigger(final String triggerName) {
        //构建TriggerKey
        final TriggerKey triggerKey = new TriggerKey(triggerName);
        //暂停trigger
        try {
            this.scheduler.pauseTrigger(triggerKey);
        } catch (final SchedulerException e) {
            throw new RuntimeException(String.format("暂停Trigger失败，triggerName[%s]", triggerName), e);
        }
        return true;
    }

    @Override
    public boolean resumeTrigger(final String triggerName) {
        //构建TriggerKey
        final TriggerKey triggerKey = new TriggerKey(triggerName);
        //恢复trigger
        try {
            this.scheduler.resumeTrigger(triggerKey);
        } catch (final SchedulerException e) {
            throw new RuntimeException(String.format("恢复Trigger失败，triggerName[%s]", triggerName), e);
        }
        return true;
    }

    @Override
    public boolean updateTrigger(final TriggerBean triggerBean) {
        //构建TriggerKey
        final TriggerKey triggerKey = new TriggerKey(triggerBean.getTriggerKey());
        try {
            //查询相关trigger信息进行修改
            final Trigger trigger = this.scheduler.getTrigger(triggerKey);
            if (trigger instanceof CronTriggerImpl) {
                final CronTriggerImpl newTrigger = (CronTriggerImpl) trigger;

                // 优先级
                newTrigger.setPriority(triggerBean.getPriority());

                // 时间设置
                newTrigger.setStartTime(triggerBean.getStartTime());
                newTrigger.setEndTime(triggerBean.getEndTime());

                if (!CronExpression.isValidExpression(triggerBean.getCronExpression())) {
                    throw new RuntimeException("表达式不正确！");
                }
                try {
                    newTrigger.setCronExpression(triggerBean.getCronExpression());
                    //重新注册trigger
                    this.scheduler.rescheduleJob(triggerKey, newTrigger);
                } catch (final ParseException e) {
                    throw new RuntimeException("表达式不正确！");
                }

            }
            if (trigger instanceof SimpleTriggerImpl) {
                final SimpleTriggerImpl newTrigger = (SimpleTriggerImpl) this.scheduler.getTrigger(triggerKey);
                if (triggerBean.getRepeatInterval() <= 0) {
                    throw new RuntimeException("时间间隔不正确！");
                }
                // 优先级
                newTrigger.setPriority(triggerBean.getPriority());

                // 时间设置
                newTrigger.setStartTime(triggerBean.getStartTime());
                newTrigger.setEndTime(triggerBean.getEndTime());
                newTrigger.setRepeatCount(triggerBean.getRepeatCount());
                newTrigger.setRepeatInterval(triggerBean.getRepeatInterval());
                //重新注册trigger
                this.scheduler.rescheduleJob(triggerKey, newTrigger);
            }
            return true;
        } catch (final Exception e) {
            throw new RuntimeException(String.format("更新Trigger失败，triggerName[%s]",
                    triggerBean.getTriggerKey()), e);
        }
    }

    @Override
    public boolean addTrigger(final TriggerBean triggerBean, final Object... params) {
        try {
            Trigger trigger = null;
            final Trigger existsTrigger = this.scheduler.getTrigger(new TriggerKey(triggerBean.getTriggerKey()));
            //判断是否已存在
            if (existsTrigger != null) {
                throw new RuntimeException("相同名字的触发器已存在！");
            }
            //构建trigger信息
            if (TriggerType.SimpleTrigger == triggerBean.getTriggerType()) {
                final SimpleTriggerImpl newTrigger = new SimpleTriggerImpl();
                // 基本信息设置
                newTrigger.setKey(new TriggerKey(triggerBean.getTriggerKey()));
                newTrigger.setDescription(triggerBean.getDescription());
                newTrigger.setGroup(Key.DEFAULT_GROUP);
                newTrigger.setJobKey(new JobKey(triggerBean.getJobKey()));

                // 优先级
                newTrigger.setPriority(triggerBean.getPriority());

                // 时间设置
                newTrigger.setStartTime(triggerBean.getStartTime());
                newTrigger.setEndTime(triggerBean.getEndTime());
                newTrigger.setRepeatCount(triggerBean.getRepeatCount());
                newTrigger.setRepeatInterval(triggerBean.getRepeatInterval());
                trigger = newTrigger;
            } else if (TriggerType.CronTrigger == triggerBean.getTriggerType()) {
                final CronTriggerImpl newTrigger = new CronTriggerImpl();
                // 基本信息设置
                newTrigger.setKey(new TriggerKey(triggerBean.getTriggerKey()));
                newTrigger.setDescription(triggerBean.getDescription());
                newTrigger.setGroup(Key.DEFAULT_GROUP);
                newTrigger.setJobKey(new JobKey(triggerBean.getJobKey()));

                // 优先级
                newTrigger.setPriority(triggerBean.getPriority());

                // 时间设置
                newTrigger.setStartTime(triggerBean.getStartTime());
                newTrigger.setEndTime(triggerBean.getEndTime());

                try { // 规则设置
                    final CronExpression cronExpression = new CronExpression(triggerBean.getCronExpression());
                    newTrigger.setCronExpression(cronExpression);
                } catch (final ParseException e) {
                    throw new RuntimeException("表达式格式不正确");
                }
                trigger = newTrigger;
            }
            //注册trigger
            this.commonJobsExecutor.scheduleJob(trigger, params);
            return true;
        } catch (final Exception e) {
            throw new RuntimeException(String.format("添加Trigger失败，triggerName[%s]",
                    triggerBean.getTriggerKey()), e);
        }
    }

    @Override
    public boolean deleteTrigger(final String triggerName) {
        //构建trigger
        final TriggerKey triggerKey = new TriggerKey(triggerName);
        //移除trigger
        try {
            this.commonJobsExecutor.removeJobTrigger(triggerKey);
            return true;
        } catch (final SchedulerException e) {
            throw new RuntimeException(String.format("删除Trigger失败，triggerName[%s]", triggerName), e);
        }
    }

    private boolean matches(final String search, final JobKey jobKey) {
        boolean matched = jobKey.getName().toLowerCase().contains(search.toLowerCase());
        if (!matched) {
            try {
                final String jobDescription = this.scheduler.getJobDetail(jobKey).getDescription();
                matched = ((jobDescription != null) && jobDescription.contains(search));
            } catch (final Exception e) {
            }
        }
        return matched;
    }
}
