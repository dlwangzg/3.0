/*
 * Copyright 2013-2016 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.leadingsoft.bizfuse.cloud.saas.configuration.feign;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * @author Daniel Lavoie
 */
public class HystrixRequestContextEnablerFilter implements Filter {
    private static Logger LOGGER = LoggerFactory
            .getLogger(HystrixRequestContextEnablerFilter.class);

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
            final FilterChain chain) throws IOException, ServletException {
        if (HystrixRequestContextEnablerFilter.LOGGER.isTraceEnabled()) {
            HystrixRequestContextEnablerFilter.LOGGER.trace("Initializing Hystrix Request Context.");
        }

        final HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            chain.doFilter(request, response);
        } finally {
            context.shutdown();
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
