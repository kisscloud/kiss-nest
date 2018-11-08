package com.kiss.kissnest.filter;

import entity.Guest;
import filter.GuestFilter;
import filter.InnerFilterChain;
import org.springframework.stereotype.Component;
import utils.GuestUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(filterName = "nestFilter", urlPatterns = "/*")
public class NestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        InnerFilterChain preFilterChain = new InnerFilterChain();

        if (!httpServletRequest.getRequestURI().contains("/code/login")) {
            GuestFilter userInfoFilter = new GuestFilter();
            preFilterChain.addFilter(userInfoFilter);
        }

        preFilterChain.doFilter(httpServletRequest, httpServletResponse, preFilterChain);

//        Guest operator = new Guest();
//        operator.setId(103);
//        operator.setName("qrl758");
//        GuestUtil.setGuest(operator);

        chain.doFilter(httpServletRequest,httpServletResponse);
    }

    @Override
    public void destroy() {

    }
}
