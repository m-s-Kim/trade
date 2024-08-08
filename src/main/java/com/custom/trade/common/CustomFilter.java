package com.custom.trade.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.annotation.WebFilter;


import java.io.IOException;
@Slf4j
@WebFilter(urlPatterns = "/*")
public class CustomFilter implements jakarta.servlet.Filter {

    public FilterConfig filterConfig;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        log.info("CustomFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest
            , ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        CustomHttpRequestWrapper wrapper = new CustomHttpRequestWrapper((HttpServletRequest) servletRequest);
        log.info("Before processing request: {}", wrapper.getRequestURI());
        filterChain.doFilter( wrapper,  (HttpServletResponse) servletResponse);
        log.info("After processing request: {}", wrapper.getRequestURI());

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }




}
