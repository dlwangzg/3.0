package com.leadingsoft.bizfuse.cloud.saas.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;

public class AntPathRequestMatcher {

    private static final String MATCH_ALL = "**";
    private static final String MATCH_WORD = "*";

    private final String[] patternFragments;
    private final String pattern;
    private final HttpMethod httpMethod;
    private final boolean caseSensitive;

    public AntPathRequestMatcher(final String pattern) {
        this(pattern, null);
    }

    public AntPathRequestMatcher(final String pattern, final HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        this.pattern = pattern;
        this.caseSensitive = pattern.indexOf("*") < 0;
        if (this.caseSensitive) {
            this.patternFragments = null;
        } else {
            this.patternFragments = pattern.split("/");
        }
    }

    public boolean matches(final String requestURI, final String method) {
        if ((this.httpMethod != null) && !this.httpMethod.matches(method)) {
            return false;
        }
        if (this.caseSensitive) {
            return this.pattern.equals(requestURI);
        }
        final String[] urlFragments = requestURI.split("/");
        if (urlFragments.length < this.patternFragments.length) {
            return false;
        }
        for (int i = 0; i < this.patternFragments.length; i++) {
            if (this.patternFragments[i].equals(AntPathRequestMatcher.MATCH_WORD)) {
                continue;
            }
            if (this.patternFragments[i].equals(AntPathRequestMatcher.MATCH_ALL)) {
                return true;
            }
            if (!this.patternFragments[i].equals(urlFragments[i])) {
                return false;
            }
        }
        return urlFragments.length == this.patternFragments.length;
    }

    public boolean matches(final HttpServletRequest request) {
        return this.matches(request.getRequestURI(), request.getMethod());
    }

    //    public static void main(final String[] args) {
    //        final AntPathRequestMatcher m1 = new AntPathRequestMatcher("/w/hello/world");
    //        Assert.isTrue(m1.matches("/w/hello/world", "GET"));
    //        Assert.isTrue(!m1.matches("/w/hello/world/123", "GET"));
    //
    //        final AntPathRequestMatcher m2 = new AntPathRequestMatcher("/w/*/world");
    //        Assert.isTrue(m2.matches("/w/hello/world", "GET"));
    //        Assert.isTrue(!m2.matches("/w/hello/world/123", "GET"));
    //
    //        final AntPathRequestMatcher m3 = new AntPathRequestMatcher("/w/hello/**");
    //        Assert.isTrue(m3.matches("/w/hello/world", "GET"));
    //        Assert.isTrue(m3.matches("/w/hello/world/123", "GET"));
    //    }

}
