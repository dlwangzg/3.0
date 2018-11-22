package com.leadingsoft.bizfuse.cloud.saas.configuration.feign;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import com.netflix.hystrix.util.PlatformSpecific;

public class SecurityHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    @Override
    public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey,
            final HystrixProperty<Integer> corePoolSize, final HystrixProperty<Integer> maximumPoolSize,
            final HystrixProperty<Integer> keepAliveTime, final TimeUnit unit,
            final BlockingQueue<Runnable> workQueue) {
        ThreadFactory threadFactory = null;
        if (!PlatformSpecific.isAppEngine()) {
            threadFactory = new ThreadFactory() {
                protected final AtomicInteger threadNumber = new AtomicInteger(0);

                @Override
                public Thread newThread(final Runnable r) {
                    final Thread thread = new Thread(r,
                            "hystrix-" + threadPoolKey.name() + "-" + this.threadNumber.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }

            };
        } else {
            threadFactory = PlatformSpecific.getAppEngineThreadFactory();
        }

        return new SecurityThreadPoolExecutor(corePoolSize.get(), maximumPoolSize.get(), keepAliveTime.get(), unit,
                workQueue,
                threadFactory);
    }

    public static class SecurityThreadPoolExecutor extends ThreadPoolExecutor {

        public SecurityThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                final TimeUnit unit,
                final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        @Override
        public void execute(final Runnable task) {
            final DelegatingSecurityContextRunnable wrappedRunnable = new DelegatingSecurityContextRunnable(task);
            super.execute(wrappedRunnable);
        }
    }
}
