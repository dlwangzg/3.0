package com.leadingsoft.bizfuse.quartz.core.scheduler;

import java.lang.reflect.Field;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.leadingsoft.bizfuse.quartz.core.TaskWrapper;
import com.leadingsoft.bizfuse.quartz.core.annotition.JobMapping;

// @Component
// @Job
public class DefaultTimeLimitJobExecutor {
    /** logger */
    private static final Log LOGGER = LogFactory.getLog("JOBLog");
    /** 每次执行间隔5秒钟 */
    private static final int EXECUTE_INTERVAL = 5;
    @Resource
    private CommonJobsExecutor commonJobsExecutor;
    @Resource
    private ApplicationContext applicationContext;

    /**
     * 启动Job执行任务
     *
     * @param taskWrapper 任务包装对象
     * @param maxRepeatTimes 最大执行次数
     */
    public void execute(final TaskWrapper taskWrapper, final Integer maxRepeatTimes) {
        this.beforeJob(taskWrapper);
        try {
            this.commonJobsExecutor.scheduleJob("com.leadingsoft.bizfuse.quartz.DefaultTimeLimitJobExecutor.execute", 1,
                    taskWrapper, maxRepeatTimes);
        } catch (final SchedulerException e) {
            DefaultTimeLimitJobExecutor.LOGGER.error(e.getMessage(), e);
            // TODO：DB记录所有Job执行异常？
        }
    }

    /**
     * 执行任务
     *
     * @param taskWrapper 任务包装对象
     * @param limitTimes 限制最大执行次数
     */
    @JobMapping(id = "com.leadingsoft.bizfuse.quartz.DefaultTimeLimitJobExecutor.execute")
    public void innerExecute(final TaskWrapper taskWrapper, Integer limitTimes) {
        this.beforeExecute(taskWrapper);

        if (limitTimes > 0) {// 未完成
            try {
                limitTimes = limitTimes - 1;
                taskWrapper.execute();// 执行任务

                if (taskWrapper.isFinished()) {
                    taskWrapper.callback(true);// 任务完成，回调
                    return;
                }
            } catch (final Exception ex) {
                DefaultTimeLimitJobExecutor.LOGGER.error(ex.getMessage(), ex);
            }
        }
        if (limitTimes > 0) {// 起JOB重新执行
            this.beforeJob(taskWrapper);
            try {
                this.commonJobsExecutor.scheduleJob(
                        "com.leadingsoft.bizfuse.quartz.DefaultTimeLimitJobExecutor.execute",
                        DefaultTimeLimitJobExecutor.EXECUTE_INTERVAL, taskWrapper, limitTimes);
            } catch (final SchedulerException e) {
                DefaultTimeLimitJobExecutor.LOGGER.error(e.getMessage(), e);
            }
        } else {
            // 超出最大执行次数，返回处理失败
            try {
                taskWrapper.callback(false);
            } catch (final Exception ex) {
                DefaultTimeLimitJobExecutor.LOGGER.error(ex.getMessage(), ex);
                // 是否添加消息提醒 TODO
            }
        }
    }

    private void beforeExecute(final TaskWrapper bean) {

        try {
            final Class<?> beanClass = ClassUtils.getUserClass(bean);
            this.initBeanIOC(bean, beanClass);
        } catch (final Exception ex) {
            DefaultTimeLimitJobExecutor.LOGGER.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }

    }

    private void beforeJob(final TaskWrapper bean) {

        try {
            final Class<?> beanClass = ClassUtils.getUserClass(bean);
            this.cleanBeanIOC(bean, beanClass);
        } catch (final Exception ex) {
            DefaultTimeLimitJobExecutor.LOGGER.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    private void initBeanIOC(final Object bean, final Class<?> clazz) throws IllegalArgumentException,
            IllegalAccessException {
        if ((clazz == null) || (clazz == Object.class)) {
            return;
        }
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Resource.class)) {
                final Resource resource = field.getAnnotation(Resource.class);
                // 默认按名称注入
                String beanName = resource.name();
                if (!StringUtils.hasText(beanName)) {
                    beanName = field.getName();
                }
                if (this.applicationContext.containsBean(beanName)) {
                    final Object fieldInstance = this.applicationContext.getBean(beanName);
                    field.setAccessible(true);
                    field.set(bean, fieldInstance);
                } else { // 按名称找不到的，按类型注入
                    final Object fieldInstance = this.applicationContext.getBean(field.getType());
                    field.setAccessible(true);
                    field.set(bean, fieldInstance);
                }
            } else if (field.isAnnotationPresent(Autowired.class)) {
                final Object fieldInstance = this.applicationContext.getBean(field.getType());
                field.setAccessible(true);
                field.set(bean, fieldInstance);
            } else {
                // 不需要注入
            }
        }
        // 第归调用
        this.initBeanIOC(bean, clazz.getSuperclass());
    }

    private void cleanBeanIOC(final Object bean, final Class<?> clazz) throws IllegalArgumentException,
            IllegalAccessException {
        if ((clazz == null) || (clazz == Object.class)) {
            return;
        }
        for (final Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Resource.class)) {
                // 默认按名称清空
                field.setAccessible(true);
                field.set(bean, null);
            } else if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                field.set(bean, null);
            } else {
                // 不需要注入
            }
        }
        // 第归调用
        this.cleanBeanIOC(bean, clazz.getSuperclass());
    }
}
