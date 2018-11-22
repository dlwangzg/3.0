package com.leadingsoft.bizfuse.cloud.saas.client.async;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContext;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContextHolder;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Configuration
@ConditionalOnProperty(name = "bizfuse.saas.reactor.enabled", matchIfMissing = true, havingValue = "true")
@ConditionalOnClass(name = "reactor.core.Reactor")
public class ReactorAspect {

    private static Map<Class<?>, Field> dataFields = new HashMap<>();

    @Pointcut("execution(public * reactor.core.Observable.notify(..))")
    public void notifyMethod() {
    }

    @Around(value = "notifyMethod()")
    public void addTenantInArgs(final ProceedingJoinPoint pjp) throws Throwable {
        final Object[] args = pjp.getArgs();
        if (args.length == 2) {
            final Object event = args[1];
            args[1] = this.wrapperEventData(event);
        }
        pjp.proceed();
    }

    @Around(value = "@annotation(reactor.spring.context.annotation.Selector)")
    public void loadTenantOnCurrentThread(final ProceedingJoinPoint pjp) throws Throwable {
        final Object[] args = pjp.getArgs();
        final Object event = args[0];
        args[0] = this.unwrapperEventData(event);
        pjp.proceed();
    }

    private Object unwrapperEventData(final Object event) {
        TenantContextHolder.set(null);
        try {
            final Field dataField = this.getDataField(event);
            final Object data = dataField.get(event);
            if (data instanceof TenantWrapper) {
                final TenantWrapper wrapper = (TenantWrapper) data;
                TenantContextHolder.set(wrapper.getContext());
                SecurityContextHolder.getContext().setAuthentication(wrapper.getAuth());
                dataField.set(event, wrapper.getTarget());
            }
        } catch (final Exception e) {
            ReactorAspect.log.error("包装租户信息到参数失败", e);
        }
        return event;
    }

    private Object wrapperEventData(final Object event) {
        try {
            final Field dataField = this.getDataField(event);
            final TenantContext context = TenantContextHolder.getContext();
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            final Object data = dataField.get(event);
            final TenantWrapper wrapper = new TenantWrapper(auth, context, data);
            dataField.set(event, wrapper);
        } catch (final Exception e) {
            ReactorAspect.log.error("包装租户信息到参数失败", e);
        }
        return event;
    }

    private Field getDataField(final Object event) throws Exception {
        final Class<?> clazz = event.getClass();
        Field field = ReactorAspect.dataFields.get(clazz);
        if (field == null) {
            Class<?> eventClass = clazz;
            while (!eventClass.getName().equals("reactor.event.Event")) {
                eventClass = eventClass.getSuperclass();
            }
            field = eventClass.getDeclaredField("data");
            field.setAccessible(true);
            ReactorAspect.dataFields.put(clazz, field);
        }
        return field;
    }

    @Getter
    public static class TenantWrapper {
        public TenantWrapper(final Authentication auth, final TenantContext context, final Object data) {
            this.auth = auth;
            this.context = context;
            this.target = data;
        }

        private final Authentication auth;
        private final TenantContext context;
        private final Object target;
    }
}
