package com.leadingsoft.bizfuse.common.web.config.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AdvancedTomcatMetrics implements PublicMetrics, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        if (this.applicationContext instanceof EmbeddedWebApplicationContext) {
            final EmbeddedServletContainer embeddedServletContainer =
                    ((EmbeddedWebApplicationContext) this.applicationContext)
                            .getEmbeddedServletContainer();
            if (embeddedServletContainer instanceof TomcatEmbeddedServletContainer) {
                final Connector connector =
                        ((TomcatEmbeddedServletContainer) embeddedServletContainer).getTomcat().getConnector();
                final ProtocolHandler handler = connector.getProtocolHandler();
                final ThreadPoolExecutor executor =
                        (ThreadPoolExecutor) handler.getExecutor();
                //register tomcat thread pool stat
                final List<Metric<?>> metrics = new ArrayList<Metric<?>>();
                metrics.add(new Metric<Integer>("tomcat.threads.active_count", executor.getActiveCount()));
                metrics.add(new Metric<Integer>("tomcat.threads.largest_pool_size", executor.getLargestPoolSize()));
                metrics.add(new Metric<Long>("tomcat.threads.task_count", executor.getTaskCount()));
                metrics.add(new Metric<Long>("tomcat.threads.completed_task_count", executor.getCompletedTaskCount()));
                metrics.add(new Metric<Integer>("tomcat.threads.submitted_count", executor.getSubmittedCount()));
                metrics.add(new Metric<Integer>("tomcat.threads.pool_size", executor.getPoolSize()));
                metrics.add(new Metric<Integer>("tomcat.threads.core_pool_size", executor.getCorePoolSize()));
                metrics.add(new Metric<Integer>("tomcat.threads.max_pool_size", executor.getMaximumPoolSize()));
                return metrics;
            }
        }
        return Collections.emptySet();
    }
}
